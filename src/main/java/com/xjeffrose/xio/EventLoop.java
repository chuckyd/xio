package com.xjeffrose.xio;

import java.io.*;
import java.net.*;
import java.nio.channels.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.logging.*;
import java.util.stream.*;
import java.util.*;

import com.xjeffrose.log.*;

class EventLoop implements Runnable, Closeable {
  private static final Logger log = Log.getLogger(EventLoop.class.getName());

  private final AtomicBoolean isRunning = new AtomicBoolean();
  //private final ExecutorService exs;
  private final ThreadPoolExecutor spawn;
  private final Selector selector;
  private final Gatekeeper g = new Gatekeeper();
  private final Set acceptedKeys = new HashSet();
  private final AtomicInteger loopsAreCool = new AtomicInteger(0);
  private ServerSocketChannel serverChannel;
  private ServerSocket serverSocket;

  EventLoop() throws IOException {
    isRunning.set(true);
    selector = Selector.open();
// http://hg.openjdk.java.net/jdk8/jdk8/jdk/file/687fd7c7986d/src/share/classes/java/util/concurrent/Executors.java
    int nThreads = 48;
    spawn = new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
  }

  void register(ServerSocketChannel channel) throws ClosedChannelException {
    channel.register(selector, SelectionKey.OP_ACCEPT);
    serverChannel = channel;
  }

  void register(ServerSocket socket) {
    serverSocket = socket;
  }

  private void doAccept(SelectionKey key) {
    //TODO: Clean up this Logic
    SocketChannel channel = g.acceptor(key);
    g.ipFilter();
    g.rateLimit();
    //spawn.submit(g.createSession(channel));
    acceptedKeys.add(key);
  }

  @Override
  public void close() {
    isRunning.set(false);
  }

  private void keyStream() {
    try {
      while (isRunning.get()) {
        selector.select();
        selector.selectedKeys()
            .stream()
            .distinct()
            .filter(SelectionKey::isAcceptable)
            .forEach(this::doAccept);
        selector.selectedKeys().removeAll(acceptedKeys);
        acceptedKeys.clear();
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void run() {
    keyStream();
/*
    try {
      while (true) {
        SocketChannel clientChannel = serverChannel.accept();
        loopsAreCool.incrementAndGet();
        g.acceptor();
        //spawn.submit(g.createSession(clientChannel));
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
*/
  }

  public void stats() {
    log.info(
      "\nActive Threads: " + spawn.getActiveCount() +
      "\nPool Size: " + spawn.getPoolSize() +
      "\nCompleted Tasks: " + spawn.getCompletedTaskCount() +
      "\nScheduled Tasks: " + spawn.getTaskCount() +
      "\nIn Flight Tasks: " + (spawn.getTaskCount() - spawn.getCompletedTaskCount()) +
      "\nAccepted Connections: " + g.acceptedConnections() +
      "\nServiced Connections: " + g.servicedConnections() +
      "\nLoop Iterations: " + loopsAreCool.get()
    );
  }

}

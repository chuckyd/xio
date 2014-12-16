package com.xjeffrose.xio;

import java.io.*;
import java.nio.channels.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.logging.*;
import java.util.stream.*;

import com.xjeffrose.log.*;

class EventLoop implements Closeable {
  private static final Logger log = LogUtil.config(EventLoop.class.getName());

  private final AtomicBoolean isRunning = new AtomicBoolean();
  private final ExecutorService exs;
  private final Selector selector;

  EventLoop() throws IOException {
    isRunning.set(true);
    selector = Selector.open();
    exs = Executors.newFixedThreadPool(24);
  }

  void register(ServerSocketChannel channel) throws ClosedChannelException {
    channel.register(selector, SelectionKey.OP_ACCEPT);
  }

  private SelectionKey doAccept(SelectionKey key) {
    exs.submit(new Gatekeeper(key));
    return key;
  }

  private void doDone(SelectionKey key) {
    selector.selectedKeys().remove(key);
  }

  @Override public void close() {
    isRunning.set(false);
  }

  void run() throws IOException {
    while (isRunning.get()) {
      selector.select();
      selector.selectedKeys()
          .stream()
          .filter(SelectionKey::isAcceptable)
          .map(this::doAccept)
          .forEach(this::doDone);
    }
  }
}

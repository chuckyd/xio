package com.xjeffrose.xio;

import java.io.*;
import java.nio.channels.*;
/* import java.util.concurrent.*; */
import java.util.concurrent.atomic.*;
import java.util.logging.*;
import java.util.stream.*;

import com.xjeffrose.log.*;

class EventLoop extends Thread {
  private static final Logger log = Log.getLogger(EventLoop.class.getName());

  private final AtomicBoolean isRunning = new AtomicBoolean(true);
  private final AtomicBoolean isReady = new AtomicBoolean(true);
  private final Gatekeeper g = new Gatekeeper();

  private Selector selector;

  EventLoop() {
    try {
      selector = Selector.open();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public boolean Ready() {
    return isReady.get();
  }

  public boolean Running() {
    return isRunning.get();
  }

  public void register(ServerSocketChannel channel) {
    try {
      channel.register(selector, SelectionKey.OP_ACCEPT);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private void doAccept(SelectionKey key) {
    //TODO: Clean up this Logic
    g.accept(key);
    g.ipFilter();
    g.rateLimit();
  }

  private void process() {
    log.info("processing");
    try{
      while (Ready()) {
        selector.select();
        selector.selectedKeys()
            .stream()
            .distinct()
            .filter(SelectionKey::isAcceptable)
            .forEach(this::doAccept);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }


  public void close() {
    isRunning.set(false);
  }

  public void run() {
    while (Running()) {
      process();
    }
  }

}
package com.xjeffrose.xio;

import java.io.*;
import java.nio.channels.*;
import java.util.concurrent.*;
import java.util.logging.*;

import com.xjeffrose.log.*;

class Gatekeeper implements Runnable {
  private static final Logger log = LogUtil.config(Gatekeeper.class.getName());

  private final SelectionKey key;

  Gatekeeper(SelectionKey key) {
    this.key = key;
  }

  private void accept(SelectionKey key) {
    try {

      ServerSocketChannel server = (ServerSocketChannel) key.channel();
      SocketChannel client = server.accept();
      log.info("Accepted Connection from " + client);

    } catch (IOException e) {
      log.info("There was an error here with ");
      key.cancel();
    }

  }

  @Override public void run() {
    accept(key);
  }
}

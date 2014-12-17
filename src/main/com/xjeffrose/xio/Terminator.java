package com.xjeffrose.xio;

import java.nio.channels.*;
import java.util.logging.*;

import com.xjeffrose.log.*;

class Terminator {
  private static final Logger log = Log.getLogger(EventLoop.class.getName());
  private final SocketChannel channel;

  Terminator(SocketChannel channel) {
    this.channel = channel;
  }

}

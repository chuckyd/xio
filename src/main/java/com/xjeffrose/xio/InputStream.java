package com.xjeffrose.xio;

import java.io.*;
import java.nio.channels.*;
import java.nio.charset.*;
import java.util.logging.*;

import com.xjeffrose.log.*;

//TODO: Implement Closable and isRunning while loop to prevent runaway stram
class InputStream implements Runnable {
  private static final Logger log = Log.getLogger(InputStream.class.getName());
  private ChannelContext ctx;
  private int nread;

  InputStream(ChannelContext ctx) {
    this.ctx = ctx;
  }

  private void stream() {
    nread = 1;
    boolean delimiter_seen = false;
    while (nread > 0 && !delimiter_seen) {
      try{
        nread = ctx.channel.read(ctx.inBuf);
        String raw = new String(ctx.inBuf.array(), Charset.forName("UTF-8"));
        if (raw.contains("\r\n\r\n")) {
          delimiter_seen = true;
        }
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    if (nread == -1) {
      try {
        //log.info("Closing Channel " + ctx.channel);
        ctx.channel.close();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  @Override public void run() {
    //log.info("Stream Started");
    stream();
  }
}

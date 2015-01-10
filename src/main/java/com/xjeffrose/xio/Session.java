package com.xjeffrose.xio;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;
import java.util.logging.*;
import java.util.stream.*;

import com.xjeffrose.log.*;
import com.xjeffrose.util.*;

class Session implements Runnable {
  private static final Logger log = Log.getLogger(Session.class.getName());
  private final ChannelContext ctx;
  private final SocketChannel channel;
  private int nread;

  Session (SocketChannel channel) {
    this.channel = channel;
    this.ctx = new ChannelContext(channel);
  }

  @Override
  public void run() {
    /* read(); */
    InputStream stream = new InputStream(ctx);
    //Thread inStream = new Thread(stream); // Should be a Future
    //inStream.start();
    stream.run();
    /* xioFuture f = new xioFuture(new InputStream(ctx)); */
    /* Await.ready(f); */
    /*
    try {
      Thread.sleep(200);
    } catch (Exception e) {}
    */
    process();
  }

/*
  private void read() {
    try {
      nread = ctx.channel.read(ctx.inBuf);
      if (nread == -1) {
        ctx.channel.close();
      }
    } catch (IOException e) {}
  }
*/

  private String[] i_hope_you_are_happy_jeff(String raw) {
    String[] parts = raw.split("\r\n\r\n");
    return parts;
  }
  private void process() {
    String raw = new String(ctx.inBuf.array(), Charset.forName("UTF-8"));
    String[] parts = raw.split("\r\n\r\n");
    String[] headers = parts[0].split("\r\n");
    //log.info(headers[0]);

    super_naive_proxy(raw);
    //log.info("Done proxying");
  }

  private void super_naive_proxy(String payload) {
    StringBuffer are_you_fucking_serious = new StringBuffer();
    byte[] pbites = new byte[1024];// = 1024;
    try{
      InetAddress address = InetAddress.getByName("127.0.0.1");
      Socket proxy = new Socket(address, 8000);
      proxy.getOutputStream().write(payload.getBytes("UTF-8"));
      boolean all_done = false;
      while (!all_done) {
        int bytes_read = proxy.getInputStream().read(pbites);
        are_you_fucking_serious.append((new String(pbites, Charset.forName("UTF-8"))).substring(0,bytes_read));
        channel.write(ByteBuffer.wrap(pbites, 0, bytes_read));
        //log.info("bytes_read: " + bytes_read);
        //log.info("payload length: " + are_you_fucking_serious.toString().length());
        if (are_you_fucking_serious.indexOf("\r\n\r\n") != -1) {
          //log.info("separator at: " + are_you_fucking_serious.indexOf("\r\n\r\n"));
          String[] parts = i_hope_you_are_happy_jeff(are_you_fucking_serious.toString());
          String[] headers = parts[0].split("\r\n");
          for (String header : headers) {
            //log.info("HEADER " + header);
            if (header.contains("Content-Length")) {
              int length = Integer.parseInt(header.split(": ")[1]);
              //log.info("length " + length + " parts " + parts[1].length());
              if (parts[1].length() >= length) {
                all_done = true;
                break;
              }
            }
          }
        }
      }
      proxy.close();
      channel.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}

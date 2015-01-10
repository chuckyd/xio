package com.xjeffrose.xio;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.logging.*;

import com.xjeffrose.log.*;

class Gatekeeper implements Runnable {
  private static final Logger log = Log.getLogger(Gatekeeper.class.getName());

  private SelectionKey key;
  //private SocketChannel channel;
  private SocketAddress remoteAddress;

  private final boolean ssl = false; //for debug will remove

  private final AtomicInteger connections_accepted = new AtomicInteger(0);
  private final AtomicInteger connections_serviced = new AtomicInteger(0);

  Gatekeeper() {}

  /* public void accept(SelectionKey key) { */
  /*   this.key = key; */
  /*  */
  /*   try { */
  /*     ServerSocketChannel ssc = (ServerSocketChannel) key.channel(); */
  /*     channel = ssc.accept(); */
  /*     SocketAddress remoteAddress = channel.getRemoteAddress(); */
  /*     log.info("remote address: " + remoteAddress.toString()); */
  /*  */
  /*     if (channel == null) { */
  /*       log.info("Dropping null channel " + channel + " " + key.isAcceptable()); */
  /*       killClient(); */
  /*     } */
  /*  */
  /*     else if (IpFilter.filter(remoteAddress)) { */
  /*       log.info("Dropping connection" + channel + " Based on IpFilter rule "); */
  /*       killClient(); */
  /*     } */
  /*  */
  /*     else if (RateLimit.limit()) { */
  /*       log.info("Dropping " + channel + " Due to RateLiming"); */
  /*       killClient(); */
  /*     } */
  /*  */
  /*     else log.info("Accepted Connection from " + channel); */
  /*  */
  /*  */
  /*   } catch (IOException e) { */
  /*     log.info("There was an error here with " + key.channel()); */
  /*     key.cancel(); */
  /*   } */
  /*  */
  /* } */

  public SocketChannel acceptor(SelectionKey key) {
    try {
      ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
      SocketChannel channel = ssc.accept();
      //remoteAddress = channel.getRemoteAddress();
      if(channel == null) {
        log.info("Dropping null channel " + channel + " " + key.isAcceptable());
        killClient();
      }
      //log.info("Accepted Connection from " + channel);
      int accepted = connections_accepted.incrementAndGet();
      /*
      if (accepted % 1000 == 0) {
        log.info("Connections accepted: " + accepted);
      }
      */
      return channel;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void acceptor() {
    int accepted = connections_accepted.incrementAndGet();
  }

  public void ipFilter() {
    IpFilter ipf = new IpFilter();
    if(ipf.filter(remoteAddress)) {
      killClient();
    }
  }

  public void rateLimit() {
    RateLimit rl = new RateLimit();
    if(rl.limit()) {
      killClient();
    }
  }

 private void killClient() {
      key.cancel();
      /*
    try {
      //channel.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    */
  }

  @Override public void run() {
/*
    if (ssl) {
      Terminator terminator = new Terminator(channel);
    }
    Session session = new Session(channel);
*/
  }

  public int acceptedConnections() {
    return connections_accepted.get();
  }

  public int servicedConnections() {
    return connections_serviced.get();
  }

  public Runnable createSession(SocketChannel channel) {
    final Session s = new Session(channel);
    return new Runnable() {
      public void run() {
        s.run();
        int serviced = connections_serviced.incrementAndGet();
      }
    };
  }

}

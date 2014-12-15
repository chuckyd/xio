package com.xjeffrose.xio;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.*;

import com.xjeffrose.log.*;

class Main {
  private static final Logger log = Log.getLogger();

  public static void main(String[] args) {
    log.info("Starting xio: Be well John Spartan");

    try {
      Server s = new Server(8080);

      s.serve();

    } catch (IOException e) {
      e.printStackTrace();
      System.exit(-1);
    }

  }
}

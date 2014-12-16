package com.xjeffrose.log;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.Level;

public class LogUtil {

  public static Logger config(String className) {
    Logger logger = Logger.getLogger(className);
    addHandler(logger);
    logger.setUseParentHandlers(false);
    return logger;
  }

  private static void addHandler(Logger logger) {
    Handler consoleHandler = new ConsoleHandler();
    consoleHandler.setLevel(Level.ALL);
    consoleHandler.setFilter(null);
    consoleHandler.setFormatter(new LogFormatter());
    logger.addHandler(consoleHandler);
  }

}

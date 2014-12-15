package com.xjeffrose.jrmalloc;

import sun.misc.Unsafe;
import java.lang.reflect.*;

class JRMalloc {


  JRMalloc() {
  }

  public long malloc() {
    long address = unsafe.allocateMemory(1024);
    return address;
  }

  public void free(long address) {
    unsafe.freeMemory(address);
  }


}

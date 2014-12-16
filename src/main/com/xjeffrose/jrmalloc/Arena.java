package com.xjeffrose.jrmalloc;

import java.lang.reflect.*;
import java.util.*;
import sun.misc.Unsafe;

class Arena {
  private final int bufferSize = 1024;
  private Field f;
  private Unsafe unsafe;
  private final Map<String,Long> arena = new HashMap<String,Long>();

  Arena() {
    getUnsafe();
  }


  private long stripe() {
    long address = unsafe.allocateMemory(bufferSize);
    return address;
  }

  private final void createPage(Map<String,Long> arena) {
    arena.put("one", stripe());
    arena.put("two", stripe());
    arena.put("three", stripe());
    arena.put("four", stripe());
  }

  private final void destroyPage(Map<String,Long> arena) {
    unsafe.freeMemory(arena.get("one"));
    unsafe.freeMemory(arena.get("two"));
    unsafe.freeMemory(arena.get("three"));
    unsafe.freeMemory(arena.get("four"));
  }

  private void getUnsafe()  {
    try {
      f = Unsafe.class.getDeclaredField("theUnsafe");
      f.setAccessible(true);
      unsafe = (Unsafe) f.get(null);
    } catch (NoSuchFieldException e){
    } catch (IllegalAccessException e) {
    }
  }
}

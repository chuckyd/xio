package com.xjeffrose.jrmalloc;

import sun.misc.Unsafe;
import java.lang.reflect.*;

class TheUnsafe {

  private Field f;
  private Unsafe unsafe;

  TheUnsafe() {
  }

  public Unsafe getUnsafe()  {
    try {
      f = Unsafe.class.getDeclaredField("theUnsafe");
      f.setAccessible(true);
      unsafe = (Unsafe) f.get(null);
    } catch (NoSuchFieldException e){
    } catch (IllegalAccessException e) {
    }
    return unsafe;
  }

}

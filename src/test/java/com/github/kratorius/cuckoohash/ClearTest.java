package com.github.kratorius.cuckoohash;

import junit.framework.TestCase;

public class ClearTest extends TestCase {
  public void testClear() {
    CuckooHashMap<String, String> m = new CuckooHashMap<>();
    m.put("hello1", "world1");
    m.put("hello2", "world2");
    m.put("hello3", "world3");
    m.put("hello4", "world4");
    m.put("hello5", "world5");
    assertEquals(5, m.size());

    m.clear();
    assertEquals(0, m.size());
  }

  public void testClear_alreadyEmpty() {
    CuckooHashMap<String, String> m = new CuckooHashMap<>();
    assertEquals(0, m.size());
    m.clear();
    assertEquals(0, m.size());
  }

  public void testReuseAfterClear() {
    CuckooHashMap<Integer, String> m = new CuckooHashMap<>();
    for (int i = 0; i < 10000; i++) {
      m.put(i, "i-" + i);
    }
    assertEquals(10000, m.size());

    m.clear();
    assertEquals(0, m.size());

    for (int i = 0; i < 10000; i++) {
      m.put(i, "i-" + i);
    }
    assertEquals(10000, m.size());
  }
}

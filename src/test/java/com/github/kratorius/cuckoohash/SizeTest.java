package com.github.kratorius.cuckoohash;

import junit.framework.TestCase;

public class SizeTest extends TestCase {
  @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
  public void testEmptySize() {
    CuckooHashMap<String, String> m = new CuckooHashMap<>();
    assertEquals(0, m.size());
  }

  public void testSizeAfterAllUniquePut() {
    final int size = 1024;
    CuckooHashMap<Integer, Integer> m = new CuckooHashMap<>();

    assertEquals(0, m.size());

    for (int i = 0; i < size; i++) {
      assertEquals(i, m.size());
      m.put(i, i);
    }

    assertEquals(size, m.size());
  }

  public void testSizeAfterReplacePut() {
    CuckooHashMap<Integer, Integer> m = new CuckooHashMap<>();

    assertEquals(0, m.size());

    m.put(1, 1);
    m.put(2, 2);
    m.put(3, 3);
    m.put(4, 4);
    m.put(5, 5);

    for (int i = 0; i < 100; i++) {
      m.put(1, 1);
      m.put(2, 2);
      m.put(3, 3);
      m.put(4, 4);
      m.put(5, 5);
    }

    assertEquals(5, m.size());
  }
}

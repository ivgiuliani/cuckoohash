package com.github.kratorius.cuckoohash;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Random;

/**
 * Tests for the put() method of the Cuckoo hash map.
 */
public class PutTest extends TestCase {
  @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
  public void testNullKey() {
    CuckooHashMap<Character, Boolean> m = new CuckooHashMap<>();

    try {
      m.put(null, true);
      fail("Accepted null key for put()");
    } catch (NullPointerException e) {
      // Expected.
    }
  }

  public void testUpdate() {
    CuckooHashMap<Character, Integer> m = new CuckooHashMap<>();

    m.put('A', 3);
    assertEquals(3, (int) m.get('A'));

    m.put('A', 42);
    assertEquals(42, (int) m.get('A'));
  }

  public void testManyKeys() {
    // Enough to generate a fair amount of rehashes but not too many so that it fits in memory
    // in the default heap size, and also so that it doesn't take too long to execute.
    final int size = 1024 * 128;

    ArrayList<String> keys = new ArrayList<>(size);
    for (int i = 0; i < size; i++) {
      keys.add(String.valueOf("key-" + i));
    }

    CuckooHashMap<String, Integer> m = new CuckooHashMap<>();
    for (int i = 0; i < size; i++) {
      m.put(keys.get(i), i);
      assertEquals(i, (int) m.get(keys.get(i)));
    }

    for (int i = 0; i < size; i++) {
      assertEquals(i, (int) m.get(keys.get(i)));
    }
  }
}

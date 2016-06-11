package com.github.kratorius.cuckoohash;

import junit.framework.TestCase;

import java.util.Map;

public class CuckooHashMapTest extends TestCase {
  @SuppressWarnings("UnnecessaryBoxing")
  public void testMap() {
    Map<Character,Boolean> m = new CuckooHashMap<Character, Boolean>();
    put(m, 'A', true,  null);
    put(m, 'A', false, true); // Guaranteed identical by JLS
    put(m, 'B', true,  null);
    put(m, new Character('A'), false, false);

    try {
      m.get(null);
      fail("map did not reject null key");
    } catch (NullPointerException e) {
      // expected.
    }

    try {
      m.put(null, true);
      fail("map did not reject null key");
    } catch (NullPointerException e) {
      // expected.
    }

    try {
      m.put('C', null);
    } catch (NullPointerException e) {
      fail("Rejected null value");
    }
  }

  private static void put(Map<Character, Boolean> m,
                          Character key,
                          Boolean value,
                          Boolean oldValue) {
    if (oldValue != null) {
      assertTrue(m.containsValue(oldValue));
      assertTrue(m.values().contains(oldValue));
    }

    assertEquals(m.put(key, value), oldValue);
    assertEquals(m.get(key), value);

    assertTrue(m.containsKey(key));
    assertTrue(m.containsValue(value));

    assertTrue(m.values().contains(value));

    assertFalse(m.isEmpty());
  }
}

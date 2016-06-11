package com.github.kratorius.cuckoohash;

import junit.framework.TestCase;
import org.junit.Before;

import java.util.Map;

public class CuckooHashMapTest extends TestCase {
  private CuckooHashMap<Character, Boolean> mCB;

  @Before
  public void setUp() {
    mCB = new CuckooHashMap<>();
  }

  @SuppressWarnings("UnnecessaryBoxing")
  public void testMap() {
    put(mCB, 'A', true,  null);
    put(mCB, 'A', false, true); // Guaranteed identical by JLS
    put(mCB, 'B', true,  null);
    put(mCB, new Character('A'), false, false);

    try {
      mCB.get(null);
      fail("map did not reject null key");
    } catch (NullPointerException e) {
      // expected.
    }

    try {
      mCB.put(null, true);
      fail("map did not reject null key");
    } catch (NullPointerException e) {
      // expected.
    }

    try {
      mCB.put('C', null);
    } catch (NullPointerException e) {
      fail("Rejected null value");
    }
  }

  public void testContainsKey() {
    try {
      mCB.containsKey(null);
      fail("accepted null key");
    } catch (NullPointerException e) {
      // Expected.
    }

    mCB.put('A', true);
    mCB.put('B', true);
    mCB.put('C', true);

    assertTrue(mCB.containsKey('A'));
    assertTrue(mCB.containsKey('B'));
    assertTrue(mCB.containsKey('C'));
    assertFalse(mCB.containsKey('D'));
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

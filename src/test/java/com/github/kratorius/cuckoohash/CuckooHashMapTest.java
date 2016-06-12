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
}

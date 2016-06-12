package com.github.kratorius.cuckoohash;

import junit.framework.TestCase;
import org.junit.Before;

public class CuckooHashMapTest extends TestCase {
  private CuckooHashMap<Character, Boolean> mCB;

  @Before
  public void setUp() {
    mCB = new CuckooHashMap<>();
  }

  public void testPut() {
    assertTrue(mCB.isEmpty());
    mCB.put('A', true);
    mCB.put('B', false);
    assertFalse(mCB.isEmpty());

    assertEquals(true, (boolean) mCB.get('A'));
    assertEquals(false, (boolean) mCB.get('B'));
    assertNull(mCB.get('C'));
  }

  public void testGet() {
    assertTrue(mCB.isEmpty());
    assertNull(mCB.get('A'));

    mCB.put('A', true);
    assertEquals(true, (boolean) mCB.get('A'));
    assertFalse(mCB.isEmpty());
  }

  public void testRemove() {
    assertTrue(mCB.isEmpty());

    // Should never fail if the key didn't exist.
    assertNull(mCB.remove('A'));
    assertNull(mCB.remove('B'));
    assertNull(mCB.remove('C'));

    mCB.put('A', true);
    mCB.put('B', false);
    assertEquals(2, mCB.size());

    assertEquals(false, (boolean) mCB.remove('B'));
    assertEquals(1, mCB.size());
    assertNull(mCB.get('B'));

    assertEquals(true, (boolean) mCB.remove('A'));
    assertEquals(0, mCB.size());
    assertNull(mCB.get('A'));
  }

  public void testClear() {
    assertTrue(mCB.isEmpty());

    mCB.clear();
    assertTrue(mCB.isEmpty());

    mCB.put('A', true);
    mCB.put('B', false);
    assertEquals(2, mCB.size());

    mCB.clear();
    assertTrue(mCB.isEmpty());
    assertNull(mCB.get('A'));
    assertNull(mCB.get('B'));
  }

  public void testSize() {
    assertEquals(0, mCB.size());

    mCB.put('A', true);
    assertEquals(1, mCB.size());

    mCB.put('B', true);
    assertEquals(2, mCB.size());

    mCB.remove('C');
    assertEquals(2, mCB.size());

    mCB.remove('A');
    assertEquals(1, mCB.size());

    mCB.remove('B');
    assertEquals(0, mCB.size());
  }

  public void testIsEmpty() {
    assertTrue(mCB.isEmpty());

    mCB.put('A', true);
    assertFalse(mCB.isEmpty());

    mCB.put('B', true);
    assertFalse(mCB.isEmpty());

    mCB.remove('C');
    assertFalse(mCB.isEmpty());

    mCB.remove('A');
    assertFalse(mCB.isEmpty());

    mCB.remove('B');
    assertTrue(mCB.isEmpty());
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

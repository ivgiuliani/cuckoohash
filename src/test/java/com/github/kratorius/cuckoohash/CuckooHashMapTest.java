package com.github.kratorius.cuckoohash;

import junit.framework.TestCase;
import org.junit.Before;

import java.util.*;

/**
 * Tests for basic behaviour of all the cuckoo hash map's methods.
 */
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

  public void testPutAll() {
    HashMap<Character, Boolean> copy = new HashMap<>();
    copy.put('A', true);
    copy.put('B', false);
    copy.put('C', true);
    copy.put('D', false);

    assertTrue(mCB.isEmpty());
    mCB.putAll(copy);
    assertFalse(mCB.isEmpty());

    assertEquals(true, (boolean) mCB.get('A'));
    assertEquals(false, (boolean) mCB.get('B'));
    assertEquals(true, (boolean) mCB.get('C'));
    assertEquals(false, (boolean) mCB.get('D'));
  }

  public void testKeySet() {
    mCB.put('A', true);
    mCB.put('B', true);
    mCB.put('C', true);

    Set<Character> keySet = mCB.keySet();
    assertTrue(keySet.contains('A'));
    assertTrue(keySet.contains('B'));
    assertTrue(keySet.contains('C'));
    assertFalse(keySet.contains('D'));
  }

  public void testValues() {
    mCB.put('A', true);
    mCB.put('B', true);
    mCB.put('C', true);

    Collection<Boolean> valueSet = mCB.values();
    assertTrue(valueSet.contains(true));
    assertFalse(valueSet.contains(false));
  }

  public void testEntrySet() {
    mCB.put('A', true);
    mCB.put('B', false);
    mCB.put('C', true);

    Set<Map.Entry<Character, Boolean>> entrySet = mCB.entrySet();
    assertEquals(3, entrySet.size());

    assertTrue(entrySet.contains(new AbstractMap.SimpleEntry<>('A', true)));
    assertTrue(entrySet.contains(new AbstractMap.SimpleEntry<>('B', false)));
    assertTrue(entrySet.contains(new AbstractMap.SimpleEntry<>('C', true)));
    assertFalse(entrySet.contains(new AbstractMap.SimpleEntry<>('B', true)));
  }

  public void testGetOrDefault() {
    assertTrue(mCB.getOrDefault('A', true));
    assertFalse(mCB.getOrDefault('A', false));

    mCB.put('A', true);
    assertTrue(mCB.getOrDefault('A', false));
  }

  public void testContainsValue() {
    assertFalse(mCB.containsValue(true));
    assertFalse(mCB.containsValue(false));

    mCB.put('A', true);
    assertTrue(mCB.containsValue(true));
    assertFalse(mCB.containsValue(false));

    mCB.put('B', false);
    assertTrue(mCB.containsValue(true));
    assertTrue(mCB.containsValue(false));
  }

  public void testInitialSize() {
    CuckooHashMap<String, Integer> m = new CuckooHashMap<>(1000);
    assertTrue(m.isEmpty());

    for (int i = 0; i < 5000; i++) {
      m.put("i=" + i, i);
    }

    assertEquals(5000, m.size());

    for (int i = 0; i < 5000; i++) {
      assertEquals(i, (int) m.get("i=" + i));
    }
  }

  public void testLoadFactor_zeroIsNotValid() {
    try {
      new CuckooHashMap<>(0.f);
      fail("0.0f accepted as a valid load factor.");
    } catch (IllegalArgumentException ex) {
      // Expected.
    }
  }

  public void testLoadFactor_lessThanZero() {
    try {
      new CuckooHashMap<>(-0.1f);
      fail("negative number accepted as a valid load factor.");
    } catch (IllegalArgumentException ex) {
      // Expected.
    }
  }

  public void testLoadFactor_greaterThanOne() {
    try {
      new CuckooHashMap<>(1.01f);
      fail("load factor > 1.f accepted as a valid .");
    } catch (IllegalArgumentException ex) {
      // Expected.
    }
  }
}

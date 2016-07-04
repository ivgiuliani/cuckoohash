package com.github.kratorius.cuckoohash;

import junit.framework.TestCase;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Tests for entrySet(), keySet() and values().
 */
public class ViewsTest extends TestCase {
  @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
  public void testEmptyEntrySet() {
    CuckooHashMap<String, Integer> m = new CuckooHashMap<>();
    assertTrue(m.entrySet().isEmpty());
  }

  public void testSimpleEntrySet() {
    CuckooHashMap<String, Integer> m = new CuckooHashMap<>();

    m.put("key1", 1);
    assertEquals(1, m.entrySet().size());

    Map.Entry<String, Integer> item = m.entrySet().iterator().next();
    assertEquals(1, (int) item.getValue());
  }

  public void testLargeEntrySet() {
    final int size = 1024;
    CuckooHashMap<String, Integer> m = new CuckooHashMap<>();

    for (int i = 0; i < size; i++){
      m.put("key-" + i, 100);
    }

    Set<Map.Entry<String, Integer>> entrySet = m.entrySet();
    assertEquals(size, entrySet.size());
    assertEquals(size, m.size());

    for (int i = 0; i < size; i++) {
      assertTrue(entrySet.contains(new AbstractMap.SimpleEntry<>("key-" + i, 100)));
    }
  }

  public void testKeySet() {
    final int size = 1024;
    CuckooHashMap<String, Integer> m = new CuckooHashMap<>();

    for (int i = 0; i < size; i++){
      m.put("key-" + i, i);
    }

    // keySet() returns a set and therefore there's no guarantee on the ordering of the keys.
    Set<String> keys = m.keySet();
    assertEquals(size, keys.size());

    for (int i = 0; i < size; i++){
      assertTrue(keys.contains("key-" + i));
    }

    // Override a key.
    m.put("key-123", 2134);
    keys = m.keySet();
    assertEquals(size, keys.size());

    for (int i = 0; i < size; i++){
      assertTrue(keys.contains("key-" + i));
    }
  }

  public void testValues_unique() {
    final int size = 1024;
    CuckooHashMap<String, Integer> m = new CuckooHashMap<>();

    for (int i = 0; i < size; i++){
      m.put("key-" + i, i);
    }

    Collection<Integer> values = m.values();
    assertEquals(size, values.size());

    for (int i = 0; i < size; i++){
      assertTrue(values.contains(i));
    }
  }

  public void testValues_duplicates() {
    final int size = 1024;
    CuckooHashMap<String, Integer> m = new CuckooHashMap<>();

    for (int i = 0; i < size; i++){
      m.put("key-" + i, i / 2);
    }

    Collection<Integer> values = m.values();
    assertEquals(size, values.size());

    for (int i = 0; i < size; i++){
      assertTrue(values.contains(i / 2));
    }
  }
}

package com.github.kratorius.cuckoohash;

import junit.framework.TestCase;

public class GetTest extends TestCase {
  public void testGet() {
    CuckooHashMap<String, String> m = new CuckooHashMap<>();

    m.put("key1", "value1");
    m.put("key2", "value2");
    m.put("key3", "value3");
    m.put("key4", "value4");

    assertEquals("value1", m.get("key1"));
    assertEquals("value2", m.get("key2"));
    assertEquals("value3", m.get("key3"));
    assertEquals("value4", m.get("key4"));

    assertNull(m.get("invalid"));
  }

  public void testGetOrDefault() {
    CuckooHashMap<String, String> m = new CuckooHashMap<>();
    assertEquals("default", m.getOrDefault("key", "default"));

    m.put("key", "not default");
    assertEquals("not default", m.getOrDefault("key", "default"));
  }

  public void testGetAfterClear() {
    CuckooHashMap<String, String> m = new CuckooHashMap<>();

    assertNull(m.get("key"));

    m.put("key", "value");
    assertEquals("value", m.get("key"));

    m.clear();

    assertNull(m.get("key"));
  }

  public void testGetAfterUpdate() {
    CuckooHashMap<String, String> m = new CuckooHashMap<>();

    assertNull(m.get("key"));
    for (int i = 0; i < 1024; i++) {
      m.put("key", "value-" + i);
      assertEquals("value-" + i, m.get("key"));
    }

    assertEquals("value-1023", m.get("key"));
  }
}

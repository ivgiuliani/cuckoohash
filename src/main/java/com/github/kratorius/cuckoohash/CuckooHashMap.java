package com.github.kratorius.cuckoohash;

import java.util.*;

/**
 * Cuckoo hash table based implementation of the <tt>Map</tt> interface.
 *
 * @param <K>  the type of keys maintained by this map
 * @param <V>  the type of mapped values
 */
public class CuckooHashMap<K, V> extends AbstractMap<K, V> implements Map<K, V> {
  // TODO implement Cloneable and Serializable

  private static final Random RANDOM = new Random();

  private static final int THRESHOLD_LOOP = 8;
  private static final int DEFAULT_START_SIZE = 16;
  private static final float DEFAULT_LOAD_FACTOR = 0.75f;

  private int defaultStartSize = DEFAULT_START_SIZE;
  private float loadFactor = DEFAULT_LOAD_FACTOR;

  private int size = 0;

  /**
   * Immutable container of entries in the map.
   */
  private static class MapEntry<K1, V1> {
    final K1 key;
    final V1 value;

    MapEntry(final K1 key, final V1 value) {
      this.key = key;
      this.value = value;
    }
  }

  private MapEntry<K, V>[] T1;
  private MapEntry<K, V>[] T2;

  /**
   * Constructs an empty <tt>CuckooHashMap</tt> with the default initial capacity (16).
   */
  public CuckooHashMap() {
    this(DEFAULT_START_SIZE, DEFAULT_LOAD_FACTOR);
  }

  /**
   * Constructs an empty <tt>CuckooHashMap</tt> with the specified initial capacity.
   * The given capacity will be rounded to the nearest power of two.
   *
   * @param initialCapacity  the initial capacity.
   */
  public CuckooHashMap(int initialCapacity) {
    this(initialCapacity, DEFAULT_LOAD_FACTOR);
  }

  /**
   * Constructs an empty <tt>CuckooHashMap</tt> with the specified load factor.
   *
   * The load factor will cause the Cuckoo hash map to double in size when the number
   * of items it contains has filled up more than <tt>loadFactor</tt>% of the available
   * space.
   *
   * @param loadFactor  the load factor.
   */
  public CuckooHashMap(float loadFactor) {
    this(DEFAULT_START_SIZE, loadFactor);
  }

  @SuppressWarnings("unchecked")
  public CuckooHashMap(int initialCapacity, float loadFactor) {
    if (initialCapacity <= 0) {
      throw new IllegalArgumentException("initial capacity must be strictly positive");
    }
    if (loadFactor <= 0.f || loadFactor > 1.f) {
      throw new IllegalArgumentException("load factor must be a value in the (0.0f, 1.0f] range.");
    }

    initialCapacity = roundPowerOfTwo(initialCapacity);
    defaultStartSize = initialCapacity;

    // Capacity is meant to be the total capacity of the two internal tables.
    T1 = new MapEntry[initialCapacity / 2];
    T2 = new MapEntry[initialCapacity / 2];

    this.loadFactor = loadFactor;
  }

  @Override
  public boolean containsKey(Object key) {
    return get(key) != null;
  }

  @Override
  public V get(Object key) {
    return get(key, null);
  }

  @SuppressWarnings("Since15")
  @Override
  public V getOrDefault(Object key, V defaultValue) {
    return get(key, defaultValue);
  }

  private V get(Object key, V defaultValue) {
    if (key == null) {
      throw new NullPointerException();
    }

    MapEntry<K, V> v1 = T1[hash1(key)];
    MapEntry<K, V> v2 = T2[hash2(key)];

    if (v1 == null && v2 == null) {
      return defaultValue;
    } else if (v1 != null && v1.key.equals(key)) {
      return v1.value;
    } else if (v2 != null && v2.key.equals(key)) {
      return v2.value;
    }
    return defaultValue;
  }

  @Override
  public V put(K key, V value) {
    if (key == null) {
      throw new NullPointerException();
    }

    final V old = get(key);
    if (old == null) {
      // If we need to grow after adding this item, it's probably best to grow before we add it.
      final float currentLoad = (size() + 1) / (T1.length + T2.length);
      if (currentLoad >= loadFactor) {
        grow();
      }
    }

    MapEntry<K, V> v;

    while ((v = putSafe(key, value)) != null) {
      // TODO in the original algorithm we do not grow the table but rather pick two new hash functions.
      grow();
      key = v.key;
      value = v.value;
    }

    if (old == null) {
      // Do not increase the size if we're replacing the item.
      size++;
    }

    return old;
  }

  /**
   * @return the key we failed to move because of collisions or <tt>null</tt> if
   * successful.
   */
  private MapEntry<K, V> putSafe(K key, V value) {
    MapEntry<K, V> newV, t1, t2;
    int loop = 0;

    while (loop++ < THRESHOLD_LOOP) {
      newV = new MapEntry<>(key, value);
      t1 = T1[hash1(key)];
      t2 = T2[hash2(key)];

      // Check if we must just update the value first.
      if (t1 != null && t1.key.equals(key)) {
        T1[hash1(key)] = newV;
        return null;
      }
      if (t2 != null && t2.key.equals(key)) {
        T2[hash2(key)] = newV;
        return null;
      }

      // We're intentionally biased towards adding items in T1 since that leads to
      // slightly faster successful lookups.
      if (t1 == null) {
        T1[hash1(key)] = newV;
        return null;
      } else if (t2 == null) {
        T2[hash2(key)] = newV;
        return null;
      } else {
        // Both tables have an item in the required position, we need to move things around.
        if (RANDOM.nextBoolean()) {
          // move from T1
          key = t1.key;
          value= t1.value;
          T1[hash1(key)] = newV;
        } else {
          // move from T2
          key = t2.key;
          value= t2.value;
          T2[hash2(key)] = newV;
        }
      }
    }

    return new MapEntry<>(key, value);
  }

  @Override
  public V remove(Object key) {
    // TODO halve the size of the hashmap when we delete enough keys.

    MapEntry<K, V> v1 = T1[hash1(key)];
    MapEntry<K, V> v2 = T2[hash2(key)];
    V oldValue = null;

    if (v1 != null && v1.key.equals(key)) {
      oldValue = T1[hash1(key)].value;
      T1[hash1(key)] = null;
      size--;
    }

    if (v2 != null && v2.key.equals(key)) {
      oldValue = T2[hash2(key)].value;
      T2[hash2(key)] = null;
      size--;
    }

    return oldValue;
  }

  @SuppressWarnings("unchecked")
  @Override
  public void clear() {
    size = 0;
    T1 = new MapEntry[defaultStartSize / 2];
    T2 = new MapEntry[defaultStartSize / 2];
  }

  /**
   * Double the size of the map until we can successfully manage to re-add all the items
   * we currently contain.
   */
  private void grow() {
    int newSize = T1.length;
    do {
      newSize <<= 1;
    } while (!grow(newSize));
  }

  @SuppressWarnings("unchecked")
  private boolean grow(final int newSize) {
    // Save old state as we may need to restore it if the grow fails.
    MapEntry<K, V>[] oldT1 = T1;
    MapEntry<K, V>[] oldT2 = T2;

    // Already point T1 and T2 to the new tables since putSafe operates on them.
    T1 = new MapEntry[newSize];
    T2 = new MapEntry[newSize];

    for (int i = 0; i < oldT1.length; i++) {
      if (oldT1[i] != null) {
        if (putSafe(oldT1[i].key, oldT1[i].value) != null) {
          T1 = oldT1;
          T2 = oldT2;
          return false;
        }
      }
      if (oldT2[i] != null) {
        if (putSafe(oldT2[i].key, oldT2[i].value) != null) {
          T1 = oldT1;
          T2 = oldT2;
          return false;
        }
      }
    }

    return true;
  }

  @Override
  public int size() {
    return size;
  }

  @Override
  public boolean isEmpty() {
    return size == 0;
  }

  @Override
  public void putAll(Map<? extends K, ? extends V> m) {
    for (Map.Entry<? extends K, ? extends V> entry : m.entrySet()) {
      put(entry.getKey(), entry.getValue());
    }
  }

  @Override
  public Set<K> keySet() {
    Set<K> set = new HashSet<>(size);
    for (int i = 0; i < T1.length; i++) {
      if (T1[i] != null) {
        set.add(T1[i].key);
      }
      if (T2[i] != null) {
        set.add(T2[i].key);
      }
    }
    return set;
  }

  @Override
  public Collection<V> values() {
    List<V> values = new ArrayList<>(size);
    for (int i = 0; i < T1.length; i++) {
      if (T1[i] != null) {
        values.add(T1[i].value);
      }
    }
    for (int i = 0; i < T2.length; i++) {
      if (T2[i] != null) {
        values.add(T2[i].value);
      }
    }
    return values;
  }

  @Override
  public Set<Entry<K, V>> entrySet() {
    Set<Entry<K, V>> entrySet = new HashSet<>(size);
    for (K key : keySet()) {
      entrySet.add(new SimpleEntry<>(key, get(key)));
    }

    return entrySet;
  }

  @Override
  public boolean containsValue(Object value) {
    for (int i = 0; i < T1.length; i++) {
      if (T1[i] != null && T1[i].value.equals(value)) {
        return true;
      }
    }
    for (int i = 0; i < T2.length; i++) {
      if (T2[i] != null && T2[i].value.equals(value)) {
        return true;
      }
    }
    return false;
  }

  @Override
  protected void finalize() throws Throwable {
    super.finalize();
  }

  /**
   * Applies a supplemental hash function to a given hashCode, which defends
   * against poor quality hash functions. This is critical because CuckooHashMap
   * uses power-of-two length hash tables, that otherwise encounter collisions
   * for hashCodes that do not differ in lower or upper bits.
   */
  private static int secondaryHash(int h) {
    // Doug Lea's supplemental hash function
    h ^= (h >>> 20) ^ (h >>> 12);
    return h ^ (h >>> 7) ^ (h >>> 4);
  }

  private int hash1(Object key) {
    // TODO
    int hash = key.hashCode();

    hash = secondaryHash(hash);

    return hash & (T1.length - 1);
  }

  private int hash2(Object key) {
    // TODO
    int hash = key.hashCode() + 1;

    hash = secondaryHash(hash);

    return hash & (T2.length - 1);
  }

  private static int roundPowerOfTwo(int n) {
    n--;

    n |= n >>> 1;
    n |= n >>> 2;
    n |= n >>> 4;
    n |= n >>> 8;
    n |= n >>> 16;

    return (n < 0) ? 1 : n + 1;
  }
}

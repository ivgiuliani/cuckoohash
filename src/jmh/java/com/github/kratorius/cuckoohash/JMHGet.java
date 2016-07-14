package com.github.kratorius.cuckoohash;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.HashMap;
import java.util.Random;

@State(Scope.Thread)
public class JMHGet {
  private CuckooHashMap<Integer, Integer> cuckooHashMap = new CuckooHashMap<>();
  private HashMap<Integer, Integer> hashMap = new HashMap<>();
  private int index = 0;
  private final Random random = new Random();

  @Setup(Level.Iteration)
  public void setup() {
    cuckooHashMap.clear();
    hashMap.clear();

    index = random.nextInt(10240);
    for (int i = 0; i < 102400; i++) {
      cuckooHashMap.put(i, i);
      hashMap.put(i, i);
    }
  }

  @Benchmark
  @Warmup(iterations = 8)
  @Measurement(iterations = 32)
  @BenchmarkMode(Mode.Throughput)
  @Fork(1)
  public CuckooHashMap measureGetCuckooHashMap() {
    cuckooHashMap.get(index);
    return cuckooHashMap;
  }

  @Benchmark
  @Warmup(iterations = 8)
  @Measurement(iterations = 32)
  @BenchmarkMode(Mode.Throughput)
  @Fork(1)
  public HashMap<Integer, Integer> measureGetHashMap() {
    hashMap.get(index);
    return hashMap;
  }

  public static void main(String[] args) throws RunnerException {
    Options opt = new OptionsBuilder()
        .include(".*" + JMHGet.class.getSimpleName() + ".*")
        .forks(1)
        .build();

    new Runner(opt).run();
  }
}

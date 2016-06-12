package com.github.kratorius.cuckoohash;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.HashMap;

@State(Scope.Thread)
public class JMHPut {
  private CuckooHashMap<Integer, Integer> cuckooHashMap = new CuckooHashMap<>();
  private HashMap<Integer, Integer> hashMap = new HashMap<>();
  private int counter = 0;

  @Setup(Level.Iteration)
  public void setup() {
    counter = 0;
    cuckooHashMap.clear();
    hashMap.clear();
  }

  @Benchmark
  @Warmup(iterations = 8)
  @Measurement(iterations = 32)
  @BenchmarkMode(Mode.Throughput)
  @Fork(1)
  public CuckooHashMap measurePutCuckooHashMap() {
    cuckooHashMap.put(counter++, 42);
    return cuckooHashMap;
  }

  @Benchmark
  @Warmup(iterations = 8)
  @Measurement(iterations = 32)
  @BenchmarkMode(Mode.Throughput)
  @Fork(1)
  public HashMap<Integer, Integer> measurePutHashMap() {
    hashMap.put(counter++, 42);
    return hashMap;
  }

  public static void main(String[] args) throws RunnerException {
    Options opt = new OptionsBuilder()
        .include(".*" + JMHPut.class.getSimpleName() + ".*")
        .forks(1)
        .build();

    new Runner(opt).run();
  }
}

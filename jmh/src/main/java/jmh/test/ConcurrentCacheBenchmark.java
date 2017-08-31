package jmh.test;

import com.google.common.base.Supplier;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by ДМИТРИЙ on 23.09.2014.
 */

@State(Scope.Benchmark)
public class ConcurrentCacheBenchmark {

    public final int BUSINESS_LOAD = 1000;

    public static volatile AtomicInteger counter = new AtomicInteger();

    public final Map<String, Collection<String>> syncMapCache1 = new HashMap<String, Collection<String>>();
    public final Map<String, Collection<String>> syncMapCache2 = new HashMap<String, Collection<String>>();
    public final Map<String, Collection<String>> syncMapCache3 = new HashMap<String, Collection<String>>();
    public static final Lock syncMapCache3Lock = new ReentrantLock();
    public final ConcurrentMap<String, Supplier<Collection<String>>> concurrentMapCache = new ConcurrentHashMap<String, Supplier<Collection<String>>>();

    @State(Scope.Thread)
    public static class ThreadState {
        public final String value;
        public ThreadState() {
            value = String.valueOf((char) ('A' + counter.getAndIncrement()));
        }
//        @TearDown(Level.Iteration)
//        public void release() {
//            counter.set(0);
//        }
     }

    @TearDown(Level.Trial)
    public void release() {
        for (String key : syncMapCache1.keySet()) {
            System.out.println(key);
        }
        for (String key : syncMapCache2.keySet()) {
            System.out.println(key);
        }
        for (String key : syncMapCache3.keySet()) {
            System.out.println(key);
        }
        for (String key : concurrentMapCache.keySet()) {
            System.out.println(key);
        }
    }

    @Benchmark
    public void testConcurrentHashMap(Blackhole bh, final ThreadState state) {
        concurrentMapCache.putIfAbsent(state.value, new Supplier<Collection<String>>() {
            private Collection<String> result;

            @Override
            public Collection<String> get() {
                if (result == null) {
                    result = buisinessLoad(state.value);
                }
                return result;
            }
        });
        bh.consume(concurrentMapCache.get(state.value).get());
    }

    @Benchmark
    public void testSyncronizedHashMap(Blackhole bh, final ThreadState state) {
        synchronized (syncMapCache1) {
            if (!syncMapCache1.containsKey(state.value)) {
                syncMapCache1.put(state.value, buisinessLoad(state.value));
            }
        }
        bh.consume(syncMapCache1.get(state.value));
    }

    @Benchmark
    public void testSyncronizedHashMapExplicitLock(Blackhole bh, final ThreadState state) {
        try {
            syncMapCache3Lock.lock();
            if (!syncMapCache3.containsKey(state.value)) {
                syncMapCache3.put(state.value, buisinessLoad(state.value));
            }
        } finally {
            syncMapCache3Lock.unlock();
        }
        bh.consume(syncMapCache3.get(state.value));
    }

    public Collection<String> buisinessLoad(String arg) {
        Collection<String> result = new ArrayList<String>();
        for (int i = 0; i < BUSINESS_LOAD; i++) {
            result.add(String.valueOf(arg));
        }
        return result;
    }
}

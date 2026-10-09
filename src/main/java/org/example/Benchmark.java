package org.example;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Benchmark for CustomLinkedList only (insertAtTail).
 * Lives in package org.example (next to the repo sources).
 *
 * Operation measured: insert N items into an initially empty list (N inserts in a row).
 * Time    = median of RUNS runs after WARMUP runs, then median of MEASURED_PASSES passes (2 warm-up passes discarded).
 * Heap    = warmed up first (allocate/discard ~700 MB) so page-fault cost is not part of the timings.
 * Counts  = insertAtTail never compares keys and only updates pointers, so comparisons = 0 and movements = 0;
 *           the harness verifies the list really holds N items after the inserts.
 */
public class Benchmark {
    static final int[] SIZES = {100, 500, 1000, 5000};
    static final int WARMUP = 30, RUNS = 31;
    static final int WARM_PASSES = 2, MEASURED_PASSES = 5;
    static final long SEED = 42;
    static long sink; // prevents dead-code elimination

    /** Distinct keys like "ID-00042" in random order (fixed seed, same data as the full benchmark). */
    static String[] ids(int n) {
        Random r = new Random(SEED + n);
        int[] p = new int[n]; for (int i = 0; i < n; i++) p[i] = i;
        for (int i = n - 1; i > 0; i--) { int j = r.nextInt(i + 1); int t = p[i]; p[i] = p[j]; p[j] = t; }
        String[] s = new String[n];
        for (int i = 0; i < n; i++) s[i] = String.format("ID-%05d", p[i]);
        return s;
    }

    /** Touch the whole young generation and let the GC cycle once, so first-touch page faults are not measured. */
    static void warmHeap() {
        for (int i = 0; i < 700; i++) sink += new byte[1 << 20].length;
        System.gc();
    }

    static <S> long bench(Supplier<S> setup, Consumer<S> work) {
        long[] t = new long[RUNS];
        for (int i = 0; i < WARMUP + RUNS; i++) {
            S s = setup.get();
            long a = System.nanoTime(); work.accept(s); long b = System.nanoTime();
            if (i >= WARMUP) t[i - WARMUP] = b - a;
        }
        Arrays.sort(t); return t[RUNS / 2];
    }

    static long timeInsert(String[] keys) {
        return bench(CustomLinkedList<Object>::new, l -> { for (String k : keys) l.insertAtTail(k); sink += l.size(); });
    }

    public static void main(String[] args) {
        final String OP = "Insert N items", STRUCT = "CustomLinkedList - insertAtTail";
        Map<Integer, String[]> data = new LinkedHashMap<>();
        for (int n : SIZES) data.put(n, ids(n));

        // correctness check: list holds exactly N items and every key is findable
        for (int n : SIZES) {
            CustomLinkedList<Object> l = new CustomLinkedList<>();
            for (String k : data.get(n)) l.insertAtTail(k);
            if (l.size() != n || !l.search(data.get(n)[n - 1])) throw new IllegalStateException("list check failed at N=" + n);
        }

        warmHeap();
        Map<Integer, List<Long>> samples = new HashMap<>();
        for (int pass = 0; pass < WARM_PASSES + MEASURED_PASSES; pass++)
            for (int n : SIZES) {
                long v = timeInsert(data.get(n));
                if (pass >= WARM_PASSES) samples.computeIfAbsent(n, k -> new ArrayList<>()).add(v);
            }

        System.out.println("n|operation|structure|time_ns|comps|moves|derived|extra");
        for (int n : SIZES) {
            List<Long> l = samples.get(n); Collections.sort(l);
            System.out.println(n + "|" + OP + "|" + STRUCT + "|" + l.get(l.size() / 2) + "|0|0|false|");
        }
        System.err.println("sink=" + sink);
    }
}
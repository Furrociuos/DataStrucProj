package org.example;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Benchmark harness for the DataStrucProj data structures.
 * Lives in package org.example (next to the repo sources) so it can call the package-private BST_Methods.
 *
 * Phase 1 (timing):   real classes, plain String/Integer keys, median of RUNS after WARMUP runs.
 * Phase 2 (counting): real classes, but keys/comparators are instrumented to count equals()/compare() calls.
 *                     Movements are measured via capacity changes (reflection / capacity()), or derived (see notes).
 * Every "operation" is applied to the WHOLE dataset of size N (e.g. insert N items), so Time and counts are totals.
 */
public class Benchmark {
    static final int[] SIZES = {100, 500, 1000, 5000};
    static final int WARMUP = 30, RUNS = 31;
    static final long SEED = 42;
    static long sink; // prevents dead-code elimination

    // ---------- instrumentation ----------
    static final class CKey {
        static long eq = 0;
        final String s;
        CKey(String s) { this.s = s; }
        @Override public boolean equals(Object o) { eq++; return o instanceof CKey && ((CKey) o).s.equals(s); }
        @Override public int hashCode() { return s.hashCode(); }
    }

    /** Replica of MinHeap<Integer>, used ONLY to count swaps/copies; cross-checked against the real class. */
    static final class RHeap {
        int[] h = new int[16]; int size; long cmp, swaps, copies;
        int c(int a, int b) { cmp++; return Integer.compare(a, b); }
        void insert(int x) {
            if (size == h.length) { copies += h.length; h = Arrays.copyOf(h, h.length * 2); }
            h[size] = x; siftUp(size); size++;
        }
        int extract() { int m = h[0]; size--; h[0] = h[size]; if (size > 0) siftDown(0); return m; }
        void sw(int i, int j) { int t = h[i]; h[i] = h[j]; h[j] = t; swaps++; }
        void siftUp(int i) { while (i > 0) { int p = (i - 1) / 2; if (c(h[i], h[p]) < 0) { sw(i, p); i = p; } else break; } }
        void siftDown(int i) {
            while (true) {
                int l = 2 * i + 1, r = 2 * i + 2, s = i;
                if (l < size && c(h[l], h[s]) < 0) s = l;
                if (r < size && c(h[r], h[s]) < 0) s = r;
                if (s == i) break;
                sw(i, s); i = s;
            }
        }
    }

    // ---------- data ----------
    static final class Data {
        final int n; final String[] ins, look, del; final int[] bstKeys, bstSorted, bstLook, bstDel;
        final Integer[] heapVals; final Task[] tasks;
        Data(int n) {
            this.n = n; Random r = new Random(SEED + n);
            ins = ids(n, r); look = shuf(ins, r); del = shuf(ins, r);
            int[] k = perm(n, r); for (int i = 0; i < n; i++) k[i] = k[i] * 3 + 1;
            bstKeys = k; bstSorted = k.clone(); Arrays.sort(bstSorted);
            bstLook = shuf(k, r); bstDel = shuf(k, r);
            int[] hv = perm(n, r); heapVals = new Integer[n]; for (int i = 0; i < n; i++) heapVals[i] = hv[i];
            tasks = new Task[n]; for (int i = 0; i < n; i++) tasks[i] = new Task("T" + i, "chore" + i, i);
        }
        static int[] perm(int n, Random r) { int[] a = new int[n]; for (int i = 0; i < n; i++) a[i] = i;
            for (int i = n - 1; i > 0; i--) { int j = r.nextInt(i + 1); int t = a[i]; a[i] = a[j]; a[j] = t; } return a; }
        static String[] ids(int n, Random r) { int[] p = perm(n, r); String[] s = new String[n];
            for (int i = 0; i < n; i++) s[i] = String.format("ID-%05d", p[i]); return s; }
        static String[] shuf(String[] a, Random r) { String[] b = a.clone();
            for (int i = b.length - 1; i > 0; i--) { int j = r.nextInt(i + 1); String t = b[i]; b[i] = b[j]; b[j] = t; } return b; }
        static int[] shuf(int[] a, Random r) { int[] b = a.clone();
            for (int i = b.length - 1; i > 0; i--) { int j = r.nextInt(i + 1); int t = b[i]; b[i] = b[j]; b[j] = t; } return b; }
    }

    // ---------- BST helpers (read-only walks over the real tree via its public fields) ----------
    static long visits(BST_Methods.Node root, int key) {
        long v = 0; BST_Methods.Node n = root;
        while (n != null) { v++; if (key == n.data) break; n = key < n.data ? n.left : n.right; }
        return v;
    }
    /** returns {comparisons, movements} for the upcoming delete(root,key). */
    static long[] deleteCost(BST_Methods.Node root, int key) {
        long k = 0; BST_Methods.Node n = root;
        while (n != null) { k++; if (key == n.data) break; n = key < n.data ? n.left : n.right; }
        if (n == null) return new long[]{k, 0};
        if (n.left != null && n.right != null) {
            long v = 1; BST_Methods.Node c = n.right; while (c.left != null) { c = c.left; v++; }
            return new long[]{k + 2 * v, 1}; // successor walk + re-descent to delete it; 1 key/item copy
        }
        return new long[]{k, 0};
    }
    static int height(BST_Methods.Node n) { return n == null ? 0 : 1 + Math.max(height(n.left), height(n.right)); }

    // ---------- timing ----------
    static <S> long bench(Supplier<S> setup, Consumer<S> work) {
        long[] t = new long[RUNS];
        for (int i = 0; i < WARMUP + RUNS; i++) {
            S s = setup.get();
            long a = System.nanoTime(); work.accept(s); long b = System.nanoTime();
            if (i >= WARMUP) t[i - WARMUP] = b - a;
        }
        Arrays.sort(t); return t[RUNS / 2];
    }

    interface Timer { long run(Data d) throws Exception; }
    interface Counter { Res run(Data d) throws Exception; }
    record Res(long comps, long moves, boolean derived, String extra) {}
    static final class Case {
        final String op, struct; final Timer timer; final Counter counter;
        final Map<Integer, Long> time = new LinkedHashMap<>(); final Map<Integer, Res> res = new LinkedHashMap<>();
        Case(String op, String struct, Timer t, Counter c) { this.op = op; this.struct = struct; this.timer = t; this.counter = c; }
    }

    // ---------- builders ----------
    static CustomLinkedList<Object> buildLL(Object[] keys) { CustomLinkedList<Object> l = new CustomLinkedList<>(); for (Object k : keys) l.insertAtTail(k); return l; }
    static HashTable<Object, Integer> buildHT(Object[] keys) { HashTable<Object, Integer> t = new HashTable<>(); for (Object k : keys) t.put(k, 1); return t; }
    static SimpleList<Object> buildSL(Object[] keys) { SimpleList<Object> l = new SimpleList<>(); for (Object k : keys) l.add(k); return l; }
    static BST_Methods.Node buildBST(int[] keys) { BST_Methods.Node r = null; for (int k : keys) r = BST_Methods.insert(r, k, "x"); return r; }
    static CKey[] ck(String[] s) { CKey[] a = new CKey[s.length]; for (int i = 0; i < s.length; i++) a[i] = new CKey(s[i]); return a; }
    static ChoreGraph buildChain(int n) { ChoreGraph g = new ChoreGraph(); for (int i = 0; i < n - 1; i++) g.addDependency("c" + i, "c" + (i + 1)); return g; }

    public static void main(String[] args) throws Exception {
        Thread t = new Thread(null, () -> { try { run(); } catch (Exception e) { throw new RuntimeException(e); } }, "bench", 64L << 20);
        t.start(); t.join();
    }

    static void run() throws Exception {
        List<Case> cases = new ArrayList<>();
        final String INS = "Insert N items", SRCH = "Search N existing keys", REM = "Remove N items (random order)";
        final Field slData = SimpleList.class.getDeclaredField("data"); slData.setAccessible(true);

        // ===== INSERT =====
        cases.add(new Case(INS, "CustomLinkedList - insertAtTail",
            d -> bench(CustomLinkedList<Object>::new, l -> { for (String s : d.ins) l.insertAtTail(s); sink += l.size(); }),
            d -> new Res(0, 0, false, "")));
        cases.add(new Case(INS, "SimpleList (dynamic array) - add",
            d -> bench(SimpleList<Object>::new, l -> { for (String s : d.ins) l.add(s); sink += l.size(); }),
            d -> { SimpleList<Object> l = new SimpleList<>(); long mv = 0; int cap = 10;
                   for (String s : d.ins) { int b = ((Object[]) slData.get(l)).length; l.add(s); int a = ((Object[]) slData.get(l)).length; if (a != b) mv += b; cap = a; }
                   return new Res(0, mv, false, "final capacity=" + cap); }));
        cases.add(new Case(INS, "HashTable (chaining) - put",
            d -> bench(HashTable<Object, Integer>::new, h -> { for (String s : d.ins) h.put(s, 1); sink += h.size(); }),
            d -> { HashTable<Object, Integer> h = new HashTable<>(); CKey.eq = 0; long mv = 0;
                   for (CKey k : ck(d.ins)) { int cb = h.capacity(); h.put(k, 1); if (h.capacity() != cb) mv += h.size(); }
                   return new Res(CKey.eq, mv, false, "capacity=" + h.capacity() + ", collisions=" + h.collisionCount()); }));
        cases.add(new Case(INS, "BST - insert (random keys)",
            d -> bench(() -> new BST_Methods.Node[1], r -> { for (int k : d.bstKeys) r[0] = BST_Methods.insert(r[0], k, "x"); sink += r[0].data; }),
            d -> { BST_Methods.Node r = null; long c = 0; for (int k : d.bstKeys) { c += visits(r, k); r = BST_Methods.insert(r, k, "x"); }
                   return new Res(c, 0, false, "height=" + height(r)); }));
        cases.add(new Case(INS, "BST - insert (sorted keys, worst case)",
            d -> bench(() -> new BST_Methods.Node[1], r -> { for (int k : d.bstSorted) r[0] = BST_Methods.insert(r[0], k, "x"); sink += r[0].data; }),
            d -> { BST_Methods.Node r = null; long c = 0; for (int k : d.bstSorted) { c += visits(r, k); r = BST_Methods.insert(r, k, "x"); }
                   return new Res(c, 0, false, "height=" + height(r)); }));
        cases.add(new Case(INS, "MinHeap - insert (sift-up)",
            d -> bench(() -> new MinHeap<Integer>(Integer::compare), h -> { for (Integer v : d.heapVals) h.insert(v); sink += h.size(); }),
            d -> { long[] cnt = new long[1]; MinHeap<Integer> h = new MinHeap<>((a, b) -> { cnt[0]++; return Integer.compare(a, b); }); RHeap rh = new RHeap();
                   for (Integer v : d.heapVals) { h.insert(v); rh.insert(v); }
                   if (cnt[0] != rh.cmp) throw new IllegalStateException("heap replica mismatch (insert)");
                   return new Res(cnt[0], rh.swaps + rh.copies, false, "swaps=" + rh.swaps + ", resize copies=" + rh.copies); }));

        // ===== SEARCH =====
        cases.add(new Case(SRCH, "CustomLinkedList - search",
            d -> bench(() -> buildLL(d.ins), l -> { int hit = 0; for (String s : d.look) if (l.search(s)) hit++; sink += hit; }),
            d -> { CustomLinkedList<Object> l = buildLL(ck(d.ins)); CKey.eq = 0; for (CKey k : ck(d.look)) l.search(k); return new Res(CKey.eq, 0, false, ""); }));
        cases.add(new Case(SRCH, "BST - search (random-key tree)",
            d -> { BST_Methods.Node root = buildBST(d.bstKeys); return bench(() -> root, r -> { int hit = 0; for (int k : d.bstLook) if (BST_Methods.search(r, k)) hit++; sink += hit; }); },
            d -> { BST_Methods.Node r = buildBST(d.bstKeys); long c = 0; for (int k : d.bstLook) c += visits(r, k); return new Res(c, 0, false, "height=" + height(r)); }));
        cases.add(new Case(SRCH, "HashTable (chaining) - get",
            d -> bench(() -> buildHT(d.ins), h -> { int hit = 0; for (String s : d.look) if (h.get(s) != null) hit++; sink += hit; }),
            d -> { HashTable<Object, Integer> h = buildHT(ck(d.ins)); CKey.eq = 0; for (CKey k : ck(d.look)) h.get(k);
                   return new Res(CKey.eq, 0, false, "capacity=" + h.capacity() + ", collisions=" + h.collisionCount()); }));

        // ===== REMOVE =====
        cases.add(new Case(REM, "CustomLinkedList - delete",
            d -> bench(() -> buildLL(d.ins), l -> { for (String s : d.del) l.delete(s); sink += l.size(); }),
            d -> { CustomLinkedList<Object> l = buildLL(ck(d.ins)); CKey.eq = 0; for (CKey k : ck(d.del)) l.delete(k); return new Res(CKey.eq, 0, false, ""); }));
        cases.add(new Case(REM, "SimpleList (dynamic array) - remove",
            d -> bench(() -> buildSL(d.ins), l -> { for (String s : d.del) l.remove(s); sink += l.size(); }),
            d -> { SimpleList<Object> l = buildSL(ck(d.ins)); CKey.eq = 0; long mv = 0;
                   for (CKey k : ck(d.del)) { long b = CKey.eq; int sz = l.size(); l.remove(k); mv += sz - (CKey.eq - b); }
                   return new Res(CKey.eq, mv, false, "element shifts"); }));
        cases.add(new Case(REM, "HashTable (chaining) - remove",
            d -> bench(() -> buildHT(d.ins), h -> { for (String s : d.del) h.remove(s); sink += h.size(); }),
            d -> { HashTable<Object, Integer> h = buildHT(ck(d.ins)); CKey.eq = 0; for (CKey k : ck(d.del)) h.remove(k); return new Res(CKey.eq, 0, false, ""); }));
        cases.add(new Case(REM, "BST - delete (random-key tree)",
            d -> bench(() -> buildBST(d.bstKeys), r -> { BST_Methods.Node root = r; for (int k : d.bstDel) root = BST_Methods.delete(root, k); sink += (root == null ? 0 : 1); }),
            d -> { BST_Methods.Node r = buildBST(d.bstKeys); long c = 0, m = 0; for (int k : d.bstDel) { long[] x = deleteCost(r, k); c += x[0]; m += x[1]; r = BST_Methods.delete(r, k); }
                   return new Res(c, m, false, "movements = successor key copies (2-child deletes)"); }));

        // ===== HEAP EXTRACT =====
        cases.add(new Case("Extract-min N items", "MinHeap - extractMin (sift-down)",
            d -> bench(() -> { MinHeap<Integer> h = new MinHeap<>(Integer::compare); for (Integer v : d.heapVals) h.insert(v); return h; },
                       h -> { long s = 0; while (!h.isEmpty()) s += h.extractMin(); sink += s; }),
            d -> { long[] cnt = new long[1]; MinHeap<Integer> h = new MinHeap<>((a, b) -> { cnt[0]++; return Integer.compare(a, b); }); RHeap rh = new RHeap();
                   for (Integer v : d.heapVals) { h.insert(v); rh.insert(v); }
                   cnt[0] = 0; rh.cmp = 0; rh.swaps = 0; int prev = Integer.MIN_VALUE;
                   while (!h.isEmpty()) { int x = h.extractMin(); int y = rh.extract(); if (x != y || x < prev) throw new IllegalStateException("heap order mismatch"); prev = x; }
                   if (cnt[0] != rh.cmp) throw new IllegalStateException("heap replica mismatch (extract)");
                   return new Res(cnt[0], rh.swaps, false, "output verified sorted"); }));

        // ===== QUEUE =====
        cases.add(new Case("Enqueue N + dequeue N", "TaskQueue (linked) - enqueue/dequeue",
            d -> bench(TaskQueue::new, q -> { for (Task t : d.tasks) q.enqueue(t); long s = 0; while (!q.isEmpty()) s += q.dequeue().getDeadline(); sink += s; }),
            d -> new Res(0, 0, false, "")));

        // ===== GRAPH (chain DAG: c0 -> c1 -> ... -> c(N-1)) =====
        cases.add(new Case("Cycle detection (DFS)", "ChoreGraph - hasCycle (chain DAG)",
            d -> bench(() -> buildChain(d.n), g -> sink += g.hasCycle() ? 1 : 0),
            d -> new Res(2L * d.n - 1, -1, true, "V+E = " + (2L * d.n - 1) + " visits (1 pass)")));
        cases.add(new Case("Topological sort (BFS / Kahn)", "ChoreGraph - getTopologicalOrder (chain DAG)",
            d -> bench(() -> buildChain(d.n), g -> sink += g.getTopologicalOrder().size()),
            d -> new Res(3L * (2L * d.n - 1), -1, true, "3 passes x (V+E): cycle check + in-degree + Kahn")));

        // verify graph correctness once
        if (buildChain(50).getTopologicalOrder().size() != 50 || buildChain(50).hasCycle()) throw new IllegalStateException("graph sanity");

        // Phase 1: timing
        Map<Integer, Data> data = new HashMap<>(); for (int n : SIZES) data.put(n, new Data(n));
        // 2 discarded warm-up passes over ALL cases/sizes (lets the JIT compile everything), then 5 measured passes; report the median pass.
        final int WARM_PASSES = 2, MEASURED_PASSES = 5;
        Map<Case, Map<Integer, List<Long>>> samples = new HashMap<>();
        for (int pass = 0; pass < WARM_PASSES + MEASURED_PASSES; pass++) {
            for (Case c : cases) for (int n : SIZES) {
                long v = c.timer.run(data.get(n));
                if (pass >= WARM_PASSES) samples.computeIfAbsent(c, k -> new HashMap<>()).computeIfAbsent(n, k -> new ArrayList<>()).add(v);
            }
        }
        for (Case c : cases) for (int n : SIZES) { List<Long> l = samples.get(c).get(n); Collections.sort(l); c.time.put(n, l.get(l.size() / 2)); }
        // Phase 2: counting
        for (Case c : cases) for (int n : SIZES) c.res.put(n, c.counter.run(data.get(n)));

        System.out.println("n|operation|structure|time_ns|comps|moves|derived|extra");
        for (Case c : cases) for (int n : SIZES) { Res r = c.res.get(n);
            System.out.println(n + "|" + c.op + "|" + c.struct + "|" + c.time.get(n) + "|" + r.comps() + "|" + r.moves() + "|" + r.derived() + "|" + r.extra()); }
        System.err.println("sink=" + sink);
    }
}

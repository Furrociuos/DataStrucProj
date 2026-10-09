package org.example;

import java.io.*;
import java.util.*;
import java.util.function.Supplier;

/** Black-box test suite for DataStrucProj. Expected values are written by hand from standard data-structure semantics
 *  BEFORE running; actual values are captured from the real classes. Output: id \t label \t expected \t actual \t PASS|FAIL */
public class TestSuite {
    static final List<String[]> out = new ArrayList<>();

    static void check(String id, String label, String expected, Supplier<String> actual) {
        String a;
        try { a = actual.get(); } catch (Throwable t) { a = "threw " + t.getClass().getSimpleName(); }
        out.add(new String[]{id, label, expected, a, expected.equals(a) ? "PASS" : "FAIL"});
    }
    static String capture(Runnable r) {
        PrintStream old = System.out; ByteArrayOutputStream b = new ByteArrayOutputStream();
        System.setOut(new PrintStream(b, true));
        try { r.run(); } finally { System.setOut(old); }
        String s = b.toString().replace("\r", "").trim();
        return s.isEmpty() ? "<none>" : s.replace("\n", " ; ");
    }
    static void quiet(Runnable r) { capture(r); }

    // BST helpers (read-only, via public Node fields)
    static void inorder(BST_Methods.Node n, List<Integer> acc) { if (n == null) return; inorder(n.left, acc); acc.add(n.data); inorder(n.right, acc); }
    static String keys(BST_Methods.Node n) { List<Integer> l = new ArrayList<>(); inorder(n, l); return l.isEmpty() ? "<empty>" : String.join(",", l.stream().map(String::valueOf).toList()); }
    static boolean valid(BST_Methods.Node n, long lo, long hi) { return n == null || (n.data > lo && n.data < hi && valid(n.left, lo, n.data) && valid(n.right, n.data, hi)); }
    static boolean payloadOk(BST_Methods.Node n) { return n == null || (n.item.equals("item-" + n.data) && payloadOk(n.left) && payloadOk(n.right)); }
    static int count(BST_Methods.Node n) { return n == null ? 0 : 1 + count(n.left) + count(n.right); }
    static BST_Methods.Node bst(int... ks) { BST_Methods.Node r = null; for (int k : ks) r = BST_Methods.insert(r, k, "item-" + k); return r; }

    static BST_Methods.Node t9;

    public static void main(String[] args) {
        // ================= T01 Empty structure =================
        check("T01", "CustomLinkedList", "isEmpty=true, size=0, search=false, delete=false", () -> {
            var l = new CustomLinkedList<String>(); return "isEmpty=" + l.isEmpty() + ", size=" + l.size() + ", search=" + l.search("x") + ", delete=" + l.delete("x"); });
        check("T01", "SimpleList", "isEmpty=true, size=0, remove=false, get(0)=IndexOutOfBoundsException", () -> {
            var l = new SimpleList<String>(); String g; try { l.get(0); g = "no exception"; } catch (IndexOutOfBoundsException e) { g = "IndexOutOfBoundsException"; }
            return "isEmpty=" + l.isEmpty() + ", size=" + l.size() + ", remove=" + l.remove("x") + ", get(0)=" + g; });
        check("T01", "TaskQueue", "isEmpty=true, size=0, peek=null, dequeue=null, display='No pending chores.'", () -> {
            var q = new TaskQueue(); return "isEmpty=" + q.isEmpty() + ", size=" + q.size() + ", peek=" + q.peek() + ", dequeue=" + q.dequeue() + ", display='" + capture(q::display) + "'"; });
        check("T01", "MinHeap", "isEmpty=true, size=0, peek=null, extractMin=null", () -> {
            var h = new MinHeap<Integer>(Integer::compare); return "isEmpty=" + h.isEmpty() + ", size=" + h.size() + ", peek=" + h.peek() + ", extractMin=" + h.extractMin(); });
        check("T01", "HashTable", "isEmpty=true, get=null, containsKey=false, remove=null", () -> {
            var h = new HashTable<String, Integer>(); return "isEmpty=" + h.isEmpty() + ", get=" + h.get("x") + ", containsKey=" + h.containsKey("x") + ", remove=" + h.remove("x"); });
        check("T01", "BST_Chores", "search=false, delete=no error, range search output=<none>", () -> {
            var b = new BST_Chores(); boolean s = b.searchChore(5); b.deleteChore(5); return "search=" + s + ", delete=no error, range search output=" + capture(() -> b.searchRange(0, 100)); });
        check("T01", "ChoreGraph", "hasCycle=false, topologicalOrder=[]", () -> {
            var g = new ChoreGraph(); return "hasCycle=" + g.hasCycle() + ", topologicalOrder=" + g.getTopologicalOrder(); });
        check("T01", "HouseholdService", "complete -> 'No chores left to complete.' ; view next -> 'No chores pending.'", () -> {
            var s = new HouseholdService(); return "complete -> '" + capture(s::completeNextChore) + "' ; view next -> '" + capture(s::viewNextPriorityChore) + "'"; });

        // ================= T02 Single record =================
        check("T02", "CustomLinkedList", "size=1, search=true, delete=true, isEmpty after delete=true, re-insert works (size=1, search=true)", () -> {
            var l = new CustomLinkedList<String>(); l.insertAtTail("A"); String r = "size=" + l.size() + ", search=" + l.search("A") + ", delete=" + l.delete("A") + ", isEmpty after delete=" + l.isEmpty();
            l.insertAtTail("B"); return r + ", re-insert works (size=" + l.size() + ", search=" + l.search("B") + ")"; });
        check("T02", "SimpleList", "size=1, get(0)=A, remove=true, isEmpty after remove=true", () -> {
            var l = new SimpleList<String>(); l.add("A"); return "size=" + l.size() + ", get(0)=" + l.get(0) + ", remove=" + l.remove("A") + ", isEmpty after remove=" + l.isEmpty(); });
        check("T02", "TaskQueue", "peek=Dishes, dequeue=Dishes, isEmpty after=true, re-enqueue works (peek=Cook)", () -> {
            var q = new TaskQueue(); q.enqueue(new Task("1", "Dishes", 5)); String r = "peek=" + q.peek().getTitle() + ", dequeue=" + q.dequeue().getTitle() + ", isEmpty after=" + q.isEmpty();
            q.enqueue(new Task("2", "Cook", 6)); return r + ", re-enqueue works (peek=" + q.peek().getTitle() + ")"; });
        check("T02", "MinHeap", "peek=42, extractMin=42, isEmpty after=true, extractMin again=null", () -> {
            var h = new MinHeap<Integer>(Integer::compare); h.insert(42); return "peek=" + h.peek() + ", extractMin=" + h.extractMin() + ", isEmpty after=" + h.isEmpty() + ", extractMin again=" + h.extractMin(); });
        check("T02", "HashTable", "put returns null, size=1, get=V, remove=V, size after=0", () -> {
            var h = new HashTable<String, String>(); return "put returns " + h.put("K", "V") + ", size=" + h.size() + ", get=" + h.get("K") + ", remove=" + h.remove("K") + ", size after=" + h.size(); });
        check("T02", "BST", "search(10)=true, 1 node, delete then search(10)=false, tree empty", () -> {
            var r = bst(10); String a = "search(10)=" + BST_Methods.search(r, 10) + ", " + count(r) + " node"; r = BST_Methods.delete(r, 10);
            return a + ", delete then search(10)=" + BST_Methods.search(r, 10) + ", tree " + (r == null ? "empty" : "not empty"); });
        check("T02", "ChoreGraph", "topologicalOrder=[Dishes], hasCycle=false", () -> {
            var g = new ChoreGraph(); g.addChore("Dishes"); return "topologicalOrder=" + g.getTopologicalOrder() + ", hasCycle=" + g.hasCycle(); });

        // ================= T03 Duplicate record =================
        check("T03", "HashTable", "first put returns null, second put returns old value 1, size=1, get=2", () -> {
            var h = new HashTable<String, Integer>(); Integer a = h.put("K", 1); Integer b = h.put("K", 2); return "first put returns " + a + ", second put returns old value " + b + ", size=" + h.size() + ", get=" + h.get("K"); });
        check("T03", "HouseholdHashTables", "addMember dup -> true,false ; addTask dup -> true,false ; addExpense dup -> true,false ; blank ID -> false", () -> {
            var t = new HouseholdHashTables();
            String m = t.addMember(new Member("M1", "Ana")) + "," + t.addMember(new Member("M1", "Ben"));
            String k = t.addTask(new Task("C1", "Dishes", 5)) + "," + t.addTask(new Task("C1", "Laundry", 6));
            String e = t.addExpense(new Expense("E1", "Food", 100)) + "," + t.addExpense(new Expense("E1", "Food", 200));
            return "addMember dup -> " + m + " ; addTask dup -> " + k + " ; addExpense dup -> " + e + " ; blank ID -> " + t.addMember(new Member(" ", "X")); });
        check("T03", "BST (duplicate key)", "ignored: 1 node, original item kept (first), search=true", () -> {
            BST_Methods.Node r = BST_Methods.insert(null, 10, "first"); r = BST_Methods.insert(r, 10, "second");
            return "ignored: " + count(r) + " node, original item kept (" + r.item + "), search=" + BST_Methods.search(r, 10); });
        check("T03", "CustomLinkedList (history log)", "allows repeats: size=2 ; delete removes one occurrence only: size=1, search still true", () -> {
            var l = new CustomLinkedList<String>(); l.insertAtTail("A"); l.insertAtTail("A"); String a = "allows repeats: size=" + l.size(); l.delete("A");
            return a + " ; delete removes one occurrence only: size=" + l.size() + ", search still " + l.search("A"); });
        check("T03", "ChoreGraph", "duplicate chore and duplicate edge -> order=[A, B], hasCycle=false", () -> {
            var g = new ChoreGraph(); g.addChore("A"); g.addChore("A"); g.addDependency("A", "B"); g.addDependency("A", "B");
            return "duplicate chore and duplicate edge -> order=" + g.getTopologicalOrder() + ", hasCycle=" + g.hasCycle(); });
        check("T03", "HouseholdService (duplicate chore ID)", "'Could not add chore (duplicate or invalid ID).' ; complete -> 'Completed: Dishes' ; complete again -> 'No chores left to complete.'", () -> {
            var s = new HouseholdService(); quiet(() -> s.addChore("C1", "Dishes", 5, null));
            String d = capture(() -> s.addChore("C1", "Laundry", 6, null)); String c1 = capture(s::completeNextChore); String c2 = capture(s::completeNextChore);
            return "'" + d + "' ; complete -> '" + c1 + "' ; complete again -> '" + c2 + "'"; });

        // ================= T04 Search existing key =================
        check("T04", "CustomLinkedList", "first=true, middle=true, last=true", () -> {
            var l = new CustomLinkedList<String>(); l.insertAtTail("A"); l.insertAtTail("B"); l.insertAtTail("C");
            return "first=" + l.search("A") + ", middle=" + l.search("B") + ", last=" + l.search("C"); });
        check("T04", "SimpleList", "get(0)=A, get(1)=B, get(2)=C", () -> {
            var l = new SimpleList<String>(); l.add("A"); l.add("B"); l.add("C"); return "get(0)=" + l.get(0) + ", get(1)=" + l.get(1) + ", get(2)=" + l.get(2); });
        check("T04", "HashTable (50 keys, forces resize)", "50/50 found with correct value, containsKey=true", () -> {
            var h = new HashTable<String, Integer>(); for (int i = 0; i < 50; i++) h.put("K" + i, i);
            int ok = 0; for (int i = 0; i < 50; i++) if (h.get("K" + i) != null && h.get("K" + i) == i) ok++; return ok + "/50 found with correct value, containsKey=" + h.containsKey("K7"); });
        check("T04", "BST (root, internal, leaf)", "7/7 keys found", () -> {
            var r = bst(50, 30, 70, 20, 40, 60, 80); int ok = 0; for (int k : new int[]{50, 30, 70, 20, 40, 60, 80}) if (BST_Methods.search(r, k)) ok++; return ok + "/7 keys found"; });
        check("T04", "BST range search [10,20]", "c10 (deadline: 10) ; c15 (deadline: 15) ; c20 (deadline: 20)", () -> {
            BST_Methods.Node r = null; for (int k : new int[]{15, 5, 25, 10, 20}) r = BST_Methods.insert(r, k, "c" + k); final BST_Methods.Node root = r;
            return capture(() -> BST_Methods.rangeSearch(root, 10, 20)); });
        check("T04", "HouseholdHashTables", "getMember=Ana, getTask=Dishes, getExpense=Food 250.0", () -> {
            var t = new HouseholdHashTables(); t.addMember(new Member("M1", "Ana")); t.addTask(new Task("C1", "Dishes", 5)); t.addExpense(new Expense("E1", "Food", 250));
            return "getMember=" + t.getMember("M1").getName() + ", getTask=" + t.getTask("C1").getTitle() + ", getExpense=" + t.getExpense("E1").getCategory() + " " + t.getExpense("E1").getAmount(); });
        check("T04", "HouseholdService member lookup", "- Dishes (deadline: 5)", () -> {
            var s = new HouseholdService(); quiet(() -> { s.addMember("M1", "Ana"); s.addChore("C1", "Dishes", 5, "M1"); }); return capture(() -> s.lookupMemberChores("M1")); });

        // ================= T05 Search missing key =================
        check("T05", "CustomLinkedList", "search=false, delete=false, size unchanged=3", () -> {
            var l = new CustomLinkedList<String>(); l.insertAtTail("A"); l.insertAtTail("B"); l.insertAtTail("C");
            return "search=" + l.search("Z") + ", delete=" + l.delete("Z") + ", size unchanged=" + l.size(); });
        check("T05", "SimpleList", "remove=false, size unchanged=2", () -> {
            var l = new SimpleList<String>(); l.add("A"); l.add("B"); return "remove=" + l.remove("Z") + ", size unchanged=" + l.size(); });
        check("T05", "HashTable", "get=null, containsKey=false, remove=null, size unchanged=3", () -> {
            var h = new HashTable<String, Integer>(); h.put("A", 1); h.put("B", 2); h.put("C", 3);
            return "get=" + h.get("Z") + ", containsKey=" + h.containsKey("Z") + ", remove=" + h.remove("Z") + ", size unchanged=" + h.size(); });
        check("T05", "BST (below min / above max / between)", "below min=false, above max=false, between keys=false", () -> {
            var r = bst(50, 30, 70); return "below min=" + BST_Methods.search(r, 1) + ", above max=" + BST_Methods.search(r, 99) + ", between keys=" + BST_Methods.search(r, 55); });
        check("T05", "BST delete missing key / empty range", "keys unchanged=30,50,70 ; range [100,200] output=<none>", () -> {
            var r = bst(50, 30, 70); r = BST_Methods.delete(r, 999); final BST_Methods.Node root = r;
            return "keys unchanged=" + keys(r) + " ; range [100,200] output=" + capture(() -> BST_Methods.rangeSearch(root, 100, 200)); });
        check("T05", "HouseholdHashTables", "getMember=null, getTask=null, getExpense=null, tasksOfUnknownMember=empty, expensesOfUnknownCategory=empty", () -> {
            var t = new HouseholdHashTables(); return "getMember=" + t.getMember("nope") + ", getTask=" + t.getTask("nope") + ", getExpense=" + t.getExpense("nope")
                + ", tasksOfUnknownMember=" + (t.getTasksOfMember("nope").isEmpty() ? "empty" : "not empty") + ", expensesOfUnknownCategory=" + (t.getExpensesByCategory("nope").isEmpty() ? "empty" : "not empty"); });
        check("T05", "HouseholdService unknown member", "No chores found for this member.", () -> capture(() -> new HouseholdService().lookupMemberChores("ghost")));

        // ================= T06 Hash collision =================
        check("T06", "Colliding pair 'Aa' / 'BB' (same hashCode)", "same hashCode=true, same bucket=true, collisionCount=1, get(Aa)=1, get(BB)=2, size=2", () -> {
            var h = new HashTable<String, Integer>(); h.put("Aa", 1); h.put("BB", 2);
            return "same hashCode=" + ("Aa".hashCode() == "BB".hashCode()) + ", same bucket=" + (h.indexOf("Aa") == h.indexOf("BB")) + ", collisionCount=" + h.collisionCount()
                + ", get(Aa)=" + h.get("Aa") + ", get(BB)=" + h.get("BB") + ", size=" + h.size(); });
        check("T06", "Remove from a collision chain", "remove(Aa)=1, BB still found=2, Aa gone=null, size=1", () -> {
            var h = new HashTable<String, Integer>(); h.put("Aa", 1); h.put("BB", 2);
            return "remove(Aa)=" + h.remove("Aa") + ", BB still found=" + h.get("BB") + ", Aa gone=" + h.get("Aa") + ", size=" + h.size(); });
        check("T06", "4 keys, one bucket, remove middle", "all 4 stored (size=4, collisionCount=3), remove(BBBB)=3, remaining AaAa=1 AaBB=2 BBAa=4, BBBB=null", () -> {
            var h = new HashTable<String, Integer>(); h.put("AaAa", 1); h.put("AaBB", 2); h.put("BBBB", 3); h.put("BBAa", 4);
            String a = "all 4 stored (size=" + h.size() + ", collisionCount=" + h.collisionCount() + ")"; Integer rm = h.remove("BBBB");
            return a + ", remove(BBBB)=" + rm + ", remaining AaAa=" + h.get("AaAa") + " AaBB=" + h.get("AaBB") + " BBAa=" + h.get("BBAa") + ", BBBB=" + h.get("BBBB"); });
        check("T06", "Overwrite inside a collision chain", "put(BB,99) returns old 2, get(BB)=99, get(Aa)=1, size=2", () -> {
            var h = new HashTable<String, Integer>(); h.put("Aa", 1); h.put("BB", 2); Integer old = h.put("BB", 99);
            return "put(BB,99) returns old " + old + ", get(BB)=" + h.get("BB") + ", get(Aa)=" + h.get("Aa") + ", size=" + h.size(); });
        check("T06", "Resize keeps every chain intact (200 keys + colliding pair)", "capacity grew=true, 202/202 retrievable, size=202", () -> {
            var h = new HashTable<String, Integer>(); int cap0 = h.capacity(); for (int i = 0; i < 200; i++) h.put("k" + i, i); h.put("Aa", -1); h.put("BB", -2);
            int ok = 0; for (int i = 0; i < 200; i++) if (Integer.valueOf(i).equals(h.get("k" + i))) ok++; if (Integer.valueOf(-1).equals(h.get("Aa"))) ok++; if (Integer.valueOf(-2).equals(h.get("BB"))) ok++;
            return "capacity grew=" + (h.capacity() > cap0) + ", " + ok + "/202 retrievable, size=" + h.size(); });

        // ================= T07 BFS (Kahn topological order) =================
        check("T07", "Linear chain A->B->C->D", "[A, B, C, D]", () -> {
            var g = new ChoreGraph(); g.addDependency("A", "B"); g.addDependency("B", "C"); g.addDependency("C", "D"); return g.getTopologicalOrder().toString(); });
        check("T07", "Diamond: Shop->Cook, Shop->SetTable, Cook->Eat, SetTable->Eat, Eat->Wash", "5 of 5 chores, all 5 edges ordered, first=Shop, last=Wash", () -> {
            var g = new ChoreGraph(); String[][] e = {{"Shop", "Cook"}, {"Shop", "SetTable"}, {"Cook", "Eat"}, {"SetTable", "Eat"}, {"Eat", "Wash"}};
            for (String[] x : e) g.addDependency(x[0], x[1]); List<String> o = g.getTopologicalOrder(); int ok = 0; for (String[] x : e) if (o.indexOf(x[0]) >= 0 && o.indexOf(x[0]) < o.indexOf(x[1])) ok++;
            return o.size() + " of 5 chores, all " + ok + " edges ordered, first=" + o.get(0) + ", last=" + o.get(o.size() - 1); });
        check("T07", "Disconnected: X->Y and A->B plus isolated Z", "5 of 5 chores, X before Y=true, A before B=true, Z included=true", () -> {
            var g = new ChoreGraph(); g.addDependency("X", "Y"); g.addDependency("A", "B"); g.addChore("Z"); List<String> o = g.getTopologicalOrder();
            return o.size() + " of 5 chores, X before Y=" + (o.indexOf("X") < o.indexOf("Y")) + ", A before B=" + (o.indexOf("A") < o.indexOf("B")) + ", Z included=" + o.contains("Z"); });
        check("T07", "Multiple prerequisites: Pre1->T, Pre2->T, Pre3->T", "T is last, 4 of 4 chores, no duplicates", () -> {
            var g = new ChoreGraph(); g.addDependency("Pre1", "T"); g.addDependency("Pre2", "T"); g.addDependency("Pre3", "T"); List<String> o = g.getTopologicalOrder();
            return "T is " + (o.get(o.size() - 1).equals("T") ? "last" : "not last") + ", " + o.size() + " of 4 chores, " + (new HashSet<>(o).size() == o.size() ? "no duplicates" : "duplicates"); });
        check("T07", "Service: viewChoreOrder after addChoreDependency", "--- VALID CHORE ORDER --- ; - Buy soap ; - Wash clothes ; - Fold clothes", () -> {
            var s = new HouseholdService(); quiet(() -> { s.addChoreDependency("Buy soap", "Wash clothes"); s.addChoreDependency("Wash clothes", "Fold clothes"); }); return capture(s::viewChoreOrder); });

        // ================= T08 DFS (hasCycle) =================
        check("T08", "Acyclic chain A->B->C", "hasCycle=false", () -> { var g = new ChoreGraph(); g.addDependency("A", "B"); g.addDependency("B", "C"); return "hasCycle=" + g.hasCycle(); });
        check("T08", "Diamond (shared descendant, NOT a cycle)", "hasCycle=false", () -> {
            var g = new ChoreGraph(); g.addDependency("A", "B"); g.addDependency("A", "C"); g.addDependency("B", "D"); g.addDependency("C", "D"); return "hasCycle=" + g.hasCycle(); });
        check("T08", "Cycle A->B->C->A", "hasCycle=true, topologicalOrder=[] (no valid order)", () -> {
            var g = new ChoreGraph(); g.addDependency("A", "B"); g.addDependency("B", "C"); g.addDependency("C", "A");
            boolean c = g.hasCycle(); List<String>[] o = new List[1]; quiet(() -> o[0] = g.getTopologicalOrder()); return "hasCycle=" + c + ", topologicalOrder=" + o[0] + " (no valid order)"; });
        check("T08", "Self-loop A->A", "hasCycle=true", () -> { var g = new ChoreGraph(); g.addDependency("A", "A"); return "hasCycle=" + g.hasCycle(); });
        check("T08", "Disconnected: acyclic X->Y plus cyclic P->Q->P", "hasCycle=true (DFS reaches every component)", () -> {
            var g = new ChoreGraph(); g.addDependency("X", "Y"); g.addDependency("P", "Q"); g.addDependency("Q", "P"); return "hasCycle=" + g.hasCycle() + " (DFS reaches every component)"; });
        check("T08", "Long chain of 1,000 chores (recursion depth)", "hasCycle=false, no StackOverflowError", () -> {
            var g = new ChoreGraph(); for (int i = 0; i < 999; i++) g.addDependency("c" + i, "c" + (i + 1)); return "hasCycle=" + g.hasCycle() + ", no StackOverflowError"; });
        check("T08", "Dependency added later closes a cycle", "before: false ; after C->A added: true", () -> {
            var g = new ChoreGraph(); g.addDependency("A", "B"); g.addDependency("B", "C"); String b = "before: " + g.hasCycle(); g.addDependency("C", "A"); return b + " ; after C->A added: " + g.hasCycle(); });

        // ================= T09 BST deletion (sequential on one tree) =================
        t9 = bst(50, 30, 70, 20, 40, 60, 80, 35, 45);
        check("T09", "Initial tree (9 keys)", "20,30,35,40,45,50,60,70,80 ; valid BST=true", () -> keys(t9) + " ; valid BST=" + valid(t9, Long.MIN_VALUE, Long.MAX_VALUE));
        check("T09", "Delete leaf (20)", "keys=30,35,40,45,50,60,70,80 ; search(20)=false ; valid BST=true", () -> {
            t9 = BST_Methods.delete(t9, 20); return "keys=" + keys(t9) + " ; search(20)=" + BST_Methods.search(t9, 20) + " ; valid BST=" + valid(t9, Long.MIN_VALUE, Long.MAX_VALUE); });
        check("T09", "Delete node with one child (30)", "keys=35,40,45,50,60,70,80 ; search(30)=false ; valid BST=true", () -> {
            t9 = BST_Methods.delete(t9, 30); return "keys=" + keys(t9) + " ; search(30)=" + BST_Methods.search(t9, 30) + " ; valid BST=" + valid(t9, Long.MIN_VALUE, Long.MAX_VALUE); });
        check("T09", "Delete node with two children (70)", "keys=35,40,45,50,60,80 ; search(70)=false ; valid BST=true", () -> {
            t9 = BST_Methods.delete(t9, 70); return "keys=" + keys(t9) + " ; search(70)=" + BST_Methods.search(t9, 70) + " ; valid BST=" + valid(t9, Long.MIN_VALUE, Long.MAX_VALUE); });
        check("T09", "Delete root with two children (50)", "keys=35,40,45,60,80 ; search(50)=false ; new root=60 (in-order successor) ; valid BST=true", () -> {
            t9 = BST_Methods.delete(t9, 50); return "keys=" + keys(t9) + " ; search(50)=" + BST_Methods.search(t9, 50) + " ; new root=" + t9.data + " (in-order successor) ; valid BST=" + valid(t9, Long.MIN_VALUE, Long.MAX_VALUE); });
        check("T09", "Delete missing key (999)", "keys unchanged=35,40,45,60,80", () -> { t9 = BST_Methods.delete(t9, 999); return "keys unchanged=" + keys(t9); });
        check("T09", "Payload follows key after successor copy", "every remaining node's item matches its key=true", () -> "every remaining node's item matches its key=" + payloadOk(t9));
        check("T09", "Delete all remaining keys", "tree empty ; search(60)=false", () -> {
            for (int k : new int[]{60, 35, 80, 45, 40}) t9 = BST_Methods.delete(t9, k); return "tree " + (t9 == null ? "empty" : "not empty: " + keys(t9)) + " ; search(60)=" + BST_Methods.search(t9, 60); });

        // ================= T10 Heap extraction =================
        check("T10", "MinHeap<Integer> order (incl. duplicate 2)", "1,2,2,3,5,7,8,9 ; size after=0", () -> {
            var h = new MinHeap<Integer>(Integer::compare); for (int v : new int[]{5, 3, 8, 1, 9, 2, 7, 2}) h.insert(v);
            StringBuilder sb = new StringBuilder(); while (!h.isEmpty()) { if (sb.length() > 0) sb.append(","); sb.append(h.extractMin()); } return sb + " ; size after=" + h.size(); });
        check("T10", "peek does not remove", "peek=1, peek=1, size=3, extractMin=1, next peek=2, size=2", () -> {
            var h = new MinHeap<Integer>(Integer::compare); h.insert(3); h.insert(1); h.insert(2);
            return "peek=" + h.peek() + ", peek=" + h.peek() + ", size=" + h.size() + ", extractMin=" + h.extractMin() + ", next peek=" + h.peek() + ", size=" + h.size(); });
        check("T10", "Extract after draining", "extractMin=null, size=0", () -> {
            var h = new MinHeap<Integer>(Integer::compare); h.insert(1); h.extractMin(); return "extractMin=" + h.extractMin() + ", size=" + h.size(); });
        check("T10", "MinHeap<Task> by deadline (tie at 20)", "deadlines 10,20,20,30 ; first=Dishes, last=Laundry", () -> {
            var h = new MinHeap<Task>(Comparator.comparingInt(Task::getDeadline)); h.insert(new Task("1", "Laundry", 30)); h.insert(new Task("2", "Dishes", 10)); h.insert(new Task("3", "Cook", 20)); h.insert(new Task("4", "Sweep", 20));
            List<Task> o = new ArrayList<>(); while (!h.isEmpty()) o.add(h.extractMin());
            return "deadlines " + String.join(",", o.stream().map(t -> String.valueOf(t.getDeadline())).toList()) + " ; first=" + o.get(0).getTitle() + ", last=" + o.get(o.size() - 1).getTitle(); });
        check("T10", "100 random ints (forces resize past capacity 16)", "100 extracted, ascending=true, equals sorted copy=true", () -> {
            Random r = new Random(7); Integer[] v = new Integer[100]; var h = new MinHeap<Integer>(Integer::compare); for (int i = 0; i < 100; i++) { v[i] = r.nextInt(1000); h.insert(v[i]); }
            Integer[] s = v.clone(); Arrays.sort(s); List<Integer> o = new ArrayList<>(); while (!h.isEmpty()) o.add(h.extractMin()); boolean asc = true; for (int i = 1; i < o.size(); i++) if (o.get(i) < o.get(i - 1)) asc = false;
            return o.size() + " extracted, ascending=" + asc + ", equals sorted copy=" + o.equals(Arrays.asList(s)); });
        check("T10", "Service: completes most urgent chore first", "Next priority chore: Dishes (deadline: 10) ; Completed: Dishes ; Completed: Cook ; Completed: Laundry ; No chores left to complete.", () -> {
            var s = new HouseholdService(); quiet(() -> { s.addChore("C1", "Laundry", 30, null); s.addChore("C2", "Dishes", 10, null); s.addChore("C3", "Cook", 20, null); });
            return capture(s::viewNextPriorityChore) + " ; " + capture(s::completeNextChore) + " ; " + capture(s::completeNextChore) + " ; " + capture(s::completeNextChore) + " ; " + capture(s::completeNextChore); });

        // ================= EXTRA FINDINGS (outside the 10 requested rows) =================
        check("F01", "Pending queue after completing the most urgent chore", "1. Laundry (deadline: 30) ; 2. Cook (deadline: 20)", () -> {
            var s = new HouseholdService(); quiet(() -> { s.addChore("C1", "Laundry", 30, null); s.addChore("C2", "Dishes", 10, null); s.addChore("C3", "Cook", 20, null); s.completeNextChore(); }); return capture(s::viewPendingQueue).replace("--- PENDING CHORES --- ; ", ""); });
        check("F02", "Two chores with the same deadline in range search", "Dishes (deadline: 10) ; Trash (deadline: 10)", () -> {
            var s = new HouseholdService(); quiet(() -> { s.addChore("C1", "Dishes", 10, null); s.addChore("C2", "Trash", 10, null); }); return capture(() -> s.searchChoresByDeadlineRange(0, 20)).replace("Chores due between 0 and 20: ; ", ""); });
        check("F03", "Two expenses with the same whole-peso amount in range search", "2 results for PHP 100.25 and PHP 100.75", () -> {
            var s = new HouseholdService(); quiet(() -> { s.addExpense("E1", "Food", 100.25); s.addExpense("E2", "Utilities", 100.75); });
            String o = capture(() -> s.searchExpensesByAmountRange(0, 200)); int n = o.split(" ; ").length - 1; return n + " results for PHP 100.25 and PHP 100.75"; });
        check("F04", "Expense range output wording", "no 'deadline' label on expense results", () -> {
            var s = new HouseholdService(); quiet(() -> s.addExpense("E1", "Food", 150)); String o = capture(() -> s.searchExpensesByAmountRange(0, 200));
            return o.contains("deadline") ? "label 'deadline' appears: " + o.replace("Expenses between PHP 0 and PHP 200: ; ", "") : "no 'deadline' label on expense results"; });

        for (String[] r : out) System.out.println(String.join("\t", r));
    }
}

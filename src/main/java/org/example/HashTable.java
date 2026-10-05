import java.util.ArrayList;
import java.util.List;

public class HashTable<K, V> {

    private static class Node<K, V> {
        final K key;
        V value;
        Node<K, V> next;

        Node(K key, V value, Node<K, V> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }

    private static final int INITIAL_CAPACITY = 16;
    private static final double LOAD_FACTOR = 0.75;

    private Node<K, V>[] buckets;
    private int size;

    @SuppressWarnings("unchecked")
    public HashTable() {
        buckets = (Node<K, V>[]) new Node[INITIAL_CAPACITY];
    }

    private int indexFor(K key, int capacity) {
        return (key.hashCode() & 0x7fffffff) % capacity;
    }

    private Node<K, V> findNode(K key) {
        if (key == null) return null;
        for (Node<K, V> n = buckets[indexFor(key, buckets.length)]; n != null; n = n.next) {
            if (n.key.equals(key)) return n;
        }
        return null;
    }

    // Insert or update. Returns the old value, or null if the key was new.
    public V put(K key, V value) {
        if (key == null) throw new IllegalArgumentException("Key cannot be null");
        Node<K, V> existing = findNode(key);
        if (existing != null) {
            V old = existing.value;
            existing.value = value;
            return old;
        }
        int idx = indexFor(key, buckets.length);
        buckets[idx] = new Node<>(key, value, buckets[idx]);
        size++;
        if ((double) size / buckets.length > LOAD_FACTOR) resize();
        return null;
    }

    public V get(K key) {
        Node<K, V> n = findNode(key);
        return n == null ? null : n.value;
    }

    public boolean containsKey(K key) {
        return findNode(key) != null;
    }

    public V remove(K key) {
        if (key == null) return null;
        int idx = indexFor(key, buckets.length);
        Node<K, V> prev = null;
        for (Node<K, V> n = buckets[idx]; n != null; prev = n, n = n.next) {
            if (n.key.equals(key)) {
                if (prev == null) buckets[idx] = n.next;
                else prev.next = n.next;
                size--;
                return n.value;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        Node<K, V>[] old = buckets;
        buckets = (Node<K, V>[]) new Node[old.length * 2];
        for (Node<K, V> head : old) {
            for (Node<K, V> n = head; n != null; n = n.next) {
                int idx = indexFor(n.key, buckets.length);
                buckets[idx] = new Node<>(n.key, n.value, buckets[idx]);
            }
        }
    }

    public List<K> keys() {
        List<K> list = new ArrayList<>();
        for (Node<K, V> head : buckets)
            for (Node<K, V> n = head; n != null; n = n.next) list.add(n.key);
        return list;
    }

    public List<V> values() {
        List<V> list = new ArrayList<>();
        for (Node<K, V> head : buckets)
            for (Node<K, V> n = head; n != null; n = n.next) list.add(n.value);
        return list;
    }

    public int size() { return size; }

    public boolean isEmpty() { return size == 0; }
}
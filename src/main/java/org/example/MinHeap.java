package org.example;
import java.util.Comparator;

public class MinHeap<T> {

    private Object[] heap;
    private int size;
    private final Comparator<T> comparator;

    public MinHeap(Comparator<T> comparator) {
        this(16, comparator);
    }

    public MinHeap(int capacity, Comparator<T> comparator) {
        this.heap = new Object[capacity];
        this.size = 0;
        this.comparator = comparator;
    }

    public void insert(T item) {
        if (size == heap.length) {
            resize();
        }
        heap[size] = item;
        siftUp(size);
        size++;
    }

    @SuppressWarnings("unchecked")
    public T extractMin() {
        if (size == 0) return null;

        T min = (T) heap[0];
        size--;
        heap[0] = heap[size];
        heap[size] = null;
        if (size > 0) {
            siftDown(0);
        }
        return min;
    }

    @SuppressWarnings("unchecked")
    public T peek() {
        if (size == 0) return null;
        return (T) heap[0];
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    // ---- Internal helpers ----

    @SuppressWarnings("unchecked")
    private void siftUp(int i) {
        while (i > 0) {
            int parent = (i - 1) / 2;
            if (comparator.compare((T) heap[i], (T) heap[parent]) < 0) {
                swap(i, parent);
                i = parent;
            } else {
                break;
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void siftDown(int i) {
        while (true) {
            int left = 2 * i + 1;
            int right = 2 * i + 2;
            int smallest = i;

            if (left < size && comparator.compare((T) heap[left], (T) heap[smallest]) < 0) {
                smallest = left;
            }
            if (right < size && comparator.compare((T) heap[right], (T) heap[smallest]) < 0) {
                smallest = right;
            }
            if (smallest == i) break;

            swap(i, smallest);
            i = smallest;
        }
    }

    private void swap(int i, int j) {
        Object temp = heap[i];
        heap[i] = heap[j];
        heap[j] = temp;
    }

    private void resize() {
        Object[] bigger = new Object[heap.length * 2];
        for (int i = 0; i < heap.length; i++) {
            bigger[i] = heap[i];
        }
        heap = bigger;
    }

}

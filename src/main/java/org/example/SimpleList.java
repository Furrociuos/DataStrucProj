package org.example;

public class SimpleList<T> {
    private Object[] data = new Object[10];
    private int size;

    public void add(T item) {
        if (size == data.length) {
            Object[] bigger = new Object[data.length * 2];
            for (int i = 0; i < size; i++) bigger[i] = data[i];
            data = bigger;
        }
        data[size++] = item;
    }

    @SuppressWarnings("unchecked")
    public T get(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException();
        return (T) data[index];
    }

    public boolean remove(T item) {
        for (int i = 0; i < size; i++) {
            if (data[i].equals(item)) {
                for (int j = i; j < size - 1; j++) data[j] = data[j + 1];
                data[--size] = null;
                return true;
            }
        }
        return false;
    }

    public int size() { return size; }
    public boolean isEmpty() { return size == 0; }
}
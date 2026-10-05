package org.example;

public class CustomLinkedList<T> {

    private static class Node<T> {
        T data;
        Node<T> next;

        Node(T data) {
            this.data = data;
            this.next = null;
        }
    }

    private Node<T> head;
    private Node<T> tail;
    private int size;

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

        public void insertAtTail(T data) {
            Node<T> newNode = new Node<>(data);

            if (head == null) {
                head = newNode;
                tail = newNode;
            } else {
                tail.next = newNode;
                tail = newNode;
            }

            size++;
        }

    public boolean search(T target) {
        Node<T> current = head;

        while (current != null) {
            if (current.data == null && target == null) {
                return true;            }

            if (current.data != null && current.data.equals(target)) {
                return true;
            }

            current = current.next;
        }

        return false;
    }

    public void traverse() {
        Node<T> current = head;

        while (current != null) {
            System.out.println(current.data);
            current = current.next;
        }
    }

    public boolean delete(T target) {
        if (head == null) {
            return false;
        }

        if ((head.data == null && target == null) ||
                (head.data != null && head.data.equals(target))) {

            head = head.next;
            size--;

            if (head == null) {
                tail = null;
            }

            return true;
        }

        Node<T> current = head;

        while (current.next != null) {
            if ((current.next.data == null && target == null) ||
                    (current.next.data != null && current.next.data.equals(target))) {

                if (current.next == tail) {
                    tail = current;
                }

                current.next = current.next.next;
                size--;
                return true;
            }

            current = current.next;
        }

        return false;
    }
    }

package org.example;

public class TaskQueue {

    private static class Node {
        Task data;
        Node next;
        Node(Task data) { this.data = data; }
    }

    private Node head;
    private Node tail;
    private int size;

    public void enqueue(Task task) {
        Node newNode = new Node(task);
        if (head == null) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            tail = newNode;
        }
        size++;
    }

    public Task dequeue() {
        if (head == null) return null;
        Task data = head.data;
        head = head.next;
        if (head == null) tail = null;
        size--;
        return data;
    }

    public Task peek() {
        return head == null ? null : head.data;
    }

    public boolean isEmpty() {
        return head == null;
    }

    public int size() {
        return size;
    }

    public void display() {
        if (head == null) {
            System.out.println("No pending chores.");
            return;
        }
        Node current = head;
        int position = 1;
        while (current != null) {
            System.out.println(position + ". " + current.data.getTitle()
                    + " (deadline: " + current.data.getDeadline() + ")");
            current = current.next;
            position++;
        }
    }
}

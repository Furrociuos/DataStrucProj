package org.example;

public class LinkedListTest {
    public static void main(String[] args) {

        CustomLinkedList<String> history = new CustomLinkedList<>();

        System.out.println("Empty at start: " + history.isEmpty());

        history.insertAtTail("Electricity Bill");
        history.insertAtTail("Laundry");
        history.insertAtTail("Water Bill");

        System.out.println("Size after insertions: " + history.size());

        System.out.println("Search Laundry: " + history.search("Laundry"));
        System.out.println("Search Internet Bill: " + history.search("Internet Bill"));

        System.out.println("\nHistory before deletion:");
        history.traverse();

        System.out.println("\nDelete Laundry: " + history.delete("Laundry"));

        System.out.println("\nHistory after deletion:");
        history.traverse();

        System.out.println("\nFinal size: " + history.size());
    }
}
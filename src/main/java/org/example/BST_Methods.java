package org.example;

public class BST_Methods {
    public static class Node {
        public int data;
        public String item; // This is the expense or chore (e.g. Electricity Bill, Laundry)
        public Node left, right;
        
        Node(int data, String item) {
            this.data = data;
            this.item = item;
            left = right = null;
        }
    }
    
    // These sections of code is based from these sources: 
    // Search: https://www.geeksforgeeks.org/dsa/binary-search-tree-set-1-search-and-insertion/
    // Insert: https://www.geeksforgeeks.org/dsa/insertion-in-binary-search-tree/
    // Delete: https://www.geeksforgeeks.org/dsa/deletion-in-binary-search-tree/
    
    static boolean search(Node root, int key) {
        // Root is null; return false
        if (root == null ) { return false; }    
        // If root has key, return true
        if (root.data == key) { return true; }
        if (key > root.data) { return search(root.right, key); }
        return search(root.left, key);
    }
    
    static Node insert(Node root, int key, String item) {
        // Insert the new node at an empty position
        if (root == null) { return new Node(key, item); }
        // If key is already present, do not insert it again
        if (key == root.data) { return root; }
        // Insert into the left subtree
        if (key < root.data) { root.left = insert(root.left, key, item); }
        // Insert into the right subtree
        else { root.right = insert(root.right, key, item); }
        return root;
    }
    
    // This method, inorderSucessor, is only activated if the node has two children
    public static Node inorderSuccessor(Node current) {
        current = current.right;
        while (current != null && current.left != null) {
            current = current.left;
        }
        return current;
    }
    
    // Delete a node with value key from BST
    static Node delete(Node root, int key) {
        if (root == null) { return root; }

        if (root.data > key) { root.left = delete(root.left, key); } 
        else if (root.data < key) { root.right = delete(root.right, key); } 
        else {
            // Node with 0 or 1 child
            if (root.left == null) return root.right;
            if (root.right == null) return root.left;

            // Node with 2 children
            Node inorderSuccessor = inorderSuccessor(root);
            root.data = inorderSuccessor.data;
            root.item = inorderSuccessor.item;
            root.right = delete(root.right, inorderSuccessor.data);
        }
        return root;
    }
    
    static void inOrder(Node node) {
        if (node != null) {
            inOrder(node.left);
            System.out.println(node.item);
            inOrder(node.right);
        }
    }

    static void rangeSearch(Node node, int low, int high) {
        if (node == null) return;
        if (node.data > low) rangeSearch(node.left, low, high);
        if (node.data >= low && node.data <= high) {
            System.out.println(node.item + " (deadline: " + node.data + ")");
        }
        if (node.data < high) rangeSearch(node.right, low, high);
    }
}

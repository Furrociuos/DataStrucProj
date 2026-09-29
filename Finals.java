package com.mycompany.finals;

public class Finals {
    // Binary Search Tree
    static class Node {
        int data;
        Node left, right;
        
        Node(int data) {
            this.data = data;
            left = right = null;
        }
    }
    
    //These sections of code is based from these sources: 
    //Search: https://www.geeksforgeeks.org/dsa/binary-search-tree-set-1-search-and-insertion/
    //Insert: https://www.geeksforgeeks.org/dsa/insertion-in-binary-search-tree/
    //Delete: https://www.geeksforgeeks.org/dsa/deletion-in-binary-search-tree/
    
    static boolean search(Node root, int key) {
        // Root is null; return false
        if (root == null ) { return false; }    
        // If root has key, return true
        if (root.data == key) { return true; }
        if (key > root.data) { return search(root.right, key); }
        return search(root.left, key);
    }
    
    static Node insert(Node root, int key) {
        // Insert the new node at an empty position
        if (root == null) { return new Node(key); }
        // If key is already present, do not insert it again
        if (key == root.data) { return root; }
        // Insert into the left subtree
        if (key < root.data) { root.left = insert(root.left, key); }
        // Insert into the right subtree
        else { root.right = insert(root.right, key); }
        return root;
    }
    
    // Get inorder successor (smallest in right subtree)
    // Note: This method is used for deletion if the node has two children
    // Note 2: This gets the next greater value than the target node
    static Node getSuccessor(Node current) {
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
            Node successor = getSuccessor(root);
            root.data = successor.data;
            root.right = delete(root.right, successor.data);
        }
        return root;
    }
    
    static void inOrder(Node node) {
        if (node != null) {
            inOrder(node.left);
            System.out.println(node.data);
            inOrder(node.right);
        }
    }
}

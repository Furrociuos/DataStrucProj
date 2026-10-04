package org.example;

public class BST_Expenses {
    // Calling the node in BST_Methods.Node instead of creating a new Node class
    private BST_Methods.Node root;

    public boolean searchExpense(int key) { return BST_Methods.search(root, key); }
    public void insertExpense(int key, String expense) { root = BST_Methods.insert(root, key, expense); }
    public void deleteExpense(int key) { root = BST_Methods.delete(root, key); }
    public void displayExpenses() { BST_Methods.inOrder(root); }
}

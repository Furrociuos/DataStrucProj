package org.example;

public class BST_Expenses {
    // Calling the node in BST_Methods.Node instead of creating a new Node class
    private BST_Methods.Node root;

    public boolean searchExpense(int dueDate) { return BST_Methods.search(root, dueDate); }
    public void insertExpense(int dueDate, String expense) { root = BST_Methods.insert(root, dueDate, expense); }
    public void deleteExpense(int dueDate) { root = BST_Methods.delete(root, dueDate); }
    public void displayExpenses() { BST_Methods.inOrder(root); }
}

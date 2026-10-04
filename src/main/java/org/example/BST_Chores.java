package org.example;

public class BST_Chores {
    private BST_Methods.Node root;

    public boolean searchChore(int dueDate) { return BST_Methods.search(root, dueDate); }
    public void insertChore(int dueDate, String chore) { root = BST_Methods.insert(root, dueDate, chore); }
    public void deleteChore(int dueDate) { root = BST_Methods.delete(root, dueDate); }
    public void displayChores() { BST_Methods.inOrder(root); }
}

package org.example;

public class BST_Chores {
    private BST_Methods.Node root;

    public boolean searchChore(int deadline) { return BST_Methods.search(root, deadline); }
    public void insertChore(int deadline, String chore) { root = BST_Methods.insert(root, deadline, chore); }
    public void deleteChore(int deadline) { root = BST_Methods.delete(root, deadline); }
    public void displayChores() { BST_Methods.inOrder(root); }
    public void searchRange(int low, int high) { BST_Methods.rangeSearch(root, low, high); }
}

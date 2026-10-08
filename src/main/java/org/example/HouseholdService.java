package org.example;

import java.util.Comparator;
import java.util.List;

public class HouseholdService {

    private final HouseholdHashTables tables = new HouseholdHashTables();
    private final HouseholdArray arrayStats = new HouseholdArray();
    private final CustomLinkedList<String> choreHistory = new CustomLinkedList<>();
    private final CustomLinkedList<String> expenseHistory = new CustomLinkedList<>();
    private final TaskQueue pendingChores = new TaskQueue();
    private final MinHeap<Task> choreHeap =
            new MinHeap<>(Comparator.comparingInt(Task::getDeadline));
    private final BST_Chores choreBST = new BST_Chores();
    private final BST_Expenses expenseBST = new BST_Expenses();
    private final ChoreGraph choreGraph = new ChoreGraph();

    // ---- 1. Add family member ----
    public void addMember(String id, String name) {
        Member m = new Member(id, name);
        if (tables.addMember(m)) {
            System.out.println("Added member: " + name);
        } else {
            System.out.println("Could not add member (duplicate or invalid ID).");
        }
    }

    // ---- 2. Add chore ----
    public void addChore(String id, String title, int deadline, String memberID) {
        Task task = new Task(id, title, deadline);
        if (!tables.addTask(task)) {
            System.out.println("Could not add chore (duplicate or invalid ID).");
            return;
        }
        if (memberID != null && !memberID.isBlank()) {
            tables.assignTask(id, memberID);
        }
        choreHeap.insert(task);
        choreBST.insertChore(deadline, title);
        choreGraph.addChore(title);
        pendingChores.enqueue(task);
        System.out.println("Added chore: " + title);
    }

    // ---- 3. Add expense ----
    public void addExpense(String id, String category, double amount) {
        Expense expense = new Expense(id, category, amount);
        if (!tables.addExpense(expense)) {
            System.out.println("Could not add expense (duplicate or invalid ID).");
            return;
        }
        arrayStats.addExpense(category, amount);
        expenseHistory.insertAtTail(id + ": " + category + " - PHP " + amount);
        expenseBST.insertExpense((int) amount, category + " (PHP " + amount + ")"); // NEW
    }

    // ---- 4. Complete a chore (pulls the most urgent one off the heap) ----
    public void completeNextChore() {
        Task completed = choreHeap.extractMin();
        if (completed == null) {
            System.out.println("No chores left to complete.");
            return;
        }
        choreHistory.insertAtTail(completed.getTitle() + " (completed)");
        pendingChores.dequeue(); // keep the pending queue roughly in sync
        System.out.println("Completed: " + completed.getTitle());
    }

    // ---- 5. View next priority chore ----
    public void viewNextPriorityChore() {
        Task next = choreHeap.peek();
        if (next == null) {
            System.out.println("No chores pending.");
        } else {
            System.out.println("Next priority chore: " + next.getTitle()
                    + " (deadline: " + next.getDeadline() + ")");
        }
    }

    // ---- 6. Search chores by deadline range ----
    public void searchChoresByDeadlineRange(int low, int high) {
        System.out.println("Chores due between " + low + " and " + high + ":");
        choreBST.searchRange(low, high);
    }

    // ---- 7. Search expenses by amount range ----
    public void searchExpensesByAmountRange(int low, int high) {
        System.out.println("Expenses between PHP " + low + " and PHP " + high + ":");
        expenseBST.searchRange(low, high);
    }

    // ---- 8. View chore history ----
    public void viewChoreHistory() {
        System.out.println("--- CHORE HISTORY ---");
        choreHistory.traverse();
    }

    // ---- 9. View pending chore queue ----
    public void viewPendingQueue() {
        System.out.println("--- PENDING CHORES ---");
        pendingChores.display();
    }

    // ---- 10. View chore order (topological sort) ----
    public void viewChoreOrder() {
        List<String> order = choreGraph.getTopologicalOrder();
        System.out.println("--- VALID CHORE ORDER ---");
        for (String chore : order) {
            System.out.println("- " + chore);
        }
    }

    // ---- 11. View expenses by category ----
    public void viewExpensesByCategory() {
        arrayStats.displayExpenseCategories();
        arrayStats.displayCategoryTotals();
        arrayStats.displayHighestSpendingCategory();
        arrayStats.displayOverallExpenses();

        System.out.println("\n--- INDIVIDUAL EXPENSES BY CATEGORY ---");
        for (String category : arrayStats.getCategories()) {
            SimpleList<Expense> expenses = tables.getExpensesByCategory(category);
            if (expenses.isEmpty()) continue;

            System.out.println(category + ":");
            for (int i = 0; i < expenses.size(); i++) {
                Expense e = expenses.get(i);
                System.out.println("  - " + e.getExpenseID() + ": PHP " + e.getAmount());
            }
        }
    }

    // ---- 12. Look up a member's chores ----
    public void lookupMemberChores(String memberID) {
        SimpleList<Task> tasks = tables.getTasksOfMember(memberID);
        if (tasks.isEmpty()) {
            System.out.println("No chores found for this member.");
            return;
        }
        for (int i = 0; i < tasks.size(); i++) {
            Task t = tasks.get(i);
            System.out.println("- " + t.getTitle() + " (deadline: " + t.getDeadline() + ")");
        }
    }

    // ---- 13. Add chore dependency ----
    public void addChoreDependency(String prerequisite, String dependent) {
        choreGraph.addDependency(prerequisite, dependent);
    }
}

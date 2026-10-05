import java.util.ArrayList;
import java.util.List;

public class HouseholdHashTables {

    // memberID -> Member
    private final HashTable<String, Member> members = new HashTable<>();
    // taskID -> Task, expenseID -> Expense
    private final HashTable<String, Task> tasks = new HashTable<>();
    private final HashTable<String, Expense> expenses = new HashTable<>();
    // category -> expenses in that category
    private final HashTable<String, List<Expense>> expensesByCategory = new HashTable<>();

    // Members
    public void addMember(Member m) { members.put(m.getMemberID(), m); }
    public Member getMember(String memberID) { return members.get(memberID); }

    public boolean assignTask(String taskID, String memberID) {
        Task t = tasks.get(taskID);
        Member m = members.get(memberID);
        if (t == null || m == null) return false;
        t.setAssignedMemberID(memberID);
        m.getAssignedTaskIDs().add(taskID);
        return true;
    }

    public List<Task> getTasksOfMember(String memberID) {
        List<Task> result = new ArrayList<>();
        Member m = members.get(memberID);
        if (m == null) return result;
        for (String id : m.getAssignedTaskIDs()) {
            Task t = tasks.get(id);
            if (t != null) result.add(t);
        }
        return result;
    }

    // Tasks
    public void addTask(Task t) { tasks.put(t.getTaskID(), t); }
    public Task getTask(String taskID) { return tasks.get(taskID); }

    // Expenses
    public void addExpense(Expense e) {
        expenses.put(e.getExpenseID(), e);
        List<Expense> group = expensesByCategory.get(e.getCategory());
        if (group == null) {
            group = new ArrayList<>();
            expensesByCategory.put(e.getCategory(), group);
        }
        group.add(e);
    }

    public Expense getExpense(String expenseID) { return expenses.get(expenseID); }

    public List<Expense> getExpensesByCategory(String category) {
        List<Expense> group = expensesByCategory.get(category);
        return group == null ? new ArrayList<>() : group;
    }
}
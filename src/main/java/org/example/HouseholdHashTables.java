public class HouseholdHashTables {

    private final HashTable<String, Member> members = new HashTable<>();
    private final HashTable<String, Task> tasks = new HashTable<>();
    private final HashTable<String, Expense> expenses = new HashTable<>();
    private final HashTable<String, SimpleList<Expense>> expensesByCategory = new HashTable<>();

    private boolean validId(String id) { return id != null && !id.trim().isEmpty(); }

    // Members
    public boolean addMember(Member m) {
        if (m == null || !validId(m.getMemberID()) || members.containsKey(m.getMemberID())) return false;
        members.put(m.getMemberID(), m);
        return true;
    }

    public Member getMember(String memberID) { return members.get(memberID); }

    public Member removeMember(String memberID) {
        Member m = members.remove(memberID);
        if (m != null) {
            SimpleList<String> ids = m.getAssignedTaskIDs();
            for (int i = 0; i < ids.size(); i++) {
                Task t = tasks.get(ids.get(i));
                if (t != null) t.setAssignedMemberID(null);
            }
        }
        return m;
    }

    // Tasks
    public boolean addTask(Task t) {
        if (t == null || !validId(t.getTaskID()) || tasks.containsKey(t.getTaskID())) return false;
        tasks.put(t.getTaskID(), t);
        return true;
    }

    public Task getTask(String taskID) { return tasks.get(taskID); }

    public Task removeTask(String taskID) {
        Task t = tasks.remove(taskID);
        if (t != null && t.getAssignedMemberID() != null) {
            Member m = members.get(t.getAssignedMemberID());
            if (m != null) m.getAssignedTaskIDs().remove(taskID);
        }
        return t;
    }

    public boolean assignTask(String taskID, String memberID) {
        Task t = tasks.get(taskID);
        Member m = members.get(memberID);
        if (t == null || m == null) return false;
        if (t.getAssignedMemberID() != null) {
            Member prev = members.get(t.getAssignedMemberID());
            if (prev != null) prev.getAssignedTaskIDs().remove(taskID);
        }
        t.setAssignedMemberID(memberID);
        m.getAssignedTaskIDs().add(taskID);
        return true;
    }

    public SimpleList<Task> getTasksOfMember(String memberID) {
        SimpleList<Task> result = new SimpleList<>();
        Member m = members.get(memberID);
        if (m == null) return result;
        SimpleList<String> ids = m.getAssignedTaskIDs();
        for (int i = 0; i < ids.size(); i++) {
            Task t = tasks.get(ids.get(i));
            if (t != null) result.add(t);
        }
        return result;
    }

    // Expenses
    public boolean addExpense(Expense e) {
        if (e == null || !validId(e.getExpenseID()) || expenses.containsKey(e.getExpenseID())) return false;
        expenses.put(e.getExpenseID(), e);
        SimpleList<Expense> group = expensesByCategory.get(e.getCategory());
        if (group == null) {
            group = new SimpleList<>();
            expensesByCategory.put(e.getCategory(), group);
        }
        group.add(e);
        return true;
    }

    public Expense getExpense(String expenseID) { return expenses.get(expenseID); }

    public Expense removeExpense(String expenseID) {
        Expense e = expenses.remove(expenseID);
        if (e != null) {
            SimpleList<Expense> group = expensesByCategory.get(e.getCategory());
            if (group != null) group.remove(e);
        }
        return e;
    }

    public SimpleList<Expense> getExpensesByCategory(String category) {
        SimpleList<Expense> group = expensesByCategory.get(category);
        return group == null ? new SimpleList<>() : group;
    }

    // ----- Stats -----
    public void printStats() {
        System.out.println("members: " + members.size() + " items, " + members.capacity()
                + " buckets, " + members.collisionCount() + " collisions");
        System.out.println("tasks: " + tasks.size() + " items, " + tasks.capacity()
                + " buckets, " + tasks.collisionCount() + " collisions");
        System.out.println("expenses: " + expenses.size() + " items, " + expenses.capacity()
                + " buckets, " + expenses.collisionCount() + " collisions");
    }
}
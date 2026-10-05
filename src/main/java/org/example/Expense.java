public class Expense {
    private final String expenseID;
    private final String category;
    private final double amount;

    public Expense(String expenseID, String category, double amount) {
        this.expenseID = expenseID;
        this.category = category;
        this.amount = amount;
    }

    public String getExpenseID() { return expenseID; }
    public String getCategory() { return category; }
    public double getAmount() { return amount; }
}
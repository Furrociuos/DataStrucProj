package org.example;

public class HouseholdArray {

    private String[] familyMembers = {
            "Father",
            "Mother",
            "Son",
            "Daughter"
    };

    private String[] expenseCategories = {
            "Food",
            "Utilities",
            "Transportation",
            "School",
            "Maintenance",
            "Other"
    };

    private double[] categoryTotals = {
            0, 0, 0, 0, 0, 0
    };

    public void displayFamilyMembers() {

        System.out.println("--- FAMILY MEMBERS ---");

        for (int i = 0; i < familyMembers.length; i++) {
            System.out.println((i + 1) + ". " + familyMembers[i]);
        }
    }

    public void displayExpenseCategories() {

        System.out.println("\n--- EXPENSE CATEGORIES ---");

        for (int i = 0; i < expenseCategories.length; i++) {
            System.out.println((i + 1) + ". " + expenseCategories[i]);
        }
    }

    public void addExpense(String category, double amount) {

        if (amount <= 0) {
            System.out.println("Invalid expense amount.");
            return;
        }

        for (int i = 0; i < expenseCategories.length; i++) {

            if (expenseCategories[i].equalsIgnoreCase(category)) {

                categoryTotals[i] += amount;

                System.out.println(
                        "PHP " + amount +
                                " added to " + expenseCategories[i]
                );

                return;
            }
        }

        System.out.println("Expense category not found.");
    }

    public void displayCategoryTotals() {

        System.out.println("\n--- EXPENSE CATEGORY TOTALS ---");

        for (int i = 0; i < expenseCategories.length; i++) {

            System.out.printf(
                    "%-15s PHP %.2f%n",
                    expenseCategories[i],
                    categoryTotals[i]
            );
        }
    }

    public void displayHighestSpendingCategory() {

        int highestIndex = 0;

        for (int i = 1; i < categoryTotals.length; i++) {

            if (categoryTotals[i] > categoryTotals[highestIndex]) {
                highestIndex = i;
            }
        }

        System.out.printf(
                "%nHighest Spending Category: %s - PHP %.2f%n",
                expenseCategories[highestIndex],
                categoryTotals[highestIndex]
        );
    }

    public void displayOverallExpenses() {

        double total = 0;

        for (int i = 0; i < categoryTotals.length; i++) {
            total += categoryTotals[i];
        }

        System.out.printf(
                "Overall Household Expenses: PHP %.2f%n",
                total
        );
    }
}
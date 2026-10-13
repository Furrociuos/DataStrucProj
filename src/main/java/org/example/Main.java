package org.example;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    static void main() {
        Scanner in = new Scanner(System.in);
        HouseholdService service = new HouseholdService();
        boolean loop = true;
        String errMsg = "";

        while (loop) {
           clearConsole();
           System.out.print(errMsg + """
                   === Household Manager ===
                   1.\tAdd family member
                   2.\tAdd chore
                   3.\tAdd expense
                   4.\tComplete a chore
                   5.\tView next priority chore
                   6.\tSearch chores by deadline range
                   7.\tSearch expenses by amount range
                   8.\tView chore history
                   9.\tView pending chore queue
                   10.\tView chore order
                   11.\tView expenses by category
                   12.\tLook up member's chore
                   13.\tAdd chore dependency
                   0.\tExit
                   Choose an option:
                   """);
           errMsg = "";
           String usrIn = in.nextLine();

           switch (usrIn) {
               case "1": {
                   System.out.print("Member ID: ");
                   String id = in.nextLine();
                   System.out.print("Name: ");
                   String name = in.nextLine();
                   service.addMember(id, name);
                   pause(in);
                   break;
               }
               case "2": {
                   System.out.print("Task ID: ");
                   String id = in.nextLine();
                   System.out.print("Title: ");
                   String title = in.nextLine();
                   System.out.print("Due date (yyyy-MM-dd, earlier = more urgent): ");
                   LocalDate dueDate;
                   try {
                       dueDate = LocalDate.parse(in.nextLine().trim());
                   } catch (DateTimeParseException e) {
                       errMsg = "Invalid date. Use yyyy-MM-dd (e.g. 2026-10-20).\n";
                       break;
                   }
                   System.out.print("Assign to member ID (blank for none): ");
                   String memberID = in.nextLine();
                   service.addChore(id, title, dueDate, memberID);
                   pause(in);
                   break;
               }
               case "3": {
                   System.out.print("Expense ID: ");
                   String id = in.nextLine();
                   System.out.print("Category: ");
                   String category = in.nextLine();
                   System.out.print("Amount: ");
                   double amount = Double.parseDouble(in.nextLine());
                   service.addExpense(id, category, amount);
                   pause(in);
                   break;
               }
               case "4": {
                   service.completeNextChore();
                   pause(in);
                   break;
               }
               case "5": {
                   service.viewNextPriorityChore();
                   pause(in);
                   break;
               }
               case "6": {
                   LocalDate from, to;
                   try {
                       System.out.print("From date (yyyy-MM-dd): ");
                       from = LocalDate.parse(in.nextLine().trim());
                       System.out.print("To date (yyyy-MM-dd): ");
                       to = LocalDate.parse(in.nextLine().trim());
                   } catch (DateTimeParseException e) {
                       errMsg = "Invalid date. Use yyyy-MM-dd (e.g. 2026-10-20).\n";
                       break;
                   }
                   service.searchChoresByDeadlineRange(from, to);
                   pause(in);
                   break;
               }
               case "7": {
                   System.out.print("Low amount: ");
                   int low = Integer.parseInt(in.nextLine());
                   System.out.print("High amount: ");
                   int high = Integer.parseInt(in.nextLine());
                   service.searchExpensesByAmountRange(low, high);
                   pause(in);
                   break;
               }
               case "8": {
                   service.viewChoreHistory();
                   pause(in);
                   break;
               }
               case "9": {
                   service.viewPendingQueue();
                   pause(in);
                   break;
               }
               case "10": {
                   service.viewChoreOrder();
                   pause(in);
                   break;
               }
               case "11": {
                   service.viewExpensesByCategory();
                   pause(in);
                   break;
               }
               case "12": {
                   System.out.print("Member ID: ");
                   String memberID = in.nextLine();
                   service.lookupMemberChores(memberID);
                   pause(in);
                   break;
               }
               case "13": {
                   System.out.print("Prerequisite chore title: ");
                   String prereq = in.nextLine();
                   System.out.print("Dependent chore title (must happen after): ");
                   String dependent = in.nextLine();
                   service.addChoreDependency(prereq, dependent);
                   pause(in);
                   break;
               }
               case "0": loop = false;
               default: errMsg = "Invalid input please try again.\n";
           }
        }
    }

    private static void pause(Scanner in) {
        System.out.println("\nPress Enter to continue...");
        in.nextLine();
    }

    public static void clearConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}

package org.example;
import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    static void main() {
        Scanner in = new Scanner(System.in);
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
                   7.\tView chore history
                   8.\tView pending chore queue
                   9.\tView chore order
                   10.\tView expenses by category
                   11.\tLook up member's chore
                   0.\tExit
                   Choose an option:
                   """);
           errMsg = "";
           String usrIn = in.nextLine();

           switch (usrIn) {
               case "1": break;
               case "2": break;
               case "3": break;
               case "4": break;
               case "5": break;
               case "6": break;
               case "7": break;
               case "8": break;
               case "9": break;
               case "10": break;
               case "11": break;
               case "0": loop = false; break;
               default: errMsg = "Invalid input please try again.\n"; break;
           }
        }
    }
    public static void clearConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}

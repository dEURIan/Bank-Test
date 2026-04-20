import java.util.Scanner;
import java.util.InputMismatchException;

public class TransactionManager {

    private Transaction[] transactions = new Transaction[100];
    private int count = 0;

    // deposit
    public void deposit(Account acc, Scanner sc) {
        double amount = 0;

        try {
            System.out.print("Enter deposit amount: ");
            amount = sc.nextDouble();
            sc.nextLine();

            if (amount <= 0) {
                System.out.println("Amount must be positive.");
                return;
            }

            acc.setBalance(acc.getBalance() + amount);
            recordTransaction("Deposit", amount);

            System.out.println("Deposit successful!");

        } catch (InputMismatchException e) {
            System.out.println("Invalid input.");
            sc.nextLine();
        }
    }

    // withdraw
    public void withdraw(Account acc, Scanner sc) {
        double amount = 0;

        try {
            System.out.print("Enter withdraw amount: ");
            amount = sc.nextDouble();
            sc.nextLine();

            if (amount <= 0) {
                System.out.println("Amount must be positive.");
                return;
            }

            if (amount > acc.getBalance()) {
                System.out.println("Insufficient balance. No overdraft allowed.");
                return;
            }

            acc.setBalance(acc.getBalance() - amount);
            recordTransaction("Withdraw", amount);

            System.out.println("Withdrawal successful!");

        } catch (InputMismatchException e) {
            System.out.println("Invalid input.");
            sc.nextLine();
        }
    }

    // record transactions
    private void recordTransaction(String type, double amount) {
        transactions[count] = new Transaction(type, amount);
        count++;
    }

    // view transactions
    public void viewTransactions() {
        if (count == 0) {
            System.out.println("No transactions yet.");
            return;
        }

        System.out.println("\n===== TRANSACTION HISTORY =====");
        for (int i = 0; i < count; i++) {
            System.out.println(transactions[i]);
        }
    }

    // GUI-friendly overloads (no Scanner needed)
    public void deposit(Account acc, double amount) {
        acc.setBalance(acc.getBalance() + amount);
        recordTransaction("Deposit", amount);
    }

    public void withdraw(Account acc, double amount) {
        acc.setBalance(acc.getBalance() - amount);
        recordTransaction("Withdraw", amount);
    }

    // for save report (uhh optional getter)
    public Transaction[] getTransactions() {
        return transactions;
    }

    public int getCount() {
        return count;
    }
}

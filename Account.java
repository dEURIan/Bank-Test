public class Account {
    private int accountNumber;
    private double balance;
    private String pin;
    private String name;
    private String birthDate; // YYYY-MM-DD
    
    private TransactionManager transactionManager;

    public Account(int accountNumber, String name, double balance, String pin, String birthDate) {
        this.accountNumber = accountNumber;
        this.name = name;
        this.balance = balance;
        this.pin = pin;
        this.birthDate = birthDate;
        
        this.transactionManager = new TransactionManager();
    }
    
    public TransactionManager getTransactionManager(){
        return transactionManager;
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    public String getName() {
        return name;
    }

    public String getPin() {
        return pin;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void displayInfo() {
        System.out.println("Account Number: " + accountNumber);
        System.out.println("Name: " + name);
        System.out.println("Balance: " + balance);
    }
}

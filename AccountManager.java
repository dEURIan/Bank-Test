import java.util.Scanner;
import java.util.Random;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeParseException;
import java.util.InputMismatchException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;

public class AccountManager {

    private Account[] accounts = new Account[100];
    Random rand = new Random();
    private int count = 0;

    private int generateAccountNumber() {
        int accNum;
        do{
            accNum = 100000 + rand.nextInt(900000);
        } while(getAccount(accNum) != null);
        return accNum;
    }

    private boolean isValidName(String name) {
        if (name == null) {
            return false;
        }
        name = name.trim();
        return !name.isEmpty() && name.matches("[A-Za-z '-]+$");
    }

    private boolean isValidBirthDate(String birthDate) {
        if (birthDate == null) {
            return false;
        }
        try {
            LocalDate birth = LocalDate.parse(birthDate.trim());
            return !birth.isAfter(LocalDate.now());
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public boolean isEligible(String birthDate) {
        if (!isValidBirthDate(birthDate)) return false; // sanitize
        LocalDate birth = LocalDate.parse(birthDate.trim());
        LocalDate today = LocalDate.now();
        int age = Period.between(birth, today).getYears();
        return age >= 18;
    }

    // --- Core existing logic (No changes) ---
    public void createAccount(Scanner sc) {
        int accNum = generateAccountNumber();

        String name;
        do {
            System.out.print("Enter Name: ");
            name = sc.nextLine().trim();
            if (!isValidName(name)) {
                System.out.println("Invalid name. Please enter letters, spaces, hyphens, or apostrophes only.");
            }
        } while (!isValidName(name));

        String birthDate;
        do {
            System.out.print("Enter Birthdate (YYYY-MM-DD): ");
            birthDate = sc.nextLine().trim();
            if (!isValidBirthDate(birthDate)) {
                System.out.println("Invalid birthdate.");
            } else if (!isEligible(birthDate)) {
                System.out.println("You must be at least 18 years old to open an account.");
                return;
            }
        } while (!isValidBirthDate(birthDate));

        String pin = "";
        boolean validPin = false;
        while (!validPin) {
            System.out.print("Enter 4-digit PIN: ");
            pin = sc.nextLine();

            if (pin.matches("\\d{4}")) {
                validPin = true;
            } else {
                System.out.println("PIN must be exactly 4 digits.");
            }
        }

        double balance = 0;
        boolean validBalance = false;
        while (!validBalance) {
            try {
                System.out.print("Enter Initial Deposit: ");
                balance = sc.nextDouble();

                if (balance >= 0) {
                    validBalance = true;
                } else {
                    System.out.println("Balance cannot be negative.");
                }

            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter numbers only.");
                sc.nextLine();
            }
        }

        sc.nextLine();
        boolean success = addAccount(accNum, name, balance, pin, birthDate);
        if (!success) {
            System.out.println("Failed to create account.");
        }
    }

    public Account getAccount(int accNum) {
        for (int i = 0; i < count; i++) {
            if (accounts[i].getAccountNumber() == accNum) {
                return accounts[i];
            }
        }
        return null;
    }

    public Account getAccountByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }
        String trimmedName = name.trim();
        for (int i = 0; i < count; i++) {
            if (accounts[i].getName().equalsIgnoreCase(trimmedName)) {
                return accounts[i];
            }
        }
        return null; // No match found
    }

    public boolean addAccount(int accNum, String name, double balance, String pin, String birthDate) {
        if (count >= accounts.length){
            System.out.println("Account limit reached.");
            return false;
        }
        
        accounts[count] = new Account(accNum, name, balance, pin, birthDate);
        count++;

        System.out.println("Account created successfully!");
        System.out.println("Your Account Number: " + accNum);
        return true;
    }

    // --- Core existing logic (No changes) ---
    public Account login(Scanner sc) {
        System.out.print("Enter Account Number: ");
        int accNum;
        
        try{
            accNum = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e){
            System.out.println("Invalid account number.");
            return null;
        }
        
        Account acc = getAccount(accNum);
        
        if (acc == null){
            System.out.println("Account not found.");
            return null;
        }
        
        int attempts = 1;
        
        do{
            System.out.print("Enter PIN: ");
            String pin = sc.nextLine();
            
            if (acc.getPin().equals(pin)){
                System.out.println("Login successful!");
                return acc;
            } else{
                System.out.println("Invalid PIN.");
                attempts++;
            }
        } while (attempts <= 3);
        
        System.out.println("You have been locked out.");
        return null;
    }
    
    public void loadFromFile(){
        try (BufferedReader br = new BufferedReader(new FileReader("accounts.txt"))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");

                int accNum = Integer.parseInt(data[0]);
                String name = data[1];
                String pin = data[2];
                String birthDate = data[3];
                double balance = Double.parseDouble(data[4]);

                accounts[count++] = new Account(accNum, name, balance, pin, birthDate);
            }

        } catch (IOException e) {
            System.out.println("No previous accounts found.");
        }
    }

    public void saveToFile() {

        try (FileWriter writer = new FileWriter("accounts.txt")) {

            for (int i = 0; i < count; i++) {
                Account acc = accounts[i];

                writer.write(
                        acc.getAccountNumber() + "," +
                                acc.getName() + "," +
                                acc.getPin() + "," +
                                acc.getBirthDate() + "," +
                                acc.getBalance() + "\n");
            }

        } catch (IOException e) {
            System.out.println("Unable to save account data.");

        }
    }

    // ================================================================
    //  GUI OPTIMIZED OVERLOADS (NO SCANNER)
    // ================================================================

    public int createAccountGUI(String name, String birthDate, String pin, double initialBalance) {
        // 1. Business Logic Rule: PIN must be 4 digits
        if (!pin.matches("\\d{4}")) {
            return -1; // Indicate failure
        }


        if (initialBalance < 0) {
            return -1; // Indicate failure
        }

        if (!isEligible(birthDate)) {
            return -1; // Indicate failure
        }

        int accNum = generateAccountNumber();

        boolean added = addAccount(accNum, name, initialBalance, pin, birthDate);
        
        if (added) {
            return accNum; // Return the new account number to the GUI
        } else {
            return -1; // Indicate failure
        }
    }

    public Account loginGUI(String identifier, String pin) {
        Account acc = null;
        try {
            int accNum = Integer.parseInt(identifier);
            acc = getAccount(accNum); // Uses existing helper
        } catch (NumberFormatException e) {
            // Not a number, likely a name
        }

        if (acc == null) {
            acc = getAccountByName(identifier); 
        }
        if (acc == null) {
            return null; // Account not found
        }

        if (acc.getPin().equals(pin)) {
            return acc; // Success
        } else {
            return null; // Incorrect PIN
        }
    }

}
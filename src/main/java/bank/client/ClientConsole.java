package bank.client;

import java.util.Map;
import java.util.Scanner;

import bank.account.AccountController;
import bank.account.BankAccount;

public class ClientConsole {
    static Scanner input = new Scanner(System.in);

    private ClientConsole() {}

    public static void printClientConsole(int clientID) {
        System.out.println(
            "=====================================" + "\n" +
            "Current Client: " + clientID + "(ID)" + "\n" +
            "Choose an Action:" + "\n" +
            "1: Check Accounts" + "\n" +
            "2: Create New Account" + "\n" +
            "3: Log into Account" + "\n" +
            "4: Change Details" + "\n" +
            "5: Exit Bank" + "\n" +
            "=====================================");
    }

    //* Used to get a name to create / log into clients */
    public static void startClientLoginConsole() {
        System.out.println("Getting your information:");
        System.out.println("Enter your name:");
    }

    public static void beginDefaultClientConsole(ClientClass client) {
        try {
            AccountController currController = client.getAccountController();
            
            printClientConsole(client.id);

            boolean exitInput = false;
            String choice;
    
            while ((choice = input.nextLine()) != null) {
                switch (choice) {
                    case "1":
                        Map<Integer, BankAccount> accountList = client.getAccountList();
                        
                        System.out.println("Account list:");

                        for (BankAccount account : accountList.values()) {
                            System.out.println("Name: " + account.accountName + ", Balance: " + account.balance);
                        }
    
                        Thread.sleep(2000);
    
                        printClientConsole(client.id);
    
                        break;
                    case "2":
                        //*Need to re-print the client console as the account console yields (Input.nextLine()) */
                        currController.createAccount();
    
                        printClientConsole(client.id);
                        break;
                    case "3":
                        logInAccountOptions(currController);
    
                        printClientConsole(client.id);
                        break;

                    case "4":

                        System.out.println("Enter a new name: ");
                        String newName = input.nextLine();
                        
                        if (newName.equals(client.name)) {
                            client.changeName(newName);

                            System.out.println("Updated current client name!");
                        } else {
                            System.out.println("Failed to change name as the inputed name is equal to the current name");
                        }

                        break;
    
                    case "5":
                        System.out.println("Exiting bank");
                        exitInput = true;
                        
                        break;
                
                    default:
                        System.out.println("Choose again!");
                        break;
                }
    
                if (exitInput) { break; }
            }
        } catch (Exception e) {
            Thread.currentThread().interrupt();

            e.printStackTrace();
        }
    }

    private static void logInAccountOptions(AccountController accountController) {
        System.out.println(
            "1: Log into recent Account" + "\n" + 
            "2: Log into new Account" + "\n" +
            "3: Exit"
        );

        String choice;

        outer: while ((choice = input.nextLine()) != null) {
            switch (choice) {
                case "1":
                    accountController.logIntoRecentAccount();

                    break outer;
                case "2":
                    accountController.logIntoNewAccount();

                    break outer;
                case "3":
                    System.out.println("Exitting");
                    break outer;
                default:
                    System.out.println("Choose again!");

                    break;
            }

        }
        
    }
}
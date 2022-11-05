package bank.client;

import bank.LoanHandler;
import bank.account.AccountController;

public class ClientClass {
    public String name;
    public int id;
    
    public AccountController accountController;

    public LoanHandler loanHandler;

    ClientClass(String name, int id) {
        this.name = name;
        this.id = id;

        accountController = new AccountController(this);

        accountController.populateAccountList(this);

        loanHandler = new LoanHandler(this);
    }

    public void changeName(String newName) {
        if (newName.equals(this.name)) {
            ClientQueries.changeClientName(newName, id);
        }
     }
 }
package bank.client;

import java.util.Map;

import bank.LoanHandler;
import bank.account.AccountController;
import bank.account.AccountDAO;
import bank.account.BankAccount;

public class ClientClass {
    public String name;
    public int id;
    
    private AccountController accountController;

    public LoanHandler loanHandler;

    public ClientDAO clientDAO;

    //* Dependency injection */
    public ClientClass(AccountController AC, LoanHandler loanHandler ,ClientDAO clientDAO, String name, int id) {
        this.name = name;
        this.id = id;

        this.accountController = AC;

        accountController.populateAccountList();

        this.loanHandler = loanHandler;

        this.clientDAO = clientDAO;
    }

    //* For code that does not have access to the client  */
    public ClientClass(ClientDAO clientDAO, String name, int id) {
        this.name = name;
        this.id = id;

        this.clientDAO = clientDAO;
    }

    //TODO: Fix the dependancy circulation (Dettach the account cotroller and loan handler ref ?s\)
    public void setUpControllers() {
        this.accountController = new AccountController(this, new AccountDAO());

        accountController.populateAccountList();

        this.loanHandler = new LoanHandler(this);
    }

    public void changeName(String newName) {
        if (!newName.equals(this.name)) {
            clientDAO.changeClientName(newName, id);

            this.name = newName;
        }
     }

    public AccountController getAccountController() {
        return accountController;
    }

    public BankAccount getCurrentAccount() {
        return accountController.currentAccount;
    }

    public Map<Integer, BankAccount> getAccountList() {
        return accountController.accountList;
    }

    public ClientDAO getClientDAO() {
        return clientDAO;
    }
 }
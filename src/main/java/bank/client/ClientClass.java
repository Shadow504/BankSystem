package bank.client;

import java.util.Map;

import bank.LoanHandler;
import bank.account.BankAccount;
import bank.account.console.AccountController;

public class ClientClass {
    public String name;
    public int id;
    
    //! Dependency circulation
    //* Not fixable due to our structure (Has A relation)  */
    //* Other classes also uses the Client as an object reference (Client has an AccountController and a loanHandler) */
    private AccountController accountController;

    public LoanHandler loanHandler;

    public ClientDAO clientDAO;

    //* For code that does not have access to the client  */
    public ClientClass(ClientDAO clientDAO, String name, int id) {
        this.name = name;
        this.id = id;

        this.clientDAO = clientDAO;
    }

    //* We don't put these snippets into the constructor as a mean to be able to unit test
    public void setUpControllers(AccountController accountController, LoanHandler loanHandler) {
        this.accountController = accountController;
        this.loanHandler = loanHandler;
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
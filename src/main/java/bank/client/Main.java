package bank.client;

import bank.LoanHandler;
import bank.account.AccountDAO;
import bank.account.console.AccountController;

class Main {

    public static void main(String[] args) {

        ClientCreator clientCreator = new ClientCreator(new ClientDAO());

        try { 
            ClientClass client = clientCreator.beginLoginSession();


            AccountController accountController = new AccountController(client, new AccountDAO());
            accountController.populateAccountList();

            client.setUpControllers(accountController, new LoanHandler(client));

            //* Consoles and objects should be separated even if the consoles perform business logic*/
            ClientConsole.beginDefaultClientConsole(client);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
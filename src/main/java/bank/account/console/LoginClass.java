package bank.account.console;

import java.sql.SQLException;

import bank.account.AccountDAO;
import bank.client.ClientClass;


//* Handle all of the account inputs and get the existing account's id*/
public class LoginClass extends LoginImpl {

    LoginClass(ClientClass client, AccountDAO accountDAO) {
        super(client, accountDAO);
    }

    private String login() throws SQLException {
        String[] userTbl = getInputs();

        String name = userTbl[0];
        String password  = userTbl[1];

        if (!checkAccountNameExists(client.id, name)) {
            System.out.println("Account's name does not exist");

            return login();
        }

        if (checkMatchPassword(client.id, name, password)) {
            System.out.println("Wrong password");

            return login();
        }

        return name;
    }

    public int beginLoginSession() throws SQLException {
        System.out.println("Now  logging in:");

        String name = login();

        System.out.println("Logged in account as: " + name );

        return accountDAO.getAccountId(name, client.id);
    }
}

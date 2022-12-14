package bank.account.console;

import java.sql.SQLException;

import bank.account.AccountDAO;
import bank.client.ClientClass;


//* Handle all of the account inputs and get the future account name and password*/
public class SigninClass extends LoginImpl {

    SigninClass(ClientClass client, AccountDAO accountDAO) {
        super(client, accountDAO);
    }

    private String[] signin() throws SQLException {

        String[] userTbl = getInputs();
    
        String name = userTbl[0];
        String password  = userTbl[1];
    
        System.out.println("Confirm Password:");
    
        String confirmPassword = input.nextLine();
    
        if (checkAccountNameExists(client.id, name)) {
            System.out.println("Account's name already exist");
    
            return signin();
        }
    
        if (!authorizePassword(password)) {
            System.out.println("Password does not qualify");
    
            return signin();
        }
    
        if (!password.equals(confirmPassword)) {
            System.out.println("Passwords do not match");
    
            return signin();
        }
    
        input.close();
    
        return userTbl;
    }
    
    public String[] beginSigninSession() throws SQLException {
        System.out.println("Now signing in: ");
    
        String[] userTbl = signin();
    
        String name = userTbl[0];
        String password  = userTbl[1];
    
        System.out.println("New user created: " + name + " " + password);
    
        return userTbl;
    }
}
package bank.account.console;

import java.sql.*;
import java.util.Scanner;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;

import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;

import bank.BankDataConnection;
import bank.account.AccountDAO;
import bank.client.ClientClass;
import shared.Shared;
 
//* THIS CLASS HANDLES THE INPUTS ONLY, NOT THE ACTUAL LOGIN / SIGNIN */
class LoginImpl {
    ClientClass client;
    AccountDAO accountDAO;

    LoginImpl(ClientClass client, AccountDAO accountDAO) {
        this.client = client;
        this.accountDAO = accountDAO;
    }

    static Scanner input = Shared.input;

    protected static String[] getInputs() {

        String name = null;
        String password = null;

        System.out.println("Enter your Account Name:"); 
        name = input.nextLine();


        System.out.println("Enter your Account Password:");
        password = input.nextLine();

        String[] returnTbl = {name, password};
        return returnTbl;
    }

    protected static boolean authorizePassword(String password) {
        return password != null;
    }

    static MysqlConnectionPoolDataSource ds = BankDataConnection.ds;
    
    static Connection cn;
    static CachedRowSet crs;

    static {

        try (CachedRowSet newCrs = RowSetProvider.newFactory().createCachedRowSet()) {

            cn = ds.getConnection();

            crs = newCrs;
        } catch (SQLException e) {

            e.printStackTrace();
        }
    }

    //Used for logins
    protected static String checkMatchPasswordQuery = "CALL checkMatchPassword(?, ?, ?)";

    protected static boolean checkMatchPassword(int client_id, String name, String inputPassword) throws SQLException {
        try (CallableStatement cs = cn.prepareCall(checkMatchPasswordQuery)) {

            cs.setInt(1, client_id);
            cs.setString(2, name);
            cs.setString(3, inputPassword);

            ResultSet rs = cs.executeQuery();
            rs.next();

            return rs.getBoolean("matched");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    protected static String checkNameExist = "CALL checkNameExist(?, ?)";

    protected static boolean checkAccountNameExists(int client_id, String name) throws SQLException {
        try (CallableStatement cs = cn.prepareCall(checkNameExist)) {

            cs.setInt(1, client_id);
            cs.setString(2, name);
    
            ResultSet rs = cs.executeQuery();
            rs.next();

            return rs.getBoolean("exist");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
    
}
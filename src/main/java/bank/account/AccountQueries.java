package bank.account;

import java.sql.*;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;

import  com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;

import bank.BankDataConnection;

//* Store and handle SHARED querries for the accounts */
public class AccountQueries {
    private AccountQueries() {}

    private static MysqlConnectionPoolDataSource ds = BankDataConnection.ds;

    private static Connection cn;
    private static CachedRowSet crs;

    static {

        try (CachedRowSet newCrs = RowSetProvider.newFactory().createCachedRowSet()) {

            cn = ds.getConnection();

            crs = newCrs;
        } catch (SQLException e) {

            e.printStackTrace();
        }
    }

    //* RETRIEVE INFO QUERIES */

    private static String getAccountId = "SELECT id FROM accounts WHERE account_name = ? AND client_id = ?";

    public static int getAccountId(String name, int client_id) throws SQLException {
        crs.setCommand(getAccountId);
        crs.setString(1, name);
        crs.setInt(2, client_id);

        crs.execute(cn);
        crs.first();

        return crs.getInt("id");
    }
    
    private  static String getAccountIdLastInserted = "SELECT MAX(id) AS 'lastInsertedID' FROM accounts";

    public static int getLastInsertedId(String name, String password) throws SQLException {
        crs.setCommand(getAccountIdLastInserted);
        crs.setString(1, name);
        crs.setString(2, password);
    
        crs.execute(cn);
        crs.first();

        return crs.getInt("lastInsertedID");
    }

    private  static String getNameAndPasswordOnId = "SELECT account_name AS name, account_password AS password FROM accounts WHERE id = ?";

    public static CachedRowSet getNameAndPassWordFromId(int account_id) throws SQLException {
        crs.setCommand(getNameAndPasswordOnId);
        crs.setInt(1, account_id);

        crs.execute(cn);

        return crs;
    }

    private  static String insertAccountQuery = "INSERT INTO Accounts(client_id, account_name, account_password, balance) VALUES (?, ?, ?, 0)";

    public static void insertAccount(int client_id, String name, String password) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement(insertAccountQuery)) {
            ps.setInt(1, client_id);
            ps.setString(2, name);
            ps.setString(3, password);
    
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static String returnAccountsForClient = 
    "SELECT id, account_name AS name, account_password AS password, balance FROM accounts WHERE client_id = ?";

    public static ResultSet getAccountsForClient(int client_id) throws SQLException {
        PreparedStatement ps = null;

        try {
            
            ps = cn.prepareStatement(returnAccountsForClient);

            ps.closeOnCompletion();
    
            ps.setInt(1, client_id);
    
            ResultSet rs = ps.executeQuery();
            
            return rs;
        } catch (Exception e) {
            e.printStackTrace();

        } 
 
        return null;
    }

    private static String getRecentLoggedInAccountQuerry = "SELECT current_logged_in_account_id AS account_id FROM clients WHERE id = ?";

    //* Fetch the most recent account that was logged in */
    public static int getRecentLoggedInAccount(int client_id) {

        try (PreparedStatement newCrs = cn.prepareStatement(getRecentLoggedInAccountQuerry)){


            newCrs.setInt(1, client_id);

            ResultSet rs = newCrs.executeQuery();
            rs.next();

            return rs.getInt("account_id");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    //* UPDATE QUERIES */

    private static String updateAccountBalanceQuery = "UPDATE accounts SET balance = ? WHERE id = ?";

    public static void updateAnAccountBalance(int accountId, int amount) {
        try (
            Connection cn = ds.getConnection();
            PreparedStatement ps = cn.prepareStatement(updateAccountBalanceQuery)) {

            ps.setInt(1, amount);
            ps.setInt(2, accountId);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static String updateRecentAccountQuerry = "UPDATE clients SET current_logged_in_account_id = ? WHERE id = ?";

    //* Update the current_logged_in_account_id field to the specified account_id*/
    public static int updateRecentLoggedInAccount(int client_id, int account_id) {
        try {
            crs.setCommand(updateRecentAccountQuerry);
            crs.setInt(1, account_id);
            crs.setInt(2, client_id);

            crs.execute(cn);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

}
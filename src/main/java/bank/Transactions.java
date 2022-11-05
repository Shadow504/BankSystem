package bank;

import java.sql.Connection;
import java.sql.PreparedStatement;

import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;

import bank.account.BankAccount;

public class Transactions {
    private Transactions() {}

    static MysqlConnectionPoolDataSource ds = BankDataConnection.ds;

    static Connection cn;

    static {
        try {
            cn = ds.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String insertTransaction = 
    "INSERT INTO transactions(from_account_id, to_account_id, amount, type) VALUES(?, ?, ?, ?)";

    public static void logStandardTransaction(BankAccount currAccount, int amount) {

        try (PreparedStatement ps = cn.prepareStatement(insertTransaction)) {
            ps.setInt(1, currAccount.id);
            ps.setInt(2, currAccount.id);

            ps.setInt(3, amount);
            ps.setString(4, amount > 0 ? "Deposit" : "Withdrawal");

            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }   

    public static void logTransfer(int currAccountId, int targetAccountId , int amount) {

        try (PreparedStatement ps = cn.prepareStatement(insertTransaction)) {
            ps.setInt(1, currAccountId);
            ps.setInt(2, targetAccountId);

            ps.setInt(3, amount);
            ps.setString(4, "Transfer");

            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void logLoanAcquisition(int currAccountId, int amount) {
        try (PreparedStatement ps = cn.prepareStatement(insertTransaction)) {
            ps.setInt(1, currAccountId);
            ps.setInt(2, currAccountId);
            
            ps.setInt(3, amount);
            ps.setString(4, "Loan");

            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}   
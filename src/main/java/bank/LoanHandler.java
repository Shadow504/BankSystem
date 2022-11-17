package bank;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;

import bank.account.BankAccount;
import bank.client.ClientClass;

//* Each client should hold a single loan handler */
public class LoanHandler {

    static final String PENDING = "Pending";
    static final String PAID = "Paid";

    static final int MAX_LOAN = 5;
    static int defaultDeadlineGap = 1;

    ClientClass client;
    LoanDAO loanDAO;

    private ExecutorService queuePool;

    private HashMap<Integer, Loan> currentLoans = new HashMap<>();

    public LoanHandler(ClientClass client) {
        this.client = client;
        //TODO: make this loan dao a param
        this.loanDAO = new LoanDAO();

        queuePool = Executors.newFixedThreadPool(MAX_LOAN);
            
        instantiateLoans();
    }

    private boolean authorizeLoanRequest() {
        if (currentLoans.size() >= MAX_LOAN) {
            System.out.println("Maximum amount of loans reached");

            return false;
        }

        return true;
    }

    private void instantiateLoans() {
        CachedRowSet loansData = loanDAO.getClientLoans(client.id);
    
        try {
            while (loansData.next()) {
                int id = loansData.getInt("id");
                int accountId = loansData.getInt("account_id");
                
                int loanAmount = loansData.getInt("amount");
                Timestamp deadline = loansData.getTimestamp("date_deadline");

                Loan loanObj = new Loan(loanDAO, id, client.getAccountList().get(accountId), loanAmount, deadline);

                currentLoans.put(id, loanObj);
                checkForALoanExpireDate(loanObj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //* Constantly checking for the expire date */
    private void checkForALoanExpireDate(Loan loanObj) {
        queuePool.submit(() -> {
            try {
                long currTime = new Date().getTime();
                long timeTilExpire = loanObj.deadline.getTime() - currTime;
    
                if (timeTilExpire > 0) {
                    Thread.sleep(timeTilExpire);
                }
    
                loanObj.expire();
                loanObj.updateDeadline(calculateDeadline());

                System.out.println("Loan expired, increasing loan amount to: " + loanObj.loanAmount);
            } catch (Exception e) {
                Thread.currentThread().interrupt();

                e.printStackTrace();
            }
        });
    }

    private Timestamp addDays(Timestamp time, int days) {
        Calendar c = Calendar.getInstance();

        c.setTime(time);
        c.add(Calendar.DATE, days);

        return new Timestamp(c.getTimeInMillis());
    }

    private Timestamp calculateDeadline() {
        Timestamp currTime = new Timestamp(new Date().getTime());
        
        Timestamp deadline = addDays(currTime, defaultDeadlineGap);

        return deadline;
    }

    public void applyLoan(BankAccount account, int loanAmount) {
        if (!authorizeLoanRequest()) { return; }

        Timestamp deadline = calculateDeadline();

        int id = loanDAO.insertLoan(account.id, loanAmount, deadline);

        if (id > 0) { return; }

        try {
            
            Loan loanObj = new Loan(loanDAO, id, account, loanAmount, deadline);

            currentLoans.put(id, loanObj);
            account.incrementbalance(loanAmount);

            checkForALoanExpireDate(loanObj);
            Transactions.logLoanAcquisition(account.id, loanAmount);                
            
            System.out.println("Acquired loan successfully " + account.accountName + "'s balance is now: " + account.balance);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}

class LoanDAO {

    static MysqlConnectionPoolDataSource ds = BankDataConnection.ds;
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

    static final String insertLoanQuery = "INSERT INTO loans(account_id, amount, date_deadline, status) VALUES(?, ?, ?, ?)";

    int insertLoan(int account_id, int amount, Timestamp deadline) {

        try (PreparedStatement ps = cn.prepareStatement(insertLoanQuery, new String[]{"id"})) {

            ps.setInt(1, account_id);
            ps.setInt(2, amount);

            ps.setTimestamp(3, deadline);
            ps.setString(4, LoanHandler.PENDING);
            
            ps.execute();

            return ps.getGeneratedKeys().getInt(1);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    static String getClientLoansQuery = "SELECT * FROM loans WHERE account_id = ?";

    CachedRowSet getClientLoans(int account_id) {
        try {
            crs.setCommand(getClientLoansQuery);

            crs.setInt(1, account_id);
            
            crs.execute(cn);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return crs;
    }

    static String updateLoanAmountQuery = "UPDATE loans SET amount = ? WHERE id = ?";

    void updateLoanAmount(int loanId, int newAmount) {
        try (PreparedStatement ps = cn.prepareStatement(updateLoanAmountQuery)) {
        
            ps.setInt(1, newAmount);
            ps.setInt(2, loanId);  
            
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static String updateLoanStatusQuery = "UPDATE loans SET status = ? WHERE id = ?";

    void updateLoanStatus(int loanId, String newStatus) {
        try (PreparedStatement ps = cn.prepareStatement(updateLoanStatusQuery)) {

            ps.setString(1, newStatus);
            ps.setInt(2, loanId);  
            
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static String updateLoanDeadlineQuery = "UPDATE loans SET date_deadline = ? WHERE id = ?";

    void updateLoanDeadline(int loanId, Timestamp newDeadline) {
        try (PreparedStatement ps = cn.prepareStatement(updateLoanDeadlineQuery)) {

            ps.setTimestamp(1, newDeadline);
            ps.setInt(2, loanId);  
            
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
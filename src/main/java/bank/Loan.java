package bank;

import java.sql.Timestamp;

import bank.account.BankAccount;

public class Loan {

    static int loanInterest = 20;

    int id;
    BankAccount accountIssued;

    int loanAmount;
    String status;

    Timestamp deadline;
    
    //* Class to store the loan into a java obj, which is derived from the sql loan table */
    Loan(int id, BankAccount accountIssued, int loanAmount, Timestamp deadline) {
        this.id  = id;
        this.accountIssued = accountIssued;

        this.loanAmount = loanAmount;
        this.deadline = deadline;
    }

    //* When the deadline expires, the loanAmount will increase by loanInterest */
    public void expire() {
        loanAmount = (loanAmount * loanInterest) / 100;

        LoanQueries.updateLoanAmount(id, loanAmount);
    }

    //* Should not reduce the account's balance here due to security and organization reasons, but instead do it in the account console )

    public void payOff() {
        LoanQueries.updateLoanAmount(id, 0);

        LoanQueries.updateLoanStatus(id, LoanHandler.PAID);

        accountIssued.incrementbalance(-loanAmount);
    }

}   
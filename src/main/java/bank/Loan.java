package bank;

import java.sql.Timestamp;

import bank.account.BankAccount;

public class Loan {

    static int loanInterest = 120;

    int id;
    BankAccount accountIssued;

    int loanAmount;
    String status;

    Timestamp deadline;
    LoanDAO loanDAO;
    
    //* Class to store the loan into a java obj, which is derived from the sql loan table */
    Loan(LoanDAO loanDAO, int id, BankAccount accountIssued, int loanAmount, Timestamp deadline) {
        this.id  = id;
        this.accountIssued = accountIssued;

        this.loanAmount = loanAmount;
        this.deadline = deadline;

        this.loanDAO = loanDAO;
    }

    //* When the deadline expires, the loanAmount will increase by loanInterest */
    public void expire() {
        loanAmount = (loanAmount * loanInterest) / 100;

        loanDAO.updateLoanAmount(id, loanAmount);
    }

    public void updateDeadline(Timestamp newDeadline) {
        this.deadline = newDeadline;

        loanDAO.updateLoanDeadline(id, newDeadline);
    }

    //* Should not reduce the account's balance here due to security and organization reasons, but instead do it in the account console )

    public void payOff() {
        loanDAO.updateLoanAmount(id, 0);

        loanDAO.updateLoanStatus(id, LoanHandler.PAID);

        accountIssued.incrementbalance(-loanAmount);
    }

}   
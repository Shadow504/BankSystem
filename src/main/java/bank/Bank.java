package bank;

//* Static class, holds the bank's stats  */
public class Bank {
    private Bank() {}

    public static String bankName = "A Bank";
    public static int defaultInterestRate = 2;

    public static int minTransferAmount = 100; 
    public static int minWithdrawAmount = 100;

    public static int minDepositAmount = 150;
    public static int minLoanAmount = 200;
}
       
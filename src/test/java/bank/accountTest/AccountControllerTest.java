package bank.accountTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import bank.account.AccountDAO;
import bank.account.BankAccount;
import bank.account.console.AccountConsole;
import bank.account.console.AccountController;
import bank.client.ClientClass;

public class AccountControllerTest {
    @Mock
    AccountDAO accountDAO;

    @Mock
    ClientClass client;

    @Mock
    AccountConsole accountConsole;

    @InjectMocks
    AccountController accountController;

    static final int clientTestId = 0;

    static final int AccountTestId = 1; // Account id for populate and log ins
    static final int insertedAccountTestId = 2; //Account id for sign ins

    @BeforeEach
    public void setUp() {
        
        accountDAO = mock(AccountDAO.class);

        client = mock(ClientClass.class);
   

        accountController = new AccountController(client, accountDAO);
        accountController.currConsole = mock(AccountConsole.class);
    }

    @Test 
    public void createAccountTest() throws SQLException {
        //* Check if not exist first */
        assertNull(accountController.accountList.get(insertedAccountTestId));

        String[] userAndPass = {"Bob", "123"};
        
        //* The actual method get the input which yields, and we dont want that */
        when(accountController.currConsole.beginSigninSession()).thenReturn(userAndPass);
        //* Mock the DAO's update query */
        when(accountDAO.insertAccount(clientTestId, userAndPass[0], userAndPass[1])).thenReturn(insertedAccountTestId);

        BankAccount account = accountController.createAccount();

        assertNotNull(account);
        assertEquals(insertedAccountTestId, account.id);
        assertEquals(accountController.accountList.get(insertedAccountTestId), account);
    }

    @Test
    public void populateAccountsTest() throws SQLException {

        ResultSet mockRS = mock(ResultSet.class);

        when(accountDAO.getAccountsForClient(clientTestId)).thenReturn(mockRS);
        //* Consecutive return calls */
        when(mockRS.next()).thenReturn(true, false);

        //* Mocks the Gets methods in the RS */
        when(mockRS.getString("name")).thenReturn("Bob");
        when(mockRS.getString("password")).thenReturn("1234");

        when(mockRS.getInt("balance")).thenReturn(0);
        when(mockRS.getInt("id")).thenReturn(AccountTestId);

        accountController.populateAccountList();

        assertNotNull(accountController.accountList.get(AccountTestId));
        assertEquals(AccountTestId, accountController.accountList.get(AccountTestId).id);
    }

    @Test
    public void logInAccountTest() throws SQLException {
        //* Insert a new account to populate (As the list does not replicate) */
        BankAccount insertedAccount = 
        new BankAccount(accountDAO, AccountTestId, clientTestId, "Bob", "1234", 0);

        accountController.accountList.put(AccountTestId, insertedAccount);

        when(accountController.currConsole.beginLoginSession()).thenReturn(AccountTestId);
        
        accountController.logIntoNewAccount();

        assertEquals(AccountTestId, accountController.currentAccount.id);
    }
}
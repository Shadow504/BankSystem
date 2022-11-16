package bank.clientTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Scanner;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;

import bank.client.ClientCreator;
import bank.client.ClientDAO;

public class ClientCreatorTest {
    @Mock
    ClientDAO clientDAO;

    ClientCreator clientCreator;

    @BeforeEach
    public void setUp() {
        clientDAO = mock(ClientDAO.class);

        clientCreator = new ClientCreator(clientDAO);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    public void logInTest(boolean loginType) {
        try {

            String input = "Bob";

            InputStream in = new ByteArrayInputStream(input.getBytes());
            System.setIn(in);

            clientCreator.input = new Scanner(System.in);

            when(clientDAO.checkIfClientExist("Bob")).thenReturn(loginType);

            assertEquals("Bob", clientCreator.beginLoginSession().name);

        } catch (SQLException e) {
            e.printStackTrace();
        }     
    }
}

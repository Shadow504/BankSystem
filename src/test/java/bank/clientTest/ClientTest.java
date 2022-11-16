package bank.clientTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;

import bank.client.ClientClass;
import bank.client.ClientDAO;

public class ClientTest {

    @Mock
    ClientDAO clientDAO;
    
    ClientClass client;

    @BeforeEach
    public void setUp() {
        clientDAO = mock(ClientDAO.class);
        
        when(clientDAO.changeClientName("Chris", 0)).thenReturn(true);

        client = new ClientClass(mock(ClientDAO.class), "Bob", 0);
    }

    @Test
    public void changeClientNameTest() {
        client.changeName("Chris");
        
        assertEquals("Chris", client.name);
    }

}

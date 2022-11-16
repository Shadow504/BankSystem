package bank.client;

import java.sql.*;
import java.util.Scanner;

import shared.Shared;

public class ClientCreator {
    
    public Scanner input = Shared.input;

    boolean exitInput = false;

    private ClientDAO clientDAO;

    public ClientCreator(ClientDAO clientDAO) {
        this.clientDAO = clientDAO;
    }

    private boolean confirmClientname(String name) {
        if (!authorizeName(name)) {  return false; }

        return true;
    }

    private boolean authorizeName(String name) {
        return name != null;
    }

    private String getInfo() throws SQLException {
        
        String name = input.nextLine();

        if (!confirmClientname(name)) {
            System.out.println("Re-enter your name: ");
            return getInfo();
        }

        return name;
    }

    public ClientClass beginLoginSession() throws SQLException {
        ClientConsole.startClientLoginConsole();

        String name = getInfo();

        return createClient(name);
    }

    private ClientClass createClient(String name) throws SQLException {

        //* Create a new client if the client name doesnt exist, log into one if it exists */
        if (clientDAO.checkIfClientExist(name)) {
            System.out.println("Logged into Client: " + name);

        } else {
            clientDAO.insertClient(name);

            System.out.println("Created Client: " + name);
        }

        return new ClientClass(clientDAO, name, clientDAO.getClientIdWithName(name));
    }
}
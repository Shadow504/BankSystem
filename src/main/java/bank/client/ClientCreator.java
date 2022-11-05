package bank.client;

import java.sql.*;
import java.util.Scanner;

import shared.Shared;

public class ClientCreator {
    
    static Scanner input = Shared.input;

    private ClientCreator() {}

    boolean exitInput = false;

    private static boolean confirmClientname(String name) throws SQLException {

        if (!authorizeName(name)) {  return false; }

        //* Create a new client if the client name doesnt exist, log into one if it exists */
        if (ClientQueries.checkIfClientExist(name)) {
            System.out.println("Logged into Client: " + name);

            return true;
        } else {
            ClientQueries.insertClient(name);

            System.out.println("Created Client: " + name);

            return true;
        }
    }

    private static boolean authorizeName(String name) {
        return name != null;
    }

    private static String getInfo() throws SQLException {

        String name = input.nextLine();

        if (!confirmClientname(name)) {
            System.out.println("Re-enter your name: ");
            return getInfo();
        }

        return name;
    }

    public static ClientClass beginLoginSession() throws SQLException {
        System.out.println("Getting your information:");
        System.out.println("Enter your name:");

        String name = getInfo();

        return new ClientClass(name, ClientQueries.getClientIdWithName(name));
    }
}
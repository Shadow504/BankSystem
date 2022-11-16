package bank.client;

class Main {

    public static void main(String[] args) {

        ClientCreator clientCreator = new ClientCreator(new ClientDAO());

        try { 
            ClientClass client = clientCreator.beginLoginSession();
            client.setUpControllers();

            //* Consoles and objects should be separated even if the consoles perform business logic*/
            ClientConsole.beginDefaultClientConsole(client);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
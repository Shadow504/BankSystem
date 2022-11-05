package bank.client;

class Main {

    public static void main(String[] args) {

        try { 
            ClientClass client = ClientCreator.beginLoginSession();

            ClientConsole.beginDefaultClientConsole(client);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
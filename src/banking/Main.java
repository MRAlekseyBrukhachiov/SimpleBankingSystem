package banking;

public class Main {
    public static void main(String[] args) {
        BankSystem bankSystem = new BankSystem(new DataBase("jdbc:sqlite:" + args[1]));
        bankSystem.run();
    }
}
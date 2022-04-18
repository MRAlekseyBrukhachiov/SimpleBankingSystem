package banking;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

public class BankSystem {

    private DataBase dataBase;
    private boolean runLoop;
    private boolean loggedIn;
    private Card currentCard;
    private Scanner in;

    public BankSystem(DataBase dataBase) {
        this.dataBase = dataBase;
        runLoop = true;
        loggedIn = false;
        in = new Scanner(System.in);
    }

    private void account() {
        System.out.println("1. Balance\n" +
                "2. Add income\n" +
                "3. Do transfer\n" +
                "4. Close account\n" +
                "5. Log out\n" +
                "0. Exit");
        int value = in.nextInt();
        switch (value) {
            case 1:
                System.out.println("Balance: " + currentCard.getBalance());
                break;
            case 2:
                System.out.println("Enter income: ");
                int cash = in.nextInt();
                int balance = currentCard.getBalance();
                dataBase.addInCome(currentCard, cash);
                currentCard.setBalance(balance + cash);
                break;
            case 3:
                transfer();
                break;
            case 4:
                dataBase.closeAccount(currentCard);
                break;
            case 5:
                currentCard = null;
                loggedIn = false;
                break;
            case 0:
                System.out.println("Bye!");
                runLoop = false;
                break;
        }
    }

    private void menu() {
        System.out.println("1. Create an account\n" +
                "2. Log into account\n" +
                "0. Exit");
        int value = in.nextInt();
        switch (value) {
            case 1:
                dataBase.createCard();
                break;
            case 2:
                logIn();
                break;
            case 0:
                System.out.println("Bye!");
                runLoop = false;
                break;
        }
    }

    private void transfer() {
        System.out.println("Transfer\nEnter card number:");
        String number = in.next();
        if (number.equals(currentCard.getNumber())) {
            System.out.println("You can't transfer money to the same account!");
        } else if (!checkSum(number)) {
            System.out.println("Probably you made a mistake in the card number. Please try again!");
        } else {
            dataBase.transaction(currentCard, number);
        }
    }

    private boolean checkSum(String number) {
        int lastNum = Integer.parseInt(String.valueOf(number.toCharArray()[number.length() - 1]));
        number = number.substring(0, number.length() - 1);
        return Card.luhnAlgorithm(number) == lastNum;
    }

    private void logIn() {
        System.out.println("Enter your card number:");
        String number = in.next();
        System.out.println("Enter your PIN:");
        String pin = in.next();

        try (Connection con = dataBase.getConnection()) {
            currentCard = new Card(con, number, pin);
            if (currentCard.isLog()) {
                System.out.println("You have successfully logged in!");
                account();
                loggedIn = true;
            } else {
                System.out.println("Wrong card number or PIN!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (runLoop) {
            if (loggedIn) {
                account();
            } else {
                menu();
            }
        }
    }
}

package banking;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class Card {

    private String number;
    private String pin;
    private int balance;
    private boolean log = true;

    public Card() {
        number = generateNumber();
        pin = generatePin();
        balance = 0;
    }

    public Card(Connection con, String number, String pin) {
        String sql = "SELECT balance FROM card WHERE number = ? AND pin = ?";
        try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            preparedStatement.setString(1, number);
            preparedStatement.setString(2, pin);
            try (ResultSet cards = preparedStatement.executeQuery()) {
                if (cards.next()) {
                    balance = cards.getInt("balance");
                } else {
                    log = false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        this.number = number;
        this.pin = pin;
    }

    public String getNumber() {
        return number;
    }

    public String getPin() {
        return pin;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public boolean isLog() {
        return log;
    }

    public static int luhnAlgorithm(String number) {
        int checkSum = 0;
        int[] numArr = Arrays.stream(number.split(""))
                .mapToInt(Integer::parseInt)
                .toArray();
        for (int i = 0; i < numArr.length; i += 2) {
            numArr[i] *= 2;
        }
        for (int i = 0; i < numArr.length; i++) {
            if (numArr[i] > 9) {
                numArr[i] -= 9;
            }
        }
        int sum = Arrays.stream(numArr).sum();
        if (sum % 10 != 0) {
            checkSum = 10 - sum % 10;
        }
        return checkSum;
    }

    private String generatePin() {
        String pin = "";
        for (int i = 0; i < 4; i++) {
            int rand_num = (int) (Math.random() * 10);
            pin += Integer.toString(rand_num);
        }
        return pin;
    }

    private String generateNumber() {
        String number = "400000";
        for (int i = 0; i < 9; i++) {
            int rand_num = (int) (Math.random() * 10);
            number += Integer.toString(rand_num);
        }
        number += Integer.toString(luhnAlgorithm(number));
        return number;
    }

    @Override
    public String toString() {
        return "Your card number:\n" + number +
                "\nYour card PIN:\n" + pin;
    }
}

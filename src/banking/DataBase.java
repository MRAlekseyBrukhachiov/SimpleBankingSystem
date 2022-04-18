package banking;

import org.sqlite.SQLiteDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class DataBase {

    private SQLiteDataSource dataSource;
    private int count = 0;

    public DataBase(String url) {
        String sql = "CREATE TABLE IF NOT EXISTS card (id INTEGER, number TEXT, pin TEXT, balance INTEGER DEFAULT 0)";
        dataSource = new SQLiteDataSource();
        dataSource.setUrl(url);
        try (Connection con = dataSource.getConnection();
             PreparedStatement preparedStatement = con.prepareStatement(sql))
        {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createCard() {
        String sql = "INSERT INTO card (number, pin, id) VALUES (? , ?, ?)";
        Card a = new Card();
        try (Connection con = dataSource.getConnection();
             PreparedStatement preparedStatement = con.prepareStatement(sql))
        {
            preparedStatement.setString(1, a.getNumber());
            preparedStatement.setString(2, a.getPin());
            preparedStatement.setInt(3, count);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        count++;
        System.out.println("Your card has been created");
        System.out.println(a);
    }

    public void addInCome(Card currentCard, int cash) {
        try (Connection con = dataSource.getConnection()) {
            addCash(con, currentCard.getNumber(), cash);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Income was added!");
    }

    public void closeAccount(Card currentCard) {
        String sql = "DELETE FROM card WHERE number = ?";
        try (Connection con = dataSource.getConnection();
             PreparedStatement preparedStatement = con.prepareStatement(sql))
        {
            preparedStatement.setString(1, currentCard.getNumber());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("The account has been closed!");
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void transaction(Card card, String number) {
        Scanner in = new Scanner(System.in);
        String getAccountSQL = "SELECT * FROM card WHERE number = ?";
        try (Connection con = dataSource.getConnection();
             PreparedStatement getAccount = con.prepareStatement(getAccountSQL)) {
            getAccount.setString(1, number);

            try (ResultSet cards = getAccount.executeQuery()) {
                if (cards.next()) {
                    System.out.println("Enter how much money you want to transfer:");
                    int cash = in.nextInt();
                    int balance = card.getBalance();
                    if (cash <= balance) {
                        addCash(con, number, cash);
                        takeCash(con, card.getNumber(), cash);
                        System.out.println("Success!");
                    } else {
                        System.out.println("Not enough money!");
                    }
                } else {
                    System.out.println("Such a card does not exist.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addCash(Connection con, String number, int cash) {
        String sql = "UPDATE card SET balance = balance + ? WHERE number = ?";
        updateBalance(con, number, cash, sql);
    }

    public void takeCash(Connection con, String number, int cash) {
        String sql = "UPDATE card SET balance = balance - ? WHERE number = ?";
        updateBalance(con, number, cash, sql);
    }

    public void updateBalance(Connection con, String number, int cash, String sql) {
        try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            preparedStatement.setInt(1, cash);
            preparedStatement.setString(2, number);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

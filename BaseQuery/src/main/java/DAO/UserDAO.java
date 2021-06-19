package DAO;

import java.io.IOException;
import java.sql.*;

public class UserDAO implements DAO {

    GetConfigSQL getConfig = new GetConfigSQL();
    final Connection connection = DriverManager.getConnection(getConfig.getUrl(), getConfig.getUserSQL(), getConfig.getPassSQL());

    public UserDAO() throws SQLException, IOException {
    }

    @Override
    public String read(Integer value) throws SQLException {
        String result = null;
        try (PreparedStatement statement = connection.prepareStatement(SQLInquiry.GETMessage.Inquiry)) {
            statement.setInt(1, value);
            final ResultSet rs = statement.executeQuery();
            result = "--- Двадцать последних сообщений ---\n";
            if (rs.next()) {
                result += rs.getString("message");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        connection.close();
        return result;
    }

    @Override
    public void post(String text) throws SQLException {

    }

    @Override
    public boolean authorisation(String login, Integer hashpass, Integer hashsalt) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQLInquiry.GETUser.Inquiry)) {
            statement.setString(1, login);
            statement.setInt(2, hashpass);
            statement.setInt(3, hashsalt);
            System.out.println(statement);
            final ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                System.out.println(rs.getString("login") + " " + rs.getInt("pass") + " " + rs.getInt("passwithsalt"));
            }
            if (login.equals(rs.getString("login")) && hashpass == rs.getInt("pass") && hashsalt == rs.getInt("passwithsalt")) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException throwables) {
            System.out.println("Такого пользователя нема");
        }
        return false;
    }


    enum SQLInquiry {
        GETUser("SELECT * FROM users WHERE  login = (?) AND pass = (?) AND passwithsalt = (?)"),
        GETMessage("SELECT * FROM messagehistory ORDER BY id DESC LIMIT 0,20");
        String Inquiry;

        SQLInquiry(String Inquiry) {
            this.Inquiry = Inquiry;
        }

    }

}

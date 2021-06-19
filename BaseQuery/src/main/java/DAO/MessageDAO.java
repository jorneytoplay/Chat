package DAO;

import java.io.IOException;
import java.sql.*;

public class MessageDAO implements DAO {


    private static final char RS = 0x1E;
    GetConfigSQL getConfig = new GetConfigSQL();
    final Connection connection = DriverManager.getConnection(getConfig.getUrl(), getConfig.getUserSQL(), getConfig.getPassSQL());

    public MessageDAO() throws SQLException, IOException {
    }

    @Override
    public String read(Integer value) throws SQLException {
        String result = null;
        try (PreparedStatement statement = connection.prepareStatement(SQLInquiry.GETMessage.Inquiry)) {
            final ResultSet rs = statement.executeQuery();
            result = "--- Двадцать последних сообщений ---" + RS;
            for (int i = 0; i < 20; i++) {
                if (rs.next()) {
                    result += rs.getString("message") + RS;
                }
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            connection.close();
        }
        System.out.println(result);
        return result;
    }

    @Override
    public void post(String text) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQLInquiry.POSTMessage.Inquiry)) {
            text = text.replaceAll(String.valueOf(RS), "");
            statement.setString(1, text);
            statement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            connection.close();
        }
    }


    @Override
    public boolean authorisation(String login, Integer hashpass, Integer hashsalt) {
        return false;
    }


    enum SQLInquiry {
        GETUser("SELECT * FROM users WHERE  login = (?)"),
        GETMessage("SELECT * FROM (SELECT * from messagehistory ORDER BY id DESC LIMIT 20) AS T ORDER BY id ASC"),
        POSTMessage("INSERT INTO messagehistory (message) values (?)");
        String Inquiry;

        SQLInquiry(String Inquiry) {
            this.Inquiry = Inquiry;
        }

    }

}
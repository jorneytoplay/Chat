import org.junit.jupiter.api.Test;

import java.sql.*;

public class TestMessageRequest {
    private static final char RS = 0x1E;
    private String userSQL = "boss";
    private String passwordSQL = "boss";
    private String url = "jdbc:postgresql://localhost:5432/chat";
    final Connection connection = DriverManager.getConnection(url, userSQL, passwordSQL);

    public TestMessageRequest() throws SQLException {
    }

    @Test
    public String read() throws SQLException {
        String result = null;
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM (SELECT * from messagehistory ORDER BY id DESC LIMIT 20) AS T ORDER BY id ASC")) {
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


}

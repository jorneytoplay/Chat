import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

public class TestSQLRequests {
    TestMessageRequest testMessageRequest = new TestMessageRequest();

    public TestSQLRequests() throws SQLException {
    }

    @Test
    public static void main(String[] args) throws SQLException {
        TestSQLRequests testSQLRequests = new TestSQLRequests();
        String first = testSQLRequests.getMessage();
        String second = testSQLRequests.getMessage();
        Assertions.assertEquals(first, second);
    }

    @Test
    public String getMessage() throws SQLException {
        return testMessageRequest.read();
    }
}


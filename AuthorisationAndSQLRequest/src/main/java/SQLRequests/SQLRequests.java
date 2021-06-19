package SQLRequests;

import DAO.MessageDAO;
import DAO.UserDAO;

import java.io.IOException;
import java.sql.SQLException;

public class SQLRequests {
    UserDAO userDAO = new UserDAO();
    MessageDAO messageDAO = new MessageDAO();
    HashCoding hashCoding = new HashCoding();

    public SQLRequests() throws SQLException, IOException {
    }

    public boolean authorisation(String login, String pass) throws SQLException {
        int hash = hashCoding.GetHashCode(pass);
        int hashsalt = hashCoding.GetHashCodeSalt(hash);
        return userDAO.authorisation(login, hash, hashsalt);
    }

    public String getMessage(Integer count) throws SQLException {
        return messageDAO.read(count);
    }

    public void postMessage(String message) throws SQLException {
        messageDAO.post(message);
    }
}

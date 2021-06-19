package DAO;

import java.sql.SQLException;

public interface DAO {
    String read(Integer value) throws SQLException;

    void post(String text) throws SQLException;

    boolean authorisation(String login, Integer hashpass, Integer hashsalt) throws SQLException;
}

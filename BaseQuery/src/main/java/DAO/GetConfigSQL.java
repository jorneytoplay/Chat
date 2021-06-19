package DAO;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class GetConfigSQL {
    Properties properties = new Properties();
    FileInputStream config = new FileInputStream("Data.properties");

    public GetConfigSQL() throws FileNotFoundException {
    }

    public String getUrl() throws IOException {
        properties.load(config);
        String url = properties.getProperty("url");
        return url;
    }

    public String getUserSQL() throws IOException {
        properties.load(config);
        String userSQL = properties.getProperty("userSQL");
        return userSQL;
    }

    public String getPassSQL() throws IOException {
        properties.load(config);
        String passwordSQL = properties.getProperty("passwordSQL");
        return passwordSQL;
    }

}

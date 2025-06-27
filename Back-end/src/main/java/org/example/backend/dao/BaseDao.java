package org.example.backend.dao;

import java.sql.*;
import javax.sql.DataSource;
import javax.naming.*;

public class BaseDao {
    DataSource dataSource;
    Connection connection;

    public BaseDao() {
        try {
            Context context = new InitialContext();
            dataSource = (DataSource) context.lookup("java:comp/env/jdbc/sampleDS");
        }catch (NamingException ne) {
            System.out.println("Exception:"+ne);
        }
    }

    public void lookupConnection() throws Exception {
        connection = dataSource.getConnection();
    }
    public void releaseConnection() throws Exception {
        connection.setAutoCommit(true);
        connection.close();
    }

    public Connection getConnection() throws Exception {
        return connection;
    }
}

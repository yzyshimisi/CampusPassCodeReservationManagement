// File: src/main/java/org/example/backend/dao/BaseDao.java
package org.example.backend.dao;

import org.example.backend.utils.ConnUtils;
import org.example.backend.utils.OperatorContext;
import java.sql.SQLException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;

public class BaseDao {

    protected DataSource dataSource;
    protected Connection connection;

    public BaseDao() {
        try {
            Context ctx = new InitialContext();
            dataSource = (DataSource) ctx.lookup("java:comp/env/jdbc/sampleDS");
        } catch (NamingException ne) {
            throw new RuntimeException("获取数据源失败", ne);
        }
    }

    /** 业务代码先调用；自动把 operatorId 写进 PostgreSQL 会话变量 */
    public void lookupConnection() throws Exception {
        connection = dataSource.getConnection();

        Integer opId = OperatorContext.get();
        if (opId != null) {
            ConnUtils.setOperatorId(connection, opId);
        }
    }

    public void releaseConnection() throws Exception {
        if (connection != null) connection.close();
    }
    public Connection getConnection() throws SQLException {
        Connection conn = dataSource.getConnection();
        Integer opId = ConnUtils.getOperatorId();
        if (opId != null) {
            ConnUtils.setOperatorId(conn, opId);
        }
        return conn;
    }
}

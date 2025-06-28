package org.example.backend.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public final class ConnUtils {
    private ConnUtils() {}

    // -------- ThreadLocal 部分 --------
    private static final ThreadLocal<Integer> CURRENT_OPERATOR = new ThreadLocal<>();

    /** 在 Filter 成功解析 JWT 后调用 */
    public static void bindOperator(Integer id) {
        CURRENT_OPERATOR.set(id);
    }

    /** DAO 层获取 */
    public static Integer getOperatorId() {
        System.out.println("[GET] 当前操作者 ID = " + CURRENT_OPERATOR.get());
        return CURRENT_OPERATOR.get();
    }

    /** 请求结束后调用，防止线程复用串号 */
    public static void clearOperator() {
        CURRENT_OPERATOR.remove();
    }

    // -------- 写入 PG 会话变量部分 --------
    public static void setOperatorId(Connection conn, int operatorId) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            String sql = "SET LOCAL app.operator_id = " + operatorId;
            System.out.println("[SET LOCAL] operator_id = " + operatorId);
            stmt.execute(sql);
        }
    }
}
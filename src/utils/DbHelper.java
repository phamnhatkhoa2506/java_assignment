package utils;

import java.sql.*;

/**
 * DbHelper: Simplest JDBC helper with default auto-commit.
 */
public class DbHelper {
    private static final String URL = "jdbc:mysql://localhost:3306/cinema_booking?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASS = "vonuhuyentran1202";

    // Load JDBC driver
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found.");
            e.printStackTrace();
        }
    }

    /**
     * Opens a new connection (auto-commit = true by default).
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    /**
     * Executes INSERT, UPDATE, or DELETE. Returns number of affected rows, or -1 on error.
     */
    public static int executeUpdate(String sql, Object... params) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            setParameters(stmt, params);
            int affected = stmt.executeUpdate();
            System.out.println("[DbHelper] executeUpdate -> sql: " + sql + ", params: " + java.util.Arrays.toString(params) + ", rows: " + affected);
            return affected;
        } catch (SQLException e) {
            System.err.println("[DbHelper] Error executing update: " + sql + ", params=" + java.util.Arrays.toString(params));
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Executes SELECT. Caller must close resources via closeQuietly().
     */
    public static ResultSet executeQuery(String sql, Object... params) throws SQLException {
        Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        setParameters(stmt, params);
        System.out.println("[DbHelper] executeQuery -> sql: " + sql + ", params: " + java.util.Arrays.toString(params));
        return stmt.executeQuery();
    }

    /**
     * Closes Connection, Statement, and ResultSet quietly.
     */
    public static void closeQuietly(Connection conn, Statement stmt, ResultSet rs) {
        try { if (rs   != null) rs.close();   } catch (SQLException ignored) {}
        try { if (stmt != null) stmt.close(); } catch (SQLException ignored) {}
        try { if (conn != null) conn.close(); } catch (SQLException ignored) {}
    } 

    /**
     * Helper to bind parameters to PreparedStatement.
     */
    private static void setParameters(PreparedStatement stmt, Object... params) throws SQLException {
        if (params == null) return;
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }
    }

    /**
     * Basic test of insert functionality.
     */
    public static void main(String[] args) {
        String sql = "INSERT INTO users(id, username, password, email, firstname, lastname, birthday) VALUES(?, ?, ?, ?, ?, ?, ?)";
        Object[] params = { java.util.UUID.randomUUID().toString(), "haha", "pwd", "test@domain.com", "Test", "User", Date.valueOf(java.time.LocalDate.of(2000,1,1)) };
        int r = executeUpdate(sql, params);
        System.out.println("Test insert returned: " + r);
    }
}

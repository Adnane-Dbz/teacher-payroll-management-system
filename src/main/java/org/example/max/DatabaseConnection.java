package org.example.max;

















//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.SQLException;
//import java.util.Properties;
//
//public class DatabaseConnection {
//    private static String user;
//    private static String password;
//    private static String port;
//    private static String url;
//
//    public static void initialize(String user, String password, String port) {
//        DatabaseConnection.user = user;
//        DatabaseConnection.password = password;
//        DatabaseConnection.port = port;
//        url = "jdbc:mysql://127.0.0.1:" + port + "/salary?user=" + user + "&password=" + password;
//    }
//
//    public static Connection getConnection() throws SQLException {
//        return DriverManager.getConnection(url);
//    }
//
//
//    public static void main(String[] args) {
//        // Load saved configuration
//        DatabaseConfigApp configApp = new DatabaseConfigApp();
//        Properties config = configApp.loadConfig();
//
//        // Initialize database connection details
//        initialize(config.getProperty("user", ""), config.getProperty("password", ""), config.getProperty("port", "3306"));
//
//        // Test connection
//        try {
//            Connection connection = getConnection();
//            if (connection != null) {
//                System.out.println("Connected to the database!");
//                connection.close();
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//            System.out.println("Failed to connect to the database.");
//        }
//    }
//}

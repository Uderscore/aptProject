package utilities;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConnectToDB {

    // JDBC URL, Username, Password
    private static final String URL = "jdbc:mysql://localhost:3306/your_database_name";
    private static final String USER = "your_db_user";
    private static final String PASSWORD = "your_db_password";

    // JDBC Connection object
    private static Connection connection = null;

    // Static block to initialize connection
    static {
        try {
            // Register JDBC driver (if needed for your DB)
            Class.forName("com.mysql.cj.jdbc.Driver"); // Change to the correct driver if needed
            // Open a connection
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error connecting to the database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Method to fetch text data from the database by URL (or document ID)
    public static String getText(String url) {
        String query = "SELECT content_column FROM documents WHERE url = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, url);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString("content_column"); // Adjust to match your column name
            }
        } catch (SQLException e) {
            System.err.println("Error fetching text from database: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // Optional: You could also add other methods like `getAllDocuments()`,
    // `getDocumentById()`, etc.
    // Example for getting all URLs for a country:
    public static List<String> getAllUrlsForCountry(String country) {
        List<String> urls = new ArrayList<>();
        String query = "SELECT url FROM documents WHERE country = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, country);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                urls.add(resultSet.getString("url")); // Adjust column name if necessary
            }
        } catch (SQLException e) {
            System.err.println("Error fetching URLs for country: " + e.getMessage());
            e.printStackTrace();
        }
        return urls;
    }

    // Close connection when done (useful when shutting down the application)
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing the connection: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:sqlite:digital_kunstler_portal.db";
    private static DatabaseConnection instance;
    private Connection connection;


    private DatabaseConnection(){
        try{
            Class.forName("org.sqlite.JDBC");

        } catch (ClassNotFoundException e) {
            System.err.println("sqlite couldn't started: "+e.getMessage());
        }
    }

    public static DatabaseConnection getInstance(){
        if (instance==null){
            instance=new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {

        if (connection == null || connection.isClosed()){
            connection= DriverManager.getConnection(URL);
        }
        return connection;
    }

    public void closeConnection(){
        try{
            if(connection != null && !connection.isClosed()){
                connection.close();
                System.out.println("DatabaseConnection successfully closed.");
            }
        }catch (SQLException e){
            System.err.println("Database Connection couldn't closed: "+ e.getMessage());
        }

    }
}

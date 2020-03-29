package com.mikpuk.vava_project.db_things;

import com.mikpuk.vava_project.db_things.MD5Hashing;

import java.sql.*;

public class SQLConnector {
    private Connection connection = null;
    private Statement statement = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;

    //Nacitanie drivera a pripojenie k databaze
    public void connectToDB()
    {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found!");
            e.printStackTrace();
            return;
        }

        try {
            //Amazon databaza
            connection = DriverManager
                    .getConnection("jdbc:mysql://vava-db.ctknqglftm5b.eu-central-1.rds.amazonaws.com/vavaDB?characterEncoding=latin1",
                            "masteradmin", "vavadatabaza123.");
        } catch (SQLException e) {
            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();
            return;
        }

        //Debug If, neskor zmazat
        if (connection != null) {
            System.out.println("Connection established");
        } else {
            System.out.println("Failed to make connection!");
        }
    }

    //Skontroluje, ci sme pripojeny k databaze
    public boolean isConnectedToDB()
    {
        return connection != null;
    }

    public Connection getConnection()
    {
        return connection;
    }

    //Zavrie otvorene pripojenia
    public void closeConnection() {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (Exception e) {
            System.out.println("Error closing connection");
            e.printStackTrace();
        }
        System.out.println("Connections closed");
    }
}
package com.mikpuk.vava_project;

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

    //Prida pouzivate do databazy.
    //Vrati true ak bola akcia uspesna inak false
    public boolean addUserToDB(String username, String password)
    {
        if(connection == null) {return false;}

        try {
            preparedStatement = connection
                    .prepareStatement("INSERT INTO users (USERNAME,PASSWORD,REPUTATION) VALUES (?,?,0)");
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, MD5Hashing.getSecurePassword(password));
            preparedStatement.executeUpdate();

            //MD5 Testing
            System.out.println("Pass check : "+MD5Hashing.getSecurePassword(password) + " | "+ MD5Hashing.getSecurePassword(password));

            System.out.println("User "+username+" added.");
        } catch (SQLException e) {
            System.out.println("Problem with adding user, check logs");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    //Skontroluje, ci sa v DB nachadza dany pouzivatel aj s heslom
    public int getUserInDB(String username, String password)
    {
        if(connection == null) {    return -1;}
        try {
            preparedStatement = connection
                    .prepareStatement("SELECT * FROM users WHERE USERNAME = ?");
            preparedStatement.setString(1, username);
            resultSet = preparedStatement.executeQuery();

            System.out.println("Checking if user exists");

            //Porovnava mena kedze select je case insensitive
            while(resultSet.next())
            {
                String recievedPass = resultSet.getString("PASSWORD");
                String recievedName = resultSet.getString("USERNAME");

                if(recievedPass.equals(MD5Hashing.getSecurePassword(password))
                    && username.equals(recievedName)) {
                    return resultSet.getInt("ID");
                }
            }
        } catch (SQLException e) {
            System.out.println("Problem with checking user, check logs");
            e.printStackTrace();
            return -1;
        }

        return -1;
    }

    //Zmaze cely obsah tabulky - iba na test
    public void deleteUserDB()
    {
        try {
            statement = connection.createStatement();

            statement.execute("DELETE from users");
            //Reset autoincrement
            statement.execute("ALTER TABLE vavaDB.users AUTO_INCREMENT = 1;");

            System.out.println("USERS TABLE DELETED");

        } catch (SQLException e) {
            System.out.println("Problem with deleting users table, check logs");
            e.printStackTrace();
        }
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
package com.mikpuk.vava_project.db_things;

import java.sql.*;

public class SQLQueries {

    private static Statement statement = null;
    private static PreparedStatement preparedStatement = null;
    private static ResultSet resultSet = null;

    //Prida pouzivate do databazy.
    //Vrati true ak bola akcia uspesna inak false
    public static boolean registerUser(String username, String password,Connection connection)
    {
        if(connection == null) {return false;}

        try {
            preparedStatement = connection
                    .prepareStatement("INSERT INTO users (USERNAME,PASSWORD,REPUTATION) VALUES (?,?,0)");
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, MD5Hashing.getSecurePassword(password));
            preparedStatement.executeUpdate();

            System.out.println("User "+username+" added.");
        } catch (SQLException e) {
            System.out.println("Problem with adding user, check logs");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    //Skontroluje, ci sa v DB nachadza dany pouzivatel aj s heslom
    public static int getUserID(String username, String password,Connection connection)
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
    public static void deleteUserDB(Connection connection)
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
}

package com.company;

import java.sql.*;
import java.util.Scanner;

public class Topic {
    String TopicName;

    private Topic(String TopicName) {
        this.TopicName = TopicName;
    }

    public void Insert() throws ClassNotFoundException, SQLException { //Inserts the new Topic data into the database.
        Class.forName("com.mysql.cj.jdbc.Driver"); //Driver Used to access MySQL from JAVA :)))))
        Connection connect = DriverManager.getConnection("jdbc:mysql://localhost:3307/MASTERCAMPTP4", "root", "Steph1!!!!"); //Connecting to the Database
        Statement statement = connect.createStatement(); //Creating the object *statement* that will be used to Query
        statement.executeUpdate("INSERT INTO Topic (TopicName) VALUES ('" + TopicName + "');");
    }

    public int Find_Topic() throws ClassNotFoundException, SQLException { //Tries to return the ID of the queried Topic. If the Topic cannot be found, it creates it and inserts it into the database, before sending its ID back.
        Class.forName("com.mysql.cj.jdbc.Driver"); //Driver Used to access MySQL from JAVA :)))))
        Connection connect = DriverManager.getConnection("jdbc:mysql://localhost:3307/MASTERCAMPTP4", "root", "Steph1!!!!"); //Connecting to the Database
        ResultSet resultSet = null;
        Statement statement = connect.createStatement();
        do {
            resultSet = statement.executeQuery("SELECT TopicID FROM Topic WHERE TopicName = \"" + TopicName + "\";");
            if (!resultSet.isBeforeFirst()) { //We check if the above query returns an ID
                Insert(); //If the Tag doesn't exist, the program automatically makes a new one.
            }
        } while (!resultSet.isBeforeFirst());
        resultSet.next();
        return resultSet.getInt("TopicID"); //Once the Topic exists, the TopicID is returned.
    }

    public static Topic Generate_Topic(String TopicName) throws SQLException, ClassNotFoundException { //Tries to load the topics from their names. If the topics don't exist, they are then created.
        Topic Self = new Topic(TopicName);
        Self.Find_Topic();
        return Self;
    }

    public static Topic Load_Topic(int TopicID) throws ClassNotFoundException, SQLException { //Loads the Topic directly from the database.
        Class.forName("com.mysql.cj.jdbc.Driver"); //Driver Used to access MySQL from JAVA :)))))
        Connection connect = DriverManager.getConnection("jdbc:mysql://localhost:3307/MASTERCAMPTP4", "root", "Steph1!!!!"); //Connecting to the Database
        ResultSet resultSet = null;
        Statement statement = connect.createStatement();
        resultSet = statement.executeQuery("SELECT * FROM Topic WHERE TopicID = " + TopicID + ";");
        resultSet.next();
        return new Topic(resultSet.getString("TopicName"));
    }

    public static Topic Create_Topic() { //Creates a Topic instance from scratch, with the help of the user.
        Scanner Scan = new Scanner(System.in);
        System.out.println("INSEREZ LE NOM DU TOPIC: ");
        String TopicName = Scan.next();
        return new Topic(TopicName);
    }

    public static void New_Database_Topic() throws SQLException, ClassNotFoundException { //Creates a Topic instance from scratch with the help of the user, and inserts it in the database.
        Create_Topic().Insert();
        System.out.println("VOTRE TOPIC A BIEN ETE ENREGISTRE.");
    }

    public static void Print_Topic(String Column, String Where) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver"); //Driver Used to access MySQL from JAVA :)))))
        Connection connect = DriverManager.getConnection("jdbc:mysql://localhost:3307/MASTERCAMPTP4", "root", "Steph1!!!!"); //Connecting to the Database
        ResultSet resultSet = null;
        Statement statement = connect.createStatement();
        resultSet = statement.executeQuery("SELECT " + Column + " FROM Topic JOIN Document ON Topic.TopicID = Document.TopicID JOIN Category ON Document.CategoryID = Category.CategoryID " + Where);
        while (resultSet.next()) {
            try {
                System.out.println("\nTopic = " + resultSet.getString("TopicName"));
                System.out.println("TopicID = " + resultSet.getString("TopicID"));
                System.out.println("DocumentID = " + resultSet.getInt("DocumentID"));
                System.out.println("Document Name = " + resultSet.getString("DocumentName"));
                System.out.println("Document Date = " + resultSet.getString("DocumentDate"));
                System.out.println("Storage Address = " + resultSet.getString("StorageAddress"));
                System.out.println("Category = " + resultSet.getString("CategoryName"));
            } catch (Exception ignore) {}
            try {
                System.out.println("COUNT(Topic.TopicID) = " + resultSet.getString("COUNT(Topic.TopicID)"));
            } catch (Exception ignore) {}
        }
    }

    //---------------------------------------------------------------------------- AUTO PART ----------------------------------------------------------------------------//

    public static void Auto_Print_Document_By_Topic() throws SQLException, ClassNotFoundException {
        Print_Topic("Topic.TopicID, TopicName, DocumentName, DocumentID", "ORDER BY TopicID");
    }

    public static void Auto_Print_Most_Used_Topic() throws SQLException, ClassNotFoundException {
        Print_Topic("TopicName, COUNT(Topic.TopicID)", "GROUP BY TopicName ORDER BY COUNT(Topic.TopicID) DESC Limit 1");
    }
}

package com.company;

import java.sql.*;
import java.util.Scanner;

public class Category {
    String CategoryName;

    private Category(String CategoryName) {
        this.CategoryName = CategoryName;
    }

    public void Insert() throws ClassNotFoundException, SQLException { //Inserts the new Category data into the database.
        Class.forName("com.mysql.cj.jdbc.Driver"); //Driver Used to access MySQL from JAVA :)))))
        Connection connect = DriverManager.getConnection("jdbc:mysql://localhost:3307/MASTERCAMPTP4", "root", "Steph1!!!!"); //Connecting to the Database
        Statement statement = connect.createStatement(); //Creating the object *statement* that will be used to Query
        statement.executeUpdate("INSERT INTO Category (CategoryName) VALUES ('" + CategoryName + "');");
    }

    public int Find_Category() throws ClassNotFoundException, SQLException { //Tries to return the ID of the queried Category. If the Category cannot be found, it creates it and inserts it into the database, before sending its ID back.
        Class.forName("com.mysql.cj.jdbc.Driver"); //Driver Used to access MySQL from JAVA :)))))
        Connection connect = DriverManager.getConnection("jdbc:mysql://localhost:3307/MASTERCAMPTP4", "root", "Steph1!!!!"); //Connecting to the Database
        ResultSet resultSet = null;
        Statement statement = null;
        statement = connect.createStatement();
        do {
            resultSet = statement.executeQuery("SELECT CategoryID FROM Category WHERE CategoryName = '" + CategoryName + "';");
            if (!resultSet.isBeforeFirst()) {
                Insert();
            }
        } while (!resultSet.isBeforeFirst());
        resultSet.next();
        return resultSet.getInt("CategoryID");
    }

    public static Category Generate_Category(String CategoryName) throws SQLException, ClassNotFoundException { //Tries to load the Categories from their names. If the Categories don't exist, they are then created.
        Category Self = new Category(CategoryName);
        Self.Find_Category();
        return Self;
    }

    public static Category Load_Category(int CategoryID) throws ClassNotFoundException, SQLException { //Loads the Category directly from the database.
        Class.forName("com.mysql.cj.jdbc.Driver"); //Driver Used to access MySQL from JAVA :)))))
        Connection connect = DriverManager.getConnection("jdbc:mysql://localhost:3307/MASTERCAMPTP4", "root", "Steph1!!!!"); //Connecting to the Database
        ResultSet resultSet = null;
        Statement statement = connect.createStatement();
        resultSet = statement.executeQuery("SELECT * FROM Category WHERE CategoryID = " + CategoryID + ";");
        resultSet.next();
        return new Category(resultSet.getString("CategoryName"));
    }

    public static Category Create_Category() throws SQLException, ClassNotFoundException { //Creates a Category instance from scratch, with the help of the user.
        Scanner Scan = new Scanner(System.in);
        System.out.println("INSEREZ LE NOM DE LA CATEGORIE: ");
        String CategoryName = Scan.next();
        return new Category(CategoryName);
    }

    public static void New_Database_Category() throws SQLException, ClassNotFoundException { //Creates a Category instance from scratch with the help of the user, and inserts it in the database.
        Create_Category().Insert();
        System.out.println("VOTRE CATEGORIE A BIEN ETE ENREGISTREE.");
    }

    public static void Print_Category(String Column, String Where) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver"); //Driver Used to access MySQL from JAVA :)))))
        Connection connect = DriverManager.getConnection("jdbc:mysql://localhost:3307/MASTERCAMPTP4", "root", "Steph1!!!!"); //Connecting to the Database
        ResultSet resultSet = null;
        Statement statement = connect.createStatement();
        resultSet = statement.executeQuery("SELECT " + Column + " FROM Category JOIN Document ON Document.CategoryID = Category.CategoryID JOIN Topic ON Topic.TopicID = Document.TopicID " + Where);
        while (resultSet.next()) {
            System.out.println("\nCategory = " + resultSet.getString("CategoryName"));
            System.out.println("CategoryID = " + resultSet.getString("CategoryID"));
            System.out.println("DocumentID = " + resultSet.getInt("DocumentID"));
            System.out.println("Document Name = " + resultSet.getString("DocumentName"));
            System.out.println("Document Date = " + resultSet.getString("DocumentDate"));
            System.out.println("Storage Address = " + resultSet.getString("StorageAddress"));
            System.out.println("Topic = " + resultSet.getString("TopicName"));
        }
    }

    //---------------------------------------------------------------------------- AUTO PART ----------------------------------------------------------------------------//

    public static void Auto_Print_Document_By_Category() throws SQLException, ClassNotFoundException {
        Print_Category("CategoryName, Category.CategoryID, DocumentName, DocumentID, DocumentDate, StorageAddress, TopicName", "ORDER BY CategoryID");
    }
}

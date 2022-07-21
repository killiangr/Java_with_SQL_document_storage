package com.company;

import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Document {
    String DocumentName;
    String DocumentDate;
    String StorageAddress;
    Category Category;
    Topic Topic;
    ArrayList<Tag> Tags = new ArrayList<>();

    private Document(String DocumentName, String DocumentDate, String StorageAddress, Category Category, Topic Topic, ArrayList<Tag> Tags) {
        this.DocumentName = DocumentName;
        this.DocumentDate = DocumentDate;
        this.StorageAddress = StorageAddress;
        this.Category = Category;
        this.Topic = Topic;
        this.Tags = Tags;
    }

    public void Insert() throws ClassNotFoundException, SQLException { //Inserts the new document data into the database, including the associated tags.
        Class.forName("com.mysql.cj.jdbc.Driver"); //Driver Used to access MySQL from JAVA :)))))
        Connection connect = DriverManager.getConnection("jdbc:mysql://localhost:3307/MASTERCAMPTP4", "root", "Steph1!!!!"); //Connecting to the Database
        Statement statement = connect.createStatement(); //Creating the object *statement* that will be used to Query
        statement.executeUpdate("INSERT INTO Document (DocumentName, DocumentDate, StorageAddress, CategoryID, TopicID) VALUES ('" + DocumentName + "'," + DocumentDate + ",'" + StorageAddress + "'," + Category.Find_Category() + "," + Topic.Find_Topic() + ");");
        for (Tag All_Tags : Tags) {
            try {
                statement.executeUpdate("INSERT INTO Detenir (DocumentID, TagID) VALUES (" + Find_Document() + "," + All_Tags.Find_Tag() + ");"); //Associates the Document with all the tags
            } catch (Exception ignore) {}
        }
    }

    public int Find_Document() throws ClassNotFoundException, SQLException { //Tries to return the ID of the queried document. If the document cannot be found, it creates it and inserts it into the database, before sending its ID back.
        Class.forName("com.mysql.cj.jdbc.Driver"); //Driver Used to access MySQL from JAVA :)))))
        Connection connect = DriverManager.getConnection("jdbc:mysql://localhost:3307/MASTERCAMPTP4", "root", "Steph1!!!!"); //Connecting to the Database
        ResultSet resultSet;
        Statement statement = connect.createStatement();
        do {
            resultSet = statement.executeQuery("SELECT DocumentID FROM Document WHERE DocumentName = '" + DocumentName + "' AND StorageAddress = '" + StorageAddress + "';");
            if (!resultSet.isBeforeFirst()) { //We check if the above query returns an ID
                System.out.println("VOTRE DOCUMENT N'EXISTE PAS! CREEZ EN UN NOUVEAU: "); //If the document doesn't exist, the user is prompted to create a new one.
                New_Database_Document(DocumentName, StorageAddress);
            }
        } while (!resultSet.isBeforeFirst());
        resultSet.next();
        return resultSet.getInt("DocumentID"); //Once the document exists, the DocumentID is returned.
    }

    public static Document Create_Document() throws SQLException, ClassNotFoundException { //Creates a document instance from scratch, with the help of the user. If any of the components don't exit, it is promptly created automatically, so there isn't any crash. It is then returned.
        Scanner Scan = new Scanner(System.in);
        System.out.print("INSEREZ LE NOM DU DOCUMENT: ");
        String DocumentName = Scan.next();
        System.out.print("INSEREZ LA DATE DU DOCUMENT: (format: <YYYY-MM-DD>)");
        String DocumentDate = Scan.next();
        DocumentDate = "STR_TO_DATE('" + DocumentDate + "', '%Y-%m-%d')";
        System.out.print("INSEREZ L'ADDRESSE DE STORAGE DU DOCUMENT: ");
        String StorageAddress = Scan.next();
        System.out.print("INSEREZ LE NOM DE LA CATEGORIE ASSOCIEE: ");
        String Category_Object = Scan.next();
        System.out.print("INSEREZ LE NOM DU TOPIC ASSOCIE: ");
        String Topic_Object = Scan.next();
        System.out.print("INSEREZ LE NOMBRE DE TAGS ASSOCIES: ");
        ArrayList<String> Tag_List = new ArrayList<>();
        for (int i = 0; i < Scan.nextInt(); i++) {
            System.out.print("INSEREZ UN TAG ASSOCIE AU DOCUMENT: ");
            Tag_List.add(Scan.next());
        }
        return new Document(DocumentName, DocumentDate, StorageAddress, com.company.Category.Generate_Category(Category_Object), com.company.Topic.Generate_Topic(Topic_Object), Tag.Generate_Tags(Tag_List));
    }

    public static Document Create_Document(String DocumentName, String StorageAddress) throws SQLException, ClassNotFoundException { //Creates a document instance from a specified Name and Storage Address. It is then returned.
        Scanner Scan = new Scanner(System.in);
        System.out.print("INSEREZ LA DATE DU DOCUMENT: (format: <YYYY-MM-DD>)");
        String DocumentDate = Scan.next();
        DocumentDate = "STR_TO_DATE('" + DocumentDate + "', '%Y-%m-%d')";
        System.out.print("INSEREZ LE NOM DE LA CATEGORIE ASSOCIEE: ");
        String Category_Object = Scan.next();
        System.out.print("INSEREZ LE NOM DU TOPIC ASSOCIE: ");
        String Topic_Object = Scan.next();
        System.out.print("INSEREZ LE NOMBRE DE TAGS ASSOCIES: ");
        ArrayList<String> Tag_List = new ArrayList<>();
        for (int i = 0; i < Scan.nextInt(); i++) {
            System.out.print("INSEREZ UN TAG ASSOCIE AU DOCUMENT: ");
            Tag_List.add(Scan.next());
        }
        return new Document(DocumentName, DocumentDate, StorageAddress, com.company.Category.Generate_Category(Category_Object), com.company.Topic.Generate_Topic(Topic_Object), Tag.Generate_Tags(Tag_List));
    }

    public static ArrayList<Tag> Find_Associated_Tags(int DocumentID) throws ClassNotFoundException, SQLException { //Find all the tags associated with the queried document, creates instances for them, and returns them in an ArrayList.
        Class.forName("com.mysql.cj.jdbc.Driver"); //Driver Used to access MySQL from JAVA :)))))
        Connection connect = DriverManager.getConnection("jdbc:mysql://localhost:3307/MASTERCAMPTP4", "root", "Steph1!!!!"); //Connecting to the Database
        ResultSet resultSet = null;
        Statement statement = connect.createStatement();
        resultSet = statement.executeQuery("SELECT * FROM Detenir WHERE DocumentID = " + DocumentID + ";"); //This query finds all the tags associated with the Document.
        ArrayList<Tag> Associated_Tags = new ArrayList<>();
        while (resultSet.next()) {
            Associated_Tags.add(Tag.Light_Load_Tag(resultSet.getInt("TagID"))); //As the tags already exist (since they are queried), we load them from the database, rather than generating them.
        }
        return Associated_Tags;
    }

    public static ArrayList<Document> Generate_Documents(ArrayList<String> Document_List) throws SQLException, ClassNotFoundException { //Tries to load the documents from their names and storage address. If the documents don't exist, they are then created.
        ArrayList<Document> Generated_Documents = new ArrayList<>();
        for (int i = 0; i < Document_List.size() - 1; i++) {
            Document Temp_Document = new Document(Document_List.get(i), null, Document_List.get(i + 1), null, null, null); //Document_List alternates, on even cells, it is the DocumentName, on odd cells, it is the Document's Storage Address.
            int DocumentID = Temp_Document.Find_Document(); //The document is then searched for in the database. If it is not found, it is created, and the new ID returned.
            Temp_Document = Load_Document(DocumentID); //Since the document is for sure in the database, we load it in the Temp_Document.
            Generated_Documents.add(Temp_Document); //And we add that document in the Arraylist.
        }
        return Generated_Documents;
    }

    public static Document Load_Document(int DocumentID) throws ClassNotFoundException, SQLException { //Loads the document directly from the database.
        Class.forName("com.mysql.cj.jdbc.Driver"); //Driver Used to access MySQL from JAVA :)))))
        Connection connect = DriverManager.getConnection("jdbc:mysql://localhost:3307/MASTERCAMPTP4", "root", "Steph1!!!!"); //Connecting to the Database
        ResultSet resultSet;
        Statement statement = connect.createStatement();
        resultSet = statement.executeQuery("SELECT * FROM Document WHERE DocumentID = " + DocumentID + ";");
        resultSet.next();
        return new Document(resultSet.getString("DocumentName"), resultSet.getString("DocumentDate"), resultSet.getString("StorageAddress"), com.company.Category.Load_Category(resultSet.getInt("CategoryID")) , com.company.Topic.Load_Topic(resultSet.getInt("TopicID")), Find_Associated_Tags(resultSet.getInt("DocumentID")));
    }

    public static Document Light_Load_Document(int DocumentID) throws ClassNotFoundException, SQLException { //Loads the document directly from the database.
        Class.forName("com.mysql.cj.jdbc.Driver"); //Driver Used to access MySQL from JAVA :)))))
        Connection connect = DriverManager.getConnection("jdbc:mysql://localhost:3307/MASTERCAMPTP4", "root", "Steph1!!!!"); //Connecting to the Database
        ResultSet resultSet = null;
        Statement statement = connect.createStatement();
        resultSet = statement.executeQuery("SELECT * FROM Document WHERE DocumentID = " + DocumentID + ";");
        resultSet.next();
        return new Document(resultSet.getString("DocumentName"), resultSet.getString("DocumentDate"), resultSet.getString("StorageAddress"), com.company.Category.Load_Category(resultSet.getInt("CategoryID")) , com.company.Topic.Load_Topic(resultSet.getInt("TopicID")), null);
    }

    public static void New_Database_Document() throws SQLException, ClassNotFoundException { //Creates a document instance from scratch with the help of the user, and inserts it in the database.
        Create_Document().Insert();
        System.out.println("VOTRE DOCUMENT A BIEN ETE ENREGISTRE.");
    }

    public static void New_Database_Document(String DocumentName, String StorageAddress) throws SQLException, ClassNotFoundException { //Creates a document instance with the name and storage address already specified, and inserts it in the database.
        Create_Document(DocumentName, StorageAddress).Insert();
        System.out.println("VOTRE DOCUMENT A BIEN ETE ENREGISTRE.");
    }

    public static void Print_Documents(String Column, String Where) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver"); //Driver Used to access MySQL from JAVA :)))))
        Connection connect = DriverManager.getConnection("jdbc:mysql://localhost:3307/MASTERCAMPTP4", "root", "Steph1!!!!"); //Connecting to the Database
        ResultSet resultSet = null;
        Statement statement = connect.createStatement();
        resultSet = statement.executeQuery("SELECT " + Column + " FROM Document JOIN Category ON Document.CategoryID = Category.CategoryID JOIN Topic ON Topic.TopicID = Document.TopicID " + Where);
        while (resultSet.next()) {
            System.out.println("\nDocumentID = " + resultSet.getInt("DocumentID"));
            System.out.println("Document Name = " + resultSet.getString("DocumentName"));
            System.out.println("Document Date = " + resultSet.getString("DocumentDate"));
            System.out.println("Storage Address = " + resultSet.getString("StorageAddress"));
            System.out.println("Category = " + resultSet.getString("CategoryName"));
            System.out.println("Topic = " + resultSet.getString("TopicName"));
            System.out.println("ASSOCIATED TAGS: ");
            Document.Print_Tags(resultSet.getInt("DocumentID")); //Because one resultset can be opened at a time, we have to use another function to read all the tags
        }
    }

    private static void Print_Tags(int DocumentID) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver"); //Driver Used to access MySQL from JAVA :)))))
        Connection connect = DriverManager.getConnection("jdbc:mysql://localhost:3307/MASTERCAMPTP4", "root", "Steph1!!!!"); //Connecting to the Database
        Statement statement = connect.createStatement();
        ResultSet Tag_resultSet = statement.executeQuery("SELECT DISTINCT TagName FROM Document JOIN Detenir ON Document.DocumentID = Detenir.DocumentID JOIN Tag ON Tag.TagID = Detenir.TagID WHERE Document.DocumentID = " + DocumentID);
        while (Tag_resultSet.next()) {
            System.out.println("TagName = " + Tag_resultSet.getString("TagName"));
        }
    }

    //---------------------------------------------------------------------------- AUTO PART ----------------------------------------------------------------------------//
    public static ArrayList<Document> Auto_Create_Document() throws SQLException, ClassNotFoundException { //In this auto method, we simply create new documents for the exercises.
        ArrayList<Document> Auto_Documents = new ArrayList<>();
        ArrayList<String> Tags = new ArrayList<>();
        Tags.add("legal");
        Tags.add("medical");
        Tags.add("administrative");
        Tags.add("technical");
        Auto_Documents.add(new Document("Document A", "STR_TO_DATE('2021-01-01', '%Y-%m-%d')", "Paris", com.company.Category.Generate_Category("policy"), com.company.Topic.Generate_Topic("Cluster Graduation Projet en 2022"), Tag.Generate_Tags(Tags)));
        Tags.clear();
        Tags.add("legal");
        Tags.add("administrative");
        Tags.add("Spy");
        Tags.add("politics");
        Auto_Documents.add(new Document("Document B", "STR_TO_DATE('2022-01-01', '%Y-%m-%d')", "Lyon", com.company.Category.Generate_Category("plan"), com.company.Topic.Generate_Topic("Spy on the white house"), Tag.Generate_Tags(Tags)));
        Tags.clear();
        Tags.add("Dangerous");
        Tags.add("Religion");
        Tags.add("administrative");
        Tags.add("Conspiracy");
        Auto_Documents.add(new Document("Document C", "STR_TO_DATE('2023-01-01', '%Y-%m-%d')", "Luxembourg", com.company.Category.Generate_Category("origin"), com.company.Topic.Generate_Topic("Find god"), Tag.Generate_Tags(Tags)));
        Tags.clear();
        Tags.add("Space");
        Tags.add("administrative");
        Tags.add("technical");
        Auto_Documents.add(new Document("Document D", "STR_TO_DATE('2024-01-01', '%Y-%m-%d')", "Vatan", com.company.Category.Generate_Category("root"), com.company.Topic.Generate_Topic("Travel to the center of the universe"), Tag.Generate_Tags(Tags)));
        Tags.clear();
        Tags.add("legal");
        Tags.add("medical");
        Tags.add("administrative");
        Tags.add("technical");
        Auto_Documents.add(new Document("Document E", "STR_TO_DATE('2025-01-01', '%Y-%m-%d')", "Vatican", com.company.Category.Generate_Category("cryogenic"), com.company.Topic.Generate_Topic("CS243 Course Files in Fall 2022"), Tag.Generate_Tags(Tags)));
        return Auto_Documents;
    }

    public static void Auto_New_Database_Document() throws SQLException, ClassNotFoundException { //Here, we create the documents needed to complete the exercise, then we print them, and delete them when we are done.
        ArrayList<Document> Auto_Documents = new ArrayList<>();
        Auto_Documents = Auto_Create_Document();
        for (Document Auto_All_Documents : Auto_Documents) {
            Auto_All_Documents.Insert();
        }
        Print_Documents("*","");
        Class.forName("com.mysql.cj.jdbc.Driver"); //Driver Used to access MySQL from JAVA :)))))
        Connection connect = DriverManager.getConnection("jdbc:mysql://localhost:3307/MASTERCAMPTP4", "root", "Steph1!!!!"); //Connecting to the Database
        Statement statement = connect.createStatement();
        statement.executeUpdate("DELETE FROM Document WHERE DocumentName = 'Document A' AND DocumentName = 'Document B' AND DocumentName = 'Document C' AND DocumentName = 'Document D' AND DocumentName = 'Document E'");
        System.out.println("EXERCICE COMPLETE.");
    }

    public static void Auto_Exercice_C() throws SQLException, ClassNotFoundException {
        ArrayList<String> Tags = new ArrayList<>();
        Tags.add("XK-class End of the world scenario");
        Tags.add("medical");
        Tags.add("Administrative");
        Tags.add("Conspiracy");
        new Document("Document F", null, "Vatican", com.company.Category.Generate_Category("end"), com.company.Topic.Generate_Topic("End of Death"), Tag.Generate_Tags(Tags)).Insert();
        Print_Documents("*", "WHERE DocumentName = 'Document F' AND StorageAddress = 'Vatican'");
        //---------------------------------------//
        Class.forName("com.mysql.cj.jdbc.Driver"); //Driver Used to access MySQL from JAVA :)))))
        Connection connect = DriverManager.getConnection("jdbc:mysql://localhost:3307/MASTERCAMPTP4", "root", "Steph1!!!!"); //Connecting to the Database
        ResultSet resultSet;
        Statement statement = connect.createStatement();
        statement.executeUpdate("UPDATE DOCUMENT SET DocumentDate = STR_TO_DATE('2030-01-01','%Y-%m-%d') WHERE DocumentName = 'Document F' AND StorageAddress = 'Vatican'");
        Print_Documents("*", "WHERE DocumentName = 'Document F' AND StorageAddress = 'Vatican'");
        //---------------------------------------//
        ArrayList<String> Document_Info = new ArrayList<>();
        Document_Info.add("Document F");
        Document_Info.add("Vatican");
        ArrayList<Document> Loaded_Document = Generate_Documents(Document_Info);
        int ID_Loaded_Document = Loaded_Document.get(0).Find_Document();
        resultSet = statement.executeQuery("SELECT TagID FROM Detenir WHERE DocumentID = " + ID_Loaded_Document + ";");
        int i = 1;
        while (resultSet.next()) {
            if (resultSet.getInt("TagID") == i) {
                i++;
            } else {
                break;
            }
        }
        statement.executeUpdate("INSERT INTO Detenir VALUES (" + ID_Loaded_Document + "," + i + ");");
    }
}

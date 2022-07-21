package com.company;

import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Tag {
    String TagName;
    ArrayList<Document> Documents = new ArrayList<>();

    private Tag(String TagName, ArrayList<Document> Documents) {
        this.TagName = TagName;
        this.Documents = Documents;
    }

    public void Insert() throws ClassNotFoundException, SQLException { //Inserts the new Tag data into the database, including the associated Documents.
        Class.forName("com.mysql.cj.jdbc.Driver"); //Driver Used to access MySQL from JAVA :)))))
        Connection connect = DriverManager.getConnection("jdbc:mysql://localhost:3307/MASTERCAMPTP4", "root", "Steph1!!!!"); //Connecting to the Database
        Statement statement = connect.createStatement(); //Creating the object *statement* that will be used to Query
        statement.executeUpdate("INSERT INTO Tag (TagName) VALUES ('" + TagName + "');");
        for (Document All_Documents : Documents) {
            statement.executeUpdate("INSERT INTO Detenir (DocumentID, TagID) VALUES ('" + All_Documents + "','" + Find_Tag() + "');"); //Associates the Tag with all the documents
        }
    }

    public int Find_Tag() throws ClassNotFoundException, SQLException { //Tries to return the ID of the queried Tag. If the Tag cannot be found, it creates it and inserts it into the database, before sending its ID back.
        Class.forName("com.mysql.cj.jdbc.Driver"); //Driver Used to access MySQL from JAVA :)))))
        Connection connect = DriverManager.getConnection("jdbc:mysql://localhost:3307/MASTERCAMPTP4", "root", "Steph1!!!!"); //Connecting to the Database
        ResultSet resultSet = null;
        Statement statement = connect.createStatement();
        do {
            resultSet = statement.executeQuery("SELECT TagID FROM Tag WHERE TagName = \"" + TagName + "\";");
            if (!resultSet.isBeforeFirst()) { //We check if the above query returns an ID
                Insert(); //If the Tag doesn't exist, the program automatically makes a new one.
            }
        } while (!resultSet.isBeforeFirst());
        resultSet.next();
        return resultSet.getInt("TagID"); //Once the Tag exists, the TagID is returned.
    }

    public static ArrayList<Document> Find_Associated_Documents(int TagID) throws ClassNotFoundException, SQLException { //Find all the Documents associated with the queried Tag, creates instances for them, and returns them in an ArrayList.
        Class.forName("com.mysql.cj.jdbc.Driver"); //Driver Used to access MySQL from JAVA :)))))
        Connection connect = DriverManager.getConnection("jdbc:mysql://localhost:3307/MASTERCAMPTP4", "root", "Steph1!!!!"); //Connecting to the Database
        ResultSet resultSet = null;
        Statement statement = connect.createStatement();
        resultSet = statement.executeQuery("SELECT * FROM Detenir WHERE TagID = " + TagID + ";"); //This query finds all the Documents associated with the Tag.
        ArrayList<Document> Associated_Documents = new ArrayList<>();
        while (resultSet.next()) {
            Associated_Documents.add(Document.Light_Load_Document(resultSet.getInt("DocumentID"))); //As the tags already exist (since they are queried), we load them from the database, rather than generating them.
        }
        return Associated_Documents;
    }

    public static ArrayList<Tag> Generate_Tags(ArrayList<String> Tag_List) throws SQLException, ClassNotFoundException { //Tries to load the tags from their names. If the tags don't exist, they are then created.
        ArrayList<Tag> Generated_Tags = new ArrayList<>();
        for (String All_Tags : Tag_List) {
            ArrayList<Document> Associated_Documents = new ArrayList<>(); //Associated_Documents is empty at first.
            Tag Self = new Tag(All_Tags, Associated_Documents);
            int TagID = Self.Find_Tag(); //We do not need the associated documents to find the tags, so we are not worried about the null pointer.
            Self.Documents = Find_Associated_Documents(TagID); //We try to find the tags, and in the case where they do not exist, we create them.
            Generated_Tags.add(Self);
        }
        return Generated_Tags;
    }

    public static Tag Load_Tag(int TagID) throws ClassNotFoundException, SQLException { //Loads the Tag directly from the database.
        Class.forName("com.mysql.cj.jdbc.Driver"); //Driver Used to access MySQL from JAVA :)))))
        Connection connect = DriverManager.getConnection("jdbc:mysql://localhost:3307/MASTERCAMPTP4", "root", "Steph1!!!!"); //Connecting to the Database
        ResultSet resultSet = null;
        Statement statement = connect.createStatement();
        resultSet = statement.executeQuery("SELECT TagName FROM Tag WHERE TagID = " + TagID + ";");
        resultSet.next();
        return new Tag(resultSet.getString("TagName"), Find_Associated_Documents(TagID));
    }

    public static Tag Light_Load_Tag(int TagID) throws ClassNotFoundException, SQLException { //Loads the Tag directly from the database.
        Class.forName("com.mysql.cj.jdbc.Driver"); //Driver Used to access MySQL from JAVA :)))))
        Connection connect = DriverManager.getConnection("jdbc:mysql://localhost:3307/MASTERCAMPTP4", "root", "Steph1!!!!"); //Connecting to the Database
        ResultSet resultSet = null;
        Statement statement = connect.createStatement();
        resultSet = statement.executeQuery("SELECT TagName FROM Tag WHERE TagID = " + TagID + ";");
        resultSet.next();
        return new Tag(resultSet.getString("TagName"), null);
    }

    public static Tag Create_Tag() throws SQLException, ClassNotFoundException { //Creates a Tag instance from scratch, with the help of the user. If any of the components don't exit, it is promptly created automatically, so there isn't any crash. It is then returned.
        Scanner Scan = new Scanner(System.in);
        System.out.println("INSEREZ LE NOM DU TAG: ");
        String TagName = Scan.next();
        System.out.println("INSEREZ LE NOMBRE DE DOCUMENTS ASSOCIES AU TAG: ");
        int Associated_Documents_Number = Scan.nextInt();
        ArrayList<String> Document_List = new ArrayList<>();
        for (int i = 0; i < Associated_Documents_Number * 2; i++) {
            System.out.println("INSEREZ LE NOM DU DOCUMENT: ");
            Document_List.add(Scan.next());
            System.out.println("INSEREZ L'ESPACE DE STOCKAGE DU MEME DOCUMENT: ");
            Document_List.add(Scan.next());
        }
        return new Tag(TagName, Document.Generate_Documents(Document_List));
    }

    public static void New_Database_Tag() throws SQLException, ClassNotFoundException { //Creates a Tag instance from scratch with the help of the user, and inserts it in the database.
        Create_Tag().Insert();
        System.out.println("VOTRE TAG A BIEN ETE ENREGISTRE.");
    }

    public static void Print_Tags(String Column, String Where) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver"); //Driver Used to access MySQL from JAVA :)))))
        Connection connect = DriverManager.getConnection("jdbc:mysql://localhost:3307/MASTERCAMPTP4", "root", "Steph1!!!!"); //Connecting to the Database
        ResultSet resultSet;
        Statement statement = connect.createStatement();
        resultSet = statement.executeQuery("SELECT " + Column + " FROM Tag " + Where);
        while (resultSet.next()) {
            System.out.println("\nTagID = " + resultSet.getInt("TagID"));
            System.out.println("TagName = " + resultSet.getString("TagName"));
            System.out.println("ASSOCIATED DOCUMENTS: ");
            Print_Documents(resultSet.getInt("TagID"));
        }
    }

    private static void Print_Documents(int TagID) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver"); //Driver Used to access MySQL from JAVA :)))))
        Connection connect = DriverManager.getConnection("jdbc:mysql://localhost:3307/MASTERCAMPTP4", "root", "Steph1!!!!"); //Connecting to the Database
        Statement statement = connect.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT Document.DocumentID, DocumentName, DocumentDate, StorageAddress FROM Document JOIN Detenir ON Document.DocumentID = Detenir.DocumentID JOIN Tag ON Tag.TagID = Detenir.TagID WHERE Tag.TagID = " + TagID);
        while (resultSet.next()) {
            System.out.println("\nDocumentID = " + resultSet.getInt("DocumentID"));
            System.out.println("Document Name = " + resultSet.getString("DocumentName"));
            System.out.println("Document Date = " + resultSet.getString("DocumentDate"));
            System.out.println("Storage Address = " + resultSet.getString("StorageAddress"));
        }
    }

    //---------------------------------------------------------------------------- AUTO PART ----------------------------------------------------------------------------//

    public static void Auto_Print_Document_By_Tag() throws SQLException, ClassNotFoundException {
        Print_Tags("TagName, TagID", "ORDER BY TagID");
    }

    public static void Auto_Tag_Occurrence() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver"); //Driver Used to access MySQL from JAVA :)))))
        Connection connect = DriverManager.getConnection("jdbc:mysql://localhost:3307/MASTERCAMPTP4", "root", "Steph1!!!!"); //Connecting to the Database
        ResultSet resultSet = null;
        Statement statement = connect.createStatement();
        resultSet = statement.executeQuery("SELECT Tag.TagID, COUNT(Tag.TagID), Tag.TagName FROM Tag JOIN Detenir ON Tag.TagID = Detenir.TagID GROUP BY Tag.TagID ORDER BY COUNT(Tag.TagID) DESC;");
        while (resultSet.next()) {
            System.out.println("\nTagID = " + resultSet.getString("TagID"));
            System.out.println("TagName = " + resultSet.getString("TagName"));
            System.out.println("Tag Count = " + resultSet.getString("COUNT(Tag.TagID)"));
        }
    }
}

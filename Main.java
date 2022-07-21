package com.company;

import java.util.Scanner;
import java.sql.*;
public class Main {

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver"); //Driver Used to access MySQL from JAVA :)))))
        Connection connect = DriverManager.getConnection("jdbc:mysql://localhost:3307/MASTERCAMPTP4", "root", "Steph1!!!!"); //Connecting to the Database
        Scanner Scan = new Scanner(System.in);
        String Command = "Boot up";
        String DataTable;
        String Where;
        String Column;
        Statement statement;
        while (!Command.equals("QUIT")){
            System.out.print("INSEREZ \"SELECT\" POUR VOIR LES DONNEES\nINPUT \"UPDATE\" TO METTRE A JOUR LES DONNEES\nINSEREZ \"DELETE\" TO SUPPRIMER LES DONNEES\nINSEREZ \"INSERT\" POUR INSERER DES DONNEES \nINSEREZ \"FREE\" POUR ENTREZ N'IMPORTE QUELLE REQUETE\nINSEREZ \"AUTO\" POUR COMPLETER L'EXERCICE AUTOMATIQUEMENT\nINSEREZ \"QUIT\" POUR QUITTER LE PROGRAMME\nENTREE: ");
            Command = Scan.nextLine();
            switch (Command) {
                case "UPDATE" -> { //METTRE A JOUR UNE INFORMATION
                    System.out.print("SELECTIONNEZ LA TABLE DESIREE\n");
                    DataTable = Scan.nextLine();
                    System.out.print("SELECTIONNEZ LA COLONNE ET LA MISE A JOUR A EFFECTUER (Format: <table> = <modification> || Exemple: CategoryName = Polar)\n");
                    Column = Scan.nextLine();
                    System.out.print("SELECTIONNEZ LES CONDITIONS DESIREES (Format: <specification de condition> <clause de specification> || Exemple: WHERE CategoryID = 1 || laisser vide si pas de condition)\n");
                    Where = Scan.nextLine();
                    statement = connect.createStatement(); //Creating the object *statement* that will be used to Query
                    statement.executeUpdate("UPDATE " + DataTable + " SET " + Column + " " + Where);
                }
                case "DELETE" -> { //SUPPRIMER UNE LIGNE
                    System.out.print("SELECTIONNEZ LA TABLE DESIREE\n");
                    DataTable = Scan.nextLine();
                    System.out.print("SELECTIONNEZ LES CONDITIONS POUR LA DELETION A EFFECTUER (Format: <specification de condition> <clause de specification> || Exemple: TopicName = Explosions) || Leave empty if no condition)\n");
                    Where = Scan.nextLine();
                    statement = connect.createStatement(); //Creating the object *statement* that will be used to Query
                    statement.executeUpdate("DELETE FROM " + DataTable + " " + Where);
                }
                case "SELECT" -> { //AFFICHER UNE TABLE
                    System.out.println("POUR VOIR LES DOCUMENTS, ENTREZ \"DOCUMENT\"");
                    System.out.println("POUR VOIR LES TAGS, ENTREZ \"TAG\"");
                    System.out.println("POUR VOIR LES TOPICS, ENTREZ \"TOPIC\"");
                    System.out.println("POUR VOIR LES CATEGORIES, ENTREZ \"CATEGORY\"");
                    System.out.print("VOTRE CHOIX: ");
                    Command = Scan.next();
                    System.out.print("ENTREZ LES COLONNES QUE VOUS VOULEZ AFFICHER (Format: <Column, Column, ...> || Exemple1: * || Exemple2: email || Exemple3: DocumentID, TagName, DocumentName, TagID)\n");
                    Column = Scan.next();
                    System.out.print("SELECT THE DESIRED CONDITIONS (Format: <condition specifier> <condition clause> || Example: WHERE id = 1 || Leave empty if no condition)\n");
                    Where = Scan.nextLine();
                    switch (Command) {
                        case "DOCUMENT" -> Document.Print_Documents(Column, Where);
                        case "TAG" -> Tag.Print_Tags(Column,Where);
                        case "TOPIC" -> Topic.Print_Topic(Column, Where);
                        case "CATEGORY" -> Category.Print_Category(Column, Where);
                        default -> System.out.println("COMMANDE INCONNUE. RETOUR AU MENU.");
                    }
                }
                case "INSERT" -> { //INSERER DE NOUVELLES INFORMATIONS
                    System.out.println("POUR INSERER UN DOCUMENT, ENTREZ \"DOCUMENT\"");
                    System.out.println("POUR INSERER UN TAG, ENTREZ \"TAG\"");
                    System.out.println("POUR INSERER UN TOPIC, ENTREZ \"TOPIC\"");
                    System.out.println("POUR INSERER UNE CATEGORY, ENTREZ \"CATEGORY\"");
                    System.out.print("VOTRE CHOIX: ");
                    Command = Scan.next();
                    switch (Command) {
                        case "DOCUMENT" -> Document.New_Database_Document();
                        case "TAG" -> Tag.New_Database_Tag();
                        case "TOPIC" -> Topic.New_Database_Topic();
                        case "CATEGORY" -> Category.New_Database_Category();
                        default -> System.out.println("COMMANDE INCONNUE. RETOUR AU MENU.");
                    }
                    Command = "MENU";
                }
                case "FREE" -> {
                    System.out.println("ENTREZ VOTRE REQUETE SQL (AFFICHAGE NON DISPONIBLE DANS CE MODE): ");
                    String Query = Scan.next();
                    statement = connect.createStatement(); //Creating the object *statement* that will be used to Query
                    try {
                        statement.executeUpdate(Query);
                    } catch (SQLException sqlException) {
                        statement.executeQuery(Query);
                    }
                }
                case "AUTO" -> {
                    System.out.println("EXERCICE A) I & II: ");
                    Document.Auto_New_Database_Document();
                    System.out.println("------------------------------------");
                    System.out.println("EXERCICE B) I: ");
                    System.out.println("DOCUMENTS DEPUIS CATEGORY: ");
                    Category.Auto_Print_Document_By_Category();
                    System.out.println("--------------------");
                    System.out.println("DOCUMENTS DEPUIS TOPIC: ");
                    Topic.Auto_Print_Document_By_Topic();
                    System.out.println("--------------------");
                    System.out.println("DOCUMENTS DEPUIS TAGS: ");
                    Tag.Auto_Print_Document_By_Tag();
                    System.out.println("------------------------------------");
                    System.out.println("EXERCICE B) II: ");
                    Topic.Auto_Print_Most_Used_Topic();
                    System.out.println("------------------------------------");
                    System.out.println("EXERCICE B) III: ");
                    Tag.Auto_Tag_Occurrence();
                    System.out.println("EXERCICE C) I & II & III: ");
                    Document.Auto_Exercice_C();
                }
                case "QUIT" -> {

                }
                default -> {
                    System.out.print("ERREUR: COMMANDE NON RECONNUE. REESSAYEZ: \n");
                    Command = Scan.next();
                }
            }
        }
    }
}

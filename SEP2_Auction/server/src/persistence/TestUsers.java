package persistence;

import java.sql.SQLException;

public class TestUsers {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        AuctionPersistence database = new AuctionDatabase();
/*
        //  Play around with values for full content

        ///////////////// CREATE USER //////////////////////
        System.out.println("--- Creating first user ---");
        //  There is a check if email and phone number is already in the system...
        try{
            database.createUser("Anthony","Soprano","tony.es@google.com","secretpassword","52743420");
        }
        catch (SQLException e){
            System.out.println("Database exception: " + e.getLocalizedMessage());
        }

        //////////////////// getUser ///////////////////////
        System.out.println("--- Getting the created user ---");
        try{
            System.out.println(database.getUser("tony.es@google.com","secretpassword"));
        }
        catch (SQLException e){
            System.out.println("Database exception: " + e.getLocalizedMessage());
        }*/
    }
}

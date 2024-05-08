package model;

import utility.observer.javaobserver.NamedPropertyChangeSubject;

import java.beans.PropertyChangeEvent;
import java.sql.SQLException;

public interface AuctionModel extends NamedPropertyChangeSubject
{
  Auction startAuction(String title, String description, int reservePrice,
      int buyoutPrice, int minimumIncrement, int auctionTime, byte[] imageData)
      throws SQLException, ClassNotFoundException;
  Auction getAuction(int ID) throws SQLException;
  AuctionList getOngoingAuctions() throws SQLException;
  void addUser(String firstname, String lastname, String email, String password, String phone);
  String getUser(String email, String password);
}

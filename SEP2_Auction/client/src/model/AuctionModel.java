package model;

import utility.observer.javaobserver.NamedPropertyChangeSubject;

import java.rmi.RemoteException;
import java.sql.SQLException;

public interface AuctionModel extends NamedPropertyChangeSubject
{
  Auction startAuction(String title, String description, int reservePrice,
      int buyoutPrice, int minimumIncrement, int auctionTime, byte[] imageData)
      throws SQLException, ClassNotFoundException;
  Auction getAuction(int ID) throws SQLException;
  AuctionList getOngoingAuctions() throws SQLException;

  NotificationList getNotifications(String receiver) throws SQLException;
  Bid placeBid(String bidder, int bidValue, int auctionId) throws SQLException;


  void addUser(String firstname,String lastname, String email, String password, String phone) throws SQLException;
  User getUser(String email, String password) throws SQLException;
}


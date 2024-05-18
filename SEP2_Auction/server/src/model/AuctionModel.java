package model;

import utility.observer.javaobserver.NamedPropertyChangeSubject;

import java.rmi.RemoteException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public interface AuctionModel extends NamedPropertyChangeSubject {
  Auction startAuction(String title, String description, int reservePrice,
      int buyoutPrice, int minimumIncrement, int auctionTime, byte[] imageData, String seller)
      throws SQLException, ClassNotFoundException;

  Auction getAuction(int ID) throws SQLException;

  AuctionList getOngoingAuctions() throws SQLException;

  NotificationList getNotifications(String receiver) throws SQLException;

  Bid placeBid(String bidder, int bidValue, int auctionId) throws SQLException;

  String addUser(String firstname, String lastname, String email, String password, String repeatedPassword,
      String phone, LocalDate birthday) throws SQLException;

  String login(String email, String password) throws SQLException;

  AuctionList getPreviousBids(String bidder) throws SQLException;

  AuctionList getCreatedAuctions(String seller) throws SQLException;

  void resetPassword(String userEmail, String oldPassword, String newPassword, String repeatPassword)
      throws SQLException;

  User getUser(String email) throws SQLException;

  User getModeratorInfo() throws SQLException;

  boolean isModerator(String email) throws SQLException;

  User editInformation(String oldEmail, String firstname, String lastname, String email, String password, String phone,
      LocalDate birthday) throws SQLException;

  AuctionList getAllAuctions() throws SQLException;

  ArrayList<User> getAllUsers() throws SQLException;

  void buyOut(String bidder, int auctionId) throws RemoteException, SQLException;

}

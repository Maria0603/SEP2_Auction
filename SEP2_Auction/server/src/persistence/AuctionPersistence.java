package persistence;

import model.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public interface AuctionPersistence
{
  Auction saveAuction(String title, String description, int reservePrice,
      int buyoutPrice, int minimumIncrement, int auctionTime, byte[] imageData, String seller)
      throws SQLException;
  Auction getAuctionById(int id) throws SQLException;
  void markAsClosed(int id) throws SQLException;
  AuctionList getOngoingAuctions() throws SQLException;
  NotificationList getNotifications(String receiver) throws SQLException;
  Notification saveNotification(String content, String receiver) throws SQLException;
  Bid saveBid(String participantEmail, int bidAmount, int auctionId) throws SQLException;
  Bid getCurrentBidForAuction(int auctionId) throws SQLException;
  User createUser(String firstname, String lastname, String email, String password, String repeatedPassword, String phone, LocalDate birthday) throws SQLException;
  String login(String email, String password) throws SQLException;
  AuctionList getPreviousBids(String bidder) throws SQLException;
  AuctionList getCreatedAuctions(String seller) throws SQLException;
  ArrayList<User> getAllUsers() throws SQLException;
  void resetPassword(String userEmail, String oldPassword, String newPassword, String repeatPassword)
      throws SQLException;
  User getUserInfo(String email) throws SQLException;
  User getModeratorInfo() throws SQLException;
  boolean isModerator(String email) throws SQLException;
  AuctionList getAllAuctions() throws SQLException;
  void setBuyer(int auctionId, String current_bider) throws SQLException;
  void buyOut(String bidder, int auctionId) throws SQLException;
  User editInformation(String oldEmail, String firstname, String lastname, String email, String password, String phone, LocalDate birthday) throws SQLException;

}

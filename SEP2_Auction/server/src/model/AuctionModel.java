package model;

import utility.observer.javaobserver.NamedPropertyChangeSubject;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;

public interface AuctionModel extends NamedPropertyChangeSubject
{
  Auction startAuction(String title, String description, int reservePrice,
      int buyoutPrice, int minimumIncrement, int auctionTime, byte[] imageData, String seller)
      throws SQLException, ClassNotFoundException;
  Auction getAuction(int ID) throws SQLException;
  AuctionList getOngoingAuctions() throws SQLException;

  NotificationList getNotifications(String receiver) throws SQLException;
  Bid placeBid(String bidder, int bidValue, int auctionId) throws SQLException;

  String addUser(String firstname, String lastname, String email, String password, String repeatedPassword, String phone, LocalDate birthday) throws SQLException;
  String login(String email, String password) throws SQLException;

  AuctionList getPreviousBids(String bidder) throws SQLException;
  void resetPassword(String userEmail, String oldPassword, String newPassword, String repeatPassword)
      throws SQLException;
  User getParticipant(String email) throws SQLException;
  boolean isModerator(String email) throws SQLException;
}


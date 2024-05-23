package persistence;

import model.*;

import java.sql.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;

public class ProtectionProxy implements AuctionPersistence
{
  private AuctionDatabase database;
  private static final String MODERATOR_EMAIL = "bob@bidhub";
  private static final String MODERATOR_TEMPORARY_PASSWORD = "1234";

  public ProtectionProxy() throws SQLException, ClassNotFoundException
  {
    this.database = new AuctionDatabase();
  }

  @Override public Auction saveAuction(String title, String description,
      int reservePrice, int buyoutPrice, int minimumIncrement, int auctionTime,
      byte[] imageData, String seller) throws SQLException
  {
    if (!isNotModerator(seller))
      throw new SQLException("The moderator cannot start auctions.");
    checkAuctionTime(auctionTime);
    checkTitle(title);
    checkDescription(description);
    checkReservePrice(reservePrice);
    checkBuyoutPrice(buyoutPrice, reservePrice);
    checkMinimumIncrement(minimumIncrement);
    return database.saveAuction(title, description, reservePrice, buyoutPrice,
        minimumIncrement, auctionTime, imageData, seller);
  }

  @Override public Auction getAuctionById(int id) throws SQLException
  {
    return database.getAuctionById(id);
  }

  @Override public void markAsClosed(int id) throws SQLException
  {
    database.markAsClosed(id);
  }

  @Override public AuctionList getOngoingAuctions() throws SQLException
  {
    return database.getOngoingAuctions();
  }

  @Override public NotificationList getNotifications(String receiver)
      throws SQLException
  {
    return database.getNotifications(receiver);
  }

  @Override public Notification saveNotification(String content,
      String receiver) throws SQLException
  {
    return database.saveNotification(content, receiver);
  }

  @Override public Bid saveBid(String participantEmail, int bidAmount,
      int auctionId) throws SQLException
  {
    if (!isNotModerator(participantEmail))
      throw new SQLException("The moderator cannot place bids.");
    return database.saveBid(participantEmail, bidAmount, auctionId);
  }

  @Override public Bid getCurrentBidForAuction(int auctionId)
      throws SQLException
  {
    return database.getCurrentBidForAuction(auctionId);
  }

  @Override public User createUser(String firstname, String lastname,
      String email, String password, String repeatedPassword, String phone,
      LocalDate birthday) throws SQLException
  {
    checkFirstName(firstname);
    checkLastName(lastname);
    checkPhone(phone);
    ageValidation(birthday);
    return database.createUser(firstname, lastname, email, password,
        repeatedPassword, phone, birthday);
  }

  @Override public String login(String email, String password)
      throws SQLException
  {
    return database.login(email, password);
  }

  @Override public AuctionList getPreviousBids(String bidder)
      throws SQLException
  {
    return database.getPreviousBids(bidder);
  }

  @Override public AuctionList getCreatedAuctions(String seller)
      throws SQLException
  {
    return database.getCreatedAuctions(seller);
  }

  @Override public ArrayList<User> getAllUsers(String moderatorEmail) throws SQLException
  {
    return database.getAllUsers(moderatorEmail);
  }

  @Override public void resetPassword(String userEmail, String oldPassword,
      String newPassword, String repeatPassword) throws SQLException
  {
    database.resetPassword(userEmail, oldPassword, newPassword, repeatPassword);
  }

  @Override public User getUserInfo(String email) throws SQLException
  {
    return database.getUserInfo(email);
  }

  @Override public User getModeratorInfo() throws SQLException
  {
    return database.getModeratorInfo();
  }

  @Override public boolean isModerator(String email) throws SQLException
  {
    return database.isModerator(email);
  }

  @Override public AuctionList getAllAuctions(String moderatorEmail) throws SQLException
  {
    if(isNotModerator(moderatorEmail))
      throw new SQLException("You cannot access all auctions");
    return database.getAllAuctions(moderatorEmail);
  }

  @Override public void setBuyer(int auctionId, String current_bider)
      throws SQLException
  {
    database.setBuyer(auctionId, current_bider);
  }

  @Override public void buyOut(String bidder, int auctionId) throws SQLException
  {
    if(!isNotModerator(bidder))
      throw new SQLException("You cannot participate in auctions");
    database.buyOut(bidder, auctionId);
  }

  @Override public User editInformation(String oldEmail, String firstname,
      String lastname, String email, String password, String phone,
      LocalDate birthday) throws SQLException
  {
      checkFirstName(firstname);
      checkLastName(lastname);
      ageValidation(birthday);
      return database.editInformation(oldEmail, firstname, lastname, email, password, phone, birthday);
  }

  @Override public void banParticipant(String moderatorEmail,
      String participantEmail, String reason) throws SQLException
  {
    if (isNotModerator(moderatorEmail))
      throw new SQLException("You cannot ban another participant.");
    checkReason(reason);
    database.banParticipant(moderatorEmail, participantEmail, reason);
  }

  @Override public String extractBanningReason(String email) throws SQLException
  {
    return database.extractBanningReason(email);
  }

  @Override public void unbanParticipant(String moderatorEmail,
      String participantEmail) throws SQLException
  {
    if (isNotModerator(moderatorEmail))
      throw new SQLException("You cannot unban another participant.");
    database.unbanParticipant(moderatorEmail, participantEmail);
  }

  @Override public void deleteAuction(String moderatorEmail, int auctionId,
      String reason) throws SQLException
  {
    if (isNotModerator(moderatorEmail))
      throw new SQLException("You cannot delete this item.");
    checkReason(reason);
    database.deleteAuction(moderatorEmail, auctionId, reason);
  }
  private boolean isNotModerator(String email)
  {
    return !email.equals(MODERATOR_EMAIL);
  }

  private void checkTitle(String title) throws SQLException
  {
    int maxTitleLength = 80;
    int minTitleLength = 5;
    if (title.length() > maxTitleLength)
      throw new SQLException("The title is too long!");
    else if (title.length() < minTitleLength)
      throw new SQLException("The title is too short!");
  }

  private void checkDescription(String description) throws SQLException
  {
    int maxDescriptionLength = 1400, minDescriptionLength = 20;
    if (description.length() > maxDescriptionLength)
      throw new SQLException("The description is too long!");
    else if (description.length() < minDescriptionLength)
      throw new SQLException("The description is too short!");
  }

  private void checkReservePrice(int reservePrice) throws SQLException
  {
    if (reservePrice <= 0)
      throw new SQLException("The reserve price must be a positive number!");
  }

  private void checkBuyoutPrice(int buyoutPrice, int reservePrice)
      throws SQLException
  {
    if (buyoutPrice <= reservePrice)
      throw new SQLException(
          "The buyout price must be greater than the reserve price!");
  }

  private void checkMinimumIncrement(int minimumIncrement) throws SQLException
  {
    if (minimumIncrement < 1)
      throw new SQLException("The minimum bid increment must be at least 1!");
  }

  private void checkAuctionTime(int auctionTime) throws SQLException
  {
    // to be updated when the moderator adds the time interval
    if (auctionTime <= 0 || auctionTime > 24)
      throw new SQLException("The auction time can be at most 24 hours!");
  }

  private void checkFirstName(String firstname) throws SQLException
  {
    if (firstname.isEmpty())
    {
      throw new SQLException("Empty first name.");
    }
    if (firstname.length() < 4)
    {
      throw new SQLException(
          "The first name must be at least 3 characters long.");
    }
  }

  private void checkLastName(String lastname) throws SQLException
  {
    if (lastname.isEmpty())
    {
      throw new SQLException("Empty last name.");
    }
    if (lastname.length() < 4)
    {
      throw new SQLException(
          "The last name must be at least 3 characters long.");
    }
  }


  private void checkPhone(String phone) throws SQLException
  {
    if (phone.length() < 4)
    {
      throw new SQLException("Invalid phone number.");
    }
  }

  private void ageValidation(LocalDate birthday) throws SQLException
  {
    if (birthday != null)
    {
      LocalDate currentDate = LocalDate.now();
      Period period = Period.between(birthday, currentDate);
      int age = period.getYears();
      if (age < 18)
        throw new SQLException("You must be over 18 years old.");
    }
  }
  private void checkReason(String reason) throws SQLException
  {
    if (reason == null || reason.length() < 3)
      throw new SQLException("The reason is too short.");
    if (reason.length() > 600)
      throw new SQLException("The reason is too long.");
  }
}

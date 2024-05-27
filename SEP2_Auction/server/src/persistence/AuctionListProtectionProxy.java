package persistence;

import model.domain.*;

import java.sql.*;
public class AuctionListProtectionProxy extends DatabasePersistence implements AuctionListPersistence
{
  private final AuctionListDatabase database;

  public AuctionListProtectionProxy() throws SQLException, ClassNotFoundException
  {
    database=new AuctionListDatabase();
  }

  @Override public Auction getAuctionById(int id) throws SQLException
  {
    return database.getAuctionById(id);
  }

  @Override public AuctionList getOngoingAuctions() throws SQLException
  {
    return database.getOngoingAuctions();
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

  @Override public boolean isModerator(String email) throws SQLException
  {
    return database.isModerator(email);
  }

  @Override public AuctionList getAllAuctions(String moderatorEmail) throws SQLException
  {
    if(isNotModerator(moderatorEmail))
      throw new SQLException("You cannot access all auctions.");
    return database.getAllAuctions(moderatorEmail);
  }

  private boolean isNotModerator(String email)
  {
    return !email.equals(super.getModeratorEmail());
  }
}

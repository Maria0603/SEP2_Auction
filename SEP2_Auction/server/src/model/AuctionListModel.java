package model;

import model.domain.Auction;
import model.domain.AuctionList;

import java.sql.SQLException;

public interface AuctionListModel
{
  AuctionList getOngoingAuctions() throws SQLException;
  AuctionList getPreviousBids(String bidder) throws SQLException;
  AuctionList getCreatedAuctions(String seller) throws SQLException;
  AuctionList getAllAuctions(String moderatorEmail) throws SQLException;
  Auction getAuction(int ID) throws SQLException;
  boolean isModerator(String email) throws SQLException;
}

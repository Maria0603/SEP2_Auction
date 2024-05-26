package persistence;

import model.domain.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public interface AuctionListPersistence
{
  Auction getAuctionById(int id) throws SQLException;
  AuctionList getOngoingAuctions() throws SQLException;
  AuctionList getPreviousBids(String bidder) throws SQLException;
  AuctionList getCreatedAuctions(String seller) throws SQLException;
  boolean isModerator(String email) throws SQLException;
  AuctionList getAllAuctions(String moderatorEmail) throws SQLException;

}

package model;

import model.domain.Auction;
import model.domain.AuctionList;
import utility.observer.javaobserver.NamedPropertyChangeSubject;

import java.sql.SQLException;
import java.time.LocalDate;

public interface AuctionListModel  extends NamedPropertyChangeSubject
{
  AuctionList getOngoingAuctions() throws SQLException;
  AuctionList getPreviousBids(String bidder) throws SQLException;
  AuctionList getCreatedAuctions(String seller) throws SQLException;
  AuctionList getAllAuctions(String moderatorEmail) throws SQLException;
  Auction getAuction(int ID) throws SQLException;
  boolean isModerator(String email) throws SQLException;
}

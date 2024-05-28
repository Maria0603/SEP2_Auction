package model;

import model.domain.Auction;
import model.domain.AuctionList;
import utility.observer.javaobserver.NamedPropertyChangeSubject;


public interface AuctionListModel extends NamedPropertyChangeSubject
{
  AuctionList getOngoingAuctions() throws IllegalArgumentException;
  AuctionList getPreviousBids(String bidder) throws IllegalArgumentException;
  AuctionList getCreatedAuctions(String seller) throws IllegalArgumentException;
  AuctionList getAllAuctions(String moderatorEmail) throws IllegalArgumentException;
  Auction getAuction(int ID) throws IllegalArgumentException;
  boolean isModerator(String email) throws IllegalArgumentException;
}

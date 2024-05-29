package model;

import model.domain.Auction;
import model.domain.AuctionList;
import utility.observer.javaobserver.NamedPropertyChangeSubject;

/**
 * The AuctionListModel interface defines the methods for interacting with
 * the auction list, including getting ongoing auctions, previous bids, created auctions,
 * and all auctions for a moderator. It also includes methods for retrieving a specific auction
 * and checking if a user is a moderator.
 */
public interface AuctionListModel extends NamedPropertyChangeSubject {

  /**
   * Retrieves the list of ongoing auctions.
   *
   * @return the list of ongoing auctions.
   * @throws IllegalArgumentException if an illegal argument is provided.
   */
  AuctionList getOngoingAuctions() throws IllegalArgumentException;

  /**
   * Retrieves the list of previous bids by a bidder.
   *
   * @param bidder the name of the bidder.
   * @return the list of previous bids.
   * @throws IllegalArgumentException if an illegal argument is provided.
   */
  AuctionList getPreviousBids(String bidder) throws IllegalArgumentException;

  /**
   * Retrieves the list of auctions created by a seller.
   *
   * @param seller the name of the seller.
   * @return the list of created auctions.
   * @throws IllegalArgumentException if an illegal argument is provided.
   */
  AuctionList getCreatedAuctions(String seller) throws IllegalArgumentException;

  /**
   * Retrieves the list of all auctions for a moderator.
   *
   * @param moderatorEmail the email of the moderator.
   * @return the list of all auctions.
   * @throws IllegalArgumentException if an illegal argument is provided.
   */
  AuctionList getAllAuctions(String moderatorEmail) throws IllegalArgumentException;

  /**
   * Retrieves an auction by its ID.
   *
   * @param ID the ID of the auction.
   * @return the Auction with the specified ID.
   * @throws IllegalArgumentException if an illegal argument is provided.
   */
  Auction getAuction(int ID) throws IllegalArgumentException;

  /**
   * Checks if a user is a moderator.
   *
   * @param email the email of the user.
   * @return true if the user is a moderator, false otherwise.
   * @throws IllegalArgumentException if an illegal argument is provided.
   */
  boolean isModerator(String email) throws IllegalArgumentException;
}

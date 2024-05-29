package model;

import model.domain.Auction;
import model.domain.Bid;
import utility.observer.javaobserver.NamedPropertyChangeSubject;

/**
 * The AuctionModel interface defines the methods for interacting with individual auctions,
 * including starting an auction, retrieving an auction, placing a bid, performing a buyout,
 * and deleting an auction. It extends NamedPropertyChangeSubject to support property change notifications.
 */
public interface AuctionModel extends NamedPropertyChangeSubject {

  /**
   * Starts a new auction.
   *
   * @param title the title of the auction.
   * @param description the description of the auction.
   * @param reservePrice the reserve price of the auction.
   * @param buyoutPrice the buyout price of the auction.
   * @param minimumIncrement the minimum increment for bids.
   * @param auctionTime the duration of the auction.
   * @param imageData the image data associated with the auction.
   * @param seller the seller of the auction.
   * @return the started Auction.
   * @throws IllegalArgumentException if an illegal argument is provided.
   * @throws ClassNotFoundException if a class is not found.
   */
  Auction startAuction(String title, String description, int reservePrice, int buyoutPrice, int minimumIncrement, int auctionTime, byte[] imageData, String seller)
          throws IllegalArgumentException, ClassNotFoundException;

  /**
   * Retrieves an auction by its ID.
   *
   * @param ID the ID of the auction.
   * @return the Auction with the specified ID.
   * @throws IllegalArgumentException if an illegal argument is provided.
   */
  Auction getAuction(int ID) throws IllegalArgumentException;

  /**
   * Places a bid on an auction.
   *
   * @param bidder the bidder's name.
   * @param bidValue the value of the bid.
   * @param auctionId the ID of the auction.
   * @return the placed Bid.
   * @throws IllegalArgumentException if an illegal argument is provided.
   */
  Bid placeBid(String bidder, int bidValue, int auctionId) throws IllegalArgumentException;

  /**
   * Performs a buyout on an auction.
   *
   * @param bidder the bidder's name.
   * @param auctionId the ID of the auction.
   * @throws IllegalArgumentException if an illegal argument is provided.
   */
  void buyout(String bidder, int auctionId) throws IllegalArgumentException;

  /**
   * Deletes an auction.
   *
   * @param moderatorEmail the email of the moderator.
   * @param auctionId the ID of the auction.
   * @param reason the reason for deleting the auction.
   * @throws IllegalArgumentException if an illegal argument is provided.
   */
  void deleteAuction(String moderatorEmail, int auctionId, String reason) throws IllegalArgumentException;
}

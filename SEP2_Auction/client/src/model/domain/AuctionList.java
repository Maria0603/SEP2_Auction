package model.domain;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * The AuctionList class represents a list of auctions and provides methods to
 * manage the auctions within the list. It implements Serializable for object serialization.
 */
public class AuctionList implements Serializable {

  private final ArrayList<Auction> auctions;

  ///////////////////////////////////////////////////////////////////
  // Do not change this number
  @Serial
  private static final long serialVersionUID = 6529685098267757690L;
  //////////////////////////////////////////////////////////////////

  /**
   * Constructs a new AuctionList object.
   * Initializes an empty list of auctions.
   */
  public AuctionList() {
    this.auctions = new ArrayList<>();
  }

  /**
   * Adds an auction to the list.
   *
   * @param auction the auction to add.
   */
  public void addAuction(Auction auction) {
    if (auction != null)
      auctions.add(auction);
  }

  /**
   * Removes an auction from the list by its ID.
   *
   * @param ID the ID of the auction to remove.
   */
  public void removeAuction(int ID) {
    for (int i = 0; i < auctions.size(); i++) {
      if (auctions.get(i).getID() == ID) {
        auctions.remove(i);
        i--;
      }
    }
  }

  /**
   * Retrieves an auction by its ID.
   *
   * @param ID the ID of the auction to retrieve.
   * @return the Auction with the specified ID.
   * @throws IllegalArgumentException if no auction with the specified ID is found.
   */
  public Auction getAuctionByID(int ID) {
    for (Auction auction : auctions) {
      if (auction.getID() == ID)
        return auction;
    }
    throw new IllegalArgumentException("No auction with this ID.");
  }

  /**
   * Retrieves an auction by its index in the list.
   *
   * @param index the index of the auction to retrieve.
   * @return the Auction at the specified index.
   */
  public Auction getAuction(int index) {
    return auctions.get(index);
  }

  /**
   * Gets the number of auctions in the list.
   *
   * @return the size of the auction list.
   */
  public int getSize() {
    return auctions.size();
  }

  /**
   * Returns a string representation of the auction list.
   *
   * @return a string representation of the auction list.
   */
  public String toString() {
    StringBuilder s = new StringBuilder();
    for (int i = 0; i < getSize(); i++)
      s.append(auctions.get(i)).append('\n');
    return s.toString();
  }

  /**
   * Checks if the auction list contains an auction with the specified ID.
   *
   * @param auctionId the ID of the auction to check for.
   * @return true if the auction list contains the auction, false otherwise.
   */
  public boolean contains(int auctionId) {
    for (int i = 0; i < auctions.size(); i++) {
      if (auctions.get(i).getID() == auctionId)
        return true;
    }
    return false;
  }
}

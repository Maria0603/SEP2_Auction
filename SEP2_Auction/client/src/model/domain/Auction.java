package model.domain;

import utility.observer.javaobserver.NamedPropertyChangeSubject;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serial;
import java.io.Serializable;
import java.sql.Time;

/**
 * The Auction class represents an auction and its related information such as item, price constraints,
 * current bid, seller, and status. It implements Serializable for object serialization.
 */
public class Auction implements Serializable {

  private final int ID;
  private final Item item;
  private final PriceConstraint priceConstraint;
  private String currentBidder, seller, status;
  private int currentBid;
  Time start, end;
  private byte[] imageData;

  ///////////////////////////////////////////////////////////////////
  // Do not change this number
  @Serial
  private static final long serialVersionUID = 6529685098267757690L;
  //////////////////////////////////////////////////////////////////

  /**
   * Constructs a new Auction object with the specified parameters.
   *
   * @param ID the ID of the auction.
   * @param title the title of the auction.
   * @param description the description of the auction.
   * @param reservePrice the reserve price of the auction.
   * @param buyoutPrice the buyout price of the auction.
   * @param minimumIncrement the minimum increment for bids.
   * @param auctionStart the start time of the auction.
   * @param auctionEnd the end time of the auction.
   * @param currentBid the current bid of the auction.
   * @param currentBidder the current bidder of the auction.
   * @param seller the seller of the auction.
   * @param imageData the image data of the auction item.
   * @param status the status of the auction.
   */
  public Auction(int ID, String title, String description, int reservePrice,
                 int buyoutPrice, int minimumIncrement, Time auctionStart, Time auctionEnd,
                 int currentBid, String currentBidder, String seller, byte[] imageData,
                 String status) {
    this.ID = ID;
    this.item = new Item(title, description);
    this.priceConstraint = new PriceConstraint(reservePrice, buyoutPrice, minimumIncrement);
    this.start = auctionStart;
    this.end = auctionEnd;
    setImageData(imageData);
    setCurrentBid(currentBid);
    setCurrentBidder(currentBidder);
    setSeller(seller);
    this.status = status;
  }

  /**
   * Constructs a new Auction object with the specified parameters.
   *
   * @param ID the ID of the auction.
   * @param title the title of the auction.
   * @param currentBid the current bid of the auction.
   * @param end the end time of the auction.
   * @param imageData the image data of the auction item.
   */
  public Auction(int ID, String title, int currentBid, Time end, byte[] imageData) {
    this(ID, title, null, 0, 0, 0, null, end, currentBid, null, null, imageData, null);
  }

  /**
   * Checks if the auction matches the given search mask.
   *
   * @param searchMask the search mask to check.
   * @return true if the auction matches the search mask, false otherwise.
   */
  public boolean isMatchesSearchMask(String searchMask) {
    return String.valueOf(ID).contains(searchMask) || item.getTitle().toLowerCase().contains(searchMask);
  }

  /**
   * Gets the item associated with the auction.
   *
   * @return the item of the auction.
   */
  public Item getItem() {
    return item;
  }

  /**
   * Gets the price constraints of the auction.
   *
   * @return the price constraints of the auction.
   */
  public PriceConstraint getPriceConstraint() {
    return priceConstraint;
  }

  /**
   * Gets the seller of the auction.
   *
   * @return the seller of the auction.
   */
  public String getSeller() {
    return seller;
  }

  /**
   * Gets the end time of the auction.
   *
   * @return the end time of the auction.
   */
  public Time getEndTime() {
    return end;
  }

  /**
   * Gets the start time of the auction.
   *
   * @return the start time of the auction.
   */
  public Time getStartTime() {
    return start;
  }

  /**
   * Gets the image data of the auction item.
   *
   * @return the image data of the auction item.
   */
  public byte[] getImageData() {
    return imageData;
  }

  /**
   * Sets the image data of the auction item.
   *
   * @param imageData the image data to set.
   */
  public void setImageData(byte[] imageData) {
    this.imageData = imageData;
  }

  /**
   * Gets the current bid of the auction.
   *
   * @return the current bid of the auction.
   */
  public int getCurrentBid() {
    return currentBid;
  }

  /**
   * Sets the current bid of the auction.
   *
   * @param bid the current bid to set.
   */
  public void setCurrentBid(int bid) {
    this.currentBid = bid;
  }

  /**
   * Gets the current bidder of the auction.
   *
   * @return the current bidder of the auction.
   */
  public String getCurrentBidder() {
    return currentBidder;
  }

  /**
   * Sets the current bidder of the auction.
   *
   * @param bidder the current bidder to set.
   */
  public void setCurrentBidder(String bidder) {
    this.currentBidder = bidder;
  }

  /**
   * Sets the seller of the auction.
   *
   * @param seller the seller to set.
   */
  public void setSeller(String seller) {
    this.seller = seller;
  }

  /**
   * Gets the ID of the auction.
   *
   * @return the ID of the auction.
   */
  public int getID() {
    return ID;
  }

  /**
   * Gets the status of the auction.
   *
   * @return the status of the auction.
   */
  public String getStatus() {
    return status;
  }

  /**
   * Sets the status of the auction.
   *
   * @param status the status to set.
   */
  public void setStatus(String status) {
    this.status = status;
  }

  /**
   * Gets the ID of the auction.
   *
   * @return the ID of the auction.
   */
  public int getId() {
    return ID;
  }

  /**
   * Gets the title of the auction item.
   *
   * @return the title of the auction item.
   */
  public String getTitle() {
    return item.getTitle();
  }

  /**
   * Gets the description of the auction item.
   *
   * @return the description of the auction item.
   */
  public String getDescription() {
    return item.getDescription();
  }
}

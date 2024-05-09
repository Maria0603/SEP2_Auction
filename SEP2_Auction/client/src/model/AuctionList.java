package model;

import java.io.Serializable;
import java.sql.Time;
import java.util.ArrayList;

import java.util.List;

import java.util.StringTokenizer;

public class AuctionList implements Serializable {
  private ArrayList<Auction> auctions;
  ///////////////////////////////////////////////////////////////////
  // do not change this number
  private static final long serialVersionUID = 6529685098267757690L;
  //////////////////////////////////////////////////////////////////

  public AuctionList() {
    this.auctions = new ArrayList<>();
  }

  public void addAuction(Auction auction) {
    if (auction != null)
      auctions.add(auction);
  }

  public void removeAuction(Auction auction) {
    auctions.remove(auction);
  }

  public void removeAuction(int ID) {
    auctions.remove(getAuctionByID(ID));
  }

  public Auction getAuctionByID(int ID) {
    for (Auction auction : auctions)
      if (auction.getID() == ID)
        return auction;
    throw new IllegalArgumentException("No auction with this ID.");
  }

  public Auction getAuction(int index) {
    return auctions.get(index);
  }

  public int getSize() {
    return auctions.size();
  }

  public String toString() {
    StringBuilder s = new StringBuilder();
    for (int i = 0; i < getSize(); i++)
      s.append(auctions.get(i)).append('\n');
    return s.toString();
  }
}

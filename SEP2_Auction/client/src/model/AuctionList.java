package model;

import java.io.Serializable;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class AuctionList implements Serializable
{
  private ArrayList<Auction> auctions;
  ///////////////////////////////////////////////////////////////////
  //do not change this number
  private static final long serialVersionUID = 6529685098267757690L;
  //////////////////////////////////////////////////////////////////

  public AuctionList()
  {
    this.auctions = new ArrayList<>();
  }

  public void addAuction(Auction auction)
  {
    if (auction != null)
      auctions.add(auction);
  }

  public void removeAuction(Auction auction)
  {
    auctions.remove(auction);
  }

  public void removeAuction(int ID)
  {
    auctions.remove(getAuctionByID(ID));
  }

  public Auction getAuctionByID(int ID)
  {
    for (Auction auction : auctions)
      if (auction.getID() == ID)
        return auction;
    throw new IllegalArgumentException("No auction with this ID.");
  }

  public Auction getAuction(int index)
  {
    return auctions.get(index);
  }

  public int getSize()
  {
    return auctions.size();
  }


  @Override public String toString() {
    String out = "";
    for (Auction auction : auctions){
      out += auction.toString() + "\n";
    }
    return "AuctionList{" + out + "}";
  }

  public static void main(String[] args) {
    Auction auction = new Auction(11, "qqqq", "sfde", 22, 222, 2,
        new Time(1111111111), new Time(333333333), 4444, "dfd", "efbeflk", null, "ONGOING");
    Auction auction1 = new Auction(12, "qqaqqq", "scefde", 22, 222, 2,
        new Time(1111111111), new Time(333333333), 4444, "dfd", "efbeflk", null, "ONGOING");
    Auction auction2 = new Auction(11, "qqqq", "sfde", 22, 222, 2,
        new Time(1111111111), new Time(333333333), 4444, "dfd", "efbeflk", null, "ONGOING");

    AuctionList auctionList= new AuctionList();
    auctionList.addAuction(auction1);
    auctionList.addAuction(auction);
    auctionList.addAuction(auction2);

    System.out.println(auctionList);

    System.out.println(auctionList.searchAuctions("sfde"));
  }
}

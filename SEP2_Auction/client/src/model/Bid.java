package model;

public class Bid {

  private double reservePrice;
  private double currentBid;
  private double incomingBid;

  public Bid(double reservePrice, double currentBid, double incomingBid) {
    this.reservePrice = reservePrice;
    this.currentBid = currentBid;
    this.incomingBid = incomingBid;
  }

  public double getReservePrice() {return reservePrice;}
  public double getCurrentBid() {return currentBid;}
  public double getincomingBid() {return incomingBid;}



  public void setReservePrice() {this.reservePrice = reservePrice;}
  public void setincomingBidd(){this.incomingBid = incomingBid;}
  public void setCurrentBid() {this.currentBid = currentBid;}




  }



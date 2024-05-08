package model;

import java.io.Serializable;

public class Bid implements Serializable {
    private int auctionId;
    private String bidder;
    private int bidAmount;

    public Bid(int auctionId, String bidder, int bidAmount) {
        this.auctionId = auctionId;
        this.bidder = bidder;
        this.bidAmount = bidAmount;
    }

    public int getAuctionId() {
        return auctionId;
    }


    public String getBidder() {
        return bidder;
    }

    public void setBidder(String bidder) {
        this.bidder = bidder;
    }

    public int getBidAmount() {
        return bidAmount;
    }



    @Override
    public String toString() {
        return "Bid{" +
            ", auctionId=" + auctionId +
            ", participantEmail='" + bidder + '\'' +
            ", bidAmount=" + bidAmount +
            '}';
    }
}

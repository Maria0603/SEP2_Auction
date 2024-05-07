package model;


import java.io.Serializable;
import java.time.LocalDateTime;

public class Bid implements Serializable {
    private int bidId;
    private int auctionId;
    private String participantEmail;
    private double bidAmount;
    private LocalDateTime bidTime;

    public Bid(int bidId, int auctionId, String participantEmail, double bidAmount, LocalDateTime bidTime) {
        this.bidId = bidId;
        this.auctionId = auctionId;
        this.participantEmail = participantEmail;
        this.bidAmount = bidAmount;
        this.bidTime = bidTime;
    }

    public int getBidId() {
        return bidId;
    }

    public void setBidId(int bidId) {
        this.bidId = bidId;
    }

    public int getAuctionId() {
        return auctionId;
    }

    public void setAuctionId(int auctionId) {
        this.auctionId = auctionId;
    }

    public String getParticipantEmail() {
        return participantEmail;
    }

    public void setParticipantEmail(String participantEmail) {
        this.participantEmail = participantEmail;
    }

    public double getBidAmount() {
        return bidAmount;
    }

    public void setBidAmount(double bidAmount) {
        this.bidAmount = bidAmount;
    }

    public LocalDateTime getBidTime() {
        return bidTime;
    }

    public void setBidTime(LocalDateTime bidTime) {
        this.bidTime = bidTime;
    }

    @Override
    public String toString() {
        return "Bid{" +
                "bidId=" + bidId +
                ", auctionId=" + auctionId +
                ", participantEmail='" + participantEmail + '\'' +
                ", bidAmount=" + bidAmount +
                ", bidTime=" + bidTime +
                '}';
    }
}

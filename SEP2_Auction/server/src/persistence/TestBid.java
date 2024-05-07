package persistence;

import model.Bid;

import java.sql.SQLException;
import java.util.List;

public class TestBid {
    public static void main(String[] args) {
        try {
            testSaveBid();
            testGetBidsForAuction();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void testSaveBid() throws SQLException, ClassNotFoundException {
        AuctionDatabase bidDatabase = new AuctionDatabase();
        // Dummy data for testing
        int auctionId = 1;
        String participantEmail = "test@example.com";
        double bidAmount = 100.0;
        // Save bid
        Bid bid = bidDatabase.saveBid(auctionId, participantEmail, bidAmount);
        if (bid != null) {
            System.out.println("Bid saved successfully: " + bid);
        } else {
            System.out.println("Failed to save bid.");
        }
    }

    private static void testGetBidsForAuction() throws SQLException, ClassNotFoundException {
        AuctionDatabase bidDatabase = new AuctionDatabase();
        // Dummy auction ID for testing
        int auctionId = 1;
        // Get bids for auction
        List<Bid> bids = bidDatabase.getBidsForAuction(auctionId);
        if (bids != null && !bids.isEmpty()) {
            System.out.println("Bids for auction " + auctionId + ":");
            for (Bid bid : bids) {
                System.out.println(bid);
            }
        } else {
            System.out.println("No bids found for auction " + auctionId);
        }
    }
}

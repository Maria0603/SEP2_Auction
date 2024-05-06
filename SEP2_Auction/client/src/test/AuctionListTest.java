
package test;

import model.Auction;
import model.AuctionList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuctionListTest
{

  private AuctionList auctions;
  private Auction auction1 = new Auction(1, "teeest", "teeeeeeeeeeeeeeeeeeeeest", 1, 1, 1, null, null, 1, null, new byte[] {},
      "test");
  private Auction auction2 = new Auction(2, "teeest", "teeeeeeeeeeeeeeeeeeeeest", 2, 2, 2, null, null, 2, null, new byte[] {},
      "test");
  private Auction auction3 = new Auction(3, "teeest", "teeeeeeeeeeeeeeeeeeeeest", 3, 3, 3, null, null, 3, null, new byte[] {},
      "test");

  @BeforeEach void setUp()
  {
    auctions = new AuctionList();

    auctions.addAuction(auction1);
    auctions.addAuction(auction2);
    auctions.addAuction(auction3);
  }

  // Zero - remove null
  @Test public void remove_Auction_null_Does_Nothing()
  {
    int sizeBefore = auctions.getSize();

    auctions.removeAuction(null);

    assertEquals(auctions.getSize(), sizeBefore);
  }

  // One - remove one
  @Test public void remove_One_Auction_Removes_From_List()
  {
    int sizeBefore = auctions.getSize();

    auctions.removeAuction(auction1);

    assertEquals(auctions.getSize(), sizeBefore - 1);
  }

  // Many - remove many
  @Test public void remove_Two_Auctions_Removes_Two_Auctions_From_List()
  {
    int sizeBefore = auctions.getSize();

    auctions.removeAuction(auction1);
    auctions.removeAuction(auction2);

    assertEquals(auctions.getSize(), sizeBefore - 2);
  }

  // Zero - Remove auction with non-existing ID
  @Test
  void remove_Auction_NonExisting_ID() {
    int sizeBefore = auctions.getSize();
    auctions.removeAuction(100); // No auction with id 100
    assertEquals(sizeBefore, auctions.getSize());
  }

  // One - remove one auction with an existing ID
  @Test
  void remove_One_Auction_Existing_ID() {
    int sizeBefore = auctions.getSize();
    auctions.removeAuction(1);

    assertEquals(sizeBefore - 1, auctions.getSize());
    assertThrows(IllegalArgumentException.class, () -> auctions.getAuctionByID(1));
  }



  // Many - remove many auctions with different IDs
  @Test
  void remove_Many_Auctions_Different_IDs() {
    int sizeBefore = auctions.getSize();
    auctions.removeAuction(1);
    auctions.removeAuction(3);


    assertEquals(sizeBefore - 2, auctions.getSize());
    assertThrows(IllegalArgumentException.class, () -> auctions.getAuctionByID(1));
    assertThrows(IllegalArgumentException.class, () -> auctions.getAuctionByID(3));
  }



  @Test
  void remove_Auction_Invalid_ID() {
    assertThrows(IllegalArgumentException.class, () -> auctions.removeAuction(-1));
    assertThrows(IllegalArgumentException.class, () -> auctions.removeAuction(0));
    assertThrows(IllegalArgumentException.class, () -> auctions.removeAuction(100));
  }

  //Zero
  @Test
  void get_Auction_By_ID_Empty_List() {
    auctions = new AuctionList();
    assertThrows(IllegalArgumentException.class, () -> auctions.getAuctionByID(1));
  }

  // One - get one auction
  @Test
  void get_Auction_By_ID_One_Auction() {
    Auction auction = auctions.getAuctionByID(1);
    assertNotNull(auction);
    assertEquals(1, auction.getID());
  }

  // Many - get multiple auctions
  @Test
  void get_Auction_By_ID_Many_Auctions() {
    Auction auction = auctions.getAuctionByID(2);
    assertNotNull(auction);
    assertEquals(2, auction.getID());
  }

  // Boundary conditions
  @Test
  void get_Auction_By_ID_At_Boundary() {
    Auction firstAuction = auctions.getAuctionByID(1);
    assertNotNull(firstAuction);
    assertEquals(1, firstAuction.getID());

    Auction lastAuction = auctions.getAuctionByID(3);
    assertNotNull(lastAuction);
    assertEquals(3, lastAuction.getID());
  }

  // Exceptional - Retrieve auction with an invalid or non-existing ID
  @Test
  void get_Auction_By_ID_Invalid_ID() {
    assertThrows(IllegalArgumentException.class, () -> auctions.getAuctionByID(-1)); // Negative ID
    assertThrows(IllegalArgumentException.class, () -> auctions.getAuctionByID(0)); // Zero ID
    assertThrows(IllegalArgumentException.class, () -> auctions.getAuctionByID(100)); // Non-existing ID
  }
}



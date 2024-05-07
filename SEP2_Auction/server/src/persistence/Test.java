package persistence;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class Test {
  public static void main(String[] args)
      throws IOException, SQLException, ClassNotFoundException {
    BufferedImage image = ImageIO.read(new File("server\\images\\12.jpg"));

    ByteArrayOutputStream outStreamObj = new ByteArrayOutputStream();
    ImageIO.write(image, "jpg", outStreamObj);

    byte[] byteArray = outStreamObj.toByteArray();

    AuctionDatabase database = new AuctionDatabase();


    database.saveAuction("titleeeeeeeee", "defwefbiptbprfnrjvbrbo", 500, 55555, 50, 5, byteArray);
    database.getAuctionById(12);
  }
  private static void testBidDatabase() throws SQLException, ClassNotFoundException {
    AuctionDatabase bidDatabase = new AuctionDatabase();
    bidDatabase.saveBid(1, "test@example.com", 100.0);
    bidDatabase.getBidsForAuction(1);
  }
}

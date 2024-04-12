package model;

import java.beans.PropertyChangeListener;

public class AuctionModelManager implements AuctionModel
{
  @Override public Auction startAuction(int ID, String title, String description,
      int reservePrice, int buyoutPrice, int minimumIncrement, int auctionTime,
      String imagePath)
  {
    return new Auction();
  }

  @Override public int generateID()
  {
    return 0;
  }

  @Override public Auction getAuction(int ID)
  {
    return null;
  }

  @Override public void addListener(String propertyName,
      PropertyChangeListener listener)
  {

  }

  @Override public void removeListener(String propertyName,
      PropertyChangeListener listener)
  {

  }
}

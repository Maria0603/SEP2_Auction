package model;

import java.io.Serializable;
import java.sql.Time;

public class AuctionShortVersion implements Serializable
{
  private int currentBid, id;
  private String title;
  private Time end;
  private byte[] imageData;
  ///////////////////////////////////////////////////////////////////
  //do not change this number
  private static final long serialVersionUID = 6529685098267757690L;
  //////////////////////////////////////////////////////////////////

  public AuctionShortVersion(int id, String title, int currentBid, Time end, byte[] imageData)
  {
    this.id=id;
    this.title=title;
    this.currentBid=currentBid;
    this.end=end;
    this.imageData=imageData;
  }
  public int getCurrentBid()
  {
    return currentBid;
  }

  public int getId()
  {
    return id;
  }

  public String getTitle()
  {
    return title;
  }

  public Time getEnd()
  {
    return end;
  }

  public byte[] getImageData()
  {
    return imageData;
  }

}

package model;

import java.io.Serial;
import java.io.Serializable;

public class Item implements Serializable
{
  ///////////////////////////////////////////////////////////////////
  //do not change this number
  @Serial private static final long serialVersionUID = 6529685098267757690L;
  //////////////////////////////////////////////////////////////////

  private String title, description;

  public Item(String title, String description)
  {
    setTitle(title);
    setDescription(description);
  }

  public String getTitle()
  {
    return title;
  }

  public void setTitle(String title)
  {
    this.title = title;
  }

  public String getDescription()
  {
    return description;
  }

  public void setDescription(String description)
  {
    this.description = description;
  }
}

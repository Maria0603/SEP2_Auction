package model;

import java.io.Serializable;

public class Item implements Serializable
{
  ///////////////////////////////////////////////////////////////////
  //do not change this number
  private static final long serialVersionUID = 6529685098267757690L;
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
    int maxTitleLength = 80;
    int minTitleLength = 5;
    if (title.length() > maxTitleLength)
      throw new IllegalArgumentException("The title is too long!");
    else if (title.length() < minTitleLength)
      throw new IllegalArgumentException("The title is too short!");
    this.title = title;
  }

  public String getDescription()
  {
    return description;
  }

  public void setDescription(String description)
  {
    int maxDescriptionLength = 1400, minDescriptionLength = 20;
    if (description.length() > maxDescriptionLength)
      throw new IllegalArgumentException("The description is too long!");
    else if (description.length() < minDescriptionLength)
      throw new IllegalArgumentException("The description is too short!");
    this.description = description;
  }
}

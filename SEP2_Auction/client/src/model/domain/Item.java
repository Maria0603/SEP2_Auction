package model.domain;

import java.io.Serial;
import java.io.Serializable;

/**
 * The Item class represents an item with a title and description.
 * It implements Serializable for object serialization.
 */
public class Item implements Serializable {

  ///////////////////////////////////////////////////////////////////
  // Do not change this number
  @Serial
  private static final long serialVersionUID = 6529685098267757690L;
  //////////////////////////////////////////////////////////////////

  private String title;
  private String description;

  /**
   * Constructs a new Item object with the specified title and description.
   *
   * @param title the title of the item.
   * @param description the description of the item.
   */
  public Item(String title, String description) {
    setTitle(title);
    setDescription(description);
  }

  /**
   * Gets the title of the item.
   *
   * @return the title of the item.
   */
  public String getTitle() {
    return title;
  }

  /**
   * Sets the title of the item.
   *
   * @param title the title to set.
   */
  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * Gets the description of the item.
   *
   * @return the description of the item.
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets the description of the item.
   *
   * @param description the description to set.
   */
  public void setDescription(String description) {
    this.description = description;
  }
}

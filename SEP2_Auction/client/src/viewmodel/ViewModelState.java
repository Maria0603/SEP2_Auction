package viewmodel;

import model.domain.Auction;

/**
 * Manages the state of the view model, including selected auction, user email, and view modes.
 */
public class ViewModelState {
  private Auction selectedAuction;
  private String userEmail;
  // For windows
  private boolean bids, createdAuctions, allAuctions;
  private boolean create, login, resetPassword, edit, display;
  private boolean moderator;
  private boolean lookingAtModerator;

  /**
   * Constructs a new ViewModelState with initial values.
   */
  public ViewModelState() {
    selectedAuction = null;
    userEmail = null;
    moderator = false;
    lookingAtModerator = false;
    setAllAuctions();
    setCreate();
  }

  /**
   * Sets the selected auction.
   * @param auction the selected auction
   */
  public void setAuction(Auction auction) {
    this.selectedAuction = auction;
  }

  /**
   * Gets the selected auction.
   * @return the selected auction
   */
  public Auction getSelectedAuction() {
    return selectedAuction;
  }

  /**
   * Gets the user email.
   * @return the user email
   */
  public String getUserEmail() {
    return userEmail;
  }

  /**
   * Sets whether the user is a moderator.
   * @param isModerator true if the user is a moderator, false otherwise
   */
  public void setModerator(boolean isModerator) {
    this.moderator = isModerator;
  }

  /**
   * Sets whether the user is looking at moderator information.
   * @param looking true if the user is looking at moderator information, false otherwise
   */
  public void setLookingAtModerator(boolean looking) {
    this.lookingAtModerator = looking;
  }

  /**
   * Checks if the user is looking at moderator information.
   * @return true if the user is looking at moderator information, false otherwise
   */
  public boolean isLookingAtModerator() {
    return lookingAtModerator;
  }

  /**
   * Checks if the user is a moderator.
   * @return true if the user is a moderator, false otherwise
   */
  public boolean isModerator() {
    return moderator;
  }

  /**
   * Sets the user email.
   * @param userEmail the user email
   */
  public void setUserEmail(String userEmail) {
    this.userEmail = userEmail;
  }

  private void setAllCreatedBids(boolean all, boolean created, boolean bids) {
    this.allAuctions = all;
    this.createdAuctions = created;
    this.bids = bids;
  }

  /**
   * Sets the view state to show bids.
   */
  public void setBids() {
    setAllCreatedBids(false, false, true);
  }

  /**
   * Sets the view state to show created auctions.
   */
  public void setCreatedAuctions() {
    setAllCreatedBids(false, true, false);
  }

  /**
   * Sets the view state to show all auctions.
   */
  public void setAllAuctions() {
    setAllCreatedBids(true, false, false);
  }

  private void setCreateLoginResetEditDisplay(boolean create, boolean login, boolean reset, boolean edit, boolean display) {
    this.create = create;
    this.login = login;
    this.resetPassword = reset;
    this.edit = edit;
    this.display = display;
  }

  /**
   * Sets the view state to create mode.
   */
  public void setCreate() {
    setCreateLoginResetEditDisplay(true, false, false, false, false);
  }

  /**
   * Sets the view state to login mode.
   */
  public void setLogin() {
    setCreateLoginResetEditDisplay(false, true, false, false, false);
  }

  /**
   * Sets the view state to reset password mode.
   */
  public void setResetPassword() {
    setCreateLoginResetEditDisplay(false, false, true, false, false);
  }

  /**
   * Sets the view state to edit mode.
   */
  public void setEdit() {
    setCreateLoginResetEditDisplay(false, false, false, true, false);
  }

  /**
   * Sets the view state to display mode.
   */
  public void setDisplay() {
    setCreateLoginResetEditDisplay(false, false, false, false, true);
  }

  /**
   * Checks if the view state is showing bids.
   * @return true if showing bids, false otherwise
   */
  public boolean getBids() {
    return bids;
  }

  /**
   * Checks if the view state is showing created auctions.
   * @return true if showing created auctions, false otherwise
   */
  public boolean getCreatedAuctions() {
    return createdAuctions;
  }

  /**
   * Checks if the view state is showing all auctions.
   * @return true if showing all auctions, false otherwise
   */
  public boolean getAllAuctions() {
    return allAuctions;
  }

  /**
   * Checks if the view state is in create mode.
   * @return true if in create mode, false otherwise
   */
  public boolean isCreate() {
    return create;
  }

  /**
   * Checks if the view state is in login mode.
   * @return true if in login mode, false otherwise
   */
  public boolean isLogin() {
    return login;
  }

  /**
   * Checks if the view state is in reset password mode.
   * @return true if in reset password mode, false otherwise
   */
  public boolean isResetPassword() {
    return resetPassword;
  }

  /**
   * Checks if the view state is in edit mode.
   * @return true if in edit mode, false otherwise
   */
  public boolean isEdit() {
    return edit;
  }

  /**
   * Checks if the view state is in display mode.
   * @return true if in display mode, false otherwise
   */
  public boolean isDisplay() {
    return display;
  }
}

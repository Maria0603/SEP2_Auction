package model;

public abstract class Cache
{
  private String userEmail;
  public Cache()
  {
    this.userEmail=null;
  }
  public void setUserEmail(String userEmail)
  {
    this.userEmail=userEmail;
  }
  public String getUserEmail()
  {
    return userEmail;
  }
}

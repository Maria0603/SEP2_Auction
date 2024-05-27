package model;

public abstract class Cache
{
  protected static String userEmail=null;
  public Cache()
  {

  }
  public void setUserEmail(String userEmail)
  {
    Cache.userEmail =userEmail;
  }
  public String getUserEmail()
  {
    return userEmail;
  }
}

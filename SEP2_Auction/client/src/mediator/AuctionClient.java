package mediator;

import model.*;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

import utility.observer.event.ObserverEvent;
import utility.observer.listener.RemoteListener;

public class AuctionClient
    implements RemoteListener<String, Object>, AuctionModel
{
  private AuctionRemote server;
  private PropertyChangeSupport property;

  public AuctionClient() throws IOException
  {
    start();
    property = new PropertyChangeSupport(this);
  }

  // establish server connection
  private void start()
  {
    try
    {
      UnicastRemoteObject.exportObject(this, 0);
      server = (AuctionRemote) Naming.lookup("rmi://localhost:1099/Connect");

      server.addListener(this, "Auction");
      server.addListener(this, "Time");
      server.addListener(this, "End");
      server.addListener(this,"Bid");
      server.addListener(this, "Notification");
      server.addListener(this, "Edit");
      server.addListener(this, "Ban");
      server.addListener(this, "Reset");

    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  @Override public Auction startAuction(String title, String description,
      int reservePrice, int buyoutPrice, int minimumIncrement, int auctionTime,
      byte[] imageData, String seller) throws SQLException, ClassNotFoundException
  {
    try
    {
      return server.startAuction(title, description, reservePrice, buyoutPrice,
          minimumIncrement, auctionTime, imageData, seller);
    }
    catch (RemoteException e)
    {
      e.printStackTrace();
    }
    return null;
  }

  @Override public Auction getAuction(int ID) throws SQLException
  {
    try
    {
      return server.getAuction(ID);
    }
    catch (RemoteException e)
    {
      e.printStackTrace();
    }
    return null;
  }

  @Override public AuctionList getOngoingAuctions() throws SQLException
  {
    try
    {
      return server.getOngoingAuctions();
    }
    catch (RemoteException e)
    {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public NotificationList getNotifications(String receiver) throws SQLException {
    try {
      return server.getNotifications(receiver);
    } catch (RemoteException e) {
      return null;
    }
  }

  @Override
  public Bid placeBid(String bidder, int bidValue, int auctionId) throws SQLException {
    try {
      return server.placeBid(bidder, bidValue, auctionId);
    } catch (RemoteException e) {
      return null;
    }
  }


  @Override
  public String addUser(String firstname, String lastname, String email, String password, String repeatedPassword, String phone, LocalDate birthday) throws SQLException {
    try {
      return server.addUser(firstname,lastname,email,password,repeatedPassword, phone, birthday);
    } catch (RemoteException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public String login(String email, String password) throws SQLException {
    try {
      return server.login(email,password);
    } catch (RemoteException e) {
      return null;
    }
  }

  @Override public AuctionList getPreviousBids(String bidder)
      throws SQLException
  {
    try
    {
      return server.getPreviousBids(bidder);
    }
    catch(RemoteException e)
    {
      e.printStackTrace();
    }
    return null;
  }

  @Override public AuctionList getCreatedAuctions(String seller)
      throws SQLException
  {
    try
    {
      return server.getCreatedAuctions(seller);
    }
    catch (RemoteException e)
    {
      e.printStackTrace();
    }
    return null;
  }

  @Override public void resetPassword(String userEmail, String oldPassword,
      String newPassword, String repeatPassword) throws SQLException
  {
    try
    {
      server.resetPassword(userEmail, oldPassword, newPassword, repeatPassword);
    }
    catch(RemoteException e)
    {
      e.printStackTrace();
    }
  }

  @Override public User getUser(String email) throws SQLException
  {
    try
    {
      return server.getUser(email);
    }
    catch(RemoteException e)
    {
      e.printStackTrace();
    }
    return null;
  }
  @Override public User getModeratorInfo() throws SQLException
  {
    try
    {
      return server.getModeratorInfo();
    }
    catch(RemoteException e)
    {
      e.printStackTrace();
    }
    return null;
  }

  @Override public boolean isModerator(String email) throws SQLException
  {
    try
    {
      return server.isModerator(email);
    }
    catch(RemoteException e)
    {
      e.printStackTrace();
    }
    return false;
  }

  @Override public User editInformation(String oldEmail, String firstname,
      String lastname, String email, String password, String phone,
      LocalDate birthday) throws SQLException
  {
    try
    {
      return server.editInformation(oldEmail, firstname, lastname, email,
          password, phone, birthday);
    }
    catch(RemoteException e)
    {
      e.printStackTrace();
    }
    return null;
  }
  @Override public AuctionList getAllAuctions() throws SQLException
  {
    try
    {
      return server.getAllAuctions();
    }
    catch(RemoteException e)
    {
      e.printStackTrace();
    }
    return null;
  }

  @Override public void buyOut(String bidder, int auctionId)
      throws SQLException {
    try {
      server.buyOut(bidder, auctionId);
    } catch (RemoteException e) {
      e.printStackTrace();
    }

  }

  @Override public ArrayList<User> getAllUsers()
      throws SQLException {
    try
    {
      return server.getAllUsers();
    }
    catch(RemoteException e)
    {
      e.printStackTrace();
    }
    return null;
  }
  @Override public void banParticipant(String moderatorEmail,
      String participantEmail, String reason) throws SQLException
  {
    try
    {
      server.banParticipant(moderatorEmail, participantEmail, reason);
    }
    catch(RemoteException e)
    {
      e.printStackTrace();
    }
  }

  @Override public String extractBanningReason(String email) throws SQLException
  {
    try
    {
      return server.extractBanningReason(email);
    }
    catch(RemoteException e)
    {
      e.printStackTrace();
    }
    return null;
  }

  @Override public void unbanParticipant(String moderatorEmail,
      String participantEmail) throws SQLException
  {
    try
    {
      server.unbanParticipant(moderatorEmail, participantEmail);
    }
    catch(RemoteException e)
    {
      e.printStackTrace();
    }
  }

  @Override public void addListener(String s,
      PropertyChangeListener propertyChangeListener)
  {
    property.addPropertyChangeListener(s, propertyChangeListener);
  }

  @Override public void removeListener(String s,
      PropertyChangeListener propertyChangeListener)
  {
    property.removePropertyChangeListener(s, propertyChangeListener);
  }

  @Override public void propertyChange(ObserverEvent<String, Object> event)
      throws RemoteException
  {
    property.firePropertyChange(event.getPropertyName(), event.getValue1(),
        event.getValue2());
  }

}

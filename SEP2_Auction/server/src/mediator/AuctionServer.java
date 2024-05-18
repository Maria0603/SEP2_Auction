package mediator;

import model.*;
import utility.observer.listener.GeneralListener;
import utility.observer.subject.PropertyChangeHandler;
import utility.observer.subject.RemoteSubject;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public class AuctionServer
    implements AuctionRemote, RemoteSubject<String, Object>,
    PropertyChangeListener
{
  private AuctionModel model;
  private PropertyChangeHandler<String, Object> property;

  public AuctionServer(AuctionModel model)
      throws MalformedURLException, RemoteException
  {
    this.model = model;
    property = new PropertyChangeHandler<>(this, true);

    model.addListener("Auction", this);
    model.addListener("Time", this);
    model.addListener("End", this);
    model.addListener("Bid", this);
    model.addListener("Notification", this);
    model.addListener("Edit", this);

    startRegistry();
    startServer();
  }

  private void startRegistry()
  {
    try
    {
      Registry reg = LocateRegistry.createRegistry(1099);
      System.out.println("Registry started...");
    }
    catch (RemoteException e)
    {
      e.printStackTrace();
    }
  }

  private void startServer() throws RemoteException, MalformedURLException
  {
    UnicastRemoteObject.exportObject(this, 0);
    Naming.rebind("Connect", this);
  }

  @Override public synchronized Auction startAuction(String title, String description,
      int reservePrice, int buyoutPrice, int minimumIncrement, int auctionTime,
      byte[] imageData, String seller)
      throws RemoteException, SQLException, ClassNotFoundException
  {
    return model.startAuction(title, description, reservePrice, buyoutPrice,
        minimumIncrement, auctionTime, imageData, seller);
  }

  @Override public synchronized Auction getAuction(int id)
      throws RemoteException, SQLException
  {
    return model.getAuction(id);
  }

  @Override public synchronized AuctionList getOngoingAuctions()
      throws RemoteException, SQLException
  {
    return model.getOngoingAuctions();
  }


  @Override public synchronized NotificationList getNotifications(String receiver)
      throws RemoteException, SQLException
  {
    return model.getNotifications(receiver);
  }

  @Override public synchronized Bid placeBid(String bidder, int bidValue, int auctionId)
      throws RemoteException, SQLException
  {
    return model.placeBid(bidder, bidValue, auctionId);
  }

  @Override
  public synchronized String buyOut(String bidder, int auctionId)
      throws RemoteException, SQLException {
    try {
      model.buyOut(bidder, auctionId);
    } catch (SQLException e) {
      e.printStackTrace();
      return "Failed to process buyout";
    }
    return "Buyout successful!";
  }

  @Override public ArrayList<User> getAllUsers() throws SQLException {
    return model.getAllUsers();
  }

  @Override
  public synchronized String addUser(String firstname, String lastname, String email, String password, String repeatedPassword, String phone, LocalDate birthday) throws SQLException {
    return model.addUser(firstname,lastname,email,password, repeatedPassword, phone, birthday);
  }

  @Override
  public synchronized String login(String email, String password) throws SQLException {
    System.out.println("AuctionServer: " + email + ", " + password);
    return model.login(email,password);
  }

  @Override public synchronized AuctionList getPreviousBids(String bidder) throws SQLException
  {
    return model.getPreviousBids(bidder);
  }

  @Override public AuctionList getCreatedAuctions(String seller)
      throws RemoteException, SQLException
  {
    return model.getCreatedAuctions(seller);
  }

  @Override public synchronized void resetPassword(String userEmail, String oldPassword,
      String newPassword, String repeatPassword)
      throws RemoteException, SQLException
  {
    model.resetPassword(userEmail, oldPassword, newPassword, repeatPassword);
  }

  @Override public synchronized User getUser(String email) throws RemoteException, SQLException
  {
    return model.getUser(email);
  }

  @Override public User getModeratorInfo() throws SQLException
  {
    return model.getModeratorInfo();
  }

  @Override public synchronized boolean isModerator(String email)
      throws RemoteException, SQLException
  {
    return model.isModerator(email);
  }

  @Override public User editInformation(String oldEmail, String firstname,
      String lastname, String email, String password, String phone,
      LocalDate birthday) throws RemoteException, SQLException
  {
    return model.editInformation(oldEmail, firstname, lastname, email, password, phone, birthday);
  }

  @Override public AuctionList getAllAuctions() throws SQLException
  {
    return model.getAllAuctions();
  }

  @Override public synchronized boolean addListener(GeneralListener<String, Object> listener,
      String... propertyNames) throws RemoteException
  {
    return property.addListener(listener, propertyNames);
  }

  @Override public synchronized boolean removeListener(
      GeneralListener<String, Object> listener, String... propertyNames)
      throws RemoteException
  {
    return property.removeListener(listener, propertyNames);
  }

  @Override public synchronized void propertyChange(PropertyChangeEvent evt)
  {
    property.firePropertyChange(evt.getPropertyName(),
        String.valueOf(evt.getOldValue()), evt.getNewValue());
  }
}
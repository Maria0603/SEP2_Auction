package mediator;

import model.*;
import utility.observer.listener.GeneralListener;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;

public interface AuctionRemote extends Remote
{
  Auction startAuction(String title, String description, int reservePrice,
      int buyoutPrice, int minimumIncrement, int auctionTime, byte[] imageData, String seller)
      throws RemoteException, SQLException, ClassNotFoundException;
  Auction getAuction(int ID) throws RemoteException, SQLException;
  AuctionList getOngoingAuctions() throws RemoteException, SQLException;

  NotificationList getNotifications(String receiver) throws RemoteException, SQLException;
  Bid placeBid(String bidder, int bidValue, int auctionId) throws RemoteException, SQLException;


  String addUser(String firstname,String lastname, String email, String password, String repeatedPassword, String phone, LocalDate birthday) throws RemoteException, SQLException;
  String login(String email, String password) throws RemoteException, SQLException;

  boolean addListener(GeneralListener<String, Object> listener,
      String... propertyNames) throws RemoteException;
  boolean removeListener(GeneralListener<String, Object> listener,
      String... propertyNames) throws RemoteException;
}

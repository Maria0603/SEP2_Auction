package mediator;

import model.Auction;
import model.AuctionList;
import model.Bid;
import model.NotificationList;
import utility.observer.listener.GeneralListener;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;

public interface AuctionRemote extends Remote
{
  Auction startAuction(String title, String description, int reservePrice,
                       int buyoutPrice, int minimumIncrement, int auctionTime, byte[] imageData)
          throws RemoteException, SQLException, ClassNotFoundException;
  Auction getAuction(int ID) throws RemoteException, SQLException;
  AuctionList getOngoingAuctions() throws RemoteException, SQLException;

  NotificationList getNotifications(String receiver) throws RemoteException, SQLException;
  Bid placeBid(String bidder, int bidValue, int auctionId) throws RemoteException, SQLException;


  void addUser(String firstname,String lastname, String email, String password, String phone) throws RemoteException;
  String getUser(String email, String password) throws RemoteException;

  boolean addListener(GeneralListener<String, Object> listener,
                      String... propertyNames) throws RemoteException;
  boolean removeListener(GeneralListener<String, Object> listener,
                         String... propertyNames) throws RemoteException;
}

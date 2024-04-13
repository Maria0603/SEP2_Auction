package mediator;

import model.Auction;
import utility.observer.listener.GeneralListener;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface AuctionRemote extends Remote
{
  Auction startAuction(int ID, String title, String description, int reservePrice, int buyoutPrice, int minimumIncrement, int auctionTime, String imagePath) throws RemoteException;
  int generateID() throws RemoteException;
  Auction getAuction(int ID) throws RemoteException;
  boolean addListener(GeneralListener<String, Object> listener, String... propertyNames)
      throws RemoteException;
  boolean removeListener(
      GeneralListener<String, Object> listener, String... propertyNames)
      throws RemoteException;
}

package mediator;

import model.domain.Auction;
import model.domain.Bid;
import utility.observer.listener.GeneralListener;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;

public interface AuctionRemote extends Remote
{
  Auction startAuction(String title, String description, int reservePrice,
      int buyoutPrice, int minimumIncrement, int auctionTime, byte[] imageData,
      String seller)
      throws RemoteException, SQLException, ClassNotFoundException;
  Auction getAuction(int ID) throws RemoteException, SQLException;
  Bid placeBid(String bidder, int bidValue, int auctionId)
      throws RemoteException, SQLException;
  void buyout(String bidder, int auctionId)
      throws RemoteException, SQLException;
  void deleteAuction(String moderatorEmail, int auctionId, String reason)
      throws RemoteException, SQLException;

  boolean addListener(GeneralListener<String, Object> listener,
      String... propertyNames) throws RemoteException;
  boolean removeListener(GeneralListener<String, Object> listener,
      String... propertyNames) throws RemoteException;
}

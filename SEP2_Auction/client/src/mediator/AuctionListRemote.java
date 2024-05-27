package mediator;

import model.domain.Auction;
import model.domain.AuctionList;
import utility.observer.listener.GeneralListener;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;

public interface AuctionListRemote extends Remote
{
  AuctionList getOngoingAuctions() throws RemoteException, SQLException;
  AuctionList getPreviousBids(String bidder)
      throws RemoteException, SQLException;
  AuctionList getCreatedAuctions(String seller)
      throws RemoteException, SQLException;
  AuctionList getAllAuctions(String moderatorEmail)
      throws RemoteException, SQLException;
  Auction getAuction(int ID) throws RemoteException, SQLException;
  boolean isModerator(String email) throws RemoteException, SQLException;

  boolean addListener(GeneralListener<String, Object> listener,
      String... propertyNames) throws RemoteException;
  boolean removeListener(GeneralListener<String, Object> listener,
      String... propertyNames) throws RemoteException;
}

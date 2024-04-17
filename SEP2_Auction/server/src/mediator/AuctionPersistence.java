package mediator;

import model.Auction;

import java.sql.SQLException;

public interface AuctionPersistence
{
  Auction loadOngoing() throws SQLException;
  Auction loadClosed() throws SQLException;
  void save(Auction auction) throws SQLException;
  void remove(Auction auction) throws SQLException;
  void clear() throws SQLException;
}

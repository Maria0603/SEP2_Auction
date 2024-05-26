import javafx.application.Application;
import javafx.stage.Stage;
import mediator.AuctionListServer;
import mediator.AuctionServer;
import mediator.UserListServer;
import mediator.UserServer;
import model.*;

import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.SQLException;

public class MyApplication extends Application
{
  private AuctionServer server;

  @Override public void start(Stage primaryStage)
  {
    try
    {
      AuctionModel auctionModel = new AuctionModelManager();
      AuctionListModel auctionListModel=new AuctionListModelManager();
      UserModel userModel=new UserModelManager();
      UserListModel userListModel=new UserListModelManager();
      startRegistry();
      //server = new AuctionServer(model);
      new AuctionServer(auctionModel);
      new AuctionListServer(auctionListModel);
      new UserServer(userModel);
      new UserListServer(userListModel);
    }
    catch (MalformedURLException | RemoteException | ClassNotFoundException |
           SQLException e)
    {
      e.printStackTrace();
    }

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
}

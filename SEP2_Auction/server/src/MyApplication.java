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
      ListenerSubjectInterface listenerSubject=new ListenerSubject(auctionModel, auctionListModel, userModel, userListModel);
      new AuctionServer(auctionModel, listenerSubject);
      new AuctionListServer(auctionListModel, listenerSubject);
      new UserServer(userModel, listenerSubject);
      new UserListServer(userListModel, listenerSubject);
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
      Registry regi = LocateRegistry.getRegistry("localhost", 1099);
      String[] boundNames = regi.list();
      for (String name : boundNames)
        System.out.println("Bound name: " + name);
      System.out.println("Registry started...");
    }
    catch (RemoteException e)
    {
      e.printStackTrace();
    }
  }
}

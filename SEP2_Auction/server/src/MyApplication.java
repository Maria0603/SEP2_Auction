import javafx.application.Application;
import javafx.stage.Stage;
import mediator.AuctionServer;
import model.AuctionModel;
import model.AuctionModelManager;

import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.sql.SQLException;

public class MyApplication extends Application
{
  private AuctionServer server;

  @Override public void start(Stage primaryStage)
  {
    try
    {
      AuctionModel model = new AuctionModelManager();
      server = new AuctionServer(model);
    }
    catch (MalformedURLException | RemoteException | ClassNotFoundException |
           SQLException e)
    {
      e.printStackTrace();
    }

  }
}

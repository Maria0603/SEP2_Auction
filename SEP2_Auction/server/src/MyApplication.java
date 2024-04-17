import javafx.application.Application;
import javafx.stage.Stage;
import mediator.AuctionServer;
import model.AuctionModel;
import model.AuctionModelManager;

import java.net.MalformedURLException;
import java.rmi.RemoteException;

public class MyApplication extends Application
{
  private AuctionServer server;

  @Override public void start(Stage primaryStage)
  {
    AuctionModel model = new AuctionModelManager();
    try
    {
      server=new AuctionServer(model);
    }
    catch (MalformedURLException | RemoteException e)
    {
      e.printStackTrace();
    }

  }
}

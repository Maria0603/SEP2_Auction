import javafx.application.Application;
import javafx.stage.Stage;
import mediator.AuctionClient;
import mediator.AuctionListClient;
import mediator.UserClient;
import mediator.UserListClient;
import model.*;
import view.ViewHandler;
import viewmodel.ViewModelFactory;

import java.io.IOException;
import java.sql.SQLException;

public class MyApplication extends Application
{
  @Override public void start(Stage primaryStage)
  {
    try
    {
      AuctionModel auctionModel = new AuctionCacheProxy();
      AuctionListModel auctionListModel = new AuctionListCacheProxy();
      UserModel userModel = new UserCacheProxy();
      UserListModel userListModel=new UserListCacheProxy();
      ViewModelFactory viewModelFactory = new ViewModelFactory(auctionModel, auctionListModel, userModel, userListModel);
      ViewHandler view = new ViewHandler(viewModelFactory);
      new AuctionClient();
      new AuctionListClient();
      new UserClient();
      new UserListClient();
      view.start(primaryStage);
    }
    catch (IOException | SQLException e)
    {
      e.printStackTrace();
    }

  }
}

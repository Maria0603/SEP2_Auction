import javafx.application.Application;
import javafx.stage.Stage;
import mediator.AuctionClient;
import model.AuctionModel;
import model.AuctionModelManager;
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
      AuctionModel model = new AuctionModelManager();
      ViewModelFactory viewModelFactory = new ViewModelFactory(model);
      ViewHandler view = new ViewHandler(viewModelFactory);
      AuctionClient client = new AuctionClient();
      view.start(primaryStage);
    }
    catch (IOException | SQLException e)
    {
      e.printStackTrace();
    }

  }
}

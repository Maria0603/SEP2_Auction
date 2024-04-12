package view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import viewmodel.AuctionViewModel;
import viewmodel.ViewModelFactory;

public class ViewHandler
{
  private Stage primaryStage;
  private Scene currentScene;
  private ViewModelFactory viewModelFactory;
  private FixedPaneViewController fixedPaneViewController;
  private String id;


  public ViewHandler(ViewModelFactory viewModelFactory)
  {
    this.viewModelFactory = viewModelFactory;
    currentScene = new Scene(new Region());
    ///////////////////////
    id="startAuction";
  }

  public void start(Stage primaryStage)
  {
    this.primaryStage = primaryStage;
    openView("startAuction");
  }

  public void openView(String id)
  {
    Region root = null;
    switch (id)
    {
      case "startAuction", "displayAuction":
        root = loadFixedPaneView("FixedPaneView.fxml", id, viewModelFactory.getAuctionViewModel());
        break;
      //case "login":
      //case "createAccount":
    }
    currentScene.setRoot(root);
    String title = "";
    if (root.getUserData() != null)
    {
      title += root.getUserData();
    }

    primaryStage.setTitle(title);
    primaryStage.setScene(currentScene);

    primaryStage.show();
  }

  public void closeView()
  {
    primaryStage.close();
  }

  private Region loadFixedPaneView(String fxmlFile, String id, Object auctionViewModel)
  {
    if (fixedPaneViewController == null)
    {
      try
      {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(fxmlFile));
        Region root = loader.load();
        fixedPaneViewController = loader.getController();
        fixedPaneViewController.init(this, viewModelFactory.getFixedPaneViewModel(), viewModelFactory, root, id);
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }
    else
    {
      fixedPaneViewController.reset();
    }
    return fixedPaneViewController.getRoot();
  }


}

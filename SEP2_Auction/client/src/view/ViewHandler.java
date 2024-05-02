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

  public ViewHandler(ViewModelFactory viewModelFactory)
  {
    this.viewModelFactory = viewModelFactory;
    currentScene = new Scene(new Region());
  }

  public void start(Stage primaryStage)
  {
    this.primaryStage = primaryStage;
    openView("startAuction");
    //openView("allAuctions");
  }

  public void openView(String id)
  {
    Region root = null;
    if (id.equals("startAuction") || id.equals("displayAuction") || id.equals(
        "allAuctions"))
    {
      root = loadFixedPaneView("FixedPaneView.fxml", id);
    }
    /*switch (id)
    {
      case "startAuction", "displayAuction", "allAuctions":
        root = loadFixedPaneView("FixedPaneView.fxml", id);

      //case "login":
      //case "createAccount":
    }
     */
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

  private Region loadFixedPaneView(String fxmlFile, String id)
  {
    if (fixedPaneViewController == null)
    {
      try
      {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(fxmlFile));
        Region root = loader.load();
        fixedPaneViewController = loader.getController();

        fixedPaneViewController.init(this,
            viewModelFactory.getFixedPaneViewModel(), viewModelFactory, root,
            id);
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }
    else
    {
      fixedPaneViewController.reset(id);
    }
    return fixedPaneViewController.getRoot();
  }

}

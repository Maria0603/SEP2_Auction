package view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import viewmodel.ViewModelFactory;

public class ViewHandler {
  private Stage primaryStage;
  private Scene currentScene;
  private ViewModelFactory viewModelFactory;
  private FixedPaneViewHandler fixedPaneViewController;

  public ViewHandler(ViewModelFactory viewModelFactory) {
    this.viewModelFactory = viewModelFactory;
    currentScene = new Scene(new Region());
  }

  public void start(Stage primaryStage) {
    this.primaryStage = primaryStage;
    //openView("startAuction");
    //openView("allAuctions");
    openView(WindowType.ALL_AUCTIONS);
  }

  public void openView(WindowType type) {
    Region root = null;
    switch (type) {
      case START_AUCTION, DISPLAY_AUCTION, ALL_AUCTIONS ->{
          root = loadFixedPaneView("FixedPaneView.fxml", type);
      }
      case SING_UP, LOG_IN -> {
        //for future
      }
      default -> {
        System.out.println("Unexpected value: " + type);
      }
    }
    currentScene.setRoot(root);

    String title = "";
    if (root.getUserData() != null) {
      title += root.getUserData();
    }

    primaryStage.setTitle(title);
    primaryStage.setScene(currentScene);
    primaryStage.show();
  }


  public void closeView() {
    primaryStage.close();
  }


  private Region loadFixedPaneView(String fxmlFile, WindowType windowType) {
    if (fixedPaneViewController == null) {
      try {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(fxmlFile));
        Region root = loader.load();
        fixedPaneViewController = loader.getController();

        fixedPaneViewController.init(this,
            viewModelFactory.getFixedPaneViewModel(), viewModelFactory, root,
            windowType);
      }
      catch (Exception e) {
        e.printStackTrace();
      }
    }
    else {
      fixedPaneViewController.reset(windowType);
    }
    return fixedPaneViewController.getRoot();
  }
}


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
  private CreateLoginViewController createLoginViewController;

  public ViewHandler(ViewModelFactory viewModelFactory) {
    this.viewModelFactory = viewModelFactory;
    currentScene = new Scene(new Region());
  }

  public void start(Stage primaryStage) {
    this.primaryStage = primaryStage;
    //openView("startAuction");
    //openView("allAuctions");
//    openView(WindowType.ALL_AUCTIONS);
    openView(WindowType.SIGN_UP);
  }

  public void openView(WindowType type) {
    Region root = null;
    switch (type) {
      case START_AUCTION, DISPLAY_AUCTION, ALL_AUCTIONS ->{
        root = loadFixedPaneView("FixedPaneView.fxml", type);
      }
      case SIGN_UP, LOG_IN -> {
        root = loadCreateLoginView("CreateAccountEditProfileView.fxml",type);
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
  private Region loadCreateLoginView(String fxmlFile, WindowType windowType) {
    if (createLoginViewController == null) {
      try {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(fxmlFile));
        Region root = loader.load();
        createLoginViewController = loader.getController();

        createLoginViewController.init(this,
                viewModelFactory.getCreateLoginViewModel(), root,
                windowType);
      }
      catch (Exception e) {
        e.printStackTrace();
      }
    }
    else {
      createLoginViewController.reset(windowType);
    }
    return createLoginViewController.getRoot();
  }
}







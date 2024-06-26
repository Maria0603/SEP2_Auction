package view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import viewmodel.ViewModelFactory;

/**
 * The ViewHandler class is responsible for managing the different views in the application.
 * It handles the opening and loading of various FXML files and their associated controllers.
 */
public class ViewHandler
{
  private Stage primaryStage;
  private final Scene currentScene;
  private final ViewModelFactory viewModelFactory;
  private FixedPaneViewHandler fixedPaneViewController;
  private CreateLoginViewController createLoginViewController;

  /**
   * Constructs a ViewHandler with the specified ViewModelFactory.
   *
   * @param viewModelFactory the view model factory
   */
  public ViewHandler(ViewModelFactory viewModelFactory)
  {
    this.viewModelFactory = viewModelFactory;
    currentScene = new Scene(new Region());
  }

  /**
   * Starts the primary stage and opens the initial view.
   *
   * @param primaryStage the primary stage
   */
  public void start(Stage primaryStage)
  {
    this.primaryStage = primaryStage;
    openView(WindowType.SIGN_UP);
  }

  /**
   * Opens the specified view based on the WindowType.
   *
   * @param type the type of window to open
   */
  public void openView(WindowType type)
  {
    Region root = null;
    switch (type)
    {
      case START_AUCTION, DISPLAY_AUCTION, ALL_AUCTIONS, MY_AUCTIONS, DISPLAY_PROFILE ->
              root = loadFixedPaneView("FixedPaneView.fxml", type);
      case SIGN_UP, LOG_IN ->
              root = loadCreateLoginView("CreateAccountEditProfileView.fxml", type);
    }
    currentScene.setRoot(root);

    primaryStage.setResizable(false);
    primaryStage.setTitle("Auction system");
    primaryStage.setScene(currentScene);
    primaryStage.show();
  }

  /**
   * Loads the FixedPane view from the specified FXML file.
   *
   * @param fxmlFile the FXML file to load
   * @param windowType the type of window to load
   * @return the root region of the loaded view
   */
  private Region loadFixedPaneView(String fxmlFile, WindowType windowType)
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
                windowType);
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }
    else
    {
      fixedPaneViewController.reset(windowType);
    }
    return fixedPaneViewController.getRoot();
  }

  /**
   * Loads the CreateLogin view from the specified FXML file.
   *
   * @param fxmlFile the FXML file to load
   * @param windowType the type of window to load
   * @return the root region of the loaded view
   */
  private Region loadCreateLoginView(String fxmlFile, WindowType windowType)
  {
    if (createLoginViewController == null)
    {
      try
      {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(fxmlFile));
        Region root = loader.load();
        createLoginViewController = loader.getController();

        createLoginViewController.init(this,
                viewModelFactory.getCreateLoginViewModel(), root, windowType);
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }
    else
    {
      createLoginViewController.reset(windowType);
    }
    return createLoginViewController.getRoot();
  }
}

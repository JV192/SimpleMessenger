import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class SimpleMessenger extends Application {

	/**
	 * Name of the program
	 */
	private final String TITLE = "Simple Messenger";

	/**
	 * Version of the program
	 */
	private final String VERSION = "1.00";

	/**
	 * Initialize the program and construct the scene from the corresponding FXML
	 * file.
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {

		SocketController socketController = new SocketController();

		FXMLLoader loader = new FXMLLoader(getClass().getResource("Main.fxml"));
		Pane root = (Pane) loader.load();
		MainFXMLController mainFXMLController = loader.getController();

		mainFXMLController.startServerSideThread(socketController);

		primaryStage.setOnCloseRequest(event -> {
			socketController.closeServerSocket();
			socketController.closeCurrentSocket();
			Platform.exit();
			System.exit(0);
		});

		Scene scene = new Scene(root, primaryStage.getWidth(), primaryStage.getHeight());
		primaryStage.setResizable(false);
		primaryStage.setScene(scene);
		primaryStage.setTitle(TITLE + " v" + VERSION);

		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}

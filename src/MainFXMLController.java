import java.io.IOException;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class MainFXMLController {

	@FXML
	private TextField TextField_SendInput;

	@FXML
	private TextArea TextArea_Key;

	@FXML
	private TextFlow TextFlow_History;

	@FXML
	private Button Button_Connect;

	@FXML
	private Pane Pane_root;

	@FXML
	private TextField TextField_Connect;

	@FXML
	private Button Button_Send;

	@FXML
	private Button Button_Disconnect;

	@FXML
	private RadioButton RadioButton_ClientMode;

	@FXML
	private RadioButton RadioButton_ServerMode;

	@FXML
	private RadioButton RadioButton_CipherNone;

	@FXML
	private RadioButton RadioButton_CipherAES128;

	/**
	 * Name displayed next to this user's messages.
	 */
	private final String myUsername = "You";
	/**
	 * Name displayed next to the system's messages.
	 */
	private final String systemUsername = "System";

	/**
	 * The instance of {@link SocketController} class used for communication.
	 */
	private SocketController socketController;
	/**
	 * The instance of {@link Thread} used for listening to incoming messages.
	 */
	private Thread socketListenerThread;

	/**
	 * {@link Cipher} used for encryption/decryption of sent/received messages.
	 */
	private Cipher currentCipher = Cipher.AES128;

	/**
	 * Tries to establish a connection by the address read from the GUI
	 * 
	 * @param event the {@link ActionEvent} sent by the GUI
	 */
	@FXML
	void Button_ConnectAction(ActionEvent event) {
		socketController.setClientMode();
		boolean success = socketController.connect(TextField_Connect.getText());
		if (success) {
			postLine("Connected to " + socketController.getPartnerAddress(), systemUsername, Style.SYSTEM);
			updateUI(true);
		} else {
			postLine("Failed to connect to " + TextField_Connect.getText(), systemUsername, Style.SYSTEM);
		}
	}

	/**
	 * Closes the connection
	 * 
	 * @param event the {@link ActionEvent} sent by the GUI
	 */
	@FXML
	void Button_DisconnectAction(ActionEvent event) {
		if (RadioButton_ServerMode.isSelected()) {
			socketController.setServerMode();
		} else {
			socketController.setClientMode();
		}
		updateUI(false);
	}

	/**
	 * Tries to send a message through the established connection using values read
	 * from the GUI
	 * 
	 * @param event the {@link ActionEvent} sent by the GUI
	 */
	@FXML
	void Button_SendAction(ActionEvent event) {
		if (!TextField_SendInput.getText().isEmpty()) {
			String text = TextField_SendInput.getText();
			String key = TextArea_Key.getText();
			try {
				socketController.send(Crypto.encrypt(text, key, currentCipher));
				postLine(TextField_SendInput.getText(), myUsername, Style.USER);
			} catch (UnsupportedOperationException e) {
				postLine("Encryption failed, the message was not sent.", systemUsername, Style.SYSTEM);
			} catch (IOException e) {
				postLine("Failed to send the message.", systemUsername, Style.SYSTEM);
				e.printStackTrace();
			}
			TextField_SendInput.clear();
		}
	}

	/**
	 * Sets client mode and updates the GUI
	 * 
	 * @param event the {@link ActionEvent} sent by the GUI
	 */
	@FXML
	void RadioButton_ClientModeAction(ActionEvent event) {
		socketController.setClientMode();
		RadioButton_ServerMode.setSelected(false);

		updateUI(false);
	}

	/**
	 * Sets the currently used {@link Cipher} to {@link Cipher#NONE}.
	 * 
	 */
	@FXML
	void RadioButton_CipherNoneAction(ActionEvent event) {
		RadioButton_CipherAES128.setSelected(false);
		TextArea_Key.setDisable(true);
		currentCipher = Cipher.NONE;
	}

	/**
	 * Sets the currently used {@link Cipher} to {@link Cipher#AES128}.
	 * 
	 * @param event the {@link ActionEvent} sent by the GUI
	 */
	@FXML
	void RadioButton_CipherAES128Action(ActionEvent event) {
		RadioButton_CipherNone.setSelected(false);
		TextArea_Key.setDisable(false);
		currentCipher = Cipher.AES128;
	}

	/**
	 * Sets server mode and updates the GUI
	 * 
	 * @param event the {@link ActionEvent} sent by the GUI
	 */
	@FXML
	void RadioButton_ServerModeAction(ActionEvent event) {
		socketController.setServerMode();
		RadioButton_ClientMode.setSelected(false);

		updateUI(false);
	}

	/**
	 * Initializes the GUI
	 */
	public void initialize() {
		TextField_Connect.setText("localhost");
		TextArea_Key.setWrapText(true);
		RadioButton_ClientMode.setSelected(true);
		RadioButton_CipherAES128.setSelected(true);
		updateUI(false);
	}

	/**
	 * Creates a {@link Thread} to handle the server side of the program and updates
	 * the GUI accordingly.
	 * 
	 * @param socketController the instance of {@link SocketController} to be used
	 */
	public void startServerSideThread(SocketController socketController) {
		this.socketController = socketController;
		socketListenerThread = new Thread(() -> {
			while (true) {
				try {
					Thread.sleep(1000);

					if (socketController.getSocketStatus() == SocketStatus.STARTINGSERVER) {
						socketController.startServer();
						if (socketController.getSocketStatus() == SocketStatus.SERVERMODE) {
							Platform.runLater(() -> {
								postLine(socketController.getPartnerAddress() + " connected", systemUsername,
										Style.SYSTEM);
								updateUI(true);
							});
						}
					}

					if (socketController.getSocketStatus() == SocketStatus.SERVERMODE
							|| socketController.getSocketStatus() == SocketStatus.CLIENTMODE) {
						try {
							byte[] receivedMessage = socketController.receiveMessage();
							if (receivedMessage != null) {
								String stringKey = TextArea_Key.getText();
								String message = Crypto.decrypt(receivedMessage, stringKey, currentCipher);
								Platform.runLater(() -> {
									postLine(message, socketController.getPartnerAddress(), Style.PARTNER);
								});
							}
						} catch (IOException e) {
							socketController.setServerMode();
							Platform.runLater(() -> {
								postLine(socketController.getPartnerAddress() + " disconnected", systemUsername,
										Style.SYSTEM);
								updateUI(false);
							});

						}
					}

					System.out.println("Running : " + socketController.getSocketStatus().toString());

				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		});

		socketListenerThread.start();
	}

	/**
	 * Prints the passed line to the main chat box
	 * 
	 * @param line   line to be printed
	 * @param sender username that the line is related to
	 * @param style  {@link Style} of the message
	 */
	private void postLine(String line, String sender, Style style) {
		Text text = new Text(sender + " : " + line + '\n');
		switch (style) {
		case USER:
			text.setFill(Color.BLUE);
			break;
		case PARTNER:
			text.setFill(Color.RED);
			break;
		default:
			break;
		}
		TextFlow_History.getChildren().add(text);
	}

	/**
	 * Updates the UI to match the current state of the connection.
	 * <p>
	 * Doesn't let the user perform potentially harmful actions e.g. initiating
	 * connection to a peer twice.
	 * 
	 * @param isConnected indicates if there is connection to a peer.
	 */
	private void updateUI(boolean isConnected) {
		if (isConnected) {
			TextField_Connect.setDisable(true);
			Button_Connect.setVisible(false);
			Button_Disconnect.setVisible(true);
			RadioButton_ClientMode.setDisable(true);
			RadioButton_ServerMode.setDisable(true);
			TextField_SendInput.setDisable(false);
			Button_Send.setDisable(false);
		} else {
			if (RadioButton_ServerMode.isSelected()) {
				TextField_Connect.setDisable(true);
				Button_Connect.setDisable(true);
			} else {
				TextField_Connect.setDisable(false);
				Button_Connect.setDisable(false);
			}
			Button_Connect.setVisible(true);
			Button_Disconnect.setVisible(false);
			RadioButton_ClientMode.setDisable(false);
			RadioButton_ServerMode.setDisable(false);
			TextField_SendInput.setDisable(true);
			Button_Send.setDisable(true);
		}
	}

}
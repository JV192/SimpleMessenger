import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketController {
	private final int port = 1234;

	private Socket socket;
	private ServerSocket serverSocket;
	private SocketStatus socketStatus;

	public SocketController() {
		socketStatus = SocketStatus.DISCONNECTED;
	}

	/**
	 * Tries to establish connection with a waiting remote Socket on the passed
	 * {@code address}
	 * 
	 * @param address to be connected to
	 * @return true if the connection was successfully established, false otherwise
	 */
	public boolean connect(String address) {
		InetAddress inetAddress;
		try {
			setClientMode();
			inetAddress = InetAddress.getByName(address);
			InetSocketAddress inetSocketAddress = new InetSocketAddress(inetAddress, port);
			socket = new Socket();
			socket.connect(inetSocketAddress, port);
			socketStatus = SocketStatus.CLIENTMODE;
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * Tries to send the passed {@code message} through the current connection
	 * <p>
	 * Sends the message to the remote peer. Doesn't check if the message has been
	 * actually received.
	 * 
	 * @param message to be sent
	 * @throws IOException in case of socket errors
	 * 
	 */
	public void send(byte[] message) throws IOException {
		int messageLength = message.length;

		if (messageLength > 255) {
			messageLength = 255;
		}

		OutputStream os = socket.getOutputStream();
		os.write(messageLength);
		os.write(message, 0, messageLength);
	}

	/**
	 * Receives a message from the currently connected {@link Socket}.
	 * <p>
	 * Blocks the current {@link Thread} until a message is received or an
	 * {@link Exception} occurs.
	 * 
	 * @return {@code array of byte} message if the read operation succeeded, null
	 *         otherwise.
	 * @throws IOException in case of socket errors
	 */
	public byte[] receiveMessage() throws IOException {
		InputStream is = socket.getInputStream();

		int messageLength = is.read();
		if (messageLength > 0) {
			byte[] b = new byte[messageLength];
			is.read(b);
			return b;
		} else {
			return null;
		}
	}

	/**
	 * Starts listening to incoming connections
	 * <p>
	 * Blocks the current {@link Thread} until a successful connection has been
	 * established or an {@link Exception} occurred.
	 * 
	 * @return true once a connection has been established, false in case of
	 *         {@link Exception}
	 */
	public boolean startServer() {
		try (ServerSocket serverSocket = new ServerSocket(port)) {
			this.serverSocket = serverSocket;
			socket = serverSocket.accept();
			socketStatus = SocketStatus.SERVERMODE;
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * Closes the currently connected socket and sets the status of the program to
	 * SERVERMODE
	 */
	public void setServerMode() {
		if (socketStatus == SocketStatus.CLIENTMODE) {
			closeCurrentSocket();
		} else if (socketStatus == SocketStatus.SERVERMODE) {
			closeCurrentSocket();
			closeServerSocket();
		}
		socketStatus = SocketStatus.STARTINGSERVER;
	}

	/**
	 * Closes the currently connected socket and sets the status of the program to
	 * CLIENTMODE
	 */
	public void setClientMode() {
		if (socketStatus == SocketStatus.SERVERMODE || socketStatus == SocketStatus.STARTINGSERVER) {
			closeCurrentSocket();
			closeServerSocket();
		} else if (socketStatus == SocketStatus.CLIENTMODE) {
			closeCurrentSocket();
		}
	}

	/**
	 * Closes the currently open {@link Socket}.
	 */
	public void closeCurrentSocket() {
		socketStatus = SocketStatus.DISCONNECTED;

		if (socket == null) {
			return;
		}

		try {
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Closes the current {@link ServerSocket}.
	 * <p>
	 * Unblocks the corresponding {@link Thread}
	 */
	public void closeServerSocket() {
		try {
			if (serverSocket != null) {
				serverSocket.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return {@link InetAddress} of the currently connected peer as
	 *         {@link String}, null if the socket doesn't exist.
	 */
	public String getPartnerAddress() {
		if (socket == null) {
			return null;
		}

		return socket.getInetAddress().toString();
	}

	/**
	 * @return the {@link SocketStatus} of the current {@link Socket}
	 */
	public SocketStatus getSocketStatus() {
		return socketStatus;
	}
}

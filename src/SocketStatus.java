/**
 * 
 * Reflects the status of a socket.
 *
 */
public enum SocketStatus {
	/**
	 * Indicates that the socket should be switched to server mode.
	 */
	STARTINGSERVER,

	/**
	 * Indicates that the socket is currently listening to incoming connections.
	 */
	SERVERMODE,

	/**
	 * Indicates that the socket is open and ready for connection/is connected.
	 */
	CLIENTMODE,

	/**
	 * Indicates that the socket is closed.
	 */
	DISCONNECTED;
}

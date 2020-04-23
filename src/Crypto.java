import java.nio.charset.Charset;

/**
 * The Crypto class provides static methods for encryption and decryption of
 * messages.
 * <p>
 * The encryption/decryption is done using one of the supported Ciphers and the
 * passed {@link String} key. Messages are converted from {@link String} to
 * {@code array of bytes} and back using the {@link #charset}.
 * 
 * @see Cipher
 *
 */
public class Crypto {
	/**
	 * The {@link Charset} used for {@link String} to {@code array of bytes} and
	 * back conversions.
	 */
	private static Charset charset = Charset.forName("UTF-8");

	/**
	 * Encrypts the passed text using the passed {@link Cipher} and {@link String}
	 * key. Text is interpreted using the current {@link #charset}.
	 * 
	 * @param text   to be encrypted
	 * @param key    to be used for encryption
	 * @param cipher to encrypt with
	 * @return encrypted text as {@code array of bytes}
	 * @see Cipher
	 */
	public static byte[] encrypt(String text, String key, Cipher cipher) {
		byte[] byteArray = text.getBytes(charset);
		byte[] byteKey = key.getBytes(charset);

		switch (cipher) {
		case AES128:
			byteArray = new AES().encrypt(byteArray, byteKey);
			break;
		default:
			//
		}

		return byteArray;
	}

	/**
	 * Decrypts the passed {@code array of bytes} using the passed {@link Cipher}
	 * and {@link String} key. The resulting {@code array of bytes} message is
	 * interpreted as text using {@link #charset}.
	 * 
	 * @param byteArray message to be decrypted
	 * @param key       to use for decryption
	 * @param cipher    to use for decryption
	 * @return decrypted text as {@code String}
	 */
	public static String decrypt(byte[] byteArray, String key, Cipher cipher) {
		byte[] byteKey = key.getBytes(charset);

		switch (cipher) {
		case AES128:
			byteArray = new AES().decrypt(byteArray, byteKey);
			break;
		default:
			//
		}

		return new String(byteArray, charset);
	}
}

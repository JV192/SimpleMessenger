import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 
 * Implements the Advanced Encryption Standard algorithm. Supports
 * <ul>
 * <li>128 bits key</li>
 *</ul>
 */
public class AES {
	/**
	 * The constant number of rounds used for the encryption. Set for AES-128
	 */
	private final static int ROUNDS = 10;
	/**
	 * The Rijndael's S-Box used by various operations of the AES algorithm.
	 */
	private final static int[] S_BOX = { 0x63, 0x7c, 0x77, 0x7b, 0xf2, 0x6b, 0x6f, 0xc5, 0x30, 0x01, 0x67, 0x2b, 0xfe,
			0xd7, 0xab, 0x76, 0xca, 0x82, 0xc9, 0x7d, 0xfa, 0x59, 0x47, 0xf0, 0xad, 0xd4, 0xa2, 0xaf, 0x9c, 0xa4, 0x72,
			0xc0, 0xb7, 0xfd, 0x93, 0x26, 0x36, 0x3f, 0xf7, 0xcc, 0x34, 0xa5, 0xe5, 0xf1, 0x71, 0xd8, 0x31, 0x15, 0x04,
			0xc7, 0x23, 0xc3, 0x18, 0x96, 0x05, 0x9a, 0x07, 0x12, 0x80, 0xe2, 0xeb, 0x27, 0xb2, 0x75, 0x09, 0x83, 0x2c,
			0x1a, 0x1b, 0x6e, 0x5a, 0xa0, 0x52, 0x3b, 0xd6, 0xb3, 0x29, 0xe3, 0x2f, 0x84, 0x53, 0xd1, 0x00, 0xed, 0x20,
			0xfc, 0xb1, 0x5b, 0x6a, 0xcb, 0xbe, 0x39, 0x4a, 0x4c, 0x58, 0xcf, 0xd0, 0xef, 0xaa, 0xfb, 0x43, 0x4d, 0x33,
			0x85, 0x45, 0xf9, 0x02, 0x7f, 0x50, 0x3c, 0x9f, 0xa8, 0x51, 0xa3, 0x40, 0x8f, 0x92, 0x9d, 0x38, 0xf5, 0xbc,
			0xb6, 0xda, 0x21, 0x10, 0xff, 0xf3, 0xd2, 0xcd, 0x0c, 0x13, 0xec, 0x5f, 0x97, 0x44, 0x17, 0xc4, 0xa7, 0x7e,
			0x3d, 0x64, 0x5d, 0x19, 0x73, 0x60, 0x81, 0x4f, 0xdc, 0x22, 0x2a, 0x90, 0x88, 0x46, 0xee, 0xb8, 0x14, 0xde,
			0x5e, 0x0b, 0xdb, 0xe0, 0x32, 0x3a, 0x0a, 0x49, 0x06, 0x24, 0x5c, 0xc2, 0xd3, 0xac, 0x62, 0x91, 0x95, 0xe4,
			0x79, 0xe7, 0xc8, 0x37, 0x6d, 0x8d, 0xd5, 0x4e, 0xa9, 0x6c, 0x56, 0xf4, 0xea, 0x65, 0x7a, 0xae, 0x08, 0xba,
			0x78, 0x25, 0x2e, 0x1c, 0xa6, 0xb4, 0xc6, 0xe8, 0xdd, 0x74, 0x1f, 0x4b, 0xbd, 0x8b, 0x8a, 0x70, 0x3e, 0xb5,
			0x66, 0x48, 0x03, 0xf6, 0x0e, 0x61, 0x35, 0x57, 0xb9, 0x86, 0xc1, 0x1d, 0x9e, 0xe1, 0xf8, 0x98, 0x11, 0x69,
			0xd9, 0x8e, 0x94, 0x9b, 0x1e, 0x87, 0xe9, 0xce, 0x55, 0x28, 0xdf, 0x8c, 0xa1, 0x89, 0x0d, 0xbf, 0xe6, 0x42,
			0x68, 0x41, 0x99, 0x2d, 0x0f, 0xb0, 0x54, 0xbb, 0x16, };

	/**
	 * Inverse of the {@link #S_BOX}.
	 */
	private final static int[] S_BOX_INVERSE = { 0x52, 0x09, 0x6a, 0xd5, 0x30, 0x36, 0xa5, 0x38, 0xbf, 0x40, 0xa3, 0x9e,
			0x81, 0xf3, 0xd7, 0xfb, 0x7c, 0xe3, 0x39, 0x82, 0x9b, 0x2f, 0xff, 0x87, 0x34, 0x8e, 0x43, 0x44, 0xc4, 0xde,
			0xe9, 0xcb, 0x54, 0x7b, 0x94, 0x32, 0xa6, 0xc2, 0x23, 0x3d, 0xee, 0x4c, 0x95, 0x0b, 0x42, 0xfa, 0xc3, 0x4e,
			0x08, 0x2e, 0xa1, 0x66, 0x28, 0xd9, 0x24, 0xb2, 0x76, 0x5b, 0xa2, 0x49, 0x6d, 0x8b, 0xd1, 0x25, 0x72, 0xf8,
			0xf6, 0x64, 0x86, 0x68, 0x98, 0x16, 0xd4, 0xa4, 0x5c, 0xcc, 0x5d, 0x65, 0xb6, 0x92, 0x6c, 0x70, 0x48, 0x50,
			0xfd, 0xed, 0xb9, 0xda, 0x5e, 0x15, 0x46, 0x57, 0xa7, 0x8d, 0x9d, 0x84, 0x90, 0xd8, 0xab, 0x00, 0x8c, 0xbc,
			0xd3, 0x0a, 0xf7, 0xe4, 0x58, 0x05, 0xb8, 0xb3, 0x45, 0x06, 0xd0, 0x2c, 0x1e, 0x8f, 0xca, 0x3f, 0x0f, 0x02,
			0xc1, 0xaf, 0xbd, 0x03, 0x01, 0x13, 0x8a, 0x6b, 0x3a, 0x91, 0x11, 0x41, 0x4f, 0x67, 0xdc, 0xea, 0x97, 0xf2,
			0xcf, 0xce, 0xf0, 0xb4, 0xe6, 0x73, 0x96, 0xac, 0x74, 0x22, 0xe7, 0xad, 0x35, 0x85, 0xe2, 0xf9, 0x37, 0xe8,
			0x1c, 0x75, 0xdf, 0x6e, 0x47, 0xf1, 0x1a, 0x71, 0x1d, 0x29, 0xc5, 0x89, 0x6f, 0xb7, 0x62, 0x0e, 0xaa, 0x18,
			0xbe, 0x1b, 0xfc, 0x56, 0x3e, 0x4b, 0xc6, 0xd2, 0x79, 0x20, 0x9a, 0xdb, 0xc0, 0xfe, 0x78, 0xcd, 0x5a, 0xf4,
			0x1f, 0xdd, 0xa8, 0x33, 0x88, 0x07, 0xc7, 0x31, 0xb1, 0x12, 0x10, 0x59, 0x27, 0x80, 0xec, 0x5f, 0x60, 0x51,
			0x7f, 0xa9, 0x19, 0xb5, 0x4a, 0x0d, 0x2d, 0xe5, 0x7a, 0x9f, 0x93, 0xc9, 0x9c, 0xef, 0xa0, 0xe0, 0x3b, 0x4d,
			0xae, 0x2a, 0xf5, 0xb0, 0xc8, 0xeb, 0xbb, 0x3c, 0x83, 0x53, 0x99, 0x61, 0x17, 0x2b, 0x04, 0x7e, 0xba, 0x77,
			0xd6, 0x26, 0xe1, 0x69, 0x14, 0x63, 0x55, 0x21, 0x0c, 0x7d };
	/**
	 * Maximum size of the key used for encryption/decryption.
	 */
	private int MAX_KEY_SIZE = 128;

	/**
	 * Encrypts the passed data using AES-128 cipher.
	 * 
	 * @param byteArray data to be encrypted
	 * @param byteKey   key to be used for encryption
	 * @return encrypted data as {@code array of bytes}
	 * @throws UnsupportedOperationException if the passed key is too large
	 */
	public byte[] encrypt(byte[] byteArray, byte[] byteKey) {
		if (byteKey.length > MAX_KEY_SIZE) {
			throw new UnsupportedOperationException();
		}

		List<AESBlock> aesBlockList = splitIntoBlocks(byteArray);
		AESKey aesKey = new AESKey(byteKey);

		aesKey.expandKey();
		for (AESBlock block : aesBlockList) {
			block.addRoundKey(aesKey.getSubKey(0));
			for (int i = 1; i <= ROUNDS - 1; i++) {
				block.subBytes();
				block.shiftRows();
				block.mixColumns();
				block.addRoundKey(aesKey.getSubKey(4 * i));
			}
			block.subBytes();
			block.shiftRows();
			block.addRoundKey(aesKey.getSubKey(4 * ROUNDS));
		}

		return collectBlocksToByteArray(aesBlockList);
	}

	/**
	 * Decrypts the passed data assuming AES-128 encryption.
	 * 
	 * @param byteArray data to be decrypted
	 * @param byteKey   key to use for decryption
	 * @return decrypted data
	 */
	public byte[] decrypt(byte[] byteArray, byte[] byteKey) {
		if (byteKey.length > MAX_KEY_SIZE) {
			throw new UnsupportedOperationException();
		}

		List<AESBlock> aesBlockList = splitIntoBlocks(byteArray);
		AESKey aesKey = new AESKey(byteKey);

		aesKey.expandKey();
		for (AESBlock block : aesBlockList) {
			block.addRoundKey(aesKey.getSubKey(4 * ROUNDS));
			block.shiftRowsInverse();
			block.subBytesInverse();
			for (int i = ROUNDS - 1; i >= 1; i--) {
				block.addRoundKey(aesKey.getSubKey(4 * i));
				block.mixColumnsInverse();
				block.shiftRowsInverse();
				block.subBytesInverse();
			}
			block.addRoundKey(aesKey.getSubKey(0));
		}

		return collectBlocksToByteArray(aesBlockList);
	}

	/**
	 * Split the passed array of bytes into separate blocks used by the AES
	 * algorithm
	 * 
	 * @param byteArray data to be split
	 * @return {@link List} of blocks
	 * @see AESBlock
	 */
	private List<AESBlock> splitIntoBlocks(byte[] byteArray) {
		List<AESBlock> aesBlockList = new ArrayList<AESBlock>();
		int i = 0;
		while (i < byteArray.length) {
			byte[] byteArrayPart = Arrays.copyOfRange(byteArray, i, i + 16);
			AESBlock aesBlock = new AESBlock(byteArrayPart);
			aesBlockList.add(aesBlock);
			i += 16;
		}
		return aesBlockList;
	}

	/**
	 * Collects separate blocks used by the AES algorithm to a unified array of
	 * bytes.
	 * 
	 * @param aesBlockList blocks to be combined
	 * @return assembled array of bytes
	 * @see AESBlock
	 */
	private byte[] collectBlocksToByteArray(List<AESBlock> aesBlockList) {
		byte[] byteArray = new byte[0];

		for (AESBlock block : aesBlockList) {
			byte[] currentArray = byteArray;
			byte[] arrayPart = block.toByteArray();
			byteArray = Arrays.copyOf(currentArray, currentArray.length + arrayPart.length);
			System.arraycopy(arrayPart, 0, byteArray, currentArray.length, arrayPart.length);
		}

		return byteArray;
	}

	/**
	 * Rotates the passed array of bytes by 1 position to the left.
	 * 
	 * @param word to be rotated
	 * @return word after rotation
	 */
	public static byte[] rotWord(byte[] word) {
		byte[] newWord = new byte[word.length];

		for (int i = 0; i < word.length - 1; i++) {
			newWord[i] = word[i + 1];
		}
		newWord[word.length - 1] = word[0];

		return newWord;
	}

	/**
	 * Rotates the passed array of bytes by 1 position to the right. Inverse of
	 * {@link #rotWord(byte[])}.
	 * 
	 * @param word to be rotated
	 * @return word after rotation
	 */
	public static byte[] rotWordInverse(byte[] word) {
		byte[] newWord = new byte[word.length];

		for (int i = 0; i < word.length - 1; i++) {
			newWord[i + 1] = word[i];
		}
		newWord[0] = word[word.length - 1];

		return newWord;
	}

	/**
	 * Transposes the passed matrix.
	 * 
	 * @param matrix to be transposed
	 * @return result of the operation
	 */
	private byte[][] transpose(byte[][] matrix) {
		byte[][] transpose = new byte[matrix.length][matrix.length];
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix.length; j++) {
				transpose[i][j] = matrix[j][i];
			}
		}
		return transpose;
	}

	/**
	 * Calculates the XOR operation of the two given words.
	 * <p>
	 * Applies the XOR operator to each of the bytes of the given words. The words
	 * are represented as arrays of bytes and must have equal size.
	 * 
	 * @param word1
	 * @param word2
	 * @return result of the XOR operation
	 */
	private byte[] xor(byte[] word1, byte[] word2) {
		assert word1.length == word2.length : "Both words must have equal length";
		byte[] newWord = new byte[word1.length];
		for (int i = 0; i < word1.length; i++) {
			newWord[i] = (byte) (word1[i] ^ word2[i]);
		}

		return newWord;
	}

	/**
	 * 
	 * Internal class AESBlock used to simplify the implementation of the AES
	 * algorithm by encapsulating each of its steps.
	 * <p>
	 * Each block is a {@value #MATRIX_SIZE} x {@value #MATRIX_SIZE} matrix storing
	 * a portion of data in the column-major order.
	 *
	 */
	private class AESBlock {
		/**
		 * Precalculated constants used for the multiplication operation inside a 2^8
		 * Galois field.
		 */
		private final int[] GALOIS256_MULTIPLY_2 = { 0x00, 0x02, 0x04, 0x06, 0x08, 0x0a, 0x0c, 0x0e, 0x10, 0x12, 0x14,
				0x16, 0x18, 0x1a, 0x1c, 0x1e, 0x20, 0x22, 0x24, 0x26, 0x28, 0x2a, 0x2c, 0x2e, 0x30, 0x32, 0x34, 0x36,
				0x38, 0x3a, 0x3c, 0x3e, 0x40, 0x42, 0x44, 0x46, 0x48, 0x4a, 0x4c, 0x4e, 0x50, 0x52, 0x54, 0x56, 0x58,
				0x5a, 0x5c, 0x5e, 0x60, 0x62, 0x64, 0x66, 0x68, 0x6a, 0x6c, 0x6e, 0x70, 0x72, 0x74, 0x76, 0x78, 0x7a,
				0x7c, 0x7e, 0x80, 0x82, 0x84, 0x86, 0x88, 0x8a, 0x8c, 0x8e, 0x90, 0x92, 0x94, 0x96, 0x98, 0x9a, 0x9c,
				0x9e, 0xa0, 0xa2, 0xa4, 0xa6, 0xa8, 0xaa, 0xac, 0xae, 0xb0, 0xb2, 0xb4, 0xb6, 0xb8, 0xba, 0xbc, 0xbe,
				0xc0, 0xc2, 0xc4, 0xc6, 0xc8, 0xca, 0xcc, 0xce, 0xd0, 0xd2, 0xd4, 0xd6, 0xd8, 0xda, 0xdc, 0xde, 0xe0,
				0xe2, 0xe4, 0xe6, 0xe8, 0xea, 0xec, 0xee, 0xf0, 0xf2, 0xf4, 0xf6, 0xf8, 0xfa, 0xfc, 0xfe, 0x1b, 0x19,
				0x1f, 0x1d, 0x13, 0x11, 0x17, 0x15, 0x0b, 0x09, 0x0f, 0x0d, 0x03, 0x01, 0x07, 0x05, 0x3b, 0x39, 0x3f,
				0x3d, 0x33, 0x31, 0x37, 0x35, 0x2b, 0x29, 0x2f, 0x2d, 0x23, 0x21, 0x27, 0x25, 0x5b, 0x59, 0x5f, 0x5d,
				0x53, 0x51, 0x57, 0x55, 0x4b, 0x49, 0x4f, 0x4d, 0x43, 0x41, 0x47, 0x45, 0x7b, 0x79, 0x7f, 0x7d, 0x73,
				0x71, 0x77, 0x75, 0x6b, 0x69, 0x6f, 0x6d, 0x63, 0x61, 0x67, 0x65, 0x9b, 0x99, 0x9f, 0x9d, 0x93, 0x91,
				0x97, 0x95, 0x8b, 0x89, 0x8f, 0x8d, 0x83, 0x81, 0x87, 0x85, 0xbb, 0xb9, 0xbf, 0xbd, 0xb3, 0xb1, 0xb7,
				0xb5, 0xab, 0xa9, 0xaf, 0xad, 0xa3, 0xa1, 0xa7, 0xa5, 0xdb, 0xd9, 0xdf, 0xdd, 0xd3, 0xd1, 0xd7, 0xd5,
				0xcb, 0xc9, 0xcf, 0xcd, 0xc3, 0xc1, 0xc7, 0xc5, 0xfb, 0xf9, 0xff, 0xfd, 0xf3, 0xf1, 0xf7, 0xf5, 0xeb,
				0xe9, 0xef, 0xed, 0xe3, 0xe1, 0xe7, 0xe5 };

		/**
		 * @see GALOIS256_MULTIPLY_2
		 */
		private final int[] GALOIS256_MULTIPLY_3 = { 0x00, 0x03, 0x06, 0x05, 0x0c, 0x0f, 0x0a, 0x09, 0x18, 0x1b, 0x1e,
				0x1d, 0x14, 0x17, 0x12, 0x11, 0x30, 0x33, 0x36, 0x35, 0x3c, 0x3f, 0x3a, 0x39, 0x28, 0x2b, 0x2e, 0x2d,
				0x24, 0x27, 0x22, 0x21, 0x60, 0x63, 0x66, 0x65, 0x6c, 0x6f, 0x6a, 0x69, 0x78, 0x7b, 0x7e, 0x7d, 0x74,
				0x77, 0x72, 0x71, 0x50, 0x53, 0x56, 0x55, 0x5c, 0x5f, 0x5a, 0x59, 0x48, 0x4b, 0x4e, 0x4d, 0x44, 0x47,
				0x42, 0x41, 0xc0, 0xc3, 0xc6, 0xc5, 0xcc, 0xcf, 0xca, 0xc9, 0xd8, 0xdb, 0xde, 0xdd, 0xd4, 0xd7, 0xd2,
				0xd1, 0xf0, 0xf3, 0xf6, 0xf5, 0xfc, 0xff, 0xfa, 0xf9, 0xe8, 0xeb, 0xee, 0xed, 0xe4, 0xe7, 0xe2, 0xe1,
				0xa0, 0xa3, 0xa6, 0xa5, 0xac, 0xaf, 0xaa, 0xa9, 0xb8, 0xbb, 0xbe, 0xbd, 0xb4, 0xb7, 0xb2, 0xb1, 0x90,
				0x93, 0x96, 0x95, 0x9c, 0x9f, 0x9a, 0x99, 0x88, 0x8b, 0x8e, 0x8d, 0x84, 0x87, 0x82, 0x81, 0x9b, 0x98,
				0x9d, 0x9e, 0x97, 0x94, 0x91, 0x92, 0x83, 0x80, 0x85, 0x86, 0x8f, 0x8c, 0x89, 0x8a, 0xab, 0xa8, 0xad,
				0xae, 0xa7, 0xa4, 0xa1, 0xa2, 0xb3, 0xb0, 0xb5, 0xb6, 0xbf, 0xbc, 0xb9, 0xba, 0xfb, 0xf8, 0xfd, 0xfe,
				0xf7, 0xf4, 0xf1, 0xf2, 0xe3, 0xe0, 0xe5, 0xe6, 0xef, 0xec, 0xe9, 0xea, 0xcb, 0xc8, 0xcd, 0xce, 0xc7,
				0xc4, 0xc1, 0xc2, 0xd3, 0xd0, 0xd5, 0xd6, 0xdf, 0xdc, 0xd9, 0xda, 0x5b, 0x58, 0x5d, 0x5e, 0x57, 0x54,
				0x51, 0x52, 0x43, 0x40, 0x45, 0x46, 0x4f, 0x4c, 0x49, 0x4a, 0x6b, 0x68, 0x6d, 0x6e, 0x67, 0x64, 0x61,
				0x62, 0x73, 0x70, 0x75, 0x76, 0x7f, 0x7c, 0x79, 0x7a, 0x3b, 0x38, 0x3d, 0x3e, 0x37, 0x34, 0x31, 0x32,
				0x23, 0x20, 0x25, 0x26, 0x2f, 0x2c, 0x29, 0x2a, 0x0b, 0x08, 0x0d, 0x0e, 0x07, 0x04, 0x01, 0x02, 0x13,
				0x10, 0x15, 0x16, 0x1f, 0x1c, 0x19, 0x1a };
		/**
		 * @see GALOIS256_MULTIPLY_2
		 */
		private final int[] GALOIS256_MULTIPLY_9 = { 0x00, 0x09, 0x12, 0x1b, 0x24, 0x2d, 0x36, 0x3f, 0x48, 0x41, 0x5a,
				0x53, 0x6c, 0x65, 0x7e, 0x77, 0x90, 0x99, 0x82, 0x8b, 0xb4, 0xbd, 0xa6, 0xaf, 0xd8, 0xd1, 0xca, 0xc3,
				0xfc, 0xf5, 0xee, 0xe7, 0x3b, 0x32, 0x29, 0x20, 0x1f, 0x16, 0x0d, 0x04, 0x73, 0x7a, 0x61, 0x68, 0x57,
				0x5e, 0x45, 0x4c, 0xab, 0xa2, 0xb9, 0xb0, 0x8f, 0x86, 0x9d, 0x94, 0xe3, 0xea, 0xf1, 0xf8, 0xc7, 0xce,
				0xd5, 0xdc, 0x76, 0x7f, 0x64, 0x6d, 0x52, 0x5b, 0x40, 0x49, 0x3e, 0x37, 0x2c, 0x25, 0x1a, 0x13, 0x08,
				0x01, 0xe6, 0xef, 0xf4, 0xfd, 0xc2, 0xcb, 0xd0, 0xd9, 0xae, 0xa7, 0xbc, 0xb5, 0x8a, 0x83, 0x98, 0x91,
				0x4d, 0x44, 0x5f, 0x56, 0x69, 0x60, 0x7b, 0x72, 0x05, 0x0c, 0x17, 0x1e, 0x21, 0x28, 0x33, 0x3a, 0xdd,
				0xd4, 0xcf, 0xc6, 0xf9, 0xf0, 0xeb, 0xe2, 0x95, 0x9c, 0x87, 0x8e, 0xb1, 0xb8, 0xa3, 0xaa, 0xec, 0xe5,
				0xfe, 0xf7, 0xc8, 0xc1, 0xda, 0xd3, 0xa4, 0xad, 0xb6, 0xbf, 0x80, 0x89, 0x92, 0x9b, 0x7c, 0x75, 0x6e,
				0x67, 0x58, 0x51, 0x4a, 0x43, 0x34, 0x3d, 0x26, 0x2f, 0x10, 0x19, 0x02, 0x0b, 0xd7, 0xde, 0xc5, 0xcc,
				0xf3, 0xfa, 0xe1, 0xe8, 0x9f, 0x96, 0x8d, 0x84, 0xbb, 0xb2, 0xa9, 0xa0, 0x47, 0x4e, 0x55, 0x5c, 0x63,
				0x6a, 0x71, 0x78, 0x0f, 0x06, 0x1d, 0x14, 0x2b, 0x22, 0x39, 0x30, 0x9a, 0x93, 0x88, 0x81, 0xbe, 0xb7,
				0xac, 0xa5, 0xd2, 0xdb, 0xc0, 0xc9, 0xf6, 0xff, 0xe4, 0xed, 0x0a, 0x03, 0x18, 0x11, 0x2e, 0x27, 0x3c,
				0x35, 0x42, 0x4b, 0x50, 0x59, 0x66, 0x6f, 0x74, 0x7d, 0xa1, 0xa8, 0xb3, 0xba, 0x85, 0x8c, 0x97, 0x9e,
				0xe9, 0xe0, 0xfb, 0xf2, 0xcd, 0xc4, 0xdf, 0xd6, 0x31, 0x38, 0x23, 0x2a, 0x15, 0x1c, 0x07, 0x0e, 0x79,
				0x70, 0x6b, 0x62, 0x5d, 0x54, 0x4f, 0x46 };
		/**
		 * @see GALOIS256_MULTIPLY_2
		 */
		private final int[] GALOIS256_MULTIPLY_11 = { 0x00, 0x0b, 0x16, 0x1d, 0x2c, 0x27, 0x3a, 0x31, 0x58, 0x53, 0x4e,
				0x45, 0x74, 0x7f, 0x62, 0x69, 0xb0, 0xbb, 0xa6, 0xad, 0x9c, 0x97, 0x8a, 0x81, 0xe8, 0xe3, 0xfe, 0xf5,
				0xc4, 0xcf, 0xd2, 0xd9, 0x7b, 0x70, 0x6d, 0x66, 0x57, 0x5c, 0x41, 0x4a, 0x23, 0x28, 0x35, 0x3e, 0x0f,
				0x04, 0x19, 0x12, 0xcb, 0xc0, 0xdd, 0xd6, 0xe7, 0xec, 0xf1, 0xfa, 0x93, 0x98, 0x85, 0x8e, 0xbf, 0xb4,
				0xa9, 0xa2, 0xf6, 0xfd, 0xe0, 0xeb, 0xda, 0xd1, 0xcc, 0xc7, 0xae, 0xa5, 0xb8, 0xb3, 0x82, 0x89, 0x94,
				0x9f, 0x46, 0x4d, 0x50, 0x5b, 0x6a, 0x61, 0x7c, 0x77, 0x1e, 0x15, 0x08, 0x03, 0x32, 0x39, 0x24, 0x2f,
				0x8d, 0x86, 0x9b, 0x90, 0xa1, 0xaa, 0xb7, 0xbc, 0xd5, 0xde, 0xc3, 0xc8, 0xf9, 0xf2, 0xef, 0xe4, 0x3d,
				0x36, 0x2b, 0x20, 0x11, 0x1a, 0x07, 0x0c, 0x65, 0x6e, 0x73, 0x78, 0x49, 0x42, 0x5f, 0x54, 0xf7, 0xfc,
				0xe1, 0xea, 0xdb, 0xd0, 0xcd, 0xc6, 0xaf, 0xa4, 0xb9, 0xb2, 0x83, 0x88, 0x95, 0x9e, 0x47, 0x4c, 0x51,
				0x5a, 0x6b, 0x60, 0x7d, 0x76, 0x1f, 0x14, 0x09, 0x02, 0x33, 0x38, 0x25, 0x2e, 0x8c, 0x87, 0x9a, 0x91,
				0xa0, 0xab, 0xb6, 0xbd, 0xd4, 0xdf, 0xc2, 0xc9, 0xf8, 0xf3, 0xee, 0xe5, 0x3c, 0x37, 0x2a, 0x21, 0x10,
				0x1b, 0x06, 0x0d, 0x64, 0x6f, 0x72, 0x79, 0x48, 0x43, 0x5e, 0x55, 0x01, 0x0a, 0x17, 0x1c, 0x2d, 0x26,
				0x3b, 0x30, 0x59, 0x52, 0x4f, 0x44, 0x75, 0x7e, 0x63, 0x68, 0xb1, 0xba, 0xa7, 0xac, 0x9d, 0x96, 0x8b,
				0x80, 0xe9, 0xe2, 0xff, 0xf4, 0xc5, 0xce, 0xd3, 0xd8, 0x7a, 0x71, 0x6c, 0x67, 0x56, 0x5d, 0x40, 0x4b,
				0x22, 0x29, 0x34, 0x3f, 0x0e, 0x05, 0x18, 0x13, 0xca, 0xc1, 0xdc, 0xd7, 0xe6, 0xed, 0xf0, 0xfb, 0x92,
				0x99, 0x84, 0x8f, 0xbe, 0xb5, 0xa8, 0xa3 };
		/**
		 * @see GALOIS256_MULTIPLY_2
		 */
		private final int[] GALOIS256_MULTIPLY_13 = { 0x00, 0x0d, 0x1a, 0x17, 0x34, 0x39, 0x2e, 0x23, 0x68, 0x65, 0x72,
				0x7f, 0x5c, 0x51, 0x46, 0x4b, 0xd0, 0xdd, 0xca, 0xc7, 0xe4, 0xe9, 0xfe, 0xf3, 0xb8, 0xb5, 0xa2, 0xaf,
				0x8c, 0x81, 0x96, 0x9b, 0xbb, 0xb6, 0xa1, 0xac, 0x8f, 0x82, 0x95, 0x98, 0xd3, 0xde, 0xc9, 0xc4, 0xe7,
				0xea, 0xfd, 0xf0, 0x6b, 0x66, 0x71, 0x7c, 0x5f, 0x52, 0x45, 0x48, 0x03, 0x0e, 0x19, 0x14, 0x37, 0x3a,
				0x2d, 0x20, 0x6d, 0x60, 0x77, 0x7a, 0x59, 0x54, 0x43, 0x4e, 0x05, 0x08, 0x1f, 0x12, 0x31, 0x3c, 0x2b,
				0x26, 0xbd, 0xb0, 0xa7, 0xaa, 0x89, 0x84, 0x93, 0x9e, 0xd5, 0xd8, 0xcf, 0xc2, 0xe1, 0xec, 0xfb, 0xf6,
				0xd6, 0xdb, 0xcc, 0xc1, 0xe2, 0xef, 0xf8, 0xf5, 0xbe, 0xb3, 0xa4, 0xa9, 0x8a, 0x87, 0x90, 0x9d, 0x06,
				0x0b, 0x1c, 0x11, 0x32, 0x3f, 0x28, 0x25, 0x6e, 0x63, 0x74, 0x79, 0x5a, 0x57, 0x40, 0x4d, 0xda, 0xd7,
				0xc0, 0xcd, 0xee, 0xe3, 0xf4, 0xf9, 0xb2, 0xbf, 0xa8, 0xa5, 0x86, 0x8b, 0x9c, 0x91, 0x0a, 0x07, 0x10,
				0x1d, 0x3e, 0x33, 0x24, 0x29, 0x62, 0x6f, 0x78, 0x75, 0x56, 0x5b, 0x4c, 0x41, 0x61, 0x6c, 0x7b, 0x76,
				0x55, 0x58, 0x4f, 0x42, 0x09, 0x04, 0x13, 0x1e, 0x3d, 0x30, 0x27, 0x2a, 0xb1, 0xbc, 0xab, 0xa6, 0x85,
				0x88, 0x9f, 0x92, 0xd9, 0xd4, 0xc3, 0xce, 0xed, 0xe0, 0xf7, 0xfa, 0xb7, 0xba, 0xad, 0xa0, 0x83, 0x8e,
				0x99, 0x94, 0xdf, 0xd2, 0xc5, 0xc8, 0xeb, 0xe6, 0xf1, 0xfc, 0x67, 0x6a, 0x7d, 0x70, 0x53, 0x5e, 0x49,
				0x44, 0x0f, 0x02, 0x15, 0x18, 0x3b, 0x36, 0x21, 0x2c, 0x0c, 0x01, 0x16, 0x1b, 0x38, 0x35, 0x22, 0x2f,
				0x64, 0x69, 0x7e, 0x73, 0x50, 0x5d, 0x4a, 0x47, 0xdc, 0xd1, 0xc6, 0xcb, 0xe8, 0xe5, 0xf2, 0xff, 0xb4,
				0xb9, 0xae, 0xa3, 0x80, 0x8d, 0x9a, 0x97 };
		/**
		 * @see GALOIS256_MULTIPLY_2
		 */
		private final int[] GALOIS256_MULTIPLY_14 = { 0x00, 0x0e, 0x1c, 0x12, 0x38, 0x36, 0x24, 0x2a, 0x70, 0x7e, 0x6c,
				0x62, 0x48, 0x46, 0x54, 0x5a, 0xe0, 0xee, 0xfc, 0xf2, 0xd8, 0xd6, 0xc4, 0xca, 0x90, 0x9e, 0x8c, 0x82,
				0xa8, 0xa6, 0xb4, 0xba, 0xdb, 0xd5, 0xc7, 0xc9, 0xe3, 0xed, 0xff, 0xf1, 0xab, 0xa5, 0xb7, 0xb9, 0x93,
				0x9d, 0x8f, 0x81, 0x3b, 0x35, 0x27, 0x29, 0x03, 0x0d, 0x1f, 0x11, 0x4b, 0x45, 0x57, 0x59, 0x73, 0x7d,
				0x6f, 0x61, 0xad, 0xa3, 0xb1, 0xbf, 0x95, 0x9b, 0x89, 0x87, 0xdd, 0xd3, 0xc1, 0xcf, 0xe5, 0xeb, 0xf9,
				0xf7, 0x4d, 0x43, 0x51, 0x5f, 0x75, 0x7b, 0x69, 0x67, 0x3d, 0x33, 0x21, 0x2f, 0x05, 0x0b, 0x19, 0x17,
				0x76, 0x78, 0x6a, 0x64, 0x4e, 0x40, 0x52, 0x5c, 0x06, 0x08, 0x1a, 0x14, 0x3e, 0x30, 0x22, 0x2c, 0x96,
				0x98, 0x8a, 0x84, 0xae, 0xa0, 0xb2, 0xbc, 0xe6, 0xe8, 0xfa, 0xf4, 0xde, 0xd0, 0xc2, 0xcc, 0x41, 0x4f,
				0x5d, 0x53, 0x79, 0x77, 0x65, 0x6b, 0x31, 0x3f, 0x2d, 0x23, 0x09, 0x07, 0x15, 0x1b, 0xa1, 0xaf, 0xbd,
				0xb3, 0x99, 0x97, 0x85, 0x8b, 0xd1, 0xdf, 0xcd, 0xc3, 0xe9, 0xe7, 0xf5, 0xfb, 0x9a, 0x94, 0x86, 0x88,
				0xa2, 0xac, 0xbe, 0xb0, 0xea, 0xe4, 0xf6, 0xf8, 0xd2, 0xdc, 0xce, 0xc0, 0x7a, 0x74, 0x66, 0x68, 0x42,
				0x4c, 0x5e, 0x50, 0x0a, 0x04, 0x16, 0x18, 0x32, 0x3c, 0x2e, 0x20, 0xec, 0xe2, 0xf0, 0xfe, 0xd4, 0xda,
				0xc8, 0xc6, 0x9c, 0x92, 0x80, 0x8e, 0xa4, 0xaa, 0xb8, 0xb6, 0x0c, 0x02, 0x10, 0x1e, 0x34, 0x3a, 0x28,
				0x26, 0x7c, 0x72, 0x60, 0x6e, 0x44, 0x4a, 0x58, 0x56, 0x37, 0x39, 0x2b, 0x25, 0x0f, 0x01, 0x13, 0x1d,
				0x47, 0x49, 0x5b, 0x55, 0x7f, 0x71, 0x63, 0x6d, 0xd7, 0xd9, 0xcb, 0xc5, 0xef, 0xe1, 0xf3, 0xfd, 0xa7,
				0xa9, 0xbb, 0xb5, 0x9f, 0x91, 0x83, 0x8d };
		/**
		 * Preset matrix used for the MixColumns step of the algorithm.
		 */
		private final int[][] MIXCOLUMNS_MATRIX = { { 0x02, 0x03, 0x01, 0x01 }, { 0x01, 0x02, 0x03, 0x01 },
				{ 0x01, 0x01, 0x02, 0x03 }, { 0x03, 0x01, 0x01, 0x02 } };
		/**
		 * Inverse of the {@link #MIXCOLUMNS_MATRIX}.
		 */
		private final int[][] MIXCOLUMNS_MATRIX_INVERSE = { { 0x0e, 0x0b, 0x0d, 0x09 }, { 0x09, 0x0e, 0x0b, 0x0d },
				{ 0x0d, 0x09, 0x0e, 0x0b }, { 0x0b, 0x0d, 0x09, 0x0e } };
		/**
		 * Preset matrix size for the {@link AESBlock}.
		 */
		private static final int MATRIX_SIZE = 4;

		/**
		 * Matrix of the {@link AESBlock}.
		 */
		private byte[][] matrix = new byte[MATRIX_SIZE][MATRIX_SIZE];

		/**
		 * Packs the passed array into an {@link AESBlock}.
		 * <p>
		 * Packs the passed byte array into a 4x4 transposed matrix as required by the
		 * AES algorithm. If the passed array is too long the excessive bits are
		 * ignored. If the passed array is too short the missing bits are replaced with
		 * zeros;
		 * 
		 * @param byteArray to be packed into the block
		 */
		public AESBlock(byte[] byteArray) {
			for (int i = 0; i < MATRIX_SIZE * MATRIX_SIZE; i++) {
				if (i < byteArray.length) {
					matrix[i / MATRIX_SIZE][i % MATRIX_SIZE] = byteArray[i];
				} else {
					matrix[i / MATRIX_SIZE][i % MATRIX_SIZE] = 0x00;
				}
			}

			matrix = transpose(matrix);
		}

		/**
		 * The AddRoundKey step of the algorithm.
		 * <p>
		 * Combines the current block with the passed key matrix.
		 * 
		 * @param keyBlock array of bytes matrix of the used key
		 */
		private void addRoundKey(byte[][] keyBlock) {
			System.out.println("addRoundKey");
			printBlock();
			for (int i = 0; i < MATRIX_SIZE; i++) {
				for (int j = 0; j < MATRIX_SIZE; j++) {
					matrix[i][j] = (byte) ((int) (matrix[i][j] ^ (int) keyBlock[j][i]));
				}
			}
			System.out.println("-->");
			printBlock();
		}

		/**
		 * The MixColumns step of the algorithm.
		 * <p>
		 * Performs the multiplication of each value of the underlying matrix with the
		 * preset {@link #MIXCOLUMNS_MATRIX} inside the 2^8 Galois field.
		 */
		private void mixColumns() {
			System.out.println("mixColumns");
			printBlock();
			byte[][] newMatrix = new byte[MATRIX_SIZE][MATRIX_SIZE];
			for (int i = 0; i < MATRIX_SIZE; i++) {
				for (int j = 0; j < MATRIX_SIZE; j++) {

					newMatrix[i][j] = (byte) (galois256Multiply(matrix[0][j], MIXCOLUMNS_MATRIX[i][0])
							^ galois256Multiply(matrix[1][j], MIXCOLUMNS_MATRIX[i][1])
							^ galois256Multiply(matrix[2][j], MIXCOLUMNS_MATRIX[i][2])
							^ galois256Multiply(matrix[3][j], MIXCOLUMNS_MATRIX[i][3]));
				}
			}
			matrix = newMatrix;
			System.out.println("-->");
			printBlock();
		}

		/**
		 * Inverse of the {@link #mixColumns()} step of the algorithm used for
		 * decryption.
		 */
		private void mixColumnsInverse() {
			System.out.println("mixColumnsInverse");
			printBlock();
			byte[][] newMatrix = new byte[MATRIX_SIZE][MATRIX_SIZE];
			for (int i = 0; i < MATRIX_SIZE; i++) {
				for (int j = 0; j < MATRIX_SIZE; j++) {

					newMatrix[i][j] = (byte) (galois256Multiply(matrix[0][j], MIXCOLUMNS_MATRIX_INVERSE[i][0])
							^ galois256Multiply(matrix[1][j], MIXCOLUMNS_MATRIX_INVERSE[i][1])
							^ galois256Multiply(matrix[2][j], MIXCOLUMNS_MATRIX_INVERSE[i][2])
							^ galois256Multiply(matrix[3][j], MIXCOLUMNS_MATRIX_INVERSE[i][3]));
				}
			}
			matrix = newMatrix;
			System.out.println("-->");
			printBlock();
		}

		/**
		 * Performs the ShiftRows step of the algorithm.
		 * <p>
		 * Shifts each word inside the passed block. The first row is left unchanged,
		 * the second row is shifted by 1 position to the left, the third row is shifted
		 * by 2 positions and the forth row is shifted by 3 positions.
		 * 
		 * @param block to each row of which the shift will be applied
		 */
		private void shiftRows() {
			System.out.println("shiftRows");
			printBlock();
			for (int i = 1; i <= 3; i++) {
				for (int j = 1; j <= i; j++) {
					matrix[i] = rotWord(matrix[i]);
				}
			}
			System.out.println("-->");
			printBlock();
		}

		/**
		 * Inverse of the {@link #shiftRows()} step of the algorithm.
		 * <p>
		 * The {@link #MIXCOLUMNS_MATRIX_INVERSE} matrix is used for this operation.
		 */
		private void shiftRowsInverse() {
			System.out.println("shiftRowsInverse");
			printBlock();
			for (int i = 1; i <= 3; i++) {
				for (int j = 1; j <= i; j++) {
					matrix[i] = rotWordInverse(matrix[i]);
				}
			}
			System.out.println("-->");
			printBlock();
		}

		/**
		 * The SubBytes step of the algorithm.
		 * <p>
		 * Each byte of the underlying matrix is replaced with the appropriate byte from
		 * the {@link #S_BOX}. The position of the replacing byte is determined by
		 * interpreting the byte to be replaced as unsigned byte.
		 */
		private void subBytes() {
			System.out.println("subBytes");
			printBlock();
			byte[][] newMatrix = new byte[MATRIX_SIZE][MATRIX_SIZE];
			for (int i = 0; i < MATRIX_SIZE; i++) {
				for (int j = 0; j < MATRIX_SIZE; j++) {
					newMatrix[i][j] = (byte) S_BOX[matrix[i][j] & 0xFF];
				}
			}
			matrix = newMatrix;
			System.out.println("-->");
			printBlock();
		}

		/**
		 * The inverse of {@link #subBytes()} step of the algorithm.
		 * <p>
		 * The {@link #S_BOX_INVERSE} is used for this operation.
		 */
		private void subBytesInverse() {
			System.out.println("subBytesInverse");
			printBlock();
			byte[][] newMatrix = new byte[MATRIX_SIZE][MATRIX_SIZE];
			for (int i = 0; i < MATRIX_SIZE; i++) {
				for (int j = 0; j < MATRIX_SIZE; j++) {
					newMatrix[i][j] = (byte) S_BOX_INVERSE[matrix[i][j] & 0xFF];
				}
			}

			matrix = newMatrix;
			System.out.println("-->");
			printBlock();
		}

		/**
		 * Multiplication inside the 2^8 Galois field.
		 * <p>
		 * Uses precalculated values
		 * <li>{@link #GALOIS256_MULTIPLY_2}</li>
		 * <li>{@link #GALOIS256_MULTIPLY_3}</li>
		 * <li>{@link #GALOIS256_MULTIPLY_9}</li>
		 * <li>{@link #GALOIS256_MULTIPLY_11}</li>
		 * <li>{@link #GALOIS256_MULTIPLY_13}</li>
		 * <li>{@link #GALOIS256_MULTIPLY_14}</li> The value is interpreted as unsigned
		 * byte.
		 * 
		 * @param value  first factor of the multiplication
		 * @param factor second factor of the multiplication
		 * @return product of the multiplication
		 * @throws UnsupportedOperationException if the needed matrix with preset values
		 *                                       is missing
		 */
		private int galois256Multiply(byte value, int factor) {
			if (factor == 0) {
				return 0;
			}

			if (factor == 1) {
				return value;
			}

			if (factor == 2) {
				return GALOIS256_MULTIPLY_2[value & 0xFF];
			}

			if (factor == 3) {
				return GALOIS256_MULTIPLY_3[value & 0xFF];
			}

			if (factor == 9) {
				return GALOIS256_MULTIPLY_9[value & 0xFF];
			}

			if (factor == 11) {
				return GALOIS256_MULTIPLY_11[value & 0xFF];
			}

			if (factor == 13) {
				return GALOIS256_MULTIPLY_13[value & 0xFF];
			}

			if (factor == 14) {
				return GALOIS256_MULTIPLY_14[value & 0xFF];
			}

			throw new UnsupportedOperationException();
		}

		/**
		 * Unpacks the underlying matrix into a byte array
		 * <p>
		 * Transposes the underlying matrix and unpacks it back into a byte array of
		 * size 16.
		 * 
		 * @return array of bytes
		 */
		public byte[] toByteArray() {
			byte[] byteArray = new byte[MATRIX_SIZE * MATRIX_SIZE];
			matrix = transpose(matrix);

			for (int i = 0; i < MATRIX_SIZE; i++) {
				for (int j = 0; j < MATRIX_SIZE; j++) {
					byteArray[MATRIX_SIZE * i + j] = matrix[i][j];
				}
			}

			return byteArray;
		}

		/**
		 * Prints the block to the standard output.
		 * <p>
		 * For debugging purposes only.
		 */
		public void printBlock() {
			for (int i = 0; i < MATRIX_SIZE; i++) {
				for (int j = 0; j < MATRIX_SIZE; j++) {
					System.out.print(String.format("%02X", matrix[i][j]) + " ");
				}
				System.out.println();
			}
		}
	}

	/**
	 * 
	 * Internal class AESKey used to represent the key for the AES algorithm.
	 * <p>
	 * Only calculates the expanded key on {@link #expandKey()} call. Can only
	 * represent 128 bit keys.
	 *
	 */
	private class AESKey {
		private byte[][] expandedKey;
		private byte[] initialKey;

		/**
		 * Extends and sets the {@link #initialKey}
		 * <p>
		 * Extends the passed raw key to the size of 128 bits by filling the missing
		 * part with zeroes and initializes the {@link #initialKey} field.
		 * 
		 * @param rawKey original key
		 */
		public AESKey(byte[] rawKey) {
			byte[] adjustedKey = new byte[MAX_KEY_SIZE];
			for (int i = 0; i < rawKey.length; i++) {
				adjustedKey[i] = rawKey[i];
			}

			this.initialKey = adjustedKey;
		}

		/**
		 * ExpandKey step of the AES algorithm. Fills the {@link expandedKey} matrix.
		 */
		public void expandKey() {
			// Calculate the rcon constant
			final byte[][] rcon = new byte[ROUNDS + 1][4];
			for (int i = 1; i <= ROUNDS; i++) {
				byte firstByte;
				if (i == 1) {
					firstByte = 0x01;
				} else {
					if ((rcon[i - 1][0] & 0xFF) < 0x80) {
						firstByte = (byte) (2 * rcon[i - 1][0]);
					} else {
						firstByte = (byte) ((2 * rcon[i - 1][0]) ^ 0x1B);
					}
				}

				rcon[i] = new byte[] { firstByte, 0x0, 0x0, 0x0 };
			}

			// Constants for calculation of a 128 bit key
			final int N = 4;
			final int R = 11;
			byte[][] k = new byte[N][4];
			for (int i = 0; i < 4; i++) {
				for (int j = 0; j < 4; j++) {
					k[i][j] = initialKey[4 * i + j];
				}
			}

			expandedKey = new byte[4 * R][4];

			for (int i = 0; i < expandedKey.length; i++) {
				if (i < N) {
					expandedKey[i] = k[i];
				} else {
					if (i >= N && i % N == 0) {
						expandedKey[i] = xor(xor(expandedKey[i - N], subWord(rotWord(expandedKey[i - 1]))),
								rcon[i / N]);
					} else {
						expandedKey[i] = xor(expandedKey[i - N], expandedKey[i - 1]);

					}
				}
			}

			printKey();
		}

		/**
		 * Substitutes each byte of the passed word using {@link #S_BOX}.
		 * 
		 * @param word to be processed
		 * @return word with replaced bytes
		 */
		private byte[] subWord(byte[] word) {
			byte[] newWord = new byte[word.length];
			for (int i = 0; i < word.length; i++) {
				newWord[i] = (byte) S_BOX[word[i] & 0xFF];
			}

			return newWord;
		}

		/**
		 * Prints the contents of the key.
		 * <p>
		 * For debugging purposes only.
		 */
		public void printKey() {
			for (int g = 0; g < expandedKey.length; g = g + 4) {
				for (int i = g; i < g + 4; i++) {
					for (int j = 0; j < 4; j++) {
						System.out.print(String.format("%02X", expandedKey[i][j]) + " ");
					}
					System.out.println();
				}
				System.out.println("-------------------------------");
			}
		}

		/**
		 * Gets the key block at the passed index.
		 * <p>
		 * Key block is a 4x4 matrix built from 4 byte words at indices {@code index} to
		 * {@code index+4}
		 * 
		 * @param index
		 * @return
		 */
		public byte[][] getSubKey(int index) {
			return new byte[][] { expandedKey[index], expandedKey[index + 1], expandedKey[index + 2],
					expandedKey[index + 3] };
		}
	}
}

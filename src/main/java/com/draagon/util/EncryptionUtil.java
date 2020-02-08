package com.draagon.util;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.SecretKeySpec;


public class EncryptionUtil {

  private static String ALGORITHM = "DESede";

  /** Our default secret key, shhh! Don't tell anybody about it!
   * If you want to create a new one (and thus invalidate all existing encrypted
   * data), just run method generateKey() and store its value here. Then call
   * setPrivatekey() in an initialization block of your code.
   */
  private static String privatekey = "9JiPL4ws33auLxqe04Ms9xbWSRO6StA0";

    /**
     * This array is a lookup table that translates 6-bit positive integer
     * index values into their "Base64 Alphabet" equivalents as specified
     * in Table 1 of RFC 2045.
     */
    private static final char intToBase64[] = {
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
        'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
        'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'
    };

    /**
     * This array is a lookup table that translates unicode characters
     * drawn from the "Base64 Alphabet" (as specified in Table 1 of RFC 2045)
     * into their 6-bit positive integer equivalents.  Characters that
     * are not in the Base64 alphabet but fall within the bounds of the
     * array are translated to -1.
     */
    private static final byte base64ToInt[] = {
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63, 52, 53, 54,
            55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4,
            5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23,
            24, 25, -1, -1, -1, -1, -1, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34,
            35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51
        };

    /**
     * Translates the specified character, which is assumed to be in the
     * "Base 64 Alphabet" into its equivalent 6-bit positive integer.
     *
     * @throw IllegalArgumentException or ArrayOutOfBoundsException if
     *        c is not in the Base64 Alphabet.
     */
    private static int base64toInt(char c, byte[] alphaToInt) {
        int result = alphaToInt[c];
        if (result < 0)
            throw new IllegalArgumentException("Illegal character " + c);
        return result;
    }


    /** Convert given byte array to base 64. */
    private static String byteArrayToBase64(byte[] a) {
        int aLen = a.length;
        int numFullGroups = aLen/3;
        int numBytesInPartialGroup = aLen - 3*numFullGroups;
        int resultLen = 4*((aLen + 2)/3);
        StringBuffer result = new StringBuffer(resultLen);
        char[] intToAlpha = intToBase64;

        // Translate all full groups from byte array elements to Base64
        int inCursor = 0;
        for (int i=0; i<numFullGroups; i++) {
            int byte0 = a[inCursor++] & 0xff;
            int byte1 = a[inCursor++] & 0xff;
            int byte2 = a[inCursor++] & 0xff;
            result.append(intToAlpha[byte0 >> 2]);
            result.append(intToAlpha[(byte0 << 4)&0x3f | (byte1 >> 4)]);
            result.append(intToAlpha[(byte1 << 2)&0x3f | (byte2 >> 6)]);
            result.append(intToAlpha[byte2 & 0x3f]);
        }

        // Translate partial group if present
        if (numBytesInPartialGroup != 0) {
            int byte0 = a[inCursor++] & 0xff;
            result.append(intToAlpha[byte0 >> 2]);
            if (numBytesInPartialGroup == 1) {
                result.append(intToAlpha[(byte0 << 4) & 0x3f]);
                result.append("==");
            } else {
                // assert numBytesInPartialGroup == 2;
                int byte1 = a[inCursor++] & 0xff;
                result.append(intToAlpha[(byte0 << 4)&0x3f | (byte1 >> 4)]);
                result.append(intToAlpha[(byte1 << 2)&0x3f]);
                result.append('=');
            }
        }
        // assert inCursor == a.length;
        // assert result.length() == resultLen;
        return result.toString();
    }

    /** Decode given base 64 string to byte array. */
    private static byte[] base64ToByteArray(String s) {
        byte[] alphaToInt = base64ToInt;
        int sLen = s.length();
        int numGroups = sLen/4;
        if (4*numGroups != sLen)
            throw new IllegalArgumentException(
                "String length must be a multiple of four.");
        int missingBytesInLastGroup = 0;
        int numFullGroups = numGroups;
        if (sLen != 0) {
            if (s.charAt(sLen-1) == '=') {
                missingBytesInLastGroup++;
                numFullGroups--;
            }
            if (s.charAt(sLen-2) == '=')
                missingBytesInLastGroup++;
        }
        byte[] result = new byte[3*numGroups - missingBytesInLastGroup];

        // Translate all full groups from base64 to byte array elements
        int inCursor = 0, outCursor = 0;
        for (int i=0; i<numFullGroups; i++) {
            int ch0 = base64toInt(s.charAt(inCursor++), alphaToInt);
            int ch1 = base64toInt(s.charAt(inCursor++), alphaToInt);
            int ch2 = base64toInt(s.charAt(inCursor++), alphaToInt);
            int ch3 = base64toInt(s.charAt(inCursor++), alphaToInt);
            result[outCursor++] = (byte) ((ch0 << 2) | (ch1 >> 4));
            result[outCursor++] = (byte) ((ch1 << 4) | (ch2 >> 2));
            result[outCursor++] = (byte) ((ch2 << 6) | ch3);
        }

        // Translate partial group, if present
        if (missingBytesInLastGroup != 0) {
            int ch0 = base64toInt(s.charAt(inCursor++), alphaToInt);
            int ch1 = base64toInt(s.charAt(inCursor++), alphaToInt);
            result[outCursor++] = (byte) ((ch0 << 2) | (ch1 >> 4));

            if (missingBytesInLastGroup == 1) {
                int ch2 = base64toInt(s.charAt(inCursor++), alphaToInt);
                result[outCursor++] = (byte) ((ch1 << 4) | (ch2 >> 2));
            }
        }
        // assert inCursor == s.length()-missingBytesInLastGroup;
        // assert outCursor == result.length;
        return result;
    }

    /** Generate key, primarily useful during development to get a key. */
  public static String generateKey() throws Exception {
    KeyGenerator keygen = KeyGenerator.getInstance(ALGORITHM);
    SecretKey key = keygen.generateKey();
    SecretKeyFactory desEdeFactory = SecretKeyFactory.getInstance(ALGORITHM);
    DESedeKeySpec desEdeSpec = (DESedeKeySpec)
      desEdeFactory.getKeySpec(key, javax.crypto.spec.DESedeKeySpec.class);
    byte[] rawDesEdeKey = desEdeSpec.getKey();
    return byteArrayToBase64(rawDesEdeKey);
  }

  /** Sets the private key to use for encryption/decryption */
  public static void setPrivateKey( String key ) {
      privatekey = key;
  }
  
  /** Return private key. Note that this way of creating key doesn't work for all ciphers. */
  private static SecretKeySpec getPrivateKey() {
    return new SecretKeySpec(base64ToByteArray(privatekey), ALGORITHM);
  }

  /** Encrypt given text. */
  public static String encrypt( String plaintext ) {
    try {
	    Cipher cipher = Cipher.getInstance(ALGORITHM);
	    cipher.init(Cipher.ENCRYPT_MODE, getPrivateKey());
	    byte[] ciphertext = cipher.doFinal(plaintext.getBytes());
	    return byteArrayToBase64(ciphertext);
	}
	catch( Exception e ) {
		throw new RuntimeException( "Could not encrypt string [" + plaintext + "]: " + e.getMessage(), e );
	}
  }

  /** Decrypt given text. */
  public static String decrypt( String ciphertext ) {
	try {
	    Cipher cipher = Cipher.getInstance(ALGORITHM);
	    cipher.init(Cipher.DECRYPT_MODE, getPrivateKey());
	    byte[] plaintext = cipher.doFinal(base64ToByteArray(ciphertext));
	    return new String( plaintext );
	}
	catch( Exception e ) {
		throw new RuntimeException( "Could not decrypt string [" + ciphertext + "]: " + e.getMessage(), e );
	}
  }

  public static void main(String argv[]) throws Exception {
    String key = EncryptionUtil.generateKey();
    System.out.println("New key (" + key.length() + " chars): " + key);
    String plaintext, encrypted, decrypted;

    plaintext = "insight";
    encrypted = EncryptionUtil.encrypt(plaintext);
    System.out.println("Encrypted to: " + encrypted);
    decrypted = EncryptionUtil.decrypt(encrypted);
    System.out.println("Decrypted to: " + decrypted);
    if( decrypted.equals( plaintext ) ) {
      System.out.println("Success, they MATCH!!!");
    } else {
      System.out.println("Failure, they do NOT MATCH!!!");
    }
  }
}
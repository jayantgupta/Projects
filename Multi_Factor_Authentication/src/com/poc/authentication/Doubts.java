package com.poc.authentication;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class Doubts {
	public static void main(String [] args) throws Exception{
		String A = "ABCDEFGH";
		String uEK = AESEncrypt(A);
		String B = AESDecrypt(uEK);
		System.out.println(B);
	}

	// Encrypts the user key given to it by the server.
	private static String AESEncrypt(String userPlainKey)throws Exception{
		/*********/
		byte [] sk_bytes = getSystemKey();
		SecretKey sk = new SecretKeySpec(sk_bytes, "AES");

		Cipher skCipher = Cipher.getInstance("AES");
		skCipher.init(Cipher.ENCRYPT_MODE, sk);
		/*********/
		
		byte [] eArray = skCipher.doFinal(userPlainKey.getBytes());
		byte [] encArray = new Base64().encode(eArray);
		
		String uEK = new String(encArray);
		return uEK;
	}

	// Decrypts the user key.
	private static String AESDecrypt(String uEK)throws Exception{
		/*********/
		byte [] sk_bytes = getSystemKey();
		SecretKey sk = new SecretKeySpec(sk_bytes, "AES");

		Cipher skCipher = Cipher.getInstance("AES");
		skCipher.init(Cipher.DECRYPT_MODE, sk);
		/*********/
		byte [] enkArray = uEK.getBytes();
		byte [] decArray = new Base64().decode(enkArray);
		
		byte [] decrypted_data = skCipher.doFinal(decArray);
		String user_key =  new String(decrypted_data);
		return user_key;
	}

	
	private static final String keyPath = "/home/jayant/Desktop/systemKey.txt";
	protected static byte[] getSystemKey()throws Exception{
		Path path = Paths.get(keyPath);
		byte[] keyBytes = Files.readAllBytes(path);
		System.out.println("Successfully read the system key");
		return keyBytes;
	}
}

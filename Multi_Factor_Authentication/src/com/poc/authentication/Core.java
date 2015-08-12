package com.poc.authentication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.RSAKeyGenParameterSpec;
import java.util.ArrayList;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Core {
	public static void main(String [] args) throws NoSuchAlgorithmException, IOException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
		SecretKey sk1 = KeyGenerator.getInstance("AES").generateKey();
		KeyPair rsaKP;
		try {
			rsaKP = generateKeyPair();
			complex_check(rsaKP.getPublic(), rsaKP.getPrivate(), sk1);
			String str = "hello";
			byte [] t = new byte[128];
			t = str.getBytes();
			System.out.println("-" + new String(t) + "-");
		} catch (InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void complex_check(PublicKey public_key, PrivateKey private_key, SecretKey secret_key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
		String test = "Hello";
		
		// Encrypting the data with secret key. [C, S/Login]
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, secret_key);
		byte [] encrypted_data = cipher.doFinal(test.getBytes());
		
		// Encrypting the secret key with public key. [C]
		Cipher pk_cipher = Cipher.getInstance("RSA");
		pk_cipher.init(Cipher.ENCRYPT_MODE, public_key);
		byte [] encrypted_key = pk_cipher.doFinal(secret_key.getEncoded());
		
		//Decrypting the secret key with private key. [S/Registering]
		pk_cipher.init(Cipher.DECRYPT_MODE, private_key);
		byte [] decrypted_key = pk_cipher.doFinal(encrypted_key);
		
		//Creating the SecretKey from the byte representation.[S/Login]
		System.out.println("--" + secret_key.getFormat() + "--");
		SecretKey recovered_key = new SecretKeySpec(decrypted_key,"AES");
		System.out.println(secret_key.equals(recovered_key));
		
		//Decrypting the data with secret key.
		cipher.init(Cipher.DECRYPT_MODE, recovered_key);
		byte [] decrypted_data = cipher.doFinal(encrypted_data);
		System.out.println(new String(decrypted_data));
		return;
	}
	
	public static KeyPair generateKeyPair() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException{
		KeyPairGenerator rsaKeyPairGenerator = KeyPairGenerator.getInstance("RSA");
		RSAKeyGenParameterSpec spec= new RSAKeyGenParameterSpec(1024, RSAKeyGenParameterSpec.F4);
		rsaKeyPairGenerator.initialize(spec);
		KeyPair rsaKP = rsaKeyPairGenerator.generateKeyPair();
		return rsaKP;
	}
}
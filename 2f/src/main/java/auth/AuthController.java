/*
	Author : Jayant Gupta
	Date : April 29, 2015
	1. This the Module listening to incoming OTP verification
	requests and accordingly reply back to the user.
	2. Adds a new user's data to the database.
	Key Considerations :-
	1. Where should I store the system key....
	
*/
package auth;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.apache.commons.codec.binary.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;

import java.util.Date;

import main.java.com.warrenstrange.googleauth.*;
import org.apache.commons.codec.binary.Base32;

@RestController
public class AuthController{
	
	// Verifies the user's OTP code, can only be accessed by the server.
	@RequestMapping("/verify")
	public static String verifyOTP(@RequestParam(value="OTP", required=true) int OTP, @RequestParam(value="user_id", required=true) String UserID){
		try{
			if(OTP == generateOTP(UserID)){
				return "200";
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return "401";
	}
	
	private static int generateOTP(String UserID)throws Exception{
		// Get key from the DB
		String userEncryptedKey=DBController.getCurrentKey(UserID);
		System.out.println(userEncryptedKey);
		// Decrypt the user key using the system key.
		String userKey = AESDecrypt(userEncryptedKey);
		System.out.println(userKey);
		
		// Generate the OTP code
		// google Authenticator uses Base32 Encoding.
		// 2F-Auth uses, Base64 encoding,
		// but, since the system are different 
		// it is irrelevant.
		GoogleAuthenticator gAuth = new GoogleAuthenticator();
		Base32 codec = new Base32();
		byte[] decodedKey = codec.decode(userKey);
		long tm = new Date().getTime() / 30000;
		int code = gAuth.calculateCode(decodedKey, tm);
		System.out.println(code);
		return code;
	}
	
	// It will serve POST requests received from the Secrata Server.
	/*
	   Generate the user key.
	   Encrypt user key using AES key
	   then, store in the database.
	   Encrypt user key using pk_SS,
	   then, send it back to the SS.
	 */
	@RequestMapping("/addusr")
	public static String addUser(@RequestParam(value="user_id", required=true) String userID, @RequestParam(value="SS_pk", required=true) String SS_pk)throws Exception{
		// Initiate Google Authenticator Object.
		GoogleAuthenticator gAuth = new GoogleAuthenticator();
		// Create google credentials.
		GoogleAuthenticatorKey key = gAuth.createCredentials();
		// Extract key from the credentials.
		String userKey = key.getKey();
		// Encrypt the user-key.
		String user_AESEncryptedKey = AESEncrypt(userKey);
		// Insert the encrypted user-key into the database.
		DBController.addNewUser(userID, user_AESEncryptedKey);
		// Encrypt user-key with Secrata Server public key.
		String user_RSAEncryptedKey = RSAEncrypt(userKey, SS_pk);
		// Return the user key to SS.
		return user_RSAEncryptedKey;
	}
	
	// Encrypts the user key with Secrata Server Public Key.
	// Assumes the pubic key to be Base64 encode.
	// Returns the user key in Base64 Encoded format.
	private static String RSAEncrypt(String userKey, String SS_pk)throws Exception{
		// base 64 decode of the public key.
		byte [] data = new Base64().decode(SS_pk); //TODO Needs to be verified, depends on public key encoding.
		// get the Encoded spec from the data bits.
		X509EncodedKeySpec spec = new X509EncodedKeySpec(data);
		// get RSA instance from the Key Factory.
		KeyFactory fact = KeyFactory.getInstance("RSA");
		// Generate the public key from the KeyFactory.
		PublicKey pk = fact.generatePublic(spec);
		
		// Instantiate Cipher
		Cipher pkCipher = Cipher.getInstance("RSA");
		// Initialize Cipher
		pkCipher.init(Cipher.ENCRYPT_MODE, pk);
		
		// Encrypt the user Key.	
		byte [] encrypted_data = pkCipher.doFinal(userKey.getBytes());
		byte [] encoded_encrypted_data = new Base64().encode(encrypted_data);
		String user_RSAEncryptedKey = new String(encoded_encrypted_data);
		return user_RSAEncryptedKey;
	}

	// Encrypts the user key using Auth System key.
	private static String AESEncrypt(String userPlainKey)throws Exception{
		KeyManager KM = new KeyManager();
		byte [] sk_bytes = KM.getSystemKey();
		SecretKey sk = new SecretKeySpec(sk_bytes, "AES");

		Cipher skCipher = Cipher.getInstance("AES");
		skCipher.init(Cipher.ENCRYPT_MODE, sk);

		byte [] encrypted_data = skCipher.doFinal(userPlainKey.getBytes());
		byte [] encoded_encrypted_data = new Base64().encode(encrypted_data);
		String userEncryptedKey = new String(encoded_encrypted_data);
		return userEncryptedKey;
	}
	
	// Decrypts the user encrypted key obtained from the database.
	private static String AESDecrypt(String userEncryptedKey)throws Exception{
		KeyManager KM = new KeyManager();
		byte [] sk_bytes = KM.getSystemKey();
		SecretKey sk = new SecretKeySpec(sk_bytes, "AES");

		Cipher skCipher = Cipher.getInstance("AES");
		skCipher.init(Cipher.DECRYPT_MODE, sk);

		byte [] uEK = userEncryptedKey.getBytes();
		byte [] decoded_key = new Base64().decode(uEK);
		
		byte [] decrypted_data = skCipher.doFinal(decoded_key);
		String user_key =  new String(decrypted_data);
		return user_key;
	}
}

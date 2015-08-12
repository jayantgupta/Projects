/**
 * @author jayant gupta
 * Start Date Date : November 11, 2014 -
 * 
 *  Implements the SecureClient which talks to the server in a secure manner
 *  and helps to authenticate the user to the server.
 *  
 *  Presently all the methods are static to test the functionality of all the functions
 *  easy. Will be changed or decided upon at a later stage.
 *
 * Helpful Resources :: http://www.macs.hw.ac.uk/~ml355/lore/pkencryption.htm (encryption and decryption using public key and private key)
 */

package com.poc.authentication;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Console;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import main.java.com.warrenstrange.googleauth.GoogleAuthenticator;

public class SecureClient {
	
	private static SecretKey sk;	
	private static Socket clientSocket;
	private static String get_key_command = "000\n"; // \n is necessary for readLine() to function properly.
	private static DataOutputStream outToServer;
	private static ObjectInputStream keyFromServer;
	private static PublicKey public_key;
	private static Cipher pkCipher; 
	
	/*
	 * Initializes the Client with the sockets, Output Stream and InputStream 
	 */
	private static void init() throws UnknownHostException, IOException, NoSuchAlgorithmException, NoSuchPaddingException{
		System.out.println(" || Initializing the client ||");
		clientSocket = new Socket("localhost", 6789);
		outToServer = new DataOutputStream(clientSocket.getOutputStream());
		keyFromServer = new ObjectInputStream(clientSocket.getInputStream());
		pkCipher = Cipher.getInstance("RSA");
	}
	
	/*
	 * Sends the first Ping! to the server to get the public key from the server
	 * As soon as it gets the public it stores it in a private static variable.
	 */
	private static void get_public_key()throws IOException, ClassNotFoundException, InvalidKeySpecException, NoSuchAlgorithmException{
		System.out.println("|| Sending public key request to server ||");
		outToServer.writeBytes(get_key_command); // Bugged #1
		public_key = (PublicKey)keyFromServer.readObject();
	}
	
	/*
	 * Performs the initial function of conversing with the server 
	 * Instantiates the register request or login request.
	 */
	private static void initiateConversation() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException{
		System.out.println("|| Initiating conversation using server's public key ||");
		System.out.print("Enter Username : ");
		
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String username = in.readLine();
		int opt = 0;
		do{
			System.out.println("Please select the option below : \n 1) LOGIN \n 2) REGISTER \n 3) Exit");
			String option = in.readLine();
			try{
				opt = Integer.parseInt(option);
			}catch(Exception E){
				System.out.println("Invalid Input");
			}
		} while(opt != 1 && opt !=2 && opt != 3);
		switch( opt ){
		case(1):
			if(server_response(username, opt)){
				user_login(username);
			}
			break;
		case(2) : 
			if(server_response(username, opt)){
				user_register(username);
			}
			break;
		case(3) : close_all();
		}		
		System.out.println("Completed the initial conversation");
	}
	
	/*
	 * Checks the response of the server based on the user-name and
	 * request sent to the server.
	 */
	private static boolean server_response(String username, int i) throws InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException {
		//Encrypting and sending the user name to the server along with the request.
		pkCipher.init(Cipher.ENCRYPT_MODE, public_key);
		String request = username + ":" + String.valueOf(i);
		outToServer.write(pkCipher.doFinal(request.getBytes()));

		byte [] b = new byte[128];
		
		//Reading server response
		try{
			clientSocket.getInputStream().read(b);
		}catch(Exception E){
			E.printStackTrace();
		}
		String str = new String(b, 0, 4);
		if(str.equals("1111")){
			System.out.println("Invalid Input");
			return false;
		}
		switch(i){
		case(1): // Login request
			if(str.equals("1000")) return true;
			else if(str.equals("1100")){
				System.out.println("Cannot proceed with login / not a registered user.");
				return false;
			}
			else{
				System.out.println("Unrecognizable Response from the server");
				return false;
			}
		case(2): // Registration Request.
			if(str.equals("0010"))return true;
			else if(str.equals("0011")){
				System.out.println("Cannot proceed with registration / already a registered user.");
				return false;
			}
			else{
				System.out.println("Unrecognizable Response from the server");
				return false;
			}
		default:
			return false;
		}
	}
	
	/*
	 * Encryption Function, encrypts the data with server's public key.
	 */
	private static byte [] encrypt_data(byte [] data) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
		pkCipher.init(Cipher.ENCRYPT_MODE, public_key);
		byte[] encrypted_data = pkCipher.doFinal(data);
		return encrypted_data;
	}
	
	/*
	 * Enables the Authentication inputs based on the code provided.
	 * Arguments include the code and flag. Flag representing the 
	 * current state, either registration or Login phase. 
	 */
	private static final String otp_key_file = "/home/jayant/Desktop/3f_auth/otp.key";
	private static void enable_interfaces(boolean [] code, int flag) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException, NoSuchAlgorithmException, NoSuchPaddingException{ 
		System.out.println("Enabling interfaces");
		Console console = System.console();
		int count;
		
		// Initializing the secret key cipher.
		Cipher skCipher = Cipher.getInstance("AES");
		
		if(code[0]){
			System.out.print("Enter Password (1F): ");
			char [] password = console.readPassword(); // Reading the password.
			byte [] password_data = new String(password).getBytes(); // Getting the password in byte array format.
		
			//Encrypting password with the Secret Key.
			skCipher.init(Cipher.ENCRYPT_MODE,sk);
			byte [] sk_encrypted_pass = skCipher.doFinal(password_data);

			// Encrypting the encrypted_password with the public key of server
			byte [] encrypted_password = encrypt_data(sk_encrypted_pass);
			clientSocket.getOutputStream().write(encrypted_password);
			
			if(flag == 2){ // only happens while registering.
				// Encrypting the secret key with the public key of the server 
				byte [] encrypted_secret_key = encrypt_data(sk.getEncoded());
				clientSocket.getOutputStream().write(encrypted_secret_key);
			}
		}
		if(code[1]){
			byte [] decrypted_otp_data = null;
			if(flag == 2){
				System.out.println("Getting the OTP authentication data.");
				// OTP code to be inserted here.
				byte [] otp_data = new byte[128];
				count = clientSocket.getInputStream().read(otp_data);
				skCipher.init(Cipher.DECRYPT_MODE, sk);

				decrypted_otp_data = skCipher.doFinal(otp_data, 0, count);
				String user_key = new String(decrypted_otp_data);

				System.out.println(user_key);
				System.out.println("Received the OTP authentication data.");

				FileOutputStream fos = new FileOutputStream(new File(otp_key_file));
				/*
				 * Storing the file in byte form in a file.
				 * TODO  store the key in an encrypted form. { Bcrypt }
				 */
				fos.write(decrypted_otp_data);
			}
			if(flag == 1){// Login sequence.
				
				//read the stored key
				File key_file = new File(otp_key_file);
				FileInputStream fis = new FileInputStream(key_file);
				decrypted_otp_data = new byte[(int)key_file.length()];
				fis.read(decrypted_otp_data);
				
				GoogleAuthenticator gAuth = new GoogleAuthenticator();
				int validation_code = gAuth.calculateCode(decrypted_otp_data, new Date().getTime());
				
				System.out.println("Sending the generated OTP code : " + String.valueOf(validation_code));

				ByteBuffer b = ByteBuffer.allocate(4);
				b.putInt(validation_code);

				byte [] encrypted_code = encrypt_data(b.array());
				clientSocket.getOutputStream().write(encrypted_code);
			}
			
		}
		if(code[2]){
			// Finger print module to be inserted here.
		}
		return;
	}

	/*
	 * Handles the registering mechanism of the user	
	 */
	private static void user_register(String username) throws NoSuchAlgorithmException, IOException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException {
		set_secret_key(null);
		Console console = System.console();
		String reply;
		boolean [] code = new boolean []{true, true, true};
		
		// Disabling the 1F Authentication
		System.out.print("Input \"n\" to disable 1F Authentication else enter/return [Default is enabled]: " );
		reply = console.readLine();
		if(reply.toLowerCase().equals("n"))	code[0] = false;
		
		//Disabling the 2F Authentication
		System.out.print("Input \"n\" to disable 2F Authentication else enter/return [Default is enabled]: " );
		reply = console.readLine();
		if(reply.toLowerCase().equals("n")) code[1] = false;
		
		//Disabling the 3F Authentication
		System.out.print("Input \"n\" to disable 3F Authentication else enter/return [Default is enabled]: " );
		reply = console.readLine();
		if(reply.toLowerCase().equals("n")) code[2] = false;
		
		// Generating the code to be sent to the server.
		String str_code = "";
		for(boolean b:code){
			if(b)str_code += "1";
			else str_code +="0";
		}
		
		//TODO Encrypt and then send the code to the server.
		clientSocket.getOutputStream().write(str_code.getBytes());
		
		// TODO get an ack from the server here.
		enable_interfaces(code , 2);
	}
	
	/*
	 * Handles the logging in mechanism of the user. 
	 * Sends the various authentication factors in an encrypted form to the server.
	 */
	private static final String secret_key_file = "/home/jayant/Desktop/3f_auth/AES.key";
	// TODO :: Note that at present, client cannot be run on the same machine for multiple users
	// since it has a single hard coded file for storing the passwords.
	private static void user_login(String username) throws NoSuchAlgorithmException, IOException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		System.out.println("Logging in !!");

		// Using the system stored secret key.
		set_secret_key(new File(secret_key_file));

		// Reading the user code, set at the time of registration 
		byte [] encrypted_code = new byte[128];
		int count = clientSocket.getInputStream().read(encrypted_code);
		
		// Initializing an AES cipher to decrypt the code 
		Cipher skCipher = Cipher.getInstance("AES");
		skCipher.init(Cipher.DECRYPT_MODE, sk);
		byte [] decrypted_code = skCipher.doFinal(encrypted_code, 0, count);
		
		// Generating the client understandable code.
		String code = new String(decrypted_code);
		char [] char_code = code.toCharArray();
		boolean [] bool_code = new boolean[char_code.length];
		for(int i = 0 ; i < bool_code.length ; i++){
			if(char_code[i] == '1')bool_code[i] = true;
			else bool_code[i] = false;
		}
		enable_interfaces(bool_code, 1);
	}
	
	/*
	 * Retrieves the secret key from file or generates a new key if
	 * file pointer in null.
	 * TODO : Key management.
	 */
	@SuppressWarnings("resource")
	private static void set_secret_key(File secret_holder) throws NoSuchAlgorithmException, IOException{
		// Generating a new key.
		if(secret_holder == null){
			KeyGenerator KeyGen = KeyGenerator.getInstance("AES");
			KeyGen.init(128);
			sk = KeyGen.generateKey();
			
			FileOutputStream fos = new FileOutputStream(new File(secret_key_file));
			fos.write(sk.getEncoded());
		}
		// Using the system stored data for making the secret key. 
		else{
			byte[] key_from_file = new byte[(int)secret_holder.length()];
			new FileInputStream(secret_holder).read(key_from_file);
			sk = new SecretKeySpec(key_from_file, 0 , key_from_file.length, "AES");
		}
		return;
	}

	/*
	 * Called when the work of the client is finished.
	 * Closes the streams attached to the client socket
	 * and finally the socket itself.
	 */
	private static void close_all() throws IOException {
		System.out.println("Closing the client and signing off!!");
		keyFromServer.close();
		outToServer.close();
		clientSocket.close();
		System.out.println("Good Day!!");
	}
	
	/*
	 * Main function to test, demonstrate and call the functions
	 */
	public static void main(String [] args) throws UnknownHostException, IOException{
		System.out.println("~~~~~~~~~~|| Client running ||~~~~~~~~~~");
		try{
			init();
			get_public_key();
			initiateConversation();
			close_all();
		}catch(Exception E){
			System.out.println(E.toString());
		}
	}
}
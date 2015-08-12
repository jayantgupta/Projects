/**
 * Author : Jayant Gupta
 * Start Date : November 11, 2014 --
 * 
 *  This program is a Secure Server, used to authenticate multiple clients 
 *  based on multiple Factors.
 *  
 *  All the methods are non-static.
 *  The server waits for a client to come, every new client starts a new thread
 *  this thread starts by sending the public key of the server, all the 
 *  subsequent interaction is encrypted eiher by server's public key or the 
 *  user's (Registered) established key.
 */
package com.poc.authentication;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.RSAKeyGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import main.java.com.warrenstrange.googleauth.GoogleAuthenticator;

public class SecureServer {
	private KeyPair rsaKP;
	private DataBase DB;
	
	public static void main(String [] args) throws IOException{
		SecureServer SS = new SecureServer();
		try{
			SS.init();
			SS.AcceptRequests();
		}catch(Exception E){
			E.printStackTrace();
		}
	}
	
	private void init() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException{
		System.out.println("Initializing the Server !!");
		generateKeyPair();
		DB = new DataBase();
		DB.init();
	}
	
	/*
	 * Generates the Public/Private key of the server and stores in a global variable.
	 */
	private void generateKeyPair() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException{
		KeyPairGenerator rsaKeyPairGenerator = KeyPairGenerator.getInstance("RSA");
		RSAKeyGenParameterSpec spec= new RSAKeyGenParameterSpec(1024, RSAKeyGenParameterSpec.F4);
		rsaKeyPairGenerator.initialize(spec);
		rsaKP = rsaKeyPairGenerator.generateKeyPair();
	}
	
	/*
	 * Runs infinitely and answers to various client requests.
	 */
	private void AcceptRequests() throws IOException{
		@SuppressWarnings("resource")
		ServerSocket welcomeSocket = new ServerSocket(6789);
		System.out.println("----------|| Waiting for requests ||-----------");
		while(true){
			ClientWorker CW;
			try{
				CW = new ClientWorker(welcomeSocket.accept(),rsaKP, DB);
				Thread t = new Thread(CW);
				t.start();
			}catch(Exception E){
				System.out.println(E.toString());
			}
		}
	}
}

/*
 * Special Printing class.
 */
class Printer{
	/*
	 * Prints the byte array as a character array.
	 */
	public static void printBytes(byte [] data){
		for(byte B: data){
			System.out.print((char)B);
		}
		System.out.println();
	}
}

/*
 * Database class for storing the user information.
 */
class DataBase{
	
	// The following variable contains the username as the key and
	// a string array as value. The first value is the code of active
	// factors, followed by client's secret key, then n ( = 3) factors.
	//TODO || Username | Enc(Code)_sk_user | Enc(Secret Key)_pk_server | Enc(Factor1/Pass)_sk_user || 
	private HashMap<String, ArrayList<byte[]>> user_info;
	
	// Constructor
	DataBase(){
		user_info = null;
	}
	
	// Initializes the database HashMap, with username string as the key.
	public void init(){
		if(user_info == null){
			user_info = new HashMap<String,ArrayList<byte []>>();
		}
	}
	
	// Inserts the record in the database.
	public void insert_new_user(String userName, ArrayList<byte []> factors){
		user_info.put(userName, factors);
	}
	
	// Checks whether the user is a registered user or not.
	public boolean is_registered_user(String username){
		return this.user_info.containsKey(username);
	}
	
	//updates the database with new user information.
	public void update_database(String username, ArrayList<byte []> factors){
		user_info.put(username, factors);
	}
	
	public ArrayList<byte []> get_details(String username){
		return user_info.get(username);
	}
}
/*
 * Handles a singular client
 * - Sends the public key of the server.
 * - Handles Registration of a user.
 * - Handles Login of the user.
 */
class ClientWorker implements Runnable{

	private Socket client;
	private KeyPair rsaKP;
	private BufferedReader inFromClient;
	private ObjectOutputStream outToClient;
	private DataInputStream bytesFromClient;
	String username;
	private DataBase DB;
	
	//Constructor
	ClientWorker(Socket client, KeyPair rsaKP, DataBase DB) throws IOException{
		this.client = client;
		this.rsaKP = rsaKP;
		this.inFromClient = new BufferedReader(new InputStreamReader(client.getInputStream()));
		this.outToClient = new ObjectOutputStream(client.getOutputStream());
		this.bytesFromClient = new DataInputStream(client.getInputStream());
		this.DB = DB;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 * Takes in the client request, if acceptable then registers or logs in the client accordingly.
	 */
	@Override
	public void run() {
		String clientDetails;
		System.out.println("Connected to a new client : ");
		System.out.println("Client Address : " + this.client.getRemoteSocketAddress().toString());
		try{
			clientDetails = inFromClient.readLine();
			if(clientDetails.equals("000")){
				outToClient.writeObject(rsaKP.getPublic()); // Sending the public key back to the client
				System.out.println("Public Key Sent");
			}
			// Improper request for the public key from the server.
			else return; 
			
			byte [] userName = new byte[128]; // Fixed Length.
			//Receives encrypted data from the client
			bytesFromClient.read(userName);
					
			byte[] decrypted_data = decrypt(userName);
			String status = handle_client_request(decrypted_data);
			client.getOutputStream().write(status.getBytes());
			switch (status){
			case("1111"):
				outToClient.writeBytes("1111");
				break;
			case("1000"):
				outToClient.writeBytes("1000");
				logging_in(decrypted_data);
				break;
			case("1100"):
				outToClient.writeBytes("1100");
				break;
			case("0010"):
				outToClient.writeBytes("0010");
				registering_in(decrypted_data);
				break;
			case("0011"):
				outToClient.writeBytes("0011");
				break;
			}
		}catch(Exception E){
			E.printStackTrace();
		}
		try {
			this.client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Registers a new client, with valid checks and stores various authentication factors corresponding to the client.
	 */
	private void registering_in(byte[] decrypted_data) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		System.out.println("Registeration service active");
		//Receiving the code for active authentication factors.
		byte [] code = new byte[128];
		int count = bytesFromClient.read(code);
		String str_code = new String(code, 0, count);
		char [] char_code = str_code.toCharArray();
		if(str_code.trim().length() != 3){
			System.out.println("Unrecognizable Code <" + str_code + ">. Cannot Register the user");
			return;
		}
		// Code check complete. Proceeding further.
		
		// Initializing the record of the user to be entered into the database.
		ArrayList<byte []> user_info = new ArrayList<byte []>();
		user_info.add(str_code.getBytes()); // adding code to the user_info

		byte sk_bytes [] = null;
		// Handling the first factor.
		if(char_code[0] == '1'){
			System.out.println("Receiving data for First Authentication");
			
			//Receiving the password encrypted twice, user secret key and server public key.
			byte [] doubly_encrypted_pass = new byte[128];
			count = bytesFromClient.read(doubly_encrypted_pass);
			
			// Creating a public key cipher to decrypt the Secret key of the user.
			Cipher pkCipher = Cipher.getInstance("RSA");
			pkCipher.init(Cipher.DECRYPT_MODE, rsaKP.getPrivate());
			
			/**
			 * Receiving the user's secret key here !!
			 */
			//Receiving the secret key of the user encrypted with server's public key.
			byte [] user_secret_key = new byte[128];
			count = bytesFromClient.read(user_secret_key);
			
			// Decrypting the user's secret key with the public key cipher.
			sk_bytes = pkCipher.doFinal(user_secret_key,0 ,count);
			
			// creating the database values.
			user_info.add(1, sk_bytes); // storing the user secret key.
			// storing the user password encrypted with user's public 
			// key and server's public key.
			user_info.add(2, doubly_encrypted_pass); 
		}
		
		// Handling the Second factor.
		if(char_code[1] == '1'){
			// If the users does not utilize the first authentication factor.
			if(char_code[0] != '1'){
				// Creating a public key cipher to decrypt the Secret key of the user.
				Cipher pkCipher = Cipher.getInstance("RSA");
				pkCipher.init(Cipher.DECRYPT_MODE, rsaKP.getPrivate());
				
				/**
				 * Receiving the user's secret key here !!
				 */
				//Receiving the secret key of the user encrypted with server's public key.
				byte [] user_secret_key = new byte[128];
				count = bytesFromClient.read(user_secret_key);
				
				// Decrypting the user's secret key with the public key cipher.
				sk_bytes = pkCipher.doFinal(user_secret_key,0 ,count);
				user_info.add(1,sk_bytes);
				user_info.add(2,null);
			}
			
			System.out.println("Generating credentials for Second Authentication");
			
			GoogleAuthenticator gAuth = new GoogleAuthenticator();
//			GoogleAuthenticatorKey key = gAuth.createCredentials();
			String user_key = "Q4G55MQKMTN7H6EK";
			
			// storing the user key.
			user_info.add(3,user_key.getBytes());
			
			byte [] encrypted_user_key = AES_Encryptor(sk_bytes, user_key.getBytes());
			client.getOutputStream().write(encrypted_user_key);
			
			System.out.println("Credentials sent !!");
		}
		
		// Handling the Third factor.
		if(char_code[2] == '1'){
			// TODO
		}
		
		// Finally, Inserting the user record in the database.
		DB.insert_new_user(this.username, user_info);
		System.out.println("Registeration Complete !!");
		return;
	}
	
	public static byte [] AES_Encryptor(byte [] sk_bytes, byte [] data) throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException{
		SecretKey sk = new SecretKeySpec(sk_bytes, "AES");
		
		Cipher skCipher = Cipher.getInstance("AES");
		skCipher.init(Cipher.ENCRYPT_MODE, sk);
		
		byte [] encrypted_data = skCipher.doFinal(data);
		return encrypted_data;
	}
	
	/*
	 * The following function takes a log-in request of the user and checks
	 * the user input with the stored information.
	 */
	private void logging_in(byte[] decrypted_data) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException {
		System.out.println("Logging the user into the server.");
		// Getting the user details.
		ArrayList<byte []> user_info = DB.get_details(this.username);
		
		// Initiating and encrypting the user code with user secret key.
		// TODO Later would need to decrypt it with private key also.
		Cipher skCipher = Cipher.getInstance("AES");
		SecretKey sk = new SecretKeySpec(user_info.get(1), "AES");
		skCipher.init(Cipher.ENCRYPT_MODE, sk);
		byte [] encrypted_code = skCipher.doFinal(user_info.get(0));
		
		// Sending the encrypted code to the user.
		client.getOutputStream().write(encrypted_code);
		
		char [] char_code = (new String(user_info.get(0))).toCharArray();
		
		// Checking the first authentication factor.
		if(char_code[0] == '1'){
			byte [] doubly_encrypted_pass = new byte[128];
			int count = bytesFromClient.read(doubly_encrypted_pass);
			
			// Initializing a RSA cipher to decrypt the user input and
			// previously stored user info.
			Cipher pkCipher = Cipher.getInstance("RSA");
			pkCipher.init(Cipher.DECRYPT_MODE, rsaKP.getPrivate());
			byte [] sk_encrypted_pass = pkCipher.doFinal(doubly_encrypted_pass, 0, count);
			byte [] sk_stored_encrypted_pass = pkCipher.doFinal(user_info.get(2));
			
			// Constructing the user secret key from the bytes stored in the database.
			SecretKey user_key = new SecretKeySpec(user_info.get(1),"AES");
			
			// Initializing a AES cipher to decrypt user input and 
			// previously stored password.
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, user_key);
			byte [] decrypted_pass = cipher.doFinal(sk_encrypted_pass);
			byte [] decrypted_stored_pass = cipher.doFinal(sk_stored_encrypted_pass);
			
			// Changing the user input and stored password to string
			String str_user_input_pass = new String(decrypted_pass);
			String str_user_stored_pass = new String(decrypted_stored_pass);
			
			// Checking for equality.
			if(!str_user_input_pass.equals(str_user_stored_pass)){
				System.out.println("Authentication failure !!");
				return;
			}
		}
		
		// Checking the Second Authentication Factor
		if(char_code[1] == '1'){
			System.out.println("Checking the OTP code");
			byte [] data = new byte[128];
			client.getInputStream().read(data);
			
			byte [] decrypted_code = decrypt(data);
			ByteBuffer wrapped = ByteBuffer.wrap(decrypted_code);
			int input_code = wrapped.getInt();
			System.out.println(input_code);
			
			byte [] user_key = user_info.get(3);
			
			GoogleAuthenticator gAuth = new GoogleAuthenticator();
			boolean isTrue = gAuth.authorize(new String(user_key), input_code);
			if(!isTrue){
				System.out.println("Authentication Failure");
				return;
			}
		}
		
		//Checking the Third Authentication Factor
		if(char_code[2] == '1'){
			//TODO
		}
		System.out.println("Authentication Successful");
	}

	// Handles the initial client request.
	// Takes in the un-encrypted data bytes as the input.
	private char request_delimeter = ':';
	/*
	 * Returns the status to the user request.
	 * 1000 : Proceed with Login.
	 * 1100 : Cannot proceed with login / not a registered user.
	 * 0010 : Proceed with Registration.
	 * 0011 : Cannot proceed with registration / already a registered user.
	 * 1111 : Incorrect option / neither registration is valid nor login is valid.
	 */
	private String handle_client_request(byte [] data){
		System.out.println("handling the client request");
		
		// Getting the user_data in string form and separating username with the request/opt
		String user_data = new String(data);
		int index = user_data.lastIndexOf(request_delimeter);
		int opt = 0;
		try{
			opt = Integer.parseInt(user_data.substring(index + 1));	
		}catch(Exception E){
			E.printStackTrace();
		}
		String username = user_data.substring(0, index);
		this.username = username;
		String status = "1111";
		switch(opt){
			case (1): // Handling login request
				status = "1100";
				if(get_status(username, opt)){
					status = "1000";
				}
				break;
			case(2): // Handling registration request
				status = "0011";
				if(get_status(username, opt)){
					status = "0010";
				}
				break;
			case(0):
				break;
			default: break;
		}
		return status;
	}
	
	/*
	 * Checks whether the user is making a valid request or not.
	 */
	private boolean get_status(String username, int opt) {
		if(!DB.is_registered_user(username) && opt == 2 || DB.is_registered_user(username) && opt == 1 )
			return true;
		else return false;
	}

	/*
	 * The following function uses the private key and using RSA algorithm to decrypt 
	 * the data.
	 */
	private byte [] decrypt(byte [] data) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
		System.out.println("Decrypting data");

		// Creating a RSA cipher to decrypt the user input.
		PrivateKey private_key = rsaKP.getPrivate();
		Cipher pkCipher = Cipher.getInstance("RSA");
		pkCipher.init(Cipher.DECRYPT_MODE, private_key);
		byte[] decrypted_data = pkCipher.doFinal(data);
		return decrypted_data;
	}
}

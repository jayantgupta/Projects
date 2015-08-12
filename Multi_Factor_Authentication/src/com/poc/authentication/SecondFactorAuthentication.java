/**
* Author : Jayant Gupta
 * Date : November 20, 2014
 * The following class implements the Google Authenticator
 * and would be used by SecureClient and SecureServer as 
 * a second factor to authenticate the user.
 */
package com.poc.authentication;

import java.nio.ByteBuffer;
import java.util.BitSet;
import java.util.Date;

import org.apache.commons.codec.binary.Base32;

import main.java.com.warrenstrange.googleauth.GoogleAuthenticator;
import main.java.com.warrenstrange.googleauth.GoogleAuthenticatorKey;

public class SecondFactorAuthentication {
	
	/*
	 * Main function to test the correctness of this class.
	 */
	public static void main(String [] args){
		// Initializing the GoogleAuthenticator Code.
		GoogleAuthenticator gAuth = new GoogleAuthenticator();
		
		// Creating the secret key to be used with the Authenticator.
		final GoogleAuthenticatorKey key = gAuth.createCredentials();
		System.out.println(key.getKey());
		
		// Initializing a Base32 object.
		Base32 codec = new Base32();
		
		// decoding the key.
	    byte[] decodedKey = codec.decode(key.getKey());
	    
	    // Getting the time of the system.
	    long tm = new Date().getTime() / 30000;
	    System.out.println(new Date().getTime());
	    System.out.println(tm);
//	    
//	    for(int i = -5 ; i <= 5 ; i++){
//	    	System.out.printf("%d %d\n", i, gAuth.calculateCode(decodedKey, tm + i));
//	    }
//	    System.out.println(tm);
//	    int code = gAuth.calculateCode(decodedKey, tm);
//	    System.out.println("Newly Generated Code : " + code);
//	    code = gAuth.calculateCode(decodedKey, 0);
//	    System.out.println("Epoch Generated Code : " + code);
//	    System.out.println("Earlier Stored Code : " + key.getVerificationCode());
//	    System.out.println(gAuth.checkCode(key.getKey(), code, 0, 5));
	}
}
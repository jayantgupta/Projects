/*
	Author : Jayant Gupta
	Helpful ref. : http://stackoverflow.com/questions/20796042/
	This module mangaes the key.
	1. It can generate the new key replacing older key [TO BE USED CAREFULLY].
	2. Reads the system key and sends it to the desired utility.
*/
package auth;

import javax.crypto.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

public class KeyManager{

	// Generates a new key.
	// Needs to preserve older key, or all the
	// user keys needs to be decrypted and encrypted 
	// again.

	private final String keyPath = "/home/jayant/Desktop/systemKey.txt";
	private final File keyFile = new File("/home/jayant/Desktop/systemKey.txt");

	private void generateKey()throws Exception{
		KeyGenerator KeyGen = KeyGenerator.getInstance("AES");
		KeyGen.init(128);

		SecretKey SecKey = KeyGen.generateKey();
		byte [] keyBytes = SecKey.getEncoded();
		saveKey(keyBytes);
	}

	private void saveKey(byte [] keyBytes)throws Exception{
		if(!keyFile.exists()){
			keyFile.createNewFile();
			FileOutputStream fos = new FileOutputStream(keyFile.getAbsoluteFile());
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			bos.write(keyBytes);
			bos.close();
			System.out.println("File Successfully Written");
		}
		else{
			System.out.println("The file already exists. Currently Creating of file allowed only once, i.e. the origin of this function.");
		}
	}
	
	/*
	   Reads the file to get the system key.
	*/
	protected byte[] getSystemKey()throws Exception{
		Path path = Paths.get(keyPath);
		byte[] keyBytes = Files.readAllBytes(path);
		System.out.println("Successfully read the system key");
		return keyBytes;
	}

	public static void main(String [] args){
		KeyManager KM = new KeyManager();
		try{
			KM.generateKey();
			KM.getSystemKey();
		}catch(Exception E){
			E.printStackTrace();
		}
	}
}

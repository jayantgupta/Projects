package com.poc.authentication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class FingerprintAuthentication {
	public static void main(String args[]) throws IOException {
		Process pb = Runtime.getRuntime().exec("cd /home/jayant/Desktop/fingerprint ; ls");
		String line;
		BufferedReader input = new BufferedReader(new InputStreamReader(pb.getInputStream()));
		while((line = input.readLine())!= null){
			System.out.println(line);
		}
	}
} 
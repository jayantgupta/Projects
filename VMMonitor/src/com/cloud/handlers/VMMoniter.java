package com.cloud.handlers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.Scanner;

public class VMMoniter {
	public static void main (String [] args) throws InterruptedException, IOException{
		//check host OS
		//Properties p=System.getProperties();
		//String OS=p.getProperty("os.name");
		//////System.out.println(OS);
		//checking how many vms and vm info

		//check guest OS
		String G = guestos();
		System.out.println(G);
	}

	//check guest os
	public static String guestos()throws IOException, InterruptedException{

		Scanner s= new Scanner(System.in);
		System.out.println("please enter the VM user_name you're checking on: ");
		String user_name=s.nextLine();
		File a=new File("/home/jayant/Desktop/b1.bat");
		System.out.println(a.createNewFile());
		FileWriter fw;
		a.setExecutable(true);
		String GOS = "A";
		try {
			fw=new FileWriter(a);
			fw.write("vboxmanage showvminfo " + user_name);
			fw.close();
			System.out.println("here");
			Process p = Runtime.getRuntime().exec("/home/jayant/Desktop/b1.bat");
			System.out.println("here");
			p.waitFor();
//			a.delete();
			BufferedReader buf=new BufferedReader(new InputStreamReader(p.getInputStream()));
			String fetch=buf.readLine();
			//boolean namecheck=user_name��contains("Win")||user_name��contains("win")||user_name��contains("Ubu")||user_name��contains("ubu");
			while(fetch != null){
				if( fetch.contains("Win")) {
					GOS="Win";}
				if( fetch.contains("Ubu")) {
					GOS="Ubu";}
				fetch = buf.readLine();
			}
		}catch(IOException e){
			System.out.println("Error!");
			e.printStackTrace();
		}
		return GOS;
	}
}

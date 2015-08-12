package com.cloud.handlers;

/*
 * Author : Jayant Gupta
 * Tests the working of the resource monitor.
 */

public class Test_Resource_Monitor {
	public static void main(String [] args) throws InterruptedException{
		Lin_Lin_Resource_Monitor LLRM = new Lin_Lin_Resource_Monitor();
		while(true){
			LLRM.init("VM1", "vm1-jayant", "a");
			// Getting Values.
			int a = LLRM.Processor_Usage("VM1");
			int b = LLRM.RAM_Usage("VM1");
			int [] c = LLRM.Network_Usage("VM1");
			String d =  LLRM.Process_List("VM1");
			//Printing values. Put Xiao's module here.
			System.out.println(a);
			System.out.println(b);
			System.out.println(c[0]);
			System.out.println(c[1]);
			System.out.println(d);
			//Go to Sleep.
			Thread.sleep(1000); //1s
		}
	}	
}
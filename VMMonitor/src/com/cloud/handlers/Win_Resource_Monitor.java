package com.cloud.handlers;

/*
 * @Author : Jayant Gupta
 * June 4, 2015
 * Monitors Windows resource usage.
 */
import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.commons.lang3.StringUtils;

public class Win_Resource_Monitor implements VMResourceMonitor {
	private String totalMemory_command;
	private String freeMemory_command;
	private String userProcessor_command;
	private String kernelProcessor_command;
	private String RxNetwork_command;
	private String TxNetwork_command;
	private String processlist_command;

	public void init(String VM_ID, String user_name, String password){
		this.totalMemory_command = "VBoxManage metrics query " + VM_ID + " Guest/RAM/Usage/Total";
		this.freeMemory_command = "VBoxManage metrics query " + VM_ID + " Guest/RAM/Usage/Free";
		this.userProcessor_command = "VBoxManage metrics query " + VM_ID + " Guest/CPU/Load/User";
		this.kernelProcessor_command = "VBoxManage metrics query " + VM_ID + " Guest/CPU/Load/Kernel";
		this.RxNetwork_command = "VBoxManage metrics query " + VM_ID + " Net/Rate/Rx";
		this.TxNetwork_command = "VBoxManage metrics query " + VM_ID + " Net/Rate/Tx";
		this.processlist_command = "VBoxManage guestcontrol " + VM_ID + " exec --image C:\\\\Windows\\System32\\tasklist.exe " +
				"--username " + user_name + " --password " + password + " --wait-exit --wait-stdout /FO CSV"; 
	}

	//	Object     Metric               Values
	//	---------- -------------------- --------------------------------------------
	//	Win_VM1    Guest/RAM/Usage/Free 1371184 kB

	@Override
	public int RAM_Usage(String VM_ID) {
		String totalData = executeCommand(totalMemory_command).toString();
		totalData = parseValue(totalData);
		String freeData = executeCommand(freeMemory_command).toString();
		freeData = parseValue(freeData);
		int tD = Integer.parseInt(totalData);
		int fD = Integer.parseInt(freeData);
		return (tD - fD)*100/fD;
	}

	//	Object     Metric               Values
	//	---------- -------------------- --------------------------------------------
	//	Win_VM1    Guest/CPU/Load/Kernel 10.00%
	@Override
	public int Processor_Usage(String VM_ID) {
		String userData = executeCommand(userProcessor_command).toString();
		userData = parseValue(userData);
		String kernelData = executeCommand(kernelProcessor_command).toString();
		kernelData = parseValue(kernelData);
		int uD = Math.round(Float.parseFloat(userData.replace("%","")));
		int kD = Math.round(Float.parseFloat(kernelData.replace("%","")));
		return uD + kD;
	}

	//	Object     Metric               Values
	//	---------- -------------------- --------------------------------------------
	//	Win_VM1    Net/Rate/Rx          0 B/s
	@Override
	public int[] Network_Usage(String VM_ID) {
		String rx_data = executeCommand(RxNetwork_command).toString();
		rx_data = parseValue(rx_data);
		String tx_data = executeCommand(TxNetwork_command).toString();
		tx_data = parseValue(tx_data);
		int rx = Integer.parseInt(rx_data)/(1000*20);
		int tx = Integer.parseInt(tx_data)/(1000*20);
		int [] stats = {rx, tx};
		return stats;
	}

	//	"csrss.exe","328","Services","0","2,768 K"
	//	"wininit.exe","376","Services","0","3,120 K""csrss.exe","384","Console","1","6,948 K"
	@Override
	public String Process_List(String VM_ID) {
		String process_data = executeCommand(processlist_command).toString();
		String [] p_list = process_data.split("\n");
		String output = "";
		for(int i = 0 ; i < p_list.length ; i++){
			String p = p_list[i];
			if(p.contains("\"\"")){
				String p1 = p.split("\"\"")[0].replaceAll("\"", "");
				output += smartPrint(p1, i);
				String p2 = p.split("\"\"")[1].replaceAll("\"", "");
				output += smartPrint(p2, i);
			}
			else{
				p = p.replaceAll("\"", "");
				output += smartPrint(p, i);
			}
		}
		return output;
	}
	//26 8 16 10 12
	private String smartPrint(String data, int index){
		String line = data;
		int [] space = {50, 9, 17, 11, 13};
		try{
			String [] vals = data.split(",",5);
			line="";
			for(int i = 0 ; i < 5 ; i++){
				int extra = space[i] - vals[i].length();
				line += vals[i] + StringUtils.repeat(' ', extra);
			}
		}catch(Exception e){
			line = data;
			//do nothing.
		}
		if(index == 0){
			line += "\n" + StringUtils.repeat('=', 100);
		}
		return line  + "\n";
	}

	@Override
	public boolean killProcess(String VM_ID, int Process_ID) {
		return false;
	}

	private String parseValue(String data){
		if(data==null)return null;
		try{
			String val = data.split("\n")[2].replaceAll("\\s+"," ").split(" ")[2];			
			return val;
		}catch(Exception E){
			return null;
		}
	}

	public StringBuffer executeCommand(String command){
		StringBuffer output = new StringBuffer();
		Process p;
		try{
			p = Runtime.getRuntime().exec(command);
			p.waitFor();
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = "";
			while((line = reader.readLine())!=null){
				output.append(line + '\n');
			}
		}catch(Exception E){
			E.printStackTrace();
		}
		return output;
	}

	public static void main(String [] args){
		Win_Resource_Monitor VMRM = new Win_Resource_Monitor();
		while(true){
			VMRM.init("Win_VM1", "jayant", "a");
			System.out.println(VMRM.RAM_Usage("Win_VM1"));
			System.out.println(VMRM.Processor_Usage("Win_VM1"));
			System.out.println(VMRM.Network_Usage("Win_VM1")[0]);
			System.out.println(VMRM.Process_List("Win_VM1"));
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
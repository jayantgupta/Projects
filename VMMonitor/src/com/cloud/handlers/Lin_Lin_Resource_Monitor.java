package com.cloud.handlers;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/*
 * @Author: Jayant Gupta
 * Date : May 30, 2015
 *
 * Linux-Linux VM Monitor: Monitors the resource statistics on the Virtual Machine.
 */

public class Lin_Lin_Resource_Monitor implements VMResourceMonitor{
	private String memory_command;
	private String processor_command;
	private String network_command;
	private String processlist_command;

	// Processor global variables.
	private int PREV_IDLE = 0;
	private int PREV_TOTAL = 0;

	//Network global variables.
	private long PREV_RECEIVE = 0;
	private long PREV_SENT = 0;
	// Execute all the commands to get the data
	// from the Virtual Machine.
	public void init(String VM_ID, String user_name, String password){
		this.memory_command = "VBoxManage guestcontrol " + VM_ID + " exec --image /usr/bin/free " +
				"--username " + user_name + " --password " + password + " --wait-exit " +
				"--wait-stdout -- -m";
		this.processor_command = "VBoxManage guestcontrol " + VM_ID + " exec --image /bin/cat " +
				"--username " + user_name + " --password " + password + " --wait-exit --wait-stdout -- " +
				"/proc/stat | grep '^cpu'"; 
		this.network_command = "VBoxManage guestcontrol " + VM_ID + " exec --image /bin/cat --username " +
				user_name + " --password " + password + " --wait-exit --wait-stdout -- /proc/net/dev";
		this.processlist_command = "VBoxManage guestcontrol " + VM_ID + " exec --image /bin/ps " +
				"--username " + user_name + " --password " + password + " --wait-exit --wait-stdout -- " + 
				"-u " + user_name + " -opid,pcpu,size,user,cmd"; 
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

	@Override
	public int RAM_Usage(String VM_ID) {
		String memoryData = executeCommand(memory_command).toString();
		String [] lines = memoryData.split("\n");
		String to_use = lines[1].replaceAll("\\s+", " ");
		String []vals = to_use.split(" ");
		int a = Integer.parseInt(vals[1]);
		int b = Integer.parseInt(vals[2]);
		return (b*100) / a;
	}

	@Override
	public int Processor_Usage(String VM_ID) {
		String processorData = executeCommand(processor_command).toString();
		String [] lines = processorData.split("\n");
		String to_use = lines[1];
		String [] tokens = to_use.split(" ");
		int IDLE = Integer.parseInt(tokens[3]);
		int TOTAL = Integer.parseInt(tokens[1]) + Integer.parseInt(tokens[2]) + Integer.parseInt(tokens[3]);
		int DIFF_IDLE = IDLE - PREV_IDLE;
		int DIFF_TOTAL = TOTAL - PREV_TOTAL;
		int usage = (100*(DIFF_TOTAL - DIFF_IDLE) / (DIFF_TOTAL+5)); // 5 added to avoid 0 value case;
		PREV_IDLE = IDLE;
		PREV_TOTAL = TOTAL;
		return usage;
	}

	@Override
	public int [] Network_Usage(String VM_ID){
		System.out.println("Inside network usage");
		String networkData = executeCommand(network_command).toString();
		String [] lines = networkData.split("\n");
		long RECEIVE = 0;
		long SENT = 0;
		for(int  i=2;i<lines.length;i++){
			String net_data = lines[i].trim().replaceAll("\\s+", " ");
			String [] vals = net_data.split(" ");
			RECEIVE += Long.parseLong(vals[1]);
			SENT += Long.parseLong(vals[9]);
		}
		int stats [] = new int[2];
		stats[0] = (int)(RECEIVE -  PREV_RECEIVE)/(1000*20); // 1MB/s = 50
		stats[1] = (int)(SENT  - PREV_SENT)/(1000*20); // 1MB/s = 50
		PREV_RECEIVE = RECEIVE;
		PREV_SENT = SENT;
		System.out.println("prev_sent:" + String.valueOf(PREV_RECEIVE) + "prev_recv" +  String.valueOf(PREV_SENT) + "sent" + String.valueOf(SENT) + "recv" + String.valueOf(RECEIVE));
		return stats;
	}

	@Override
	public String Process_List(String VM_ID) {
		String processList = executeCommand(processlist_command).toString(); //TODO
		return processList;
	}

	@Override
	public boolean killProcess(String VM_ID, int Process_ID) {
		return false;
	}
}
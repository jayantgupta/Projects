package com.cloud.GUI;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.jfree.ui.RefineryUtilities;

public class VMRM {
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

	private String win_user = "jayant";
	private String win_pass = "a";

	private String ubuntu_user1 = "vm1-jayant";
	private String ubuntu_pass1 = "a";

	private String ubuntu_user2 = "jayant";
	private String ubuntu_pass2 = "a";

	public void init(){
		String listVm_command = "VBoxManage list runningvms";
		String running_vms = this.executeCommand(listVm_command).toString();
		String [] lines = running_vms.split("\n");
		for(String line : lines){
			String vm_id = line.replaceAll("\"", "").split(" ")[0];
			String vminfo_command = "VBoxManage showvminfo " + vm_id;
			String info = executeCommand(vminfo_command).toString();
			String OS_info = info.split("\n")[2];
			System.out.println(OS_info);
			if(OS_info.contains("Windows")){
				final VMsMonitor demo = new VMsMonitor("Windows-7", vm_id, win_user, win_pass, 1);
				demo.pack();
				RefineryUtilities.centerFrameOnScreen(demo);
				demo.setVisible(true);
			}
			else if(OS_info.contains("Ubuntu") && vm_id.equals("VM1")){
				final VMsMonitor demo = new VMsMonitor("VM1 (Ubuntu 14.04)", vm_id, ubuntu_user1, ubuntu_pass1, 0);
				demo.pack();
				RefineryUtilities.centerFrameOnScreen(demo);
				demo.setVisible(true);
			}
			else if(OS_info.contains("Ubuntu") && vm_id.equals("VM2")){
				final VMsMonitor demo = new VMsMonitor("VM2 (Ubuntu 14.04)", vm_id, ubuntu_user2, ubuntu_pass2, 0);
				demo.pack();
				RefineryUtilities.centerFrameOnScreen(demo);
				demo.setVisible(true);
			}
		}
	}

	public static void main(String [] args){
		VMRM vmmm = new VMRM();
		vmmm.init();
	}
}
package com.cloud.handlers;

public interface VMResourceMonitor {
	// Function that monitors RAM usage
	public int RAM_Usage(String VM_ID);

	// Function that monitors Processor usage
	public int Processor_Usage(String VM_ID);

	// Function that monitors Network usage
	public int [] Network_Usage(String VM_ID);

	// Function that monitors running Processes
	public String Process_List(String VM_ID);

	//Optional: Function to kill a process
	public boolean killProcess(String VM_ID, int Process_ID);
	/*{
		if Success return true;
		else return false;
	}*/
}
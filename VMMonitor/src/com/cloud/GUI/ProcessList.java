package com.cloud.GUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.Timer;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.Millisecond;

import com.cloud.handlers.Lin_Lin_Resource_Monitor;
import com.cloud.handlers.VMResourceMonitor;
import com.cloud.handlers.Win_Resource_Monitor;

/**
 * @author Xiao
 * This class shows the Process list panel box, showing the Process List
 */

public class ProcessList implements ActionListener{
	private final TimeSeries series;
	private final javax.swing.JTextPane Pane;
	private final javax.swing.JScrollPane jScrollPane1;
	private final Timer timer = new Timer(250, this);
	private String Content;
	private VMResourceMonitor LLRM;
	
	ProcessList(String Title, VMResourceMonitor LLRM, int flag){
		this.series = new TimeSeries("Random Data", Millisecond.class);
		if(flag == 0){
			this.LLRM = (Lin_Lin_Resource_Monitor)LLRM;
		}
		else{
			this.LLRM = (Win_Resource_Monitor)LLRM;
		}
		Pane=new JTextPane();              
		jScrollPane1= new JScrollPane();         
		timer.setInitialDelay(1000);
		timer.start();
	}   
	javax.swing.JScrollPane getPane(){
		return jScrollPane1;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		this.Content = this.LLRM.Process_List("VM1");
		Pane.setText(this.Content);
		jScrollPane1.setViewportView(Pane);
	}
}
package com.cloud.GUI;

import org.jfree.chart.ChartPanel;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import com.cloud.handlers.Lin_Lin_Resource_Monitor;
import com.cloud.handlers.Win_Resource_Monitor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
/**
 * The Main window of the Process Monitor.
 * @author Xiao.
 */
public class VMsMonitor extends ApplicationFrame {

	private static final long serialVersionUID = 1L;
	/** The most recent value added. */
	/** Timer to refresh graph after every 1/4th of a second */
	/**
	 * Constructs a new dynamic chart application.
	 * @param title  the frame title.
	 */              
	private final javax.swing.JLabel jLabel1;
	private final javax.swing.JPanel jPanel1;

	@SuppressWarnings("deprecation")
	public VMsMonitor(String title, String VM_ID, String user_name, String user_pass, int flag){
		super(title);
		
		DynamicLineAndTimeSeriesChart cpu_values;
		DynamicLineAndTimeSeriesChart network_in;
		DynamicLineAndTimeSeriesChart network_out;
		DynamicLineAndTimeSeriesChart ram_values;
		ProcessList TextPane;
		
		if(flag == 0){
			Lin_Lin_Resource_Monitor LLRM = new Lin_Lin_Resource_Monitor();
			LLRM.init(VM_ID, user_name, user_pass);
		
			cpu_values = new DynamicLineAndTimeSeriesChart("|Processor|", "Processor Usage in %", LLRM, flag);
			network_in = new DynamicLineAndTimeSeriesChart("|Network (Incoming)|", "Network Usage. (scale=20KB/s)", LLRM, flag);
			network_out = new DynamicLineAndTimeSeriesChart("|Network (Outgoing)|", "Network Usage. (scale=20KB/s)", LLRM, flag);
			ram_values = new DynamicLineAndTimeSeriesChart("|RAM|", " RAM Usage (Scale= 20MB/%)", LLRM, flag);
			TextPane=new ProcessList("Processes", LLRM, flag);
		}
		else{
			Win_Resource_Monitor WRM = new Win_Resource_Monitor();
			WRM.init(VM_ID, user_name, user_pass);
		
			cpu_values = new DynamicLineAndTimeSeriesChart("|Processor|", "Processor Usage in %", WRM, flag);
			network_in = new DynamicLineAndTimeSeriesChart("|Network (Incoming)|", "Network Usage. (scale=20KB/s)", WRM, flag);
			network_out = new DynamicLineAndTimeSeriesChart("|Network (Outgoing)|", "Network Usage. (scale=20KB/s)", WRM, flag);
			ram_values = new DynamicLineAndTimeSeriesChart("|RAM|", " RAM Usage (Scale= 20MB/%)", WRM, flag);
			TextPane=new ProcessList("Processes", WRM, flag);
		}
		
		//Sets background color of chart
		//Created to show graph on screen
		//Created Chart-panel for chart area
		final ChartPanel chartPanel1 = new ChartPanel(cpu_values.getChart());
		final ChartPanel chartPanel2 = new ChartPanel(network_in.getChart());
		final ChartPanel chartPanel3 = new ChartPanel(network_out.getChart());
		final ChartPanel chartPanel4 = new ChartPanel(ram_values.getChart());

		jPanel1 = new javax.swing.JPanel();
		jLabel1 = new javax.swing.JLabel();

		jPanel1.setLayout(new java.awt.GridLayout(1, 0));

		jLabel1.setText("Processes");

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
				.addComponent(TextPane.getPane())
				);
		layout.setVerticalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
						.addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(jLabel1)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(TextPane.getPane(), javax.swing.GroupLayout.DEFAULT_SIZE, 274, Short.MAX_VALUE))
				);

		//Added chartpanel to main panel
		jPanel1.add(chartPanel1);
		jPanel1.add(chartPanel2);
		jPanel1.add(chartPanel3);
		jPanel1.add(chartPanel4);
		//Sets the size of whole window (JPanel)
		chartPanel1.setPreferredSize(new java.awt.Dimension(400, 375));
		chartPanel2.setPreferredSize(new java.awt.Dimension(400, 375));       
		chartPanel3.setPreferredSize(new java.awt.Dimension(400, 375)); 
		chartPanel4.setPreferredSize(new java.awt.Dimension(400, 375));
		//Puts the whole content on a Frame
	}

	/**
	 * Starting point for the dynamic graph application.
	 * @param args  ignored.
	 **/
	// flag = 0 Ubuntu
	// flag = 1 Windows
	public static void main(String[] args) {
		final VMsMonitor demo = new VMsMonitor("VM1 (Ubuntu 14.04)" , "VM1", "vm1-jayant", "a", 0);
		demo.pack();
		RefineryUtilities.centerFrameOnScreen(demo);
		demo.setVisible(true);
	}
}   
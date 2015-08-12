package com.cloud.GUI;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

import com.cloud.handlers.Lin_Lin_Resource_Monitor;
import com.cloud.handlers.VMResourceMonitor;
import com.cloud.handlers.Win_Resource_Monitor;
/**
 * @author Xiao
 * This class generates line charts, the data is generated in the class
 * actionPerformed. 
 */
public class DynamicLineAndTimeSeriesChart implements ActionListener {
	private final TimeSeries series;
	private double lastValue = 50.0;
	JFreeChart chart;
	private final Timer timer = new Timer(250, this);
	private final String chartTitle;
	private final String scale;
	
	private VMResourceMonitor LLRM;
	
	DynamicLineAndTimeSeriesChart(String title, String scale, VMResourceMonitor LLRM, int flag) { 
		this.chartTitle=title;
		this.series = new TimeSeries( scale, Millisecond.class);
		if(flag == 0){
			this.LLRM = (Lin_Lin_Resource_Monitor)LLRM;
		}
		else{
			this.LLRM = (Win_Resource_Monitor)LLRM;
		}
		 
		
		TimeSeriesCollection dataset = new TimeSeriesCollection(this.series);
		timer.setInitialDelay(1000);
		chart = createChart(dataset,this.chartTitle);
		this.scale = scale;
		timer.start();
	}
	public JFreeChart getChart(){
		return this.chart;
	}

	private JFreeChart createChart(final XYDataset dataset, String title) {
		final JFreeChart result = ChartFactory.createTimeSeriesChart(
				title,
				"Time",
				"Value",
				dataset,
				true,
				true,
				false
				);

		final XYPlot plot = result.getXYPlot();

		plot.setBackgroundPaint(new Color(0xffffe0));
		plot.setDomainGridlinesVisible(true);
		plot.setDomainGridlinePaint(Color.lightGray);
		plot.setRangeGridlinesVisible(true);
		plot.setRangeGridlinePaint(Color.lightGray);

		ValueAxis xaxis = plot.getDomainAxis();
		xaxis.setAutoRange(true);

		//Domain axis would show data of 60 seconds for a time
		xaxis.setFixedAutoRange(60000.0);  // 60 seconds
		xaxis.setVerticalTickLabels(true);

		ValueAxis yaxis = plot.getRangeAxis();
		yaxis.setRange(0.0, 100.0);

		return result;
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		int [] vals= {0,0};
		switch(this.chartTitle){
			case "|Processor|":
				this.lastValue = this.LLRM.Processor_Usage("VM1");
				break;
			case "|Network (Incoming)|":
				vals = this.LLRM.Network_Usage("VM1");
				this.lastValue = vals[0];
				break;
			case "|Network (Outgoing)|":
				vals = this.LLRM.Network_Usage("VM1");
				this.lastValue = vals[1];
				break;
			case "|RAM|":
				this.lastValue = this.LLRM.RAM_Usage("VM1");
				break;
			default:
				final double factor = 0.9 + 0.2*Math.random();
				this.lastValue = this.lastValue * factor;
				break;
		}
		//final Millisecond now = new Millisecond();
		this.series.add(new Millisecond(), this.lastValue);
		System.out.println( this.chartTitle + " : Current Value : " + this.lastValue);
	}
}
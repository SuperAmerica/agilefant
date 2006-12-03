package fi.hut.soberit.agilefant.web;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.opensymphony.webwork.interceptor.SessionAware;
import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

public class SimpleCounter extends ActionSupport implements SessionAware {

	private static final long serialVersionUID = -5169540628300552058L;
	private static final Logger log = Logger.getLogger(SimpleCounter.class);
	public static final String COUNTER_PARAM = "simpleCounter";
	
	private Map<String, Integer> sessionMap;
	private int counter;
	
	public int getCounter(){
		return counter;
	}
	
	@SuppressWarnings("unchecked")
	public void setSession(Map sessionMap) {
		this.sessionMap = sessionMap;
	}
	
	public String resetCounter(){
		sessionMap.remove(SimpleCounter.COUNTER_PARAM);
		log.info("Counter reseted");
		this.counter = 0;
		return Action.SUCCESS;
	}

	public String execute(){
		increaseCount();
		return Action.SUCCESS;
	}
	
	protected void increaseCount(){
		log.info("Increasing counter");
		Integer count = (Integer)sessionMap.get(SimpleCounter.COUNTER_PARAM);
		if (count == null){
			count = new Integer(0);
		}
		counter = count.intValue();
		counter++;
		count = new Integer(counter);
		sessionMap.put(SimpleCounter.COUNTER_PARAM, count);
		log.info("Counter increased. New value is " + counter); 
	}
	
	public String refreshChart(){
//		 Create a simple XY chart
		XYSeries series = new XYSeries("XYGraph");
		series.add(1, 1);
		series.add(1, 2);
		series.add(2, 1);
		series.add(3, 9);
		series.add(4, 10);
//		 Add the series to your data set
		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(series);
//		 Generate the graph
		JFreeChart chart = ChartFactory.createXYLineChart(
				"XY Chart", // Title
				"x-axis", // x-axis Label
				"y-axis", // y-axis Label
				dataset, // Dataset
				PlotOrientation.VERTICAL, // Plot Orientation
				true, // Show Legend
				true, // Use tooltips
				false // Configure chart to generate URLs?
		);
		try {
			ChartUtilities.saveChartAsPNG(new File("./chart.png"), chart, 500, 300);
		} catch (IOException e) {
			System.err.println("Problem occurred creating chart.");
		}
		return Action.SUCCESS;
	}
}

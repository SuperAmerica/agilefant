package fi.hut.soberit.agilefant.web;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.*;

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
//		 Create a time series chart
		TimeSeries pop = new TimeSeries("Workhours", Day.class);
		pop.add(new Day(1, 12, 2006), 20);
		pop.add(new Day(2, 12, 2006), 40);
		pop.add(new Day(3, 12, 2006), 30);
		pop.add(new Day(4, 12, 2006), 10);
		pop.add(new Day(5, 12, 2006), 17);
		pop.add(new Day(6, 12, 2006), 15);
		TimeSeriesCollection dataset = new TimeSeriesCollection();
		dataset.addSeries(pop);
		JFreeChart chart1 = ChartFactory.createTimeSeriesChart(
		"Agilefant07 worhours per day",
		"Date",
		"Workhours",
		dataset,
		true,
		true,
		false);
		XYPlot plot = chart1.getXYPlot();
		DateAxis axis = (DateAxis) plot.getDomainAxis();
		axis.setDateFormatOverride(new SimpleDateFormat("yyyy-MM-dd"));	
		try {
			String dir="user.dir"; // set to current directory
			dir=new File(System.getProperty(dir)).getCanonicalPath();
			File b = new File(dir + File.separator + "webapps" + File.separator + "agilefant" + File.separator + "chart2.png");
			ChartUtilities.saveChartAsPNG(b, chart1, 500, 300);
			
		} catch (IOException e) {
			System.err.println("Problem occurred creating chart.");
		}
		return Action.SUCCESS;
	}
}

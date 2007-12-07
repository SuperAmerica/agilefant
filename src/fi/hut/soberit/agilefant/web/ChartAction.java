package fi.hut.soberit.agilefant.web;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.RectangleInsets;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

import fi.hut.soberit.agilefant.business.ChartBusiness;
import fi.hut.soberit.agilefant.db.BacklogItemDAO;
import fi.hut.soberit.agilefant.db.IterationDAO;
import fi.hut.soberit.agilefant.db.ProjectDAO;
import fi.hut.soberit.agilefant.db.TaskDAO;

public class ChartAction extends ActionSupport {

    private static final long serialVersionUID = 1L;

    private byte[] result;

    private int taskId;

    private int backlogItemId;

    private int iterationId;

    private int projectId;

    private TaskDAO taskDAO;

    private BacklogItemDAO backlogItemDAO;

    private IterationDAO iterationDAO;

    private ProjectDAO projectDAO;

    private int workDone;

    private double effortDone;

    private double effortLeft;

    private double notStarted;

    private double started;
    
    private double pending;

    private double blocked;

    private double implemented;

    private double done;

    private Date startDate;

    private Date endDate;

    private Color color1;

    private Color color2;

    private Color color3;

    private Color color4;

    private Color color5;
    
    private Color color6;

    private ChartBusiness chartBusiness;

    /**
     * This method draws the iteration burndown chart.
     */
    public String execute() {
        if (iterationId > 0) {
            result = chartBusiness.getIterationBurndown(iterationId);
        }

        return Action.SUCCESS;
    }

    public String projectChart() {
        if (projectId > 0) {
            result = chartBusiness.getProjectBurndown(projectId);
        }

        return Action.SUCCESS;
    }
    
    public String smallChart() {
        if (iterationId > 0) {
            result = chartBusiness.getSmallIterationBurndown(iterationId);
        }
        return Action.SUCCESS;
    }
    
    public String smallProjectChart() {
        if (projectId > 0) {
            result = chartBusiness.getSmallProjectBurndown(projectId);
        }
        return Action.SUCCESS;
    }

    /**
     * Bar chart takes two parameters, effort done and effort left. The
     * procentage of work compleated is calculated based on theses two numbers.
     * Finally the bar chart is returned back as a png-image.
     * 
     * @param effortDone
     * @param effortLeft
     * @return
     */
    public String barChart() {

        // Intitializing variables
        double done = 0;
        double left = 0;

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        JFreeChart chart2 = null;

        if (effortDone > 0) {
            done = effortDone;
        }

        if (effortLeft > 0) {
            left = effortLeft;
        }

        // We want to avoid division by zero
        if (done > 0 || left > 0) { // Two parameters chart
            double donePros = ((double) done / (double) (done + left)) * 100;
            dataset.setValue(donePros, "done", "");
            dataset.setValue((100 - donePros), "left", "");
            double stringPros = Math.round(donePros); // We want the output
            // neat!
            chart2 = ChartFactory.createStackedBarChart("", "", "" + stringPros
                    + " % done", dataset, PlotOrientation.HORIZONTAL, true,
                    true, false);
        } else { // Chart based on one "workDone" parameter
            dataset.setValue(workDone, "done", "");
            dataset.setValue((100 - workDone), "left", "");
            chart2 = ChartFactory.createStackedBarChart("", "", "" + workDone
                    + " % done", dataset, PlotOrientation.HORIZONTAL, true,
                    true, false);
        }

        CategoryPlot plot = chart2.getCategoryPlot();
        CategoryItemRenderer renderer = plot.getRenderer();
        renderer.setSeriesItemLabelsVisible(0, false);
        renderer.setSeriesPaint(0, Color.green);
        renderer.setSeriesPaint(1, Color.red);
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ChartUtilities.writeChartAsPNG(out, chart2, 200, 100);
            result = out.toByteArray();
        } catch (IOException e) {
            System.err.println("Problem occurred creating chart.");
        }
        return Action.SUCCESS;
    }

    /**
     * Extended Bar chart takes five parameters, the amount of work not started,
     * started, blocked, implemented and done. Finally the bar chart is returned
     * back as a png-image.
     * 
     * @return
     */
    public String extendedBarChart() {

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        JFreeChart chart2 = null;

        /*-- We want to show the relative percentages of different work states --*/
        double allTypes = 0;
        allTypes = this.getNotStarted() + this.getStarted() + this.getPending()
                + this.getBlocked() + this.getDone() + this.getImplemented();
        double ns = 100/6;
        double st = 100/6;
        double pe = 100/6;
        double bl = 100/6;
        double im = 100/6;
        double dn = 100/6;
        if (allTypes > 0) {
            ns = (this.getNotStarted() / allTypes) * 100.0;
            st = (this.getStarted() / allTypes) * 100.0;
            pe = (this.getPending() / allTypes) * 100.0;
            bl = (this.getBlocked() / allTypes) * 100.0;
            im = (this.getImplemented() / allTypes) * 100.0;
            dn = (this.getDone() / allTypes) * 100.0;
        }

        dataset.setValue(ns, "Not started", "");
        dataset.setValue(st, "Started", "");
        dataset.setValue(pe, "Pending", "");
        dataset.setValue(bl, "Blocked", "");
        dataset.setValue(im, "Implemented", "");
        dataset.setValue(dn, "Done", "");
        chart2 = ChartFactory.createStackedBarChart(null, null, null, dataset,
                PlotOrientation.HORIZONTAL, false, false, false);

        CategoryPlot plot = chart2.getCategoryPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        // CategoryAxis axis = plot.getDomainAxis();
        ValueAxis axis = plot.getRangeAxis();

        /* -- some efforts to get rid of the outline scale, no success so far -- */
        plot.setDomainGridlinesVisible(false);
        plot.setRangeGridlinesVisible(false);
        plot.setBackgroundPaint(Color.white);

        /* Make background transparent? */
        // plot.setBackgroundAlpha(100);
        chart2.setBorderVisible(false); // Disable chart border
        // Sets the background color for the chart
        chart2.setBackgroundPaint(Color.white);

        /* Make background transparent? */
        // chart2.setBackgroundImageAlpha(100);
        // No padding around chart
        chart2.setPadding(new RectangleInsets(0, 0, 0, 0));

        axis.setAxisLineVisible(false);
        axis.setTickLabelsVisible(false);
        axis.setTickMarksVisible(false);
        renderer.setDrawBarOutline(false);
        renderer.setOutlineStroke(null);
        /*-----------------------------------------*/

        // axis.setCategoryMargin(0.01); // one percent
        if (color1 != null) {
            renderer.setSeriesPaint(0, this.getColor1()); // color for not
            // started
        } else {
            renderer.setSeriesPaint(0, new Color(0xd3, 0xd3, 0xd3)); // color
            // for
            // not
            // started
            // (light
            // gray)
        }

        if (color2 != null) {
            renderer.setSeriesPaint(1, this.getColor2()); // color for started
        } else {
            renderer.setSeriesPaint(1, new Color(0xff, 0x99, 0x0)); // color for
            // started
            // (orange)
        }
        
        if (color3 != null) {
            renderer.setSeriesPaint(2, this.getColor3()); // color for pending
        } else {
            renderer.setSeriesPaint(2, new Color(0x61, 0x89, 0xff)); // color for pending
        }
        
        if (color4 != null) {
            renderer.setSeriesPaint(3, this.getColor3()); // color for blocked
        } else {
            renderer.setSeriesPaint(3, Color.red); // color for blocked
        }

        if (color5 != null) {
            renderer.setSeriesPaint(4, this.getColor4()); // color for
            // implemented
        } else {
            renderer.setSeriesPaint(4, Color.green); // color for implemented
        }

        if (color6 != null) {
            renderer.setSeriesPaint(5, this.getColor5()); // color for done
        } else {
            renderer.setSeriesPaint(5, new Color(0x00, 0x77, 0x00)); // color
            // for
            // done
        }

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ChartUtilities.writeChartAsPNG(out, chart2, 110, // here we set
                    // the width of
                    // the total bar
                    // in pixels
                    15); // here we set the hight of the bar in pixels
            result = out.toByteArray();
        } catch (IOException e) {
            System.err.println("Problem occurred creating chart.");
        }
        return Action.SUCCESS;
    }

    /**
     * Utility method for creating <code>Date</code> objects.
     * 
     * @param day
     *            the date.
     * @param month
     *            the month.
     * @param year
     *            the year.
     * 
     * @return a date.
     */
    @SuppressWarnings("unused")
    private static Date date(final int day, final int month, final int year) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        Date result = calendar.getTime();
        return result;

    }

    /*---------------------------------------------------------------------------*/

    public InputStream getInputStream() {
        return new ByteArrayInputStream(result);
    }

    public BacklogItemDAO getBacklogItemDAO() {
        return backlogItemDAO;
    }

    public void setBacklogItemDAO(BacklogItemDAO backlogItemDAO) {
        this.backlogItemDAO = backlogItemDAO;
    }

    public int getBacklogItemId() {
        return backlogItemId;
    }

    public void setBacklogItemId(int backlogItemId) {
        this.backlogItemId = backlogItemId;
    }

    public ProjectDAO getProjectDAO() {
        return projectDAO;
    }

    public void setProjectDAO(ProjectDAO projectDAO) {
        this.projectDAO = projectDAO;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public IterationDAO getIterationDAO() {
        return iterationDAO;
    }

    public void setIterationDAO(IterationDAO iterationDAO) {
        this.iterationDAO = iterationDAO;
    }

    public int getIterationId() {
        return iterationId;
    }

    public void setIterationId(int iterationId) {
        this.iterationId = iterationId;
    }

    public byte[] getResult() {
        return result;
    }

    public void setResult(byte[] result) {
        this.result = result;
    }

    public TaskDAO getTaskDAO() {
        return taskDAO;
    }

    public void setTaskDAO(TaskDAO taskDAO) {
        this.taskDAO = taskDAO;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public int getWorkDone() {
        return workDone;
    }

    public void setWorkDone(int workDone) {
        this.workDone = workDone;
    }

    public double getEffortDone() {
        return effortDone;
    }

    public void setEffortDone(double effortDone) {
        this.effortDone = effortDone;
    }

    public double getEffortLeft() {
        return effortLeft;
    }

    public void setEffortLeft(double effortLeft) {
        this.effortLeft = effortLeft;
    }

    public double getBlocked() {
        return blocked;
    }

    public void setBlocked(double blocked) {
        this.blocked = blocked;
    }

    public double getDone() {
        return done;
    }

    public void setDone(double done) {
        this.done = done;
    }

    public double getImplemented() {
        return implemented;
    }

    public void setImplemented(double implemented) {
        this.implemented = implemented;
    }

    public double getNotStarted() {
        return notStarted;
    }

    public void setNotStarted(double notStarted) {
        this.notStarted = notStarted;
    }

    public double getStarted() {
        return started;
    }

    public void setStarted(double started) {
        this.started = started;
    }
    
    public double getPending() {
        return pending;
    }

    public void setPending(double pending) {
        this.pending = pending;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Color getColor1() {
        return color1;
    }

    public void setColor1(Color color1) {
        this.color1 = color1;
    }

    public Color getColor2() {
        return color2;
    }

    public void setColor2(Color color2) {
        this.color2 = color2;
    }

    public Color getColor3() {
        return color3;
    }

    public void setColor3(Color color3) {
        this.color3 = color3;
    }

    public Color getColor4() {
        return color4;
    }

    public void setColor4(Color color4) {
        this.color4 = color4;
    }

    public Color getColor5() {
        return color5;
    }

    public void setColor5(Color color5) {
        this.color5 = color5;
    }
    
    public Color getColor6() {
        return color6;
    }

    public void setColor6(Color color6) {
        this.color6 = color6;
    }

    public void setChartBusiness(ChartBusiness chartBusiness) {
        this.chartBusiness = chartBusiness;
    }
}

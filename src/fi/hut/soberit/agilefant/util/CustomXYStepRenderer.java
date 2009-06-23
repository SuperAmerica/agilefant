package fi.hut.soberit.agilefant.util;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.chart.renderer.xy.XYStepRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleEdge;

/**
 * A custom XYStepRenderer that supports separate style for steps on the range
 * axis.
 * 
 * For example, it can be used to style vertical steps as dashed lines
 * 
 * @author jtjavana
 * 
 */
public class CustomXYStepRenderer extends XYStepRenderer {

    private static final long serialVersionUID = -1913038208340040789L;

    private Paint stepPaint;

    private Stroke stepStroke;

    public void setStepPaint(Paint stepPaint) {
        this.stepPaint = stepPaint;
    }

    public void setStepStroke(Stroke stepStroke) {
        this.stepStroke = stepStroke;
    }

    public void drawItem(Graphics2D g2, XYItemRendererState state,
            Rectangle2D dataArea, PlotRenderingInfo info, XYPlot plot,
            ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset,
            int series, int item, CrosshairState crosshairState, int pass) {

        // do nothing if item is not visible
        if (!getItemVisible(series, item)) {
            return;
        }

        PlotOrientation orientation = plot.getOrientation();

        Paint seriesPaint = getItemPaint(series, item);
        Stroke seriesStroke = getItemStroke(series, item);
        g2.setPaint(seriesPaint);
        g2.setStroke(seriesStroke);

        Paint seriesStepPaint = (stepPaint == null) ? seriesPaint : stepPaint;
        Stroke seriesStepStroke = (stepStroke == null) ? seriesStroke
                : stepStroke;

        // get the data point...
        double x1 = dataset.getXValue(series, item);
        double y1 = dataset.getYValue(series, item);

        RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
        RectangleEdge yAxisLocation = plot.getRangeAxisEdge();
        double transX1 = domainAxis.valueToJava2D(x1, dataArea, xAxisLocation);
        double transY1 = (Double.isNaN(y1) ? Double.NaN : rangeAxis
                .valueToJava2D(y1, dataArea, yAxisLocation));

        if (pass == 0 && item > 0) {
            // get the previous data point...
            double x0 = dataset.getXValue(series, item - 1);
            double y0 = dataset.getYValue(series, item - 1);
            double transX0 = domainAxis.valueToJava2D(x0, dataArea,
                    xAxisLocation);
            double transY0 = (Double.isNaN(y0) ? Double.NaN : rangeAxis
                    .valueToJava2D(y0, dataArea, yAxisLocation));

            if (orientation == PlotOrientation.HORIZONTAL) {
                if (transY0 == transY1) {
                    // this represents the situation
                    // for drawing a horizontal bar.
                    drawLine(g2, state.workingLine, transY0, transX0, transY1,
                            transX1);
                } else { // this handles the need to perform a 'step'.

                    // calculate the step point
                    double transXs = transX0
                            + (getStepPoint() * (transX1 - transX0));
                    drawLine(g2, state.workingLine, transY0, transX0, transY0,
                            transXs);
                    g2.setPaint(seriesStepPaint);
                    g2.setStroke(seriesStepStroke);
                    drawLine(g2, state.workingLine, transY0, transXs, transY1,
                            transXs);
                    g2.setPaint(seriesPaint);
                    g2.setStroke(seriesStroke);
                    drawLine(g2, state.workingLine, transY1, transXs, transY1,
                            transX1);
                }
            } else if (orientation == PlotOrientation.VERTICAL) {
                if (transY0 == transY1) { // this represents the situation
                    // for drawing a horizontal bar.
                    drawLine(g2, state.workingLine, transX0, transY0, transX1,
                            transY1);
                } else { // this handles the need to perform a 'step'.
                    // calculate the step point
                    double transXs = transX0
                            + (getStepPoint() * (transX1 - transX0));
                    drawLine(g2, state.workingLine, transX0, transY0, transXs,
                            transY0);
                    g2.setPaint(seriesStepPaint);
                    g2.setStroke(seriesStepStroke);
                    drawLine(g2, state.workingLine, transXs, transY0, transXs,
                            transY1);
                    g2.setPaint(seriesPaint);
                    g2.setStroke(seriesStroke);
                    drawLine(g2, state.workingLine, transXs, transY1, transX1,
                            transY1);
                }
            }

            // submit this data item as a candidate for the crosshair point
            int domainAxisIndex = plot.getDomainAxisIndex(domainAxis);
            int rangeAxisIndex = plot.getRangeAxisIndex(rangeAxis);
            updateCrosshairValues(crosshairState, x1, y1, domainAxisIndex,
                    rangeAxisIndex, transX1, transY1, orientation);

            // collect entity and tool tip information...
            EntityCollection entities = state.getEntityCollection();
            if (entities != null) {
                addEntity(entities, null, dataset, series, item, transX1,
                        transY1);
            }

        }

        if (pass == 1) {
            // draw the item label if there is one...
            if (isItemLabelVisible(series, item)) {
                double xx = transX1;
                double yy = transY1;
                if (orientation == PlotOrientation.HORIZONTAL) {
                    xx = transY1;
                    yy = transX1;
                }
                drawItemLabel(g2, orientation, dataset, series, item, xx, yy,
                        (y1 < 0.0));
            }
        }
    }

    private void drawLine(Graphics2D g2, Line2D line, double x0, double y0,
            double x1, double y1) {
        if (Double.isNaN(x0) || Double.isNaN(x1) || Double.isNaN(y0)
                || Double.isNaN(y1)) {
            return;
        }
        line.setLine(x0, y0, x1, y1);
        g2.draw(line);
    }

}

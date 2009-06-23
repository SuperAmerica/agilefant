package fi.hut.soberit.agilefant.util;

import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.RenderingHints.Key;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.Map;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.chart.renderer.xy.XYStepRenderer;
import org.jfree.data.xy.XYDataset;

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

    // All methods except draw(Shape s) are delegated to the wrapped Graphics2D
    class Graphics2DWrapper extends Graphics2D {

        private Graphics2D wrapped;

        public Graphics2DWrapper(Graphics2D wrapped) {
            this.wrapped = wrapped;
        }

        public void addRenderingHints(Map<?, ?> hints) {
            wrapped.addRenderingHints(hints);
        }

        public void clearRect(int x, int y, int width, int height) {
            wrapped.clearRect(x, y, width, height);
        }

        public void clip(Shape s) {
            wrapped.clip(s);
        }

        public void clipRect(int x, int y, int width, int height) {
            wrapped.clipRect(x, y, width, height);
        }

        public void copyArea(int x, int y, int width, int height, int dx, int dy) {
            wrapped.copyArea(x, y, width, height, dx, dy);
        }

        public Graphics create() {
            return wrapped.create();
        }

        public Graphics create(int x, int y, int width, int height) {
            return wrapped.create(x, y, width, height);
        }

        public void dispose() {
            wrapped.dispose();
        }

        public void draw(Shape s) {
            if (s instanceof Line2D) {
                Line2D line = (Line2D) s;
                if (line.getX1() == line.getX2()) {
                    // We are drawing a vertical line, so let's add some custom style!
                    Stroke stroke = wrapped.getStroke();
                    Paint paint = wrapped.getPaint();
                    if (stepStroke != null) {
                        wrapped.setStroke(stepStroke);
                    }
                    if (stepPaint != null) {
                        wrapped.setPaint(stepPaint);
                    }
                    wrapped.draw(s);
                    wrapped.setStroke(stroke);
                    wrapped.setPaint(paint);
                    return;
                }
            }
            wrapped.draw(s);
        }

        public void draw3DRect(int x, int y, int width, int height,
                boolean raised) {
            wrapped.draw3DRect(x, y, width, height, raised);
        }

        public void drawArc(int x, int y, int width, int height,
                int startAngle, int arcAngle) {
            wrapped.drawArc(x, y, width, height, startAngle, arcAngle);
        }

        public void drawBytes(byte[] data, int offset, int length, int x, int y) {
            wrapped.drawBytes(data, offset, length, x, y);
        }

        public void drawChars(char[] data, int offset, int length, int x, int y) {
            wrapped.drawChars(data, offset, length, x, y);
        }

        public void drawGlyphVector(GlyphVector g, float x, float y) {
            wrapped.drawGlyphVector(g, x, y);
        }

        public void drawImage(BufferedImage img, BufferedImageOp op, int x,
                int y) {
            wrapped.drawImage(img, op, x, y);
        }

        public boolean drawImage(Image img, AffineTransform xform,
                ImageObserver obs) {
            return wrapped.drawImage(img, xform, obs);
        }

        public boolean drawImage(Image img, int x, int y, Color bgcolor,
                ImageObserver observer) {
            return wrapped.drawImage(img, x, y, bgcolor, observer);
        }

        public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
            return wrapped.drawImage(img, x, y, observer);
        }

        public boolean drawImage(Image img, int x, int y, int width,
                int height, Color bgcolor, ImageObserver observer) {
            return wrapped.drawImage(img, x, y, width, height, bgcolor,
                    observer);
        }

        public boolean drawImage(Image img, int x, int y, int width,
                int height, ImageObserver observer) {
            return wrapped.drawImage(img, x, y, width, height, observer);
        }

        public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2,
                int sx1, int sy1, int sx2, int sy2, Color bgcolor,
                ImageObserver observer) {
            return wrapped.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2,
                    sy2, bgcolor, observer);
        }

        public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2,
                int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
            return wrapped.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2,
                    sy2, observer);
        }

        public void drawLine(int x1, int y1, int x2, int y2) {
            wrapped.drawLine(x1, y1, x2, y2);
        }

        public void drawOval(int x, int y, int width, int height) {
            wrapped.drawOval(x, y, width, height);
        }

        public void drawPolygon(int[] points, int[] points2, int points3) {
            wrapped.drawPolygon(points, points2, points3);
        }

        public void drawPolygon(Polygon p) {
            wrapped.drawPolygon(p);
        }

        public void drawPolyline(int[] points, int[] points2, int points3) {
            wrapped.drawPolyline(points, points2, points3);
        }

        public void drawRect(int x, int y, int width, int height) {
            wrapped.drawRect(x, y, width, height);
        }

        public void drawRenderableImage(RenderableImage img,
                AffineTransform xform) {
            wrapped.drawRenderableImage(img, xform);
        }

        public void drawRenderedImage(RenderedImage img, AffineTransform xform) {
            wrapped.drawRenderedImage(img, xform);
        }

        public void drawRoundRect(int x, int y, int width, int height,
                int arcWidth, int arcHeight) {
            wrapped.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
        }

        public void drawString(AttributedCharacterIterator iterator, float x,
                float y) {
            wrapped.drawString(iterator, x, y);
        }

        public void drawString(AttributedCharacterIterator iterator, int x,
                int y) {
            wrapped.drawString(iterator, x, y);
        }

        public void drawString(String str, float x, float y) {
            wrapped.drawString(str, x, y);
        }

        public void drawString(String str, int x, int y) {
            wrapped.drawString(str, x, y);
        }

        public boolean equals(Object obj) {
            return wrapped.equals(obj);
        }

        public void fill(Shape s) {
            wrapped.fill(s);
        }

        public void fill3DRect(int x, int y, int width, int height,
                boolean raised) {
            wrapped.fill3DRect(x, y, width, height, raised);
        }

        public void fillArc(int x, int y, int width, int height,
                int startAngle, int arcAngle) {
            wrapped.fillArc(x, y, width, height, startAngle, arcAngle);
        }

        public void fillOval(int x, int y, int width, int height) {
            wrapped.fillOval(x, y, width, height);
        }

        public void fillPolygon(int[] points, int[] points2, int points3) {
            wrapped.fillPolygon(points, points2, points3);
        }

        public void fillPolygon(Polygon p) {
            wrapped.fillPolygon(p);
        }

        public void fillRect(int x, int y, int width, int height) {
            wrapped.fillRect(x, y, width, height);
        }

        public void fillRoundRect(int x, int y, int width, int height,
                int arcWidth, int arcHeight) {
            wrapped.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
        }

        public void finalize() {
            wrapped.finalize();
        }

        public Color getBackground() {
            return wrapped.getBackground();
        }

        public Shape getClip() {
            return wrapped.getClip();
        }

        public Rectangle getClipBounds() {
            return wrapped.getClipBounds();
        }

        public Rectangle getClipBounds(Rectangle r) {
            return wrapped.getClipBounds(r);
        }

        public Rectangle getClipRect() {
            return wrapped.getClipRect();
        }

        public Color getColor() {
            return wrapped.getColor();
        }

        public Composite getComposite() {
            return wrapped.getComposite();
        }

        public GraphicsConfiguration getDeviceConfiguration() {
            return wrapped.getDeviceConfiguration();
        }

        public Font getFont() {
            return wrapped.getFont();
        }

        public FontMetrics getFontMetrics() {
            return wrapped.getFontMetrics();
        }

        public FontMetrics getFontMetrics(Font f) {
            return wrapped.getFontMetrics(f);
        }

        public FontRenderContext getFontRenderContext() {
            return wrapped.getFontRenderContext();
        }

        public Paint getPaint() {
            return wrapped.getPaint();
        }

        public Object getRenderingHint(Key hintKey) {
            return wrapped.getRenderingHint(hintKey);
        }

        public RenderingHints getRenderingHints() {
            return wrapped.getRenderingHints();
        }

        public Stroke getStroke() {
            return wrapped.getStroke();
        }

        public AffineTransform getTransform() {
            return wrapped.getTransform();
        }

        public int hashCode() {
            return wrapped.hashCode();
        }

        public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
            return wrapped.hit(rect, s, onStroke);
        }

        public boolean hitClip(int x, int y, int width, int height) {
            return wrapped.hitClip(x, y, width, height);
        }

        public void rotate(double theta, double x, double y) {
            wrapped.rotate(theta, x, y);
        }

        public void rotate(double theta) {
            wrapped.rotate(theta);
        }

        public void scale(double sx, double sy) {
            wrapped.scale(sx, sy);
        }

        public void setBackground(Color color) {
            wrapped.setBackground(color);
        }

        public void setClip(int x, int y, int width, int height) {
            wrapped.setClip(x, y, width, height);
        }

        public void setClip(Shape clip) {
            wrapped.setClip(clip);
        }

        public void setColor(Color c) {
            wrapped.setColor(c);
        }

        public void setComposite(Composite comp) {
            wrapped.setComposite(comp);
        }

        public void setFont(Font font) {
            wrapped.setFont(font);
        }

        public void setPaint(Paint paint) {
            wrapped.setPaint(paint);
        }

        public void setPaintMode() {
            wrapped.setPaintMode();
        }

        public void setRenderingHint(Key hintKey, Object hintValue) {
            wrapped.setRenderingHint(hintKey, hintValue);
        }

        public void setRenderingHints(Map<?, ?> hints) {
            wrapped.setRenderingHints(hints);
        }

        public void setStroke(Stroke s) {
            wrapped.setStroke(s);
        }

        public void setTransform(AffineTransform Tx) {
            wrapped.setTransform(Tx);
        }

        public void setXORMode(Color c1) {
            wrapped.setXORMode(c1);
        }

        public void shear(double shx, double shy) {
            wrapped.shear(shx, shy);
        }

        public String toString() {
            return wrapped.toString();
        }

        public void transform(AffineTransform Tx) {
            wrapped.transform(Tx);
        }

        public void translate(double tx, double ty) {
            wrapped.translate(tx, ty);
        }

        public void translate(int x, int y) {
            wrapped.translate(x, y);
        }

    }

    protected Graphics2D wrap(Graphics2D wrapped) {
        return new Graphics2DWrapper(wrapped);
    }

    public void drawItem(Graphics2D g2, XYItemRendererState state,
            Rectangle2D dataArea, PlotRenderingInfo info, XYPlot plot,
            ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset,
            int series, int item, CrosshairState crosshairState, int pass) {
        super.drawItem(wrap(g2), state, dataArea, info, plot, domainAxis,
                rangeAxis, dataset, series, item, crosshairState, pass);
    }

}

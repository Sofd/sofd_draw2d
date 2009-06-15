package de.sofd.draw2d.viewer.adapters;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.sofd.draw2d.DrawingObject;
import de.sofd.draw2d.Location;
import de.sofd.draw2d.event.DrawingObjectEvent;
import de.sofd.draw2d.viewer.DrawingViewer;
import de.sofd.draw2d.viewer.tools.DrawingViewerTool;

/**
 * Base class for drawing object viewer adapters, which handle things like
 * drawing, mouse hit testing and mouse handle definition for a
 * {@link DrawingObject} inside a {@link DrawingViewer}.
 * <p>
 * A DrawingViewer does not itself know how to draw any of the DrawingObjects in
 * its Drawing, nor does it know about things like the objects' bounding boxes
 * (which DrawingViewer uses to determine when an object actually needs to be
 * redrawn -- it doesn't if its bounding box doesn't intersect the area in need
 * of redrawing), where the "interior" of an object is (relevant for mouse hit
 * testing) or what "mouse dragging handles" (see {@link MouseHandle} class) an
 * object provides. Instead, the viewer delegates drawing of the DrawingObjects
 * (including visual feedback of "selected" state etc.) and all the other tasks
 * to per-object viewer adapters, which are instances of subclasses of this
 * class. There is one such adapter per DrawingObject in the Drawing; the
 * adapter knows its DrawingObject. N.B.: The DrawingViewer does not itself
 * perform things like mouse hit testing and dragging of mouse handles. Instead,
 * it delegates these things to its currently activated
 * {@link DrawingViewerTool}.
 * <p>
 * This base class already performs reasonable default behaviour for many of the
 * necessary tasks. When the object is selected in the viewer, this base class
 * draws a dashed rectangle around the object's outline, and it draws small
 * squares for all the mouse handles of the object. The base class itself
 * defines 4 handles at the four corner points of the object's outline, which
 * when dragged change the bounding box accordingly.
 * <p>
 * If you've written a new DrawingObject subclass, you should write a
 * corresponding {@link DrawingObjectViewerAdapter} subclass for it, and
 * override at least the {@link #paintObjectOn(Graphics2D)} method with the code
 * that draws your object. You must then define your own
 * {@link ObjectViewerAdapterFactory} (most likely you'll subclass
 * {@link DefaultObjectViewerAdapterFactory}), and supply that to the viewer in
 * the constructor.
 * 
 * @author Olaf Klischat
 */
public class DrawingObjectViewerAdapter {

    private final DrawingViewer viewer;
    private final DrawingObject drawingObject;

    public static final int HANDLE_BOX_WIDTH = 6;
    
    public DrawingObjectViewerAdapter(DrawingViewer viewer, DrawingObject drawingObject) {
        this.viewer = viewer;
        this.drawingObject = drawingObject;
    }

    public DrawingViewer getViewer() {
        return viewer;
    }

    public DrawingObject getDrawingObject() {
        return drawingObject;
    }

    /**
     * Paint the {@link #getDrawingObject()} of this adapter to the given
     * Graphics2D. This should *not* also paint anything if the object is
     * selected -- use
     * {@link #paintSelectionVisualizationOn(Graphics2D, boolean)} for that (the
     * reason for doing this in two methods is that DrawingViewer wants to paint
     * the selection visualizations of selected objects above all the objects
     * themselves). The default implementation of this method paints nothing.
     * Your should override, or your objects will be invisible unless they're
     * selected.
     * 
     * @param g2d
     */
    public void paintObjectOn(Graphics2D g2d) {
        
    }

    /**
     * Paint the "selection visualization" for the {@link #getDrawingObject()}
     * of this adapter. This is a separate method because DrawingViewer wants to
     * paint the selection visualizations of selected objects above all the
     * objects themselves.
     * <p>
     * The default implementation draws a dashed rectangle around a selected
     * object's outline, and it draws small squares for all the mouse handles
     * (defined by {@link #getHandleCount()} / {@link #getHandle(int)}) of the
     * object. It draws nothing if the object is not selected.
     * 
     * @param g2d
     *            graphics to paint onto
     * @param isSelected
     *            flags that tells whether the object is actually selected in
     *            the viewer atm. Normally you'd only draw anything if it is.
     */
    public void paintSelectionVisualizationOn(Graphics2D g2d, boolean isSelected) {
        if (isSelected) {
            g2d.setPaint(Color.GREEN);
            // by default, draw all the object's handles
            int count = getHandleCount();
            for (int i=0; i<count; ++i) {
                MouseHandle handle = getHandle(i);
                if (null != handle) { // should always be the case...
                    Point2D posn = getViewer().objToDisplay(handle.getPosition());
                    g2d.fillRect((int) (posn.getX() - HANDLE_BOX_WIDTH/2),
                                 (int) (posn.getY() - HANDLE_BOX_WIDTH/2),
                                 HANDLE_BOX_WIDTH,
                                 HANDLE_BOX_WIDTH);
                }
            }

            //... and a dashed rectangle around the object
            g2d.setStroke(new BasicStroke(0, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10.0f, new float[]{2,4}, 0.0f));
            // transform the shape, not g2d, so the stroke (dash segment lenghts) isn't transformed as well
            g2d.draw(getViewer().getObjectToDisplayTransform().createTransformedShape(getDrawingObject().getBounds2D()));
        }
    }

    /**
     * Tell whether the given rectangle intersects this adapter's DrawingObject,
     * in display coordinates. Default implementation tests whether the rect
     * intersects with the {@link #getBounds2DDisp()}. Used to decide whether
     * the object needs redrawing, given a specific invalidated area.
     * 
     * @param rect
     * @return
     */
    public boolean intersectsDisp(Rectangle2D rect) {
        return rect.intersects(getBounds2DDisp());
    }

    /**
     * Returns a rectangle in display coordinates that marks the outline of an
     * area outside of which no drawing operations by this adapter should take
     * place. Used by {@link #intersectsDisp(Rectangle2D)} for now. Default
     * implementation returns the object's bounding box (
     * {@link DrawingObject#getBounds2D()}), transformed to display coordinates
     * and extended such that any MouseHandles that might be drawn on the edge
     * of the bounding box will still lie completely inside the returned
     * rectangle. If you define handles outside that area, override this method
     * (or just override {@link #intersectsDisp(Rectangle2D)} to always return
     * true, if that doesn't hinder performance too much). If the returned
     * rectangle is too small, you might see artifacts.
     * 
     * @return
     */
    public Rectangle2D getBounds2DDisp() {
        Rectangle result = getViewer().getObjectToDisplayTransform()
                                            .createTransformedShape(getDrawingObject().getBounds2D())
                                                .getBounds();
        result.setRect(result.getMinX() - HANDLE_BOX_WIDTH/2 - 1,
                       result.getMinY() - HANDLE_BOX_WIDTH/2 - 1,
                       result.getWidth() + HANDLE_BOX_WIDTH + 1,
                       result.getHeight() + HANDLE_BOX_WIDTH + 1);
        return result;
    }
    
    protected void scheduleSelfRepaint() {
        viewer.repaintObjectArea(drawingObject);
    }
    
    /**
     * Callback that's called by the viewer if any {@link DrawingObjectEvent}
     * has occured on this adapter's DrawingObject. By default, just schedule a
     * repaint. Subclasses may override.
     */
    public void onDrawingObjectEvent(DrawingObjectEvent e) {
        /*
         * N.B.: If this is a before-change DrawingObjectLocationChangeEvent,
         * the source DrawingObject (lce.getSource()) is still located at its
         * original 2D position. Otherwise, if this is an after-change
         * DrawingObjectLocationChangeEvent, the object it is already located at
         * its new position. In both cases, the area of the object needs to be
         * scheduled for repainting
         */
        scheduleSelfRepaint();
    }

    /**
     * {@link MouseHandle} implementation that represents a specific
     * corner point of the outline of our DrawingObject.
     * 
     * @author Olaf Klischat
     */
    private class BoundsHandle extends MouseHandle {
        //private static final long serialVersionUID = 3673099677005547116L;
        private final int nr;

        /**
         * 
         * @param id
         *            => superclass c'tor
         * @param nr
         *            number (0..3) of the corner point. The number being
         *            defined as in {@link Location#getPt(int)}.
         */
        public BoundsHandle(int nr) {
            super(DrawingObjectViewerAdapter.this.getDrawingObject(),  // TODO: this would always be the same; let the viewer set it using a setter in the base class
                  DrawingObjectViewerAdapter.class.getName() + ".boundsHandle" + nr);
            this.nr = nr;
        }
        public int getNr() {
            return nr;
        }
        @Override
        public Point2D getPosition() {
            return getDrawingObject().getLocation().getPt(nr);
        }
        @Override
        public void setPosition(Point2D posn) {
            getDrawingObject().setLocationPt(nr, posn);
        }
    }
    
    public int getHandleCount() {
        return 4;
    }
    
    public MouseHandle getHandle(int i) {
        if (i >= 0 && i < 4) {
            Point2D position = drawingObject.getLocation().getPt(i);
            viewer.getObjectToDisplayTransform().transform(position, position);
            return new BoundsHandle(i);
        } else {
            return null;
        }
    }
    
    public List<MouseHandle> getAllHandles() {
        int count = getHandleCount();
        List<MouseHandle> result = new ArrayList<MouseHandle>(count);
        for (int i=0; i<count; ++i) {
            result.add(getHandle(i));
        }
        return Collections.unmodifiableList(result);
    }

    /**
     * Handle hit test.
     * 
     * @param x
     *            x
     * @param y
     *            y
     * @return {@link MouseHandle} located below (x,y) (display
     *         coordinates). null if there's no handle there.
     */
    public MouseHandle getHandleAt(double x, double y) {
        int count = getHandleCount();
        for (int i=0; i<count; ++i) {
            MouseHandle handle = getHandle(i);
            if (hits(handle, x, y)) {
                return handle;
            }
        }
        return null;
    }
    
    public MouseHandle getHandleAt(Point2D pt) {
        return getHandleAt(pt.getX(), pt.getY());
    }
    
    protected boolean hits(MouseHandle handle, double x, double y) {
        Point2D handlePosnDisp = getViewer().objToDisplay(handle.getPosition());
        return (Math.abs(handlePosnDisp.getX() - x) < HANDLE_BOX_WIDTH/2) &&
               (Math.abs(handlePosnDisp.getY() - y) < HANDLE_BOX_WIDTH/2);
    }
    
}

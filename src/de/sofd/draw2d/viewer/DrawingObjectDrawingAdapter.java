package de.sofd.draw2d.viewer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.sofd.draw2d.DrawingObject;
import de.sofd.draw2d.Location;
import de.sofd.draw2d.event.DrawingObjectEvent;


public class DrawingObjectDrawingAdapter {

    private final DrawingViewer viewer;
    private final DrawingObject drawingObject;

    public static final int HANDLE_BOX_WIDTH = 5;
    
    public DrawingObjectDrawingAdapter(DrawingViewer viewer, DrawingObject drawingObject) {
        this.viewer = viewer;
        this.drawingObject = drawingObject;
    }

    public DrawingViewer getViewer() {
        return viewer;
    }

    public DrawingObject getDrawingObject() {
        return drawingObject;
    }

    public void paintObjectOn(Graphics2D g2d) {
        
    }

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
    
    public boolean objectOverlaps(Rectangle rect) {
        return true;  // TODO: real impl
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
        viewer.repaintObjectArea(drawingObject);
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
            super(DrawingObjectDrawingAdapter.this.getDrawingObject(),  // TODO: this would always be the same; let the viewer set it using a setter in the base class
                  DrawingObjectDrawingAdapter.class.getName() + ".boundsHandle" + nr);
            this.nr = nr;
        }
        public int getNr() {
            return nr;
        }
        @Override
        public double getX() {
            return getDrawingObject().getLocation().getPt(nr).getX();
        }
        @Override
        public double getY() {
            return getDrawingObject().getLocation().getPt(nr).getY();
        }
        @Override
        public void setX(double x) {
            Point2D newpt = getDrawingObject().getLocationPt(nr);
            newpt.setLocation(x, newpt.getY());
        }
        @Override
        public void setY(double y) {
            Point2D newpt = getDrawingObject().getLocationPt(nr);
            newpt.setLocation(newpt.getX(), y);
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
    
    protected boolean hits(MouseHandle handle, double x, double y) {
        return (Math.abs(handle.getX() - x) < HANDLE_BOX_WIDTH/2) &&
               (Math.abs(handle.getY() - y) < HANDLE_BOX_WIDTH/2);
    }
    
}

package de.sofd.draw2d;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import de.sofd.draw2d.event.DrawingObjectEvent;
import de.sofd.draw2d.event.DrawingObjectListener;
import de.sofd.draw2d.event.DrawingObjectLocationChangeEvent;

/**
 * Base class for vector drawing objects that live in an infinite, continuous,
 * 2-dimensional cartesian coordinate system with double-valued x- and y-axes.
 * <p>
 * No drawing code in here or in any subclasses, no dependency on any GUI or
 * drawing toolkits. For displaying DrawingObjects, add them to a Drawing and
 * display that using a DrawingViewer.
 * 
 * @author olaf
 */
public abstract class DrawingObject {
    
    private final List<DrawingObjectListener> drawingObjectListeners =
        new ArrayList<DrawingObjectListener>();
    
    private final Rectangle2D location = new Rectangle2D.Double();

    /**
     * Gives the current 2D location of this object, as a rectangular bounding
     * box. The default implementation stores the location in an internal member
     * variable, whose value is returned here. See
     * {@link #setLocation(Rectangle2D)} for more information.
     * 
     * @return current 2D location of this object, as a rectangular bounding box
     */
    public Rectangle2D getLocation() {
        return location;
    }

    /**
     * "Relocate" the object by specifying its new bounding box. This can be
     * used to change the position or the dimensions of the object, or both.
     * <p>
     * The default implementation stores the location in an internal member
     * variable, calls {@link #onLocationChanged(Rectangle2D)}, and also fires
     * {@link DrawingObjectLocationChangeEvent}s before and after the change
     * correctly. Subclasses would normally override
     * {@link #onLocationChanged(Rectangle2D)} to implement specific
     * functionality that should take place when the location changes. However,
     * they may also override (replace) both {@link #getLocation()} and
     * {@link #setLocation(Rectangle2D)} in concert and change/store the
     * location in whatever fashion they like. In that case, the subclass is
     * responsible for firing the correct events and calling
     * {@link #onLocationChanged(Rectangle2D)}.
     * 
     * @param newLocation new location for this object
     */
    public void setLocation(Rectangle2D newLocation) {
        Rectangle2D oldLocation = new Rectangle2D.Double();
        oldLocation.setRect(this.location);
        fireDrawingObjectEvent(new DrawingObjectLocationChangeEvent(this, true, oldLocation, newLocation));
        this.location.setRect(newLocation);
        onLocationChanged(oldLocation);
        fireDrawingObjectEvent(new DrawingObjectLocationChangeEvent(this, false, oldLocation, newLocation));
    }

    public void setLocation(double x, double y, double w, double h) {
        setLocation(new Rectangle2D.Double(x, y, w, h));
    }
    
    /**
     * Called immediately after this DrawingObject's {@link #getLocation()} has
     * changed (before any post-change {@link DrawingObjectLocationChangeEvent}s
     * are fired). The new position is already in {@link #getLocation()};
     * oldLocation is the previous location.
     * <p>
     * DrawingObject's implementation of this method does nothing.
     * 
     * @param oldLocation
     *            oldLocation
     */
    protected void onLocationChanged(Rectangle2D oldLocation) {
        //
    }
    
    public boolean contains(Point2D pt) {
        return getLocation().contains(pt);
    }
    
    public void addDrawingObjectListener(DrawingObjectListener l) {
        drawingObjectListeners.add(l);
    }
    
    public void removeDrawingObjectListener(DrawingObjectListener l) {
        drawingObjectListeners.remove(l);
    }
    
    protected void fireDrawingObjectEvent(DrawingObjectEvent e) {
        for (DrawingObjectListener l : drawingObjectListeners) {
            l.onDrawingObjectEvent(e);
        }
    }
    
}

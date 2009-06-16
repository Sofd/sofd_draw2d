package de.sofd.draw2d;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.sofd.draw2d.event.DrawingObjectColorChangeEvent;
import de.sofd.draw2d.event.DrawingObjectEvent;
import de.sofd.draw2d.event.DrawingObjectListener;
import de.sofd.draw2d.event.DrawingObjectLocationChangeEvent;
import de.sofd.draw2d.viewer.DrawingViewer;

/**
 * Base class for vector drawing objects that live in an infinite, continuous,
 * 2-dimensional cartesian coordinate system with double-valued x- and y-axes.
 * <p>
 * Fires pre- and post-change events if any aspect of itself changes (see
 * {@link #addDrawingObjectListener(DrawingObjectListener)} and friends).
 * <p>
 * No drawing code in here or in any subclasses, no dependency on any GUI or
 * drawing toolkits. For displaying DrawingObjects, add them to a
 * {@link Drawing} and display that using a {@link DrawingViewer}.
 * 
 * @author olaf
 */
public abstract class DrawingObject {
    
    private final List<DrawingObjectListener> drawingObjectListeners =
        new ArrayList<DrawingObjectListener>();
    
    private final Location location = new Location(0,0,0,0);
    
    private Color color = Color.RED;
    
    private final Map<String, Object> tags = new HashMap<String, Object>();

    /**
     * Gives the current 2D location of this object, as a rectangular bounding
     * box. The default implementation stores the location in an internal member
     * variable, whose value is returned here. See
     * {@link #setLocation(Rectangle2D)} for more information.
     * 
     * @return current 2D location of this object, as a rectangular bounding box
     */
    public Location getLocation() {
        return new Location(location);
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
    public void setLocation(Location newLocation) {
        Location oldLocation = new Location(this.location);
        fireDrawingObjectEvent(new DrawingObjectLocationChangeEvent(this, true, oldLocation, newLocation));
        this.location.setLocation(newLocation);
        onLocationChanged(oldLocation);
        fireDrawingObjectEvent(new DrawingObjectLocationChangeEvent(this, false, oldLocation, newLocation));
    }
    
    public void setLocation(double x1, double y1, double x2, double y2) {
        setLocation(new Location(x1, y1, x2, y2));
    }
    
    public void setLocation(Point2D pt0, Point2D pt2) {
        setLocation(new Location(pt0, pt2));
    }
    
    public Point2D getLocationPt(int n) {
        return location.getPt(n);
    }
    
    public void setLocationPt(int n, Point2D pt) {
        Location newLoc = new Location(location);
        newLoc.setPt(n, pt);
        setLocation(newLoc);
    }

    public void moveBy(double dx, double dy) {
        Location newLoc = new Location(location);
        newLoc.moveBy(dx, dy);
        setLocation(newLoc);
    }

    public Rectangle2D getBounds2D() {
        return this.location.getBounds2D();
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
    protected void onLocationChanged(Location oldLocation) {
        //
    }
    
    public Color getColor() {
        return color;
    }
    
    public void setColor(Color newColor) {
        Color oldColor = this.color;
        fireDrawingObjectEvent(DrawingObjectColorChangeEvent.newBeforeChangeEvent(this, oldColor, newColor));
        this.color = newColor;
        fireDrawingObjectEvent(DrawingObjectColorChangeEvent.newAfterChangeEvent(this, oldColor, newColor));
    }
    
    public boolean contains(Point2D pt) {
        return getBounds2D().contains(pt);
    }
    
    public void setTag(String name, Object value) {
        tags.put(name, value);
    }
    
    public void deleteTag(String name) {
        tags.remove(name);
    }
    
    public Object getTag(String name) {
        return tags.get(name);
    }
    
    public Collection<String> getAllTagNames() {
        return tags.keySet();
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

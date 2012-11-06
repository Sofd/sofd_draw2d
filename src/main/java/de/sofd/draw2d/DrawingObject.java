package de.sofd.draw2d;

import de.sofd.draw2d.event.ChangeRejectedException;
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
import de.sofd.draw2d.event.DrawingObjectTagChangeEvent;
import de.sofd.draw2d.viewer.DrawingViewer;
import de.sofd.util.Misc;
import java.io.IOException;
import java.io.Serializable;

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
public abstract class DrawingObject implements Serializable, Cloneable {
    
    private static final long serialVersionUID = -3793808304161426235L;

    private /*final*/ transient List<DrawingObjectListener> drawingObjectListeners =
        new ArrayList<DrawingObjectListener>();  // field can't be final because of deserialization
    
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
     * variable, calls {@link #onLocationChanged(Rectangle2D)} and
     * {@link #onLocationChangedAfterEvents(Location)}, and also fires
     * {@link DrawingObjectLocationChangeEvent}s before and after the change
     * correctly. Subclasses would normally override
     * {@link #onLocationChanged(Rectangle2D)} or
     * {@link #onLocationChangedAfterEvents(Location)} to implement specific
     * functionality that should take place when the location changes. However,
     * they may also override (replace) both {@link #getLocation()} and
     * {@link #setLocation(Rectangle2D)} in concert and change/store the
     * location in whatever fashion they like. In that case, the subclass is
     * responsible for firing the correct events and calling
     * {@link #onLocationChanged(Rectangle2D)} and
     * {@link #onLocationChangedAfterEvents(Location)}.
     * <p>
     * All other location-changing methods (
     * {@link #setLocation(Point2D, Point2D)},
     * {@link #setLocation(double, double, double, double)},
     * {@link #setLocationPt(int, Point2D)}, {@link #moveBy(double, double)})
     * are just convenience wrappers around this method; they're all guaranteed
     * to ultimately call this one.
     * 
     * @param newLocation
     *            new location for this object
     */
    public void setLocation(Location newLocation) {
        Location oldLocation = new Location(this.location);
        if (fireDrawingObjectEvent(new DrawingObjectLocationChangeEvent(this, true, oldLocation, newLocation))) {
            this.location.setLocation(newLocation);
            onLocationChanged(oldLocation);
            fireDrawingObjectEvent(new DrawingObjectLocationChangeEvent(this, false, oldLocation, newLocation));
            onLocationChangedAfterEvents(oldLocation);
        }
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

    /**
     * Called immediately after this DrawingObject's {@link #getLocation()} has
     * changed and all post-change {@link DrawingObjectLocationChangeEvent}
     * listeners have been invoked. The new position is in
     * {@link #getLocation()}; oldLocation is the previous location.
     * <p>
     * DrawingObject's implementation of this method does nothing.
     * 
     * @param oldLocation
     *            oldLocation
     */
    protected void onLocationChangedAfterEvents(Location oldLocation) {
        //
    }
    
    public Color getColor() {
        return color;
    }
    
    public void setColor(Color newColor) {
        Color oldColor = this.color;
        if (fireDrawingObjectEvent(DrawingObjectColorChangeEvent.newBeforeChangeEvent(this, oldColor, newColor))) {
            this.color = newColor;
            fireDrawingObjectEvent(DrawingObjectColorChangeEvent.newAfterChangeEvent(this, oldColor, newColor));
        }
    }
    
    public boolean contains(Point2D pt) {
        return getBounds2D().contains(pt);
    }
    
    public void setTag(String name, Object value) {
        Object oldValue = getTag(name);
        if (fireDrawingObjectEvent(DrawingObjectTagChangeEvent.newBeforeChangeEvent(this, name, oldValue, value))) {
            tags.put(name, value);
            fireDrawingObjectEvent(DrawingObjectTagChangeEvent.newAfterChangeEvent(this, name, oldValue, value));
        }
    }
    
    public void deleteTag(String name) {
        Object oldValue = getTag(name);
        if (fireDrawingObjectEvent(DrawingObjectTagChangeEvent.newBeforeChangeEvent(this, name, oldValue, null))) {
            tags.remove(name);
            fireDrawingObjectEvent(DrawingObjectTagChangeEvent.newAfterChangeEvent(this, name, oldValue, null));
        }
    }
    
    public Object getTag(String name) {
        return tags.get(name);
    }
    
    public Collection<String> getAllTagNames() {
        return tags.keySet();
    }
    
    public Map<String,Object> getTags() {
        return new HashMap<String, Object>(tags);
    }

    /**
     * Completely replace this DrawingObject's tags with the supplied ones. Mainly
     * needed for XML beans serialization, but can be used for any purpose.
     *
     * @param newTags
     */
    public void setTags(Map<String,Object> newTags) {
        // do this properly, including firing of tagDeleted events for
        // the old tags and tagAdded events for the new ones
        for (String tagName : getAllTagNames()) {
            deleteTag(tagName);
        }
        for (Map.Entry<String, Object> e : newTags.entrySet()) {
            setTag(e.getKey(), e.getValue());
        }
    }

    public void addDrawingObjectListener(DrawingObjectListener l) {
        drawingObjectListeners.add(l);
    }
    
    public void removeDrawingObjectListener(DrawingObjectListener l) {
        drawingObjectListeners.remove(l);
    }

    /**
     * Helper method for firing {@link DrawingObjectEvent}s.
     *
     * @param e the event
     * @return false if e was a pre-change event and one of the listeners rejected
     *         the event by firing {@link ChangeRejectedException} (the fired exception
     *         will be available in {@link ChangeRejectedException#getLastException()}).
     *         Otherwise, true (and {@link ChangeRejectedException#getLastException()} will
     *         be reset to null).
     */
    protected boolean fireDrawingObjectEvent(DrawingObjectEvent e) {
        try {
            // iterate over a copy of drawingObjectListeners so a listener adding new listeners
            // won't lead to ConcurrentModificationException
            for (DrawingObjectListener l : drawingObjectListeners.toArray(new DrawingObjectListener[drawingObjectListeners.size()])) {
                l.onDrawingObjectEvent(e);
            }
            ChangeRejectedException.resetLastException();
            return true;
        } catch (ChangeRejectedException ex) {
            ChangeRejectedException.setLastException(ex);
            return false;
        }
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return Misc.deepCopy(this);
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        drawingObjectListeners = new ArrayList<DrawingObjectListener>();
        in.defaultReadObject();
    }

}

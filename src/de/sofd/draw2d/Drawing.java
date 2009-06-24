package de.sofd.draw2d;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.sofd.draw2d.event.DrawingListener;
import de.sofd.draw2d.event.DrawingObjectAddOrMoveEvent;
import de.sofd.draw2d.event.DrawingObjectEvent;
import de.sofd.draw2d.event.DrawingObjectListener;
import de.sofd.draw2d.event.DrawingObjectRemoveEvent;
import de.sofd.draw2d.viewer.DrawingViewer;

/**
 * A Drawing made up of {@link DrawingObject}s. The objects are contained in a
 * defined order (z-order) which determines which objects are drawn above/below
 * which other ones.
 * <p>
 * Fires pre- and post-change events if objects are added or removed, and also
 * forwards all the change events of the constituent objects, all to the same
 * set of event listeners (see {@link #addDrawingListener(DrawingListener)} and
 * friends). (so users will only have to add one listener to the drawing instead
 * of also adding/removing listener to/from all the drawing's objects as they're
 * added/removed).
 * <p>
 * For painting or displaying a drawing and for providing a means for the end
 * user to interactively modify the drawing, use a {@link DrawingViewer}.
 * 
 * @author olaf
 */
public class Drawing {

    // TODO: a LinkedIdentityHashSet would be better here (as soon as we've implemented it)
    private final List<DrawingObject> drawingObjects = new ArrayList<DrawingObject>();

    private final Map<String, Object> tags = new HashMap<String, Object>();
    
    /**
     * Add o to this drawing at position index in the z order. If o was already
     * in the drawing, just move it to position index in the z order.
     * 
     * @param index
     *            index
     * @param o
     *            o
     */
    public void addDrawingObject(int index, DrawingObject o) {
        int oldIndex = drawingObjects.indexOf(o);
        if ((oldIndex != -1) && (oldIndex != index)) {
            fireEvent(DrawingObjectAddOrMoveEvent.newBeforeObjectMoveEvent(this, oldIndex, index));
            drawingObjects.remove(oldIndex);
            drawingObjects.add(index, o);
            fireEvent(DrawingObjectAddOrMoveEvent.newAfterObjectMoveEvent(this, oldIndex, index));
        } else if (oldIndex != index) {
            fireEvent(DrawingObjectAddOrMoveEvent.newBeforeObjectAddEvent(this, index));
            drawingObjects.add(index, o);
            o.addDrawingObjectListener(drawingObjectEventForwarder);
            fireEvent(DrawingObjectAddOrMoveEvent.newAfterObjectAddEvent(this, index));
        }
    }

    /**
     * Add o to this drawing at the top of the z order (i.e. into the
     * foreground, i.e. to the end of {@link #getObjects()}). If o was already
     * in the drawing, just move it to the top of the z order.
     * 
     * @param o
     *            o
     */
    public void addDrawingObject(DrawingObject o) {
        addDrawingObject(getObjectCount(), o);
    }
    
    public boolean contains(DrawingObject o) {
        return drawingObjects.contains(o);
    }

    /**
     * 
     * @param o
     *            o
     * @return position of o in this drawing's z order, or -1 if o isn't part of
     *         this drawing.
     */
    public int indexOf(DrawingObject o) {
        return drawingObjects.indexOf(o);
    }
    
    public void removeDrawingObject(DrawingObject o) {
        int index = indexOf(o);
        if (index != -1) {
            removeDrawingObject(index);
        }
    }

    /**
     * Remove DrawingObject at index index in z-order.
     * 
     * @param index
     *            index
     * @throws IndexOutOfBoundsException
     *             if index is out of range
     */
    public void removeDrawingObject(int index) {
        DrawingObject o = get(index);
        fireEvent(DrawingObjectRemoveEvent.newBeforeObjectRemoveEvent(this, index));
        drawingObjects.remove(index);
        o.removeDrawingObjectListener(drawingObjectEventForwarder);
        fireEvent(DrawingObjectRemoveEvent.newAfterObjectRemoveEvent(this, index));
    }

    public int getObjectCount() {
        return drawingObjects.size();
    }

    /**
     * 
     * @param index
     *            index
     * @return DrawingObject at index index in z-order
     * @throws IndexOutOfBoundsException
     *             if index is out of range
     */
    public DrawingObject get(int index) {
        return drawingObjects.get(index);
    }
    
    /**
     * 
     * @return list of all DrawingObjects in this drawing, in reverse z order
     *         (backmost object first)
     */
    public List<DrawingObject> getObjects() {
        List<DrawingObject> result = new ArrayList<DrawingObject>();
        result.addAll(drawingObjects);
        return result;
    }

    /**
     * 
     * @param pt
     *            pt
     * @return list of all DrawingObjects located under pt, in z order (topmost
     *         object first)
     */
    public List<DrawingObject> getDrawingObjectsAt(Point2D pt) {
        List<DrawingObject> result = new ArrayList<DrawingObject>();
        int count = drawingObjects.size();
        for (int i = count-1; i >=0; i--) {
            DrawingObject drobj = drawingObjects.get(i);
            if (drobj.contains(pt)) {
                result.add(drobj);
            }
        }
        return result;
    }

    /**
     * 
     * @param pt
     *            pt
     * @return topmost DrawingObject located under pt. null if there is no
     *         DrawingObject there.
     */
    public DrawingObject getTopmostDrawingObjectAt(Point2D pt) {
        List<DrawingObject> result = new ArrayList<DrawingObject>();
        int count = drawingObjects.size();
        for (int i = count-1; i >=0; i--) {
            DrawingObject drobj = drawingObjects.get(i);
            if (drobj.contains(pt)) {
                return drobj;
            }
        }
        return null;
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
    
    private final List<DrawingListener> drawingListeners =
        new ArrayList<DrawingListener>();
    
    private final DrawingObjectListener drawingObjectEventForwarder = new DrawingObjectListener() {
        @Override
        public void onDrawingObjectEvent(DrawingObjectEvent e) {
            Drawing.this.fireEvent(e);
        }
    };
    
    public void addDrawingListener(DrawingListener l) {
        drawingListeners.add(l);
    }
    
    public void removeDrawingListener(DrawingListener l) {
        drawingListeners.remove(l);
    }
    
    protected void fireEvent(EventObject e) {
        for (DrawingListener l : drawingListeners) {
            l.onDrawingEvent(e);
        }
    }

}

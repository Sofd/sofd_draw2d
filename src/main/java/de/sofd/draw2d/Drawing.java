package de.sofd.draw2d;

import de.sofd.draw2d.event.ChangeRejectedException;

import java.awt.Color;
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
import de.sofd.util.Misc;
import java.io.IOException;
import java.io.Serializable;

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
public class Drawing implements Serializable {

    private static final long serialVersionUID = -2815369104571946712L;

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
            if (fireEvent(DrawingObjectAddOrMoveEvent.newBeforeObjectMoveEvent(this, oldIndex, index))) {
                drawingObjects.remove(oldIndex);
                drawingObjects.add(index, o);
                fireEvent(DrawingObjectAddOrMoveEvent.newAfterObjectMoveEvent(this, oldIndex, index));
            }
        } else if (oldIndex != index) {
            if (fireEvent(DrawingObjectAddOrMoveEvent.newBeforeObjectAddEvent(this, index))) {
                drawingObjects.add(index, o);
                o.addDrawingObjectListener(drawingObjectEventForwarder);
                fireEvent(DrawingObjectAddOrMoveEvent.newAfterObjectAddEvent(this, index));
            }
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
        if (fireEvent(DrawingObjectRemoveEvent.newBeforeObjectRemoveEvent(this, index))) {
            DrawingObject removedObject = drawingObjects.remove(index);
            o.removeDrawingObjectListener(drawingObjectEventForwarder);
            fireEvent(DrawingObjectRemoveEvent.newAfterObjectRemoveEvent(this, index, removedObject));
        }
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
     * Completely replace this Drawing's list of DrawingObjects with the
     * supplied ones. Mainly needed for XML beans serialization, but can be used
     * for any purpose.
     * 
     * @param objs
     */
    public void setObjects(List<DrawingObject> objs) {
        // do this properly, including firing of DrawingObjectRemoveEvents for
        // the old objects and DrawingObjectAddEvents for the new ones
        while (getObjectCount() > 0) {
            removeDrawingObject(0);
        }
        for (DrawingObject o : objs) {
            addDrawingObject(o);
        }
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
    
    public Map<String,Object> getTags() {
        return new HashMap<String, Object>(tags);
    }

    /**
     * Completely replace this Drawing's tags with the supplied ones. Mainly
     * needed for XML beans serialization, but can be used for any purpose.
     * 
     * @param newTags
     */
    public void setTags(Map<String,Object> newTags) {
        // do this properly, including firing of tagDeleted events for
        // the old tags and tagAdded events for the new ones (as soon
        // as we have those events...:-P)
        for (String tagName : getAllTagNames()) {
            deleteTag(tagName);
        }
        for (Map.Entry<String, Object> e : newTags.entrySet()) {
            setTag(e.getKey(), e.getValue());
        }
    }
    
    private /*final*/ transient List<DrawingListener> drawingListeners =
        new ArrayList<DrawingListener>();  // field can't be final because of deserialization

    private final DrawingObjectEventForwarderClass drawingObjectEventForwarder =
            new DrawingObjectEventForwarderClass();

    // need to define non-anonymous class for this to facilitate serialization
    private class DrawingObjectEventForwarderClass implements DrawingObjectListener, Serializable {
        /**
         * 
         */
        private static final long serialVersionUID = -3155603009672189788L;

        @Override
        public void onDrawingObjectEvent(DrawingObjectEvent e) {
            if (!Drawing.this.fireEvent(e)) {
                throw ChangeRejectedException.getLastException();
            }
        }
    };
    
    public void addDrawingListener(DrawingListener l) {
        drawingListeners.add(l);
    }
    
    public void removeDrawingListener(DrawingListener l) {
        drawingListeners.remove(l);
    }
    
    /**
     * Helper method for firing events.
     *
     * @param e the event
     * @return false if e was a pre-change event and one of the listeners rejected
     *         the event by firing {@link ChangeRejectedException} (the fired exception
     *         will be available in {@link ChangeRejectedException#getLastException()}).
     *         Otherwise, true (and {@link ChangeRejectedException#getLastException()} will
     *         be reset to null).
     */
    protected boolean fireEvent(EventObject e) {
        try {
            for (DrawingListener l : drawingListeners) {
                l.onDrawingEvent(e);
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
        drawingListeners = new ArrayList<DrawingListener>();
        in.defaultReadObject();
        // the drawingObjects list was read from the stream, but all the associated processing
        // (adding drawingObjectEventForwarder to each object, creating the DrawingAdapter for each object, etc.)
        // wasn't done, so we're in an invalid state right now. Clear out drawingObjects and re-add
        // all the DrawingObjects through the regular addDrawingObject method so all the necessary
        // processing can take place.
        // TODO: ideally, we'd read the drawingObjects field manually from the stream without
        //   immediately setting it into this.drawingObjects, thus obviating the need to clear
        //   out the latter. Consult the Java Object Serialization spec for how to do this.
        ArrayList<DrawingObject> deserializedObjs = new ArrayList<DrawingObject>(drawingObjects);
        drawingObjects.clear();
        for (DrawingObject o : deserializedObjs) {
            addDrawingObject(o);
        }
    }

}

package de.sofd.draw2d.event;

import de.sofd.draw2d.Drawing;
import de.sofd.draw2d.DrawingObject;

/**
 * Event indication that a {@link DrawingObject} is to be or has been removed
 * from a {@link Drawing}.
 * 
 * @author olaf
 */
public class DrawingObjectRemoveEvent extends DrawingEvent {

    private static final long serialVersionUID = -436574494714032465L;

    private final boolean isBeforeChange;
    private final int index;
    private final DrawingObject removedObject;

    protected DrawingObjectRemoveEvent(Drawing source, boolean isBeforeChange, int index, DrawingObject removedObject) {
        super(source);
        this.isBeforeChange = isBeforeChange;
        this.index = index;
        this.removedObject = removedObject;
    }

    /**
     * 
     * @return true iff the event indicates that the removal is imminent, but
     *         has not occured yet (so the object is still present in the
     *         drawing at position {@link #getIndex()})
     */
    public boolean isBeforeChange() {
        return isBeforeChange;
    }

    /**
     * 
     * @return !{@link #isBeforeChange()}
     */
    public boolean isAfterChange() {
        return !isBeforeChange;
    }

    /**
     * 
     * @return z-order index of the DrawingObject to be removed or having been
     *         removed
     */
    public int getIndex() {
        return index;
    }

    /**
     * 
     * @return the DrawingObject this event refers to, if it can be retrieved
     *         from the Drawing (which is, if {@link #isBeforeChange()})
     */
    public DrawingObject getObject() {
        if (isBeforeChange) {
            return getSource().get(getIndex());
        } else {
            return removedObject;
        }
    }
    
    // public "constructors"

    public static DrawingObjectRemoveEvent newBeforeObjectRemoveEvent(Drawing source, int index) {
        return new DrawingObjectRemoveEvent(source, true, index, null);
    }

    public static DrawingObjectRemoveEvent newAfterObjectRemoveEvent(Drawing source, int index, DrawingObject removedObject) {
        return new DrawingObjectRemoveEvent(source, false, index, removedObject);
    }
}

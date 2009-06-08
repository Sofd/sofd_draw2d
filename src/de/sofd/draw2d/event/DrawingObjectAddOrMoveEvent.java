package de.sofd.draw2d.event;

import de.sofd.draw2d.Drawing;
import de.sofd.draw2d.DrawingObject;

/**
 * Event indicating that a {@link DrawingObject} has been added to a
 * {@link Drawing} or was moved (in the z-order) in the drawing.
 * 
 * @author olaf
 */
public class DrawingObjectAddOrMoveEvent extends DrawingEvent {
    
    private static final long serialVersionUID = 6241763053687496180L;

    private final boolean isBeforeChange;
    private final int oldIndex, newIndex;

    protected DrawingObjectAddOrMoveEvent(Drawing source, boolean isBeforeChange, int oldIndex, int newIndex) {
        super(source);
        this.isBeforeChange = isBeforeChange;
        this.oldIndex = oldIndex;
        this.newIndex = newIndex;
    }

    public boolean isBeforeChange() {
        return isBeforeChange;
    }

    public boolean isAfterChange() {
        return !isBeforeChange;
    }
    
    /**
     * 
     * @return true if {@link #getObject()} was in the drawing before, and
     *         was only moved in the z-order (from position
     *         {@link #getOldIndex()} to position {@link #getNewIndex()}),
     *         rather than being newly added (at position {@link #getNewIndex()}
     *         )
     */
    public boolean isMoved() {
        return oldIndex != -1;
    }

    /**
     * 
     * @return if the DrawingObject was / is to be moved in the z-order, the old
     *         index. Undefined otherwise.
     */
    public int getOldIndex() {
        if (!isMoved()) {
            throw new IllegalStateException("old index undefined");
        }
        return oldIndex;
    }

    /**
     * 
     * @return the new index of the DrawingObject in the z order
     */
    public int getNewIndex() {
        return newIndex;
    }

    /**
     * 
     * @return the DrawingObject this event refers to, if it can be retrieved
     *         from the Drawing
     */
    public DrawingObject getObject() {
        if (isMoved()) {
            return getSource().get(isBeforeChange ? getOldIndex() : getNewIndex());
        } else {
            if (isBeforeChange) {
                throw new IllegalStateException("can't determine to-be-added DrawingObject for a before-DrawingObjectAdd event");
            }
            return getSource().get(getNewIndex());
        }
    }
    
    // public "constructors"

    public static DrawingObjectAddOrMoveEvent newBeforeObjectAddEvent(Drawing source, int index) {
        return new DrawingObjectAddOrMoveEvent(source, true, -1, index);
    }

    public static DrawingObjectAddOrMoveEvent newAfterObjectAddEvent(Drawing source, int index) {
        return new DrawingObjectAddOrMoveEvent(source, false, -1, index);
    }

    public static DrawingObjectAddOrMoveEvent newBeforeObjectMoveEvent(Drawing source, int oldIndex, int newIndex) {
        return new DrawingObjectAddOrMoveEvent(source, true, oldIndex, newIndex);
    }

    public static DrawingObjectAddOrMoveEvent newAfterObjectMoveEvent(Drawing source, int oldIndex, int newIndex) {
        return new DrawingObjectAddOrMoveEvent(source, false, oldIndex, newIndex);
    }

}

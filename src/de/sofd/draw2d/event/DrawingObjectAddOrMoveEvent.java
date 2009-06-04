package de.sofd.draw2d.event;

import de.sofd.draw2d.Drawing;
import de.sofd.draw2d.DrawingObject;

/**
 * Event indication that a {@link DrawingObject} has been added to a
 * {@link Drawing} or was moved (in the z-order) in the drawing.
 * 
 * @author olaf
 */
public class DrawingObjectAddOrMoveEvent extends DrawingEvent {
    
    private static final long serialVersionUID = 6241763053687496180L;

    private final int oldIndex, newIndex;

    protected DrawingObjectAddOrMoveEvent(Drawing source, int oldIndex, int newIndex) {
        super(source);
        this.oldIndex = oldIndex;
        this.newIndex = newIndex;
    }

    public boolean isMoved() {
        return oldIndex != -1;
    }
    
    public int getOldIndex() {
        return oldIndex;
    }

    public int getNewIndex() {
        return newIndex;
    }

    
    // public "constructors"

    public static DrawingObjectAddOrMoveEvent newObjectAddEvent(Drawing source, int index) {
        return new DrawingObjectAddOrMoveEvent(source, -1, index);
    }

    public static DrawingObjectAddOrMoveEvent newObjectMoveEvent(Drawing source, int oldIndex, int newIndex) {
        return new DrawingObjectAddOrMoveEvent(source, oldIndex, newIndex);
    }

}

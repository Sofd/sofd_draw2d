package de.sofd.draw2d.event;

import de.sofd.draw2d.Drawing;
import de.sofd.draw2d.DrawingObject;

/**
 * Event indication that a {@link DrawingObject} has been removed from a
 * {@link Drawing}.
 * 
 * @author olaf
 */
public class DrawingObjectRemoveEvent extends DrawingEvent {

    private static final long serialVersionUID = -436574494714032465L;

    private final int oldIndex;

    protected DrawingObjectRemoveEvent(Drawing source, int oldIndex) {
        super(source);
        this.oldIndex = oldIndex;
    }

    public boolean isMoved() {
        return oldIndex != -1;
    }
    
    public int getOldIndex() {
        return oldIndex;
    }

    // public "constructors"

    public static DrawingObjectRemoveEvent newObjectRemoveEvent(Drawing source, int index) {
        return new DrawingObjectRemoveEvent(source, index);
    }
}

package de.sofd.draw2d.event;

import java.awt.Color;

import de.sofd.draw2d.DrawingObject;

/**
 * Event indicating that a {@link DrawingObject}'s color has changed.
 * 
 * @author Olaf Klischat
 */
public class DrawingObjectColorChangeEvent extends DrawingObjectEvent {

    private static final long serialVersionUID = 6826706696411318409L;

    private final boolean isBeforeChange;
    private final Color lastColor;
    private final Color newColor;
    
    public DrawingObjectColorChangeEvent(DrawingObject source,
                                         boolean isBeforeChange,
                                         Color lastColor,
                                         Color newColor) {
        super(source);
        this.isBeforeChange = isBeforeChange;
        this.lastColor = lastColor;
        this.newColor = newColor;
    }

    public boolean isBeforeChange() {
        return isBeforeChange;
    }

    public boolean isAfterChange() {
        return !isBeforeChange;
    }

    public Color getLastColor() {
        return lastColor;
    }
    
    public Color getNewColor() {
        return newColor;
    }
    
    // public "constructors"

    public static DrawingObjectColorChangeEvent newBeforeChangeEvent(DrawingObject source, Color lastColor, Color newColor) {
        return new DrawingObjectColorChangeEvent(source, true, lastColor, newColor);
    }

    public static DrawingObjectColorChangeEvent newAfterChangeEvent(DrawingObject source, Color lastColor, Color newColor) {
        return new DrawingObjectColorChangeEvent(source, false, lastColor, newColor);
    }

}

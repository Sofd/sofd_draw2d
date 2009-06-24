package de.sofd.draw2d.event;

import de.sofd.draw2d.DrawingObject;

/**
 * Event indicating that a tag of a {@link DrawingObject} has changed (tag
 * added, removed, or value changed). See {@link DrawingObject#getTag(String)}
 * and co.
 * 
 * @author Olaf Klischat
 */
public class DrawingObjectTagChangeEvent extends DrawingObjectEvent {

    private static final long serialVersionUID = 6826706696411318409L;

    private final boolean isBeforeChange;
    private final String tagName;
    private final Object lastValue;
    private final Object newValue;
    
    public DrawingObjectTagChangeEvent(DrawingObject source,
                                       boolean isBeforeChange,
                                       String tagName,
                                       Object lastValue,
                                       Object newValue) {
        super(source);
        this.isBeforeChange = isBeforeChange;
        this.tagName = tagName;
        this.lastValue = lastValue;
        this.newValue = newValue;
    }

    public boolean isBeforeChange() {
        return isBeforeChange;
    }

    public boolean isAfterChange() {
        return !isBeforeChange;
    }

    public String getTagName() {
        return tagName;
    }
    
    public Object getLastValue() {
        return lastValue;
    }
    
    public Object getNewValue() {
        return newValue;
    }
    
    // public "constructors"

    public static DrawingObjectTagChangeEvent newBeforeChangeEvent(DrawingObject source, String tagName, Object lastValue, Object newValue) {
        return new DrawingObjectTagChangeEvent(source, true, tagName, lastValue, newValue);
    }

    public static DrawingObjectTagChangeEvent newAfterChangeEvent(DrawingObject source, String tagName, Object lastValue, Object newValue) {
        return new DrawingObjectTagChangeEvent(source, false, tagName, lastValue, newValue);
    }

}

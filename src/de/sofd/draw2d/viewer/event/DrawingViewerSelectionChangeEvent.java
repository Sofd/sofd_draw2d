package de.sofd.draw2d.viewer.event;

import java.util.Collection;

import de.sofd.draw2d.DrawingObject;
import de.sofd.draw2d.viewer.DrawingViewer;


public class DrawingViewerSelectionChangeEvent extends DrawingViewerEvent {

    private final boolean isBeforeChange;
    private final boolean isAddition;
    private final Collection<DrawingObject> objects;
    
    public DrawingViewerSelectionChangeEvent(DrawingViewer source,
                                             boolean isBeforeChange,
                                             boolean isAddition,
                                             Collection<DrawingObject> objects) {
        super(source);
        this.isBeforeChange = isBeforeChange;
        this.isAddition = isAddition;
        this.objects = objects;
    }

    public boolean isBeforeChange() {
        return isBeforeChange;
    }

    public boolean isAfterChange() {
        return !isBeforeChange;
    }
    
    public boolean isAddition() {
        return isAddition;
    }

    public boolean isRemoval() {
        return !isAddition;
    }

    public Collection<DrawingObject> getObjects() {
        return objects;
    }

    
    // public "constructors"

    public static DrawingViewerSelectionChangeEvent newBeforeObjectAddEvent(DrawingViewer source, Collection<DrawingObject> objects) {
        return new DrawingViewerSelectionChangeEvent(source, true, true, objects);
    }

    public static DrawingViewerSelectionChangeEvent newAfterObjectAddEvent(DrawingViewer source, Collection<DrawingObject> objects) {
        return new DrawingViewerSelectionChangeEvent(source, false, true, objects);
    }

    public static DrawingViewerSelectionChangeEvent newBeforeObjectRemoveEvent(DrawingViewer source, Collection<DrawingObject> objects) {
        return new DrawingViewerSelectionChangeEvent(source, true, false, objects);
    }

    public static DrawingViewerSelectionChangeEvent newAfterObjectRemoveEvent(DrawingViewer source, Collection<DrawingObject> objects) {
        return new DrawingViewerSelectionChangeEvent(source, false, false, objects);
    }

}

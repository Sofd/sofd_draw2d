package de.sofd.draw2d;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

import de.sofd.draw2d.event.DrawingListener;
import de.sofd.draw2d.event.DrawingObjectEvent;
import de.sofd.draw2d.event.DrawingObjectListener;


public class Drawing {

    // TODO: a LinkedIdentityHashSet would be better here (as soon as we've implemented it)
    private final List<DrawingObject> drawingObjects = new ArrayList<DrawingObject>();

    public void addDrawingObject(DrawingObject o) {
        if (drawingObjects.add(o)) {
            o.addDrawingObjectListener(drawingObjectEventForwarder);
        }
    }
    
    public void removeDrawingObject(DrawingObject o) {
        if (drawingObjects.remove(o)) {
            o.removeDrawingObjectListener(drawingObjectEventForwarder);
        }
    }

    public int getObjectCount() {
        return drawingObjects.size();
    }
    
    public List<DrawingObject> getObjects() {
        List<DrawingObject> result = new ArrayList<DrawingObject>();
        result.addAll(drawingObjects);
        return result;
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

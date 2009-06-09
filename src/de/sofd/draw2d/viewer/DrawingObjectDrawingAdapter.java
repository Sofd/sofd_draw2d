package de.sofd.draw2d.viewer;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import de.sofd.draw2d.DrawingObject;
import de.sofd.draw2d.event.DrawingObjectEvent;


public class DrawingObjectDrawingAdapter {

    private final DrawingViewer viewer;
    private final DrawingObject drawingObject;
    
    public DrawingObjectDrawingAdapter(DrawingViewer viewer, DrawingObject drawingObject) {
        this.viewer = viewer;
        this.drawingObject = drawingObject;
    }

    public DrawingViewer getViewer() {
        return viewer;
    }

    public DrawingObject getDrawingObject() {
        return drawingObject;
    }

    public void paintObjectOn(Graphics2D g2d) {
        
    }

    public boolean objectOverlaps(Rectangle rect) {
        return true;  // TODO: real impl
    }

    /**
     * Callback that's called by the viewer if any {@link DrawingObjectEvent}
     * has occured on this adapter's DrawingObject. By default, just schedule a
     * repaint. Subclasses may override.
     */
    public void onDrawingObjectEvent(DrawingObjectEvent e) {
        /*
         * N.B.: If this is a before-change DrawingObjectLocationChangeEvent,
         * the source DrawingObject (lce.getSource()) is still located at its
         * original 2D position. Otherwise, if this is an after-change
         * DrawingObjectLocationChangeEvent, the object it is already located at
         * its new position. In both cases, the area of the object needs to be
         * scheduled for repainting
         */
        viewer.repaintObjectArea(drawingObject);
    }

}

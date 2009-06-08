package de.sofd.draw2d.viewer;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import de.sofd.draw2d.DrawingObject;


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

}

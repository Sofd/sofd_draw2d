package de.sofd.draw2d.viewer;

import java.awt.Color;
import java.awt.Graphics2D;

import de.sofd.draw2d.RectangleObject;

public class RectangleObjectDrawingAdapter extends DrawingObjectDrawingAdapter {

    public RectangleObjectDrawingAdapter(DrawingViewer viewer, RectangleObject drawingObject) {
        super(viewer, drawingObject);
    }

    @Override
    public RectangleObject getDrawingObject() {
        return (RectangleObject) super.getDrawingObject();
    }
    
    @Override
    public void paintObjectOn(Graphics2D g2d) {
        g2d.setPaint(Color.RED);
        g2d.transform(getViewer().getObjectToDisplayTransform());
        g2d.draw(getDrawingObject().getBounds2D());
    }

}

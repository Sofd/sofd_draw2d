package de.sofd.draw2d.viewer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import de.sofd.draw2d.EllipseObject;

public class EllipseObjectDrawingAdapter extends DrawingObjectDrawingAdapter {

    public EllipseObjectDrawingAdapter(DrawingViewer viewer, EllipseObject drawingObject) {
        super(viewer, drawingObject);
    }

    @Override
    public EllipseObject getDrawingObject() {
        return (EllipseObject) super.getDrawingObject();
    }
    
    @Override
    public void paintObjectOn(Graphics2D g2d) {
        g2d.setPaint(Color.RED);
        g2d.transform(getViewer().getObjectToDisplayTransform());
        g2d.setStroke(new BasicStroke(0));
        g2d.draw(getDrawingObject().getEllipse());
    }

}

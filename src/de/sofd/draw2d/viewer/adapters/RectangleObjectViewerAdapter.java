package de.sofd.draw2d.viewer.adapters;

import java.awt.BasicStroke;
import java.awt.Graphics2D;

import de.sofd.draw2d.RectangleObject;
import de.sofd.draw2d.viewer.DrawingViewer;
import de.sofd.draw2d.viewer.gc.GC;

public class RectangleObjectViewerAdapter extends DrawingObjectViewerAdapter {

    public RectangleObjectViewerAdapter(DrawingViewer viewer, RectangleObject drawingObject) {
        super(viewer, drawingObject);
    }

    @Override
    public RectangleObject getDrawingObject() {
        return (RectangleObject) super.getDrawingObject();
    }
    
    @Override
    public void paintObjectOn(GC gc) {
        Graphics2D g2d = (Graphics2D) gc.getGraphics2D().create();
        g2d.setPaint(getDrawingObject().getColor());
        g2d.transform(getViewer().getObjectToDisplayTransform());
        g2d.setStroke(new BasicStroke(0));
        g2d.draw(getDrawingObject().getBounds2D());
    }

}

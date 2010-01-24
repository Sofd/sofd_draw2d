package de.sofd.draw2d.viewer.adapters;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import de.sofd.draw2d.PolygonObject;
import de.sofd.draw2d.viewer.DrawingViewer;
import de.sofd.draw2d.viewer.gc.GC;

public class PolygonObjectViewerAdapter extends DrawingObjectViewerAdapter {

    public PolygonObjectViewerAdapter(DrawingViewer viewer, PolygonObject drawingObject) {
        super(viewer, drawingObject);
    }

    @Override
    public PolygonObject getDrawingObject() {
        return (PolygonObject) super.getDrawingObject();
    }
    
    @Override
    public void paintObjectOn(GC gc) {
        Graphics2D g2d = (Graphics2D) gc.getGraphics2D().create();
        g2d.setPaint(getDrawingObject().getColor());
        g2d.transform(getViewer().getObjectToDisplayTransform());
        g2d.setStroke(new BasicStroke(0));
        int ptCount = getDrawingObject().getPointCount();
        if (ptCount > 1) {
            Point2D startPt = getDrawingObject().getPoint(0);
            Point2D prevPt = (Point2D) startPt.clone();
            for (int i = 1; i < ptCount; ++i) {
                Point2D nextPt = getDrawingObject().getPoint(i);
                g2d.draw(new Line2D.Double(prevPt, nextPt));
                prevPt = nextPt;
            }
            if (getDrawingObject().isClosed()) {
                g2d.draw(new Line2D.Double(prevPt, startPt));
            }
        }
    }

}

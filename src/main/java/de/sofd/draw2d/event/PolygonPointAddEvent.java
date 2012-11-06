package de.sofd.draw2d.event;

import java.awt.geom.Point2D;

import de.sofd.draw2d.PolygonObject;

public class PolygonPointAddEvent extends DrawingObjectEvent {

    private static final long serialVersionUID = 5073507056352253489L;

    private final boolean isBeforeChange;
    private final int pointIndex;
    private final Point2D newPoint;

    public PolygonPointAddEvent(PolygonObject source, boolean isBeforeChange, int pointIndex, Point2D newPoint) {
        super(source);
        this.isBeforeChange = isBeforeChange;
        this.pointIndex = pointIndex;
        this.newPoint = new Point2D.Double(newPoint.getX(), newPoint.getY());
    }
    
    @Override
    public PolygonObject getSource() {
        return (PolygonObject) super.getSource();
    }

    public boolean isBeforeChange() {
        return isBeforeChange;
    }

    public boolean isAfterChange() {
        return !isBeforeChange;
    }

    public int getPointIndex() {
        return pointIndex;
    }

    public Point2D getNewPoint() {
        return newPoint;
    }
    

    // public "constructors"

    public static PolygonPointAddEvent newBeforeChangeEvent(PolygonObject source, int pointIndex, Point2D newPoint) {
        return new PolygonPointAddEvent(source, true, pointIndex, newPoint);
    }

    public static PolygonPointAddEvent newAfterChangeEvent(PolygonObject source, int pointIndex, Point2D newPoint) {
        return new PolygonPointAddEvent(source, false, pointIndex, newPoint);
    }
    
}

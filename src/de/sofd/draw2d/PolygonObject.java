package de.sofd.draw2d;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import de.sofd.draw2d.event.PolygonPointAddEvent;

public class PolygonObject extends DrawingObject {

    private ArrayList<Point2D> points = new ArrayList<Point2D>();
    private boolean isClosed = false;

    /**
     * during "internal" location changes (i.e. changes to the bounding box as a
     * reaction to polygon points being added/moved/removed) we must not scale
     * all the points in the polygon to fit the new box. Use this variable to
     * flag internal location changes.
     */
    private boolean inInternalSetLocation = false;
    

    protected void runInInternalSetLocation(Runnable r) {
        boolean oldValue = inInternalSetLocation;
        inInternalSetLocation = true;
        try {
            r.run();
        } finally {
            inInternalSetLocation = oldValue;
        }
    }
    
    protected void internalSetLocation(final Point2D pt0, final Point2D pt2) {
        runInInternalSetLocation(new Runnable() {
            @Override
            public void run() {
                setLocation(pt0, pt2);
            }
        });
    }
    
    protected void internalSetLocation(final Location newLocation) {
        runInInternalSetLocation(new Runnable() {
            @Override
            public void run() {
                setLocation(newLocation);
            }
        });
    }
    
    public int getPointCount() {
        return points.size();
    }
    
    public Point2D getPoint(int index) {
        Point2D result = points.get(index);
        return new Point2D.Double(result.getX(), result.getY());
    }
    
    public void appendPoint(Point2D pt) {
        if (points.isEmpty()) {
            internalSetLocation(pt, pt);
        } else {
            expandLocationToInclude(pt);
        }
        fireDrawingObjectEvent(PolygonPointAddEvent.newBeforeChangeEvent(this, getPointCount(), pt));
        points.add(pt);
        fireDrawingObjectEvent(PolygonPointAddEvent.newAfterChangeEvent(this, getPointCount() - 1, pt));
    }
    
    private void expandLocationToInclude(Point2D pt) {
        Point2D pt0 = getLocationPt(0);
        Point2D pt2 = getLocationPt(2);
        int leftmostPtIndex = ( pt0.getX() < pt2.getX() ? 0 : 2);
        int rightmostPtIndex = 2 - leftmostPtIndex;
        int topmostPtIndex = ( pt0.getY() < pt2.getY() ? 0 : 2);
        int bottommostPtIndex = 2 - topmostPtIndex;

        Point2D leftmostPt = getLocationPt(leftmostPtIndex);
        Point2D rightmostPt = getLocationPt(rightmostPtIndex);
        Point2D topmostPt = getLocationPt(topmostPtIndex);
        Point2D bottommostPt = getLocationPt(bottommostPtIndex);
        
        Location newLocation = new Location(getLocation());
        boolean locationChanged = false;
        if (pt.getX() < leftmostPt.getX()) {
            leftmostPt.setLocation(pt.getX(), leftmostPt.getY());
            newLocation.setPt(leftmostPtIndex, leftmostPt);
            locationChanged = true;
        }
        if (pt.getX() > rightmostPt.getX()) {
            rightmostPt.setLocation(pt.getX(), rightmostPt.getY());
            newLocation.setPt(rightmostPtIndex, rightmostPt);
            locationChanged = true;
        }
        if (pt.getY() < topmostPt.getY()) {
            topmostPt.setLocation(topmostPt.getX(), pt.getY());
            newLocation.setPt(topmostPtIndex, topmostPt);
            locationChanged = true;
        }
        if (pt.getY() > bottommostPt.getY()) {
            bottommostPt.setLocation(bottommostPt.getX(), pt.getY());
            newLocation.setPt(bottommostPtIndex, bottommostPt);
            locationChanged = true;
        }
        
        if (locationChanged) {
            internalSetLocation(newLocation);
        }
    }

    @Override
    protected void onLocationChanged(Location oldLocation) {
        if (!inInternalSetLocation) {
            // scale the points
        }
    }

}

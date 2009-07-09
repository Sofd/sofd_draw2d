package de.sofd.draw2d;

import de.sofd.draw2d.event.ChangeRejectedException;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import de.sofd.draw2d.event.DrawingObjectEvent;
import de.sofd.draw2d.event.PolygonPointAddEvent;

public class PolygonObject extends DrawingObject {
    
    private static final long serialVersionUID = -1665813672031268850L;

    private ArrayList<Point2D> points = new ArrayList<Point2D>();
    private boolean isClosed = true;

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
                if (null != ChangeRejectedException.getLastException()) {
                    throw ChangeRejectedException.getLastException();
                }
            }
        });
    }
    
    protected void internalSetLocation(final Location newLocation) {
        runInInternalSetLocation(new Runnable() {
            @Override
            public void run() {
                setLocation(newLocation);
                if (null != ChangeRejectedException.getLastException()) {
                    throw ChangeRejectedException.getLastException();
                }
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
        try {
            if (points.isEmpty()) {
                internalSetLocation(pt, pt);
            } else {
                expandLocationToInclude(pt);
            }
            if (fireDrawingObjectEvent(PolygonPointAddEvent.newBeforeChangeEvent(this, getPointCount(), pt))) {
                points.add(pt);
                fireDrawingObjectEvent(PolygonPointAddEvent.newAfterChangeEvent(this, getPointCount() - 1, pt));
            }
        } catch (ChangeRejectedException e) {
            // the initial internalSetLocation / expandLocationToInclude rejected the location change.
            // don't add the point, do nothing.
        }
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
        Point2D topmostPt;
        Point2D bottommostPt;
        if (topmostPtIndex == leftmostPtIndex) {
            topmostPt = leftmostPt;
            bottommostPt = rightmostPt;
        } else {
            topmostPt = rightmostPt;
            bottommostPt = leftmostPt;
        }
        
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

    private void movePoint(int ptIndex, Point2D newPt, boolean adjustBounds) {
        points.set(ptIndex, (Point2D) newPt.clone());
        // TODO: implement adjustBounds == true
    }
    
    @Override
    protected void onLocationChangedAfterEvents(Location oldLocation) {
        if (!inInternalSetLocation) {
            // scale all the points
            Location newLocation = getLocation();
            AffineTransform t = getLocationTransform(oldLocation, newLocation);
            int count = getPointCount();
            for (int i = 0; i < count; ++i) {
                Point2D newPt = t.transform(points.get(i), null);
                movePoint(i, newPt, false);
            }
            // send just generic "something's changed" message, triggering a complete redraw.
            // may use more specific change event later
            fireDrawingObjectEvent(new DrawingObjectEvent(this));
        }
    }
    
    private AffineTransform getLocationTransform(Location fromLoc, Location toLoc) {
        AffineTransform result = new AffineTransform();
        result.translate(toLoc.getPt0().getX(), toLoc.getPt0().getY());
        double oldWidth = fromLoc.getPt2().getX() - fromLoc.getPt0().getX();
        double oldHeight = fromLoc.getPt2().getY() - fromLoc.getPt0().getY();
        double newWidth = toLoc.getPt2().getX() - toLoc.getPt0().getX();
        double newHeight = toLoc.getPt2().getY() - toLoc.getPt0().getY();
        if (Math.abs(oldWidth) > 1e-20) {
            result.scale(newWidth / oldWidth, 1.0);
        }
        if (Math.abs(oldHeight) > 1e-20) {
            result.scale(1.0, newHeight / oldHeight);
        }
        result.translate(- fromLoc.getPt0().getX(), - fromLoc.getPt0().getY());
        return result;
    }
    
    public boolean isClosed() {
        return isClosed;
    }
    
    public void setClosed(boolean isClosed) {
        this.isClosed = isClosed;
        // send just generic "something's changed" message, triggering a complete redraw.
        // may use more specific change event later
        fireDrawingObjectEvent(new DrawingObjectEvent(this));
    }

    @Override
    public boolean contains(Point2D pt) {
        if (!getBounds2D().contains(pt)) { return false; }

        //precise point-in-polygon test starting here
        
        //result true <=> beam in +x direction starting at pt crosses
        //                the polygon's outline an odd number of times

        int pointsCount = points.size();
        if (pointsCount < 2) { return false; }
        Point2D prevVertex = points.get(0);
        int nCrosses = 0;
        // for each edge (prevVertex---vertex)
        for (int i = 1; i <= pointsCount; i++) {
            Point2D vertex = points.get(i==pointsCount ? 0 : i);
            if ((prevVertex.getY() > pt.getY()) == (vertex.getY() > pt.getY())) {
                prevVertex = vertex;
                // edge lies completely inside one of the two half spaces defined by the beam
                continue;
            }
            double crossX = prevVertex.getX() + (vertex.getX()-prevVertex.getX())*(pt.getY()-prevVertex.getY())/(vertex.getY()-prevVertex.getY());
            if (crossX > pt.getX()) {
                nCrosses++;
            }

            prevVertex = vertex;
        }
        return nCrosses%2 == 1;
    }
    
}

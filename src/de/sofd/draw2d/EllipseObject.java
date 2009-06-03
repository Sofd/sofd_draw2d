package de.sofd.draw2d;

import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class EllipseObject extends DrawingObject {

    @Override
    public boolean contains(Point2D pt) {
        Rectangle2D loc = this.getLocation();
        // TODO: cache the ellipse whenever this.location changes...
        return new Ellipse2D.Double(loc.getX(), loc.getY(), loc.getWidth(), loc.getHeight()).contains(pt);
    }
    
}

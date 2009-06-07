package de.sofd.draw2d;

import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class EllipseObject extends DrawingObject {

    @Override
    public boolean contains(Point2D pt) {
        Rectangle2D bounds = this.getBounds2D();
        // TODO: cache the ellipse whenever this.location changes...
        return new Ellipse2D.Double(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight()).contains(pt);
    }
    
}

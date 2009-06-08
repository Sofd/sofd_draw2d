package de.sofd.draw2d;

import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class EllipseObject extends DrawingObject {

    public EllipseObject() {
        super();
    }
    
    public EllipseObject(double x1, double y1, double x2, double y2) {
        super();
        setLocation(x1, y1, x2, y2);
    }
    
    /**
     * 
     * @return the ellipse shape this EllipseObject represents
     */
    public Ellipse2D getEllipse() {
        // TODO: cache the ellipse whenever this.location changes...
        Rectangle2D bounds = this.getBounds2D();
        return new Ellipse2D.Double(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
    }
    
    @Override
    public boolean contains(Point2D pt) {
        return getEllipse().contains(pt);
    }
    
}

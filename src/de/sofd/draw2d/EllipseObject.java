package de.sofd.draw2d;

import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class EllipseObject extends DrawingObject {

    private static final long serialVersionUID = 3905185786243383543L;
    
    /**
     * the ellipse shape this EllipseObject represents
     */
    private Ellipse2D ellipse = new Ellipse2D.Double();
    
    public EllipseObject() {
        super();
    }
    
    public EllipseObject(double x1, double y1, double x2, double y2) {
        super();
        setLocation(x1, y1, x2, y2);
    }
    
    @Override
    protected void onLocationChanged(Location oldLocation) {
        super.onLocationChanged(oldLocation);
        Rectangle2D bounds = this.getBounds2D();
        this.ellipse = new Ellipse2D.Double(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
    }
    
    /**
     * 
     * @return the ellipse shape this EllipseObject represents
     */
    public Ellipse2D getEllipse() {
        return ellipse;
    }
    
    @Override
    public boolean contains(Point2D pt) {
        return getEllipse().contains(pt);
    }
    
}

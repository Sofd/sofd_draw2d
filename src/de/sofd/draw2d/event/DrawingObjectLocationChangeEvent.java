package de.sofd.draw2d.event;

import java.awt.geom.Rectangle2D;

import de.sofd.draw2d.DrawingObject;

public class DrawingObjectLocationChangeEvent extends DrawingObjectEvent {

    private static final long serialVersionUID = 6826706696411318409L;

    private final boolean isBeforeChange;
    private final Rectangle2D lastLocation;
    private final Rectangle2D newLocation;
    
    public DrawingObjectLocationChangeEvent(DrawingObject source,
                                            boolean isBeforeChange,
                                            Rectangle2D lastLocation,
                                            Rectangle2D newLocation) {
        super(source);
        this.isBeforeChange = isBeforeChange;
        this.lastLocation = lastLocation;
        this.newLocation = newLocation;
    }

    public boolean isBeforeChange() {
        return isBeforeChange;
    }

    public Rectangle2D getLastLocation() {
        return lastLocation;
    }

    public Rectangle2D getNewLocation() {
        return newLocation;
    }

}

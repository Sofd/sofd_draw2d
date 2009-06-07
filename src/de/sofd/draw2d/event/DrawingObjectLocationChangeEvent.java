package de.sofd.draw2d.event;

import de.sofd.draw2d.DrawingObject;
import de.sofd.draw2d.Location;

public class DrawingObjectLocationChangeEvent extends DrawingObjectEvent {

    private static final long serialVersionUID = 6826706696411318409L;

    private final boolean isBeforeChange;
    private final Location lastLocation;
    private final Location newLocation;
    
    public DrawingObjectLocationChangeEvent(DrawingObject source,
                                            boolean isBeforeChange,
                                            Location lastLocation,
                                            Location newLocation) {
        super(source);
        this.isBeforeChange = isBeforeChange;
        this.lastLocation = lastLocation;
        this.newLocation = newLocation;
    }

    public boolean isBeforeChange() {
        return isBeforeChange;
    }

    public Location getLastLocation() {
        return lastLocation;
    }

    public Location getNewLocation() {
        return newLocation;
    }

}

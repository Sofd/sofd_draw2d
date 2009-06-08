package de.sofd.draw2d.event;

import java.util.EventObject;

import de.sofd.draw2d.DrawingObject;

/**
 * Base class for events that indicate changes happening to a
 * {@link DrawingObject}.
 * 
 * @author Olaf Klischat
 */
public class DrawingObjectEvent extends EventObject {

    private static final long serialVersionUID = 1629657628134515033L;

    public DrawingObjectEvent(DrawingObject source) {
        super(source);
    }
    
    @Override
    public DrawingObject getSource() {
        return (DrawingObject) super.getSource();
    }
    
}

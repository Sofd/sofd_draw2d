package de.sofd.draw2d.event;

import java.util.EventObject;

import de.sofd.draw2d.DrawingObject;

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

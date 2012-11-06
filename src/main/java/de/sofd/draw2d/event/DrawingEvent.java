package de.sofd.draw2d.event;

import java.util.EventObject;

import de.sofd.draw2d.Drawing;

public class DrawingEvent extends EventObject {

    private static final long serialVersionUID = 1629657628134515033L;

    public DrawingEvent(Drawing source) {
        super(source);
    }
    
    @Override
    public Drawing getSource() {
        return (Drawing) super.getSource();
    }
    
}

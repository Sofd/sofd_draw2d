package de.sofd.draw2d.event;

import java.util.EventListener;

public interface DrawingObjectListener extends EventListener {
    void onDrawingObjectEvent(DrawingObjectEvent e);
}

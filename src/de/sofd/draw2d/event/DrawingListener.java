package de.sofd.draw2d.event;

import java.util.EventListener;
import java.util.EventObject;

public interface DrawingListener extends EventListener {
    void onDrawingEvent(EventObject e);
}

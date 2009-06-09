package de.sofd.draw2d.viewer.event;

import java.util.EventListener;

public interface DrawingViewerListener extends EventListener {
    void onDrawingViewerEvent(DrawingViewerEvent e);
}

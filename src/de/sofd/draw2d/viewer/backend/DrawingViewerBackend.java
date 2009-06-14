package de.sofd.draw2d.viewer.backend;

import de.sofd.draw2d.viewer.DrawingViewer;


public interface DrawingViewerBackend {
    void connected(DrawingViewer viewer);
    void repaint();
    void repaint(double x, double y, double width, double height);
    void disconnected();
}

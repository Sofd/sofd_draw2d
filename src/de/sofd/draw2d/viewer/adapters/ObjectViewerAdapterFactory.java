package de.sofd.draw2d.viewer.adapters;

import de.sofd.draw2d.DrawingObject;
import de.sofd.draw2d.viewer.DrawingViewer;


public interface ObjectViewerAdapterFactory {
    DrawingObjectViewerAdapter createAdapterFor(DrawingViewer viewer, DrawingObject drawingObject);
}

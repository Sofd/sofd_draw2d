package de.sofd.draw2d.viewer.adapters;

import de.sofd.draw2d.DrawingObject;
import de.sofd.draw2d.EllipseObject;
import de.sofd.draw2d.PolygonObject;
import de.sofd.draw2d.RectangleObject;
import de.sofd.draw2d.viewer.DrawingViewer;

/**
 * ObjectViewerAdapterFactory used by DrawingViewer if no other factory was
 * explicitly specified. Can create the correct adapters for rectangle and
 * ellipse objects. Creates just a basic {@link DrawingObjectViewerAdapter} for
 * any other objects (which means they won't be visible unless they're selected,
 * in which case just the empty bounding rectangle with its mouse handles will
 * be displayed).
 * <p>
 * You can derive your own factories from this class, and in the
 * createAdapterFor implementation just call the super implementation for all
 * drawing objects that your don't handle yourself.
 * 
 * @author Olaf Klischat
 */
public class DefaultObjectViewerAdapterFactory implements ObjectViewerAdapterFactory {

    @Override
    public DrawingObjectViewerAdapter createAdapterFor(DrawingViewer viewer, DrawingObject drawingObject) {
        if (drawingObject instanceof EllipseObject) {
            return new EllipseObjectViewerAdapter(viewer, (EllipseObject) drawingObject);
        } else if (drawingObject instanceof RectangleObject) {
            return new RectangleObjectViewerAdapter(viewer, (RectangleObject) drawingObject);
        } else if (drawingObject instanceof PolygonObject) {
            return new PolygonObjectViewerAdapter(viewer, (PolygonObject) drawingObject);
        } else {
            return new DrawingObjectViewerAdapter(viewer, drawingObject);
        }
    }

}

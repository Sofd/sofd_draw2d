package de.sofd.draw2d.viewer.gc;

import de.sofd.draw2d.viewer.DrawingViewer;
import de.sofd.draw2d.viewer.adapters.DrawingObjectViewerAdapter;
import de.sofd.draw2d.viewer.adapters.ObjectViewerAdapterFactory;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * A GC ("graphics context") is used by draw2d classes (mainly, the {@link DrawingObjectViewerAdapter}
 * implementations) whenever anything needs to be drawn. Most of the cases, this is just a wrapper
 * around a {@link Graphics2D} (in fact, all classes in the draw2d package will never use it for
 * anything else). However, external users might want to display their draw2d drawings on completely
 * different 2D surfaces (e.g. an SWT graphics context or an OpenGL context or whatever). In that case,
 * you'd prepare GC instances (possibly subclassing GC, or using the {@link #setAttribute(java.lang.String, java.lang.Object) } /
 * {@link #getAttribute(java.lang.String) } stuff of GC) that hold all the context that's needed
 * for the drawing operation (e.g. an OpenGL context or whatever) and pass that into {@link DrawingViewer#paint(GC) }.
 * The paint method, when drawing the DrawingObjects in the drawing, will pass this GC on to
 * the {@link DrawingObjectViewerAdapter} of each DrawingObject. The DrawingObjectViewerAdapter must
 * then handle the actual drawing task. The standard DrawingObjectViewerAdapters can only draw
 * onto the GC's Graphics2D, so to draw onto anything else, you'd have to create your own
 * {@link ObjectViewerAdapterFactory} and pass that to the DrawingViewer's constructor. The
 * factory must create your own DrawingObjectViewerAdapter instance for each DrawingObject,
 * and that created adapter's {@link DrawingObjectViewerAdapter#paintObjectOn(GC) }
 * method will receive the GC and must do all the painting.
 *
 * @author olaf
 */
public class GC {

    protected Graphics2D graphics2d;
    protected Map<String, Object> attributes = new HashMap<String, Object>();

    public GC() {
    }

    public GC(Graphics2D graphics2d) {
        this.graphics2d = graphics2d;
    }

    /**
     *
     * @return the Graphics2D wrapped by this GC, if any.
     */
    public Graphics2D getGraphics2D() {
        return graphics2d;
    }

    /**
     * Returns the 2D clip bounds that drawing in this GC will be clipped to (optional operation).
     *
     * The default impl. delegates to the getClipBounds method of the {@link #getGraphics2D() }.
     * Subclasses should return the clip bounds of whatever graphics context they use, if
     * available. If no clip bounds are available or the subclass doesn't even
     * support clip bounds, just return null.
     */
    public Rectangle getClipBounds() {
        if (graphics2d != null) {
            return graphics2d.getClipBounds();
        } else {
            return null;
        }
    }

    /**
     * Store arbitrary additional data in this GC. The data can be retrieved again using
     * {@link #getAttribute(java.lang.String) }. The classes in draw2d don't use this feature,
     * but other classes (e.g. external {@link DrawingObjectViewerAdapter}s might.
     *
     * @param name arbitrary name to store the data under. Should follow hiearchical naming conventions
     *        ("de.sofd.foo.bar.someValue"} to avoid name clashes between multiple independent users.
     * @param value value to store
     */
    public void setAttribute(String name, Object value) {
        attributes.put(name, value);
    }

    /**
     * Retrieve data previously store using {@link #setAttribute(java.lang.String, java.lang.Object) }.
     *
     * @param name
     * @return
     */
    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    public Collection<String> getAllAttributeNames() {
        return attributes.keySet();
    }

    public Object removeAttribute(String name) {
        return attributes.remove(name);
    }
}

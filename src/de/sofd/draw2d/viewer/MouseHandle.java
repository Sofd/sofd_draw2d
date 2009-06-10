package de.sofd.draw2d.viewer;

import java.awt.geom.Point2D;
import java.io.Serializable;

import de.sofd.draw2d.DrawingObject;

/**
 * Abstract base class for a representation of a mouse handle of a
 * {@link DrawingObject}. Mouse handles can be dragged by the user using the
 * mouse, normally with a tool like the {@link SelectorTool}, to modify the
 * object in some way, e.g. drag a corner to change the size, or drag a single
 * vertex of a polygon object.
 * <p>
 * The {@link DrawingObjectDrawingAdapter} of a DrawingObject in a DrawingViewer
 * defines which handles the object has using its
 * {@link DrawingObjectDrawingAdapter#getHandleCount()} /
 * {@link DrawingObjectDrawingAdapter#getHandle(int)} etc. methods.
 * <p>
 * This is an abstract base class. Subclasses must as least implement the
 * {@link #getX()}, {@link #setX(double)}, {@link #getY()},
 * {@link #setY(double)} methods with code that gets/sets the handle's position
 * (in object coordinates), normally modifying the DrawingObject in the process.
 * Example: A handle representing a specific vertice of some polygon
 * DrawingObject would implement these methode with code that gets and sets that
 * vertice's position.
 * <p>
 * Please note that the position setter methods may be implemented to not
 * actually set the handle to the position specified (e.g., after calling
 * setX(56.0) on a handle, getX() *may not* return 56.0. It may not even change
 * the getX() coordinate at all. This is done so you can implement handles that
 * are in some way constrained in what their position may be. For example, a
 * handle in the middle of the upper line of an object's outline may be used to
 * change the height of the object by dragging the handle vertically up or down.
 * In this case, the x position of the handle would never change.
 * <p>
 * It may happen that a handle ceases to exist because of changes to its object.
 * For example, if you have handles for representing each vertice of a polygon
 * object, and you delete some points of that polygon object, all the handles
 * representing no-longer-existing vertices would become "invalid" (and there
 * might still be MouseHandle instances representing those handles floating
 * around in the program). In that case, all the handle's getter and setter
 * methods must throw {@link NoSuchMouseHandleException}. Code using those
 * methods (mostly, {@link DrawingViewerTool} implementations) should normally
 * be aware that this exception may be thrown and be prepared to deal with it in
 * some sensible way. (The exception is still derived from
 * {@link RuntimeException} rather than {@link Exception} because some handles
 * might never go away, and the code using them might know that).
 * 
 * @author olaf
 */
public abstract class MouseHandle implements Serializable {

    private static final long serialVersionUID = 8151248930824622484L;

    private final DrawingObject drawingObject;
    private final String id;  // TODO: do we still need this thing at all?

    /**
     * @param drawingObject
     *            the {@link DrawingObject} this handle belongs to
     * @param id
     *            id of the handle. Must be unique among all handles of the same
     *            DrawingObject in the same DrawingViewer
     */
    public MouseHandle(DrawingObject drawingObject, String id) {
        super();
        this.drawingObject = drawingObject;
        this.id = id;
    }

    public DrawingObject getDrawingObject() {
        return drawingObject;
    }
    
    public String getId() {
        return id;
    }

    /**
     * 
     * @return x position of the handle in display coordinates
     */
    public abstract double getX();

    public abstract void setX(double x);

    /**
     * 
     * @return y position of the handle in display coordinates
     */
    public abstract double getY();

    public abstract void setY(double y);
    
    /**
     * 
     * @return position of the handle in display coordinates
     */
    public Point2D getPosition() {
        return new Point2D.Double(getX(), getY());
    }

    public void setPosition(Point2D posn) {
        setX(posn.getX());
        setY(posn.getY());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MouseHandle other = (MouseHandle) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}

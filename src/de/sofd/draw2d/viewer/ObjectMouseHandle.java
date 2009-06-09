package de.sofd.draw2d.viewer;

import java.awt.geom.Point2D;
import java.io.Serializable;

import de.sofd.draw2d.DrawingObject;

/**
 * Representation of a mouse handle of a {@link DrawingObject} within a
 * {@link DrawingViewer}. Mouse handles can be dragged by the user using the
 * mouse, normally with the selector tool, to modify the object in some way,
 * e.g. drag a corner to change the size, or drag a single vertex of a polygon
 * object.
 * <p>
 * The {@link DrawingObjectDrawingAdapter} of a DrawingObject in a DrawingViewer
 * defines which handles the object has using its ....TODO method.
 * 
 * @author olaf
 */
public class ObjectMouseHandle implements Serializable {

    private static final long serialVersionUID = 8151248930824622484L;
    
    private final String id;
    private double x, y;     // position of the handle in display coordinates

    /**
     * 
     * @param id
     *            id of the handle. Must be unique among all handles of the same
     *            DrawingObject in the same DrawingViewer
     * @param x
     *            x coordinate of handle's position (in display coordinat space)
     * @param y
     *            y coordinate of handle's position (in display coordinat space)
     */
    public ObjectMouseHandle(String id, double x, double y) {
        super();
        this.id = id;
        this.x = x;
        this.y = y;
    }
    
    public String getId() {
        return id;
    }
    
    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
    
    public Point2D getPosition() {
        return new Point2D.Double(x, y);
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
        ObjectMouseHandle other = (ObjectMouseHandle) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}

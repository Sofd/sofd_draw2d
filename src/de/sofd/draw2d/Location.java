package de.sofd.draw2d;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

/**
 * Rectangular 2D area. Like a {@link Rectangle2D}, but works correctly with
 * negative widths and/or heights. It does this by representing the rectangle by
 * its 4 corner points, named pt0, pt1, pt2, pt3 here, where pt0 and pt2 as well
 * as pt1 and pt3 are diagonally arranged. So, pt0 and pt1 share their y
 * coordinates, as do pt2 and pt3, and pt1 and pt2 share their x coordinates, as
 * do pt0 and pt3. Setting one of the points using the
 * {@link #setPt0(double, double)} .. {@link #setPt3(double, double)} methods
 * thus automatically modifies the adjacent other points as well to keep
 * everything consistent. Diagonally arranged points are of course independent
 * of each other in this respect; changing one of them won't change the other.
 * There's also no requirement for any of the points to always be to the
 * "lower right", "upper left" etc. of some other point for all the methods in
 * this class to work correctly, as would be the case with Rectangle2D.
 * 
 * @author Olaf Klischat
 */
public class Location implements Serializable {
    
    private double x1, y1, x2, y2;

    public Location() {
        x1 = y1 = x2 = y2 = 0.0;
    }
    
    /**
     * Create new Location object, specifying two diagonally arranged corner
     * points, with pt0 = (x1,y1), and pt2 = (x2,y2)
     * 
     * @param x1
     *            x1
     * @param y1
     *            y1
     * @param x2
     *            x2
     * @param y2
     *            y2
     */
    public Location(double x1, double y1, double x2, double y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }
    
    public Location(Point2D pt0, Point2D pt2) {
        setPt(0, pt0);
        setPt(2, pt2);
    }
    
    public Location(Location other) {
        setLocation(other);
    }

    public Point2D getPt0() {
        return new Point2D.Double(x1,y1);
    }

    public void setPt0(double x, double y) {
        x1 = x;
        y1 = y;
    }

    public Point2D getPt1() {
        return new Point2D.Double(x2,y1);
    }

    public void setPt1(double x, double y) {
        x2 = x;
        y1 = y;
    }

    public Point2D getPt2() {
        return new Point2D.Double(x2,y2);
    }

    public void setPt2(double x, double y) {
        x2 = x;
        y2 = y;
    }

    public Point2D getPt3() {
        return new Point2D.Double(x1,y2);
    }
    
    public void setPt3(double x, double y) {
        x1 = x;
        y2 = y;
    }
    
    public Point2D getPt(int n) {
        switch(n) {
        case 0:
            return getPt0();
        case 1:
            return getPt1();
        case 2:
            return getPt2();
        case 3:
            return getPt3();
        default:
            throw new IndexOutOfBoundsException("Location point idx not in [0..3]: " + n);
        }
    }
    
    public void setPt(int n, Point2D newPt) {
        switch(n) {
        case 0:
            setPt0(newPt.getX(), newPt.getY());
            break;
        case 1:
            setPt1(newPt.getX(), newPt.getY());
            break;
        case 2:
            setPt2(newPt.getX(), newPt.getY());
            break;
        case 3:
            setPt3(newPt.getX(), newPt.getY());
            break;
        default:
            throw new IndexOutOfBoundsException("Location point idx not in [0..3]: " + n);
        }
    }
    
    public Rectangle2D getBounds2D() {
        return new Rectangle2D.Double(x1 < x2 ? x1 : x2,
                                      y1 < y2 ? y1 : y2,
                                      Math.abs(x2 - x1),
                                      Math.abs(y2 - y1));
    }

    public boolean contains(double x, double y) {
        return getBounds2D().contains(x, y);
    }

    public double getCenterX() {
        return (x2 - x1) / 2;
    }

    public double getCenterY() {
        return (y2 - y1) / 2;
    }

    public Point2D getCenter() {
        return new Point2D.Double(getCenterX(), getCenterY());
    }

    public void moveCenterTo(double x, double y) {
        moveBy(x - getCenterX(), y - getCenterY());
    }

    public void moveBy(double dx, double dy) {
        x1 += dx; x2 += dx;
        y1 += dy; y2 += dy;
    }

    public void setLocation(Location other) {
        this.x1 = other.x1;
        this.y1 = other.y1;
        this.x2 = other.x2;
        this.y2 = other.y2;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(x1);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(x2);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y1);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y2);
        result = prime * result + (int) (temp ^ (temp >>> 32));
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
        Location other = (Location) obj;
        if (Double.doubleToLongBits(x1) != Double.doubleToLongBits(other.x1))
            return false;
        if (Double.doubleToLongBits(x2) != Double.doubleToLongBits(other.x2))
            return false;
        if (Double.doubleToLongBits(y1) != Double.doubleToLongBits(other.y1))
            return false;
        if (Double.doubleToLongBits(y2) != Double.doubleToLongBits(other.y2))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Loc[(" + x1 + "," + y1 + ") -- (" + x2 + "," + y2 + ")]";
    }
    
}

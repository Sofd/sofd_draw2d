package de.sofd.draw2d;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Rectangular 2D area. Like a {@link Rectangle2D.Double}, but works correctly
 * with negative widths and/or heights. It does this by representing the
 * rectangle by its 4 corner points, named pt1, pt2, pt3, pt4 here, where pt1
 * and pt3 as well as pt2 and pt4 are diagonally arranged. So, pt1 and pt2 share
 * their y coordinates, as do pt3 and pt4, and pt2 and pt3 share their x
 * coordinates, as do pt1 and pt4. Setting one of the points using the
 * {@link #setPt1(double, double)} .. {@link #setPt4(double, double)} methods
 * thus automatically modifies the connected other points as well to keep
 * everything consistent.
 * 
 * @author Olaf Klischat
 */
public class Location {
    
    private double x1, y1, x2, y2;

    /**
     * Create new Location object, specifying two diagonally arranged corner
     * points, with pt1 = (x1,y1), and pt2 = (x2,y2)
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
    
    public Location(Location other) {
        setLocation(other);
    }

    public Point2D getPt1() {
        return new Point2D.Double(x1,y1);
    }

    public void setPt1(double x, double y) {
        x1 = x;
        y1 = y;
    }

    public Point2D getPt2() {
        return new Point2D.Double(x2,y1);
    }

    public void setPt2(double x, double y) {
        x2 = x;
        y1 = y;
    }

    public Point2D getPt3() {
        return new Point2D.Double(x2,y2);
    }

    public void setPt3(double x, double y) {
        x2 = x;
        y2 = y;
    }

    public Point2D getPt4() {
        return new Point2D.Double(x1,y2);
    }
    
    public void setPt4(double x, double y) {
        x1 = x;
        y2 = y;
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
        y1 =+ dy; y2 =+ dy;
    }

    public void setLocation(Location other) {
        this.x1 = other.x1;
        this.y1 = other.y1;
        this.x2 = other.x2;
        this.y2 = other.y2;
    }
    
    @Override
    public String toString() {
        return "Loc[(" + x1 + "," + y1 + ") -- (" + x2 + "," + y2 + ")]";
    }
    
}

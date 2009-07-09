package de.sofd.draw2d;

public class RectangleObject extends DrawingObject {

    private static final long serialVersionUID = 2417910576210833887L;

    public RectangleObject() {
        super();
    }
    
    public RectangleObject(double x1, double y1, double x2, double y2) {
        super();
        setLocation(x1, y1, x2, y2);
    }
    
}

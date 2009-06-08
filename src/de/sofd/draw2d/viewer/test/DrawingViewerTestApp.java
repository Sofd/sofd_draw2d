package de.sofd.draw2d.viewer.test;

import javax.swing.JFrame;

import de.sofd.draw2d.Drawing;
import de.sofd.draw2d.EllipseObject;
import de.sofd.draw2d.RectangleObject;

public class DrawingViewerTestApp {

    public DrawingViewerTestApp() {
        RectangleObject rect = new RectangleObject();
        rect.setLocation(100, 50, 400, 250);
        
        EllipseObject ellipse = new EllipseObject();
        ellipse.setLocation(180, 20, 270, 400);
        
        EllipseObject ell2 = new EllipseObject();
        ell2.setLocation(rect.getLocation());

        Drawing dr = new Drawing();
        dr.addDrawingObject(rect);
        dr.addDrawingObject(ellipse);
        dr.addDrawingObject(ell2);
        
        ViewerDisplayFrame frame1 = new ViewerDisplayFrame("Viewer 1", dr);
        frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame1.setSize(500, 500);
        frame1.setVisible(true);

        ViewerDisplayFrame frame2 = new ViewerDisplayFrame("Viewer 2", dr);
        frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame2.setSize(500, 500);
        frame2.setLocation(400, 200);
        frame2.setVisible(true);
    }
    

    /**
     * @param args
     */
    public static void main(String[] args) {
        new DrawingViewerTestApp();
    }

}

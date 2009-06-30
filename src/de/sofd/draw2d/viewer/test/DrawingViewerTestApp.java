package de.sofd.draw2d.viewer.test;

import java.awt.Color;
import java.awt.geom.Point2D;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import de.sofd.draw2d.Drawing;
import de.sofd.draw2d.EllipseObject;
import de.sofd.draw2d.PolygonObject;
import de.sofd.draw2d.RectangleObject;

public class DrawingViewerTestApp {

    public DrawingViewerTestApp() {
        RectangleObject rect = new RectangleObject();
        rect.setLocation(100, 50, 400, 250);
        rect.setColor(Color.RED);
        
        EllipseObject ellipse = new EllipseObject();
        ellipse.setLocation(180, 20, 270, 400);
        ellipse.setColor(Color.CYAN);
        
        EllipseObject ell2 = new EllipseObject();
        ell2.setLocation(rect.getLocation());
        ell2.setColor(Color.YELLOW);
        
        PolygonObject poly = getTestPolygon();
        PolygonObject poly2 = getTestPolygon2();
        PolygonObject poly3 = getLocationBugTestPolygon();

        Drawing dr = new Drawing();
        dr.addDrawingObject(rect);
        dr.addDrawingObject(ellipse);
        dr.addDrawingObject(ell2);
        dr.addDrawingObject(poly);
        dr.addDrawingObject(poly2);
        dr.addDrawingObject(poly3);
        
        DrawingViewerFrame frame1 = new DrawingViewerFrame("Viewer 1", dr);
        frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame1.setSize(700, 700);
        frame1.setVisible(true);

        DrawingViewerFrame frame2 = new DrawingViewerFrame("Viewer 2", dr);
        frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame2.setSize(700, 700);
        frame2.setLocation(400, 200);
        frame2.setVisible(true);
        
        DrawingListEditorFrame drEdFrame = new DrawingListEditorFrame(dr);
        drEdFrame.setBounds(50, 600, 700, 300);
        drEdFrame.setVisible(true);
    }
    
    private PolygonObject getTestPolygon() {
        PolygonObject result = new PolygonObject();
        result.appendPoint(new Point2D.Double(350, 75));
        result.appendPoint(new Point2D.Double(379, 161));
        result.appendPoint(new Point2D.Double(469, 161));
        result.appendPoint(new Point2D.Double(397, 215));
        result.appendPoint(new Point2D.Double(423, 301));
        result.appendPoint(new Point2D.Double(350, 250));
        result.appendPoint(new Point2D.Double(277, 301));
        result.appendPoint(new Point2D.Double(303, 215));
        result.appendPoint(new Point2D.Double(231, 161));
        result.appendPoint(new Point2D.Double(321, 161));
        result.setColor(Color.YELLOW);
        return result;
    }
    
    private PolygonObject getTestPolygon2() {
        double centerx = 180, centery = 200, r0 = 90, r1 = 100;
        int nPoints = 360;
        
        PolygonObject result = new PolygonObject();
        for (int i = 0; i < nPoints; ++i) {
            double r = (i%2 == 0 ? r0 : r1);
            double w = 2 * Math.PI * i / nPoints;
            result.appendPoint(new Point2D.Double(centerx + r * Math.cos(w), centery + r * Math.sin(w)));
        }
        result.setColor(Color.CYAN);
        return result;
    }

    private PolygonObject getLocationBugTestPolygon() {
        PolygonObject result = new PolygonObject();
        result.appendPoint(new Point2D.Double(210, 200));
        result.appendPoint(new Point2D.Double(130, 180));
        result.appendPoint(new Point2D.Double(150, 180));
        result.appendPoint(new Point2D.Double(40, 30));
        //result.appendPoint(new Point2D.Double(42, 30));
        result.setClosed(true);
        result.setColor(Color.YELLOW);
        return result;
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new DrawingViewerTestApp();
            }
        });
    }

}

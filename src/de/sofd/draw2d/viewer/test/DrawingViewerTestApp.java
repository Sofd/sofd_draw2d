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

        Drawing dr = new Drawing();
        dr.addDrawingObject(rect);
        dr.addDrawingObject(ellipse);
        dr.addDrawingObject(ell2);
        dr.addDrawingObject(poly);
        
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
        drEdFrame.setBounds(50, 550, 500, 300);
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

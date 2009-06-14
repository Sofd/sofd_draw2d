package de.sofd.draw2d.viewer.test;

import java.awt.AWTEvent;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import de.sofd.draw2d.Drawing;
import de.sofd.draw2d.DrawingObject;
import de.sofd.draw2d.viewer.DrawingViewer;
import de.sofd.draw2d.viewer.backend.DrawingViewerBackend;
import de.sofd.draw2d.viewer.tools.DrawingViewerTool;


/**
 * Simple JComponent wrapper around a {@link DrawingViewer}.
 * 
 * @author Olaf Klischat
 */
public class JDrawingViewer extends JPanel {

    private final DrawingViewer wrappedViewer;

    /*
     * We'll register the following internally defined {@link
     * DrawingViewerBackend} with the wrappedViewer. The backend will just
     * forward all repaint requests coming from the viewer to this
     * JDrawingViewer component's own repaint methods. During the next repaint
     * event cycle, this will end up calling our paintComponent method (see
     * below), where we just call the viewer's paint method to let the viewer
     * paint itself onto the supplied Graphics2D.
     */
    
    private DrawingViewerBackend wrappedViewerBackend = new DrawingViewerBackend() {
        @Override
        public void connected(DrawingViewer viewer) {
            assert viewer == wrappedViewer;
        }
        @Override
        public void repaint() {
            // the wrapped viewer called this because it has changed in some way and
            // need to be repainted. Just forward to the JComponent's repaint method.
            // Eventually this will lead to the below paintComponent method being called,
            // which is where we'll tell the viewer to actually paint itself onto
            // the component's surface.
            JDrawingViewer.this.repaint();
        }
        @Override
        public void repaint(double x, double y, double width, double height) {
            // same as above, only with clipping rectangle
            JDrawingViewer.this.repaint((int) x, (int) y, (int) width, (int) height);
        }
        @Override
        public void disconnected() {
        }
    };
    
    public JDrawingViewer() {
        wrappedViewer = new DrawingViewer();
        wrappedViewer.setBackend(wrappedViewerBackend);
        // enable mouse events (normally done automatically upon calling any of the addMouse...Listener methods,
        // by we override processMouse...Event directly instead)
        enableEvents(AWTEvent.MOUSE_EVENT_MASK|AWTEvent.MOUSE_MOTION_EVENT_MASK|AWTEvent.MOUSE_WHEEL_EVENT_MASK);
    }

    // publish (by delegation) some of the wrapped viewer's methods
    
    public void setDrawing(Drawing d) {
        wrappedViewer.setDrawing(d);
    }

    public void setObjectToDisplayTransform(AffineTransform t) {
        wrappedViewer.setObjectToDisplayTransform(t);
    }

    public void activateTool(DrawingViewerTool t) {
        wrappedViewer.activateTool(t);
    }

    public void toggleSelected(DrawingObject drobj) {
        wrappedViewer.toggleSelected(drobj);
    }


    @Override
    protected void processMouseEvent(MouseEvent e) {
        super.processMouseEvent(e);
        if (!e.isConsumed()) {
            wrappedViewer.processInputEvent(e);
        }
    }
    
    @Override
    protected void processMouseMotionEvent(MouseEvent e) {
        super.processMouseMotionEvent(e);
        if (!e.isConsumed()) {
            wrappedViewer.processInputEvent(e);
        }
    }
    
    @Override
    protected void processMouseWheelEvent(MouseWheelEvent e) {
        super.processMouseWheelEvent(e);
        if (!e.isConsumed()) {
            wrappedViewer.processInputEvent(e);
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g;
        Graphics2D userGraphics = (Graphics2D)g2d.create();
        Insets borderInsets = getInsets();
        userGraphics.transform(AffineTransform.getTranslateInstance(borderInsets.left, borderInsets.top));
        // draw background image
        renderImage(userGraphics);
        // let the viewer paint itself on top
        wrappedViewer.paint(userGraphics);
    }


    // rendering of test image for performance analysis
    
    private BufferedImage backgroundImage;
    private boolean backgroundImageFailedToLoad = false;
    
    protected void renderImage(Graphics2D g2d) {
        if (backgroundImageFailedToLoad) { return; }
        if (null == backgroundImage) {
            try {
                backgroundImage = ImageIO.read(this.getClass().getResourceAsStream("mri_brain.jpg"));
            } catch (Exception e) {
                System.err.println("failed to load DrawingViewer test background image");
                e.printStackTrace();
                backgroundImageFailedToLoad = true;
            }
        }
        BufferedImageOp scaleImageOp = new AffineTransformOp(wrappedViewer.getObjectToDisplayTransform(), AffineTransformOp.TYPE_BILINEAR);
        g2d.drawImage(backgroundImage, scaleImageOp, 0, 0);
    }
    
}

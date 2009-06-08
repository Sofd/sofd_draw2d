package de.sofd.draw2d.viewer;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.util.EventObject;
import java.util.IdentityHashMap;
import java.util.Map;

import javax.swing.JPanel;

import de.sofd.draw2d.Drawing;
import de.sofd.draw2d.DrawingObject;
import de.sofd.draw2d.EllipseObject;
import de.sofd.draw2d.RectangleObject;
import de.sofd.draw2d.event.DrawingListener;
import de.sofd.draw2d.event.DrawingObjectAddOrMoveEvent;
import de.sofd.draw2d.event.DrawingObjectLocationChangeEvent;
import de.sofd.draw2d.event.DrawingObjectRemoveEvent;

public class DrawingViewer extends JPanel {

    private static final long serialVersionUID = -5162812267404406219L;

    private Drawing drawing;
    private Map<DrawingObject, DrawingObjectDrawingAdapter> objectDrawingAdapters
        = new IdentityHashMap<DrawingObject, DrawingObjectDrawingAdapter>();

    private AffineTransform objectToDisplayTransform;
    private AffineTransform displayToObjectTransform;

    public DrawingViewer() {
        setObjectToDisplayTransform(new AffineTransform());
    }
    
    public DrawingViewer(Drawing drawing) {
        this();
        setDrawing(drawing);
    }

    public void setObjectToDisplayTransform(AffineTransform t) {
        try {
            displayToObjectTransform = t.createInverse();
            objectToDisplayTransform = t;
        } catch (NoninvertibleTransformException e) {
            throw new IllegalArgumentException("not invertible: " + t, e);
        }
        repaint();
    }
    
    public AffineTransform getObjectToDisplayTransform() {
        return objectToDisplayTransform;
    }

    public AffineTransform getDisplayToObjectTransform() {
        return displayToObjectTransform;
    }
    
    public void setDrawing(Drawing d) {
        if (null != this.drawing) {
            this.drawing.removeDrawingListener(drawingEventHandler);
            this.objectDrawingAdapters.clear();
        }
        this.drawing = d;
        if (null != this.drawing) {
            this.drawing.addDrawingListener(drawingEventHandler);
            for (DrawingObject drobj : this.drawing.getObjects()) {
                this.objectDrawingAdapters.put(drobj, createDrawingAdapterFor(drobj));
            }
        }
        repaint();
    }

    
    protected DrawingObjectDrawingAdapter createDrawingAdapterFor(DrawingObject drobj) {
        /*
         * TODO: use externally configurable DrawingObject class => adapter
         * class mapping instead of hard-coding the factory method like this
         */
        if (drobj instanceof EllipseObject) {
            return new EllipseObjectDrawingAdapter(this, (EllipseObject) drobj);
        } else if (drobj instanceof RectangleObject) {
            return new RectangleObjectDrawingAdapter(this, (RectangleObject) drobj);
        } else {
            return new DrawingObjectDrawingAdapter(this, drobj);
        }
    }

    private DrawingListener drawingEventHandler = new DrawingListener() {
        @Override
        public void onDrawingEvent(EventObject e) {
            if (e instanceof DrawingObjectAddOrMoveEvent) {
                DrawingObjectAddOrMoveEvent de = (DrawingObjectAddOrMoveEvent) e;
                if (de.isMoved()) {
                    // DrawingObject move in the z order => schedule repainting of affected area
                    repaintObjectArea(de.getObject());
                } else if (!de.isMoved() && de.isAfterChange()) {
                    // new DrawingObject added to our drawing => create corresponding adapter, schedule repainting of affected area
                    repaintObjectArea(de.getObject());
                    objectDrawingAdapters.put(de.getObject(), createDrawingAdapterFor(de.getObject()));
                }
            } else if (e instanceof DrawingObjectRemoveEvent) {
                DrawingObjectRemoveEvent re = (DrawingObjectRemoveEvent) e;
                if (re.isBeforeChange()) {
                    repaintObjectArea(re.getObject());
                }
            } else if (e instanceof DrawingObjectLocationChangeEvent) {  // TODO: DrawingObjectEvent subclasses should be handled by the adapters
                DrawingObjectLocationChangeEvent lce = (DrawingObjectLocationChangeEvent) e;
                /*
                 * if this is a before-change notification event, the source
                 * DrawingObject (lce.getSource()) is still located at its
                 * original 2D position. Otherwise, it is already located at its
                 * new position. In both cases, the area of the object needs to
                 * be scheduled for repainting
                 */
                repaintObjectArea(lce.getSource());
            }
        }
    };

    /**
     * schedule repainting of the drawing area covered by dobj
     * 
     * @param dobj
     */
    protected void repaintObjectArea(DrawingObject dobj) {
        repaint();  // TODO: real implementation
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;
        super.paintComponent(g2d);

        if (null != drawing) {
            //give the render* methods a Graphics2D whose coordinate system
            //(and eventually, clipping) is already relative to the area in
            //which the image can be drawn (i.e. excluding the space taken
            //up by our border)
            Graphics2D userGraphics = (Graphics2D)g2d.create();
            Insets borderInsets = getInsets();
            userGraphics.transform(AffineTransform.getTranslateInstance(borderInsets.left, borderInsets.top));
            renderDrawing(userGraphics);
        }
    }

    protected void renderDrawing(Graphics2D g2d) {
        if (drawing == null) { return; }
        for (DrawingObject drobj : drawing.getObjects()) {
            DrawingObjectDrawingAdapter drawingAdapter = objectDrawingAdapters.get(drobj);
            assert drawingAdapter != null;
            Rectangle clip = g2d.getClipBounds();
            if (clip != null && !drawingAdapter.objectOverlaps(clip)) { continue; }
            drawingAdapter.paintObjectOn((Graphics2D) g2d.create());
        }
    }

}

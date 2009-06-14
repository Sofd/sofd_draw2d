package de.sofd.draw2d.viewer;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EventObject;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.event.MouseInputListener;

import de.sofd.draw2d.Drawing;
import de.sofd.draw2d.DrawingObject;
import de.sofd.draw2d.EllipseObject;
import de.sofd.draw2d.RectangleObject;
import de.sofd.draw2d.event.DrawingListener;
import de.sofd.draw2d.event.DrawingObjectAddOrMoveEvent;
import de.sofd.draw2d.event.DrawingObjectEvent;
import de.sofd.draw2d.event.DrawingObjectRemoveEvent;
import de.sofd.draw2d.viewer.backend.DrawingViewerBackend;
import de.sofd.draw2d.viewer.event.DrawingViewerEvent;
import de.sofd.draw2d.viewer.event.DrawingViewerListener;
import de.sofd.draw2d.viewer.event.DrawingViewerSelectionChangeEvent;
import de.sofd.draw2d.viewer.tools.DrawingViewerTool;
import de.sofd.util.IdentityHashSet;

/**
 * Viewer and interactive editor of a {@link Drawing}.
 * <p>
 * Two coordinate systems are involved when drawing the {@link DrawingObject}s
 * in the Drawing: The system in which the objects live (i.e. the system in
 * which their locations/bounding box corners, vertex points etc. are specified)
 * is called the <strong>object coordinate system</strong>. The system in which
 * the viewer's graphical output takes place is called the <strong>display
 * coordinate system</strong>. The DrawingViewer holds a modifiable
 * transformation ({@link AffineTransform}) that's used for converting between
 * those two systems; see {@link #getObjectToDisplayTransform()},
 * {@link #setObjectToDisplayTransform(AffineTransform)}),
 * {@link #getDisplayToObjectTransform()}. Initially this is set to the identity
 * transformation, but it may be changed at any time and the viewer will update
 * its display accordingly.
 * <p>
 * The DrawingViewer listens to change events of its Drawing, changes to the
 * transformation etc., and redraws its display accordingly.
 * <p>
 * It manages a selection, which is defined as the (possibly empty) subset of
 * the drawing's DrawingObjects that are currently "selected" in this viewer.
 * (you can have more than one viewer on the same drawing, in which case each
 * viewer will have its own selection)
 * <p>
 * It delegates drawing of the DrawingObjects (including visual feedback of
 * "selected" state etc.) and mouse "hit testing" etc. to special per-object
 * "drawing adapters", which are instances of subclasses of
 * {@link DrawingObjectDrawingAdapter}: There is one such adapter per
 * DrawingObject in the Drawing; the adapter knows its DrawingObject.
 * <p>
 * A DrawingViewer fires its own set of events (subclasses of
 * {@link DrawingViewerEvent}) for events that are specific to the viewer
 * (rather than the drawing or the DrawingObjects in it). At the moment, this is
 * used for signalling changes to the DrawingViewer's selection (see
 * {@link DrawingViewerSelectionChangeEvent}).
 * <p>
 * It is supported to have more than one DrawingViewer on the same drawing, all
 * interoperating seamlessly.
 * <p>
 * To support interactive editing of the drawing by the user through the viewer,
 * a <strong>tool</strong> (instance of a subclass of {@link DrawingViewerTool})
 * must be activated on it using the {@link #activateTool(DrawingViewerTool)}.
 * At most one tool can be associated with a DrawingViewer at a time, but this
 * tool may be changed at any time by again calling
 * {@link #activateTool(DrawingViewerTool)} . Thus, a simple
 * "vector graphics program" can be written by just creating a Drawing, writing
 * a simple GUI that contains a viewer (or multiple viewers) of that drawing,
 * and providing simple means for the user to interactively activate different
 * tools on the viewer.
 * <p>
 * A DrawingViewer can also be used to provide just a viewer for a Drawing
 * without any interactive editing, just by not ever activating a tool on the
 * viewer. That way, changes to the drawing may only be done programmatically.
 * 
 * @author Olaf Klischat
 */
public class DrawingViewer {

    private static final long serialVersionUID = -5162812267404406219L;

    private Drawing drawing;
    private Map<DrawingObject, DrawingObjectDrawingAdapter> objectDrawingAdapters
        = new IdentityHashMap<DrawingObject, DrawingObjectDrawingAdapter>();
    // invariant: selectedObjects is a subset of drawing.getObjects()
    private Collection<DrawingObject> selectedObjects = new IdentityHashSet<DrawingObject>();

    private AffineTransform objectToDisplayTransform;
    private AffineTransform displayToObjectTransform;
    
    private DrawingViewerBackend backend;

    private final List<DrawingViewerListener> drawingViewerListeners =
        new ArrayList<DrawingViewerListener>();
    
    public void addDrawingViewerListener(DrawingViewerListener l) {
        drawingViewerListeners.add(l);
    }
    
    public void removeDrawingViewerListener(DrawingViewerListener l) {
        drawingViewerListeners.remove(l);
    }

    protected void fireDrawingViewerEvent(DrawingViewerEvent e) {
        for (DrawingViewerListener l : drawingViewerListeners) {
            l.onDrawingViewerEvent(e);
        }
    }
    
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
    
    public Point2D objToDisplay(Point2D pt) {
        return getObjectToDisplayTransform().transform(pt, null);
    }
    
    public Point2D displayToObj(Point2D pt) {
        return getDisplayToObjectTransform().transform(pt, null);
    }

    public void setDrawing(Drawing d) {
        if (null != this.drawing) {
            deactivateCurrentTool();
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

    public Drawing getDrawing() {
        return drawing;
    }
    
    protected void checkDrawingSet() {
        if (null == drawing) {
            throw new IllegalStateException("no Drawing associated with this DrawingVieweriewer");
        }
    }
    
    public void setBackend(DrawingViewerBackend be) {
        if (null != this.backend) {
            this.backend.disconnected();
        }
        this.backend = be;
        if (null != this.backend) {
            this.backend.connected(this);
        }
    }
    
    public DrawingViewerBackend getBackend() {
        return backend;
    }
    
    public List<DrawingObject> getDrawingObjectsAtObjCoord(Point2D pt) {
        checkDrawingSet();
        return drawing.getDrawingObjectsAt(pt);
    }

    public DrawingObject getTopmostDrawingObjectAtObjCoord(Point2D pt) {
        checkDrawingSet();
        return drawing.getTopmostDrawingObjectAt(pt);
    }

    public List<DrawingObject> getDrawingObjectsAtDispCoord(Point2D pt) {
        checkDrawingSet();
        return drawing.getDrawingObjectsAt(displayToObj(pt));
    }

    public DrawingObject getTopmostDrawingObjectAtDispCoord(Point2D pt) {
        checkDrawingSet();
        return drawing.getTopmostDrawingObjectAt(displayToObj(pt));
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

    public DrawingObjectDrawingAdapter getDrawingAdapterFor(DrawingObject drobj) {
        return objectDrawingAdapters.get(drobj);
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
                    objectDrawingAdapters.put(de.getObject(), createDrawingAdapterFor(de.getObject()));
                    repaintObjectArea(de.getObject());
                }
            } else if (e instanceof DrawingObjectRemoveEvent) {
                DrawingObjectRemoveEvent re = (DrawingObjectRemoveEvent) e;
                if (re.isBeforeChange()) {
                    removeFromSelection(re.getObject());
                    repaintObjectArea(re.getObject());
                    objectDrawingAdapters.remove(re.getObject());
                }
            } else if (e instanceof DrawingObjectEvent) {
                DrawingObjectEvent de = (DrawingObjectEvent) e;
                objectDrawingAdapters.get(de.getSource()).onDrawingObjectEvent(de);
            }
        }
    };

    public Collection<DrawingObject> getSelection() {
        return new ArrayList<DrawingObject>(selectedObjects);
    }
    
    public boolean isSelected(DrawingObject drobj) {
        return selectedObjects.contains(drobj);
    }
    
    public void setSelection(Collection<DrawingObject> drobjs) {
        Collection<DrawingObject> toBeUnselected = new IdentityHashSet<DrawingObject>(selectedObjects);
        toBeUnselected.removeAll(drobjs);
        Collection<DrawingObject> toBeSelected = new IdentityHashSet<DrawingObject>(drobjs);
        toBeSelected.removeAll(selectedObjects);
        if (null != drawing) {
            toBeSelected.retainAll(drawing.getObjects());
        }
        if (!toBeUnselected.isEmpty()) {
            fireDrawingViewerEvent(DrawingViewerSelectionChangeEvent.newBeforeObjectRemoveEvent(this, toBeUnselected));
            selectedObjects.removeAll(toBeUnselected);
            repaintObjectAreas(toBeUnselected);
            fireDrawingViewerEvent(DrawingViewerSelectionChangeEvent.newAfterObjectRemoveEvent(this, toBeUnselected));
        }
        if (!toBeSelected.isEmpty()) {
            fireDrawingViewerEvent(DrawingViewerSelectionChangeEvent.newBeforeObjectAddEvent(this, toBeSelected));
            selectedObjects.addAll(toBeSelected);
            repaintObjectAreas(toBeSelected);
            fireDrawingViewerEvent(DrawingViewerSelectionChangeEvent.newAfterObjectAddEvent(this, toBeSelected));
        }
    }

    public void setSelection(DrawingObject drobj) {
        setSelection(Arrays.asList(new DrawingObject[]{drobj}));
    }

    public void addToSelection(Collection<DrawingObject> drobjs) {
        Collection<DrawingObject> newSelection = new IdentityHashSet<DrawingObject>(selectedObjects);
        newSelection.addAll(drobjs);
        setSelection(newSelection);
    }
    
    public void addToSelection(DrawingObject drobj) {
        addToSelection(Arrays.asList(new DrawingObject[]{drobj}));
    }

    public void removeFromSelection(Collection<DrawingObject> drobjs) {
        Collection<DrawingObject> newSelection = new IdentityHashSet<DrawingObject>(selectedObjects);
        newSelection.removeAll(drobjs);
        setSelection(newSelection);
    }

    public void removeFromSelection(DrawingObject drobj) {
        removeFromSelection(Arrays.asList(new DrawingObject[]{drobj}));
    }
    
    public void toggleSelected(DrawingObject drobj) {
        if (isSelected(drobj)) {
            removeFromSelection(drobj);
        } else {
            addToSelection(drobj);
        }
    }
    
    public void clearSelection() {
        setSelection(new ArrayList<DrawingObject>());
    }
    
    public void selectAll() {
        if (null != drawing) {
            setSelection(drawing.getObjects());
        }
    }

    private DrawingViewerTool currentTool;
    
    public void activateTool(DrawingViewerTool t) {
        if (null == t) {
            deactivateCurrentTool();
            return;
        }
        checkDrawingSet();
        if (t.getAssociatedViewer() != null) {
            if (t.getAssociatedViewer() == this) { return; }
            t.getAssociatedViewer().deactivateCurrentTool();
        }
        if (null != currentTool) {
            deactivateCurrentTool();
        }
        currentTool = t;
        t.associateWithViewer(this);
    }

    public void deactivateCurrentTool() {
        if (null != currentTool) {
            currentTool.disassociateFromViewer();
        }
        currentTool = null;
    }

    public void processInputEvent(InputEvent e) {
        int id = e.getID();
        if (e instanceof MouseEvent) {
            MouseEvent me = (MouseEvent) e;
            switch(id) {
            case MouseEvent.MOUSE_PRESSED:
                toolMouseForwarder.mousePressed(me);
                break;
                
            case MouseEvent.MOUSE_RELEASED:
                toolMouseForwarder.mouseReleased(me);
                break;

            case MouseEvent.MOUSE_CLICKED:
                toolMouseForwarder.mouseClicked(me);
                break;
            
            case MouseEvent.MOUSE_EXITED:
                toolMouseForwarder.mouseExited(me);
                break;
            
            case MouseEvent.MOUSE_ENTERED:
                toolMouseForwarder.mouseEntered(me);
                break;
            
            case MouseEvent.MOUSE_MOVED:
                toolMouseForwarder.mouseMoved(me);
                break;
            
            case MouseEvent.MOUSE_DRAGGED:
                toolMouseForwarder.mouseDragged(me);
                break;

            case MouseEvent.MOUSE_WHEEL:
                toolMouseForwarder.mouseWheelMoved((MouseWheelEvent)me);
                break;
            }
        }
    }

    private ToolMouseForwarder toolMouseForwarder = new ToolMouseForwarder();
    
    private class ToolMouseForwarder implements MouseInputListener, MouseWheelListener {

        @Override
        public void mouseClicked(MouseEvent e) {
            if (null != currentTool) {
                currentTool.mouseClicked(e);
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            if (null != currentTool) {
                currentTool.mouseEntered(e);
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {
            if (null != currentTool) {
                currentTool.mouseExited(e);
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (null != currentTool) {
                currentTool.mousePressed(e);
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (null != currentTool) {
                currentTool.mouseReleased(e);
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (null != currentTool) {
                currentTool.mouseDragged(e);
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            if (null != currentTool) {
                currentTool.mouseMoved(e);
            }
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            if (null != currentTool) {
                currentTool.mouseWheelMoved(e);
            }
        }
        
    }
    
    /**
     * schedule repainting of the drawing area covered by dobj
     * 
     * @param dobj
     */
    protected void repaintObjectArea(DrawingObject drobj) {
        Rectangle bounds = getDrawingAdapterFor(drobj).getBounds2DDisp().getBounds();
        repaint(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    protected void repaintObjectAreas(Collection<DrawingObject> drobjs) {
        for (DrawingObject drobj : drobjs) {
            repaintObjectArea(drobj);
        }
    }
    
    protected void repaint() {
        if (backend != null) {
            backend.repaint();
        }
    }
    
    protected void repaint(double x, double y, double width, double height) {
        if (backend != null) {
            backend.repaint(x, y, width, height);
        }
    }
    
    public void paint(Graphics2D g2d) {
        if (drawing == null) { return; }
        for (DrawingObject drobj : drawing.getObjects()) {
            DrawingObjectDrawingAdapter drawingAdapter = objectDrawingAdapters.get(drobj);
            assert drawingAdapter != null;
            Rectangle clip = g2d.getClipBounds();
            if (clip != null && !drawingAdapter.intersectsDisp(clip)) { continue; }
            drawingAdapter.paintObjectOn((Graphics2D) g2d.create());
        }
        // paint the selection visualizations on top of all the objects' outlines themselves
        for (DrawingObject drobj : drawing.getObjects()) {
            DrawingObjectDrawingAdapter drawingAdapter = objectDrawingAdapters.get(drobj);
            assert drawingAdapter != null;
            Rectangle clip = g2d.getClipBounds();
            if (clip != null && !drawingAdapter.intersectsDisp(clip)) { continue; }
            drawingAdapter.paintSelectionVisualizationOn((Graphics2D) g2d.create(), isSelected(drobj));
        }
    }

}

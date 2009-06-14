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
 * 
 * <h1>Coordinate Systems</h1>
 * 
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
 * Some methods that take coordinate values or points as parameters (mostly
 * methods of viewer adapters -- see below) have the name suffix "Disp" if the
 * coordinates are interpreted in the display system, and "Obj" otherwise.
 * 
 * <h1>Display Update</h1>
 * 
 * The DrawingViewer listens to change events of its Drawing, changes to the
 * transformation etc., and redraws its display accordingly. See the section
 * about backends below for how display output actually works.
 * 
 * <h1>Selection</h1>
 * 
 * The DrawingViewer manages a selection, which is defined as the (possibly
 * empty) subset of the drawing's DrawingObjects that are currently "selected"
 * in this viewer. (you can have more than one viewer on the same drawing, in
 * which case each viewer will have its own selection). The fact that an object
 * is selected is normally indicated visually (with a dashed rectangle around it
 * or similar). See the section on drawing adapters to learn who will actually
 * end up drawing these things.
 * 
 * <h1>Per-Object Viewer Adapters</h1>
 * 
 * A DrawingViewer does not itself know how to draw any of the DrawingObjects in
 * its Drawing, nor does it know about things like the objects' bounding boxes
 * (which determine when an object actually needs to be redrawn), where the
 * "interior" of an object is (relevant for mouse hit testing) or what "mouse
 * dragging handles" (see {@link MouseHandle} class) an object provides.
 * Instead, the viewer delegates drawing of the DrawingObjects (including visual
 * feedback of "selected" state etc.) and all the other tasks to special
 * per-object "drawing adapters", which are instances of subclasses of
 * {@link DrawingObjectViewerAdapter}: There is one such adapter per
 * DrawingObject in the Drawing; the adapter knows its DrawingObject. N.B.: The
 * DrawingViewer does not itself perform things like mouse hit testing and
 * dragging of mouse handles. Instead, it delegates these things to its
 * currently activated "drawing viewer tool". See below for tools.
 * <p>
 * It will be possible for users to write their own drawing adapters and
 * register them with the viewer in order to support new DrawingObject types
 * without changing or subclassing the DrawingViewer class.
 * 
 * <h1>Drawing Viewer Events</h1>
 * 
 * A DrawingViewer fires its own set of events (subclasses of
 * {@link DrawingViewerEvent}) for events that are specific to the viewer
 * (rather than the drawing or the DrawingObjects in it). At the moment, this is
 * used for signaling changes to the DrawingViewer's selection (see
 * {@link DrawingViewerSelectionChangeEvent}).
 * 
 * <h1>Multiple Viewers per Drawing</h1>
 * 
 * It is supported to have more than one DrawingViewer on the same drawing, all
 * interoperating seamlessly.
 * 
 * <h1>Drawing Viewer Tools</h1>
 * 
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
 * <h1>Drawing Viewer Backend</h1>
 * 
 * DrawingViewer is not a Java Swing component or other kind of GUI component
 * and thus cannot directly output its graphics onto a UI nor can it directly
 * receive mouse or keyboard events from a GUI. Instead, a
 * {@link DrawingViewerBackend} should be attached to it (using
 * {@link #setBackend(DrawingViewerBackend)}) for doing those things.
 * DrawingViewerBackend is just an interface; you write implementations of it
 * that actually process repaint requests, provide Graphics2D contexts for the
 * viewer to {@link #paint(Graphics2D) paint} onto, and send mouse or keyboard
 * events to the viewer using {@link #processInputEvent(InputEvent)}.
 * <p>
 * Essentially, if the viewer needs to repaint itself, it calls one of the two
 * repaint methods on its backend (either {@link DrawingViewerBackend#repaint()}
 * to request a complete repaint or
 * {@link DrawingViewerBackend#repaint(double, double, double, double)} to
 * request repainting of only some rectangular sub-area of the display
 * coordinate system). The backend should react to this by eventually calling
 * the {@link #paint(Graphics2D)} method, providing a Graphics2D onto which the
 * viewer should paint itself. If the backend is a GUI component, the repaint
 * methods would normally schedule a repaint request, and the viewer's paint
 * method would be called during each paint event processing of the component.
 * <p>
 * For mouse processing, the backend would just feed mouse events to the
 * viewer's {@link #processInputEvent(InputEvent)} method. Depending what event
 * triggers in the viewer (object selection, handle dragging etc.), this might
 * of course in turn cause the viewer to have to repaint some part of itself,
 * initiating the just described chain of events.
 * <p>
 * See the {@link JDrawingViewer} class in the test/demo subpackage for a simple
 * Swing component wrapper for a DrawingViewer.
 * <p>
 * The backend can be changed at any time by just calling
 * {@link #setBackend(DrawingViewerBackend)} repeatedly. The viewer will always
 * repaint itself onto any new backend and use that from then on.
 * <p>
 * It is also possible to set the backend to null, resulting in
 * <strong>Backend-less operation</strong>. During that, the viewer is still
 * fully functional: Tools can be activated on it, the drawing itself may be
 * changed programmatically, it can be fed mouse keyboard events via
 * {@link #processInputEvent(InputEvent)} (which may of course also trigger
 * changes to the drawing or the selection), and it will update its state
 * (selections etc.) as a result of these events. At any time,
 * {@link #paint(Graphics2D)} may be called to let the viewer paint itself onto
 * a Graphics2D; the result will look just as it would if a backend had been
 * attached all the time.
 * 
 * @author Olaf Klischat
 */
public class DrawingViewer {

    private static final long serialVersionUID = -5162812267404406219L;

    private Drawing drawing;
    private Map<DrawingObject, DrawingObjectViewerAdapter> objectDrawingAdapters = new IdentityHashMap<DrawingObject, DrawingObjectViewerAdapter>();
    // invariant: selectedObjects is a subset of drawing.getObjects()
    private Collection<DrawingObject> selectedObjects = new IdentityHashSet<DrawingObject>();

    private AffineTransform objectToDisplayTransform;
    private AffineTransform displayToObjectTransform;

    private DrawingViewerBackend backend;

    private final List<DrawingViewerListener> drawingViewerListeners = new ArrayList<DrawingViewerListener>();

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
                this.objectDrawingAdapters.put(drobj,
                        createDrawingAdapterFor(drobj));
            }
        }
        repaint();
    }

    public Drawing getDrawing() {
        return drawing;
    }

    protected void checkDrawingSet() {
        if (null == drawing) {
            throw new IllegalStateException(
                    "no Drawing associated with this DrawingVieweriewer");
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
        repaint();
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

    protected DrawingObjectViewerAdapter createDrawingAdapterFor(
            DrawingObject drobj) {
        /*
         * TODO: use externally configurable DrawingObject class => adapter
         * class mapping instead of hard-coding the factory method like this
         */
        if (drobj instanceof EllipseObject) {
            return new EllipseObjectViewerAdapter(this, (EllipseObject) drobj);
        } else if (drobj instanceof RectangleObject) {
            return new RectangleObjectViewerAdapter(this,
                    (RectangleObject) drobj);
        } else {
            return new DrawingObjectViewerAdapter(this, drobj);
        }
    }

    public DrawingObjectViewerAdapter getDrawingAdapterFor(DrawingObject drobj) {
        return objectDrawingAdapters.get(drobj);
    }

    private DrawingListener drawingEventHandler = new DrawingListener() {
        @Override
        public void onDrawingEvent(EventObject e) {
            if (e instanceof DrawingObjectAddOrMoveEvent) {
                DrawingObjectAddOrMoveEvent de = (DrawingObjectAddOrMoveEvent) e;
                if (de.isMoved()) {
                    // DrawingObject move in the z order => schedule repainting
                    // of affected area
                    repaintObjectArea(de.getObject());
                } else if (!de.isMoved() && de.isAfterChange()) {
                    // new DrawingObject added to our drawing => create
                    // corresponding adapter, schedule repainting of affected
                    // area
                    objectDrawingAdapters.put(de.getObject(),
                            createDrawingAdapterFor(de.getObject()));
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
                objectDrawingAdapters.get(de.getSource()).onDrawingObjectEvent(
                        de);
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
        Collection<DrawingObject> toBeUnselected = new IdentityHashSet<DrawingObject>(
                selectedObjects);
        toBeUnselected.removeAll(drobjs);
        Collection<DrawingObject> toBeSelected = new IdentityHashSet<DrawingObject>(
                drobjs);
        toBeSelected.removeAll(selectedObjects);
        if (null != drawing) {
            toBeSelected.retainAll(drawing.getObjects());
        }
        if (!toBeUnselected.isEmpty()) {
            fireDrawingViewerEvent(DrawingViewerSelectionChangeEvent
                    .newBeforeObjectRemoveEvent(this, toBeUnselected));
            selectedObjects.removeAll(toBeUnselected);
            repaintObjectAreas(toBeUnselected);
            fireDrawingViewerEvent(DrawingViewerSelectionChangeEvent
                    .newAfterObjectRemoveEvent(this, toBeUnselected));
        }
        if (!toBeSelected.isEmpty()) {
            fireDrawingViewerEvent(DrawingViewerSelectionChangeEvent
                    .newBeforeObjectAddEvent(this, toBeSelected));
            selectedObjects.addAll(toBeSelected);
            repaintObjectAreas(toBeSelected);
            fireDrawingViewerEvent(DrawingViewerSelectionChangeEvent
                    .newAfterObjectAddEvent(this, toBeSelected));
        }
    }

    public void setSelection(DrawingObject drobj) {
        setSelection(Arrays.asList(new DrawingObject[] { drobj }));
    }

    public void addToSelection(Collection<DrawingObject> drobjs) {
        Collection<DrawingObject> newSelection = new IdentityHashSet<DrawingObject>(
                selectedObjects);
        newSelection.addAll(drobjs);
        setSelection(newSelection);
    }

    public void addToSelection(DrawingObject drobj) {
        addToSelection(Arrays.asList(new DrawingObject[] { drobj }));
    }

    public void removeFromSelection(Collection<DrawingObject> drobjs) {
        Collection<DrawingObject> newSelection = new IdentityHashSet<DrawingObject>(
                selectedObjects);
        newSelection.removeAll(drobjs);
        setSelection(newSelection);
    }

    public void removeFromSelection(DrawingObject drobj) {
        removeFromSelection(Arrays.asList(new DrawingObject[] { drobj }));
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
            if (t.getAssociatedViewer() == this) {
                return;
            }
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

    /**
     * Feed an input event (mouse or keyboard event) into this viewer for
     * processing. Normally done by the viewer's current
     * {@link DrawingViewerBackend}, but may also be done by anyone else, even
     * if no backend is currently attached to the viewer. The x/y coordinates of
     * the mouse events must be in display coordinates. The viewer will process
     * the event (forward it on to the currently activated tool (cf.
     * {@link #activateTool(DrawingViewerTool)}), if any), which may result in
     * changes to the drawing objects and the viewer's graphical representation,
     * in which case the viewer will request a repaint of itself by calling one
     * of the repaint() methods on its backend.
     * 
     * @param e
     *            the event
     */
    public void processInputEvent(InputEvent e) {
        int id = e.getID();
        if (e instanceof MouseEvent) {
            MouseEvent me = (MouseEvent) e;
            switch (id) {
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
                toolMouseForwarder.mouseWheelMoved((MouseWheelEvent) me);
                break;
            }
        }
    }

    private ToolMouseForwarder toolMouseForwarder = new ToolMouseForwarder();

    private class ToolMouseForwarder implements MouseInputListener,
            MouseWheelListener {

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
        Rectangle bounds = getDrawingAdapterFor(drobj).getBounds2DDisp()
                .getBounds();
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

    /**
     * Paint the current graphical representation of this viewer onto the given
     * Graphics2D context g2d. The viewer will take the 2D coordinate system of
     * g2d as being identical to its (the viewer's) display coordinate system.
     * In short this means that all the 2D coordinates of the
     * {@link DrawingObject}s in the {@link Drawing} that the viewer displays
     * will be transformed using the viewer's current object-display
     * transformation ( {@link #getObjectToDisplayTransform()} to give g2d's
     * coordinates.
     * <p>
     * This is normally called by the viewer's current
     * {@link DrawingViewerBackend} in response to one of its repaint methods
     * being called, but may also be called by anyone else (even if no backend
     * is currently attached to the viewer), for example to just output a
     * current "snapshot" of the viewer into an image file or to a printer.
     * <p>
     * The methods will not clear the background if g2d; it will just paint all
     * the objects, selection handles, focus lines etc. directly on top of
     * whatever might already have been painted on g2d.
     * 
     * @param g2d
     *            the Graphics2D context to paint on
     */
    public void paint(Graphics2D g2d) {
        if (drawing == null) {
            return;
        }
        for (DrawingObject drobj : drawing.getObjects()) {
            DrawingObjectViewerAdapter drawingAdapter = objectDrawingAdapters
                    .get(drobj);
            assert drawingAdapter != null;
            Rectangle clip = g2d.getClipBounds();
            if (clip != null && !drawingAdapter.intersectsDisp(clip)) {
                continue;
            }
            drawingAdapter.paintObjectOn((Graphics2D) g2d.create());
        }
        // paint the selection visualizations on top of all the objects'
        // outlines themselves
        for (DrawingObject drobj : drawing.getObjects()) {
            DrawingObjectViewerAdapter drawingAdapter = objectDrawingAdapters
                    .get(drobj);
            assert drawingAdapter != null;
            Rectangle clip = g2d.getClipBounds();
            if (clip != null && !drawingAdapter.intersectsDisp(clip)) {
                continue;
            }
            drawingAdapter.paintSelectionVisualizationOn((Graphics2D) g2d
                    .create(), isSelected(drobj));
        }
    }

}

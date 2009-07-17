package de.sofd.draw2d.viewer.tools;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import de.sofd.draw2d.DrawingObject;
import de.sofd.draw2d.viewer.DrawingViewer;

/**
 * Base class for DrawingViewer tools that allow the user to interactively
 * create new objects on the drawing by dragging their bounding box with the
 * mouse. Subclasses need to override only {@link #createNewObject()} and create
 * the desired object there to get a fully functional tool. This class will do
 * everything else, for example, it will call
 * {@link DrawingObject#setLocation(de.sofd.draw2d.Location)} on the new object
 * continuously as the user drags the box.
 * <p>
 * Sets the {@link TagNames#TN_CREATION_COMPLETED} tag whenever a new object
 * has been completed (i.e., the user released the mouse button
 * after dragging open the bounding box).
 * 
 * @author olaf
 */
public abstract class ObjectCreatorByBBoxTool extends DrawingViewerTool {

    protected abstract DrawingObject createNewObject();
    
    /**
     * The object we're creating
     */
    private DrawingObject currentObject;
    
    /**
     * mousePressed sets pt0 of the object's location, mouseDrag/-Release sets
     * pt2.
     */
    private Point2D pt0;  // we create pt0 of the object's location and drag pt2
    
    @Override
    public void associateWithViewer(DrawingViewer viewer) {
        if (currentObject != null) {  // should never happen, but just to be sure...
            getAssociatedViewer().getDrawing().removeDrawingObject(currentObject);
        }
        super.associateWithViewer(viewer);
        currentObject = null;
        pt0 = null;
    }
    
    @Override
    public void disassociateFromViewer() {
        if (currentObject != null) {
            getAssociatedViewer().getDrawing().removeDrawingObject(currentObject);
        }
        super.disassociateFromViewer();
        currentObject = null;
        pt0 = null;
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        if (currentObject == null) {
            if (pt0 == null) {
                Point2D ptInObjCoords = getAssociatedViewer().displayToObj(e.getPoint());
                pt0 = ptInObjCoords;
            } else {
                currentObject = null;
                pt0 = null;
            }
        }
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
        Point2D ptInObjCoords = getAssociatedViewer().displayToObj(e.getPoint());
        if (null == currentObject && null != pt0) {
            currentObject = createNewObject();
            currentObject.setLocation(pt0, ptInObjCoords);
            getAssociatedViewer().getDrawing().addDrawingObject(currentObject);
        } else if (currentObject != null) {
            currentObject.setLocation(pt0, ptInObjCoords);
        }
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
        if (null != currentObject) {
            DrawingObject drobj = currentObject;
            currentObject = null;
            drobj.setTag(TagNames.TN_CREATION_COMPLETED, true);
        }
        pt0 = null;
    }
    
}

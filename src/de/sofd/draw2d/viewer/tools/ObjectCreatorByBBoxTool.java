package de.sofd.draw2d.viewer.tools;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import de.sofd.draw2d.DrawingObject;
import de.sofd.draw2d.viewer.DrawingViewer;

/**
 * Base class for DrawingViewer tools that allow the user to interactively
 * create new objects on the drawing by dragging their bounding box with the
 * mouse. Subclasses need to override only {@link #createNewObject()} and create
 * the desired object there to get a fully functional tool. This class will\ do
 * everything else, for example, it will call
 * {@link DrawingObject#setLocation(de.sofd.draw2d.Location)} on the new object
 * whenever needed.
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
    private Point2D pt0;  // we create pt0 of the 
    
    @Override
    public void associateWithViewer(DrawingViewer viewer) {
        super.associateWithViewer(viewer);
    }
    
    @Override
    public void disassociateFromViewer() {
        super.disassociateFromViewer();
        currentObject = null;
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        Point2D ptInObjCoords = getAssociatedViewer().displayToObj(e.getPoint());
        pt0 = ptInObjCoords;
        currentObject = null;
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
        Point2D ptInObjCoords = getAssociatedViewer().displayToObj(e.getPoint());
        if (null == currentObject) {
            currentObject = createNewObject();
            currentObject.setLocation(pt0, ptInObjCoords);
            getAssociatedViewer().getDrawing().addDrawingObject(currentObject);
        }
        currentObject.setLocation(pt0, ptInObjCoords);
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
    }
    
}

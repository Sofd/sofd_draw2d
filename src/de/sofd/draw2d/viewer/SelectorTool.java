package de.sofd.draw2d.viewer;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import de.sofd.draw2d.DrawingObject;

public class SelectorTool extends DrawingViewerTool {

    private enum State {UNASSOCIATED, IDLE, HANDLE_DRAGGING, SELECTION_DRAGGING};
    private State state = State.UNASSOCIATED;
    
    private MouseHandle draggedHandle = null;
    private Point2D latestSelectionDragPt;

    @Override
    protected void associateWithViewer(DrawingViewer viewer) {
        super.associateWithViewer(viewer);
        state = State.IDLE;
    }
    
    @Override
    protected void disassociateFromViewer() {
        super.disassociateFromViewer();
        state = State.UNASSOCIATED;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (state == State.IDLE) {
            Point ptInDisplayCoords = e.getPoint();
            Point2D ptInObjCoords = getAssociatedViewer().displayToObj(ptInDisplayCoords);
            
            for (DrawingObject drobj : getAssociatedViewer().getSelection()) {
                MouseHandle handle = getAssociatedViewer().getDrawingAdapterFor(drobj).getHandleAt(ptInDisplayCoords);
                if (null != handle) {
                    draggedHandle = handle;
                    state = State.HANDLE_DRAGGING;
                    return;
                }
            }
            
            DrawingObject clickedObj = getAssociatedViewer().getTopmostDrawingObjectAtDispCoord(ptInDisplayCoords);
            if (null == clickedObj) {
                getAssociatedViewer().clearSelection();
                return;
            }
            if (false/*TODO: shift pressed*/) {
                getAssociatedViewer().addToSelection(clickedObj);
            } else {
                getAssociatedViewer().setSelection(clickedObj);
            }
            latestSelectionDragPt = ptInObjCoords;
            state = State.SELECTION_DRAGGING;
        }
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
        Point ptInDisplayCoords = e.getPoint();
        Point2D ptInObjCoords = getAssociatedViewer().displayToObj(ptInDisplayCoords);
        switch (state) {
        
        case HANDLE_DRAGGING:
            try {
                draggedHandle.setPosition(ptInObjCoords);
            } catch (NoSuchMouseHandleException ex) {
                state = State.IDLE;
            }
            break;
            
        case SELECTION_DRAGGING:
            double dx = ptInObjCoords.getX() - latestSelectionDragPt.getX();
            double dy = ptInObjCoords.getY() - latestSelectionDragPt.getY();
            for (DrawingObject drobj : getAssociatedViewer().getSelection()) {
                drobj.moveBy(dx, dy);
            }
            latestSelectionDragPt = ptInObjCoords;
            break;

        }
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
        state = State.IDLE;
    }
    
}

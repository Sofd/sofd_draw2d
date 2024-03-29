package de.sofd.draw2d.viewer.tools;

import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import de.sofd.draw2d.DrawingObject;
import de.sofd.draw2d.viewer.DrawingViewer;
import de.sofd.draw2d.viewer.adapters.MouseHandle;
import de.sofd.draw2d.viewer.adapters.NoSuchMouseHandleException;

public class SelectorTool extends DrawingViewerTool {

    private enum State {UNASSOCIATED, IDLE, HANDLE_DRAGGING, SELECTION_DRAGGING};
    private State state = State.UNASSOCIATED;
    
    private MouseHandle draggedHandle = null;
    private Point2D latestSelectionDragPt;

    @Override
    public void associateWithViewer(DrawingViewer viewer) {
        super.associateWithViewer(viewer);
        state = State.IDLE;
    }
    
    @Override
    public void disassociateFromViewer() {
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
                    e.consume();
                    return;
                }
            }
            
            DrawingObject clickedObj = getAssociatedViewer().getTopmostDrawingObjectAtDispCoord(ptInDisplayCoords);
            if (null == clickedObj) {
                if (!getAssociatedViewer().getSelection().isEmpty()) {
                    getAssociatedViewer().clearSelection();
                    e.consume();
                }
                return;
            }
            if ((e.getModifiersEx() & InputEvent.SHIFT_DOWN_MASK) == InputEvent.SHIFT_DOWN_MASK) {
                getAssociatedViewer().addToSelection(clickedObj);
            } else {
                getAssociatedViewer().setSelection(clickedObj);
            }
            latestSelectionDragPt = ptInObjCoords;
            state = State.SELECTION_DRAGGING;
            e.consume();
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
            e.consume();
            break;
            
        case SELECTION_DRAGGING:
            double dx = ptInObjCoords.getX() - latestSelectionDragPt.getX();
            double dy = ptInObjCoords.getY() - latestSelectionDragPt.getY();
            for (DrawingObject drobj : getAssociatedViewer().getSelection()) {
                drobj.moveBy(dx, dy);
            }
            latestSelectionDragPt = ptInObjCoords;
            e.consume();
            break;

        }
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
        if (state != State.IDLE) {
            state = State.IDLE;
            e.consume();
        }
    }
    
}

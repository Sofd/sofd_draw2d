package de.sofd.draw2d.viewer;

import java.awt.event.MouseEvent;


public class SelectorTool extends DrawingViewerTool {

    private MouseHandle handleBeingDragged = null;
    
    @Override
    protected void disassociateFromViewer() {
        super.disassociateFromViewer();
        handleBeingDragged = null;
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
        handleBeingDragged = null;
    }
    
}

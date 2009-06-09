package de.sofd.draw2d.viewer;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.event.MouseInputListener;

/**
 * Base class for drawing manipulation tools, like vector drawing programs have
 * them (rectangle tool, ellipsis tool, polygon tool, selector tool etc.)
 * <p>
 * Associated with the DrawingViewer to operate on (which may change during the
 * lifetime of the DrawingViewerTool)
 * <p>
 * DrawingViewer and DrawingViewerTool know each other: A DrawingViewerTool is
 * "activated on" a DrawingViewer, at which point it becomes the DrawingViewer's
 * "current tool" (at any one time, there is 0 or 1 current tool on a
 * DrawingViewer). The DrawingViewer is aware of its current tool, if any, and a
 * DrawingViewerTool knows when it is the current tool of a DrawingViewer
 * <p>
 * Defines mouse event listener methods (mousePressed/Released/Clicked/Moved)
 * that are called by the current DrawingViewer of the tool if the corresponding
 * event happens on the DrawingViewer.
 * <p>
 * Subclasses override these methods to implement their specific behaviour, i.e.
 * manipulate the Drawing accordingly
 * <p>
 * Some keyboard event listener methods will also be declared here eventually.
 * <p>
 * You'll probably want to derive your own tools from {@link DrawingViewerTool}
 * rather than completely implementing this interface yourself.
 * <p>
 * DrawingViewerTool itself provides implementations for
 * {@link #associateWithViewer(DrawingViewer)} /
 * {@link #disassociateFromViewer()} / {@link #getAssociatedViewer()} as well as
 * empty implementation of the mouse event callbacks.
 * 
 * @author Olaf Klischat
 */
public abstract class DrawingViewerTool implements MouseInputListener, MouseWheelListener {
    
    private DrawingViewer associatedViewer;

    /**
     * Called by {@link DrawingViewer} when this DrawingViewerTool is newly
     * associated with the viewer. May be called multiple times (the same tool
     * may be associated/disassociated with the same or different viewers at any
     * time).
     * <p>
     * Implementation must make sure the viewer supplied here is returned by
     * {@link #getAssociatedViewer()} afterwards (derive from
     * {@link DrawingViewerTool} to inherit this behaviour).
     * <p>
     * <strong>Caution: </strong> Do not call this method yourself; the
     * DrawingViewer calls it when a tool is being associated with it.
     * <p>
     * DrawingViewerTool's implementation stores the viewer in an internal field
     * that's returned by {@link #getAssociatedViewer()} (so the contract
     * specified above is fulfilled). When overriding, make sure to first call
     * the super implementation to inherit this behaviour.
     * 
     * @param viewer the {@link DrawingViewer} that this tool is being associated with.
     */
    protected void associateWithViewer(DrawingViewer viewer) {
        this.associatedViewer = viewer;
    }

    /**
     * Called by the {@link #getAssociatedViewer()} when this DrawingViewerTool
     * is being disassociated from the {@link DrawingViewer}. May be called
     * multiple times (the same tool may be associated/disassociated with the
     * same or different viewers at any time).
     * <p>
     * Implementation must make sure that {@link #getAssociatedViewer()} returns
     * null immediately after this method was called (derive from
     * {@link DrawingViewerTool} to inherit this behaviour).
     * <p>
     * Also, the implementation should probably forget any state it held with
     * respect to the previously associated viewer (DrawingObject being created,
     * mouse handle being dragged etc.).
     * <p>
     * <strong>Caution: </strong> Do not call this method yourself; the
     * DrawingViewer calls it when its current tool is being disassociated from
     * it.
     * <p>
     * DrawingViewerTool's implementation clears the internal field that's
     * returned by {@link #getAssociatedViewer()}, such that the latter returns
     * null afterwards (as required by the contract specified above). When
     * overriding, make sure to first call the super implementation to inherit
     * this behaviour.
     */
    protected void disassociateFromViewer() {
        this.associatedViewer = null;
    }

    public DrawingViewer getAssociatedViewer() {
        return this.associatedViewer;
    }

    /*
     * TODO: the following methods should better be protected because, just like #associateWithViewer() etc.,
     * they shouldn't be called from the outside.
     */
    
    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
    }

}

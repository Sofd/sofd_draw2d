package de.sofd.draw2d.viewer.backend;

import java.awt.Graphics2D;
import java.awt.event.InputEvent;

import de.sofd.draw2d.viewer.DrawingViewer;

/**
 * Base class for <strong>backends</strong> attached to a {@link DrawingViewer},
 * used for graphical output and mouse event input. At ant time, zero or one
 * backends are attached to a viewer. The communication between a viewer and its
 * backend it bidirectinal: The viewer signals that its display has changed and
 * needs repainting by calling one of the repaint methods on the backend, and
 * the backend may feed mouse/keyboard events into the viewer using
 * {@link DrawingViewer#processInputEvent(InputEvent)}, change the viewer's
 * object-display coordinate transformation by calling
 * {@link DrawingViewer#setObjectToDisplayTransform(java.awt.geom.AffineTransform)}
 * , or doing other things to it.
 * <p>
 * See the section on backends in the Javadoc for the {@link DrawingViewer}
 * class for further details.
 * 
 * @author Olaf Klischat
 */
public interface DrawingViewerBackend {

    /**
     * Called by the DrawingViewer as the first method immediately after this
     * backend has been attached to it.
     * 
     * @param viewer
     *            the viewer. The backend should probably store this in a member
     *            variable for later use.
     */
    void connected(DrawingViewer viewer);
    
    /**
     * Called by the currently attached viewer to signal that its whole display
     * area has changed somehow and needs to be repainted. The backend should
     * call {@link DrawingViewer#paint(Graphics2D)} sometime in the future
     * to let the viewer paint itself.
     */
    void repaint();

    /**
     * Like {@link #repaint()}, but the viewer signals that only some
     * rectangular part of itself (specified in display coordinates) needs
     * repainting, not the whole thing.
     * 
     * @param x
     * @param y
     * @param width
     * @param height
     */
    void repaint(double x, double y, double width, double height);
    
    /**
     * Called by the DrawingViewer as the last method immediately before this
     * backend will detached from it.
     */
    void disconnecting();

}

package de.sofd.draw2d.viewer.tools;

import de.sofd.draw2d.DrawingObject;

/**
 * Well-known names of {@link DrawingObject} tags (see {@link DrawingObject#getTag(java.lang.String) },
 * {@link DrawingObject#setTag(java.lang.String, java.lang.Object)}  etc.) that
 * {@link DrawingViewerTool}s may set on DrawingObjects for specific purposes.
 *
 * @author olaf
 */
public class TagNames {

    private TagNames() {}

    /**
     * Name of a Boolean-valued {@link DrawingObject} tag that {@link DrawingViewerTool}s
     * that create new objects should set to true to indicate that the
     * creation of an object has been completed and the tool has done its
     * work for that object. What this actually means is defined by the tool;
     * e.g. the ellipsis and rectangle tools (in fact, all tools deriving from
     * ObjectCreatorByBBoxTool) set this tag to true as soon
     * as the user has finished dragging open the ellipse with the mouse
     * (i.e., has released the mouse).
     */
    public static final String TN_CREATION_COMPLETED = TagNames.class.getPackage().getName() + ".drawingObjectCreationCompleted";
}

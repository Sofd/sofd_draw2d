package de.sofd.draw2d.viewer.tools;

import de.sofd.draw2d.DrawingObject;
import de.sofd.draw2d.RectangleObject;

public class RectangleTool extends ObjectCreatorByBBoxTool {

    @Override
    protected DrawingObject createNewObject() {
        return new RectangleObject();
    }

}

package de.sofd.draw2d.viewer.tools;

import de.sofd.draw2d.DrawingObject;
import de.sofd.draw2d.EllipseObject;

public class EllipseTool extends ObjectCreatorByBBoxTool {

    @Override
    protected DrawingObject createNewObject() {
        return new EllipseObject();
    }

}

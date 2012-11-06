package de.sofd.draw2d.viewer.event;

import java.util.EventObject;

import de.sofd.draw2d.viewer.DrawingViewer;

public class DrawingViewerEvent extends EventObject {

    private static final long serialVersionUID = 4334964917738100359L;

    public DrawingViewerEvent(DrawingViewer source) {
        super(source);
    }
    
    @Override
    public DrawingViewer getSource() {
        return (DrawingViewer) super.getSource();
    }
    
}

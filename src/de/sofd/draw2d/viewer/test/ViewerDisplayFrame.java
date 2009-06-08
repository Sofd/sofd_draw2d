package de.sofd.draw2d.viewer.test;

import java.awt.BorderLayout;
import java.awt.HeadlessException;
import java.awt.geom.AffineTransform;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.sofd.draw2d.Drawing;
import de.sofd.draw2d.viewer.DrawingViewer;

public class ViewerDisplayFrame extends JFrame {

    private static final long serialVersionUID = 1952563188075659723L;

    private Drawing drawing;
    private DrawingViewer drawingViewer;
    
    public ViewerDisplayFrame(String title, Drawing drawing) throws HeadlessException {
        super(title);
        setDrawing(drawing);
    }

    @Override
    protected void frameInit() {
        super.frameInit();

        drawingViewer = new DrawingViewer();
        
        JToolBar toolbar = new JToolBar("toolbar");
        toolbar.setFloatable(false);
        toolbar.add(new JLabel("Zoom:"));
        final JLabel zoomValueLabel = new JLabel("      ");
        final JSlider zoomSlider = new JSlider(SwingConstants.HORIZONTAL, 20, 400, 100);
        //zoomSlider.setPaintLabels(true);
        toolbar.add(zoomSlider);
        toolbar.add(zoomValueLabel);
        zoomSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                zoomValueLabel.setText(""+zoomSlider.getValue()+"%");
                double scale = (double)zoomSlider.getValue()/100;
                drawingViewer.setObjectToDisplayTransform(AffineTransform.getScaleInstance(scale, scale));
            }
        });

        getContentPane().add(toolbar, BorderLayout.PAGE_START);
        getContentPane().add(drawingViewer, BorderLayout.CENTER);
    }
    
    public void setDrawing(Drawing d) {
        drawingViewer.setDrawing(d);
        this.drawing = d;
    }

    public DrawingViewer getDrawingViewer() {
        return drawingViewer;
    }

}

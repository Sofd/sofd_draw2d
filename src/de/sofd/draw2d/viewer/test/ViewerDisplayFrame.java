package de.sofd.draw2d.viewer.test;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.geom.AffineTransform;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.sofd.draw2d.Drawing;
import de.sofd.draw2d.DrawingObject;
import de.sofd.draw2d.viewer.DrawingViewer;
import de.sofd.draw2d.viewer.EllipseTool;
import de.sofd.draw2d.viewer.ObjectCreatorByBBoxTool;
import de.sofd.draw2d.viewer.RectangleTool;
import de.sofd.draw2d.viewer.SelectorTool;

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
        toolbar.add(new JLabel("ObjNr:"));
        final JSpinner objNrChooser = new JSpinner(new SpinnerNumberModel(0,0,100,1)) {
            @Override
            public Dimension getMaximumSize() {
                return new Dimension(50, super.getMaximumSize().height);
            }
        };
        toolbar.add(objNrChooser);
        toolbar.add(new AbstractAction("toggleSel") {
            @Override
            public void actionPerformed(ActionEvent e) {
                int nr = (Integer)objNrChooser.getValue();
                if (nr >= 0 && nr < drawing.getObjectCount()) {
                    DrawingObject drobj = drawing.get(nr);
                    drawingViewer.toggleSelected(drobj);
                }
            }
        });

        getContentPane().add(toolbar, BorderLayout.PAGE_START);
        getContentPane().add(drawingViewer, BorderLayout.CENTER);
    }
    
    public void setDrawing(Drawing d) {
        drawingViewer.setDrawing(d);
        drawingViewer.activateTool(new SelectorTool());
        //drawingViewer.activateTool(new EllipseTool());
        //drawingViewer.activateTool(new RectangleTool());
        this.drawing = d;
    }

    public DrawingViewer getDrawingViewer() {
        return drawingViewer;
    }

}

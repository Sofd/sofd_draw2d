package de.sofd.draw2d.viewer.test;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;

import javax.swing.AbstractAction;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.sofd.draw2d.Drawing;
import de.sofd.draw2d.DrawingObject;
import de.sofd.draw2d.viewer.tools.EllipseTool;
import de.sofd.draw2d.viewer.tools.RectangleTool;
import de.sofd.draw2d.viewer.tools.SelectorTool;

public class DrawingViewerFrame extends JFrame {

    private static final long serialVersionUID = 1952563188075659723L;

    private Drawing drawing;
    private JDrawingViewer viewer;
    
    private Color currColor = Color.RED;
    private double currZoom = 1.0;
    private double currRot = 0.0;
    
    public DrawingViewerFrame(String title, Drawing drawing) throws HeadlessException {
        super(title);
        setDrawing(drawing);
    }

    private void updateViewerTransform() {
        AffineTransform t = new AffineTransform();
        t.translate(currZoom * 256, currZoom * 256);
        t.rotate(currRot);
        t.scale(currZoom, currZoom);
        t.translate(-256, -256);
        viewer.setObjectToDisplayTransform(t);
    }
    
    @Override
    protected void frameInit() {
        super.frameInit();

        viewer = new JDrawingViewer();
        
        JToolBar toolbar = new JToolBar("toolbar");
        toolbar.setFloatable(false);
        
        toolbar.add(new AbstractAction("Sel") {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewer.activateTool(new SelectorTool());
            }
        });
        toolbar.add(new AbstractAction("Rec") {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewer.activateTool(new RectangleTool() {
                    @Override
                    protected DrawingObject createNewObject() {
                        DrawingObject rect = super.createNewObject();
                        rect.setColor(currColor);
                        return rect;
                    }
                });
            }
        });
        toolbar.add(new AbstractAction("Ell") {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewer.activateTool(new EllipseTool() {
                    @Override
                    protected DrawingObject createNewObject() {
                        DrawingObject ell = super.createNewObject();
                        ell.setColor(currColor);
                        return ell;
                    }
                });
            }
        });
        toolbar.add(new AbstractAction("del") {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (DrawingObject drobj : viewer.getSelection()) {
                    drawing.removeDrawingObject(drobj);
                }
            }
        });
        
        toolbar.add(new JLabel("Zoom:"));
        final JLabel zoomValueLabel = new JLabel("      ");
        final JSlider zoomSlider = new JSlider(SwingConstants.HORIZONTAL, 20, 400, 100);
        //zoomSlider.setPaintLabels(true);
        toolbar.add(zoomSlider);
        toolbar.add(zoomValueLabel);
        zoomSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                zoomValueLabel.setText(""+zoomSlider.getValue()+"% ");
                currZoom = (double)zoomSlider.getValue()/100;
                updateViewerTransform();
            }
        });
        toolbar.add(new JLabel("Rot:"));
        final JLabel rotValueLabel = new JLabel("      ");
        final JSlider rotSlider = new JSlider(SwingConstants.HORIZONTAL, 0, 360, 0);
        toolbar.add(rotSlider);
        toolbar.add(rotValueLabel);
        rotSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                rotValueLabel.setText(""+rotSlider.getValue()+"\u00b0 ");
                currRot = (double)rotSlider.getValue() / 360.0 * 2 * Math.PI;
                updateViewerTransform();
            }
        });
        
        toolbar.add(new JLabel("Col:"));
        final JComboBox colorCombo = new JComboBox(new Object[]{Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.CYAN});
        toolbar.add(colorCombo);
        colorCombo.setEditable(false);
        colorCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currColor = (Color) colorCombo.getModel().getSelectedItem();
                for (DrawingObject drobj : viewer.getSelection()) {
                    drobj.setColor(currColor);
                }
            }
        });
        colorCombo.setRenderer(new DefaultListCellRenderer() {
            private static final long serialVersionUID = -2010584656340119829L;
            @Override
            public Component getListCellRendererComponent(JList list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                Color col = (Color) value;
                JLabel result = new JLabel();
                if (null == col) {
                    result.setText("null   ");
                } else if (col.equals(Color.RED)) {
                    result.setText("red");
                } else if (col == Color.GREEN) {
                    result.setText("green");
                } else if (col == Color.BLUE) {
                    result.setText("blue");
                } else if (col == Color.YELLOW) {
                    result.setText("yellow");
                } else if (col == Color.CYAN) {
                    result.setText("cyan");
                } else {
                    result.setText("xy   ");
                }
                result.setOpaque(true);
                result.setBackground((Color)value);
                // JComboBox will reset the background if this is the selected item :-(
                return result;
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
                    viewer.toggleSelected(drobj);
                }
            }
        });

        getContentPane().add(toolbar, BorderLayout.PAGE_START);
        getContentPane().add(viewer, BorderLayout.CENTER);
    }
    
    public void setDrawing(Drawing d) {
        viewer.setDrawing(d);
        viewer.activateTool(new SelectorTool());
        //drawingViewer.activateTool(new EllipseTool());
        //drawingViewer.activateTool(new RectangleTool());
        this.drawing = d;
    }

    public JDrawingViewer getDrawingViewerComponent() {
        return viewer;
    }

}

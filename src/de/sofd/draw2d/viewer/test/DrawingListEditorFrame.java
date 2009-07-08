package de.sofd.draw2d.viewer.test;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.geom.Point2D;
import java.util.EventObject;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.sofd.draw2d.Drawing;
import de.sofd.draw2d.DrawingObject;
import de.sofd.draw2d.EllipseObject;
import de.sofd.draw2d.Location;
import de.sofd.draw2d.RectangleObject;
import de.sofd.draw2d.event.ChangeRejectedException;
import de.sofd.draw2d.event.DrawingListener;
import de.sofd.draw2d.event.DrawingObjectAddOrMoveEvent;
import de.sofd.draw2d.event.DrawingObjectEvent;
import de.sofd.draw2d.event.DrawingObjectLocationChangeEvent;
import de.sofd.draw2d.event.DrawingObjectRemoveEvent;
import de.sofd.draw2d.event.DrawingObjectTagChangeEvent;
import de.sofd.draw2d.viewer.tools.TagNames;
import java.awt.event.ItemListener;
import java.awt.geom.Rectangle2D;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeListener;

public class DrawingListEditorFrame extends JFrame {

    private static final long serialVersionUID = 1952563188075659723L;

    private Drawing drawing;
    private JList drawingEditorList;
    private DefaultListModel drawingEditorListModel;
    private List<ChangeListener> drawingChangedListeners = new ArrayList<ChangeListener>();
    
    public DrawingListEditorFrame(Drawing drawing) throws HeadlessException {
        this("drawing list editor", drawing);
    }

    public DrawingListEditorFrame(String title, Drawing drawing) throws HeadlessException {
        super(title);
        setDrawing(drawing);
    }

    public void addDrawingChangedListener(ChangeListener l) {
        drawingChangedListeners.add(l);
    }

    public void removeDrawingChangedListener(ChangeListener l) {
        drawingChangedListeners.remove(l);
    }

    protected abstract class SelectedObjectAction extends AbstractAction {
        public SelectedObjectAction(String name) {
            super(name);
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            Object drobj = drawingEditorList.getSelectedValue();
            if (null != drobj) {
                actionPerformed((DrawingObject) drobj);
            }
        }
        abstract void actionPerformed(DrawingObject drobj);
    };
    
    private static void mvLocPt2By(double dx, double dy, DrawingObject drobj) {
        Location loc = new Location(drobj.getLocation());
        Point2D pt2 = loc.getPt2();
        loc.setPt2(pt2.getX() + dx, pt2.getY() + dy);
        drobj.setLocation(loc);
    }

    protected void addCheckboxTo(JToolBar toolbar, String label, final boolean initialState, ItemListener itemListener) {
        final JCheckBox cb = new JCheckBox(label);
        toolbar.add(cb);
        cb.getModel().setSelected(!initialState);
        cb.addItemListener(itemListener);
        // trigger initial changedListener call to run after this event cycle has completed
        // so that in case addCheckboxTo() was called from the constructor or frameInit().
        // the listener runs after the the constructor has completed
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                cb.getModel().setSelected(initialState);
            }
        });
    }

    private DrawingListener xmaxBoundaryEnforcer = new DrawingListener() {
        @Override
        public void onDrawingEvent(EventObject e) {
            if (e instanceof DrawingObjectLocationChangeEvent) {
                DrawingObjectLocationChangeEvent lce = (DrawingObjectLocationChangeEvent) e;
                if (lce.isBeforeChange()) {
                    Rectangle2D lastBounds = lce.getLastLocation().getBounds2D();
                    Rectangle2D newBounds = lce.getNewLocation().getBounds2D();
                    if ((newBounds.getMinX() > lastBounds.getMinX() || newBounds.getMaxX() > lastBounds.getMaxX()) &&
                            (newBounds.getMinX() > 512 || newBounds.getMaxX() > 512)) {
                        throw new ChangeRejectedException("x coordinate getting too large");
                    }
                }
            }
        }
    };
    
    private DrawingListener removeOnCreationHandler = new DrawingListener() {
        @Override
        public void onDrawingEvent(EventObject e) {
            if (e instanceof DrawingObjectTagChangeEvent) {
                DrawingObjectTagChangeEvent tce = (DrawingObjectTagChangeEvent) e;
                if (tce.isAfterChange() && tce.getTagName() == TagNames.TN_CREATION_COMPLETED) {
                    // user has completed creation of a new ROI
                    final DrawingObject newObj = tce.getSource();
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            drawing.removeDrawingObject(newObj);
                        }
                    });
                }
            }
        }
    };

    @Override
    protected void frameInit() {
        super.frameInit();

        drawingEditorListModel = new DefaultListModel();
        drawingEditorList = new JList(drawingEditorListModel);
        //ListCellRenderer cr = new DefaultListCellRenderer();
        drawingEditorList.setCellRenderer(new DrawingObjectListCellRenderer());
        
        JToolBar toolbar = new JToolBar("toolbar");
        toolbar.setFloatable(false);
        toolbar.add(new SelectedObjectAction("x+") {
            @Override
            void actionPerformed(DrawingObject drobj) {
                drobj.moveBy(10, 0);
            }
        });
        toolbar.add(new SelectedObjectAction("x-") {
            @Override
            void actionPerformed(DrawingObject drobj) {
                drobj.moveBy(-10, 0);
            }
        });
        toolbar.add(new SelectedObjectAction("y+") {
            @Override
            void actionPerformed(DrawingObject drobj) {
                drobj.moveBy(0, 10);
            }
        });
        toolbar.add(new SelectedObjectAction("y-") {
            @Override
            void actionPerformed(DrawingObject drobj) {
                drobj.moveBy(0, -10);
            }
        });
        toolbar.add(new SelectedObjectAction("x2+") {
            @Override
            void actionPerformed(DrawingObject drobj) {
                mvLocPt2By(10, 0, drobj);
            }
        });
        toolbar.add(new SelectedObjectAction("x2-") {
            @Override
            void actionPerformed(DrawingObject drobj) {
                mvLocPt2By(-10, 0, drobj);
            }
        });
        toolbar.add(new SelectedObjectAction("y2+") {
            @Override
            void actionPerformed(DrawingObject drobj) {
                mvLocPt2By(0, 10, drobj);
            }
        });
        toolbar.add(new SelectedObjectAction("y2-") {
            @Override
            void actionPerformed(DrawingObject drobj) {
                mvLocPt2By(0, -10, drobj);
            }
        });
        toolbar.add(new SelectedObjectAction("z+") {
            @Override
            void actionPerformed(DrawingObject drobj) {
                int idx = drawing.indexOf(drobj);
                if (idx != -1 && idx < drawing.getObjectCount()-1) {
                    drawing.addDrawingObject(idx + 1, drobj);
                }
            }
        });
        toolbar.add(new SelectedObjectAction("z-") {
            @Override
            void actionPerformed(DrawingObject drobj) {
                int idx = drawing.indexOf(drobj);
                if (idx > 0) {
                    drawing.addDrawingObject(idx - 1, drobj);
                }
            }
        });
        toolbar.add(new SelectedObjectAction("del") {
            @Override
            void actionPerformed(DrawingObject drobj) {
                drawing.removeDrawingObject(drobj);
            }
        });
        toolbar.add(new AbstractAction("+rect") {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawing.addDrawingObject(new RectangleObject(50,50,150,100));
            }
        });
        toolbar.add(new AbstractAction("+ell") {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawing.addDrawingObject(new EllipseObject(50,50,150,100));
            }
        });
        toolbar.add(new AbstractAction("save") {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                if (JFileChooser.APPROVE_OPTION == fc.showSaveDialog(DrawingListEditorFrame.this)) {
                    try {
                        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fc.getSelectedFile()));
                        try {
                            oos.writeObject(drawing);
                        } finally {
                            oos.close();
                        }
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(DrawingListEditorFrame.this,
                                                      "Error when saving the drawing: " + ex.getLocalizedMessage(),
                                                      "Error",
                                                      JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    }
                }
            }
        });
        toolbar.add(new AbstractAction("load") {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                if (JFileChooser.APPROVE_OPTION == fc.showOpenDialog(DrawingListEditorFrame.this)) {
                    try {
                        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fc.getSelectedFile()));
                        try {
                            setDrawing((Drawing)ois.readObject());
                        } finally {
                            ois.close();
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(DrawingListEditorFrame.this,
                                                      "Error when loading the drawing: " + ex.getLocalizedMessage(),
                                                      "Error",
                                                      JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    }
                }
            }
        });
        addCheckboxTo(toolbar, "xmax512", true, new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                JCheckBox src = (JCheckBox) e.getSource();
                if (src.isSelected()) {
                    drawing.addDrawingListener(xmaxBoundaryEnforcer);
                } else {
                    drawing.removeDrawingListener(xmaxBoundaryEnforcer);
                }
            }
        });
        addCheckboxTo(toolbar, "rmOnCreat", false, new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                JCheckBox src = (JCheckBox) e.getSource();
                if (src.isSelected()) {
                    drawing.addDrawingListener(removeOnCreationHandler);
                } else {
                    drawing.removeDrawingListener(removeOnCreationHandler);
                }
            }
        });

        getContentPane().add(toolbar, BorderLayout.PAGE_START);
        getContentPane().add(drawingEditorList, BorderLayout.CENTER);
    }

    protected void reinitEditorList() {
        drawingEditorListModel.clear();
        if (null != drawing) {
            for (DrawingObject drobj : drawing.getObjects()) {
                drawingEditorListModel.addElement(drobj);
            }
        }
    }

    public Drawing getDrawing() {
        return drawing;
    }

    public void setDrawing(Drawing d) {
        if (d == this.drawing) { return; }
        if (null != this.drawing) {
            this.drawing.removeDrawingListener(drawingEventHandler);
            this.drawing.removeDrawingListener(xmaxBoundaryEnforcer);
        }
        this.drawing = d;
        if (null != this.drawing) {
            this.drawing.addDrawingListener(drawingEventHandler);
            // TODO this.drawing.addDrawingListener(xmaxBoundaryEnforcer); if it was in the old one
            // TODO dito for removeOnCreationHandler
        }
        reinitEditorList();
        fireDrawingChanged();
    }

    protected void fireDrawingChanged() {
        for (ChangeListener l : drawingChangedListeners) {
            l.stateChanged(new ChangeEvent(this));
        }
    }

    private static class DrawingObjectListCellRenderer extends DefaultListCellRenderer {
        private static final long serialVersionUID = -2010584656340119829L;
        @Override
        public Component getListCellRendererComponent(JList list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            Component superclassComp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof DrawingObject && superclassComp instanceof JLabel) {
                DrawingObject drobj = (DrawingObject) value;
                JLabel lbl = (JLabel) superclassComp;
                lbl.setText("" + drobj.getClass().getSimpleName() + " @ " + drobj.getLocation());
            }
            return superclassComp;
        }
    };
    
    private DrawingListener drawingEventHandler = new DrawingListener() {
        @Override
        public void onDrawingEvent(EventObject e) {
            if (e instanceof DrawingObjectAddOrMoveEvent) {
                DrawingObjectAddOrMoveEvent de = (DrawingObjectAddOrMoveEvent) e;
                if (de.isAfterChange()) {
                    if (de.isMoved()) {
                        ListSelectionModel lsm = drawingEditorList.getSelectionModel();
                        boolean wasSelected = lsm.isSelectedIndex(de.getOldIndex());
                        Object drobj = drawingEditorListModel.remove(de.getOldIndex());
                        drawingEditorListModel.add(de.getNewIndex(), drobj);
                        if (wasSelected) {
                            lsm.addSelectionInterval(de.getNewIndex(), de.getNewIndex());
                        }
                    } else {
                        drawingEditorListModel.add(de.getNewIndex(), de.getObject());
                    }
                }
            } else if (e instanceof DrawingObjectRemoveEvent) {
                DrawingObjectRemoveEvent re = (DrawingObjectRemoveEvent) e;
                if (re.isAfterChange()) {
                    drawingEditorListModel.remove(re.getIndex());
                }
            } else if (e instanceof DrawingObjectEvent) {
                drawingEditorList.repaint();
                if (e instanceof DrawingObjectTagChangeEvent) {
                    DrawingObjectTagChangeEvent tce = (DrawingObjectTagChangeEvent) e;
                    if (tce.isAfterChange()) {
                        System.out.println("DrawingObject " + tce.getSource() + ": tag " + tce.getTagName() +
                                " changed from " + tce.getLastValue() + " to " + tce.getNewValue());
                    }
                }
            }
        }
    };

}

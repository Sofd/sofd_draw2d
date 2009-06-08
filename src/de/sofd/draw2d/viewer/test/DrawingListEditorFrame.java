package de.sofd.draw2d.viewer.test;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.util.EventObject;

import javax.swing.AbstractAction;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JToolBar;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.sofd.draw2d.Drawing;
import de.sofd.draw2d.DrawingObject;
import de.sofd.draw2d.event.DrawingListener;
import de.sofd.draw2d.event.DrawingObjectAddOrMoveEvent;
import de.sofd.draw2d.event.DrawingObjectEvent;
import de.sofd.draw2d.event.DrawingObjectRemoveEvent;

public class DrawingListEditorFrame extends JFrame {

    private static final long serialVersionUID = 1952563188075659723L;

    private Drawing drawing;
    private JList drawingEditorList;
    private DefaultListModel drawingEditorListModel;
    
    public DrawingListEditorFrame(Drawing drawing) throws HeadlessException {
        this("drawing list editor", drawing);
    }

    public DrawingListEditorFrame(String title, Drawing drawing) throws HeadlessException {
        super(title);
        setDrawing(drawing);
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
    
    @Override
    protected void frameInit() {
        super.frameInit();

        drawingEditorListModel = new DefaultListModel();
        drawingEditorList = new JList(drawingEditorListModel);
        //ListCellRenderer cr = new DefaultListCellRenderer();
        drawingEditorList.setCellRenderer(new DrawingObjectListCellRenderer());
        drawingEditorList.addListSelectionListener(editorListSelectionHandler);
        
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
    
    public void setDrawing(Drawing d) {
        if (null != this.drawing) {
            this.drawing.removeDrawingListener(drawingEventHandler);
        }
        this.drawing = d;
        if (null != this.drawing) {
            this.drawing.addDrawingListener(drawingEventHandler);
        }
        reinitEditorList();
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
                        Object drobj = drawingEditorListModel.remove(de.getOldIndex());
                        drawingEditorListModel.add(de.getNewIndex(), drobj);
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
            }
        }
    };


    private ListSelectionListener editorListSelectionHandler = new ListSelectionListener() {
        @Override
        public void valueChanged(ListSelectionEvent e) {
        }
    };
    
}
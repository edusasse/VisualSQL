package com.edusasse.visualsql.gui.entity;

import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;

import com.edusasse.visualsql.gui.*;
import com.edusasse.visualsql.gui.elements.MyElement;
import com.edusasse.visualsql.gui.elements.OperatorElementFactory;
import com.edusasse.visualsql.gui.elements.TableElement;

import java.awt.dnd.DnDConstants;
import java.awt.datatransfer.*;

public class EntityDropTargetListener implements DropTargetListener {

    private ViewDiagram viewDiagram = null;
    private static final DataFlavor entityDataFlavor = new DataFlavor(Entity.class, Entity.class.getName());
    private OperatorElementFactory oeFactory;

    public EntityDropTargetListener(ViewDiagram viewdiagram) {
        super();
        setViewDiagram(viewdiagram);
        this.oeFactory = new OperatorElementFactory();
    }

    @Override
    public void dragEnter(DropTargetDragEvent event) {
    }

    @Override
    public void dragExit(DropTargetEvent event) {
    }

    @Override
    public void dragOver(DropTargetDragEvent event) {
    }

    @Override
    public void dropActionChanged(DropTargetDragEvent event) {
    }

    @Override
    public void drop(DropTargetDropEvent event) {
        event.acceptDrop(DnDConstants.ACTION_COPY);
        Transferable transferable = event.getTransferable();
        try {
            Entity entity = (Entity) transferable.getTransferData(entityDataFlavor);
            MyElement element;
            /* Se a classe for:
                0..4 -> Operador
                5    -> Tabela */
            if(entity.getClasse()==Classes.TABELA)
                element = new TableElement(viewDiagram.getCurrentDB(),((TableEntity)entity).getEntityName(), (int) event.getLocation().getX(), (int) event.getLocation().getY(), this.viewDiagram);
            else
                element = this.oeFactory.getOperatorElement((int) event.getLocation().getX(), (int) event.getLocation().getY(), entity.getClasse());
            this.viewDiagram.addItem(element);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        event.dropComplete(true);
    }

    public boolean isDropAcceptable(DropTargetDropEvent event) {
        return event.getCurrentDataFlavorsAsList().contains(entityDataFlavor);
    }

    public boolean isDragAcceptable(DropTargetDragEvent event) {
        return event.getCurrentDataFlavorsAsList().contains(entityDataFlavor);
    }

    public ViewDiagram getViewDiagram() {
        return viewDiagram;
    }

    public void setViewDiagram(ViewDiagram viewDiagram) {
        this.viewDiagram = viewDiagram;
    }
}

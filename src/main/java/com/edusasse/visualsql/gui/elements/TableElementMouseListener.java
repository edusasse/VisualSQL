package com.edusasse.visualsql.gui.elements;

import java.awt.Cursor;
import java.awt.MouseInfo;
import java.awt.event.MouseEvent;

import com.edusasse.visualsql.gui.ViewDiagramMouseListener;

public class TableElementMouseListener extends ViewDiagramMouseListener {

    private TableElement tableElement;

    public TableElementMouseListener(TableElement te) {
        super(te.getDiagramPai());
        this.tableElement = te;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        diagram.setCursor(new Cursor(Cursor.MOVE_CURSOR));
        diagram.pressionar(tableElement.getLocation().x, tableElement.getLocation().y, (int) MouseInfo.getPointerInfo().getLocation().getX(), (int) MouseInfo.getPointerInfo().getLocation().getY());
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        diagram.arrastar(0, 0, (int) MouseInfo.getPointerInfo().getLocation().getX(), (int) MouseInfo.getPointerInfo().getLocation().getY());
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        diagram.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        diagram.soltar(tableElement.getLocation().x, tableElement.getLocation().y);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        diagram.mover(tableElement.getMiddlePoint().x, tableElement.getMiddlePoint().y, (int) MouseInfo.getPointerInfo().getLocation().getX(), (int) MouseInfo.getPointerInfo().getLocation().getY());
    }
}

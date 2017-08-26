package com.edusasse.visualsql.gui;

import java.awt.Cursor;
import java.awt.MouseInfo;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class ViewDiagramMouseListener implements MouseListener, MouseMotionListener {

    protected ViewDiagram diagram;

    public ViewDiagramMouseListener(ViewDiagram vd) {
        this.diagram = vd;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        //System.out.println("mouseClicked");
        if (e.getButton() == MouseEvent.BUTTON3) {
            diagram.setAcaoDefault();
        } else {
            if (e.getClickCount() == 2) {
                diagram.editar();
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        //System.out.println("mousePressed");
        diagram.selecionar(e.getX(), e.getY());
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        diagram.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        diagram.soltar(e.getX(), e.getY());
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        
    }

    @Override
    public void mouseExited(MouseEvent e) {
        diagram.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        //System.out.println("mouseDragged");
        diagram.setCursor(new Cursor(Cursor.MOVE_CURSOR));
        diagram.arrastar(e.getX(), e.getY());
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        diagram.mover(e.getX(), e.getY(), (int) MouseInfo.getPointerInfo().getLocation().getX(), (int) MouseInfo.getPointerInfo().getLocation().getY());
    }
}

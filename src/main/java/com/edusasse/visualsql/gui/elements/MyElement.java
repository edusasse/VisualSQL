package com.edusasse.visualsql.gui.elements;

import java.awt.*;
import javax.swing.JPanel;

import com.edusasse.visualsql.gui.GUIController;

@SuppressWarnings("serial")
public abstract class MyElement extends JPanel {
    private final int id;
    
    public MyElement(){
        id = GUIController.getInstance().gimmeOneId();
    }

    public MyElement(int id) {
        // apenas gasta uma id
        GUIController.getInstance().gimmeOneId();
        this.id = id;
    }

    public abstract void desenhar(Graphics g, int x, int y, boolean selecionado);

    public abstract boolean contido(int x, int y);

    public abstract void deslocar(int x, int y);

    public abstract String getKind();

    public int getId(){
        return id;
    }
}	
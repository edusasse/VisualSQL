package com.edusasse.visualsql.gui.actions;

import com.edusasse.visualsql.gui.ViewDiagram;

public abstract class Action {

    private ViewDiagram dono;

    public void setViewDiagram(ViewDiagram dono) {
        this.dono = dono;
    }

    public ViewDiagram getViewDiagram() {
        return dono;
    }

    public abstract void executar(int x, int y);
    public abstract void encerrar(int x, int y);
    public abstract void mover(int x, int y);
    public abstract void mudouAcao();
    public abstract void editar();

}

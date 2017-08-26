package com.edusasse.visualsql.gui.elements;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

public interface LinkedDataElement {
    public abstract Rectangle getRectangle();
    public abstract Point getPoint();
    public abstract Point getMiddlePoint();
    public abstract ArrayList<LineElement> getArcos();
    public abstract void addArco(LineElement arco);
    public abstract void removerArco(LineElement arco);
    public abstract String getQuery();
    public abstract int getClasse();
    public abstract boolean canBeStart();
    public abstract boolean canBeEnd();
    public abstract void restartElement();
}

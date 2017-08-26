package com.edusasse.visualsql.gui.elements;

import com.edusasse.visualsql.gui.entity.Classes;

public class OperatorElementFactory {

    public OperatorElementFactory(){

    }

    public OperatorElement getOperatorElement(int x, int y, int classe){
        if(classe==Classes.PROJECAO)
            return new ProjectionOperatorElement(x,y);
        if(classe==Classes.SELECAO)
            return new SelectionOperatorElement(x,y);
        if(classe==Classes.PRODUTOCARTESIANO)
            return new CartesianProductOperatorElement(x,y);
        if(classe==Classes.UNIAO)
            return new UnionOperatorElement(x,y);
        if(classe==Classes.AGRUPAMENTO)
            return new AgroupmentOperatorElement(x,y);
        return null;
    }

}

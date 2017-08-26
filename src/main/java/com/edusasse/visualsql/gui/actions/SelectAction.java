package com.edusasse.visualsql.gui.actions;

import java.sql.SQLException;
import java.util.*;

import com.edusasse.visualsql.gui.GUIController;
import com.edusasse.visualsql.gui.ViewDiagram;
import com.edusasse.visualsql.gui.elements.LinkedDataElement;
import com.edusasse.visualsql.gui.elements.MyElement;
import com.edusasse.visualsql.gui.elements.OperatorElement;

public class SelectAction extends Action {

    private int ultPosX,ultPosY;

    @Override
    public void executar(int x, int y) {
        ViewDiagram dono = getViewDiagram();
        int totalItems = dono.qtosItems();
        MyElement item = null;
        ultPosX = x;
        ultPosY = y;
        //remove selecionados anteoriormente
        dono.removerSelecionados();
        for (int i = 0; i < totalItems; i++) {
            item = dono.getItem(i);
            if (item.contido(x,y)){
                dono.addSelecionado(item);
                try {
                    GUIController.getInstance().showTableData(((LinkedDataElement) item).getQuery());
                } catch (SQLException ex) {
                    //jah mostra erro no metodo da GUIController
                }
                return;
            }
        }
    }

    @Override
    public void encerrar(int x, int y) {
    }

    @Override
    public void mover(int x, int y) {
        ArrayList<MyElement> selecionados = getViewDiagram().getSelecionados();
        int difX, difY;

        difX = x - ultPosX;
        difY = y - ultPosY;

        ultPosX = x;
        ultPosY = y;

        MyElement item;
        for (int i = 0; i < selecionados.size(); i++) {
                item = (MyElement) selecionados.get(i);
            item.deslocar(difX, difY);
        }
    }

    @Override
    public void mudouAcao() {
    }

    @Override
    public void editar(){
        //pegar o itens selecionados do diagrama
        ViewDiagram dono = getViewDiagram();
        MyElement item = null;
        for (int i = 0; i < dono.getSelecionados().size(); i++) {
            item = (MyElement) dono.getSelecionados().get(i);
            //se item for um OperatorElement
            if(item instanceof OperatorElement)
                //chamar metodo de configuracao do elemento
                ((OperatorElement)item).configOperator(null);
        }
    }
}

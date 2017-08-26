package com.edusasse.visualsql.gui.actions;

import java.util.ArrayList;

import com.edusasse.visualsql.gui.ViewDiagram;
import com.edusasse.visualsql.gui.elements.*;

public class LineAction extends Action {

    //atributos
    private LinkedDataElement nohIni;
    private LineElement arcoAtual;

    @Override
    public void executar(int x, int y) {
        //remover selecionados
        getViewDiagram().removerSelecionados();
        //de acordo com o noh inicial da linha
        if (nohIni == null) {
            nohIni = findNoh(x,y);
            if (nohIni == null) {
                return;
            }
            arcoAtual = new LineElement(nohIni);
            nohIni.addArco(arcoAtual);
            getViewDiagram().addItem(arcoAtual);
            getViewDiagram().addSelecionado((MyElement) nohIni);
        } else {
            LinkedDataElement noh = findNoh(x,y);
            //nao pode selecionar destino e origem iguais
            if (noh == null || noh == nohIni) {
                return;
            }
            arcoAtual.setNohFinal(noh);
            noh.addArco(arcoAtual);
            arcoAtual = null;
            nohIni = null;
            getViewDiagram().addSelecionado((MyElement) noh);
        }
    }

    @Override
    public void encerrar(int x, int y) {
    }

    @Override
    public void mover(int x, int y) {
    }

    @Override
    public void mudouAcao() {
        if (nohIni != null) {
            nohIni.removerArco(arcoAtual);
            getViewDiagram().removerItem(arcoAtual);
            arcoAtual = null;
            nohIni = null;
        }
    }

    @Override
    public void editar() {
    //faz nada
    }

    private LinkedDataElement findNoh(int x, int y) {
        ViewDiagram pg = getViewDiagram();
        int totalItems = pg.qtosItems();
        MyElement item;
        for (int i = 0; i < totalItems; i++) {
            item = pg.getItem(i);
            if (item.contido(x,y)) {
                //se arco atual esta nullo
                if(this.arcoAtual==null){
                    //esta criando o arco
                    if(((LinkedDataElement) item).canBeStart()){
                        return (LinkedDataElement) item;
                    }
                }
                else{
                    //esta fechando o arco
                    if(((LinkedDataElement) item).canBeEnd()){
                        return (LinkedDataElement) item;
                    }
                }
            }
        }
        return null;
    }
}

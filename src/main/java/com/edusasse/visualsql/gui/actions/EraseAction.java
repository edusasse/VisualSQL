package com.edusasse.visualsql.gui.actions;

import java.util.ArrayList;

import com.edusasse.visualsql.gui.GUIController;
import com.edusasse.visualsql.gui.ViewDiagram;
import com.edusasse.visualsql.gui.elements.LineElement;
import com.edusasse.visualsql.gui.elements.LinkedDataElement;
import com.edusasse.visualsql.gui.elements.MyElement;

public class EraseAction extends Action {

    @Override
    public void executar(int x, int y) {
        ViewDiagram dono = getViewDiagram();
        int totalItems = dono.qtosItems();
        MyElement item = null;
        dono.removerSelecionados();
        for (int i = 0; i < totalItems; i++) {
            item = dono.getItem(i);
            if (item.contido(x,y)) {
                //variavel para saber se ira excluir ou nao
                boolean canDelete = true;
                //busca dos arcos do elemento
                LinkedDataElement lde = (LinkedDataElement) item;
                ArrayList<LineElement> arcos = lde.getArcos();
                LineElement arco;
                //var para guardar arcos fim do elemento
                ArrayList<LineElement> arcosFim = new ArrayList<LineElement>();
                //para cada arco
                for(int j = 0; j < arcos.size(); j++){
                    //arco atual
                    arco = arcos.get(j);
                    //verifica se o elemento eh nohInicial de algum destes arcos
                    if(arco.getNohInicial()==lde){
                        canDelete = false;
                        break;
                    }else{
                        //guarda esse arco
                        arcosFim.add(arco);
                    }
                }
                //se puder excluir
                if(canDelete){
                    //Busca os arcos guardados anteriormente
                    for(int j = 0; j < arcosFim.size(); j++){
                        //arco atual
                        arco = arcos.get(j);
                        //arco se exclui dos operadores onde este eh noh
                        arco.getNohInicial().removerArco(arco);
                        arco.getNohFinal().removerArco(arco);
                        //se excluir do diagrama
                        dono.removerItem(arco);
                    }
                    //item encontrado remover-se do diagrama
                    dono.removerItem(item);
                }
                else{
                    //avisar ao usuario que nao pode
                    GUIController.getInstance().showMessageDialog("Voce nao pode excluir este Elemento, exclua\nos elementos que ele esta referenciando antes.",2);
                }
                //sair
                return;
            }
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

    }

    @Override
    public void editar(){
        
    }
}

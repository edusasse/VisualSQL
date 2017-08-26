package com.edusasse.visualsql.gui.elements;

import java.sql.SQLException;
import java.util.ArrayList;

import com.edusasse.visualsql.gui.GUIController;
import com.edusasse.visualsql.gui.entity.Classes;

@SuppressWarnings("serial")
public class CartesianProductOperatorElement extends OperatorElement {

    //Restricao
    private String restrict = "";

    //criar o operador de produto cartesiano
    public CartesianProductOperatorElement(double x, double y) {
        super(x, y, GUIController.getInstance().getOperatorImage(Classes.PRODUTOCARTESIANO));
    }

    //criar o operador de produto cartesiano
    public CartesianProductOperatorElement(int id, double x, double y) {
        super(id, x, y, GUIController.getInstance().getOperatorImage(Classes.PRODUTOCARTESIANO));
    }

    //retorno da classe
    @Override
    public int getClasse() {
        return Classes.PRODUTOCARTESIANO;
    }

    //ajuste da restricao
    public void setRestrict(String restrict) {
        this.restrict = restrict;
    }

    //retorno da restricao
    public String getRestrict() {
        return this.restrict;
    }

    @Override
    public String getSourceQuery() {
        return "";
    }

    //funcao para retornar a coluna sem o prefixo
    private String removePrefix(String argS) {
        return argS.substring(argS.indexOf(".", 0) + 1);
    }

    @Override
    public String getQuery() {
        //query
        String query = "";
        //busca dos nohs onde esse operador eh o nohFinal
        ArrayList<LineElement> arcosTmp = this.getSourceArcos();
        //se existir pelo menos dois
        if (arcosTmp.size() == 2) {
            //Busca das queryes
            String query1 = arcosTmp.get(0).getNohInicial().getQuery();
            String query2 = arcosTmp.get(1).getNohInicial().getQuery();
            //Busca do array das colunas das querys
            String[] col1 = GUIController.getInstance().getQueryColumns("R" + ((MyElement) arcosTmp.get(0).getNohInicial()).getId(), query1);
            String[] col2 = GUIController.getInstance().getQueryColumns("R" + ((MyElement) arcosTmp.get(1).getNohInicial()).getId(), query2);
            //colunas total
            String colunas = new String();
            for (int j = 0; j < col1.length; j++) {
                colunas += "," + col1[j];
            }
            for (int j = 0; j < col2.length; j++) {
                //Para evitar colunas duplicadas
                //pesquisa nas outras colunas se jah existe 
                //uma igual a esta
                for (int k = 0; k < col1.length; k++) {
                    if (removePrefix(col1[k]).equals(removePrefix(col2[j]))) {
                        //Adicionar um alias para evitar erro
                        //de coluna duplicada
                        col2[j] = col2[j] + " as " + removePrefix(col2[j]) + "_";
                        break;
                    }
                }
                colunas += "," + col2[j];
            }
            //Se o tamanho for zero
            if (!colunas.equals("")) {
                colunas = colunas.substring(1);
            } else {
                return "";
            //montagem das clausulas
            }
            String subSelect = " SELECT " + colunas + " FROM (" + query1 + ") R" + ((MyElement) arcosTmp.get(0).getNohInicial()).getId() + ",(" + query2 + ") R" + ((MyElement) arcosTmp.get(1).getNohInicial()).getId();
            if (!this.getRestrict().equals("")) {
                subSelect += " WHERE " + this.getRestrict();
            //montagem da query
            }
            query = "SELECT R" + this.getId() + ".*" + " FROM( " + subSelect + " ) R" + this.getId();
        }
        return query;
    }

    @Override
    public void configOperator(LineElement arcoAtual) {
        //so abre a janela se este operador foi fechado por dois arcos
        if (this.getSourceArcos().size() < 2) {
            //sair, nao deve abrir a janela de configuracoes
            return;
        }
        //Se o arco foi passado, tem que validar para commitar 
        //o mesmo no operador
        if (arcoAtual != null) {
            //Se a query deste operador retornar nulla
            if (this.getQuery().equals("")) {
                //Nao conseguiu gerar o sql
                //deve remover o arco dos nohs
                arcoAtual.getNohFinal().removerArco(arcoAtual);
                arcoAtual.getNohInicial().removerArco(arcoAtual);
                //remover o arco do diagrama
                GUIController.getInstance().getCurrentViewDiagram().removerItem(arcoAtual);
            }
            else{
                try {
                    //testar a execucao do sql
                    GUIController.getInstance().showTableData(this.getQuery());
                } catch (SQLException ex) {
                    //em qualquer erro remover o arco dos nohs
                    arcoAtual.getNohFinal().removerArco(arcoAtual);
                    arcoAtual.getNohInicial().removerArco(arcoAtual);
                    //remover o arco do diagrama
                    GUIController.getInstance().getCurrentViewDiagram().removerItem(arcoAtual);
                }
            }
        }
    }

    @Override
    public boolean canBeStart() {
        //pode iniciar aqui caso possua dois arcos fechados
        if (this.getSourceArcos().size() < 2) {
            //GUIController.getInstance().showMessageDialog("Este operador ainda nao esta pronto!");
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean canBeEnd() {
        //procedimento exclusivo para uni?o e Produto Cartesiano
        //pois estes operadores podem ser fechar at? dois arcos
        if (this.getSourceArcos().size() < 2) {
            return true;
        } else {
            //GUIController.getInstance().showMessageDialog("Este operador ja foi fechado!");
            return false;
        }
    }

    @Override
    public String getKind() {
        return "CartesianProductOperatorElement";
    }

    @Override
    public void restartElement() {
    }
}

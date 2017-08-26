package com.edusasse.visualsql.gui.elements;

import java.sql.SQLException;

import com.edusasse.visualsql.gui.GUIController;
import com.edusasse.visualsql.gui.entity.Classes;

@SuppressWarnings("serial")
public class UnionOperatorElement extends OperatorElement {

    //criar o operador de uniao
    public UnionOperatorElement(double x, double y) {
        super(x, y, GUIController.getInstance().getOperatorImage(Classes.UNIAO));
    }

    //criar o operador de uniao
    public UnionOperatorElement(int id, double x, double y) {
        super(id, x, y, GUIController.getInstance().getOperatorImage(Classes.UNIAO));
    }

    //retorno da classe
    @Override
    public int getClasse() {
        return Classes.UNIAO;
    }

    @Override
    public String getSourceQuery() {
        return "";
    }

    @Override
    public String getQuery() {
        //query
        String query = "";
        //somente continuar se este operador
        //esta fechado por dois arcos
        if (this.getSourceArcos().size() == 2) {
            //Busca das queryes
            String query1 = this.getSourceArcos().get(0).getNohInicial().getQuery();
            String query2 = this.getSourceArcos().get(1).getNohInicial().getQuery();
            //Busca do array das colunas das querys
            String[] col1 = GUIController.getInstance().getQueryColumns("R" + this.getId(), query1);
            String[] col2 = GUIController.getInstance().getQueryColumns("R" + this.getId(), query2);
            if (col1.length != col2.length) {
                GUIController.getInstance().showMessageDialog("Erro de compatibilidade de Uniao!",0);
                return "";
            }
            //varrer coluna por coluna das queries
            //String colunas = "";
            //Busca do array dos tipos das colunas das querys
            int[] colType1 = GUIController.getInstance().getQueryColumnsType(query1);
            int[] colType2 = GUIController.getInstance().getQueryColumnsType(query2);
            for (int i = 0; i < col1.length; i++) {
                //validar se o tipo das duas colunas eh igual
                if (colType1[i] != colType2[i]) {
                    GUIController.getInstance().showMessageDialog("Erro de compatibilidade de Uniao!",0);
                    return "";
                }
            //validou, entao guardar o nome da coluna na primeira query
            //colunas += ",R"+this.getId()+"."+col1[i];
            }
            //retirar a primeira casa da string de colunas
            //if(colunas.length()>0)
            //	colunas = colunas.substring(1);
            //montagem da consulta
            query = "SELECT R" + this.getId() + ".*" + "  FROM(" + query1 + "       UNION " + "       " + query2 + "      ) R" + this.getId();
        }
        //setar a query no pai
        return query;
    }

    @Override
    public void configOperator(LineElement arcoAtual) {
        //se o arcoAtual foi passado
        if (arcoAtual != null) {
            //apenas se o operador possuir duas fontes de dados
            if (this.getSourceArcos().size() == 2) {
                try {
                    //Se a query deste operador retornar nulla
                    if (this.getQuery().equals("")) {
                        //Nao conseguiu gerar o sql
                        //deve remover o arco dos nohs
                        arcoAtual.getNohFinal().removerArco(arcoAtual);
                        arcoAtual.getNohInicial().removerArco(arcoAtual);
                        //remover o arco do diagrama
                        GUIController.getInstance().getCurrentViewDiagram().removerItem(arcoAtual);
                    } else {
                        //testar a execucao do sql de uniao
                        GUIController.getInstance().showTableData(this.getQuery());
                    }
                } catch (SQLException ex) {
                    //em qualquer erro
                    //remover o arco dos nohs
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
        //procedimento exclusivo para uniao e Produto Cartesiano
        //pois estes operadores podem ser fechar ate dois arcos
        if (this.getSourceArcos().size() < 2) {
            return true;
        } else {
            //GUIController.getInstance().showMessageDialog("Este operador ja foi fechado!");
            return false;
        }
    }

    @Override
    public String getKind() {
        return "UnionOperatorElement";
    }

    @Override
    public void restartElement() {
    }
}

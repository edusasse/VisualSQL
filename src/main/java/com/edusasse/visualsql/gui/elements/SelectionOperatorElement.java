package com.edusasse.visualsql.gui.elements;

import com.edusasse.visualsql.gui.GUIController;
import com.edusasse.visualsql.gui.elementsconfig.SelectionConfig;
import com.edusasse.visualsql.gui.entity.Classes;

@SuppressWarnings("serial")
public class SelectionOperatorElement extends OperatorElement {

    //Restricao
    private String restrict = "";

    //criar o operador de selecao
    public SelectionOperatorElement(double x, double y){
        super(x,y,GUIController.getInstance().getOperatorImage(Classes.SELECAO));
    }

     //criar o operador de selecao
    public SelectionOperatorElement(int id,double x, double y){
        super(id, x,y,GUIController.getInstance().getOperatorImage(Classes.SELECAO));
    }

    //retorno da classe
    @Override
    public int getClasse() {
        return Classes.SELECAO;
    }

    //ajuste da restricao
    public void setRestrict(String restrict){
        this.restrict = restrict;
    }

    //retorno da restricao
    public String getRestrict(){
        return this.restrict;
    }

    @Override
    public String getQuery() {
        //query
        String query = "";
        //somente continuar se este operador
        //esta fechado por algum arco
        if(this.getSourceArcos().size()>0){
            //busca da query de origem
            String queryRelation = this.getSourceQuery();
            //Se query de origem nao for nulla
            if(!queryRelation.equals("")){
                //adicionar parenteses para transformar a query em uma sub-query
                queryRelation = "("+queryRelation+") R"+this.getId();
                //montagem da consulta
                query = "SELECT R"+this.getId()+".* FROM "+queryRelation+" WHERE "+ this.getRestrict();
            }
        }
        //setar a query no pai
        return query;
    }

    @Override
    public void configOperator(LineElement arcoAtual) {
        //se o arcoAtual nao foi passado
        if(arcoAtual==null){
            //ou o operador nao foi fechado ainda, ou esta editando
            //entao so abre a janela se este operador foi fechado por um arco
            if(this.getSourceArcos().size()==0){
                //sair, nao deve abrir a janela de configuracoes
                return;
            }
        }
        //criar uma janela de configuracao do projecao
        SelectionConfig jpc = new SelectionConfig(GUIController.getInstance().getMain().findParentFrame(), true, this);
        //chamar janela para efetuar a configuracao
        jpc.setVisible(true);
        //verificar o resultado do dialogo
        if(!jpc.getModalResult()){
            //se o arcoAtual foi passado, est?? criando o arco
            if(arcoAtual!=null){
                //remover o arco dos nohs
                arcoAtual.getNohFinal().removerArco(arcoAtual);
                arcoAtual.getNohInicial().removerArco(arcoAtual);
                //remover o arco do diagrama
                GUIController.getInstance().getCurrentViewDiagram().removerItem(arcoAtual);
            }
        }
    }

    @Override
    public String getKind() {
        return "SelectionOperatorElement";
    }

    @Override
    public void restartElement() {
        this.restrict = "";
    }

}

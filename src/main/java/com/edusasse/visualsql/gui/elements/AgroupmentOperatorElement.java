package com.edusasse.visualsql.gui.elements;

import java.util.ArrayList;

import com.edusasse.visualsql.gui.GUIController;
import com.edusasse.visualsql.gui.elementsconfig.AgroupmentConfig;
import com.edusasse.visualsql.gui.entity.Classes;

@SuppressWarnings("serial")
public class AgroupmentOperatorElement extends OperatorElement {
	
    //Lista de colunas da projecao
    private ArrayList<ColumnElement> projectionList;
    //Lista de colunas do agrupamento
    private ArrayList<ColumnElement> agroupmentList;
	
    //criar o operador de projecao
    public AgroupmentOperatorElement(double x, double y){
        super(x,y,GUIController.getInstance().getOperatorImage(Classes.AGRUPAMENTO));
        //criar o array das colunas
        this.projectionList = new ArrayList<ColumnElement>();
        this.agroupmentList = new ArrayList<ColumnElement>();
    }

    public AgroupmentOperatorElement(int id, double x, double y){
        super(id,x,y,GUIController.getInstance().getOperatorImage(Classes.AGRUPAMENTO));
        //criar o array das colunas
        this.projectionList = new ArrayList<ColumnElement>();
        this.agroupmentList = new ArrayList<ColumnElement>();
    }

    public ArrayList<ColumnElement> getProjectionList(){
        return this.projectionList;
    }

    public void setProjectionList(ArrayList<ColumnElement> list){
        this.projectionList = list;
    }
    
    public ArrayList<ColumnElement> getAgroupmentList(){
        return this.agroupmentList;
    }

    public void setAgroupmentList(ArrayList<ColumnElement> list){
        this.agroupmentList = list;
    }    
    
    //retorno da classe
    @Override
    public int getClasse() {
        return Classes.AGRUPAMENTO;
    }

    @Override
    public String getQuery() {
        String query = "";
        //somente continuar se este operador
        //esta fechado por algum arco
        if(this.getSourceArcos().size()>0){
            //busca da query de origem
            String queryRelation = this.getSourceQuery();
            //Se query de origem nao for nulla
            if(!queryRelation.equals("")){
                //adicionar parenteses para transformar a query
                //em uma sub-query
                queryRelation = "("+queryRelation+") R"+this.getId();
                //agrupamentos
                String groupBy = "";
                //para cada coluna projetada
                for(int i = 0; i < this.projectionList.size(); i++){
                    //query += ", R"+this.getId()+"."+projectionList.get(i).getColumn();
                	query += ", "+projectionList.get(i).getColumn();
                	if(!projectionList.get(i).getAlias().equals("")){
                        query += " as " + projectionList.get(i).getAlias();
                    }
                    groupBy += ", "+projectionList.get(i).getColumn();
                }
                //para cada coluna agrupada
                for(int i = 0; i < this.agroupmentList.size(); i++){
                    query += ", "+agroupmentList.get(i).getColumn();
                    if(!agroupmentList.get(i).getAlias().equals("")){
                        query += " as " + agroupmentList.get(i).getAlias();
                    }
                }
                //retira primera virgula
                if(!query.equals(""))
                	query = query.substring(1);
                if(!groupBy.equals(""))
                	groupBy = groupBy.substring(1);
                //montagem da consulta
                query = "SELECT" 
                	  + query 
                	  + " FROM "
                	  + queryRelation
                	  + " GROUP BY "
                	  + groupBy;
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
            //entao soh abre a janela se este operador eh o final de outro
            if(this.getSourceArcos().size()==0){
                //sair, nao deve abrir a janela de configuracoes
                return;
            }
        }
        //criar uma janela de configuracao do projecao
        AgroupmentConfig jpc = new AgroupmentConfig(GUIController.getInstance().getMain().findParentFrame(), true, this);
        //chamar janela para efetuar a configuracao
        jpc.setVisible(true);
        //verificar o resultado do dialogo
        //se for falso eh pq foi cancelado
        if(!jpc.getModalResult()){
            //se o arcoAtual foi passado, esta criando o arco
            //e devesse remover
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
        return "AgroupmentOperatorElement";
    }

    @Override
    public void restartElement() {
    	this.projectionList = new ArrayList<ColumnElement>();
    	this.agroupmentList = new ArrayList<ColumnElement>();
    }
}

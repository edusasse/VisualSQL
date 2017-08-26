
package com.edusasse.visualsql.gui.elements;

public class ColumnElement {

    //atributos
    private String column;
    private String alias;

    public ColumnElement(String col, String alias){
        this.column = col;
        this.alias = alias;
    }

    public void setProjection(String col){
        this.column = col;
    }

    public void setAlias(String alias){
        this.alias = alias;
    }

    public String getColumn(){
        return this.column;
    }

    public String getAlias(){
        return this.alias;
    }

}

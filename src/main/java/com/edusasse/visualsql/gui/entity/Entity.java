package com.edusasse.visualsql.gui.entity;

public class Entity implements Classes {

    private int classe;

    /** Creates a new instance of Entity */
    public Entity(int classe){
        this.classe = classe;
    }

    @Override
    public String toString(){
        return CLASS_NAME[this.classe];
    }

    public int getClasse(){
        return classe;
    }

}

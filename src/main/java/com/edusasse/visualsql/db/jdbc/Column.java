/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edusasse.visualsql.db.jdbc;

/**
 *
 * @author Eduardo
 */
public class Column {

    private int pos;
    private String name;
    private String type;

    public Column(int pos, String name, String type) {
        this.pos = pos;
        this.name = name;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public int getPos() {
        return pos;
    }

    public String getName() {
        return name;
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edusasse.visualsql.db.jdbc;

import java.util.HashMap;

/**
 *
 * @author Eduardo
 */
public class Table {

    private HashMap<String, Column> columns;
    private String type;
    private String tbName;
    private String schema;

    public Table(String type,String schema,String tbName) {
        this.type = type;
        this.schema = schema;
        this.tbName = tbName;
        columns = new HashMap<String, Column>();
    }

    public String getTbName() {
        return tbName;
    }

    public String getType() {
        return type;
    }

    public void setColumns(HashMap<String, Column> columns) {
        this.columns = columns;
    }

    public Column[] getColumns() {
        return columns.values().toArray(new Column[columns.size()]);
    }

    public Column getColumn(String key) {
        return this.columns.get(key);
    }

    public void addColumn(Column c) {
        this.columns.put(c.getName(), c);
    }

    public String getSchema() {
        return schema;
    }

    @Override
    public String toString() {
        return this.tbName;
    }



    
}

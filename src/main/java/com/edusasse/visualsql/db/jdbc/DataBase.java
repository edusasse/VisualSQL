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
public class DataBase {

    private String dbName;
    private HashMap<String, HashMap<String,Table>> tables;

    public DataBase(String dbName) {
        this.tables = new HashMap<String, HashMap<String,Table>>();
        this.dbName = dbName;

    }
 public boolean contains(String type, String schema,String tbName) {
         if (this.tables.get(type) == null)
             return false;

        if (this.tables.get(type).get((schema.equals("") ? "" : schema+".")+tbName) == null)
            return false;

         return true;
    }
    public void addTable(String type, Table t) {
         if (this.tables.get(type) == null)
             this.tables.put(type, new HashMap<String,Table>());

        this.tables.get(type).put((t.getSchema().equals("") ? "" : t.getSchema()+".")+t.getTbName(), t);
    }

    public HashMap<String, Table> getTables(int type) {
        return this.tables.get(type);
    }

    public String getDbName() {
        return dbName;
    }

    public HashMap<String, HashMap<String, Table>> getTables() {
        return tables;
    }

    @Override
    public String toString() {
        return dbName;
    }


    
}

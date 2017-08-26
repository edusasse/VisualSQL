package com.edusasse.visualsql.gui.entity;

public class TableEntity extends Entity{
    
    private String schema = "";
    private String entityName = "";

    /** Creates a new instance of Entity */
    public TableEntity(int classe, String schema, String entityName) {
        super(classe);
        this.schema = schema;
        this.entityName = entityName;
    }
    
    public String toString(){
        return getEntityName();
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }
    
}

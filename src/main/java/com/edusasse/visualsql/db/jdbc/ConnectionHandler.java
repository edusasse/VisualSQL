package com.edusasse.visualsql.db.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;

public class ConnectionHandler {

    public final int CATALOGS = 1;
    public final int TABLES = 2;
    public final int COLUMNS = 3;
    public final int TYPES = 4;
    private boolean savePassword = false;
    private boolean autoConnectOnOpen = false;
    private boolean autoSaveOnExit = true;
    private boolean loadMetaDataOnConnect = true;
    private Connection connection;
    private Hashtable<String, DataBase> metacache;
    private String key;
    private String url;
    private String driverClass;
    private Properties info;

    public void setSavePassword(boolean savePassword) {
        this.savePassword = savePassword;
    }

    public boolean isLoadMetaDataOnConnect() {
        return loadMetaDataOnConnect;
    }

    public void setLoadMetaDataOnConnect(boolean loadMetaDataOnConnect) {
        this.loadMetaDataOnConnect = loadMetaDataOnConnect;
    }

    public ConnectionHandler(String key, String url, boolean loadMetaData) {
        this.key = key;
        this.url = url;
        this.metacache = new Hashtable<String, DataBase>();
        this.setLoadMetaDataOnConnect(loadMetaDataOnConnect);


    }

    public void loadMetaData(boolean loadMetaData) {
        if (loadMetaData && connection != null) {
            this.loadCatalogs();
            this.loadTables();
        }
    }

    public void disconnect() {

        this.connection = null;

    }

    public void connect() {
        if (this.connection == null) {
            try {
                this.connection = ConnController.getDriver(this.getDriverClass()).connect(this.url, this.info);
                this.loadMetaData(this.isLoadMetaDataOnConnect());
            } catch (ClassNotFoundException ex) {
                ;// Implementar
            } catch (SQLException ex) {
                ;// Implementar
            }
        }
    }

    public void setInfo(Properties info) {
        this.info = info;
    }

    public Properties getInfo() {
        return info;
    }

    public void setDriverClass(String driverClass) {
        this.driverClass = driverClass;
    }

    public String getUrl() {
        return url;
    }

    public String getDriverClass() {
        return driverClass;
    }

    private void loadCatalogs() {
        try {
            ResultSet rs = connection.getMetaData().getCatalogs();

            while (rs.next()) {
                String name = rs.getString(1).trim();
                if (!this.metacache.contains(name)) {
                    this.metacache.put(name, new DataBase(name));
                }
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    //   metacache.put(TABLES, catalogs);
    }

    private void loadTables() {

        Iterator<DataBase> i = this.metacache.values().iterator();
        while (i.hasNext()) {
            loadTables(i.next());
        }

        loadTableColumns();
    }

    private void loadTableColumns() {

        Iterator<DataBase> i = this.metacache.values().iterator();
        while (i.hasNext()) {

            DataBase db = i.next();

            Iterator<HashMap<String, Table>> c = db.getTables().values().iterator();
            while (c.hasNext()) {
                Iterator<Table> t = c.next().values().iterator();
                while (t.hasNext()) {
                    this.loadTableColumns(db, t.next());
                }
            }
        }
    }

    public Table getTable(DataBase db, String tabName) {
        if (db == null || tabName == null || db.getTables().get("TABLE") == null) {
            return null;
        }
        Iterator<Table> t = db.getTables().get("TABLE").values().iterator();
        while (t.hasNext()) {
            Table x = t.next();
            if (x.getTbName().equals(tabName)) {
                return x;
            }
        }
        return null;
    }

    public Table[] getTableList(DataBase db) {


        if (db == null || db.getTables().get("TABLE") == null) {
            return null;
        }
        Table[] t = new Table[db.getTables().get("TABLE").values().size()];
        return db.getTables().get("TABLE").values().toArray(t);
    }

    public Column[] getTableColumnList(DataBase db, Table tab) {
        return tab.getColumns();
    }

    public void loadTables(DataBase db) {

        try {
            connection.setCatalog(db.getDbName());
            ResultSet rs = connection.getMetaData().getTables(db.getDbName(), null, null, null);

            while (rs.next()) {
                Table t = new Table(rs.getString(4).trim(), (rs.getString(2) == null ? "" : rs.getString(2).trim()), rs.getString(3).trim());
                if (!this.metacache.get(db.getDbName()).contains(t.getType(), t.getSchema(), t.getTbName())) {
                    this.metacache.get(db.getDbName()).addTable(t.getType(), t);
                    //System.out.println(db.getDbName() + " = " + t.getType() + " : " + (t.getSchema().equals("") ? "" : t.getSchema() + ".") + t.getTbName());
                }
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadTableColumns(DataBase db, Table t) {
        try {
            ResultSet rs = connection.getMetaData().getColumns(db.getDbName(), t.getSchema(), t.getTbName(), null);

            while (rs.next()) {

                t.addColumn(new Column(0, rs.getString(4).trim(), rs.getString(6).trim()));
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void close() throws SQLException {
        connection.close();
    }

    public Object getObject(String key) {
        return metacache.get(key);
    }

    public Object[] getMetacache() {
        return metacache.keySet().toArray();
    }

    @Override
    public String toString() {
        return key;
    }

    public String getKey() {
        return key;
    }

    /**
     * @return the savePassword
     */
    public boolean isSavePassword() {
        return savePassword;
    }

    /**
     * @return the autoConnectOnOpen
     */
    public boolean isAutoConnectOnOpen() {
        return autoConnectOnOpen;
    }

    /**
     * @param autoConnectOnOpen the autoConnectOnOpen to set
     */
    public void setAutoConnectOnOpen(boolean autoConnectOnOpen) {
        this.autoConnectOnOpen = autoConnectOnOpen;
    }

    /**
     * @return the autoSaveOnExit
     */
    public boolean isAutoSaveOnExit() {
        return autoSaveOnExit;
    }

    /**
     * @param autoSaveOnExit the autoSaveOnExit to set
     */
    public void setAutoSaveOnExit(boolean autoSaveOnExit) {
        this.autoSaveOnExit = autoSaveOnExit;
    }
}
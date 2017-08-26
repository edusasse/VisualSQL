/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edusasse.visualsql.gui.connstore;

/**
 *
 * @author Eduardo
 */
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.edusasse.visualsql.db.jdbc.ConnController;

public class ConnStore {

    private static ConnStore cs = null;

    public static ConnStore getInstance() {
        if (cs == null) {
            cs = new ConnStore();
        }
        return cs;
    }

    private ConnStore() {
    }

    private void load(String filename) throws ClassNotFoundException, Exception {
        String linha = null;
        BufferedReader br;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
        } catch (Exception exception) {
            return;
        }
        String[] s = new String[9];
        while ((linha = br.readLine()) != null) {

            if (linha.trim().startsWith("[") && linha.trim().endsWith("]")) {
                s[0] = linha.substring(1, linha.trim().length() - 1);
            } else if (linha.trim().startsWith("classname=")) {
                s[1] = linha.trim().replace("classname=", "");
            } else if (linha.trim().startsWith("url=")) {
                s[2] = linha.trim().replace("url=", "");
            } else if (linha.trim().startsWith("user=")) {
                s[3] = linha.trim().replace("user=", "");
            } else if (linha.trim().startsWith("passwd=")) {
                s[4] = linha.trim().replace("passwd=", "");
            } else if (linha.trim().startsWith("saveOnExit=")) {
                s[5] = linha.trim().replace("saveOnExit=", "");
            } else if (linha.trim().startsWith("autoConnectOnOpen=")) {
                s[6] = linha.trim().replace("autoConnectOnOpen=", "");
            } else if (linha.trim().startsWith("savePassword=")) {
                s[7] = linha.trim().replace("savePassword=", "");
            } else if (linha.trim().startsWith("savePassword=")) {
                s[8] = linha.trim().replace("loadMetaDataOnConnect=", "");
            } else if (linha.trim().equals(";")) {
                Hashtable<String, Object> par = new Hashtable<String, Object>();
                par.put("AUTO_SAVE_ON_EXIT", new Boolean(s[5]));
                par.put("AUTO_CONNECT_ON_OPEN", new Boolean(s[6]));
                par.put("SAVE_PASSWORD", new Boolean(s[7]));
                par.put("LOAD_META_DATA", new Boolean(s[8]));
                ConnController.open(s[1], s[0], s[2], s[3], s[4], par);
                s = new String[9];
            }
        }
    }

    private void save(String filename) throws IOException, SQLException {
        Writer out = new FileWriter(filename);
        String my = "";
        for (int i = 0; i < ConnController.getConnectionHandlers().length; i++) {
            if (ConnController.getHandler((String) ConnController.getConnectionHandlers()[i]).isAutoSaveOnExit() == true) {
                my += "[" + ConnController.getHandler((String) ConnController.getConnectionHandlers()[i]).getKey() + "]" + "\n";
                my += " classname=" + ConnController.getHandler((String) ConnController.getConnectionHandlers()[i]).getDriverClass() + "\n";
                my += " url=" + ConnController.getHandler((String) ConnController.getConnectionHandlers()[i]).getUrl() + "\n";
                my += " user=" + ConnController.getHandler((String) ConnController.getConnectionHandlers()[i]).getInfo().getProperty("user") + "\n";
                if (ConnController.getHandler((String) ConnController.getConnectionHandlers()[i]).isSavePassword()) {
                    my += " passwd=" + ConnController.getHandler((String) ConnController.getConnectionHandlers()[i]).getInfo().getProperty("password") + "\n";
                } else {
                    my += " passwd=" + "\n";
                }
                my += " saveOnExit=" + ConnController.getHandler((String) ConnController.getConnectionHandlers()[i]).isAutoSaveOnExit() + "\n";
                my += " autoConnectOnOpen=" + ConnController.getHandler((String) ConnController.getConnectionHandlers()[i]).isAutoConnectOnOpen() + "\n";
                my += " savePassword=" + ConnController.getHandler((String) ConnController.getConnectionHandlers()[i]).isSavePassword() + "\n";
                my += " loadMetaDataOnConnect=" + ConnController.getHandler((String) ConnController.getConnectionHandlers()[i]).isLoadMetaDataOnConnect() + "\n";

                my += ";\n";
            }
        }
        out.write(my);
        out.flush();
        out.close();

    }

    public void save() {

        try {
            String filename = System.getProperty("user.home") + "/.visualsql";
            try {


                this.save(filename);

            } catch (SQLException ex) {
                Logger.getLogger(ConnStore.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IOException e) {
            ;//e.printStackTrace();
        }
    }

    public void load() {
        try {
            String filename = System.getProperty("user.home") + "/.visualsql";
            this.load(filename);
        } catch (IOException ex) {
            Logger.getLogger(ConnStore.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ConnStore.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(ConnStore.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

package com.edusasse.visualsql.db.jdbc;

import java.sql.Connection;
import java.sql.Driver;
import java.util.Hashtable;
import java.util.Properties;

public class ConnController {
    // Mapa de drivers

    private static Hashtable drivers = new Hashtable();
    // Mapa de conexoes
    private static Hashtable connections = new Hashtable();

    // Conexao
    public static ConnectionHandler open(String keycad, String keycah, String url, String uid, String pwd, Hashtable par) throws Exception {
        Properties info = new Properties();

        if (uid != null) {
            info.put("user", uid);
        }
        if (pwd != null) {
            info.put("password", pwd);
        }

        ConnectionHandler ch = new ConnectionHandler(keycah, url, ((Boolean) par.get("LOAD_META_DATA")).booleanValue());
        connections.put(keycah, ch);
        
        // Parametros
        ch.setAutoSaveOnExit(((Boolean) par.get("AUTO_SAVE_ON_EXIT")).booleanValue());
        ch.setAutoConnectOnOpen(((Boolean) par.get("AUTO_CONNECT_ON_OPEN")).booleanValue());
        ch.setSavePassword(((Boolean) par.get("SAVE_PASSWORD")).booleanValue());
        ch.setInfo(info);
        ch.setDriverClass(keycad);

        // Iniciando a coneccao
        Connection cd = null;
        if (((Boolean) par.get("AUTO_CONNECT_ON_OPEN")).booleanValue() == true) {
            ch.connect();
        }

        return ch;
    }

    public static Driver getDriver(String keycad) throws ClassNotFoundException {
        Driver d = null;
        try {
            Class c = Class.forName(((MyDriver) drivers.get(keycad)).getClassname());
            d = (Driver) c.newInstance();

        } catch (InstantiationException ex) {
            ;
        } catch (IllegalAccessException ex) {
            ;
        }
        return d;
    }

    public static boolean hasHandler(String keycah) {
        return keycah == null ? false : connections.containsKey(keycah);
    }

    public static ConnectionHandler getHandler(String keycah) {
        return (ConnectionHandler) connections.get(keycah);
    }

    public static void removeHandler(String keycah) {
        connections.remove(keycah);
    }

    @SuppressWarnings("unchecked")
	public static String declare(MyDriver mydriver, String keycad) throws Exception {
        if (!drivers.containsKey(keycad)) {
            drivers.put(keycad, mydriver);
        }

        return new String(keycad);
    }

    public static Object[] getConnectionHandlers() {
        return ConnController.connections.keySet().toArray();
    }

    public static MyDriver getDrivers(String key) {
        return (MyDriver) drivers.get(key);
    }

    public static Object[] getDeclaredDriversKeys() {

        return ConnController.drivers.keySet().toArray();


    }
}
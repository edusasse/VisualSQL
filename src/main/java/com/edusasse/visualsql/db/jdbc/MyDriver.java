package com.edusasse.visualsql.db.jdbc;

public class MyDriver {

    private String name;
    private String classname;
    private String example;
    private String message;


    public String getKey() {
        return name + "@" + classname;
    }

    public String toString() {
        return classname;
    }

    public String getName() {
        return name;
    }

    public String getMessage() {
        return message;
    }

    public String getExample() {
        return example;
    }

    public String getClassname() {
        return classname;
    }

    public static void loadDefaults() {
        MyDriver[] drivers = new MyDriver[10];

        drivers[0] = new MyDriver();
        drivers[0].name = "ODBC Bridge";
        drivers[0].classname = "sun.jdbc.odbc.JdbcOdbcDriver";
        drivers[0].example = "jdbc:odbc:<data source name>";

        drivers[1] = new MyDriver();
        drivers[1].name = "Apache Derby";
        drivers[1].classname = "org.apache.derby.jdbc.ClientDriver";
        drivers[1].example = "jdbc:derby:net://<host>:<port1527>/<databaseName>";

        drivers[2] = new MyDriver();
        drivers[2].name = "HSQLDB - Embedded";
        drivers[2].classname = "org.hsqldb.jdbcDriver";
        drivers[2].example = "jdbc:hsqldb:<database>";

        drivers[3] = new MyDriver();
        drivers[3].name = "HSQLDB - Server";
        drivers[3].classname = "org.hsqldb.jdbcDriver";
        drivers[3].example = "jdbc:hsqldb:hsql://<host>:<port>";

        drivers[4] = new MyDriver();
        drivers[4].name = "jTDS - SQL Server";
        drivers[4].classname = "net.sourceforge.jtds.jdbc.Driver";
        drivers[4].example = "jdbc:jtds:sqlserver://<server>[:<port>][/<database>]";

        drivers[5] = new MyDriver();
        drivers[5].name = "jTDS - Sybase";
        drivers[5].classname = "net.sourceforge.jtds.jdbc.Driver";
        drivers[5].example = "jdbc:jtds:sybase://<server>[:<port>][/<database>]";

        drivers[6] = new MyDriver();
        drivers[6].name = "MySQL";
        drivers[6].classname = "com.mysql.jdbc.Driver";
        drivers[6].example = "jdbc:mysql://<host>:<port3306>/<database>";

        drivers[7] = new MyDriver();
        drivers[7].name = "Oracle Thin";
        drivers[7].classname = "oracle.jdbc.OracleDriver";
        drivers[7].example = "jdbc:oracle:thin@<host>:<port1521>:<SID>";

        drivers[8] = new MyDriver();
        drivers[8].name = "Oracle OCI";
        drivers[8].classname = "oracle.jdbc.OracleDriver";
        drivers[8].example = "jdbc:oracle:oci@<host>:<port1521>:<SID>";

        drivers[9] = new MyDriver();
        drivers[9].name = "PostgreSQL";
        drivers[9].classname = "org.postgresql.Driver";
        drivers[9].example = "jdbc:postgresql://<host>:<port5432>/<database>";

        for (int i = 0; i < drivers.length; i++) {
            try {
                ConnController.declare(drivers[i], drivers[i].classname);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}
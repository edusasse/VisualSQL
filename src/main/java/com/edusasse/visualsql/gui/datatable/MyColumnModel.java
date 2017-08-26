/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edusasse.visualsql.gui.datatable;

import java.awt.FontMetrics;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;

@SuppressWarnings("serial")
public class MyColumnModel extends DefaultTableColumnModel {

    private static int charMaxSize;
    //private static int numberMaxSize;
    private ResultSet rs = null;

    public MyColumnModel(FontMetrics fm) {
        charMaxSize = fm.charWidth('M');
        //numberMaxSize = fm.charWidth('0');
    }

    public void setResultSet(ResultSet rs) throws SQLException {
        this.rs = rs;
        // Atualiza as Columnas
        columnUpd();
    }

    public void columnUpd() throws SQLException {
        if (this.rs == null) {
            return;
        }
        while (super.getColumnCount() > 0) {
            super.removeColumn(super.getColumn(0));
        }

        TableColumn colindex = new TableColumn();

        int tam = charMaxSize * Util.getRowCount(rs);
        if (tam < 10) {
            tam = 10;
        }
        tam = 34;
        colindex.setWidth(tam);
        colindex.setMaxWidth(tam);
        colindex.setMinWidth(tam);
        colindex.setCellRenderer(new MyCellRenderer());
        colindex.setHeaderValue("*");
        colindex.setResizable(false);
        colindex.setModelIndex(0);

        super.addColumn(colindex);

        for (int i = 0; i < this.rs.getMetaData().getColumnCount(); i++) {
            TableColumn col = new TableColumn();
            final String a = this.rs.getMetaData().getColumnLabel(i + 1);
            int maxColumnLenght = a.length() + (charMaxSize);
            while (rs.next()) {
                if (a.length() > rs.getString(i + 1).length() + (charMaxSize)) {
                    maxColumnLenght = a.length();
                }
            }
            rs.absolute(1);
            col.setWidth(charMaxSize * maxColumnLenght);
            col.setMaxWidth(charMaxSize * maxColumnLenght);
            col.setCellRenderer(new MyCellRenderer());
            col.setHeaderValue(this.rs.getMetaData().getColumnLabel(i + 1));
            col.setResizable(false);
            col.setModelIndex(i + 1);
            super.addColumn(col);
        }
    }
}

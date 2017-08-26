/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.edusasse.visualsql.gui.datatable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Eduardo
 */
public class Util {

    public static int getRowCount(ResultSet rs) {
        try {
            int rowCount;
            int currentRow = rs.getRow();
            rowCount = rs.last() ? rs.getRow() : 0;
            if (currentRow == 0) {
                rs.beforeFirst();
            } else {
                rs.absolute(currentRow);
            }
            return rowCount;
        } catch (SQLException ex) {
            Logger.getLogger(MyTableModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }
}

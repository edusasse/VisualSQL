package com.edusasse.visualsql.gui.datatable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

@SuppressWarnings("serial")
public class MyTableModel extends  AbstractTableModel {

    private ResultSet rs = null;

    public MyTableModel() {

    }
    public void setResultSet(ResultSet rs) {
        this.rs = rs;
    }

    @Override
    public int getRowCount() {
        return Util.getRowCount(rs);
    }

    @Override
    public int getColumnCount() {
        try {
            if (rs == null)
                return 0;
            return rs.getMetaData().getColumnCount() +1;
        } catch (SQLException ex) {
            ;//
        }
        return 0;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        try {
            if (rs.absolute(rowIndex + 1)) {
                if (columnIndex == 0)
                    return rs.getRow();
                else
                    return rs.getObject(columnIndex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(MyTableModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }


    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        ;
    }

    @Override
    public void addTableModelListener(TableModelListener l) {
        ;//throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeTableModelListener(TableModelListener l) {
        ;//throw new UnsupportedOperationException("Not supported yet.");
    }
}

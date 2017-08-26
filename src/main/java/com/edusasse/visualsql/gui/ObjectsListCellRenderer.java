package com.edusasse.visualsql.gui;

import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

@SuppressWarnings("serial")
public class ObjectsListCellRenderer extends DefaultListCellRenderer {
    
    static ImageIcon objectIcon = null;

    /** Creates a new instance of ObjectsListCellRenderer */
    public ObjectsListCellRenderer() {
        super();
        if (objectIcon == null) {
           objectIcon = new javax.swing.ImageIcon(getClass().getResource("/images/database_table.png"));
        }
    }

    @Override
    public java.awt.Component getListCellRendererComponent(javax.swing.JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        java.awt.Component retValue;
        retValue = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        ((JLabel) retValue).setIcon(objectIcon);
        return retValue;
    }

}
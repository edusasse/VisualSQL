package com.edusasse.visualsql.gui.datatable;

import java.awt.Color;
import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

@SuppressWarnings("serial")
public class MyCellRenderer extends DefaultTableCellRenderer {

    /** Creates a new instance of CadastroClienteRenderer */
    public MyCellRenderer() {
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component o = null;

        if (column == 0) {
            JButton bt = new JButton(String.valueOf(value));
             float[] a = new float[3];
            a = Color.RGBtoHSB(48,81,101,a);
            bt.setBackground(Color.getHSBColor(a[0],a[1],a[2]));
            bt.setForeground(Color.WHITE);
            bt.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY)));
            o = bt;
        } else {
            JLabel lb = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
              lb.setForeground(Color.BLACK);
            if (row % 2 == 0) {
                lb.setBackground(Color.lightGray);
            } else {
                lb.setBackground(Color.white);
            }
            o = lb;
        }
        if (isSelected && column > 0) {
            o.setForeground(Color.WHITE);
            o.setBackground(Color.DARK_GRAY);
        }
        return o;
    }
}

package com.edusasse.visualsql.gui.elementsconfig;

import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

import com.edusasse.visualsql.gui.elements.ColumnElement;

@SuppressWarnings("serial")
public class ColumnElementConfigTableModel extends AbstractTableModel {
	
	//tipo do tableModel
	private int tipo;
    //Lista de colunas 
    private ArrayList<ColumnElement> columnList;

    public ColumnElementConfigTableModel(int tipo, ArrayList<ColumnElement> columnList) {
        this.tipo = tipo;
    	this.columnList = columnList;
    }

    public ColumnElement getColumnElement(int idx){
        return(this.columnList.get(idx));
    }

    @Override
    public int getRowCount() {
        return this.columnList.size();
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        ColumnElement proj = this.columnList.get(rowIndex);
        if(columnIndex==0)
            return proj.getColumn();
        if(columnIndex==1)
            return proj.getAlias();
        return null;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return (false);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return (getValueAt(0, columnIndex).getClass());
    }

    @Override
    public String getColumnName(int column) {
        if (column == 0){
            if(tipo == 1)
            	return ("Projecao");
            else
            	return ("Agrupamento");
        }    
        if (column == 1)
            return ("Apelido");
        return ("");
    }
}

package com.edusasse.visualsql.gui;

import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

import com.edusasse.visualsql.db.jdbc.ConnController;
import com.edusasse.visualsql.db.jdbc.DataBase;
import com.edusasse.visualsql.db.jdbc.Table;
import com.edusasse.visualsql.gui.entity.Classes;
import com.edusasse.visualsql.gui.entity.TableEntity;

public class MyListModel implements ListModel {

    @Override
    public int getSize() {
        String connKey = GUIController.getInstance().getCurrentViewDiagram().getConnKey();
        String curDB = GUIController.getInstance().getCurrentViewDiagram().getCurrentDB();
         
        if (ConnController.getHandler(connKey) == null) {
            return 0;
        }
        Table[] a = ConnController.getHandler(connKey).getTableList((DataBase) ConnController.getHandler(connKey).getObject(curDB));
        if (a == null) {
            return 0;
        } else {
            return a.length;
        }

    }

    @Override
    public Object getElementAt(int index) {
        String connKey = GUIController.getInstance().getCurrentViewDiagram().getConnKey();
        String curDB = GUIController.getInstance().getCurrentViewDiagram().getCurrentDB();
        TableEntity entity;
        Table[] r = ConnController.getHandler(connKey).getTableList((DataBase) ConnController.getHandler(connKey).getObject(curDB));
        entity = new TableEntity(Classes.TABELA, "", r[index].getTbName());
        return entity;
    }

    @Override
    public void addListDataListener(ListDataListener l) {
    }

    @Override
    public void removeListDataListener(ListDataListener l) {
    }
}

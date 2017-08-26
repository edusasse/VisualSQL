/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.edusasse.visualsql.gui.conntree;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import com.edusasse.visualsql.db.jdbc.ConnController;
import com.edusasse.visualsql.db.jdbc.ConnectionHandler;

/**
 *
 * @author Eduardo
 */
public class MyTreeModel implements TreeModel {

    protected MyTreeRoot root;

    public MyTreeModel(MyTreeRoot root) {
        this.root = root;
    }


    public Object getRoot() {
        return root;
    }


    public boolean isLeaf(Object node) {

        if (node instanceof MyTreeRoot || node instanceof ConnectionHandler) {
            return false;
        }
        return true;
    }


    public int getChildCount(Object parent) {
        if (parent instanceof MyTreeRoot) {
            return ConnController.getConnectionHandlers().length;
        }
        if (parent instanceof ConnectionHandler) {
            return ConnController.getHandler(parent.toString()).getMetacache().length;
        }
        return 1;
    }

    public Object getChild(Object parent, int index) {

        if (parent instanceof MyTreeRoot) {
            return ConnController.getHandler(ConnController.getConnectionHandlers()[index].toString());
        }
        if (parent instanceof ConnectionHandler) {
            return ConnController.getHandler(parent.toString()).getMetacache()[index];
        }
        return null;
    }


    public int getIndexOfChild(Object parent, Object child) {
        if (parent instanceof MyTreeRoot) {
            for (int i = 0; i < ConnController.getConnectionHandlers().length; i++) {
                //if (((String) child).equals(ConnController.getConnectionHandlers()[i].toString())) {
                    return i;
               // }
            }
            if (ConnController.getConnectionHandlers().length == 0) {
                return -1;
            }
        }
        if (parent instanceof ConnectionHandler) {
            for (int i = 0; i < ConnController.getHandler(parent.toString()).getMetacache().length; i++) {
                if (((String) child).equals(ConnController.getHandler(parent.toString()).getMetacache()[i].toString())) {
                    return i;
                }
            }
            if (ConnController.getHandler(parent.toString()).getMetacache().length == 0) {
                return -1;
            }
        }

        return -1;
    }


    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {

    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {

    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {

    }

}

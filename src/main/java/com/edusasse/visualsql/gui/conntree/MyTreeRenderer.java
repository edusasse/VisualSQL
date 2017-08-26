package com.edusasse.visualsql.gui.conntree;

import java.awt.Component;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import com.edusasse.visualsql.db.jdbc.ConnectionHandler;

@SuppressWarnings("serial")
public class MyTreeRenderer extends DefaultTreeCellRenderer {

    public MyTreeRenderer() {
    }

    public Component getTreeCellRendererComponent(
            JTree tree,
            Object value,
            boolean sel,
            boolean expanded,
            boolean leaf,
            int row,
            boolean hasFocus) {

        super.getTreeCellRendererComponent(
                tree, value, sel,
                expanded, leaf, row,
                hasFocus);

        if (value instanceof MyTreeRoot) {
            setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/treeRoot.png")));
        }
        if (value instanceof ConnectionHandler) {
            tree.setToolTipText("");
            if ( ((ConnectionHandler) value).getConnection() == null)
                setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/treePaused.png")));
            else
                setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/treeConnected.png")));
        }
        if (leaf) {
            tree.setToolTipText("Clique duplo para selecionar");
            setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/database.png")));
        }

        return this;
    }
}
package com.edusasse.visualsql.gui;

import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

@SuppressWarnings("serial")
public class ObjectsListButtonsCellRenderer extends DefaultListCellRenderer {

    static ImageIcon objectIcon = null;

    /** Creates a new instance of ObjectsListCellRenderer */
    public ObjectsListButtonsCellRenderer() {
        super();
    }

    /* pintura */
    private void setIcon(java.awt.Component comp){

        /* definicao do tipo de icone */
        String path = "/images/";
        JLabel lab = (JLabel)comp;
        String text = lab.getText();
        if(text.equals("Projecao"))
            path+="pi";
        else
            if(text.equals("Selecao"))
                path+="sigma";
            else
                if(text.equals("Prod.Cartesiano"))
                    path+="x";
                else
                   if(text.equals("Uniao"))
                       path+="union";
                   else
                       path+="gamma";
        path+=".png";
        /*Definicao do ImageIcon*/
        objectIcon = new javax.swing.ImageIcon(getClass().getResource(path));
        lab.setIcon(objectIcon);
    }

    @Override
    public java.awt.Component getListCellRendererComponent(javax.swing.JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        java.awt.Component retValue;
        retValue = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        setIcon(retValue);
        return retValue;
    }


}
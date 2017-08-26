package com.edusasse.visualsql.gui.entity;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.TransferHandler;

import com.edusasse.visualsql.gui.*;

import java.awt.datatransfer.Transferable;
 
public class EntityTransferHandler extends TransferHandler {

    public int getSourceActions(JComponent c) {
        return COPY;
    }

    protected Transferable createTransferable(JComponent c)
    {
        if (c instanceof JList)
        {
            JList list = (JList)c;
            Object obj = list.getSelectedValue();
            // Look at the
            return new TransferableObject(obj);
        }
        return null;
    }

}

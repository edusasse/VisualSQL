package com.edusasse.visualsql.gui;

import java.awt.datatransfer.DataFlavor;

import com.edusasse.visualsql.gui.entity.Entity;

public class TransferableObject implements java.awt.datatransfer.Transferable {

    private Object obj;
    java.awt.datatransfer.DataFlavor thisFlavor;

    /** Creates a new instance of TransferableObject */
    public TransferableObject(Object obj) {
        this.setObj(obj);
        Entity o = (Entity) obj;
        String mimeType = DataFlavor.javaJVMLocalObjectMimeType;
        this.thisFlavor = new DataFlavor(mimeType, String.valueOf(o.getClasse()));
    }

    public Object getTransferData(java.awt.datatransfer.DataFlavor flavor) throws java.awt.datatransfer.UnsupportedFlavorException, java.io.IOException {
        return getObj();
    }

    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{this.thisFlavor};
    }

    public boolean isDataFlavorSupported(DataFlavor flavor) {
       if (flavor == null) {
            return false;
        } else if (flavor.equals(thisFlavor)) {
            return true;
        } else {
            return false;
        }
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }
}

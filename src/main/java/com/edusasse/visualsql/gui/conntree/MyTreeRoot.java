/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.edusasse.visualsql.gui.conntree;

import com.edusasse.visualsql.db.jdbc.*;

/**
 *
 * @author Eduardo
 */
public class MyTreeRoot {
    public final String label = "Servidores";
    public final int type = TreeConstants.ROOT;

    @Override
    public String toString() {
        return label;
    }


}

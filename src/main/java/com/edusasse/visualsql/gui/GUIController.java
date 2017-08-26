package com.edusasse.visualsql.gui;

import java.awt.Image;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import com.edusasse.visualsql.db.jdbc.ConnController;
import com.edusasse.visualsql.db.jdbc.MyDriver;
import com.edusasse.visualsql.gui.actions.EraseAction;
import com.edusasse.visualsql.gui.actions.LineAction;
import com.edusasse.visualsql.gui.actions.SelectAction;
import com.edusasse.visualsql.gui.connstore.ConnStore;

public class GUIController {

    private VisualSQLApp main;
    public static ImageIcon[] images;
    private static GUIController c = null;
    private HashMap<ViewDiagram,Id> idsViewDiagram = null;
    private ViewDiagram currentViewDiagram = null;
    private SelectAction selAct;
    private LineAction lineAct;
    private EraseAction eraseAct;

    private class Id{
        private int next;

        public Id(){
            this.next = 1;
        }

        public final int getNext(){
            final int r = next;
            next++;
            return r;
        }
    }

    private GUIController() {
        this.idsViewDiagram = new HashMap<ViewDiagram,Id>();
        this.loadResources();
        MyDriver.loadDefaults();
        this.loadProfileConn();
        this.selAct = new SelectAction();
        this.lineAct = new LineAction();
        this.eraseAct = new EraseAction();
    }

    public synchronized int gimmeOneId() {
        if (this.idsViewDiagram.containsKey(this.getCurrentViewDiagram()))
            return this.idsViewDiagram.get(this.getCurrentViewDiagram()).getNext();
        else {
            Id id = new Id();
            this.idsViewDiagram.put(this.getCurrentViewDiagram(), id);
            return id.getNext();
        }
    }

    public void setMain(VisualSQLApp main) {
        this.main = main;
    }

    public VisualSQLApp getMain() {
        return this.main;
    }

    public void setCurrentViewDiagram(ViewDiagram currentViewDiagram) {
        this.currentViewDiagram = currentViewDiagram;
        if(currentViewDiagram!=null)
            this.updateActions();
    }

    public ViewDiagram getCurrentViewDiagram() {
        return currentViewDiagram;
    }

    public void updateActions(){
        this.selAct.setViewDiagram(GUIController.getInstance().getCurrentViewDiagram());
        this.lineAct.setViewDiagram(GUIController.getInstance().getCurrentViewDiagram());
        this.eraseAct.setViewDiagram(GUIController.getInstance().getCurrentViewDiagram());
        this.main.diagramUpdate();
    }

    public static GUIController getInstance() {
        if (c == null) {
            c = new GUIController();
        }
        return c;
    }
 
    public void showTableData(String query) throws SQLException {
        try {
            // Somente se a query possui algo e a conexao estiver ok
            if(!query.equals("") && currentViewDiagram.isConnected() ){
                String connKey = currentViewDiagram.getConnKey();
                String curDB =  currentViewDiagram.getCurrentDB();
                // Seta o DB corrente
                ConnController.getHandler(connKey).getConnection().setCatalog(curDB);
                //Cria statement
                Statement a = ConnController.getHandler(connKey).getConnection().createStatement();
                // Executa consulta
                ResultSet result = a.executeQuery(query);
                main.showData(result,query);
            }
        } catch (SQLException ex) {
            //mostrar ao usuario que ocorreu problema
            this.showMessageDialog("Erro ao montar SQL:\n"+ex.getMessage(),0);
            //levantar exception
            throw ex;
        }
    }

    public String[] getQueryColumns(String alias, String query){
        try {
            // Se a query possui algo e a conexao estiver ok
            if(!query.equals("") && currentViewDiagram.isConnected() ){
                String connKey = currentViewDiagram.getConnKey();
                String curDB =  currentViewDiagram.getCurrentDB();
                // Seta o DB corrente
                ConnController.getHandler(connKey).getConnection().setCatalog(curDB);
                // Cria statement
                Statement a = ConnController.getHandler(connKey).getConnection().createStatement();
                // Executa consulta
                ResultSet result = a.executeQuery(query);
                //busca das colunas
                String[] colunas = new String[result.getMetaData().getColumnCount()];
                for(int i = 0; i < result.getMetaData().getColumnCount(); i++)
                   colunas[i] =  alias+"."+result.getMetaData().getColumnLabel(i+1);
                //retornar
                return colunas;
            }
        } catch (SQLException ex) {
            this.showMessageDialog("Erro ao retornar a lista de colunas: "+ex.getMessage(),0);
        }
        return new String[0];
    }
    
    public int[] getQueryColumnsType(String query){
        try {
            // Se a query possui algo e a conexao estiver ok
            if(!query.equals("") && currentViewDiagram.isConnected() ){
                String connKey = currentViewDiagram.getConnKey();
                String curDB =  currentViewDiagram.getCurrentDB();
                // Seta o DB corrente
                ConnController.getHandler(connKey).getConnection().setCatalog(curDB);
                // Cria statement
                Statement a = ConnController.getHandler(connKey).getConnection().createStatement();
                // Executa consulta
                ResultSet result = a.executeQuery(query);
                //busca das colunas
                int[] colunasType = new int[result.getMetaData().getColumnCount()];
                for(int i = 0; i < result.getMetaData().getColumnCount(); i++){
                	colunasType[i] =  result.getMetaData().getColumnType(i+1);
                }
                //retornar
                return colunasType;
            }
        } catch (SQLException ex) {
            this.showMessageDialog("Erro ao retornar o tipo da lista de colunas: "+ex.getMessage(),0);
        }
        return new int[0];
    }
    
    public void loadProfileConn() {
        ConnStore.getInstance().load();
    }

    public void saveProfileConn() {
        ConnStore.getInstance().save();
    }

    private final void loadResources() {
        images = new ImageIcon[5];
        images[0] = new javax.swing.ImageIcon(getClass().getResource("/images/pi.png"));
        images[1] = new javax.swing.ImageIcon(getClass().getResource("/images/sigma.png"));
        images[2] = new javax.swing.ImageIcon(getClass().getResource("/images/x.png"));
        images[3] = new javax.swing.ImageIcon(getClass().getResource("/images/union.png"));
        images[4] = new javax.swing.ImageIcon(getClass().getResource("/images/gamma.png"));
    }

    public Image getOperatorImage(int OPERATOR) {
        return images[OPERATOR].getImage();
    }

    public LineAction getLineAct() {
        return lineAct;
    }

    public SelectAction getSelAct() {
        return selAct;
    }

    public EraseAction getEraseAct() {
        return eraseAct;
    }

    public static String getVersion(){
        return "0.02";
    }

    public void showMessageDialog(String msg, int messageType){
        JOptionPane.showMessageDialog(this.main.findParentFrame(),msg);
    }
}

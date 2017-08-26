package com.edusasse.visualsql.gui.elements;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JLabel;

import com.edusasse.visualsql.gui.GUIController;
import com.edusasse.visualsql.gui.ViewDiagram;
import com.edusasse.visualsql.gui.entity.Classes;

@SuppressWarnings("serial")
public class TableElement extends MyElement implements LinkedDataElement {

    private ArrayList<LineElement> arcos;
    private ViewDiagram diagramPai;
    private String dbName;
    private String tableName;
    private JLabel jlbTableName;

    public TableElement(String db, String table, int x, int y, ViewDiagram pai) {
        super();
        this.initComponents(db, table, x, y, pai);
    }

     public TableElement(int id,String db, String table, int x, int y, ViewDiagram pai) {
        super(id);
        this.initComponents(db, table, x, y, pai);
    }

    protected final void initComponents(String db, String table, int x, int y, ViewDiagram pai){
        this.setLayout(null);
        this.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        this.setLocation(x,y);
        
        this.dbName = db.trim();
        this.tableName = table.trim();
        this.arcos = new ArrayList<LineElement>();
        this.diagramPai = pai;

        this.setBounds(x,y,150,26);

        //Adicionar um icone de simbolgia de tabela
        JLabel jlbTbImg = new JLabel(new javax.swing.ImageIcon(getClass().getResource("/images/database_table.png")));
        jlbTbImg.setBounds(2,4,20,20);
        this.add(jlbTbImg);

        //Adicionar o label do nome da tabela
        this.jlbTableName = new JLabel(table);
        this.jlbTableName.setBounds(22,4,128,21);
        this.add(this.jlbTableName);
        
        //criar listener para o mouse
        TableElementMouseListener teml = new TableElementMouseListener(this);
        this.addMouseMotionListener(teml);
        this.addMouseListener(teml);
    }

    public String getDbName() {
        return dbName;
    }


    public String getTableName() {
        return tableName;
    }

    @Override
    public void addArco(LineElement arco) {
        //adiciona o arco a tabela
        arcos.add(arco);
    }

    @Override
    public void removerArco(LineElement arco) {
        this.arcos.remove(arco);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
    }

    @Override
    public void desenhar(Graphics g, int x, int y, boolean selecionado) {
        if (selecionado)
            this.setBorder(BorderFactory.createLineBorder(Color.YELLOW));
        else
            this.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        this.repaint();
    }

    @Override
    public boolean contido(int x, int y) {
        return ((x >= getLocation().x && x <= (getLocation().x + getBounds().width)) && (y >= getLocation().y && y <= (getLocation().y + getBounds().height)));
    }

    @Override
    public void deslocar(int x, int y) {
        setLocation(getLocation().x + x, getLocation().y + y);
    }

    @Override
    public Point getPoint() {
        return new Point(getLocation());
    }

    @Override
    public Point getMiddlePoint() {
        return new Point((int) (getLocation().x + (getBounds().width / 2)), (int) (getLocation().y + (getBounds().height / 2)));
    }
    
    public ViewDiagram getDiagramPai() {
        return diagramPai;
    }

    @Override
    public Rectangle getRectangle() {
        return this.getBounds();
    }

    @Override
    public String getQuery() {
        return("SELECT R"+this.getId()+".* FROM "+this.dbName+"."+this.tableName+" R"+this.getId());
    }

    @Override
    public int getClasse() {
        return Classes.TABELA;
    }

    @Override
    public boolean canBeStart(){
        //tabela sempre pode ser o inicio
        return true;
    }

    @Override
    public boolean canBeEnd(){
        //GUIController.getInstance().showMessageDialog("Voce nao pode fechar uma ligacao em uma tabela!");
        //tabela nunca pode resultar no fim de um arco
        return false;
    }

    @Override
    public String getKind() {
        return "TableElement";
    }

    @Override
    public ArrayList<LineElement> getArcos() {
        return this.arcos;
    }

    @Override
    public void restartElement() {
        
    }
}

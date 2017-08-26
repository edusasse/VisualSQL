package com.edusasse.visualsql.gui;

import java.awt.Color;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.Graphics;
import java.awt.dnd.DropTarget;
import java.io.File;
import java.util.ArrayList;
import javax.swing.JPanel;

import com.edusasse.visualsql.db.jdbc.ConnController;
import com.edusasse.visualsql.gui.actions.Action;
import com.edusasse.visualsql.gui.elements.MyElement;
import com.edusasse.visualsql.gui.elements.TableElement;
import com.edusasse.visualsql.gui.entity.EntityDropTargetListener;

@SuppressWarnings("serial")
public class ViewDiagram extends JPanel {

    private ArrayList<MyElement> items;
    private ArrayList<MyElement> selecionados;
    private Action acaoAtual;
    private int posX1Atual = -1; //Posicao X do componente

    private int posY1Atual = -1; //Posicao Y do componente

    private int posX2Atual = -1; //Posicao X do mouse

    private int posY2Atual = -1; //Posicao Y do mouse

    private String connKey = "";
    private String currentDB = "";
    private File fileSaved = null;
    
    public ViewDiagram(Action action) {
        super();
        this.setLayout(null);
        this.setBackground(Color.WHITE);
        setDropTarget(new DropTarget(this, new EntityDropTargetListener(this)));
        items = new ArrayList<MyElement>();
        selecionados = new ArrayList<MyElement>();
        acaoAtual = action;
        
        //Criacao de um novo moseListerner
        ViewDiagramMouseListener ml = new ViewDiagramMouseListener(this);
        addMouseListener(ml);
        addMouseMotionListener(ml);
    }

    public void setItems(ArrayList<MyElement> items) {
        this.items.clear();
        this.items = items;
        repaint();
    }

    public void setConnKey(String connKey) {
        this.connKey = connKey;
    }

    public void updateDb() {
        GUIController.getInstance().getMain().updateTree();
    }

    public String getConnKey() {
        return this.connKey;
    }

    public File getFileSaved() {
        return this.fileSaved;
    }

    public void setFileSaved(File file) {
        this.fileSaved = file;
    }

    public ArrayList<MyElement> getItems() {
        return items;
    }

    public void clear() {
        this.items.clear();
        repaint();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        //iterar sobre todos os elementos do diagrama
        for (int i = 0; i < items.size(); i++) {
            MyElement item = (MyElement) items.get(i);
            item.desenhar(g, posX1Atual, posY1Atual, selecionados.indexOf(item) >= 0);
        }
        //repintar
        super.repaint();
    }

    public int qtosItems() {
        return items.size();
    }

    public MyElement getItem(int i) {
        return (MyElement) items.get(i);
    }

    public Action getAcaoAtual() {
        return acaoAtual;
    }

    public void setAcao(Action novaAcao) {
        removerSelecionados();
        acaoAtual.mudouAcao();
        acaoAtual = novaAcao;
    }

    public void setAcaoDefault() {
        acaoAtual.mudouAcao();
        acaoAtual = GUIController.getInstance().getSelAct();
        GUIController.getInstance().updateActions();
    }

    public void addItem(MyElement item) {
        items.add(item);
        if (item instanceof TableElement) {
            super.add((JPanel) item);
        }
    }

    public void removerItem(MyElement item) {
        items.remove(item);
        if (item instanceof TableElement) {
            super.remove((JPanel) item);
        }
    }

    public void removerSelecionados() {
        selecionados.clear();
    }

    public void setCurrentDB(String currentDB) {
        this.currentDB = currentDB;
    }

    public String getCurrentDB() {
        return this.currentDB;
    }

    public void addSelecionado(MyElement item) {
        //adiciona na lista de selecionados
        selecionados.add(item);
        //remove este item da lista de itens 
        //do diagrama e o recoloca, para que ele 
        //fique com o item acima de todos
        items.remove(item);
        items.add(item);
    }

    public ArrayList<MyElement> getSelecionados() {
        return selecionados;
    }
    
    public boolean isConnected(){
        return(!(ConnController.getHandler(connKey) == null || ConnController.getHandler(connKey).getConnection() == null) );
    }
    
    public void setPosicaoAtual(int x, int y) {
        posX1Atual = x;
        posY1Atual = y;
    }

    public void setPosicaoAtual(int x1, int y1, int x2, int y2) {
        posX1Atual = x1;
        posY1Atual = y1;
        posX2Atual = x2;
        posY2Atual = y2;
    }

    public void soltar(int x, int y) {
        setPosicaoAtual(x, y);
        acaoAtual.encerrar(posX1Atual, posY1Atual);
        repaint();
    }

    public void selecionar(int x, int y) {
        //setar o novo item selecionado
        setPosicaoAtual(x, y);
        acaoAtual.executar(posX1Atual, posY1Atual);
        repaint();
    }

    public void editar() {
       if(this.isConnected())
           //editar as configuracoes do item
           acaoAtual.editar();
    }

    public void pressionar(int x, int y) {
        setPosicaoAtual(x, y);
        acaoAtual.executar(posX1Atual, posY1Atual);
        repaint();
    }

    public void pressionar(int x1, int y1, int x2, int y2) {
        setPosicaoAtual(x1, y1, x2, y2);
        acaoAtual.executar(posX1Atual, posY1Atual);
        repaint();
    }

    public void arrastar(int x, int y) {
        //ajustar a posicao
        setPosicaoAtual(x, y);
        acaoAtual.mover(posX1Atual, posY1Atual);
        repaint();
    }

    public void arrastar(int x1, int y1, int x2, int y2) {
        //novo x eh a posicao antiga, mais a diferenca
        //de movimentacao do mouse
        int novoX = posX1Atual + (x2 - posX2Atual);
        int novoY = posY1Atual + (y2 - posY2Atual);
        //atualiza as novas posicoes
        setPosicaoAtual(novoX, novoY, x2, y2);
        //chamar acao de mover
        acaoAtual.mover(posX1Atual, posY1Atual);
        repaint();
    }

    public void mover(int x1, int y1, int x2, int y2) {
        //atualiza posicoes do elemento e do mouse
        setPosicaoAtual(x1, y1, x2, y2);
    }
}

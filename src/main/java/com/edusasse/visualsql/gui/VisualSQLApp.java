package com.edusasse.visualsql.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.Permission;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.JTree;

import com.edusasse.visualsql.db.jdbc.ConnController;
import com.edusasse.visualsql.db.jdbc.ConnectionHandler;
import com.edusasse.visualsql.gui.conntree.MyTreeListener;
import com.edusasse.visualsql.gui.conntree.MyTreeModel;
import com.edusasse.visualsql.gui.conntree.MyTreeRenderer;
import com.edusasse.visualsql.gui.conntree.MyTreeRoot;
import com.edusasse.visualsql.gui.datatable.MyColumnModel;
import com.edusasse.visualsql.gui.datatable.MyTableModel;
import com.edusasse.visualsql.gui.entity.Classes;
import com.edusasse.visualsql.gui.entity.EntityTransferHandler;
import com.edusasse.visualsql.gui.entity.OperatorEntity;
import com.edusasse.visualsql.gui.parameters.Parameters;
import com.edusasse.visualsql.gui.xml.MyXMLEncoder;
import com.edusasse.visualsql.persistence.Persistence;

@SuppressWarnings("serial")
public class VisualSQLApp extends javax.swing.JApplet implements Parameters, ActionListener, MyTreeListener {

    private MyTableModel mt = null;
    private MyColumnModel mc = null;
    private JPopupMenu jpmServer;
    private int tabIndex = 0;

    /** Initializes the applet Main */
    @Override
    public void init() {
         SecurityManager sm = new SecurityManager() {

            public void checkPermission(Permission perm) {
            }

            public void checkPermission(Permission perm, Object context) {
            }
        };
        System.setSecurityManager(sm);
        
        // Forca iniciacao da controladora
        GUIController.getInstance().setMain(this);
        try {
            java.awt.EventQueue.invokeAndWait(new Runnable() {

                @Override
                public void run() {
                    // Inicia pre componentes
                    preInitComponents();
                    // Inicia componenttes
                    initComponents();
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        // Personaliza a criacao
        this.postComponents();
    }

    public void diagramUpdate() {
        //guarda o diagrama atual
        ViewDiagram vd = GUIController.getInstance().getCurrentViewDiagram();
        //se n?o for nullo
        if (vd != null) {
            //seta selecao como acao atual
            this.setSelectAction();
            //se jah iniciou o componente
            if (this.lbStatusFile != null) {
                //se jah salvou o arquivo no diagrama
                if (vd.getFileSaved() != null) //atualiza o label de status com o nome do arquivo
                {
                    this.lbStatusFile.setText(vd.getFileSaved().getAbsolutePath());
                } else //atualiza o label de status com o nome default
                {
                    this.lbStatusFile.setText("...");
                }
            }
        }

    }

    public void setSelectAction() {
        if (this.jbarToggleBtnCursor != null) {
            this.actionPerformed(new ActionEvent(this.jbarToggleBtnCursor, 1001, ""));
        }
    }

    private boolean fileOpenManager() {
        this.tabIndex++;
        this.runTabAdd();
        //guarda o diagrama atual
        ViewDiagram vd = GUIController.getInstance().getCurrentViewDiagram();
        // verifica se existe
        if (vd != null) {
            try {
                JFileChooser fc = new JFileChooser();
                fc.setFileFilter(new XMLFilter());
                fc.showDialog(this, "Abrir Expressao");
                if (fc.getSelectedFile() == null) {
                    return false;
                }
                final String xmlPath = Persistence.openFile(fc.getSelectedFile());
                if (xmlPath == null) {
                    return false;
                }
                MyXMLEncoder.getInstanceOf().open(xmlPath);
                //seta o fileSaved
                GUIController.getInstance().getCurrentViewDiagram().setFileSaved(fc.getSelectedFile());
                return true;
            } catch (FileNotFoundException ex) {
                Logger.getLogger(VisualSQLApp.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            } catch (IOException ex) {
                Logger.getLogger(VisualSQLApp.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        } else {
            return false;
        }
    }

    private boolean fileSaveManager() {
        //guarda o diagrama atual
        ViewDiagram vd = GUIController.getInstance().getCurrentViewDiagram();
        // verifica se existe
        if (vd != null) {
            // Se for um arquivo novo
            if (vd.getFileSaved() == null) {
                // pede onde salvar
                JFileChooser fs = new JFileChooser();
                fs.setFileFilter(new XMLFilter());
                int option = fs.showSaveDialog(this);
                if (option == JFileChooser.APPROVE_OPTION) {
                    if (fs.getSelectedFile() != null) {
                        vd.setFileSaved(fs.getSelectedFile());
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            }
            // Realiza a persistencia e salva
            final String xmlBuild = MyXMLEncoder.getInstanceOf().save(vd.getFileSaved().getAbsolutePath(), GUIController.getInstance().getCurrentViewDiagram().getItems());
            try {
                Persistence.saveFile(vd.getFileSaved().getAbsolutePath(), xmlBuild);
            } catch (IOException ex) {
                GUIController.getInstance().showMessageDialog("Erro ao salvar o arquvo: " + ex.getMessage(), 0);
            }
            return true;
        } else {
            return false;
        }
    }

    private final void preInitComponents() {
        this.myTabbedPane = new javax.swing.JTabbedPane();
        this.myTabbedPane.removeAll();
        
        runTabAdd();
    }

    public Frame findParentFrame() {
        Container c = this;
        while (c != null) {
            if (c instanceof Frame) {
                return (Frame) c;
            }
            c = c.getParent();
        }
        return (Frame) null;
    }

    public void runTabAdd() {
        ViewDiagram v = new ViewDiagram(GUIController.getInstance().getSelAct());
        GUIController.getInstance().setCurrentViewDiagram(v);
        this.setSelectAction();

        this.myTabbedPane.addTab("Meu Diagrama :" + this.tabIndex, new javax.swing.ImageIcon(getClass().getResource("/images/diagram.png")), GUIController.getInstance().getCurrentViewDiagram());
        this.initTabComponent();
        this.myTabbedPane.setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);

        this.myTabbedPane.setMnemonicAt(this.myTabbedPane.getTabCount() - 1, 48 + this.tabIndex);
        this.myTabbedPane.setSelectedComponent(v);
        
    }

    public void runTabRem() {
        GUIController.getInstance().setCurrentViewDiagram(null);
    }

    private void initTabComponent() {
        ButtonTabComponent bt = new ButtonTabComponent("", this.myTabbedPane);
        this.myTabbedPane.setTabComponentAt(this.myTabbedPane.getTabCount() - 1, bt);
    }

    private void createTree() {
        // Arvore de coneccoes
        criaPopUp();
        this.jtreeConexoes = new JTree();
        jtreeConexoes.setModel(new MyTreeModel(new MyTreeRoot()));
        jtreeConexoes.setAutoscrolls(true);
        jtreeConexoes.setCellRenderer(new MyTreeRenderer());
        jScrollPane4.setViewportView(jtreeConexoes);
        jtreeConexoes.addMouseListener(new TreeListener());
    }

    private final void postComponents() {

        splitA.setOneTouchExpandable(true);
        splitB.setOneTouchExpandable(true);

        // Arvore de coneccoes
        this.createTree();

        // Lista das tabelas
        jlstTables.setDragEnabled(true);
        jlstTables.setTransferHandler(new EntityTransferHandler());
        jlstTables.setCellRenderer(new ObjectsListCellRenderer());

        // Lista de Operadores
        OperatorEntity entity3 = new OperatorEntity(Classes.PROJECAO);
        OperatorEntity entity4 = new OperatorEntity(Classes.SELECAO);
        OperatorEntity entity5 = new OperatorEntity(Classes.PRODUTOCARTESIANO);
        OperatorEntity entity6 = new OperatorEntity(Classes.UNIAO);
        OperatorEntity entity7 = new OperatorEntity(Classes.AGRUPAMENTO);
        Vector v2 = new Vector();
        v2.add(entity3);
        v2.add(entity4);
        v2.add(entity5);
        v2.add(entity6);
        v2.add(entity7);
        lstBotoes.setDragEnabled(true);
        lstBotoes.setTransferHandler(new EntityTransferHandler());
        lstBotoes.setCellRenderer(new ObjectsListButtonsCellRenderer());
        lstBotoes.setListData(v2);

        // Listeners
        this.jbarToggleBtnCursor.addActionListener(this);
        this.jbarToggleBtnLine.addActionListener(this);
        this.jbarToggleBtnErase.addActionListener(this);

        // Icones
        this.tabConnOper.setIconAt(0, (new javax.swing.ImageIcon(getClass().getResource("/images/sum.png"))));
        this.tabConnOper.setIconAt(1, (new javax.swing.ImageIcon(getClass().getResource("/images/connect.png"))));

        this.myDataTabbedPane.setIconAt(0, (new javax.swing.ImageIcon(getClass().getResource("/images/database_table.png"))));
        this.myDataTabbedPane.setIconAt(1, (new javax.swing.ImageIcon(getClass().getResource("/images/layout.png"))));

        myTabbedPane.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
                splitB.setDividerLocation(splitB.getMaximumDividerLocation() + 999);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                ;//   throw new UnsupportedOperationException("Not supported yet.");

            }

            @Override
            public void mouseReleased(MouseEvent e) {
            
            }

            @Override
            public void mouseEntered(MouseEvent e) {
             
            }

            @Override
            public void mouseExited(MouseEvent e) {
                
            }
        });
    }

    private final void criaPopUp() {
        this.jpmServer = new JPopupMenu();
        JMenuItem a = new JMenuItem("Cadastrar Servidor");
        this.jpmServer.add(a);
    }

    @Override
    public void newConnAdd() {
        this.createTree();
        this.updateDb();
    }

    public final void updateDb() {
        jlstTables.setModel(new MyListModel());
        jlstTables.revalidate();
    }

    class TreeListener implements MouseListener {

        @SuppressWarnings("static-access")
        @Override
        public void mouseClicked(MouseEvent e) {
            if (jtreeConexoes.getPathForLocation(e.getX(), e.getY()) != null && e.getClickCount() >= 2) {
                if (jtreeConexoes.getPathForLocation(e.getX(), e.getY()).getLastPathComponent() instanceof ConnectionHandler) {
                    if (((ConnectionHandler) jtreeConexoes.getPathForLocation(e.getX(), e.getY()).getLastPathComponent()).getConnection() == null) {
                        ((ConnectionHandler) jtreeConexoes.getPathForLocation(e.getX(), e.getY()).getLastPathComponent()).connect();
                        createTree();
                    }
                }

            }
            if (jtreeConexoes.getPathForLocation(e.getX(), e.getY()) != null && e.getClickCount() >= 2) {
                if (jtreeConexoes.getPathForLocation(e.getX(), e.getY()).getLastPathComponent() instanceof String) {
                    GUIController.getInstance().getCurrentViewDiagram().setCurrentDB(((String) jtreeConexoes.getPathForLocation(e.getX(), e.getY()).getLastPathComponent()));
                    //if (!GUIController.getInstance().getCurrentViewDiagram().getConnKey().equals(((ConnectionHandler) jtreeConexoes.getPathForLocation(e.getX(), e.getY()).getParentPath().getLastPathComponent()).getKey()))
                    {
                        GUIController.getInstance().getCurrentViewDiagram().setConnKey(((ConnectionHandler) jtreeConexoes.getPathForLocation(e.getX(), e.getY()).getParentPath().getLastPathComponent()).getKey());
                        //System.out.println("Mudou conexao para " + GUIController.getInstance().getCurrentViewDiagram().getConnKey());
                        jlstTables.setModel(new MyListModel());
                        jlstTables.revalidate();
                    }
                }
            }

            if (e.getButton() == e.BUTTON3) {
                if (jtreeConexoes.getPathForLocation(e.getX(), e.getY()) == null) {
                    return;
                }
                if (jtreeConexoes.getPathForLocation(e.getX(), e.getY()).getLastPathComponent() instanceof MyTreeRoot) {
                    jpmServer.setBorderPainted(true);

                    jpmServer.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    jpmServer.show(null, e.getXOnScreen(), e.getYOnScreen());
                    return;
                }
                jpmServer.setVisible(false);
            }

            jpmServer.setVisible(false);
        }

        @Override
        public void mousePressed(MouseEvent e) {
            jtreeConexoes.setSelectionPath(jtreeConexoes.getPathForLocation(e.getX(), e.getY()));
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            //  throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            // throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void mouseExited(MouseEvent e) {
            //  throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    private String fillWithBlank(String text, int size) {
        int left = size - text.length();

        while (left > 0) {
            text += " ";
            left--;
        }
        return text + " ";
    }

    private String getUnderLine(int size) {
        String line = "";
        while (size > 0) {
            line += "-";
            size--;
        }
        return line + " ";
    }

    //** Calcula o tamanho ideal do split pane */
    public void textShow(ResultSet result, String query) throws SQLException {

        String out = "";
        out += "Expressao: ";
        out += "\nQuery: " + query.trim();
        out += "\nResultado:\n";
        String underLine = "";
        int[] size = new int[result.getMetaData().getColumnCount() + 1];
        // gera o tamanho necessario para cada coluna

        for (int i = 1; i <= result.getMetaData().getColumnCount(); i++) {
            size[i] = result.getMetaData().getColumnLabel(i).trim().length();
        }

        // gera o tamanho necessario para cada coluna
        result.beforeFirst();
        while (result.next()) {
            for (int i = 1; i <= result.getMetaData().getColumnCount(); i++) {
                size[i] = result.getString(i).trim().length() > size[i] ? result.getString(i).trim().length() : size[i];
            }
        }
        //escrve o nome das coluna
        for (int i = 1; i <= result.getMetaData().getColumnCount(); i++) {
            out += fillWithBlank(result.getMetaData().getColumnLabel(i), size[i]);
            underLine += this.getUnderLine(size[i]);
        }
        out = out + "\n" + underLine + "\n";
        //escrve os dados de cada coluna
        result.beforeFirst();
        while (result.next()) {
            for (int i = 1; i <= result.getMetaData().getColumnCount(); i++) {
                out += fillWithBlank(result.getString(i).trim(), size[i]);
            }
            out += "\n";
        }


        this.jtxtAreaDataOut.setText(out);
    }

    public void showData(ResultSet result, String query) {
        try {
            this.textShow(result, query);
            if (mt == null) {
                mt = new MyTableModel();
                mt.setResultSet(result);
                this.jtbDataOut.setModel(mt);
            }
            if (mc == null) {
                mc = new MyColumnModel(this.jtbDataOut.getFontMetrics(this.jtbDataOut.getFont()));
                mc.setResultSet(result);
                this.jtbDataOut.setColumnModel(mc);
            }
            // Atualiza o ResultSet
            mt.setResultSet(result);
            mc.setResultSet(result);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void updateTree() {
        for (int i = 0; i < this.jtreeConexoes.getRowCount(); i++) {
            if (this.jtreeConexoes.getPathForRow(i).getLastPathComponent() instanceof ConnectionHandler) {
                if (((ConnectionHandler) this.jtreeConexoes.getPathForRow(i).getLastPathComponent()).getKey().equals(GUIController.getInstance().getCurrentViewDiagram().getConnKey())) {
                    this.jtreeConexoes.setSelectionRow(i);
                }
            } else if (this.jtreeConexoes.getPathForRow(i).getLastPathComponent() instanceof String) {
                if (((String) this.jtreeConexoes.getPathForRow(i).getLastPathComponent()).equals(GUIController.getInstance().getCurrentViewDiagram().getCurrentDB())) {
                    this.jtreeConexoes.setSelectionRow(i);
                }
            }
        }
        this.updateDb();
    }

    public void updateDividerLocation() {

        this.splitB.setDividerLocation(this.splitB.getMaximumDividerLocation() / 2);
    }

    /** This method is called from within the init() method to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        splitA = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        splitB = new javax.swing.JSplitPane();
        myDataTabbedPane = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jtbDataOut = new javax.swing.JTable();
        panStatus = new javax.swing.JPanel();
        lbStatusFile = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jtaOut = new javax.swing.JScrollPane();
        jtxtAreaDataOut = new javax.swing.JTextArea();
        myTabbedPane = myTabbedPane;
        jPanel2 = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        jlstTables = new javax.swing.JList();
        tabConnOper = new javax.swing.JTabbedPane();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        lstBotoes = new javax.swing.JList();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jtreeConexoes = new javax.swing.JTree();
        jtoolBar = new javax.swing.JToolBar();
        jbarBtnNovo = new javax.swing.JButton();
        jbarBtnSave = new javax.swing.JButton();
        jbarBtnOpen = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        jbarBtnRegServer = new javax.swing.JButton();
        jbarBtnRemoveServer = new javax.swing.JButton();
        jbarBtnServerProp = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        jbarToggleBtnCursor = new javax.swing.JToggleButton();
        jbarToggleBtnLine = new javax.swing.JToggleButton();
        jbarToggleBtnErase = new javax.swing.JToggleButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        mnFile = new javax.swing.JMenu();
        mnNewDiagram = new javax.swing.JMenuItem();
        mnSaveDiagram = new javax.swing.JMenuItem();
        mnOpenDiagram = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JSeparator();
        mnExit = new javax.swing.JMenuItem();
        jMenu11 = new javax.swing.JMenu();
        mnRegServer = new javax.swing.JMenuItem();
        mnRemoveServer = new javax.swing.JMenuItem();
        mnServerProperties = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JSeparator();
        mnServeerConnect = new javax.swing.JMenuItem();
        mnServerDisconect = new javax.swing.JMenuItem();
        mnHelp = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();

        splitA.setDividerLocation(200);

        splitB.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        splitB.setDividerLocation(350);
        splitB.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        splitB.setAutoscrolls(true);

        jtbDataOut.setAutoCreateColumnsFromModel(false);
        jtbDataOut.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jtbDataOut.setFocusable(false);
        jtbDataOut.setSurrendersFocusOnKeystroke(true);
        jScrollPane3.setViewportView(jtbDataOut);

        panStatus.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lbStatusFile.setFont(new java.awt.Font("Tahoma", 0, 10));
        lbStatusFile.setText("...");

        javax.swing.GroupLayout panStatusLayout = new javax.swing.GroupLayout(panStatus);
        panStatus.setLayout(panStatusLayout);
        panStatusLayout.setHorizontalGroup(
            panStatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lbStatusFile, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 737, Short.MAX_VALUE)
        );
        panStatusLayout.setVerticalGroup(
            panStatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lbStatusFile)
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 741, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 192, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        myDataTabbedPane.addTab("Tabela", jPanel3);

        jtxtAreaDataOut.setColumns(20);
        jtxtAreaDataOut.setFont(new java.awt.Font("Courier New", 0, 12));
        jtxtAreaDataOut.setRows(5);
        jtaOut.setViewportView(jtxtAreaDataOut);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jtaOut, javax.swing.GroupLayout.DEFAULT_SIZE, 741, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jtaOut, javax.swing.GroupLayout.DEFAULT_SIZE, 215, Short.MAX_VALUE)
        );

        myDataTabbedPane.addTab("Texto", jPanel4);

        splitB.setRightComponent(myDataTabbedPane);
        myDataTabbedPane.getAccessibleContext().setAccessibleName("Tabela");

        splitB.setLeftComponent(myTabbedPane);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(splitB, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 750, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(splitB, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 597, Short.MAX_VALUE)
        );

        splitA.setRightComponent(jPanel1);

        jSplitPane1.setDividerLocation(250);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        jlstTables.setBackground(new java.awt.Color(232, 231, 231));
        jlstTables.setModel(new MyListModel());
        jlstTables.setDragEnabled(true);
        jScrollPane1.setViewportView(jlstTables);

        jSplitPane1.setRightComponent(jScrollPane1);

        tabConnOper.setTabPlacement(javax.swing.JTabbedPane.BOTTOM);

        jScrollPane2.setViewportView(lstBotoes);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 192, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 224, Short.MAX_VALUE)
        );

        tabConnOper.addTab("Operadores", jPanel5);

        jPanel6.setToolTipText("Define as Conexoes");

        jtreeConexoes.setAutoscrolls(true);
        jtreeConexoes.setCellRenderer(new MyTreeRenderer());
        jtreeConexoes.setModel(new MyTreeModel(new MyTreeRoot()));
        jScrollPane4.setViewportView(jtreeConexoes);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 192, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 224, Short.MAX_VALUE)
        );

        tabConnOper.addTab("Conexoes", jPanel6);

        jSplitPane1.setTopComponent(tabConnOper);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 199, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 597, Short.MAX_VALUE)
        );

        splitA.setLeftComponent(jPanel2);

        jtoolBar.setFloatable(false);
        jtoolBar.setRollover(true);
        jtoolBar.setToolTipText("Barra de Ferramentas");

        jbarBtnNovo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/diagram.png"))); // NOI18N
        jbarBtnNovo.setToolTipText("Novo Diagrama");
        jbarBtnNovo.setFocusable(false);
        jbarBtnNovo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbarBtnNovo.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jbarBtnNovo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbarBtnNovoActionPerformed(evt);
            }
        });
        jtoolBar.add(jbarBtnNovo);

        jbarBtnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/disk.png"))); // NOI18N
        jbarBtnSave.setToolTipText("Salvar Diagrama");
        jbarBtnSave.setFocusable(false);
        jbarBtnSave.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbarBtnSave.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jbarBtnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbarBtnSaveActionPerformed(evt);
            }
        });
        jtoolBar.add(jbarBtnSave);

        jbarBtnOpen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/open.png"))); // NOI18N
        jbarBtnOpen.setToolTipText("Abrir Diagrama");
        jbarBtnOpen.setFocusable(false);
        jbarBtnOpen.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbarBtnOpen.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jbarBtnOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbarBtnOpenActionPerformed(evt);
            }
        });
        jtoolBar.add(jbarBtnOpen);
        jtoolBar.add(jSeparator1);

        jbarBtnRegServer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/serverConnect_1.png"))); // NOI18N
        jbarBtnRegServer.setToolTipText("Registrar Servidor");
        jbarBtnRegServer.setFocusable(false);
        jbarBtnRegServer.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbarBtnRegServer.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jbarBtnRegServer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbarBtnRegServerActionPerformed(evt);
            }
        });
        jtoolBar.add(jbarBtnRegServer);

        jbarBtnRemoveServer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/serverDisconect.png"))); // NOI18N
        jbarBtnRemoveServer.setToolTipText("Remover Servidor");
        jbarBtnRemoveServer.setFocusable(false);
        jbarBtnRemoveServer.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbarBtnRemoveServer.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jbarBtnRemoveServer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbarBtnRemoveServerActionPerformed(evt);
            }
        });
        jtoolBar.add(jbarBtnRemoveServer);

        jbarBtnServerProp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/serverProp.png"))); // NOI18N
        jbarBtnServerProp.setFocusable(false);
        jbarBtnServerProp.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbarBtnServerProp.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jbarBtnServerProp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbarBtnServerPropActionPerformed(evt);
            }
        });
        jtoolBar.add(jbarBtnServerProp);
        jtoolBar.add(jSeparator2);

        jbarToggleBtnCursor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/cursor.png"))); // NOI18N
        jbarToggleBtnCursor.setSelected(true);
        jbarToggleBtnCursor.setToolTipText("Selecionar");
        jbarToggleBtnCursor.setFocusable(false);
        jbarToggleBtnCursor.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbarToggleBtnCursor.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jtoolBar.add(jbarToggleBtnCursor);

        jbarToggleBtnLine.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/line.png"))); // NOI18N
        jbarToggleBtnLine.setToolTipText("Linha");
        jbarToggleBtnLine.setFocusable(false);
        jbarToggleBtnLine.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbarToggleBtnLine.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jtoolBar.add(jbarToggleBtnLine);

        jbarToggleBtnErase.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/cross.png"))); // NOI18N
        jbarToggleBtnErase.setToolTipText("Apagar");
        jbarToggleBtnErase.setFocusable(false);
        jbarToggleBtnErase.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbarToggleBtnErase.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jtoolBar.add(jbarToggleBtnErase);
        jbarToggleBtnErase.getAccessibleContext().setAccessibleDescription("\"Apagar\"");

        mnFile.setText("Arquivo");

        mnNewDiagram.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        mnNewDiagram.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/diagram.png"))); // NOI18N
        mnNewDiagram.setText("Novo Diagrama");
        mnNewDiagram.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnNewDiagramActionPerformed(evt);
            }
        });
        mnFile.add(mnNewDiagram);

        mnSaveDiagram.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        mnSaveDiagram.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/disk.png"))); // NOI18N
        mnSaveDiagram.setText("Salvar Diagrama");
        mnSaveDiagram.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnSaveDiagramActionPerformed(evt);
            }
        });
        mnFile.add(mnSaveDiagram);

        mnOpenDiagram.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        mnOpenDiagram.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/open.png"))); // NOI18N
        mnOpenDiagram.setText("Abrir Diagrama");
        mnOpenDiagram.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnOpenDiagramActionPerformed(evt);
            }
        });
        mnFile.add(mnOpenDiagram);
        mnFile.add(jSeparator3);

        mnExit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.ALT_MASK));
        mnExit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/cross.png"))); // NOI18N
        mnExit.setText("Sair");
        mnExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnExitActionPerformed(evt);
            }
        });
        mnFile.add(mnExit);

        jMenuBar1.add(mnFile);

        jMenu11.setText("Servidores");

        mnRegServer.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_INSERT, 0));
        mnRegServer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/serverNew.png"))); // NOI18N
        mnRegServer.setText("Registrar Servidor");
        mnRegServer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnRegServerActionPerformed(evt);
            }
        });
        jMenu11.add(mnRegServer);

        mnRemoveServer.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DELETE, 0));
        mnRemoveServer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/serverDisconect.png"))); // NOI18N
        mnRemoveServer.setText("Remover Servidor");
        mnRemoveServer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnRemoveServerActionPerformed(evt);
            }
        });
        jMenu11.add(mnRemoveServer);

        mnServerProperties.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, java.awt.event.InputEvent.ALT_MASK));
        mnServerProperties.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/serverProp.png"))); // NOI18N
        mnServerProperties.setText("Propriedades do Servidor");
        mnServerProperties.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnServerPropertiesActionPerformed(evt);
            }
        });
        jMenu11.add(mnServerProperties);
        jMenu11.add(jSeparator4);

        mnServeerConnect.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_INSERT, java.awt.event.InputEvent.CTRL_MASK));
        mnServeerConnect.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/serverConnect_1.png"))); // NOI18N
        mnServeerConnect.setText("Conectar ao Servidor");
        mnServeerConnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnServeerConnectActionPerformed(evt);
            }
        });
        jMenu11.add(mnServeerConnect);

        mnServerDisconect.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DELETE, java.awt.event.InputEvent.CTRL_MASK));
        mnServerDisconect.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/serverDisconect.png"))); // NOI18N
        mnServerDisconect.setText("Desconectar do Servidor");
        mnServerDisconect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnServerDisconectActionPerformed(evt);
            }
        });
        jMenu11.add(mnServerDisconect);

        jMenuBar1.add(jMenu11);

        mnHelp.setText("Ajuda");

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F2, 0));
        jMenuItem1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/about.png"))); // NOI18N
        jMenuItem1.setText("Sobre");
        mnHelp.add(jMenuItem1);

        jMenuBar1.add(mnHelp);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jtoolBar, javax.swing.GroupLayout.DEFAULT_SIZE, 956, Short.MAX_VALUE)
            .addComponent(splitA, javax.swing.GroupLayout.DEFAULT_SIZE, 956, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jtoolBar, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(splitA, javax.swing.GroupLayout.DEFAULT_SIZE, 599, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    private void jbarBtnNovoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbarBtnNovoActionPerformed
        this.actionPerformed(evt);
}//GEN-LAST:event_jbarBtnNovoActionPerformed

    private void mnNewDiagramActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnNewDiagramActionPerformed
        this.actionPerformed(evt);
    }//GEN-LAST:event_mnNewDiagramActionPerformed

    private void mnRegServerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnRegServerActionPerformed
        this.actionPerformed(evt);
    }//GEN-LAST:event_mnRegServerActionPerformed

    private void mnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnExitActionPerformed
        System.exit(0);
    }//GEN-LAST:event_mnExitActionPerformed

    private void mnRemoveServerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnRemoveServerActionPerformed
        this.actionPerformed(evt);

    }//GEN-LAST:event_mnRemoveServerActionPerformed

    private void mnSaveDiagramActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnSaveDiagramActionPerformed
        this.actionPerformed(evt);
    }//GEN-LAST:event_mnSaveDiagramActionPerformed

    private void mnOpenDiagramActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnOpenDiagramActionPerformed
        this.actionPerformed(evt);
    }//GEN-LAST:event_mnOpenDiagramActionPerformed

    private void mnServerPropertiesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnServerPropertiesActionPerformed
        this.actionPerformed(evt);
    }//GEN-LAST:event_mnServerPropertiesActionPerformed

    private void jbarBtnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbarBtnSaveActionPerformed
        this.actionPerformed(evt);
    }//GEN-LAST:event_jbarBtnSaveActionPerformed

    private void jbarBtnOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbarBtnOpenActionPerformed
        this.actionPerformed(evt);
    }//GEN-LAST:event_jbarBtnOpenActionPerformed

    private void jbarBtnRegServerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbarBtnRegServerActionPerformed
        this.actionPerformed(evt);
    }//GEN-LAST:event_jbarBtnRegServerActionPerformed

    private void jbarBtnRemoveServerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbarBtnRemoveServerActionPerformed
        this.actionPerformed(evt);
    }//GEN-LAST:event_jbarBtnRemoveServerActionPerformed

    private void jbarBtnServerPropActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbarBtnServerPropActionPerformed
        this.actionPerformed(evt);
    }//GEN-LAST:event_jbarBtnServerPropActionPerformed

    private void mnServeerConnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnServeerConnectActionPerformed
        this.actionPerformed(evt);
    }//GEN-LAST:event_mnServeerConnectActionPerformed

    private void mnServerDisconectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnServerDisconectActionPerformed
        this.actionPerformed(evt);
    }//GEN-LAST:event_mnServerDisconectActionPerformed

    public void removeConn(ConnConfig c) {
        this.myTabbedPane.remove(c);
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu jMenu11;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JButton jbarBtnNovo;
    private javax.swing.JButton jbarBtnOpen;
    private javax.swing.JButton jbarBtnRegServer;
    private javax.swing.JButton jbarBtnRemoveServer;
    private javax.swing.JButton jbarBtnSave;
    private javax.swing.JButton jbarBtnServerProp;
    private javax.swing.JToggleButton jbarToggleBtnCursor;
    private javax.swing.JToggleButton jbarToggleBtnErase;
    private javax.swing.JToggleButton jbarToggleBtnLine;
    private javax.swing.JList jlstTables;
    private javax.swing.JScrollPane jtaOut;
    private javax.swing.JTable jtbDataOut;
    private javax.swing.JToolBar jtoolBar;
    private javax.swing.JTree jtreeConexoes;
    private javax.swing.JTextArea jtxtAreaDataOut;
    private javax.swing.JLabel lbStatusFile;
    private javax.swing.JList lstBotoes;
    private javax.swing.JMenuItem mnExit;
    private javax.swing.JMenu mnFile;
    private javax.swing.JMenu mnHelp;
    private javax.swing.JMenuItem mnNewDiagram;
    private javax.swing.JMenuItem mnOpenDiagram;
    private javax.swing.JMenuItem mnRegServer;
    private javax.swing.JMenuItem mnRemoveServer;
    private javax.swing.JMenuItem mnSaveDiagram;
    private javax.swing.JMenuItem mnServeerConnect;
    private javax.swing.JMenuItem mnServerDisconect;
    private javax.swing.JMenuItem mnServerProperties;
    private javax.swing.JTabbedPane myDataTabbedPane;
    private javax.swing.JTabbedPane myTabbedPane;
    private javax.swing.JPanel panStatus;
    private javax.swing.JSplitPane splitA;
    private javax.swing.JSplitPane splitB;
    private javax.swing.JTabbedPane tabConnOper;
    // End of variables declaration//GEN-END:variables

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.mnNewDiagram || e.getSource() == this.jbarBtnNovo) {
            this.tabIndex++;
            this.runTabAdd();
            this.updateTree();
        } else if (e.getSource() == this.mnOpenDiagram || e.getSource() == this.jbarBtnOpen) {
            if (fileOpenManager()) {
                this.lbStatusFile.setText(GUIController.getInstance().getCurrentViewDiagram().getFileSaved().getAbsolutePath());
            }
        } else if (e.getSource() == this.mnSaveDiagram || e.getSource() == this.jbarBtnSave) {
            if (fileSaveManager()) {
                this.lbStatusFile.setText(GUIController.getInstance().getCurrentViewDiagram().getFileSaved().getAbsolutePath());
            }
        } else if (e.getSource() == this.mnRegServer || e.getSource() == this.jbarBtnRegServer) {
            Component c = new ConnConfig(this);
            this.myTabbedPane.addTab("Conexao", new javax.swing.ImageIcon(getClass().getResource("/images/connect.png")), c);
            this.myTabbedPane.setSelectedComponent(c);
            this.splitB.setDividerLocation(this.splitB.getMinimumDividerLocation() + 9999);
        } else if (e.getSource() == this.mnRemoveServer || e.getSource() == this.jbarBtnRemoveServer) {
            if (jtreeConexoes.getSelectionPath().getLastPathComponent() instanceof ConnectionHandler) {
                ConnController.removeHandler(((ConnectionHandler) jtreeConexoes.getSelectionPath().getLastPathComponent()).getKey());
                GUIController.getInstance().saveProfileConn();
                this.createTree();
            } else {
                JOptionPane.showMessageDialog(null, "Selecione uma conexao valida!", "Aviso: Remover Servidor", JOptionPane.INFORMATION_MESSAGE);
            }

        } else if (e.getSource() == this.mnServerProperties || e.getSource() == this.jbarBtnServerProp) {
            ConnectionHandler ch = null;

            if (jtreeConexoes.getSelectionPath().getLastPathComponent() instanceof String) {
                ch = ((ConnectionHandler) jtreeConexoes.getSelectionPath().getParentPath().getLastPathComponent());
            }

            if (jtreeConexoes.getSelectionPath().getLastPathComponent() instanceof ConnectionHandler || jtreeConexoes.getSelectionPath().getLastPathComponent() instanceof String) {
                if (jtreeConexoes.getSelectionPath().getLastPathComponent() instanceof String) {
                    ch = ((ConnectionHandler) jtreeConexoes.getSelectionPath().getParentPath().getLastPathComponent());
                } else {
                    ch = ((ConnectionHandler) jtreeConexoes.getSelectionPath().getLastPathComponent());
                }

                Component c = new ConnConfig(this);
                this.myTabbedPane.addTab("Prop. Conexao", new javax.swing.ImageIcon(getClass().getResource("/images/connect.png")), c);
                this.myTabbedPane.setSelectedComponent(c);
                this.splitB.setDividerLocation(this.splitB.getMinimumDividerLocation() + 9999);
                ((ConnConfig) c).setEdit(ch);
            } else {
                JOptionPane.showMessageDialog(null, "Selecione uma conexao valida!", "Aviso: Editar Servidor", JOptionPane.INFORMATION_MESSAGE);
            }

        } else if (e.getSource() == this.mnServeerConnect) {
            if (jtreeConexoes.getSelectionPath().getLastPathComponent() instanceof ConnectionHandler) {
                if (((ConnectionHandler) jtreeConexoes.getSelectionPath().getLastPathComponent()).getConnection() == null) {
                    ((ConnectionHandler) jtreeConexoes.getSelectionPath().getLastPathComponent()).connect();
                    createTree();

                }


            }
        } else if (e.getSource() == this.mnServerDisconect) {
            if (jtreeConexoes.getSelectionPath().getLastPathComponent() instanceof ConnectionHandler) {
                if (((ConnectionHandler) jtreeConexoes.getSelectionPath().getLastPathComponent()).getConnection() != null) {
                    ((ConnectionHandler) jtreeConexoes.getSelectionPath().getLastPathComponent()).disconnect();
                    createTree();
                }
            }
        } else if (e.getSource() == this.jbarToggleBtnCursor) {
            GUIController.getInstance().getCurrentViewDiagram().setAcao(GUIController.getInstance().getSelAct());
            this.jbarToggleBtnCursor.setSelected(true);
            this.jbarToggleBtnLine.setSelected(false);
            this.jbarToggleBtnErase.setSelected(false);
        } else if (e.getSource() == this.jbarToggleBtnLine) {
            GUIController.getInstance().getCurrentViewDiagram().setAcao(GUIController.getInstance().getLineAct());
            this.jbarToggleBtnLine.setSelected(true);
            this.jbarToggleBtnCursor.setSelected(false);
            this.jbarToggleBtnErase.setSelected(false);
        } else if (e.getSource() == this.jbarToggleBtnErase) {
            GUIController.getInstance().getCurrentViewDiagram().setAcao(GUIController.getInstance().getEraseAct());
            this.jbarToggleBtnErase.setSelected(true);
            this.jbarToggleBtnLine.setSelected(false);
            this.jbarToggleBtnCursor.setSelected(false);
        }

    }

    private class XMLFilter extends javax.swing.filechooser.FileFilter {

        //Accept all directories and all gif, jpg, tiff, or png files.
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            }


            String extension = "";
            String fileName = f.getName();
            String ext[] = fileName.split("\\.");
            int i = ext.length;

            if (i > 1) {
                extension = ext[i - 1];
            }


            if (extension != null) {
                if (extension.equals("xml")) {
                    return true;
                } else {
                    return false;
                }
            }

            return false;
        }

        //The description of this filter
        public String getDescription() {
            return "*.xml (VisualSQL)";
        }

        public Icon getIcon() {
            return new javax.swing.ImageIcon(getClass().getResource("/images/sigma.png"));
        }
    }
}

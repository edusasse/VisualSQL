package com.edusasse.visualsql.gui.elementsconfig;

import java.sql.SQLException;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.edusasse.visualsql.gui.GUIController;
import com.edusasse.visualsql.gui.elements.ColumnElement;
import com.edusasse.visualsql.gui.elements.ProjectionOperatorElement;

@SuppressWarnings("serial")
public class ProjectionConfig extends ElementConfig {

    //objeto da projecao
    ProjectionOperatorElement poe;
    //Lista de colunas da projecao
    private ArrayList<ColumnElement> projectionList;
    private ArrayList<ColumnElement> prevProjectionList;
    //campos para a edicao
    private JTextArea jtaProjection;
    private JTextField jtfAlias;
    private JButton btnMutant;
    private JButton btnDelete;
    //Tabela
    private JScrollPane scrPane;
    private JTable tabela;
    private ColumnElementConfigTableModel model;

    //classe internas
    private class MyListSelectionListener implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            ListSelectionModel lsm = (ListSelectionModel) e.getSource();
            int row = lsm.getLeadSelectionIndex();
            if (row >= 0 && row < projectionList.size()) {
                btnMutant.setText("Alterar");
                btnDelete.setVisible(true);
                jtaProjection.setText(model.getColumnElement(row).getColumn());
                jtfAlias.setText(model.getColumnElement(row).getAlias());
            } else {
                btnMutant.setText("Incluir");
                btnDelete.setVisible(false);
            }
        }
    }

    private class mutantButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals("Incluir"))
                novo();
            else
                if (e.getActionCommand().equals("Alterar"))
                    alterar();
        }
    }

    /** Creates new form jPopProjection */
    public ProjectionConfig(java.awt.Frame parent, boolean modal, ProjectionOperatorElement poe) {
        super(parent,modal,poe);
        //propriedades da janela
        setTitle("Configuracao da Projecao");    
        this.setBounds(50, 50, 250, 250);
        this.setLocationRelativeTo(null);
        //this.pack();
    }

    // criacao dos componentes
    @Override
    protected void initComponents() {
        super.initComponents();

        //incializa variaveis locais
        this.poe = (ProjectionOperatorElement) super.operator;
        this.projectionList = new ArrayList<ColumnElement>();
        this.prevProjectionList = new ArrayList<ColumnElement>();
        //itera sobre a lista no objeto para copiar os valores
        for(int i = 0; i<this.poe.getProjectionList().size();i++){
            this.projectionList.add(new ColumnElement(this.poe.getProjectionList().get(i).getColumn(),this.poe.getProjectionList().get(i).getAlias()) );
            this.prevProjectionList.add(this.poe.getProjectionList().get(i));
        }

        JPanel panTop = new JPanel(new GridLayout(1,2));

        JButton jbutAddCol = new JButton("Add");
        jbutAddCol.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addColumnOnEdit();
            }
        });

        panTop.add(super.jcbColunas);
        panTop.add(jbutAddCol);

        this.getContentPane().add(panTop, BorderLayout.NORTH);

        JPanel panEdit = new JPanel(new GridLayout(2,2));
        
        JLabel labProj = new JLabel("Coluna:");
        panEdit.add(labProj);
        this.jtaProjection = new JTextArea();
        this.jtaProjection.setRows(2);
        this.jtaProjection.setLineWrap(true);
        this.jtaProjection.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        panEdit.add(this.jtaProjection);

        JLabel labAlias = new JLabel("Apelido:");
        panEdit.add(labAlias);
        this.jtfAlias = new JTextField();
        this.jtfAlias.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        panEdit.add(jtfAlias);

        JPanel panButEdit = new JPanel(new FlowLayout());
        this.btnMutant = new JButton("Incluir");
        this.btnMutant.addActionListener(new mutantButtonListener());
        this.btnDelete = new JButton("Excluir");
        this.btnDelete.setVisible(false);
        this.btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                excluir();
            }
        });
        panButEdit.add(this.btnMutant);
        panButEdit.add(this.btnDelete);

        this.model = new ColumnElementConfigTableModel(1,this.projectionList);
        this.tabela = new JTable(this.model);
        this.tabela.getSelectionModel().addListSelectionListener(new MyListSelectionListener());
        this.tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.tabela.setColumnSelectionAllowed(false);
        this.tabela.setRowSelectionAllowed(true);
        this.scrPane = new JScrollPane(this.tabela);

        JPanel panCen = new JPanel(new GridLayout(3,1,0,2));
        panCen.add(panEdit);
        panCen.add(panButEdit);
        panCen.add(this.scrPane);

        this.getContentPane().add(panCen,BorderLayout.CENTER);

        JPanel panBut = new JPanel(new FlowLayout());
        JButton jbutOk = new JButton("OK");
        jbutOk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ok();
            }
        });
        panBut.add(jbutOk);

        JButton jbutCanc = new JButton("Cancelar");
        jbutCanc.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancel();
            }
        });
        panBut.add(jbutCanc);
        this.getContentPane().add(panBut, BorderLayout.SOUTH);
        
    }

    @Override
    protected void addColumnOnEdit(){
        this.jtaProjection.append((String) this.jcbColunas.getSelectedItem());
    }

    @Override
    protected void novo(){
        if(!this.jtaProjection.getText().equals(""))
            this.projectionList.add(new ColumnElement(this.jtaProjection.getText(),this.jtfAlias.getText()));
        this.limparTela();
    }

    @Override
    protected void alterar(){
        if(!this.jtaProjection.getText().equals("")){
            ColumnElement proj = this.projectionList.get(this.tabela.getSelectedRow());
            proj.setProjection(this.jtaProjection.getText());
            proj.setAlias(this.jtfAlias.getText());
        }
        this.limparTela();
    }

    @Override
    protected void excluir(){
        this.projectionList.remove(this.tabela.getSelectedRow());
        this.limparTela();
    }

    @Override
    protected void limparTela(){
        this.model.fireTableDataChanged();
        this.btnMutant.setText("Incluir");
        this.btnDelete.setVisible(false);
        this.jtaProjection.setText("");
        this.jtfAlias.setText("");
    }

    @Override
    protected void ok() {
        //se n?o foi informado nada
        if(this.projectionList.size()==0){
            GUIController.getInstance().showMessageDialog("Informe ao menos uma Projecao!",2);
            return;
        }
        //copiar a lista atual para o objeto
        this.poe.setProjectionList(this.projectionList);
        //chamar metodo padrao
        try {
            super.ok();
        } catch (SQLException ex) {
            //deu erro, retornar valor inicial
            this.poe.setProjectionList(this.prevProjectionList);
        }
    }
}

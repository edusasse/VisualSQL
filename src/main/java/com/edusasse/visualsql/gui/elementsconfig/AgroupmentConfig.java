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
import javax.swing.JComboBox;
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
import com.edusasse.visualsql.gui.elements.AgroupmentOperatorElement;
import com.edusasse.visualsql.gui.elements.ColumnElement;

@SuppressWarnings("serial")
public class AgroupmentConfig extends ElementConfig {

    //objeto de agrupamento
    AgroupmentOperatorElement aoe;
    //Lista de colunas da projecao
    private ArrayList<ColumnElement> projectionList;
    private ArrayList<ColumnElement> prevProjectionList;
    //Lista de colunas do agrupamento
    private ArrayList<ColumnElement> agroupmentList;
    private ArrayList<ColumnElement> prevAgroupmentList;
    //campos para a edicao
    private JComboBox jcbTipo;
    private JTextArea jtaColumn;
    private JTextField jtfAlias;
    private JButton btnMutant;
    private JButton btnDelete;
    //Tabelas
    private JScrollPane scrPaneProj;
    private JTable tabelaProj;
    private ColumnElementConfigTableModel modelProj;
    private JScrollPane scrPaneAgro;
    private JTable tabelaAgro;
    private ColumnElementConfigTableModel modelAgro;

    //classe internas
    private class MyListSelectionListener implements ListSelectionListener {
    	
    	private int tipo;
    	
    	public MyListSelectionListener(int tipo){
    		super();
    		this.tipo = tipo;
    	}
    	
        @Override
        public void valueChanged(ListSelectionEvent e){
            ListSelectionModel lsm = (ListSelectionModel) e.getSource();
            ArrayList<ColumnElement> columnElementList;
            ColumnElementConfigTableModel model;
            if(this.tipo == 0){
            	columnElementList = projectionList;
            	model = modelProj;
            	modelAgro.fireTableDataChanged();
            }
            else{
            	columnElementList = agroupmentList;
            	model = modelAgro;
            	modelProj.fireTableDataChanged();
            }	
            int row = lsm.getLeadSelectionIndex();
            if (row >= 0 && row < columnElementList.size()) {
            	btnDelete.setVisible(false);
            	jcbTipo.setSelectedIndex(this.tipo);
                jtaColumn.setText(model.getColumnElement(row).getColumn());
                jtfAlias.setText(model.getColumnElement(row).getAlias());
                btnMutant.setText("Alterar");
                btnDelete.setVisible(true);
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
    public AgroupmentConfig(java.awt.Frame parent, boolean modal, AgroupmentOperatorElement aoe) {
        super(parent,modal,aoe);
        //propriedades da janela
        setTitle("Configuracao de Agrupamento");  
        super.setBounds(50, 50, 400, 400);
        this.setLocationRelativeTo(null);
        //this.pack();
    }

    // criacao dos componentes
    @Override
    protected void initComponents() {
        super.initComponents();

        //incializa variaveis locais
        this.aoe = (AgroupmentOperatorElement) super.operator;
        //listas de projecao
        this.projectionList = new ArrayList<ColumnElement>();
        this.prevProjectionList = new ArrayList<ColumnElement>();
        //itera sobre a lista no objeto para copiar os valores
        for(int i = 0; i<this.aoe.getProjectionList().size();i++){
            this.projectionList.add(new ColumnElement(this.aoe.getProjectionList().get(i).getColumn(),this.aoe.getProjectionList().get(i).getAlias()) );
            this.prevProjectionList.add(this.aoe.getProjectionList().get(i));
        }
        //listas de agrupamento
        this.agroupmentList = new ArrayList<ColumnElement>();
        this.prevAgroupmentList = new ArrayList<ColumnElement>();
        //itera sobre a lista no objeto para copiar os valores
        for(int i = 0; i<this.aoe.getAgroupmentList().size();i++){
            this.agroupmentList.add(new ColumnElement(this.aoe.getAgroupmentList().get(i).getColumn(),this.aoe.getAgroupmentList().get(i).getAlias()) );
            this.prevAgroupmentList.add(this.aoe.getAgroupmentList().get(i));
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

        JPanel panEdit = new JPanel(new GridLayout(3,3));
        
        JLabel labTipo = new JLabel("Tipo:");
        panEdit.add(labTipo);
        String[] tipos = {"Agrupador","Agregador"};
        this.jcbTipo = new JComboBox(tipos);
        this.jcbTipo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                comboChange();
            }
        });
        
        panEdit.add(this.jcbTipo);
        
        JLabel labProj = new JLabel("Coluna:");
        panEdit.add(labProj);
        this.jtaColumn = new JTextArea();
        this.jtaColumn.setRows(2);
        this.jtaColumn.setLineWrap(true);
        this.jtaColumn.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        panEdit.add(this.jtaColumn);

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

        JPanel panTabelas = new JPanel(new GridLayout(2,2));
        
        panTabelas.add(new JLabel("Colunas Agrupadoras"));
        panTabelas.add(new JLabel("Colunas Agregadoras"));
        
        this.modelProj = new ColumnElementConfigTableModel(1,this.projectionList);
        this.tabelaProj = new JTable(this.modelProj);
        this.tabelaProj.getSelectionModel().addListSelectionListener(new MyListSelectionListener(0));
        this.tabelaProj.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.tabelaProj.setColumnSelectionAllowed(false);
        this.tabelaProj.setRowSelectionAllowed(true);
        this.scrPaneProj = new JScrollPane(this.tabelaProj);
        panTabelas.add(this.scrPaneProj);
        
        this.modelAgro = new ColumnElementConfigTableModel(2,this.agroupmentList);
        this.tabelaAgro = new JTable(this.modelAgro);
        this.tabelaAgro.getSelectionModel().addListSelectionListener(new MyListSelectionListener(1));
        this.tabelaAgro.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.tabelaAgro.setColumnSelectionAllowed(false);
        this.tabelaAgro.setRowSelectionAllowed(true);
        this.scrPaneAgro = new JScrollPane(this.tabelaAgro);
        panTabelas.add(this.scrPaneAgro);
        
        JPanel panCen = new JPanel(new GridLayout(2,1,0,2));
        panCen.add(panEdit);
        
        JPanel panbaixo = new JPanel(new BorderLayout());
        
        panbaixo.add(panButEdit,BorderLayout.NORTH);
        panbaixo.add(panTabelas,BorderLayout.CENTER);
        
        panCen.add(panbaixo);

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
        this.jtaColumn.append((String) this.jcbColunas.getSelectedItem());
    }

    @Override
    protected void novo(){
        if(!this.jtaColumn.getText().equals("")){
        	if(this.jcbTipo.getSelectedIndex()==0)
        		this.projectionList.add(new ColumnElement(this.jtaColumn.getText(),this.jtfAlias.getText()));
        	else{
                    if(!this.jtfAlias.getText().equals(""))
                        this.agroupmentList.add(new ColumnElement(this.jtaColumn.getText(),this.jtfAlias.getText()));
                    else{
                        GUIController.getInstance().showMessageDialog("Para agregacao se faz necessario informar o apelido!",2);
                        return;
                    }                        
                }    
        }
        this.limparTela();
    }

    @Override
    protected void alterar(){
        if(!this.jtaColumn.getText().equals("")){
        	int index;
        	ColumnElement col;
        	if(this.jcbTipo.getSelectedIndex()==0){
        		index = this.tabelaProj.getSelectedRow();
        		col = this.projectionList.get(index);
        	}
        	else{
        		index = this.tabelaAgro.getSelectedRow();
        		col = this.agroupmentList.get(index);
                        if(this.jtfAlias.getText().equals("")){
                            GUIController.getInstance().showMessageDialog("Para agregacao se faz necessario informar o apelido!",2);
                            return;
                        } 
                }
        	col.setProjection(this.jtaColumn.getText());
            col.setAlias(this.jtfAlias.getText());
        }
        this.limparTela();
    }

    @Override
    protected void excluir(){
    	if(this.jcbTipo.getSelectedIndex()==0)
    		this.projectionList.remove(this.tabelaProj.getSelectedRow());
    	else
    		this.agroupmentList.remove(this.tabelaAgro.getSelectedRow());
    	this.limparTela();
    }

    @Override
    protected void limparTela(){
        this.modelProj.fireTableDataChanged();
        this.modelAgro.fireTableDataChanged();
        this.btnMutant.setText("Incluir");
        this.btnDelete.setVisible(false);
        this.jcbTipo.setSelectedIndex(0);
        this.jtaColumn.setText("");
        this.jtfAlias.setText("");
    }

    @Override
    protected void ok() {
        //se n?o foi informado nada
        if((this.projectionList.size()+this.agroupmentList.size())==0){
            GUIController.getInstance().showMessageDialog("Nenhuma Coluna informada!",2);
            return;
        }
        //copiar as listas atuais para o objeto
        this.aoe.setProjectionList(this.projectionList);
        this.aoe.setAgroupmentList(this.agroupmentList);
        //chamar metodo padrao
        try {
            super.ok();
        } catch (SQLException ex) {
            //deu erro, retornar valor inicial
            this.aoe.setProjectionList(this.prevProjectionList);
            this.aoe.setAgroupmentList(this.prevAgroupmentList);
        }
    }
    
    //onchange do combox
    private void comboChange(){
    	if(this.btnDelete.isVisible()){
    		//nao pode mudar
    		this.jcbTipo.setSelectedIndex(Math.abs(this.jcbTipo.getSelectedIndex()-1));    		
    	}
    }
}

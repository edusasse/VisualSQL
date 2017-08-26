package com.edusasse.visualsql.gui.elementsconfig;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import javax.swing.JButton;
import javax.swing.JComboBox;

import com.edusasse.visualsql.gui.GUIController;
import com.edusasse.visualsql.gui.elements.OperatorElement;

@SuppressWarnings("serial")
public abstract class ElementConfig extends javax.swing.JDialog{

    //atributos default
    private boolean modalResult = false;
    protected OperatorElement operator;
    protected JComboBox jcbColunas;

    public ElementConfig(java.awt.Frame parent, boolean modal, OperatorElement oe) {
        super(parent, modal);
        //incializacao padrao
        this.operator = oe;
        //propriedades da janela
        setDefaultCloseOperation(javax.swing.JDialog.DISPOSE_ON_CLOSE);
        this.setLayout(new BorderLayout());
        //iniciando os componentes
        initComponents();
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cancel();
            }
        });
    }

    protected void initComponents(){
    	this.jcbColunas = new JComboBox(GUIController.getInstance().getQueryColumns("R"+this.operator.getId(),this.operator.getSourceQuery()));
        JButton jbutAddCol = new JButton("Add");
        jbutAddCol.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addColumnOnEdit();
            }
        });
    }

    protected abstract void addColumnOnEdit();

    protected abstract void novo();

    protected abstract void alterar();

    protected abstract void excluir();

    protected abstract void limparTela();

    protected void ok() throws SQLException {
        //deve testar se a nova query estah integra
        GUIController.getInstance().showTableData(this.operator.getQuery());
        //nao ocorreu erro, entao procede normalmente
        this.modalResult = true;
        this.dispose();
    }

    protected void cancel() {
        this.modalResult = false;
        this.dispose();
    }

    public boolean getModalResult() {
        return this.modalResult;
    }

}

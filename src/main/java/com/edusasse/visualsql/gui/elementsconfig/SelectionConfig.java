package com.edusasse.visualsql.gui.elementsconfig;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EtchedBorder;

import com.edusasse.visualsql.gui.GUIController;
import com.edusasse.visualsql.gui.elements.SelectionOperatorElement;

@SuppressWarnings("serial")
public class SelectionConfig extends ElementConfig {

    //atributos
    private SelectionOperatorElement soe;
    private JTextArea jtaRestrict;
    private String prevRestrict;

    /** Creates new form jPopProjection */
    public SelectionConfig(java.awt.Frame parent, boolean modal, SelectionOperatorElement soe) {
        super(parent,modal,soe);
        //propriedades da janela
        this.setTitle("Configuracao da Selecao");
        this.setBounds(50, 50, 250, 250);
        this.setLocationRelativeTo(null);
        //this.pack();
    }

    // criacao dos componentes
    @Override
    protected void initComponents() {
        super.initComponents();

        this.soe = (SelectionOperatorElement) super.operator;
        this.prevRestrict = this.soe.getRestrict();

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
        
        this.getContentPane().add(panTop,BorderLayout.NORTH);

        jtaRestrict = new JTextArea(this.soe.getRestrict());
        jtaRestrict.setLineWrap(true);
        jtaRestrict.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        jtaRestrict.setRows(6);
        this.getContentPane().add(jtaRestrict,BorderLayout.CENTER);

        JPanel panBut = new JPanel(new GridLayout(1,2));

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
        
        this.getContentPane().add(panBut,BorderLayout.SOUTH);
        
    }

    protected void addColumnOnEdit(){
        this.jtaRestrict.append((String) this.jcbColunas.getSelectedItem());
    }

    @Override
    protected void ok(){
        //se n?o foi informado nadaprojectionList.size()+
        if(this.jtaRestrict.getText().equals("")){
            GUIController.getInstance().showMessageDialog("Nenhuma restricao informada!",2);
            return;
        }
        //gravar restricao
        this.soe.setRestrict(this.jtaRestrict.getText());
        //chamar metodo padrao
        try {
            super.ok();
        } catch (SQLException ex) {
            //deu erro, retornar o valor anterior
            this.soe.setRestrict(this.prevRestrict);
        }
    }

    @Override
    protected void novo() {
        
    }

    @Override
    protected void alterar() {

    }

    @Override
    protected void excluir() {

    }

    @Override
    protected void limparTela() {

    }

}

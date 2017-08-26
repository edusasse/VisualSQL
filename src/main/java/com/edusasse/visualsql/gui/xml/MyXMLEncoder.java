package com.edusasse.visualsql.gui.xml;

import java.awt.Point;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import com.edusasse.visualsql.db.jdbc.ConnController;
import com.edusasse.visualsql.db.jdbc.ConnectionHandler;
import com.edusasse.visualsql.gui.GUIController;
import com.edusasse.visualsql.gui.conntree.MyTreeListener;
import com.edusasse.visualsql.gui.elements.AgroupmentOperatorElement;
import com.edusasse.visualsql.gui.elements.CartesianProductOperatorElement;
import com.edusasse.visualsql.gui.elements.ColumnElement;
import com.edusasse.visualsql.gui.elements.LineElement;
import com.edusasse.visualsql.gui.elements.LinkedDataElement;
import com.edusasse.visualsql.gui.elements.MyElement;
import com.edusasse.visualsql.gui.elements.ProjectionOperatorElement;
import com.edusasse.visualsql.gui.elements.SelectionOperatorElement;
import com.edusasse.visualsql.gui.elements.TableElement;
import com.edusasse.visualsql.gui.elements.UnionOperatorElement;

public class MyXMLEncoder {

    private static MyXMLEncoder mxmle = null;

    public MyXMLEncoder() {
    }

    public static MyXMLEncoder getInstanceOf() {
        if (mxmle == null) {
            mxmle = new MyXMLEncoder();
        }
        return mxmle;
    }

    public String save(String name, ArrayList<MyElement> items) {
        Element root = new Element("VisualSQL");
        Attribute atribVSqlVersion = new Attribute("Version", GUIController.getVersion());
        Attribute atribSavedFileName = new Attribute("SavedFileName", name);
        root.setAttribute(atribVSqlVersion);
        root.setAttribute(atribSavedFileName);

        xmlDBConnection(root);

        // Guarda primeiramente as linhas
        for (int i = 0; i < GUIController.getInstance().getCurrentViewDiagram().getItems().size(); i++) {
            MyElement item = (MyElement) GUIController.getInstance().getCurrentViewDiagram().getItems().get(i);
            if (item.getKind().equals("LineElement")) {
                xmlLineElement(root, (LineElement) item);
            }
        }
        // Guarda demais objetos
        for (int i = 0; i < GUIController.getInstance().getCurrentViewDiagram().getItems().size(); i++) {
            MyElement item = (MyElement) GUIController.getInstance().getCurrentViewDiagram().getItems().get(i);
            // ProjectionOperatorElement
            if (item.getKind().equals("ProjectionOperatorElement")) {
                xmlProjectionOperatorElement(root, (ProjectionOperatorElement) item);
            } // TableElement

            if (item.getKind().equals("TableElement")) {
                xmlTableElement(root, (TableElement) item);
            } // SelectionOperatorElement

            if (item.getKind().equals("SelectionOperatorElement")) {
                xmlSelectionOperatorElement(root, (SelectionOperatorElement) item);
            } // AgroupmentOperatorElement

            if (item.getKind().equals("AgroupmentOperatorElement")) {
                xmlAgroupmentOperatorElement(root, (AgroupmentOperatorElement) item);
            } // CartesianProductOperatorElement

            if (item.getKind().equals("CartesianProductOperatorElement")) {
                xmlCartesianProductOperatorElement(root, (CartesianProductOperatorElement) item);
            } // UnionOperatorElement

            if (item.getKind().equals("UnionOperatorElement")) {
                xmlUnionOperatorElement(root, (UnionOperatorElement) item);
            }
        }


        String a = ElementToString(root);

        return a;


    }

    public void open(String xml) {

        // Criamos uma classe SAXBuilder que vai processar o XML
        SAXBuilder sb = new SAXBuilder();

        GUIController.getInstance().getCurrentViewDiagram().clear();

        // Este documento agora possui toda a estrutura do arquivo.
        Document d = null;
        try {
            d = sb.build(new StringReader(xml));
        } catch (JDOMException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Recuperamos o elemento root
        Element root = d.getRootElement();

        // ############## Refaz a Conexao
        remakeDBConnection(root.getChild("Connection"));
        // ############## Refaz os Arcos
        // Lista dos itens LineElement
        HashMap<Integer, TempLineElement> lines = new HashMap<Integer, TempLineElement>();
        for (int i = 0; i < root.getChildren().size(); i++) {
            // Pega os filhos do noh principal
            final Element ele = (Element) root.getChildren().get(i);
            // Caso seja um ## ProjectionOperatorElement ##
            if (ele.getName().equals("LineElement")) {
                remakeLineElement(lines, ele);
            }
        }
        // ############## Refaz Elementos
        // Lista os demais elementos
        for (int i = 0; i < root.getChildren().size(); i++) {
            // Pega os filhos do noh principal
            final Element ele = (Element) root.getChildren().get(i);
            // Caso seja um ## ProjectionOperatorElement ##
            if (ele.getName().equals("ProjectionOperatorElement")) {
                remakeProjectionOperatorElement(lines, ele);
            }
            // Caso seja um ## TableElement ##
            if (ele.getName().equals("SelectionOperatorElement")) {
                remakeSelectionOperatorElement(lines, ele);
            }
            // Caso seja um ## TableElement ##
            if (ele.getName().equals("TableElement")) {
                remakeTableElement(lines, ele);
            }
            // Caso seja um ## AgroupmentOperatorElement ##
            if (ele.getName().equals("AgroupmentOperatorElement")) {
                remakeAgroupmentOperatorElement(lines, ele);
            }
            // Caso seja um ## CartesianProductOperatorElement ##
            if (ele.getName().equals("CartesianProductOperatorElement")) {
                remakeCartesianProductOperatorElement(lines, ele);
            }
            // Caso seja um ## UnionOperatorElement ##
            if (ele.getName().equals("UnionOperatorElement")) {
                remakeUnionOperatorElement(lines, ele);
            }
        }

        // ############## Refaz os nohs nos arcos
        // Retorna os Nos
        for (int i = 0; i < GUIController.getInstance().getCurrentViewDiagram().getItems().size(); i++) {
            boolean passa = true;
            try {
                final LinkedDataElement li = (LinkedDataElement) GUIController.getInstance().getCurrentViewDiagram().getItems().get(i);
            } catch (ClassCastException cce) {
                passa = false;
            }

            if (passa) {
                final LinkedDataElement li = (LinkedDataElement) GUIController.getInstance().getCurrentViewDiagram().getItems().get(i);
                if (li.getArcos() != null) {
                    for (int j = 0; j < li.getArcos().size(); j++) {
                        final LineElement le = (LineElement) ((LinkedDataElement) GUIController.getInstance().getCurrentViewDiagram().getItems().get(i)).getArcos().get(j);
                        le.setNohInicial((LinkedDataElement) this.getMyElementId(lines.get(le.getId()).getIdNoInicial()));
                        le.setNohFinal((LinkedDataElement) this.getMyElementId(lines.get(le.getId()).getIdNofinal()));
                    }
                }
            }
        }
    }

    /* ###### ELEMENTOS ###### */

    /* ProjectionOperatorElement */
    private void remakeProjectionOperatorElement(HashMap<Integer, TempLineElement> lines, Element ele) {
        // Elemento a ser remontado
        ProjectionOperatorElement poe = null;

        final int id = Integer.parseInt(ele.getAttributeValue("id"));
        // Point necessario para criar o elemento
        Element point = (Element) ele.getChild("Point");
        final double x = Double.parseDouble(point.getAttributeValue("x"));
        final double y = Double.parseDouble(point.getAttributeValue("y"));

        // Criacao do elemento
        poe = new ProjectionOperatorElement(id, x, y);

        // Lista de Projecoes
        Element proj = (Element) ele.getChild("Projections");
        List projs = proj.getChildren("Projection");
        if (projs != null) {
            ArrayList<ColumnElement> projectionList = new ArrayList<ColumnElement>();
            for (int j = 0; j < projs.size(); j++) {
                final Element e = (Element) projs.get(j);
                projectionList.add(new ColumnElement(e.getAttributeValue("name"), e.getAttributeValue("alias")));

            }
            poe.setProjectionList(projectionList);
        }

        // Arcos
        remakeArcos(lines, ele, poe);

        // Adiciona  lista de itens
        GUIController.getInstance().getCurrentViewDiagram().addItem(poe);
    }

    public void xmlProjectionOperatorElement(Element root, ProjectionOperatorElement o) {
        Element poeRoot = new Element("ProjectionOperatorElement");
        Attribute atribId = new Attribute("id", String.valueOf(o.getId()), Attribute.ID_TYPE);
        poeRoot.setAttribute(atribId);

        // Point
        this.storePoint(o.getPoint(), poeRoot);

        // Projecoes ------
        Element myroot = new Element("Projections");
        for (ColumnElement i : o.getProjectionList()) {
            Element r = new Element("Projection");
            Attribute en = new Attribute("name", i.getColumn() + "");
            Attribute en1 = new Attribute("alias", i.getAlias() + "");
            r.setAttribute(en);
            r.setAttribute(en1);
            myroot.addContent(r);
        }
        poeRoot.addContent(myroot);

        // Arcos
        guardaArcos(poeRoot, o);

        // Adiciona ao elemento pai
        root.addContent(poeRoot);



    }

    /* TableElement */
    private void remakeTableElement(HashMap<Integer, TempLineElement> lines, Element ele) {
        // Elemento a ser remontado
        TableElement te = null;

        final int id = Integer.parseInt(ele.getAttributeValue("id"));
        // Propriedade de DB
        Element point = (Element) ele.getChild("Point");
        final int x = Integer.parseInt(point.getAttributeValue("x"));
        final int y = Integer.parseInt(point.getAttributeValue("y"));

        // Point necessario para criar o elemento
        Element db = (Element) ele.getChild("DB");
        final String dbName = db.getAttributeValue("dbName");
        final String tableName = db.getAttributeValue("tableName");

        // Criacao do elemento
        te = new TableElement(id, dbName, tableName, x, y, GUIController.getInstance().getCurrentViewDiagram());

        // Arcos
        remakeArcos(lines, ele, te);

        //Adiciona a lista de itens
        GUIController.getInstance().getCurrentViewDiagram().addItem(te);
    }

    public void xmlTableElement(Element root, TableElement o) {
        Element myroot = new Element("TableElement");
        Attribute atribId = new Attribute("id", String.valueOf(o.getId()), Attribute.ID_TYPE);
        myroot.setAttribute(atribId);

        // Propriedades de DB ------
        Element mydb = new Element("DB");
        Attribute atribDbName = new Attribute("dbName", o.getDbName());
        Attribute atribTable = new Attribute("tableName", o.getTableName());
        mydb.setAttribute(atribDbName);
        mydb.setAttribute(atribTable);

        // Adiciona ao elemento pai
        myroot.addContent(mydb);

        // Point
        Element mypoint = new Element("Point");
        Attribute atribX = new Attribute("x", "" + o.getPoint().x);
        Attribute atribY = new Attribute("y", "" + o.getPoint().y);
        mypoint.setAttribute(atribX);
        mypoint.setAttribute(atribY);
        // Adiciona ao elemento pai
        myroot.addContent(mypoint);

        // Arcos
        guardaArcos(myroot, o);

        // Adiciona ao elemento paizao
        root.addContent(myroot);
    }

    /* SelectionOperatorElement */
    private void remakeSelectionOperatorElement(HashMap<Integer, TempLineElement> lines, Element ele) {
        // Elemento a ser remontado
        SelectionOperatorElement seo = null;

        final int id = Integer.parseInt(ele.getAttributeValue("id"));
        // Propriedade de Localizacao
        Element point = (Element) ele.getChild("Point");
        final double x = Double.parseDouble(point.getAttributeValue("x"));
        final double y = Double.parseDouble(point.getAttributeValue("y"));

        // Criacao do elemento
        seo = new SelectionOperatorElement(id, x, y);
        // Point necessario para criar o elemento
        Element restr = (Element) ele.getChild("Restriction");
        //if (restr != null && restr.getContent().size() > 0) {
        //    final String r = restr.getContent().get(0).toString();
        seo.setRestrict(restr.getText());
        // Arcos
        remakeArcos(lines, ele, seo);

        //Adiciona a lista de itens
        GUIController.getInstance().getCurrentViewDiagram().addItem(seo);
    }

    public void xmlSelectionOperatorElement(Element root, SelectionOperatorElement o) {
        Element myroot = new Element("SelectionOperatorElement");
        Attribute atribId = new Attribute("id", String.valueOf(o.getId()), Attribute.ID_TYPE);
        myroot.setAttribute(atribId);

        // Propriedades de DB ------
        Element myrestr = new Element("Restriction");
        myrestr.addContent(o.getRestrict());
        // Adiciona ao elemento pai
        myroot.addContent(myrestr);

        // Point
        this.storePoint(o.getPoint(), myroot);

        // Arcos
        guardaArcos(myroot, o);

        // Adiciona ao elemento paizao
        root.addContent(myroot);
    }

    /* AgroupmentOperatorElement */
    private void remakeAgroupmentOperatorElement(HashMap<Integer, TempLineElement> lines, Element ele) {
        // Elemento a ser remontado
        AgroupmentOperatorElement aoe = null;

        final int id = Integer.parseInt(ele.getAttributeValue("id"));
        // Propriedade de Localizacao
        Element point = (Element) ele.getChild("Point");
        final double x = Double.parseDouble(point.getAttributeValue("x"));
        final double y = Double.parseDouble(point.getAttributeValue("y"));

        // Criacao do elemento
        aoe = new AgroupmentOperatorElement(id, x, y);

        // Lista de Projecoes
        Element proj = (Element) ele.getChild("Projections");
        List projs = proj.getChildren("Projection");
        if (projs != null) {
            ArrayList<ColumnElement> projectionList = new ArrayList<ColumnElement>();
            for (int j = 0; j < projs.size(); j++) {
                final Element e = (Element) projs.get(j);
                projectionList.add(new ColumnElement(e.getAttributeValue("name"), e.getAttributeValue("alias")));

            }
            aoe.setProjectionList(projectionList);
        }

        // Lista de Restricoes
        Element agrup = (Element) ele.getChild("Agroupments");
        List agrups = agrup.getChildren("Agroupment");
        if (agrups != null) {
            ArrayList<ColumnElement> agroupmentList = new ArrayList<ColumnElement>();
            for (int j = 0; j < agrups.size(); j++) {
                final Element e = (Element) agrups.get(j);
                agroupmentList.add(new ColumnElement(e.getAttributeValue("name"), e.getAttributeValue("alias")));

            }
            aoe.setAgroupmentList(agroupmentList);
        }

        // Arcos
        remakeArcos(lines, ele, aoe);

        //Adiciona a lista de itens
        GUIController.getInstance().getCurrentViewDiagram().addItem(aoe);
    }

    public void xmlAgroupmentOperatorElement(Element root, AgroupmentOperatorElement o) {
        Element myroot = new Element("AgroupmentOperatorElement");
        Attribute atribId = new Attribute("id", String.valueOf(o.getId()), Attribute.ID_TYPE);
        myroot.setAttribute(atribId);

        // Point
        this.storePoint(o.getPoint(), myroot);

        // Projecoes ------
        Element poeroot = new Element("Projections");
        for (ColumnElement i : o.getProjectionList()) {
            Element r = new Element("Projection");
            Attribute en = new Attribute("name", i.getColumn() + "");
            Attribute en1 = new Attribute("alias", i.getAlias() + "");
            r.setAttribute(en);
            r.setAttribute(en1);
            poeroot.addContent(r);
        }
        myroot.addContent(poeroot);

        // Agrupamentos ------
        Element agruproot = new Element("Agroupments");
        for (ColumnElement i : o.getAgroupmentList()) {
            Element r = new Element("Agroupment");
            Attribute en = new Attribute("name", i.getColumn() + "");
            Attribute en1 = new Attribute("alias", i.getAlias() + "");
            r.setAttribute(en);
            r.setAttribute(en1);
            agruproot.addContent(r);
        }
        myroot.addContent(agruproot);

        // Arcos
        guardaArcos(myroot, o);

        // Adiciona ao elemento paizao
        root.addContent(myroot);
    }

    /* CartesianProductOperatorElement */
    private void remakeCartesianProductOperatorElement(HashMap<Integer, TempLineElement> lines, Element ele) {
        // Elemento a ser remontado
        CartesianProductOperatorElement cpoe = null;

        final int id = Integer.parseInt(ele.getAttributeValue("id"));
        // Propriedade de Localizacao
        Element point = (Element) ele.getChild("Point");
        final double x = Double.parseDouble(point.getAttributeValue("x"));
        final double y = Double.parseDouble(point.getAttributeValue("y"));


        // Criacao do elemento
        cpoe = new CartesianProductOperatorElement(id, x, y);

        Element restr = (Element) ele.getChild("Restriction");
        //if (restr != null && restr.getContent().size() > 0) {
        //    final String r = restr.getContent().get(0).toString();
        cpoe.setRestrict(restr.getText());
        //}

        // Arcos
        remakeArcos(lines, ele, cpoe);

        //Adiciona a lista de itens
        GUIController.getInstance().getCurrentViewDiagram().addItem(cpoe);
    }

    public void xmlCartesianProductOperatorElement(Element root, CartesianProductOperatorElement o) {
        Element myroot = new Element("CartesianProductOperatorElement");
        Attribute atribId = new Attribute("id", String.valueOf(o.getId()), Attribute.ID_TYPE);
        myroot.setAttribute(atribId);


        // Point
        this.storePoint(o.getPoint(), myroot);

        Element myrestr = new Element("Restriction");
        myrestr.addContent(o.getRestrict());
        // Adiciona ao elemento pai
        myroot.addContent(myrestr);

        // Arcos
        guardaArcos(myroot, o);

        // Adiciona ao elemento paizao
        root.addContent(myroot);
    }

    /* UnionOperatorElement */
    private void remakeUnionOperatorElement(HashMap<Integer, TempLineElement> lines, Element ele) {
        // Elemento a ser remontado
        UnionOperatorElement upe = null;

        final int id = Integer.parseInt(ele.getAttributeValue("id"));
        // Propriedade de Localizacao
        Element point = (Element) ele.getChild("Point");
        final double x = Double.parseDouble(point.getAttributeValue("x"));
        final double y = Double.parseDouble(point.getAttributeValue("y"));


        // Criacao do elemento
        upe = new UnionOperatorElement(id, x, y);

        // Arcos
        remakeArcos(lines, ele, upe);

        //Adiciona a lista de itens
        GUIController.getInstance().getCurrentViewDiagram().addItem(upe);
    }

    public void xmlUnionOperatorElement(Element root, UnionOperatorElement o) {
        Element myroot = new Element("UnionOperatorElement");
        Attribute atribId = new Attribute("id", String.valueOf(o.getId()), Attribute.ID_TYPE);
        myroot.setAttribute(atribId);

        // Point
        this.storePoint(o.getPoint(), myroot);

        // Arcos
        guardaArcos(myroot, o);

        // Adiciona ao elemento paizao
        root.addContent(myroot);
    }

    /* LineElement */
    private void remakeLineElement(HashMap<Integer, TempLineElement> lines, Element ele) {
        // Elemento a ser remontado
        LineElement le = null;
        final int id = Integer.parseInt(ele.getAttributeValue("id"));
        // Propriedade Noss
        Element noh = (Element) ele.getChild("Noh");
        final int nohini = Integer.parseInt(noh.getAttributeValue("idInicial"));
        final int nohfim = Integer.parseInt(noh.getAttributeValue("idFinal"));
        le = new LineElement(id);
        // Criacao do elemento
        lines.put(id, (new TempLineElement(le, nohini, nohfim)));

        //Adiciona a lista de itens
        GUIController.getInstance().getCurrentViewDiagram().addItem(le);
    }

    public void xmlLineElement(Element root, LineElement o) {
        Element myroot = new Element("LineElement");
        Attribute atribId = new Attribute("id", String.valueOf(o.getId()), Attribute.ID_TYPE);
        myroot.setAttribute(atribId);

        // Nos ------
        Element myno = new Element("Noh");
        Attribute atribNohInicial = new Attribute("idInicial", ((MyElement) o.getNohInicial()).getId() + "");
        Attribute atribNohFinal = new Attribute("idFinal", ((MyElement) o.getNohFinal()).getId() + "");
        myno.setAttribute(atribNohInicial);
        myno.setAttribute(atribNohFinal);

        // Adiciona ao elemento pai
        myroot.addContent(myno);

        // Adiciona ao elemento paizao
        root.addContent(myroot);
    }

    /* ###### FUNCORES AUXILIARES ###### */

    /* Arcos */
    private final void guardaArcos(Element myroot, LinkedDataElement o) {
        Element myArcos = new Element("Arcos");
        for (LineElement i : o.getArcos()) {
            Element en = new Element("Arco");
            Attribute a = new Attribute("id", String.valueOf(i.getId()), Attribute.IDREF_TYPE);
            en.setAttribute(a);
            myArcos.addContent(en);
        }
        myroot.addContent(myArcos);

    }

    private void remakeArcos(HashMap<Integer, TempLineElement> lines, Element ele, LinkedDataElement te) {
        Element e = ele.getChild("Arcos");
        if (e == null) {
            return;
        }
        List arcos = e.getChildren("Arco");
        for (int i = 0; i < arcos.size(); i++) {
            final Element my = (Element) arcos.get(i);
            final int arcId = Integer.parseInt(my.getAttributeValue("id"));
            te.addArco(lines.get(arcId).getLineElement());
        }
    }

    /* Point */
    private final void storePoint(Point p, Element root) {
        // POINT ------
        Element mypoint = new Element("Point");
        Attribute atribX = new Attribute("x", "" + p.getLocation().getX());
        Attribute atribY = new Attribute("y", "" + p.getLocation().getY());
        mypoint.setAttribute(atribX);
        mypoint.setAttribute(atribY);
        // Adiciona ao elemento pai
        root.addContent(mypoint);

    }
    /* Retorna MyElemnt pelo Id */

    private final MyElement getMyElementId(int id) {
        for (int i = 0; i < GUIController.getInstance().getCurrentViewDiagram().getItems().size(); i++) {
            if (((MyElement) GUIController.getInstance().getCurrentViewDiagram().getItems().get(i)).getId() == id) {
                return ((MyElement) GUIController.getInstance().getCurrentViewDiagram().getItems().get(i));
            }
        }
        return null;
    }

    /* Converte o elemento para xml em formato String */
    public String ElementToString(Element e) {
        // Inicia o StringWriter
        StringWriter sw = new StringWriter();
        // Inicia o Documento
        Document doc = new Document();
        // Cria o elemento raiz
        XMLOutputter xout = new XMLOutputter();


        doc.setRootElement(e);
        try {
            xout.output(doc, sw);
        } catch (IOException ex) {
            ex.printStackTrace();
        }


        return sw.toString();
    }

    private final class TempLineElement {

        private LineElement le;
        int idNoInicial, idNofinal;

        public TempLineElement(LineElement le, int idNoInicial, int idNofinal) {
            this.le = le;
            this.idNoInicial = idNoInicial;
            this.idNofinal = idNofinal;
        }

        public LineElement getLineElement() {
            return le;
        }

        public int getIdNofinal() {
            return idNofinal;
        }

        public int getIdNoInicial() {
            return idNoInicial;
        }
    }

    private void remakeDBConnection(Element root) {
        if (root == null) {
            return;
        }
        final String keycah = root.getAttributeValue("keycah");
        final String currentDb = root.getAttributeValue("currentDb");
        final boolean AUTO_SAVE_ON_EXIT = new Boolean(root.getAttributeValue("AUTO_SAVE_ON_EXIT"));
        final boolean SAVE_PASSWORD = new Boolean(root.getAttributeValue("SAVE_PASSWORD"));
        final boolean AUTO_CONNECT_ON_OPEN = new Boolean(root.getAttributeValue("AUTO_CONNECT_ON_OPEN"));
        final boolean LOAD_META_DATA = new Boolean(root.getAttributeValue("LOAD_META_DATA"));
        final String libary = root.getChild("Libary").getText();
        final String url = root.getChild("URL").getText() + "";
        final String user = root.getChild("Properties").getAttributeValue("user");
        final String password = root.getChild("Properties").getAttributeValue("password");
        
        GUIController.getInstance().getCurrentViewDiagram().setConnKey(keycah);
        GUIController.getInstance().getCurrentViewDiagram().setCurrentDB(currentDb);
        GUIController.getInstance().getCurrentViewDiagram().updateDb();
        
        int r = JOptionPane.YES_OPTION;
        if (ConnController.getHandler(keycah) != null) {
            r = JOptionPane.showConfirmDialog(GUIController.getInstance().getCurrentViewDiagram(), "Conexao ja registrada. Deseja substitir?", "Atencao!", JOptionPane.YES_NO_OPTION);
        }
        if (r == JOptionPane.YES_OPTION) {
            try {
                // Recria conexao
                ConnController.removeHandler(keycah);
                Hashtable<String, Object> par = new Hashtable<String, Object>();
                par.put("AUTO_SAVE_ON_EXIT", AUTO_SAVE_ON_EXIT);
                par.put("SAVE_PASSWORD", SAVE_PASSWORD);
                par.put("AUTO_CONNECT_ON_OPEN", AUTO_CONNECT_ON_OPEN);
                par.put("LOAD_META_DATA", LOAD_META_DATA);
                ConnController.open(libary, keycah, url, user, password, par);
                ((MyTreeListener) GUIController.getInstance().getMain()).newConnAdd();
                if(!GUIController.getInstance().getCurrentViewDiagram().isConnected()){
                    GUIController.getInstance().showMessageDialog("Nao foi possivel refazer a conexao!",0);
                }
            } catch (Exception ex) {
                
            }
        }
        
    }

    private void xmlDBConnection(Element root) {
        final String keycah = GUIController.getInstance().getCurrentViewDiagram().getConnKey();
        ConnectionHandler ch = ConnController.getHandler(keycah);

        if (ch == null) {
            return;
        }

        Element con = new Element("Connection");
        root.addContent(con);

        con.setAttribute(new Attribute("keycah", keycah));
        con.setAttribute(new Attribute("currentDb", GUIController.getInstance().getCurrentViewDiagram().getCurrentDB()));
        con.setAttribute(new Attribute("AUTO_SAVE_ON_EXIT", ch.isAutoSaveOnExit() + ""));
        con.setAttribute(new Attribute("SAVE_PASSWORD", ch.isSavePassword() + ""));
        con.setAttribute(new Attribute("AUTO_CONNECT_ON_OPEN", ch.isAutoConnectOnOpen() + ""));
        con.setAttribute(new Attribute("LOAD_META_DATA", ch.isLoadMetaDataOnConnect() + ""));

        Element elib = new Element("Libary");
        elib.addContent(ch.getDriverClass().toString());
        con.addContent(elib);

        Element eurl = new Element("URL");
        eurl.addContent(ch.getUrl());
        con.addContent(eurl);

        Element eprop = new Element("Properties");
        eprop.setAttribute(new Attribute("user", ch.getInfo().getProperty("user")));
        eprop.setAttribute(new Attribute("password", ch.getInfo().getProperty("password")));
        con.addContent(eprop);


    }
}

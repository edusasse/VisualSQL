package com.edusasse.visualsql.gui.elements;

import java.awt.*;
import java.awt.geom.*;
import java.awt.geom.Ellipse2D.Double;
import java.util.*;

import com.edusasse.visualsql.gui.GUIController;

@SuppressWarnings("serial")
public abstract class OperatorElement extends MyElement implements LinkedDataElement {

    private Point p;
    private int raio = 30;
    private Ellipse2D.Double elipse = null;
    protected ArrayList<LineElement> arcos;
    private Image img;

    public OperatorElement(int id, double x, double y, Image img) {
        super(id);
        this.initComponents(x, y, img);
    }

    public OperatorElement(double x, double y, Image img) {
        super();
        this.initComponents(x, y, img);
    }

    protected final void initComponents(double x, double y, Image img) {
        this.p = new Point();
        p.setLocation(x - (raio / 2), y - (raio / 2));
        this.elipse = new Ellipse2D.Double(x - (raio / 2), y - (raio / 2), raio, raio);
        this.arcos = new ArrayList<LineElement>();
        this.img = img;
    }

    public abstract void configOperator(LineElement arcoAtual);

    @Override
    public abstract int getClasse();

    @Override
    public void addArco(LineElement arco) {
        //adiciona o arco ao operador
        arcos.add(arco);
        //se o arco possuir foi fechado
        if (arco.getNohFinal() != null) {
            //chama a configuracao deste operador
            this.configOperator(arco);
        }
    }

    @Override
    public void removerArco(LineElement arco) {
        arcos.remove(arco);
    }

    @Override
    public void desenhar(Graphics g, int x, int y, boolean selecionado) {
        //definicao da cor
        if (selecionado == true) {
            g.setColor(Color.yellow);
        } else {
            g.setColor(Color.white);
        }
        //desenho de um circulo
        g.fillOval((p.x) - 3, (p.y) - 3, raio, raio);
        g.setColor(Color.black);
        g.drawImage(this.getImage(), p.x, p.y, this.getImage().getWidth(null), this.getImage().getHeight(null), null, null);
    }

    @Override
    public boolean contido(int x, int y) {
        return elipse.contains(x, y);
    }

    @Override
    public void deslocar(int x, int y) {
        p.x = p.x + x;
        p.y = p.y + y;
        // desenhar novamente
        elipse = new Ellipse2D.Double(p.x, p.y, raio * 2, raio * 2);
    }

    public Image getImage() {
        return img;
    }

    @Override
    public Point getPoint() {
        return new Point(p.x, p.y);
    }

    @Override
    public Point getMiddlePoint() {
        return new Point(p.x + (raio / 2), p.y + (raio / 2));
    }

    @Override
    public Rectangle getRectangle() {
        return new Rectangle(this.p.x, this.p.y, this.raio, this.raio);
    }

    @Override
    public abstract String getQuery();

    @Override
    public boolean canBeStart() {
        //pode iniciar aqui caso ja possua um arco fechado nele
        if (this.getSourceArcos().size() == 0) {
            //GUIController.getInstance().showMessageDialog("Este operador nao esta pronto!");
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean canBeEnd() {
        //procedimento padrao para fim, nao deixando fechar o arco
        //neste operador caso ele ja tenha fechado outro arco
        if (this.getSourceArcos().size() == 0) {
            return true;
        } else {
            //GUIController.getInstance().showMessageDialog("Este operador ja foi fechado!");
            return false;
        }
    }

    public Double getElipse() {
        return elipse;
    }

    //busca dos arcos onde esse operador eh o nohFinal
    public ArrayList<LineElement> getSourceArcos() {
        //busca nos nohs, a quantidade de nohs onde esse
        //operador e o nohFinal
        ArrayList<LineElement> arcosTmp = new ArrayList<LineElement>();
        for (int i = 0; i < this.arcos.size(); i++) {
            if (this.arcos.get(i).getNohFinal() == this) {
                arcosTmp.add(this.arcos.get(i));
            }
        }
        return arcosTmp;
    }

    public String getSourceQuery() {
        //query
        String query = "";
        //busca dos nohs onde esse operador eh o nohFinal
        ArrayList<LineElement> arcosTmp = this.getSourceArcos();
        //se existir pelo menos um
        if (arcosTmp.size() > 0 && arcosTmp.get(0) != null) {
            //busca o primeiro arco que foi encontrado
            LineElement arco = this.getSourceArcos().get(0);
            //garantir que esse arco possui um noh inicial
            LinkedDataElement nohInicial = arco.getNohInicial();
            //Se noh nao estiver nullo
            if (nohInicial != null) {
                //buscar a query da relacao ligada pelo arco
                try {
					query = nohInicial.getQuery();
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
            }
        }
        return query;
    }

    @Override
    public ArrayList<LineElement> getArcos() {
        return arcos;
    }

    @Override
    public abstract void restartElement();

}

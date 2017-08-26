package com.edusasse.visualsql.gui.elements;

import java.awt.*;
//import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
//import java.awt.geom.Point2D;

@SuppressWarnings("serial")
public class LineElement extends MyElement {

    private LinkedDataElement nohInicial;
    private LinkedDataElement nohFinal;
    private Line2D currentLine;
    //private int barb = 10;
    //private double phi = Math.toRadians(20);

    public LineElement(LinkedDataElement nohInicial) {
        super();
        this.nohInicial = nohInicial;
        this.currentLine = new Line2D.Float();
    }

    public LineElement(int id) {
        super(id);
    }

    public void setNohInicial(LinkedDataElement nohInicial) {
        this.nohInicial = nohInicial;
    }

    public void setNohFinal(LinkedDataElement nohFinal) {
        this.nohFinal = nohFinal;
    }

    public LinkedDataElement getNohInicial() {
        return nohInicial;
    }

    public LinkedDataElement getNohFinal() {
        return nohFinal;
    }

    private int yCor(int len, double dir) {
        return (int) (len * Math.cos(dir));
    }

    private int xCor(int len, double dir) {
        return (int) (len * Math.sin(dir));
    }

    @Override
    public void desenhar(Graphics g, int x, int y, boolean selecionado) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        //g2.setPaint(Color.RED);
        //g2.draw(getPath(x,y));

        int dist;
        if (this.nohFinal != null) {
            x = nohFinal.getMiddlePoint().x;
            y = nohFinal.getMiddlePoint().y;
            dist = -(int) ((nohFinal.getMiddlePoint().x - nohFinal.getPoint().x) / 2.0) - 4;
            g.setColor(Color.red);
        } else {
            dist = 0;
            g.setColor(Color.darkGray);
        }

        currentLine = new Line2D.Float(nohInicial.getMiddlePoint().x, nohInicial.getMiddlePoint().y, x, y);


        if (nohFinal != null) {
        }

        int tamanho = 4;
        int xCenter = (int) currentLine.getX1();
        int yCenter = (int) currentLine.getY1();
        double aDir = Math.atan2(xCenter - currentLine.getX2(), yCenter - currentLine.getY2());
        int px = (int) currentLine.getX2() - xCor(dist, aDir);
        int py = (int) currentLine.getY2() - yCor(dist, aDir);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));
        g2d.drawLine(px, py, xCenter, yCenter);
        g2d.setStroke(new BasicStroke(1f));
        Polygon polySeta = new Polygon();
        int i1 = 4 + tamanho * 2;
        int i2 = 2 + tamanho;
        polySeta.addPoint(px, py);
        polySeta.addPoint(px + xCor(i1, aDir + .5), py + yCor(i1, aDir + .5));
        polySeta.addPoint(px + xCor(i2, aDir), py + yCor(i2, aDir));
        polySeta.addPoint(px + xCor(i1, aDir - .5), py + yCor(i1, aDir - .5));
        polySeta.addPoint(px, py);
        g2d.drawPolygon(polySeta);
        g2d.fillPolygon(polySeta);
    }

    @Override
    public boolean contido(int x, int y) {
        return false;  // nao consegue selecionar um arco

    }

    @Override
    public void deslocar(int x, int y) {
        // nao desloca uma linha por si soh
    }

    /*
    private GeneralPath getPath(int xParam, int yParam) {
    
    double x1 = nohInicial.getMiddlePoint().x;
    double y1 = nohInicial.getMiddlePoint().y;
    Rectangle r1 = nohInicial.getRectangle();
    
    double x2;
    double y2;
    Rectangle r2;
    
    if (nohFinal != null) {
    x2 = nohFinal.getMiddlePoint().x;
    y2 = nohFinal.getMiddlePoint().y;
    r2 = nohFinal.getRectangle();
    } else {
    x2 = xParam;
    y2 = yParam;
    r2 = new Rectangle((int) x2, (int) y2, 10, 10);
    }
    
    double theta = Math.atan2(y2 - y1, x2 - x1);
    Point2D.Double p1 = getPoint(theta, r1);
    Point2D.Double p2 = getPoint(theta + Math.PI, r2);
    GeneralPath path = new GeneralPath(new Line2D.Float(p1, p2));
    // Add an arrow head at p2.
    double x = p2.x + barb * Math.cos(theta + Math.PI - phi);
    double y = p2.y + barb * Math.sin(theta + Math.PI - phi);
    path.moveTo((float) x, (float) y);
    path.lineTo((float) p2.x, (float) p2.y);
    x = p2.x + barb * Math.cos(theta + Math.PI + phi);
    y = p2.y + barb * Math.sin(theta + Math.PI + phi);
    path.lineTo((float) x, (float) y);
    return path;
    }
    
    private Point2D.Double getPoint(double theta, Rectangle r) {
    double cx = r.getCenterX();
    double cy = r.getCenterY();
    double w = r.width / 2;
    double h = r.height / 2;
    double d = Point2D.distance(cx, cy, cx + w, cy + h);
    double x = cx + d * Math.cos(theta);
    double y = cy + d * Math.sin(theta);
    Point2D.Double p = new Point2D.Double();
    int outcode = r.outcode(x, y);
    switch (outcode) {
    case Rectangle.OUT_TOP:
    p.x = cx - h * ((x - cx) / (y - cy));
    p.y = cy - h;
    break;
    case Rectangle.OUT_LEFT:
    p.x = cx - w;
    p.y = cy - w * ((y - cy) / (x - cx));
    break;
    case Rectangle.OUT_BOTTOM:
    p.x = cx + h * ((x - cx) / (y - cy));
    p.y = cy + h;
    break;
    case Rectangle.OUT_RIGHT:
    p.x = cx + w;
    p.y = cy + w * ((y - cy) / (x - cx));
    break;
    default:
    ;
    }
    return p;
    } */
    @Override
    public String getKind() {
        return "LineElement";
    }
}

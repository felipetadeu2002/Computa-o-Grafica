package com.pucminas.cg;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

//Representa um polígono formado por uma lista de vértices.
public class PolygonObject implements Drawable{
    public List<Point2D> points;
    public PolygonObject(List<Point2D> points){
        //Copia a lista para evitar alterações externas inesperadas.
        this.points = new ArrayList<>(points);
    }

    @Override public void draw(Graphics2D g, AreaConfig config, boolean selected){
        if (points.size() < 2) return;
        g.setColor(selected ? new Color(0xE67E22) : new Color(0x27AE60));

        //Rasteriza cada aresta do polígono.
        for (int i = 0; i < points.size(); i++){
            Point2D a = points.get(i);
            Point2D b = points.get((i + 1) % points.size());
            List<Point> px = config.rasterMode == AreaConfig.RasterMode.DDA ? GraphicsAlgorithms.dda(a, b) : GraphicsAlgorithms.bresenham(a, b);
            for (Point p : px){
                g.fillRect(p.x, p.y, 1, 1);
            }
        }

        //Mostra os vértices quando o polígono está selecionado.
        if (selected) {
            g.setColor(new Color(0xD35400));
            for (Point2D p : points){
                g.drawOval((int) p.x - 4, (int) p.y - 4, 8, 8);
            }
        }
    }

    @Override public Rectangle getBounds(){
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;

        //Encontra os extremos das coordenadas dos vértices.
        for (Point2D p : points){
            minX = Math.min(minX, (int) p.x);
            minY = Math.min(minY, (int) p.y);
            maxX = Math.max(maxX, (int) p.x);
            maxY = Math.max(maxY, (int) p.y);
        }

        return points.isEmpty() ? new Rectangle() : new Rectangle(minX, minY, Math.max(1, maxX - minX + 1), Math.max(1, maxY - minY + 1));
    }

    @Override public boolean contains(Point2D q) {
        if (points.size() < 3) return false;

        //Primeiro verifica se o ponto está sobre alguma aresta.
        for (int i = 0; i < points.size(); i++) {
            Point2D a = points.get(i);
            Point2D b = points.get((i + 1) % points.size());

            if (new LineObject(a, b).contains(q)){
                return true;
            }
        }

        //Teste de ponto dentro do polígono usando o método do raio.
        boolean inside = false;

        for (int i = 0, j = points.size() - 1; i < points.size(); j = i++){
            Point2D pi = points.get(i);
            Point2D pj = points.get(j);

            boolean intersect = (pi.y > q.y) != (pj.y > q.y) && q.x < (pj.x - pi.x) * (q.y - pi.y) / (pj.y - pi.y + 1e-12) + pi.x;
            if (intersect) inside = !inside;
        }
        return inside;
    }

    @Override public List<Point2D> getPoints() {
        return points;
    }

    @Override public String getType() {
        return "Polígono";
    }

    @Override public Drawable copyTransformed(double tx, double ty, double angleDeg, double sx, double sy, boolean reflectX, boolean reflectY){
        //Calcula o centro médio dos vértices para usar como pivô.
        double cx = 0;
        double cy = 0;

        for (Point2D p : points){
            cx += p.x;
            cy += p.y;
        }

        cx /= points.size();
        cy /= points.size();

        Point2D pivot = new Point2D(cx, cy);
        List<Point2D> out = new ArrayList<>();

        //Aplica a transformação em cada vértice.
        for (Point2D p : points){
            out.add(GraphicsAlgorithms.transform(p, tx, ty, angleDeg, sx, sy, reflectX, reflectY, pivot));
        }
        return new PolygonObject(out);
    }
}

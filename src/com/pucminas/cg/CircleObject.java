package com.pucminas.cg;
import java.awt.*;
import java.util.List;

//Representa uma circunferência definida por centro e raio.
public class CircleObject implements Drawable{
    public Point2D center;
    public double radius;
    public CircleObject(Point2D c, double r){
        center = c;
        radius = Math.max(0, r);
    }
    @Override public void draw(Graphics2D g, AreaConfig config, boolean selected){
        g.setColor(selected ? new Color(0xE67E22) : new Color(0x8E44AD));

        //A circunferência é rasterizada pelo algoritmo de Bresenham.
        for (Point p : GraphicsAlgorithms.bresenhamCircle(center, radius)){
            g.fillRect(p.x, p.y, 1, 1);
        }

        //Quando selecionada, mostra também seu contorno.
        if (selected){
            g.drawOval((int) (center.x - radius), (int) (center.y - radius), (int) (radius * 2), (int) (radius * 2));
        }
    }

    @Override public Rectangle getBounds(){
        return new Rectangle((int) (center.x - radius) - 1, (int) (center.y - radius) - 1, (int) (2 * radius) + 3, (int) (2 * radius) + 3);
    }

    @Override public boolean contains(Point2D p){
        //Verifica se a distância do ponto ao centro é próxima do raio.
        return Math.abs(Math.hypot(p.x - center.x, p.y - center.y) - radius) <= 7;
    }

    @Override public List<Point2D> getPoints(){
        return List.of(center);
    }

    @Override public String getType() {
        return "Circunferência";
    }

    @Override public Drawable copyTransformed(double tx, double ty, double angleDeg, double sx, double sy, boolean reflectX, boolean reflectY){
        //O centro é transformado utilizando ele mesmo como pivô.
        Point2D c = GraphicsAlgorithms.transform(center, tx, ty, angleDeg, sx, sy, reflectX, reflectY, center);

        // Usa a média dos fatores de escala para atualizar o raio.
        double factor = (Math.abs(sx) + Math.abs(sy)) / 2.0;

        return new CircleObject(c, radius * factor);
    }
}

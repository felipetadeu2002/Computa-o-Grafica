package com.pucminas.cg;
import java.awt.*;
import java.util.List;

//Representa um ponto como objeto gráfico.
public class PointObject implements Drawable {
    public Point2D p;
    public PointObject(Point2D p){
        this.p = p;
    }

    @Override public void draw(Graphics2D g, AreaConfig config, boolean selected){
        // Muda a cor para indicar se o ponto está selecionado.
        g.setColor(selected ? new Color(0xE67E22) : new Color(0x2C3E50));

        int x = (int) Math.round(p.x);
        int y = (int) Math.round(p.y);

        //Desenha o ponto como um pequeno círculo.
        g.fillOval(x - 4, y - 4, 9, 9);
    }

    @Override public Rectangle getBounds(){
        return new Rectangle((int) p.x - 5, (int) p.y - 5, 10, 10);
    }

    @Override public boolean contains(Point2D q){
        // Considera o ponto selecionado se estiver dentro de um raio de tolerância.
        return Math.hypot(q.x - p.x, q.y - p.y) <= 8;
    }

    @Override public List<Point2D> getPoints(){
        return List.of(p);
    }

    @Override public String getType(){
        return "Ponto";
    }

    @Override public Drawable copyTransformed(double tx, double ty, double angleDeg, double sx, double sy, boolean reflectX, boolean reflectY){
        //Para um ponto, o próprio ponto é utilizado como centro da transformação.
        return new PointObject(GraphicsAlgorithms.transform(p, tx, ty, angleDeg, sx, sy, reflectX, reflectY, p));
    }
}

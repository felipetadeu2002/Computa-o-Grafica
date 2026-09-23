package com.pucminas.cg;
import java.awt.*;
import java.util.List;

/**
 * Representa uma reta definida por dois pontos.
 */
public class LineObject implements Drawable{
    public Point2D a, b;
    public LineObject(Point2D a, Point2D b){
        this.a = a;
        this.b = b;
    }

    @Override public void draw(Graphics2D g, AreaConfig config, boolean selected) {
        g.setColor(selected ? new Color(0xE67E22) : new Color(0x1F4E79));

        //Escolhe o algoritmo de rasterização configurado na interface.
        List<Point> pixels = config.rasterMode == AreaConfig.RasterMode.DDA ? GraphicsAlgorithms.dda(a, b) : GraphicsAlgorithms.bresenham(a, b);

        //Cada pixel retornado pelo algoritmo é desenhado como um ponto de 1x1.
        for (Point p : pixels){
            g.fillRect(p.x, p.y, 1, 1);
        }

        //Quando selecionada, a reta mostra seus dois pontos extremos.
        if (selected){
            g.setColor(new Color(0xD35400));
            g.drawOval((int) a.x - 4, (int) a.y - 4, 8, 8);
            g.drawOval((int) b.x - 4, (int) b.y - 4, 8, 8);
        }
    }

    @Override public Rectangle getBounds() {
        int x = (int) Math.floor(Math.min(a.x, b.x));
        int y = (int) Math.floor(Math.min(a.y, b.y));
        int w = Math.max(1, (int) Math.ceil(Math.abs(a.x - b.x)));
        int h = Math.max(1, (int) Math.ceil(Math.abs(a.y - b.y)));
        return new Rectangle(x, y, w + 1, h + 1);
    }

    @Override public boolean contains(Point2D p){
        double dx = b.x - a.x;
        double dy = b.y - a.y;
        double len2 = dx * dx + dy * dy;

        //Trata o caso de uma reta degenerada, formada por um único ponto.
        if (len2 < 1e-9){
            return Math.hypot(p.x - a.x, p.y - a.y) <= 7;
        }

        //Calcula a projeção do ponto sobre o segmento da reta.
        double t = ((p.x - a.x) * dx + (p.y - a.y) * dy) / len2;
        t = Math.max(0, Math.min(1, t));

        double px = a.x + t * dx;
        double py = a.y + t * dy;

        //Usa uma pequena tolerância para facilitar a seleção com o mouse.
        return Math.hypot(p.x - px, p.y - py) <= 7;
    }

    @Override public List<Point2D> getPoints(){
        return List.of(a, b);
    }

    @Override public String getType(){
        return "Reta";
    }

    @Override public Drawable copyTransformed(double tx, double ty, double angleDeg, double sx, double sy, boolean reflectX, boolean reflectY){
        //O centro da reta é utilizado como pivô para rotação, escala e reflexão.
        Point2D pivot = new Point2D((a.x + b.x) / 2, (a.y + b.y) / 2);
        return new LineObject(GraphicsAlgorithms.transform(a, tx, ty, angleDeg, sx, sy, reflectX, reflectY, pivot), GraphicsAlgorithms.transform(b, tx, ty, angleDeg, sx, sy, reflectX, reflectY, pivot));
    }
}

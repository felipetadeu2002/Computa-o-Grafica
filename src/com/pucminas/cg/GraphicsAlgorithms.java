package com.pucminas.cg;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * Reúne os algoritmos de Computação Gráfica utilizados no projeto.
 *
 * Implementa:
 * - DDA para retas;
 * - Bresenham para retas;
 * - Bresenham para circunferências;
 * - transformações geométricas 2D;
 * - Cohen-Sutherland para recorte;
 * - Liang-Barsky para recorte.
 */
public final class GraphicsAlgorithms{

    //Classe utilitária: não possui instâncias.
    private GraphicsAlgorithms() {}

    //Rasteriza uma reta utilizando o algoritmo DDA.
    public static List<Point> dda(Point2D a, Point2D b){
        List<Point> pixels = new ArrayList<>();
        int x1 = (int) Math.round(a.x);
        int y1 = (int) Math.round(a.y);
        int x2 = (int) Math.round(b.x);
        int y2 = (int) Math.round(b.y);
        int dx = x2 - x1;
        int dy = y2 - y1;

        //O maior deslocamento determina a quantidade de passos.
        int steps = Math.max(Math.abs(dx), Math.abs(dy));

        //Caso especial: a reta é apenas um ponto.
        if (steps == 0){
            pixels.add(new Point(x1, y1));
            return pixels;
        }

        //Calcula quanto X e Y avançam a cada passo.
        double xInc = dx / (double) steps;
        double yInc = dy / (double) steps;
        double x = x1;
        double y = y1;

        //Gera os pixels da reta arredondando as coordenadas.
        for (int i = 0; i <= steps; i++){
            pixels.add(new Point((int) Math.round(x), (int) Math.round(y)));
            x += xInc;
            y += yInc;
        }
        return pixels;
    }

    /**
     * Rasteriza uma reta utilizando o algoritmo de Bresenham.
     *
     * O algoritmo utiliza apenas operações incrementais para decidir
     * qual próximo pixel deve ser desenhado.
     */
    public static List<Point> bresenham(Point2D a, Point2D b){
        List<Point> pixels = new ArrayList<>();
        int x0 = (int) Math.round(a.x);
        int y0 = (int) Math.round(a.y);
        int x1 = (int) Math.round(b.x);
        int y1 = (int) Math.round(b.y);

        //Diferenças absolutas e sentidos de crescimento dos eixos.
        int dx = Math.abs(x1 - x0);
        int sx = x0 < x1 ? 1 : -1;
        int dy = -Math.abs(y1 - y0);
        int sy = y0 < y1 ? 1 : -1;

        //Erro acumulado utilizado para escolher o próximo pixel.
        int err = dx + dy;

        while (true){
            pixels.add(new Point(x0, y0));

            // O ponto final já foi alcançado.
            if (x0 == x1 && y0 == y1) break;

            int e2 = 2 * err;

            if (e2 >= dy){
                err += dy;
                x0 += sx;
            }

            if (e2 <= dx){
                err += dx;
                y0 += sy;
            }
        }
        return pixels;
    }

    //Rasteriza uma circunferência utilizando Bresenham.
    public static List<Point> bresenhamCircle(Point2D center, double radius){
        List<Point> pixels = new ArrayList<>();
        int xc = (int) Math.round(center.x);
        int yc = (int) Math.round(center.y);
        int r = Math.max(0, (int) Math.round(radius));
        int x = 0;
        int y = r;

        //Parâmetro inicial de decisão do algoritmo.
        int d = 3 - 2 * r;

        while (x <= y){
            //Usa a simetria da circunferência para gerar oito pontos.
            addCircleSymmetry(pixels, xc, yc, x, y);

            if (d < 0){
                d += 4 * x + 6;
            } else{
                d += 4 * (x - y) + 10;
                y--;
            }
            x++;
        }
        return pixels;
    }

    //Adiciona os oito pontos simétricos de um ponto da circunferência.
    private static void addCircleSymmetry(List<Point> pixels, int xc, int yc, int x, int y){
        int[][] pts = {{xc + x, yc + y}, {xc - x, yc + y}, {xc + x, yc - y}, {xc - x, yc - y}, {xc + y, yc + x}, {xc - y, yc + x}, {xc + y, yc - x}, {xc - y, yc - x}};

        for (int[] p : pts){
            pixels.add(new Point(p[0], p[1]));
        }
    }

    /**
     * Aplica translação, reflexão, escala e rotação a um ponto.
     *
     * As transformações que dependem de um centro são realizadas
     * em relação ao pivô recebido.
     */
    public static Point2D transform(Point2D p, double tx, double ty, double angleDeg, double sx, double sy, boolean reflectX, boolean reflectY, Point2D pivot){
        
        //Translada o ponto para que o pivô passe a ser a origem.
        double x = p.x - pivot.x;
        double y = p.y - pivot.y;

        //Reflexão em X inverte Y.
        if (reflectX) y = -y;

        //Reflexão em Y inverte X.
        if (reflectY) x = -x;

        //Aplica escala.
        x *= sx;
        y *= sy;

        //Converte graus para radianos antes de utilizar seno/cosseno.
        double angle = Math.toRadians(angleDeg);

        //Aplica a rotação em torno do pivô.
        double xr = x * Math.cos(angle) - y * Math.sin(angle);
        double yr = x * Math.sin(angle) + y * Math.cos(angle);

        //Retorna o ponto para a posição original e aplica a translação.
        return new Point2D(xr + pivot.x + tx, yr + pivot.y + ty);
    }

    /**
     * Recorta uma reta pelo algoritmo de Cohen-Sutherland.
     *
     * Os pontos são classificados em regiões utilizando códigos de 4 bits.
     */
    public static ClipResult cohenSutherland(Point2D a, Point2D b, double xmin, double ymin, double xmax, double ymax){
        Point2D p0 = a.copy();
        Point2D p1 = b.copy();
        int c0 = outCode(p0, xmin, ymin, xmax, ymax);
        int c1 = outCode(p1, xmin, ymin, xmax, ymax);
        boolean accepted = false;

        while (true){
            //Ambos dentro da janela: reta aceita.
            if ((c0 | c1) == 0){
                accepted = true;
                break;
            }

            //Compartilham uma região externa: reta rejeitada.
            if ((c0 & c1) != 0) break;

            //Seleciona um dos pontos que está fora da janela.
            int out = c0 != 0 ? c0 : c1;
            double x = 0;
            double y = 0;

            //Interseção com a borda superior.
            if ((out & 8) != 0){
                x = p0.x + (p1.x - p0.x) * (ymin - p0.y) / (p1.y - p0.y);
                y = ymin;

            //Interseção com a borda inferior.
            } else if ((out & 4) != 0){
                x = p0.x + (p1.x - p0.x) * (ymax - p0.y) / (p1.y - p0.y);
                y = ymax;

            //Interseção com a borda direita.
            } else if ((out & 2) != 0){
                y = p0.y + (p1.y - p0.y) * (xmax - p0.x) / (p1.x - p0.x);
                x = xmax;

            //Interseção com a borda esquerda.
            } else{
                y = p0.y + (p1.y - p0.y) * (xmin - p0.x) / (p1.x - p0.x);
                x = xmin;
            }

            //Substitui o ponto externo pela interseção encontrada.
            if (out == c0) {
                p0.x = x;
                p0.y = y;
                c0 = outCode(p0, xmin, ymin, xmax, ymax);
            } else {
                p1.x = x;
                p1.y = y;
                c1 = outCode(p1, xmin, ymin, xmax, ymax);
            }
        }
        return new ClipResult(accepted, p0, p1);
    }

    // Gera o código de região utilizado pelo Cohen-Sutherland.
    private static int outCode(Point2D p, double xmin, double ymin, double xmax, double ymax){
        int code = 0;
        if (p.x < xmin) code |= 1; //esquerda
        if (p.x > xmax) code |= 2; //direita
        if (p.y < ymin) code |= 8; //acima
        if (p.y > ymax) code |= 4; //abaixo
        return code;
    }

    /**
     * Recorta uma reta pelo algoritmo de Liang-Barsky.
     *
     * Trabalha com a equação paramétrica da reta e determina
     * os valores mínimo e máximo do parâmetro u.
     */
    public static ClipResult liangBarsky(Point2D a, Point2D b, double xmin, double ymin, double xmax, double ymax){
        double dx = b.x - a.x;
        double dy = b.y - a.y;

        //Coeficientes associados às quatro bordas da janela.
        double[] p = {-dx, dx, -dy, dy};
        double[] q = {a.x - xmin, xmax - a.x,a.y - ymin, ymax - a.y};
        double u1 = 0.0;
        double u2 = 1.0;

        for (int i = 0; i < 4; i++){
            //Reta paralela à borda.
            if (Math.abs(p[i]) < 1e-12){
                //Se estiver fora dessa borda, não existe interseção.
                if (q[i] < 0){
                    return new ClipResult(false, a.copy(), b.copy());
                }
                continue;
            }
            double r = q[i] / p[i];

            // Atualiza o limite de entrada ou saída.
            if (p[i] < 0){
                u1 = Math.max(u1, r);
            } else{
                u2 = Math.min(u2, r);
            }

            //Intervalo inválido: reta rejeitada.
            if (u1 > u2){
                return new ClipResult(false, a.copy(), b.copy());
            }
        }

        //Calcula os novos extremos da reta recortada.
        return new ClipResult(true, new Point2D(a.x + u1 * dx, a.y + u1 * dy), new Point2D(a.x + u2 * dx, a.y + u2 * dy));
    }

    //Resultado comum dos algoritmos de recorte.
    public record ClipResult(boolean accepted, Point2D a, Point2D b) {}
}

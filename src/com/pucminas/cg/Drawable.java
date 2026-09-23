package com.pucminas.cg;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.List;

/**
 * Interface comum aos objetos que podem ser desenhados na área de desenho.
 *
 * Cada objeto gráfico precisa saber como:
 * - desenhar a si mesmo;
 * - calcular seus limites;
 * - verificar seleção por ponto;
 * - fornecer seus pontos;
 * - informar seu tipo;
 * - gerar uma versão transformada.
 */
public interface Drawable{
    //Desenha o objeto usando as configurações atuais.
    void draw(Graphics2D g, AreaConfig config, boolean selected);

    // Retorna o retângulo que envolve o objeto.
    Rectangle getBounds();

    // Verifica se um ponto está sobre ou próximo do objeto.
    boolean contains(Point2D p);

    // Retorna os pontos que formam o objeto.
    List<Point2D> getPoints();

    // Retorna o nome do tipo do objeto.
    String getType();

    // Cria uma cópia do objeto após aplicar as transformações.
    Drawable copyTransformed(double tx, double ty, double angleDeg, double sx, double sy, boolean reflectX, boolean reflectY);
}

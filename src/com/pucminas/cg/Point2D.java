package com.pucminas.cg;
import java.util.Objects;

//Representa um ponto no plano cartesiano da área de desenho.
public class Point2D{
    public double x;
    public double y;

    public Point2D(double x, double y){
        this.x = x;
        this.y = y;
    }

    //Cria uma cópia independente do ponto atual.
    public Point2D copy(){
        return new Point2D(x, y);
    }

    //Compara dois pontos considerando suas coordenadas.
    @Override public boolean equals(Object o){
        if (this == o) return true;
        if (!(o instanceof Point2D p)) return false;
        return Double.compare(p.x, x) == 0 && Double.compare(p.y, y) == 0;
    }

    //Gera um código de hash compatível com equals.
    @Override public int hashCode() {
        return Objects.hash(x, y);
    }
}

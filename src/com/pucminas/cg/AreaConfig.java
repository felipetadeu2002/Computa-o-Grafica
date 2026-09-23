package com.pucminas.cg;

//Mantém as configurações utilizadas pela área de desenho.
public class AreaConfig{
    //Algoritmo de rasterização de retas selecionado pelo usuário.
    public RasterMode rasterMode = RasterMode.BRESENHAM;

    public enum RasterMode{
        DDA, BRESENHAM
    }
}

## Computação Gráfica — PUC Minas

Projeto desenvolvido para o Trabalho Prático da disciplina Computação Gráfica, com foco na implementação de algoritmos clássicos de transformação geométrica, rasterização e recorte de primitivas gráficas.

O sistema possui uma área de desenho gráfica e permite a criação e manipulação de pontos, retas, polígonos e circunferências por meio de cliques e arrastes do mouse.

---

## Integrantes

**Felipe Tadeu Silva**

---

## Professor

**Rosilane Ribeiro da Mota**

---

## Descrição do Projeto

O objetivo do projeto é desenvolver uma aplicação gráfica capaz de demonstrar, de forma interativa, os algoritmos estudados na Unidade 1 da disciplina.

A aplicação permite:

- criar pontos, retas, polígonos e circunferências;
- rasterizar retas utilizando DDA e Bresenham;
- rasterizar circunferências utilizando Bresenham;
- aplicar transformações geométricas 2D;
- selecionar objetos através de uma região retangular;
- realizar recorte de retas utilizando Cohen-Sutherland e Liang-Barsky;
- interagir com a área de desenho por cliques e arrastes.

O trabalho segue os requisitos definidos no roteiro da disciplina, incluindo a apresentação gráfica em uma área correspondente à matriz de pixels e a utilização de interação gráfica em vez de entrada tradicional por teclado/console.

---

## Tecnologias Utilizadas

- **Java**
- **Java Swing**
- **JDK 21 ou superior**
- **jpackage** para geração do instalador Windows
- **Git/GitHub** para versionamento do projeto

---

## Link GIT



---

## Estrutura do Projeto

```text
TP1_Algoritmos_Unidade1/
│
├── src/
│   └── com/
│       └── pucminas/
│           └── cg/
│               ├── Main.java
│               ├── DrawingCanvas.java
│               ├── GraphicsAlgorithms.java
│               ├── Drawable.java
│               ├── CanvasConfig.java
│               ├── Point2D.java
│               ├── PointObject.java
│               ├── LineObject.java
│               ├── PolygonObject.java
│               └── CircleObject.java
│
├── dist/
│   └── TP1-ComputacaoGrafica.jar
│
├── build.bat
├── installer-windows.bat
├── README.md
├── run.bat
└── test.bat



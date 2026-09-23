package com.pucminas.cg;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Área principal de desenho da aplicação.
 *
 * Controla os objetos gráficos, seleção, eventos do mouse,
 * janela de recorte e aplicação das transformações.
 */
public class DrawingArea extends JPanel{
    //Modos de interação disponíveis na área de desenho.
    public enum Mode{
        SELECT, POINT, LINE, POLYGON, CIRCLE, CLIP_WINDOW
    }

    private final List<Drawable> objects = new ArrayList<>();
    private final Set<Integer> selected = new HashSet<>();
    private final AreaConfig config = new AreaConfig();
    private Mode mode = Mode.SELECT;

    //Guarda o primeiro ponto de operações que precisam de dois cliques.
    private Point2D firstPoint;

    //Armazena temporariamente os vértices durante a criação do polígono.
    private final List<Point2D> polygonDraft = new ArrayList<>();

    private Rectangle selectionRect;
    private Rectangle clipRect;
    private Point mousePoint;
    private final JLabel status;

    //Algoritmo de recorte escolhido pelo usuário.
    private String clipAlgorithm = "Cohen-Sutherland";

    public DrawingArea(JLabel status){
        this.status = status;
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(1000, 680));
        setBorder(BorderFactory.createLineBorder(new Color(0xBDC3C7)));

        //Centraliza o tratamento dos eventos do mouse.
        MouseAdapter adapter = new MouseAdapter(){
            Point dragStart;

            @Override public void mousePressed(MouseEvent e){
                dragStart = e.getPoint();
                mousePoint = e.getPoint();

                //Seleção e janela de recorte usam arraste.
                if (mode == Mode.SELECT){
                    selectionRect = new Rectangle(dragStart);
                } else if (mode == Mode.CLIP_WINDOW){
                    selectionRect = new Rectangle(dragStart);
                } else{
                    //Os demais modos usam cliques individuais.
                    handleClick(e.getPoint());
                }
                repaint();
            }

            @Override public void mouseDragged(MouseEvent e){
                mousePoint = e.getPoint();

                //Atualiza visualmente o retângulo enquanto o usuário arrasta.
                if (dragStart != null && (mode == Mode.SELECT || mode == Mode.CLIP_WINDOW)){
                    selectionRect = makeRect(dragStart, e.getPoint());
                }
                repaint();
                updateStatus();
            }

            @Override public void mouseReleased(MouseEvent e){
                if (dragStart != null && (mode == Mode.SELECT || mode == Mode.CLIP_WINDOW)){
                    Rectangle r = makeRect(dragStart, e.getPoint());

                    if (mode == Mode.SELECT){
                        selectionRect = r;
                        selectByRectangle(r);
                    } else{
                        clipRect = r;
                        selectionRect = null;
                        status.setText("Janela de recorte definida. " + "Selecione uma reta e aplique o recorte.");
                    }
                    dragStart = null;
                    repaint();
                }
            }

            @Override public void mouseMoved(MouseEvent e){
                mousePoint = e.getPoint();
                updateStatus();
            }
        };
        addMouseListener(adapter);
        addMouseMotionListener(adapter);
    }

    //Cria um retângulo independente da direção do arraste.
    private Rectangle makeRect(Point a, Point b){
        return new Rectangle(Math.min(a.x, b.x), Math.min(a.y, b.y), Math.abs(a.x - b.x), Math.abs(a.y - b.y));
    }

    //Trata os cliques utilizados para criar os objetos.
    private void handleClick(Point p){
        Point2D point = new Point2D(p.x, p.y);

        switch (mode){
            case POINT ->{
                objects.add(new PointObject(point));
                status.setText("Ponto criado.");
            }

            case LINE ->{
                if (firstPoint == null){
                    firstPoint = point;
                    status.setText("Primeiro ponto da reta definido. " + "Clique no segundo ponto.");
                } else{
                    objects.add(new LineObject(firstPoint, point));
                    firstPoint = null;
                    status.setText("Reta criada.");
                }
            }

            case CIRCLE ->{
                if (firstPoint == null){
                    firstPoint = point;
                    status.setText("Centro definido. Clique para definir o raio.");
                } else{
                    //A distância entre os dois pontos define o raio.
                    double r = Math.hypot(point.x - firstPoint.x,point.y - firstPoint.y);
                    objects.add(new CircleObject(firstPoint, r));
                    firstPoint = null;
                    status.setText("Circunferência criada (Bresenham).");
                }
            }

            case POLYGON ->{
                polygonDraft.add(point);
                status.setText("Vértice adicionado: " + polygonDraft.size() + ". Use 'Finalizar polígono'.");
            }

            default ->{
            }
        }
        repaint();
    }

    //Finaliza um polígono somente quando existem pelo menos três vértices.
    public void finishPolygon(){
        if (polygonDraft.size() >= 3){
            objects.add(new PolygonObject(polygonDraft));
            polygonDraft.clear();
            status.setText("Polígono criado.");
            repaint();
        } else{
            status.setText("Um polígono precisa de pelo menos 3 vértices.");
        }
    }

    //Cancela a operação atual sem apagar os objetos já criados.
    public void cancelAction(){
        firstPoint = null;
        polygonDraft.clear();
        selectionRect = null;
        repaint();
        status.setText("Ação cancelada.");
    }

    //Remove todos os objetos e seleções da área.
    public void clearAll(){
        objects.clear();
        selected.clear();
        clipRect = null;
        polygonDraft.clear();
        repaint();
        status.setText("Área limpa.");
    }

    //Troca o modo de interação e limpa operações temporárias.
    public void setMode(Mode mode){
        this.mode = mode;
        firstPoint = null;
        polygonDraft.clear();
        selectionRect = null;
        status.setText("Modo: " + modeLabel(mode));
        repaint();
    }

    //Converte o enum do modo em um texto apresentado ao usuário.
    private String modeLabel(Mode m){
        return switch (m){
            case SELECT -> "Seleção retangular";
            case POINT -> "Ponto";
            case LINE -> "Reta";
            case POLYGON -> "Polígono";
            case CIRCLE -> "Circunferência";
            case CLIP_WINDOW -> "Janela de recorte";
        };
    }

    //Altera o algoritmo de rasterização de retas.
    public void setRasterMode(AreaConfig.RasterMode mode){
        config.rasterMode = mode;
        repaint();
    }

    public AreaConfig.RasterMode getRasterMode(){
        return config.rasterMode;
    }

    //Define o algoritmo de recorte utilizado.
    public void setClipAlgorithm(String s){
        clipAlgorithm = s;
    }

    public Rectangle getClipRect(){
        return clipRect;
    }

    public List<Drawable> getObjects(){
        return new ArrayList<>(objects);
    }

    public int selectedCount(){
        return selected.size();
    }

    //Seleciona os objetos que possuem interseção com o retângulo indicado.
    private void selectByRectangle(Rectangle r){
        selected.clear();

        for (int i = 0; i < objects.size(); i++){
            if (r.intersects(objects.get(i).getBounds()) || r.contains(objects.get(i).getBounds())) {
                selected.add(i);
            }
        }
        status.setText(selected.size() + " objeto(s) selecionado(s).");
        repaint();
    }

    //Seleciona todos os objetos existentes.
    public void selectAll(){
        selected.clear();

        for (int i = 0; i < objects.size(); i++){
            selected.add(i);
        }

        status.setText(selected.size() + " objeto(s) selecionado(s).");
        repaint();
    }

    //Aplica as transformações aos objetos atualmente selecionados.
    public void transformSelected(double tx, double ty, double angle, double sx, double sy, boolean rx, boolean ry){
        if (selected.isEmpty()){
            status.setText("Selecione objetos primeiro.");
            return;
        }

        //Cria novos objetos transformados e substitui os anteriores.
        for (Integer i : new ArrayList<>(selected)) {
            objects.set(i, objects.get(i).copyTransformed(tx, ty, angle, sx, sy, rx, ry));
        }

        status.setText("Transformação aplicada aos objetos selecionados.");
        repaint();
    }

    // Aplica o algoritmo de recorte escolhido às retas selecionadas.
    public void clipSelected(){
        if (clipRect == null){
            status.setText("Defina primeiro a janela de recorte.");
            return;
        }

        if (selected.isEmpty()){
            status.setText("Selecione pelo menos uma reta.");
            return;
        }

        int changed = 0;
        double xmin = clipRect.x;
        double ymin = clipRect.y;
        double xmax = clipRect.x + clipRect.width;
        double ymax = clipRect.y + clipRect.height;

        //Processa os índices do fim para o início para permitir remoções.
        List<Integer> indices = new ArrayList<>(selected);
        indices.sort(java.util.Comparator.reverseOrder());

        for (Integer i : indices){
            Drawable d = objects.get(i);

            //O recorte implementado é aplicado somente a retas.
            if (!(d instanceof LineObject)) continue;

            LineObject l = (LineObject) d;
            GraphicsAlgorithms.ClipResult r = clipAlgorithm.equals("Liang-Barsky") ? GraphicsAlgorithms.liangBarsky(l.a, l.b, xmin, ymin, xmax, ymax): GraphicsAlgorithms.cohenSutherland(l.a, l.b,xmin, ymin, xmax, ymax);

            if (!r.accepted()){
                //Reta completamente fora da janela.
                objects.remove((int) i);
                changed++;
            } else{
                //Substitui a reta pelos pontos resultantes do recorte.
                objects.set(i,new LineObject(r.a(), r.b()));
                changed++;
            }
        }
        selected.clear();

        status.setText("Recorte " + clipAlgorithm + " aplicado. " + changed + " reta(s) processada(s).");
        repaint();
    }

    //Atualiza a barra de status com a posição atual do mouse.
    private void updateStatus() {
        if (mousePoint != null) {
            status.setText("Modo: " + modeLabel(mode) + " | x=" + mousePoint.x + " y=" + mousePoint.y + " | selecionados=" + selected.size());
        }
    }

    //Desenha todos os elementos da área.
    @Override protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0.create();

        //Desativa antialiasing para manter a visualização próxima da matriz de pixels.
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        drawGrid(g);

        //Desenha os objetos já finalizados.
        for (int i = 0; i < objects.size(); i++){
            objects.get(i).draw(g, config, selected.contains(i));
        }

        //Desenha temporariamente os vértices e arestas do polígono em construção.
        if (!polygonDraft.isEmpty()){
            g.setColor(new Color(0x16A085));

            for (Point2D p : polygonDraft){
                g.fillOval((int) p.x - 3, (int) p.y - 3, 7, 7);
            }

            for (int i = 1; i < polygonDraft.size(); i++){
                Point2D a = polygonDraft.get(i - 1);
                Point2D b = polygonDraft.get(i);

                for (Point p : GraphicsAlgorithms.bresenham(a, b)){
                    g.fillRect(p.x, p.y, 1, 1);
                }
            }
        }

        //Desenha a janela de recorte já definida.
        if (clipRect != null) {
            g.setColor(new Color(0xC0392B));
            g.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1, new float[]{6, 5}, 0 ));
            g.drawRect(clipRect.x, clipRect.y, clipRect.width, clipRect.height);
        }

        //Desenha o retângulo que o usuário está usando para seleção.
        if (selectionRect != null && (mode == Mode.SELECT || mode == Mode.CLIP_WINDOW)){
            g.setColor(new Color(0x2980B9));
            g.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1, new float[]{5, 5}, 0));
            g.drawRect(selectionRect.x, selectionRect.y, selectionRect.width, selectionRect.height);
        }
        g.dispose();
    }

    //Desenha a grade visual da área de desenho.
    private void drawGrid(Graphics2D g){
        g.setColor(new Color(0xECF0F1));

        for (int x = 0; x < getWidth(); x += 20){
            g.drawLine(x, 0, x, getHeight());
        }

        for (int y = 0; y < getHeight(); y += 20){
            g.drawLine(0, y, getWidth(), y);
        }

        //Destaca as linhas de referência da origem.
        g.setColor(new Color(0x95A5A6));
        g.drawLine(0, 0, getWidth(), 0);
        g.drawLine(0, 0, 0, getHeight());
    }
}

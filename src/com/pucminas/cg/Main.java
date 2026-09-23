package com.pucminas.cg;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Classe principal da aplicação.
 *
 * Responsável por criar a janela, os controles da interface
 * e iniciar a área de desenho.
 */
public class Main{

    public static void main(String[] args){
        //Garante que a interface Swing seja criada na thread apropriada.
        SwingUtilities.invokeLater(() ->{

            //Tenta utilizar o visual padrão do sistema operacional.
            try{
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored){
                //Caso não seja possível, a aplicação utiliza o visual padrão do Swing.
            }

            JFrame frame = new JFrame("TP1 - Algoritmos de Computação Gráfica | PUC Minas");

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout(8, 8));

            //Texto apresentado na barra de status.
            JLabel status = new JLabel("Selecione um modo e interaja com a Área de Desenho.");
            status.setBorder(new EmptyBorder(5, 8, 5, 8));

            //Cria a área onde os objetos serão desenhados.
            DrawingArea area = new DrawingArea(status);

            //Adiciona os controles no topo e a área de desenho no centro.
            frame.add(buildToolPanel(area), BorderLayout.NORTH);
            frame.add(new JScrollPane(area), BorderLayout.CENTER);

            //Cria a barra inferior da janela.
            JPanel bottom = new JPanel(new BorderLayout());
            bottom.add(status, BorderLayout.CENTER);

            JLabel info = new JLabel("  Interação principal por cliques/arraste na Área");
            info.setForeground(new Color(0x566573));
            bottom.add(info, BorderLayout.EAST);
            frame.add(bottom, BorderLayout.SOUTH);

            //Define tamanho e posicionamento inicial da janela.
            frame.setSize(1200, 850);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    //Monta o painel com todos os controles da aplicação.
    private static JPanel buildToolPanel(DrawingArea area){
        JPanel root = new JPanel();
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBorder(new EmptyBorder(8, 8, 4, 8));

        //Criação e seleção
        JPanel modes = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 3));
        modes.setBorder(BorderFactory.createTitledBorder("Criação / Seleção"));

        addButton(modes, "Selecionar região", () -> area.setMode(DrawingArea.Mode.SELECT));
        addButton(modes, "Ponto", () -> area.setMode(DrawingArea.Mode.POINT));
        addButton(modes, "Reta", () -> area.setMode(DrawingArea.Mode.LINE));
        addButton(modes, "Polígono",() -> area.setMode(DrawingArea.Mode.POLYGON));
        addButton(modes, "Finalizar polígono", area::finishPolygon);
        addButton(modes, "Circunferência", () -> area.setMode(DrawingArea.Mode.CIRCLE));
        addButton(modes, "Cancelar", area::cancelAction);
        addButton(modes, "Selecionar tudo", area::selectAll);
        addButton(modes, "Limpar", area::clearAll);
        root.add(modes);

        //Rasterização e recorte
        JPanel alg = new JPanel(new FlowLayout(FlowLayout.LEFT, 7, 3));
        alg.setBorder(BorderFactory.createTitledBorder("Rasterização e recorte"));
        alg.add(new JLabel("Retas:"));
        JComboBox<String> raster = new JComboBox<>(new String[]{"DDA", "Bresenham"});

        //Atualiza o algoritmo de rasterização quando o usuário troca a opção.
        raster.addActionListener(e -> area.setRasterMode(raster.getSelectedIndex() == 0 ? AreaConfig.RasterMode.DDA : AreaConfig.RasterMode.BRESENHAM));
        alg.add(raster);
        alg.add(new JLabel("Recorte:"));
        JComboBox<String> clip = new JComboBox<>(new String[]{"Cohen-Sutherland", "Liang-Barsky"});

        //Atualiza o algoritmo de recorte selecionado.
        clip.addActionListener(e -> area.setClipAlgorithm((String) clip.getSelectedItem()));
        alg.add(clip);
        addButton(alg, "Criar janela de recorte", () -> area.setMode(DrawingArea.Mode.CLIP_WINDOW));
        addButton(alg, "Aplicar recorte", area::clipSelected);
        root.add(alg);

        //Transformações 2D
        JPanel transf = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 3));
        transf.setBorder(BorderFactory.createTitledBorder("Transformações 2D (ajustáveis por cliques)"));
        JSlider tx = new JSlider(-200, 200, 0);
        JSlider ty = new JSlider(-200, 200, 0);
        JSlider ang = new JSlider(-180, 180, 0);
        JSlider sx = new JSlider(25, 300, 100);
        JSlider sy = new JSlider(25, 300, 100);

        //Configura o tamanho e a marcação dos controles deslizantes.
        for (JSlider s : new JSlider[]{tx, ty, ang, sx, sy}){
            s.setPreferredSize(new Dimension(115, 35));
            s.setMajorTickSpacing(100);
            s.setPaintTicks(true);
        }

        transf.add(new JLabel("Tx"));
        transf.add(tx);
        transf.add(new JLabel("Ty"));
        transf.add(ty);
        transf.add(new JLabel("Rot°"));
        transf.add(ang);
        transf.add(new JLabel("Esc X%"));
        transf.add(sx);
        transf.add(new JLabel("Esc Y%"));
        transf.add(sy);

        JCheckBox rx = new JCheckBox("Reflexão X");
        JCheckBox ry = new JCheckBox("Reflexão Y");

        transf.add(rx);
        transf.add(ry);

        //Aplica os valores dos controles aos objetos selecionados.
        addButton(transf, "Aplicar transformação", () -> area.transformSelected(tx.getValue(), ty.getValue(),ang.getValue(), sx.getValue() / 100.0, sy.getValue() / 100.0, rx.isSelected(), ry.isSelected()));

        // Retorna todos os controles para seus valores iniciais.
        addButton(transf, "Zerar controles",() -> {
                    tx.setValue(0);
                    ty.setValue(0);
                    ang.setValue(0);
                    sx.setValue(100);
                    sy.setValue(100);
                    rx.setSelected(false);
                    ry.setSelected(false);
                }
        );
        root.add(transf);
        return root;
    }

    //Cria um botão e associa uma ação a ele.
    private static void addButton( JPanel panel, String text, Runnable action){
        JButton b = new JButton(text);
        b.addActionListener(e -> action.run());
        panel.add(b);
    }
}

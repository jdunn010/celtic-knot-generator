package gui;

import knot.generation.Generator;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

public class ButtonPanelGraphCreation extends JPanel implements ActionListener {
    private static JPanel me;
    private static JFileChooser fileChooser = new JFileChooser();

    ButtonPanelGraphCreation() {
        TitledBorder border = new TitledBorder("Graph Controls");
        Font titleFont = new Font("TitleFont", Font.BOLD, 14);
        border.setTitleFont(titleFont);
        this.setBorder(border);
        JButton generateKnotPointButton = new JButton("Generate Knot");
        generateKnotPointButton.setName("generateKnot");
        generateKnotPointButton.setActionCommand("generateKnot");
        generateKnotPointButton.setEnabled(false);
        generateKnotPointButton.setMnemonic(KeyEvent.VK_G);
        ;
        generateKnotPointButton
                .setToolTipText("Generate the knot based on this graph");
        generateKnotPointButton.addActionListener(this);
        this.add(generateKnotPointButton, BorderLayout.LINE_START);
        JButton deleteVertexButton = new JButton("Delete Vertex");
        deleteVertexButton.setName("delVertex");
        deleteVertexButton.setActionCommand("delVertex");
        deleteVertexButton.setEnabled(false);
        deleteVertexButton.setMnemonic(KeyEvent.VK_D);
        ;
        deleteVertexButton
                .setToolTipText("Delete the selected vertex shown in blue");
        deleteVertexButton.addActionListener(this);
        this.add(deleteVertexButton, BorderLayout.CENTER);
        JButton clearAllButton = new JButton("Clear All");
        clearAllButton.setName("clearAll");
        clearAllButton.setActionCommand("clearAll");
        clearAllButton.setEnabled(false);
        clearAllButton.setMnemonic(KeyEvent.VK_C);
        ;
        clearAllButton
                .setToolTipText("Delete all edges and vertices in the graph");
        clearAllButton.addActionListener(this);
        this.add(clearAllButton, BorderLayout.CENTER);
        JButton deleteEdgeButton = new JButton("Delete Edge");
        deleteEdgeButton.setName("delEdge");
        deleteEdgeButton.setActionCommand("delEdge");
        deleteEdgeButton.setEnabled(false);
        deleteEdgeButton.setMnemonic(KeyEvent.VK_D);
        deleteEdgeButton.setToolTipText("Delete the selected edge shown in blue");
        deleteEdgeButton.addActionListener(this);
        this.add(deleteEdgeButton, BorderLayout.CENTER);
        JButton saveGraphButton = new JButton("Save Graph");
        saveGraphButton.setName("saveGraph");
        saveGraphButton.setActionCommand("saveGraph");
        saveGraphButton.setEnabled(false);
        saveGraphButton.setMnemonic(KeyEvent.VK_S);
        saveGraphButton.setToolTipText("Save the current graph");
        saveGraphButton.addActionListener(this);
        this.add(saveGraphButton, BorderLayout.LINE_END);

        JButton loadGraphButton = new JButton("Load Graph");
        loadGraphButton.setName("loadGraph");
        loadGraphButton.setActionCommand("loadGraph");
        loadGraphButton.setMnemonic(KeyEvent.VK_L);
        loadGraphButton.setToolTipText("Load a previously saved graph");
        loadGraphButton.addActionListener(this);
        this.add(loadGraphButton, BorderLayout.LINE_END);
        fileChooser.addChoosableFileFilter(new KnotFileFilter());
        fileChooser.setAcceptAllFileFilterUsed(false);
        me = this;
    }

    static void setEnabledForComponent(String componentName, boolean setEnabled) {
        Component[] components = me.getComponents();
        for (int i = 0; i < components.length; i++) {
            Component temp = components[i];
            if (temp.getName().equals(componentName)) {
                temp.setEnabled(setEnabled);
                break;
            }
        }
    }

    static void enableAndShowPanel(boolean enableAndShow) {
        me.setEnabled(enableAndShow);
        me.setVisible(enableAndShow);
    }

    public void actionPerformed(ActionEvent event) {
        if ("generateKnot".equals(event.getActionCommand())) {
            Generator.generateKnotLines();
            GridGraphView.repaintView();
            Generator.createLinksBetweenComponents();
            ButtonPanelKnotCreated.enableAndShowPanel(true);
            enableAndShowPanel(false);
        } else if ("delVertex".equals(event.getActionCommand())) {
            GridGraphView.deleteVertex();
            GridGraphView.repaintView();
            setEnabledForComponent("delVertex", false);
        } else if ("clearAll".equals(event.getActionCommand())) {
            GridGraphView.clearGraph();
            GridGraphView.repaintView();
            setEnabledForComponent("clearAll", false);
            setEnabledForComponent("saveGraph", false);
            setEnabledForComponent("generateKnot", false);
            setEnabledForComponent("delEdge", false);
            setEnabledForComponent("delVertex", false);
        } else if ("delEdge".equals(event.getActionCommand())) {
            GridGraphView.deleteEdge();
            GridGraphView.repaintView();
            setEnabledForComponent("delEdge", false);
        } else if ("saveGraph".equals(event.getActionCommand())) {
            int result = fileChooser.showSaveDialog(me);
            if (result == JFileChooser.APPROVE_OPTION) {
                File saveFile = fileChooser.getSelectedFile();
                String absFilePath = saveFile.getAbsolutePath();
                String fileExtension = KnotFileFilter.getExtension(saveFile);
                if (fileExtension == null) {
                    fileExtension = "knot";
                    saveFile = new File(absFilePath + ".knot");
                } else if (!(fileExtension.equals("knot"))) {
                    String newFileName = absFilePath.substring(0, absFilePath.indexOf(fileExtension));
                    saveFile = new File(newFileName + "knot");
                    fileExtension = "knot";
                }
                KnotFileReaderWriter.saveFile(saveFile);
            }
        } else if ("loadGraph".equals(event.getActionCommand())) {
            int result = fileChooser.showOpenDialog(me);
            if (result == JFileChooser.APPROVE_OPTION) {
                File saveFile = fileChooser.getSelectedFile();
                String fileExtension = KnotFileFilter.getExtension(saveFile);
                if (fileExtension != null && fileExtension.equals("knot")) {
                    KnotFileReaderWriter.loadFile(saveFile);
                }
            }
        }
    }
}

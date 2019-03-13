package gui;


import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

public class ButtonPanelKnotCreated extends JPanel implements ActionListener {
    private static final long serialVersionUID = 1;
    private static JPanel me;
    private Font titleFont = new Font("TitleFont", Font.BOLD, 14);
    private static JFileChooser fileChooser = new JFileChooser();

    public ButtonPanelKnotCreated() {
        TitledBorder border = new TitledBorder("Picture Controls");
        border.setTitleFont(titleFont);
        this.setBorder(border);
        JButton returnToGraphCreation = new JButton("Back To Graph");
        returnToGraphCreation.setName("graph");
        returnToGraphCreation.setActionCommand("graph");
        returnToGraphCreation.setMnemonic(KeyEvent.VK_B);
        ;
        returnToGraphCreation.setToolTipText("Return to the graph creation stage");
        returnToGraphCreation.addActionListener(this);
        this.add(returnToGraphCreation, BorderLayout.LINE_START);
        JButton saveKnotAsPicture = new JButton("Save Picture");
        saveKnotAsPicture.setName("savePicture");
        saveKnotAsPicture.setActionCommand("savePicture");
        saveKnotAsPicture.setMnemonic(KeyEvent.VK_S);
        saveKnotAsPicture.setToolTipText("Save the knot as a picture file");
        saveKnotAsPicture.addActionListener(this);
        this.add(saveKnotAsPicture, BorderLayout.LINE_END);
        this.setEnabled(false);
        this.setVisible(false);
        fileChooser.addChoosableFileFilter(new PictureFileFilter());
        fileChooser.setAcceptAllFileFilterUsed(false);
        me = this;
    }

    public void actionPerformed(ActionEvent e) {
        if ("graph".equals(e.getActionCommand())) {
            GridGraphView.setGeneratingKnot(false);
            ButtonPanelGraphCreation.enableAndShowPanel(true);
            enableAndShowPanel(false);
            GridGraphView.repaintView();
        } else if ("savePicture".equals(e.getActionCommand())) {
            int result = fileChooser.showSaveDialog(me);
            if (result == JFileChooser.APPROVE_OPTION) {
                File saveFile = fileChooser.getSelectedFile();
                String absFilePath = saveFile.getAbsolutePath();
                String fileExtension = PictureFileFilter.getExtension(saveFile);
                if (fileExtension == null) {
                    fileExtension = "png";
                    saveFile = new File(absFilePath + ".png");
                } else if (!(fileExtension.equals("jpg") || fileExtension
                        .equals("png"))) {
                    String newFileName = absFilePath.substring(0, absFilePath.indexOf(fileExtension));
                    saveFile = new File(newFileName + "png");
                    fileExtension = "png";
                }
                GridGraphView.savePicture(saveFile, fileExtension);
            }
        }
    }

    public static void enableAndShowPanel(boolean enableAndShow) {
        me.setEnabled(enableAndShow);
        me.setVisible(enableAndShow);
    }
}

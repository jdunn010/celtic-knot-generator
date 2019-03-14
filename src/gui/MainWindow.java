/**
 * tbis code is slight modification of that published in PhD thesis:
 * http://www.cs.bath.ac.uk/~mdv/courses/CM30082/projects.bho/2007-8/Bailey-C-dissertation-2007-8.pdf
 */


package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;

public class MainWindow {

    public static void createAndShowGUI() {
        JFrame frame = new JFrame("Celtic Knot Generator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        GridBagConstraints c = new GridBagConstraints();
        JPanel window = new JPanel(new GridBagLayout());
        c.gridx = 0;
        c.gridy = 0;
        window.add(setUpGridArea(), c);
        c.gridx = 0;
        c.gridy = 1;
        window.add(setUpGraphButtonArea(), c);
        c.gridx = 0;
        c.gridy = 1;
        window.add(setUpKnotButtonArea(), c);
        c.gridx = 1;
        c.gridy = 0;
        c.gridheight = 2;
        window.add(setUpColourButtonArea(), c);
        frame.setContentPane(window);
        frame.pack();
        frame.setVisible(true);
        lockInMinSize(frame);
        frame.setResizable(false);
    }

    private static void lockInMinSize(final JFrame frame) {
        final int origX = frame.getSize().width;
        final int origY = frame.getSize().height;
        frame.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(ComponentEvent event) {
                frame.setSize((frame.getWidth() < origX) ?
                                origX : frame.getWidth(),
                        (frame.getHeight() < origY) ? origY : frame.getHeight());
            }
        });
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    private static JPanel setUpGridArea() {
        JPanel gridPanel = new JPanel(new BorderLayout());
        Dimension d = new Dimension(600, 600);
        gridPanel.setMinimumSize(d);
        gridPanel.setPreferredSize(d);
        gridPanel.add(new GridGraphView(), BorderLayout.CENTER);
        return gridPanel;
    }

    private static JPanel setUpGraphButtonArea() {
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.add(new ButtonPanelGraphCreation(), BorderLayout.CENTER);
        return buttonPanel;
    }

    private static JPanel setUpKnotButtonArea() {
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.add(new ButtonPanelKnotCreated(), BorderLayout.CENTER);
        return buttonPanel;
    }

    private static JPanel setUpColourButtonArea() {
        JPanel colourPanel = new JPanel(new BorderLayout());
        colourPanel.add(new ButtonPanelColours(), BorderLayout.CENTER);
        return colourPanel;
    }
}

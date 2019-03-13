package gui;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Hashtable;


public class ButtonPanelColours extends JPanel implements ActionListener, ChangeListener, ItemListener {
    private static final long serialVersionUID = 1;

    private static JPanel me;
    private static JSlider innerThicknessSlider;
    private static JSlider outlineThicknessSlider;
    private static JCheckBox makeTransCheck;

    private Insets labelInset = new Insets(5, 5, 5, 5);
    private Insets mainInset = new Insets(5, 5, 5, 5);
    private Dimension d = new Dimension(30, 30);
    private Font labelFont = new Font("LabelFont", Font.PLAIN, 12);
    private Font titleFont = new Font("TitleFont", Font.BOLD, 14);

    public ButtonPanelColours() {
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        c.insets = new Insets(10, 10, 0, 10);
        c.gridx = 0;
        c.gridy = 0;
        this.add(createOutlinePanel(), c);

        c.gridx = 0;
        c.gridy = 1;
        c.weighty = 5;
        this.add(createInnerPanel(), c);

        c.gridx = 0;
        c.gridy = 2;
        c.weighty = 5;
        this.add(createBackgroundPanel(), c);

        c.gridx = 0;
        c.gridy = 3;
        c.weighty = 5;
        this.add(createSliderPanel(), c);

        me = this;
    }

    public static void setSliderValues(int outlineThickness, int innerThickness) {
        innerThicknessSlider.setValue(innerThickness);
        outlineThicknessSlider.setValue(outlineThickness);
    }

    public void actionPerformed(ActionEvent e) {
        if ("changeOutline1".equals(e.getActionCommand())) {
            Color newColour = JColorChooser.showDialog(this,
                    "Select Colour For Knot Outline", GridGraphView.getOutlinePaintOne());
            if (newColour != null) {
                GridGraphView.setOutlinePaintOne(newColour);
                changePreviewPanelColour("outlineColourOneSample",
                        "outlinePanel", newColour);
            }
        } else if ("changeOutline2".equals(e.getActionCommand())) {
            Color newColour = JColorChooser.showDialog(this,
                    "Select Colour For Knot Outline",
                    GridGraphView.getOutlinePaintTwo());
            if (newColour != null) {
                GridGraphView.setOutlinePaintTwo(newColour);
                changePreviewPanelColour("outlineColourTwoSample",
                        "outlinePanel", newColour);
            }
        } else if ("changeOutline2Same".equals(e.getActionCommand())) {
            GridGraphView.setOutlinePaintTwo(GridGraphView.getOutlinePaintOne());
            changePreviewPanelColour("outlineColourTwoSample", "outlinePanel",
                    GridGraphView.getOutlinePaintOne());
        } else if ("changeInner1".equals(e.getActionCommand())) {
            Color newColour = JColorChooser.showDialog(this,
                    "Select Colour For Internal Part Of Knot", GridGraphView.getInnerPaintOne());
            if (newColour != null) {
                GridGraphView.setInnerPaintOne(newColour);
                changePreviewPanelColour("innerColourOneSample", "innerPanel", newColour);
            }
        } else if ("changeInner2".equals(e.getActionCommand())) {
            Color newColour = JColorChooser.showDialog(this,
                    "Select Colour For Internal Part Of Knot", GridGraphView
                            .getInnerPaintTwo());
            if (newColour != null) {
                GridGraphView.setInnerPaintTwo(newColour);
                changePreviewPanelColour("innerColourTwoSample", "innerPanel", newColour);
            }
        } else if ("changeInner2Same".equals(e.getActionCommand())) {
            GridGraphView.setInnerPaintTwo(GridGraphView.getInnerPaintOne());
            changePreviewPanelColour("innerColourTwoSample", "innerPanel",
                    GridGraphView.getInnerPaintOne());
        } else if ("changeBackground1".equals(e.getActionCommand())) {
            Color newColour = JColorChooser.showDialog(this, "Select Colour For Background", GridGraphView
                    .getBackgroundPaintOne());
            if (newColour != null) {
                GridGraphView.setBackgroundPaintOne(newColour);
                changePreviewPanelColour("backgroundColourOneSample",
                        "backgroundPanel", newColour);
                makeTransCheck.setSelected(false);
            }
        } else if ("changeBackground2".equals(e.getActionCommand())) {
            Color newColour = JColorChooser.showDialog(this, "Select Colour For Background", GridGraphView
                    .getBackgroundPaintTwo());
            if (newColour != null) {
                GridGraphView.setBackgroundPaintTwo(newColour);
                changePreviewPanelColour("backgroundColourTwoSample",
                        "backgroundPanel", newColour);
                makeTransCheck.setSelected(false);
            }
        } else if ("changeBackground2Same".equals(e.getActionCommand())) {
            GridGraphView.setBackgroundPaintTwo(GridGraphView.getBackgroundPaintOne());
            changePreviewPanelColour("backgroundColourTwoSample",
                    "backgroundPanel", GridGraphView.getBackgroundPaintOne());
            makeTransCheck.setSelected(false);
        }
        this.repaint();
        if (GridGraphView.isGeneratingKnot()) {
            GridGraphView.repaintView();
        }
    }

    public static void repaintView() {
        me.repaint();
    }

    public static void changePreviewPanelColour(String panelName, String outerPanelName, Color newColour) {
        Component[] components = me.getComponents();
        for (int i = 0; i < components.length; i++) {
            if (components[i].getName().equals(outerPanelName)) {
                Component[] panelComponents = ((JPanel) components[i])
                        .getComponents();
                for (int j = 0; j < panelComponents.length; j++) {
                    if (panelComponents[j].getName() != null
                            && panelComponents[j].getName().equals(panelName)) {
                        panelComponents[j].setBackground(newColour);
                        break;
                    }
                }
                break;
            }
        }
    }

    public void itemStateChanged(ItemEvent e) {
        Object source = e.getItemSelectable();
        if (source.equals(makeTransCheck)) {
            if (makeTransCheck.isSelected()) {
                Color currentBackgroundOne = GridGraphView.getBackgroundPaintOne();
                Color currentBackgroundTwo = GridGraphView.getBackgroundPaintTwo();
                Color newBackgroundOne = new Color(currentBackgroundOne.getRed(),
                        currentBackgroundOne.getGreen(), currentBackgroundOne.getBlue(), 0);
                Color newBackgroundTwo = new Color(currentBackgroundTwo.getRed(), currentBackgroundTwo.getGreen(), currentBackgroundTwo.getBlue(), 0);
                GridGraphView.setBackgroundPaintOne(newBackgroundOne);
                GridGraphView.setBackgroundPaintTwo(newBackgroundTwo);
                changePreviewPanelColour("backgroundColourOneSample",
                        "backgroundPanel", newBackgroundOne);
                changePreviewPanelColour("backgroundColourTwoSample", "backgroundPanel", newBackgroundTwo);
            } else {
                Color currentBackgroundOne = GridGraphView.getBackgroundPaintOne();
                Color currentBackgroundTwo = GridGraphView.getBackgroundPaintTwo();

                Color newBackgroundOne = new Color(currentBackgroundOne.getRed(),
                        currentBackgroundOne.getGreen(), currentBackgroundOne.getBlue(), 255);
                Color newBackgroundTwo = new Color(currentBackgroundTwo.getRed(),
                        currentBackgroundTwo.getGreen(), currentBackgroundTwo.getBlue(), 255);
                GridGraphView.setBackgroundPaintOne(newBackgroundOne);
                GridGraphView.setBackgroundPaintTwo(newBackgroundTwo);
                changePreviewPanelColour("backgroundColourOneSample",
                        "backgroundPanel", newBackgroundOne);
                changePreviewPanelColour("backgroundColourTwoSample", "backgroundPanel", newBackgroundTwo);
            }
        }
        this.repaint();
        if (GridGraphView.isGeneratingKnot()) {
            GridGraphView.repaintView();
        }
    }

    public void stateChanged(ChangeEvent e) {
        int outlineThickness = outlineThicknessSlider.getValue();
        int innerThickness = innerThicknessSlider.getValue();

        GridGraphView.setOutlineThickness(outlineThickness);
        if (innerThickness >= outlineThickness) {
            innerThickness = outlineThickness - 2;
            innerThicknessSlider.setValue(innerThickness);
        }
        innerThicknessSlider.setMaximum(outlineThickness - 2);
        Hashtable<Integer, JLabel> innerLabelTable = new Hashtable<>();
        innerLabelTable.put(new Integer(2), new JLabel("Thin"));
        innerLabelTable.put(new Integer(outlineThickness - 2), new JLabel("Thick"));
        innerThicknessSlider.setLabelTable(innerLabelTable);
        GridGraphView.setInnerThickness(innerThickness);
        this.repaint();
        if (GridGraphView.isGeneratingKnot()) {
            GridGraphView.repaintView();
        }
    }

    private JPanel createSliderPanel() {
        JPanel sliderPanel = new JPanel();
        sliderPanel.setName(" s l i d e r P a n e l ");
        sliderPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        TitledBorder border = new TitledBorder("Knot Thickness");
        border.setTitleFont(titleFont);
        sliderPanel.setBorder(border);
        JLabel outlineThicknessLabel = new JLabel("Outline Thickness");
        outlineThicknessLabel.setFont(labelFont);
        outlineThicknessSlider = new JSlider(JSlider.HORIZONTAL, 4, 18, 12);
        outlineThicknessSlider.setMinorTickSpacing(2);
        outlineThicknessSlider
                .setToolTipText("Adjust the thickness of the knot outline");
        outlineThicknessSlider.setSnapToTicks(true);
        outlineThicknessSlider.addChangeListener(this);
        Hashtable<Integer, JLabel> outerLabelTable = new Hashtable<>();
        outerLabelTable.put(new Integer(4), new JLabel("Thin"));
        outerLabelTable.put(new Integer(18), new JLabel("Thick"));
        outlineThicknessSlider.setLabelTable(outerLabelTable);
        outlineThicknessSlider.setPaintLabels(true);
        JLabel innerThicknessLabel = new JLabel("Internal Thickness");
        innerThicknessLabel.setFont(labelFont);
        innerThicknessSlider = new JSlider(JSlider.HORIZONTAL, 2, 14, 8);
        innerThicknessSlider.setMinorTickSpacing(2);
        innerThicknessSlider
                .setToolTipText("Adjust the thickness of the knot interior. Will automatically adjust to be less than the outline thickness");
        innerThicknessSlider.setSnapToTicks(true);
        innerThicknessSlider.addChangeListener(this);
        Hashtable<Integer, JLabel> innerLabelTable = new Hashtable<>();
        innerLabelTable.put(new Integer(2), new JLabel("Thin"));
        innerLabelTable.put(new Integer(14), new JLabel("Thick"));
        innerThicknessSlider.setLabelTable(innerLabelTable);
        innerThicknessSlider.setPaintLabels(true);
        c.gridx = 0;
        c.gridy = 0;
        c.insets = mainInset;
        sliderPanel.add(outlineThicknessLabel, c);
        c.gridx = 1;
        c.gridy = 0;
        sliderPanel.add(outlineThicknessSlider, c);
        c.gridx = 0;
        c.gridy = 1;
        c.insets = mainInset;
        sliderPanel.add(innerThicknessLabel, c);
        c.gridx = 1;
        c.gridy = 1;
        sliderPanel.add(innerThicknessSlider, c);
        return sliderPanel;
    }

    private JPanel createOutlinePanel() {
        JPanel outlinePanel = new JPanel();
        outlinePanel.setName("outlinePanel");
        outlinePanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        TitledBorder border = new TitledBorder("Knot Outline Colours");
        border.setTitleFont(titleFont);
        outlinePanel.setBorder(border);
        JLabel outlineColourOneLabel = new JLabel("Current Knot Outline Colour 1:");
        outlineColourOneLabel.setFont(labelFont);
        JPanel outlineColourOneSample = new JPanel();
        outlineColourOneSample.setName("outlineColourOneSample");
        outlineColourOneSample.setBackground(GridGraphView.getOutlinePaintOne());
        outlineColourOneSample.setMinimumSize(d);
        outlineColourOneSample.setPreferredSize(d);
        JButton outlineColourOneButton = new JButton("Change");
        outlineColourOneButton.setName("changeOutline1");
        outlineColourOneButton.setActionCommand("changeOutline1");
        outlineColourOneButton
                .setToolTipText("Click this button to change the first colour of the knot outline");
        outlineColourOneButton.addActionListener(this);

        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.insets = labelInset;
        outlinePanel.add(outlineColourOneLabel, c);
        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth = 1;
        c.insets = mainInset;
        outlinePanel.add(outlineColourOneSample, c);
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        outlinePanel.add(outlineColourOneButton, c);

        JLabel outlineColourTwoLabel = new JLabel("Current Knot Outline Colour 2:");
        outlineColourTwoLabel.setFont(labelFont);
        JPanel outlineColourTwoSample = new JPanel();
        outlineColourTwoSample.setName("outlineColourTwoSample");
        outlineColourTwoSample
                .setBackground(GridGraphView.getOutlinePaintTwo());
        outlineColourTwoSample.setMinimumSize(d);
        outlineColourTwoSample.setPreferredSize(d);
        JButton outlineColourTwoButton = new JButton("Change");
        outlineColourTwoButton.setName("changeOutline2");
        outlineColourTwoButton.setActionCommand("changeOutline2");
        outlineColourTwoButton
                .setToolTipText("Click this button to change the second colour of the knot outline");
        outlineColourTwoButton.addActionListener(this);
        JButton outlineColourTwoEqualOneButton = new JButton("Same As Colour 1");
        outlineColourTwoEqualOneButton.setName("changeOutline2Same");
        outlineColourTwoEqualOneButton.setActionCommand("changeOutline2Same");
        outlineColourTwoEqualOneButton
                .setToolTipText("Click this button to make the second outline colour the same as the first");
        outlineColourTwoEqualOneButton.addActionListener(this);
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 1;
        c.insets = labelInset;
        outlinePanel.add(outlineColourTwoLabel, c);
        c.gridx = 1;
        c.gridy = 2;
        c.gridwidth = 1;
        c.insets = mainInset;
        outlinePanel.add(outlineColourTwoSample, c);
        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 1;
        outlinePanel.add(outlineColourTwoButton, c);
        c.gridx = 1;
        c.gridy = 3;
        c.gridwidth = 1;
        outlinePanel.add(outlineColourTwoEqualOneButton, c);
        return outlinePanel;
    }

    private JPanel createInnerPanel() {
        JPanel innerPanel = new JPanel();
        innerPanel.setName("innerPanel");
        innerPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        TitledBorder border = new TitledBorder("Knot Internal Colours");
        border.setTitleFont(titleFont);
        innerPanel.setBorder(border);
        JLabel innerColourOneLabel = new JLabel("Current Knot Internal Colour 1:");
        innerColourOneLabel.setFont(labelFont);
        JPanel innerColourOneSample = new JPanel();
        innerColourOneSample.setName("innerColourOneSample");
        innerColourOneSample.setBackground(GridGraphView.getInnerPaintOne());
        innerColourOneSample.setMinimumSize(d);
        innerColourOneSample.setPreferredSize(d);
        JButton innerColourOneButton = new JButton("Change");
        innerColourOneButton.setName("changeInner1");
        innerColourOneButton.setActionCommand("changeInner1");
        innerColourOneButton
                .setToolTipText("Click this button to change the first colour of the knot interior");
        innerColourOneButton.addActionListener(this);
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.insets = labelInset;
        innerPanel.add(innerColourOneLabel, c);
        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth = 1;
        c.insets = mainInset;
        innerPanel.add(innerColourOneSample, c);
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        innerPanel.add(innerColourOneButton, c);
        JLabel innerColourTwoLabel = new JLabel("Current Knot Internal Colour 2:");
        innerColourTwoLabel.setFont(labelFont);
        JPanel innerColourTwoSample = new JPanel();
        innerColourTwoSample.setName("innerColourTwoSample");
        innerColourTwoSample.setBackground(GridGraphView.getInnerPaintTwo());
        innerColourTwoSample.setMinimumSize(d);
        innerColourTwoSample.setPreferredSize(d);
        JButton innerColourTwoButton = new JButton("Change");
        innerColourTwoButton.setName("changeInner2");
        innerColourTwoButton.setActionCommand("changeInner2");
        innerColourTwoButton
                .setToolTipText("Click this button to change the second colour of the knot interior");
        innerColourTwoButton.addActionListener(this);
        JButton innerColourTwoEqualOneButton = new JButton("Same As Colour 1");
        innerColourTwoEqualOneButton.setName("changeInner2Same");
        innerColourTwoEqualOneButton.setActionCommand("changeInner2Same");
        innerColourTwoEqualOneButton
                .setToolTipText("Click this button to make the second interior colour the same as the first");
        innerColourTwoEqualOneButton.addActionListener(this);
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 1;
        c.insets = labelInset;
        innerPanel.add(innerColourTwoLabel, c);
        c.gridx = 1;
        c.gridy = 2;
        c.gridwidth = 1;
        c.insets = mainInset;
        innerPanel.add(innerColourTwoSample, c);
        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 1;
        innerPanel.add(innerColourTwoButton, c);
        c.gridx = 1;
        c.gridy = 3;
        c.gridwidth = 1;
        innerPanel.add(innerColourTwoEqualOneButton, c);
        return innerPanel;
    }

    private JPanel createBackgroundPanel() {
        JPanel backgroundPanel = new JPanel();
        backgroundPanel.setName("backgroundPanel");
        backgroundPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        TitledBorder border = new TitledBorder("Background Colours");
        border.setTitleFont(titleFont);
        backgroundPanel.setBorder(border);
        JLabel backgroundColourOneLabel = new JLabel("Current Background Colour 1: ");
        backgroundColourOneLabel.setFont(labelFont);
        JPanel backgroundColourOneSample = new JPanel();
        backgroundColourOneSample.setName("backgroundColourOneSample");
        backgroundColourOneSample.setBackground(GridGraphView.getBackgroundPaintOne());
        backgroundColourOneSample.setMinimumSize(d);
        backgroundColourOneSample.setPreferredSize(d);
        JButton backgroundColourOneButton = new JButton("Change");
        backgroundColourOneButton.setName("changeBackground1");
        backgroundColourOneButton.setActionCommand("changeBackground1");
        backgroundColourOneButton
                .setToolTipText("Click this button to change the first colour of the image background");
        backgroundColourOneButton.addActionListener(this);
        makeTransCheck = new JCheckBox("Transparent");
        makeTransCheck.setToolTipText("Make the background transparent");
        makeTransCheck.setSelected(false);
        makeTransCheck.addItemListener(this);
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.insets = labelInset;
        backgroundPanel.add(backgroundColourOneLabel, c);
        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth = 1;
        c.insets = mainInset;
        backgroundPanel.add(backgroundColourOneSample, c);
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        backgroundPanel.add(backgroundColourOneButton, c);
        c.gridx = 1;
        c.gridy = 1;
        c.gridwidth = 1;
        backgroundPanel.add(makeTransCheck, c);

        JLabel backgroundColourTwoLabel = new JLabel("Current Background Colour 2: ");
        backgroundColourTwoLabel.setFont(labelFont);
        JPanel backgroundColourTwoSample = new JPanel();
        backgroundColourTwoSample.setName("backgroundColourTwoSample");
        backgroundColourTwoSample.setBackground(GridGraphView
                .getBackgroundPaintTwo());
        backgroundColourTwoSample.setMinimumSize(d);
        backgroundColourTwoSample.setPreferredSize(d);
        JButton backgroundColourTwoButton = new JButton("Change");
        backgroundColourTwoButton.setName("changeBackground2");
        backgroundColourTwoButton.setActionCommand("changeBackground2");
        backgroundColourTwoButton
                .setToolTipText("Click this button to change the second colour of the image background");
        backgroundColourTwoButton.addActionListener(this);
        JButton backgroundColourTwoEqualOneButton = new JButton("Same As Colour 1");
        backgroundColourTwoEqualOneButton.setName("changeBackground2Same");
        backgroundColourTwoEqualOneButton
                .setActionCommand("changeBackground2Same");
        backgroundColourTwoEqualOneButton
                .setToolTipText("Click this button to make the second image background colour the same as the first");
        backgroundColourTwoEqualOneButton.addActionListener(this);
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 1;
        c.insets = labelInset;
        backgroundPanel.add(backgroundColourTwoLabel, c);
        c.gridx = 1;
        c.gridy = 2;
        c.gridwidth = 1;
        c.insets = mainInset;
        backgroundPanel.add(backgroundColourTwoSample, c);
        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 1;
        backgroundPanel.add(backgroundColourTwoButton, c);
        c.gridx = 1;
        c.gridy = 3;
        c.gridwidth = 1;
        backgroundPanel.add(backgroundColourTwoEqualOneButton, c);
        return backgroundPanel;
    }
}

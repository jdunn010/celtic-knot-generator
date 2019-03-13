package gui;


import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class KnotFileReaderWriter {
    private static final String sectionSeperator = "@@@@";
    private static final String elementSeperator = "####";

    public static void saveFile(File fileToBeUsed) {
        ArrayList<Point> verticies = GridGraphView.getVertices();
        ArrayList<Point> edgesStart = GridGraphView.getEdgesStart();
        ArrayList<Point> edgesEnd = GridGraphView.getEdgesEnd();

        Color outlinePaintOne = GridGraphView.getOutlinePaintOne();
        Color outlinePaintTwo = GridGraphView.getOutlinePaintTwo();
        Color innerPaintOne = GridGraphView.getInnerPaintOne();
        Color innerPaintTwo = GridGraphView.getInnerPaintTwo();
        Color backgroundPaintOne = GridGraphView.getBackgroundPaintOne();
        Color backgroundPaintTwo = GridGraphView.getBackgroundPaintTwo();

        int outlineThickness = GridGraphView.getOutlineThickness();
        int innerThickness = GridGraphView.getInnerThickness();

        String output = "";

        for (int i = 0; i < verticies.size(); i++) {
            Point point = verticies.get(i);
            output = output + elementSeperator + point.getX() + ","
                    + point.getY();
        }
        output = output + sectionSeperator;

        for (int i = 0; i < edgesStart.size(); i++) {
            Point point = edgesStart.get(i);
            output = output + elementSeperator + point.getX() + "," + point.getY();
        }
        output = output + sectionSeperator;

        for (int i = 0; i < edgesEnd.size(); i++) {
            Point point = edgesEnd.get(i);
            output = output + elementSeperator + point.getX() + "," + point.getY();
        }
        output = output + sectionSeperator;

        output = output + outlinePaintOne.getRed() + ","
                + outlinePaintOne.getGreen() + ","
                + outlinePaintOne.getBlue() + ","
                + outlinePaintOne.getAlpha() + sectionSeperator;

        output = output + outlinePaintTwo.getRed() + ","
                + outlinePaintTwo.getGreen() + ","
                + outlinePaintTwo.getBlue() + ","
                + outlinePaintTwo.getAlpha() + sectionSeperator;

        output = output + innerPaintOne.getRed() + ","
                + innerPaintOne.getGreen() + ","
                + innerPaintOne.getBlue() + ","
                + innerPaintOne.getAlpha() + sectionSeperator;

        output = output + innerPaintTwo.getRed() + ","
                + innerPaintTwo.getGreen() + ","
                + innerPaintTwo.getBlue() + ","
                + innerPaintTwo.getAlpha() + sectionSeperator;
        output = output + outlineThickness + sectionSeperator;
        output = output + innerThickness;
        try {
            FileWriter fileWriter = new FileWriter(fileToBeUsed);
            BufferedWriter buffWriter = new BufferedWriter(fileWriter);
            buffWriter.write(output);
            buffWriter.close();
            fileWriter.flush();
            fileWriter.close();
        } catch (Exception ignore) {
        }
    }

    static void loadFile(File fileToBeUsed) {
        ArrayList<Point> vertices = new ArrayList<>();
        ArrayList<Point> edgesStart = new ArrayList<>();
        ArrayList<Point> edgesEnd = new ArrayList<>();
        Color outlinePaintOne;
        Color outlinePaintTwo;
        Color innerPaintOne;
        Color innerPaintTwo;
        Color backgroundPaintOne;
        Color backgroundPaintTwo;
        int outlineThickness;
        int innerThickness;
        boolean setClearAll = false;
        boolean setGenerateKnot = false;

        try {
            RandomAccessFile input = new RandomAccessFile(fileToBeUsed, "r");
            byte[] inputBytes = new byte[new Long(input.length()).intValue()];
            input.read(inputBytes);
            String inputString = new String(inputBytes);
            String[] sections = inputString.split(sectionSeperator);
            String[] verticesFromFile = sections[0].split(elementSeperator);
            for (String aVerticesFromFile : verticesFromFile) {
                if (aVerticesFromFile.length() > 0) {
                    String[] values = aVerticesFromFile.split(",");
                    int xValue = new Double(values[0]).intValue();
                    int yValue = new Double(values[1]).intValue();
                    if (xValue % GridGraphView.getMAGIC_NUMBER() == 0
                            && yValue % GridGraphView.getMAGIC_NUMBER() == 0) {
                        Point vertex = new Point(xValue, yValue);
                        vertices.add(vertex);
                        setClearAll = true;
                        continue;
                    }
                }
            }
            String[] edgesStartFromFile = sections[1].split(elementSeperator);
            for (String anEdgesStartFromFile : edgesStartFromFile) {
                if (anEdgesStartFromFile.length() > 0) {
                    String[] values = anEdgesStartFromFile.split(",");
                    int xValue = new Double(values[0]).intValue();
                    int yValue = new Double(values[1]).intValue();
                    if (xValue % GridGraphView.getMAGIC_NUMBER() == 0
                            && yValue % GridGraphView.getMAGIC_NUMBER() == 0) {
                        Point vertex = new Point(xValue, yValue);
                        edgesStart.add(vertex);
                        setGenerateKnot = true;
                    }
                } else {
                    continue;
                }
            }

            String[] edgesEndFromFile = sections[2].split(elementSeperator);
            for (String anEdgesEndFromFile : edgesEndFromFile) {
                if (anEdgesEndFromFile.length() > 0) {
                    String[] values = anEdgesEndFromFile.split(",");
                    int xValue = new Double(values[0]).intValue();

                    int yValue = new Double(values[1]).intValue();
                    if (xValue % GridGraphView.getMAGIC_NUMBER() == 0
                            && yValue % GridGraphView.getMAGIC_NUMBER() == 0) {
                        Point vertex = new Point(xValue, yValue);
                        edgesEnd.add(vertex);
                    }
                } else {
                    continue;
                }
            }
            String[] outlinePaintOneFromFile = sections[3].split(",");
            outlinePaintOne = new Color(new Integer(outlinePaintOneFromFile[0])
                    .intValue(), new Integer(outlinePaintOneFromFile[1])
                    .intValue(), new Integer(outlinePaintOneFromFile[2])
                    .intValue(), new Integer(outlinePaintOneFromFile[3])
                    .intValue());
            String[] outlinePaintTwoFromFile = sections[4].split(",");
            outlinePaintTwo = new Color(new Integer(outlinePaintTwoFromFile[0])
                    .intValue(), new Integer(outlinePaintTwoFromFile[1])
                    .intValue(), new Integer(outlinePaintTwoFromFile[2])
                    .intValue(), new Integer(outlinePaintTwoFromFile[3])
                    .intValue());
            String[] innerPaintOneFromFile = sections[5].split(",");
            innerPaintOne = new Color(new Integer(innerPaintOneFromFile[0])
                    .intValue(), new Integer(innerPaintOneFromFile[1])
                    .intValue(), new Integer(innerPaintOneFromFile[2])
                    .intValue(), new Integer(innerPaintOneFromFile[3])
                    .intValue());
            String[] innerPaintTwoFromFile = sections[6].split(",");
            innerPaintTwo = new Color(new Integer(innerPaintTwoFromFile[0])
                    .intValue(), new Integer(innerPaintTwoFromFile[1])
                    .intValue(), new Integer(innerPaintTwoFromFile[2])
                    .intValue(), new Integer(innerPaintTwoFromFile[3])
                    .intValue());
            String[] backgroundPaintOneFromFile = sections[7].split(",");
            backgroundPaintOne = new Color(new Integer(
                    backgroundPaintOneFromFile[0])
                    .intValue(), new Integer(backgroundPaintOneFromFile[1])
                    .intValue(), new Integer(backgroundPaintOneFromFile[2])
                    .intValue(), new Integer(backgroundPaintOneFromFile[3])
                    .intValue());
            String[] backgroundPaintTwoFromFile = sections[8].split(",");
            backgroundPaintTwo = new Color(new Integer(
                    backgroundPaintTwoFromFile[0]).intValue(), new Integer(backgroundPaintTwoFromFile[1])
                    .intValue(), new Integer(backgroundPaintTwoFromFile[2])
                    .intValue(), new Integer(backgroundPaintTwoFromFile[3])
                    .intValue());
            outlineThickness = new Integer(sections[9])
                    .intValue();
            innerThickness = new Integer(sections[10])
                    .intValue();
            GridGraphView.setVertices(vertices);
            GridGraphView.setEdgesStart(edgesStart);
            GridGraphView.setEdgesEnd(edgesEnd);
            GridGraphView.setOutlinePaintOne(outlinePaintOne);
            GridGraphView.setOutlinePaintTwo(outlinePaintTwo);
            GridGraphView.setInnerPaintOne(innerPaintOne);
            GridGraphView.setInnerPaintTwo(innerPaintTwo);
            GridGraphView.setBackgroundPaintOne(backgroundPaintOne);
            GridGraphView.setBackgroundPaintTwo(backgroundPaintTwo);
            GridGraphView.setOutlineThickness(outlineThickness);
            GridGraphView.setInnerThickness(innerThickness);
            GridGraphView.setGeneratingKnot(false);
            ButtonPanelColours.changePreviewPanelColour("outlineColourOneSample", "outlinePanel", outlinePaintOne);
            ButtonPanelColours.changePreviewPanelColour("outlineColourTwoSample", "outlinePanel", outlinePaintTwo);
            ButtonPanelColours.changePreviewPanelColour("innerColourOneSample", "innerPanel", innerPaintOne);
            ButtonPanelColours.changePreviewPanelColour("innerColourTwoSample", "innerPanel", innerPaintTwo);
            ButtonPanelColours.changePreviewPanelColour("backgroundColourOneSample", "backgroundPanel", backgroundPaintOne);
            ButtonPanelColours.changePreviewPanelColour("backgroundColourTwoSample", "backgroundPanel", backgroundPaintTwo);
            ButtonPanelColours
                    .setSliderValues(outlineThickness, innerThickness);
            ButtonPanelGraphCreation.setEnabledForComponent("clearAll", setClearAll);
            ButtonPanelGraphCreation.setEnabledForComponent("saveGraph", setClearAll);
            ButtonPanelGraphCreation.setEnabledForComponent("generateKnot", setGenerateKnot);
            ButtonPanelColours.repaintView();
            GridGraphView.repaintView();
        } catch (Exception e) {
        }
    }
}

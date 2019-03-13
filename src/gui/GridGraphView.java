package gui;


import knot.generation.Generator;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class GridGraphView extends JPanel {
    private static final long serialVersionUID = 1;
    private static JPanel me;
    private static final int MAGIC_NUMBER = 60;
    private static final int VERTEX_SIZE = 10;
    private static Point startPoint;
    private static Point endPoint;
    private static Point selected;
    private static Line2D.Double selectedEdge;
    private static final ArrayList<Point> vertices = new ArrayList<>();
    private static final ArrayList<Point> edgesStart = new ArrayList<>();
    private static final ArrayList<Point> edgesEnd = new ArrayList<>();
    private static final ArrayList<Point> edgesMidpoint = new ArrayList<>();
    private static boolean generatingKnot = false;


    private static Color outlinePaintOne = new Color(255, 133, 0);
    private static Color outlinePaintTwo = new Color(255, 133, 0);
    private static Color innerPaintOne = new Color(255, 255, 204);
    private static Color innerPaintTwo = new Color(255, 255, 204);
    private static Color backgroundPaintOne = Color.WHITE;
    private static Color backgroundPaintTwo = Color.WHITE;

    private static int outlineThickness = 18;
    private static int innerThickness = 10;

    public GridGraphView() {
        me = this;
        this.setToolTipText("Double click an intersection to create a vertex. " +
                "Click and drag from one vertex to another to create an edge");
        this.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent me) {
                Point p = getPointOnGrid(me);
                if (me.getClickCount() == 2 && isPointWithinBoundary(p) == true && doesVertexExist(p) == false) {
                    vertices.add(p);
                    repaint();
                    ButtonPanelGraphCreation.setEnabledForComponent("clearAll",
                            true);
                    ButtonPanelGraphCreation.setEnabledForComponent(
                            "saveGraph", true);
                }
            }

            public void mousePressed(MouseEvent me) {
                Point p = getPointOnGrid(me);
                if (doesVertexExist(p)) {
                    startPoint = p;
                    selected = p;
                    ButtonPanelGraphCreation.setEnabledForComponent(
                            "delVertex", true);
                    selectedEdge = null;
                    ButtonPanelGraphCreation.setEnabledForComponent("delEdge", false);
                } else if (doesPointLieOnEdge(me.getPoint()) != null) {
                    selectedEdge = doesPointLieOnEdge(me.getPoint());
                    ButtonPanelGraphCreation.setEnabledForComponent("delEdge",
                            true);
                    selected = null;
                    ButtonPanelGraphCreation.setEnabledForComponent("delVertex", false);
                } else {
                    selected = null;
                    ButtonPanelGraphCreation.setEnabledForComponent("delVertex", false);
                    selectedEdge = null;
                    ButtonPanelGraphCreation.setEnabledForComponent("delEdge",
                            false);
                }
            }

            public void mouseReleased(MouseEvent me) {
                Point p = getPointOnGrid(me);
                if (startPoint != null && !startPoint.equals(p)
                        && doesVertexExist(p)
                        && (doesEdgeExist(startPoint, p) == false)) {
                    endPoint = p;
                    if (doesNewEdgeCrossExistingEdge(startPoint, endPoint) == false) {
                        ButtonPanelGraphCreation.setEnabledForComponent("generateKnot", true);
                        breakNewEdgeIfRequired(startPoint, endPoint);
                    }
                }
                startPoint = null;
                endPoint = null;
                repaint();
            }
        });

        this.addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent mouseEvent) {
                if (startPoint != null) {
                    endPoint = mouseEvent.getPoint();
                    repaint();
                }
            }
        });
    }


    private boolean isPointWithinBoundary(Point point) {
        boolean withinBoundary = false;
        int currentHeight = (new Double(this.getSize().getHeight())).intValue();
        int currentWidth = (new Double(this.getSize().getWidth())).intValue();
        if (point.x >= (0 + MAGIC_NUMBER)
                && point.x <= (currentWidth - MAGIC_NUMBER)
                && point.y >= (0 + MAGIC_NUMBER)
                && point.y <= (currentHeight - MAGIC_NUMBER)) {
            withinBoundary = true;
        }
        return withinBoundary;
    }

    private void paintGrid(Graphics2D g2) {
        int currentHeight = (new Double(this.getSize().getHeight())).intValue();
        int currentWidth = (new Double(this.getSize().getWidth())).intValue();
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, currentWidth, currentHeight);
        g2.setColor(Color.BLACK);
        for (int i = MAGIC_NUMBER; i < currentWidth; i = i + MAGIC_NUMBER) {
            g2.drawLine(i, 0, i, currentHeight);
        }
        for (int j = MAGIC_NUMBER; j < currentHeight; j = j + MAGIC_NUMBER) {
            g2.drawLine(0, j, currentWidth, j);
        }
    }

    private void paintVerticies(Graphics2D g2) {
        if (vertices.size() > 0) {
            for (int i = 0; i < vertices.size(); i++) {
                Point p = vertices.get(i);
                if (selected != null && p.equals(selected)) {
                    g2.setColor(Color.BLUE);
                    g2.fillOval((selected.x - (VERTEX_SIZE / 2)),
                            (selected.y - (VERTEX_SIZE / 2)), VERTEX_SIZE, VERTEX_SIZE);
                    g2.setColor(Color.BLACK);
                } else {
                    g2.fillOval((p.x - (VERTEX_SIZE / 2)), (p.y - (VERTEX_SIZE / 2)), VERTEX_SIZE, VERTEX_SIZE);
                }
            }
        }
    }

    private void paintEdges(Graphics2D g2) {
        if (edgesStart.size() > 0) {
            BasicStroke thickLine = new BasicStroke(3);
            Stroke oldStroke = g2.getStroke();
            g2.setStroke(thickLine);
            for (int i = 0; i < edgesStart.size(); i++) {
                Point s = edgesStart.get(i);
                Point e = edgesEnd.get(i);
                g2.drawLine(s.x, s.y, e.x, e.y);
            }
            if (selectedEdge != null) {
                g2.setColor(Color.BLUE);
                g2.draw(selectedEdge);
                g2.setColor(Color.BLACK);
            }
            g2.setStroke(oldStroke);
        }
    }

    public static void savePicture(File file, String fileExtension) {
        try {
            BufferedImage bi = new BufferedImage(600, 600, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g3 = bi.createGraphics();
            GradientPaint outlinePaint = new GradientPaint(0, 0,
                    outlinePaintOne, 600, 600, outlinePaintTwo, true);
            GradientPaint innerPaint = new GradientPaint(0, 0, innerPaintOne,
                    600, 600, innerPaintTwo, true);
            GradientPaint backgroundPaint = new GradientPaint(0, 0,
                    backgroundPaintOne, 600, 600, backgroundPaintTwo, true);
            Generator.paintKnot(g3, outlinePaint, innerPaint, backgroundPaint,
                    outlineThickness, innerThickness);
            ImageIO.write(bi, fileExtension, file);
        } catch (IOException ignore) {
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        if (generatingKnot) {
            BasicStroke thickLine = new BasicStroke(20);
            Stroke oldStroke = g2.getStroke();
            g2.setStroke(thickLine);

            GradientPaint outlinePaint = new GradientPaint(0, 0, outlinePaintOne, 600, 600, outlinePaintTwo,
                    true);
            GradientPaint innerPaint = new GradientPaint(0, 0, innerPaintOne,
                    600, 600, innerPaintTwo, true);
            GradientPaint backgroundPaint = new GradientPaint(0, 0,
                    backgroundPaintOne, 600, 600, backgroundPaintTwo, true);
            Generator.paintKnot(g2, outlinePaint, innerPaint, backgroundPaint,
                    outlineThickness, innerThickness);
            g2.setStroke(oldStroke);
        } else {
            paintGrid(g2);
            paintVerticies(g2);
            paintEdges(g2);
        }

        if (endPoint != null && startPoint != null) {
            g2.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y);
        }
    }

    private Point getPointOnGrid(MouseEvent me) {
        Point eventPoint = me.getPoint();
        int x = eventPoint.x;
        int y = eventPoint.y;
        float gridPositionX = (new Integer(x)).floatValue() / (new Integer(MAGIC_NUMBER)).floatValue();
        int gridPositionXint = x / MAGIC_NUMBER;
        if (Math.abs(gridPositionX - gridPositionXint) > 0.5) {
            gridPositionXint++;
        }
        float gridPositionY = (new Integer(y)).floatValue() / (new Integer(MAGIC_NUMBER)).floatValue();
        int gridPositionYint = y / MAGIC_NUMBER;
        if (Math.abs(gridPositionY - gridPositionYint) > 0.5) {
            gridPositionYint++;
        }
        Point p = new Point((gridPositionXint * MAGIC_NUMBER), (gridPositionYint * MAGIC_NUMBER));
        return p;
    }

    public static Point getStartPoint() {
        return startPoint;
    }

    public static void setStartPoint(Point startPoint) {
        GridGraphView.startPoint = startPoint;
    }

    public static Point getEndPoint() {
        return endPoint;
    }

    public static void setEndPoint(Point endPoint) {
        GridGraphView.endPoint = endPoint;
    }

    public static Point getSelected() {
        return selected;
    }

    public static void setSelected(Point selected) {
        GridGraphView.selected = selected;
    }

    public static ArrayList<Point> getVertices() {
        return vertices;
    }

    public static void setVertices(ArrayList<Point> vertices) {
        GridGraphView.vertices.addAll(vertices);
    }

    public static ArrayList<Point> getEdgesStart() {
        return edgesStart;
    }

    public static void setEdgesStart(ArrayList<Point> edgesStart) {
        GridGraphView.edgesStart.addAll(edgesStart);
    }

    public static ArrayList<Point> getEdgesEnd() {
        return edgesEnd;
    }

    public static void setEdgesEnd(ArrayList<Point> edgesEnd) {
        GridGraphView.edgesEnd.addAll(edgesEnd);
    }

    public static ArrayList<Point> getEdgesMidpoint() {
        return edgesMidpoint;
    }

    public static void setEdgesMidpoint(ArrayList<Point> edgesMidpoint) {
        GridGraphView.edgesMidpoint.addAll(edgesMidpoint);
    }

    private Line2D.Double doesPointLieOnEdge(Point point) {
        for (int i = 0; i < edgesStart.size(); i++) {
            Line2D.Double temp = new Line2D.Double(edgesStart.get(i), edgesEnd.get(i));
            if (temp.ptSegDist(point) < 5) {
                return temp;
            }
        }
        return null;
    }

    private boolean doesEdgeExist(Point s, Point e) {
        boolean exists = false;
        for (int i = 0; i < edgesStart.size(); i++) {
            if (s.equals(edgesStart.get(i)) && e.equals(edgesEnd.get(i))) {
                exists = true;
            } else if (e.equals(edgesStart.get(i)) && s.equals(edgesEnd.get(i))) {
                exists = true;
            }
        }
        return exists;
    }

    private boolean doesVertexExist(Point p) {
        boolean exists = false;
        for (int i = 0; i < vertices.size(); i++) {
            if (p.equals(vertices.get(i))) {
                exists = true;
                break;
            }
        }
        return exists;
    }


    public static void repaintView() {
        me.repaint();
    }

    private boolean doesNewEdgeCrossExistingEdge(Point start, Point end) {
        boolean intersects = false;
        for (int i = 0; i < edgesStart.size(); i++) {
            Point tempStart = edgesStart.get(i);
            Point tempEnd = edgesEnd.get(i);
            if (!tempStart.equals(start) && !tempStart.equals(end)
                    && !tempEnd.equals(start) && !tempEnd.equals(end)) {
                if (Line2D.linesIntersect(start.x, start.y, end.x, end.y, tempStart.x, tempStart.y, tempEnd.x, tempEnd.y)
                        && (Line2D.ptLineDist(start.x, start.y, end.x, end.y,
                        tempStart.x, tempStart.y) != 0.0)
                        && (Line2D.ptLineDist(start.x, start.y, end.x, end.y,
                        tempEnd.x, tempEnd.y) != 0.0)) {
                    intersects = true;
                }
            }
        }
        return intersects;
    }

    private Point doesNewEdgeCrossVertex(Point start, Point end) {
        Point intersects = null;
        Line2D tempLine = new Line2D.Double(start, end);
        for (int i = 0; i < vertices.size(); i++) {
            Point p = vertices.get(i);
            if (!p.equals(end) && !p.equals(start)
                    && tempLine.ptSegDist(p) == 0) {
                intersects = p;
                break;
            }
        }
        return intersects;
    }

    private void breakNewEdgeIfRequired(Point start, Point end) {
        Point intersect = doesNewEdgeCrossVertex(start, end);
        if (intersect != null) {
            breakNewEdgeIfRequired(start, intersect);
            breakNewEdgeIfRequired(intersect, end);
        } else if (doesEdgeExist(start, end) == false) {
            edgesStart.add(start);
            edgesEnd.add(end);
        }
    }

    public static void deleteVertex() {
        if (selected == null) {
            return;
        }
        vertices.remove(selected);
        if (edgesStart.size() > 0) {
            while (edgesStart.contains(selected)) {
                int position = edgesStart.indexOf(selected);
                edgesStart.remove(position);
                edgesEnd.remove(position);
            }
            while (edgesEnd.contains(selected)) {
                int position = edgesEnd.indexOf(selected);
                edgesStart.remove(position);
                edgesEnd.remove(position);
            }
        }
        if (!(edgesStart.size() > 0)) {
            ButtonPanelGraphCreation.setEnabledForComponent("generateKnot",
                    false);
        }
        if (!(vertices.size() > 0)) {
            ButtonPanelGraphCreation.setEnabledForComponent("clearAll", false);
            ButtonPanelGraphCreation.setEnabledForComponent("saveGraph", false);
        }
    }

    public static boolean isGeneratingKnot() {
        return generatingKnot;
    }

    public static void setGeneratingKnot(boolean generatingKnot) {
        GridGraphView.generatingKnot = generatingKnot;
    }

    public static void deleteEdge() {
        if (selectedEdge != null) {
            for (int i = 0; i < edgesStart.size(); i++) {
                if (selectedEdge.ptSegDist(edgesStart.get(i)) == 0.0
                        && selectedEdge.ptSegDist(edgesEnd.get(i)) == 0.0) {
                    edgesStart.remove(i);
                    edgesEnd.remove(i);
                    break;
                }
            }
            selectedEdge = null;
        }
        if (!(edgesStart.size() > 0)) {
            ButtonPanelGraphCreation.setEnabledForComponent("generateKnot",
                    false);
        }
    }

    public static void clearGraph() {
        startPoint = null;
        endPoint = null;
        selected = null;
        vertices.clear();
        edgesStart.clear();
        edgesEnd.clear();
        edgesMidpoint.clear();
        generatingKnot = false;
    }

    public static Color getOutlinePaintOne() {
        return outlinePaintOne;
    }

    public static void setOutlinePaintOne(Color outlinePaintOne) {
        GridGraphView.outlinePaintOne = outlinePaintOne;
    }

    public static Color getOutlinePaintTwo() {
        return outlinePaintTwo;
    }

    public static void setOutlinePaintTwo(Color outlinePaintTwo) {
        GridGraphView.outlinePaintTwo = outlinePaintTwo;
    }

    public static Color getInnerPaintOne() {
        return innerPaintOne;
    }

    public static void setInnerPaintOne(Color innerPaintOne) {
        GridGraphView.innerPaintOne = innerPaintOne;
    }

    public static Color getInnerPaintTwo() {
        return innerPaintTwo;
    }

    public static void setInnerPaintTwo(Color innerPaintTwo) {
        GridGraphView.innerPaintTwo = innerPaintTwo;
    }

    public static Color getBackgroundPaintOne() {
        return backgroundPaintOne;
    }

    public static void setBackgroundPaintOne(Color backgroundPaintOne) {
        GridGraphView.backgroundPaintOne = backgroundPaintOne;
    }

    public static Color getBackgroundPaintTwo() {
        return backgroundPaintTwo;
    }

    public static void setBackgroundPaintTwo(Color backgroundPaintTwo) {
        GridGraphView.backgroundPaintTwo = backgroundPaintTwo;
    }

    public static int getOutlineThickness() {
        return outlineThickness;
    }

    public static void setOutlineThickness(int outlineThickness) {
        GridGraphView.outlineThickness = outlineThickness;
    }

    public static int getInnerThickness() {
        return innerThickness;
    }

    public static void setInnerThickness(int innerThickness) {
        GridGraphView.innerThickness = innerThickness;
    }

    public static int getMAGIC_NUMBER() {
        return MAGIC_NUMBER;
    }
}

package knot.generation;

import gui.ButtonPanelKnotCreated;
import gui.GridGraphView;
import gui.KnotFileReaderWriter;
import gui.MainWindow;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import static gui.GridGraphView.MAGIC_NUMBER;

//going to programmatically create a GridGraphView
//to generate and save CelticKnot in a .png file
public class SampleKnotCreator {

    Random random = new Random();
    final int outlineThickness = 18; //make these constants for now
    final int innerThickness = 10;
    Color backgroundPaintOne = new Color(255, 255, 255);
    Color backgroundPaintTwo = new Color(255, 255, 255);

    public void createSampleKnot() {
        //todo keeping it simple for now
        Point upperLeftCorner = new Point(60, 60);

        for (int length = 1; length < 9; length++) {
            for (int width = 1; width < 9; width++) {
                GridGraphView gridGraphView = new GridGraphView();
                GridGraphView.clearGraph();
                //leave these constant for all graphs for now
                GridGraphView.setInnerThickness(innerThickness);
                GridGraphView.setOutlineThickness(outlineThickness);
                GridGraphView.setBackgroundPaintOne(backgroundPaintOne);
                GridGraphView.setBackgroundPaintTwo(backgroundPaintTwo);

                GridGraphView.setOutlinePaintOne(new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
                GridGraphView.setOutlinePaintTwo(new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
                GridGraphView.setInnerPaintOne(new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
                GridGraphView.setInnerPaintTwo(new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)));

                Dimension dimension = new Dimension(length, width);
                Rectangle rectangle = new Rectangle(upperLeftCorner, dimension);
                generateGraph(gridGraphView, rectangle);
            }
        }
    }


    //going to do rectangles to start with here
    private void generateGraph(GridGraphView gridGraphView, Rectangle placeToStart) {
        ArrayList<Point> vertices = new ArrayList<>();
        ArrayList<Point> edgesStart = new ArrayList<>();
        ArrayList<Point> edgesEnd = new ArrayList<>();

        final int width = placeToStart.width;
        final int height = placeToStart.height;

        //create the starting point vertex from given rectangle
        Point startingPoint = new Point(placeToStart.x, placeToStart.y);
        vertices.add(startingPoint);
        edgesStart.add(startingPoint);

        //will proceed in clockwise direction around the rectangle

        //place a vertex at each grid mark across width of horizontal axis from starting point
        //to form the top row
        for (int i = 1; i <= width; i++) {
            Point p = new Point((i * MAGIC_NUMBER) + placeToStart.x, placeToStart.y);
            vertices.add(p);
            edgesEnd.add(p);
        }
        //need to add some edge starts on top row
        if (width > 1) {
            for (int i = 1; i < width; i++) {
                Point p = new Point((i * MAGIC_NUMBER) + placeToStart.x, placeToStart.y);
                edgesStart.add(p);
            }
        }
        assert(vertices.size() == width + 1);
        assert(edgesStart.size() == width);
        assert(edgesEnd.size() == width);

        //place a vertex at each grid mark across height of vertical axis from starting point + width
        //to form the right edge
        edgesStart.add(new Point(placeToStart.x + (width * 60), placeToStart.y));
        for (int i = 1; i <= height; i++) {
            Point p = new Point(placeToStart.x + (width * 60), placeToStart.y + (i * 60));
            vertices.add(p);
            edgesEnd.add(p);
        }
        //need to add some edge starts on top row
        if (height > 1) {
            for (int i = 1; i < height; i++) {
                Point p = new Point((width * MAGIC_NUMBER) + placeToStart.x, placeToStart.y + (i * MAGIC_NUMBER));
                edgesStart.add(p);
            }
        }
        assert(vertices.size() == (width + 1) + height);
        assert(edgesStart.size() == width + height);
        assert(edgesEnd.size() == width + height);


        //keep going clockwise, head west from lower right corner
        //to form the bottom row
        int lowerRightCornerX = placeToStart.x + (placeToStart.width * MAGIC_NUMBER);
        int lowerRightCornerY = placeToStart.y + (placeToStart.height * MAGIC_NUMBER);
        edgesStart.add(new Point(
                lowerRightCornerX,
                lowerRightCornerY));

        for (int i = 1; i <= width; i++) {
            Point p = new Point(
                    lowerRightCornerX - (i * MAGIC_NUMBER),
                    lowerRightCornerY);
            vertices.add(p);
            edgesEnd.add(p);
        }

        //may need to add some edge starts on bottom row
        if (width > 1) {
            for (int i = 1; i < width; i++) {
                Point p = new Point(
                        lowerRightCornerX - (i * MAGIC_NUMBER),
                        lowerRightCornerY);
                edgesStart.add(p);
            }
        }

        //go up the left edge back to the origin
        int lowerLeftCorner = placeToStart.y + (placeToStart.height * MAGIC_NUMBER);
        edgesStart.add(new Point(placeToStart.x, lowerLeftCorner));

        for (int i = 1; i < height; i++) { //already have a vertex at the origin, add final edge end separately below
            Point p = new Point(
                    placeToStart.x,
                    (lowerLeftCorner - (i * MAGIC_NUMBER)));
            vertices.add(p);
            edgesEnd.add(p);
        }

        //may need to add some more edge starts going up the left edge
        if (height > 1) {
            for (int i = 1; i < height; i++) {
                Point p = new Point(placeToStart.x,
                        (lowerLeftCorner - (i * MAGIC_NUMBER)));
                edgesStart.add(p);
            }
        }

        //finally finish with an edge end at the origin
        Point p = new Point(placeToStart.x , placeToStart.y);
        edgesEnd.add(p);

        System.out.println("building graph for height = " + height + " width = " + width);
        GridGraphView.setVertices(vertices);
        GridGraphView.setEdgesStart(edgesStart);
        GridGraphView.setEdgesEnd(edgesEnd);

        //now generate the knot
        GridGraphView.setGeneratingKnot(true);
        Generator.generateKnotLines();
        GridGraphView.repaintView();
        Generator.createLinksBetweenComponents();
        ButtonPanelKnotCreated.enableAndShowPanel(true);
        File f = new File("knots_" + random.nextInt(30000) + ".png");
        GridGraphView.savePicture(f, "png");

        System.out.println("completed saving graph image in file " + f.getPath());
        GridGraphView.clearGraph();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        SampleKnotCreator sampleKnotCreator = new SampleKnotCreator();
        MainWindow.createAndShowGUI();
        sampleKnotCreator.createSampleKnot();
    }

}

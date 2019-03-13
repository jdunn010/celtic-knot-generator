package knot.generation;

import gui.GridGraphView;
import structure.*;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;


public class Generator {

    private static final int crossingLength = 10;
    private static final int pointedReturnDistance = 10;
    private static CelticKnot celticKnot;

    private static void calculateCrossingsFromEdgeMidpoints() {
        ArrayList<Point> edgesStart = GridGraphView.getEdgesStart();
        ArrayList<Point> edgesEnd = GridGraphView.getEdgesEnd();
        ArrayList<Point> edgesMidpoint = new ArrayList<>();

        for (int i = 0; i < edgesStart.size(); i++) {
            Point start = edgesStart.get(i);
            Point end = edgesEnd.get(i);
            int x = (start.x + end.x) / 2;
            int y = (start.y + end.y) / 2;
            Point mid = new Point(x, y);
            edgesMidpoint.add(mid);
            double differenceInXAxisValues;
            double differenceInYAxisValues;

            if (start.x == end.x || start.y == end.y) {
                calculateCrossingPointSpecialCaseTwo(mid);
                continue;
            } else if (start.x > end.x && start.y < end.y) {
                differenceInXAxisValues = start.x - end.x;
                differenceInYAxisValues = end.y - start.y;
            } else if (start.x < end.x && start.y > end.y) {
                differenceInXAxisValues = end.x - start.x;
                differenceInYAxisValues = start.y - end.y;
            } else if (start.x < end.x && start.y < end.y) {
                differenceInXAxisValues = end.x - start.x;
                differenceInYAxisValues = end.y - start.y;
            } else if (start.x > end.x && start.y > end.y) {
                differenceInXAxisValues = end.x - start.x;
                differenceInYAxisValues = end.y - start.y;
            } else {
                throw new IllegalArgumentException();
            }
            double oppositeDividedByAdj = differenceInYAxisValues
                    / differenceInXAxisValues;
            double theta = Math.atan(oppositeDividedByAdj);
            theta = Math.toDegrees(theta);
            double delta = theta - 45.0;
            calculateCrossingPointLocations(mid, delta);
        }
        GridGraphView.setEdgesMidpoint(edgesMidpoint);
    }

    private static void calculateCrossingPointLocations(Point mid, double delta) {
        if (delta == 0.0) {
            calculateCrossingPointSpecialCaseCrossingLinesParallel(mid);
            return;
        }

        double length2 = crossingLength / Math.cos(Math.toRadians(delta));
        double length3 = length2 * Math.sin(Math.toRadians(delta));

        Point2D.Double crossingPoint1 = new Point2D.Double(mid.x + length2, mid.y - length3);
        Point2D.Double crossingPoint2 = new Point2D.Double(mid.x - length2, mid.y + length3);
        Point2D.Double crossingPoint3 = new Point2D.Double(mid.x + length3, mid.y + length2);
        Point2D.Double crossingPoint4 = new Point2D.Double(mid.x - length3, mid.y - length2);

        addCrossingPointsToArrays(crossingPoint1, crossingPoint2, crossingPoint3, crossingPoint4);
    }

    private static void calculateCrossingPointSpecialCaseCrossingLinesParallel(Point mid) {
        Point2D.Double crossingPoint1 = new Point2D.Double(mid.x + crossingLength, mid.y);
        Point2D.Double crossingPoint2 = new Point2D.Double(mid.x - crossingLength, mid.y);
        Point2D.Double crossingPoint3 = new Point2D.Double(mid.x, mid.y + crossingLength);
        Point2D.Double crossingPoint4 = new Point2D.Double(mid.x, mid.y - crossingLength);
        addCrossingPointsToArrays(crossingPoint1, crossingPoint2, crossingPoint3, crossingPoint4);
    }

    private static void calculateCrossingPointSpecialCaseTwo(Point mid) {
        double length = Math.sqrt(((crossingLength * crossingLength) / 2)); //square root of 50
        Point2D.Double crossingPoint1 = new Point2D.Double(mid.x + length, mid.y - length);
        Point2D.Double crossingPoint2 = new Point2D.Double(mid.x - length, mid.y + length);
        Point2D.Double crossingPoint3 = new Point2D.Double(mid.x + length, mid.y + length);
        Point2D.Double crossingPoint4 = new Point2D.Double(mid.x - length, mid.y - length);
        addCrossingPointsToArrays(crossingPoint1, crossingPoint2, crossingPoint3, crossingPoint4);
    }

    private static void addCrossingPointsToArrays(Point2D.Double crossingPoint1, Point2D.Double crossingPoint2,
                                                  Point2D.Double crossingPoint3, Point2D.Double crossingPoint4) {
        Point crossPoint1 = new Point(
                new Double(Math.floor(crossingPoint1.x)).intValue(),
                new Double(Math.floor(crossingPoint1.y)).intValue());
        Point crossPoint2 = new Point(
                new Double(Math.floor(crossingPoint2.x)).intValue(),
                new Double(Math.floor(crossingPoint2.y)).intValue());
        Point crossPoint3 = new Point(
                new Double(Math.floor(crossingPoint3.x)).intValue(),
                new Double(Math.floor(crossingPoint3.y)).intValue());
        Point crossPoint4 = new Point(
                new Double(Math.floor(crossingPoint4.x)).intValue(),
                new Double(Math.floor(crossingPoint4.y)).intValue());

        Crossing crossing = new Crossing(crossPoint1, crossPoint2, crossPoint3, crossPoint4);
        celticKnot.addKnotComponent(crossing);
    }

    public static void generateKnotLines() {
        celticKnot = new CelticKnot();
        GridGraphView.setGeneratingKnot(true);
        calculateCrossingsFromEdgeMidpoints();
        calcStraightLinesBetweenCrossings();
        calcCurvedLinesBetweenCrossings();
        calcReturnSections();
    }

    public static void createLinksBetweenComponents() {
        celticKnot.createLinksBetweenComponents();
        celticKnot.calculateOverAndUnderPattern();
    }

    private static void calcStraightLinesBetweenCrossings() {
        ArrayList<Line2D.Double> crossingLines = celticKnot.getAllCrossingLines();
        ArrayList<Point> edgesMidpoint = GridGraphView.getEdgesMidpoint();
        ArrayList<Point> edgesStart = GridGraphView.getEdgesStart();
        ArrayList<Point> edgesEnd = GridGraphView.getEdgesEnd();

        for (int i = 0; i < crossingLines.size(); i++) {
            ArrayList<Line2D.Double> completlyOverlappingCrossingLines = new ArrayList<>();

            Line2D.Double currentCrossingLine = crossingLines.get(i);

            Point pointOne = new Point(
                    new Double(currentCrossingLine.getX1()).intValue(),
                    new Double(currentCrossingLine.getY1()).intValue());
            Point pointTwo = new Point(
                    new Double(currentCrossingLine.getX2()).intValue(),
                    new Double(currentCrossingLine.getY2()).intValue());
            Point midPoint = new Point(
                    (pointOne.x + pointTwo.x) / 2,
                    (pointOne.y + pointTwo.y) / 2);

            int positionInArray = findEdgeMidPointFromCrossingMidPoint(
                    midPoint, edgesMidpoint);

            Point edgeVertexOne = edgesStart.get(positionInArray);
            Point edgeVertexTwo = edgesEnd.get(positionInArray);

            for (int j = 0; j < crossingLines.size(); j++) {
                Line2D.Double crossingLineToCompare = crossingLines.get(j);
                if (j != i
                        && currentCrossingLine.ptLineDist(crossingLineToCompare.getP1()) == 0.0
                        && currentCrossingLine.ptLineDist(crossingLineToCompare.getP2()) == 0.0) {
                    Point crossingLineToComparePointOne = new Point(
                            new Double(crossingLineToCompare.getX1()).intValue(),
                            new Double(crossingLineToCompare.getY1()).intValue());
                    Point crossingLineToComparePointTwo = new Point(
                            new Double(crossingLineToCompare.getX2()).intValue(),
                            new Double(crossingLineToCompare.getY2()).intValue());
                    Point crossingLineToCompareMidPoint = new Point(
                            (crossingLineToComparePointOne.x + crossingLineToComparePointTwo.x) / 2,
                            (crossingLineToComparePointOne.y + crossingLineToComparePointTwo.y) / 2);

                    int crossingLineToComparePositionInArray = findEdgeMidPointFromCrossingMidPoint(
                            crossingLineToCompareMidPoint, edgesMidpoint);
                    Point crossingLineToCompareEdgeVertexOne = edgesStart
                            .get(crossingLineToComparePositionInArray);
                    Point crossingLineToComparedgeVertexTwo = edgesEnd
                            .get(crossingLineToComparePositionInArray);

                    if (edgeVertexOne.equals(crossingLineToCompareEdgeVertexOne)
                            || edgeVertexOne.equals(crossingLineToComparedgeVertexTwo)
                            || edgeVertexTwo.equals(crossingLineToCompareEdgeVertexOne)
                            || edgeVertexTwo.equals(crossingLineToComparedgeVertexTwo)) {
                        completlyOverlappingCrossingLines.add(crossingLineToCompare);
                    }
                }
            }

            int crossingLinesSize = crossingLines.size();
            crossingLines = calcClosestCrossingLineAndStoreInKnot(
                    completlyOverlappingCrossingLines,
                    currentCrossingLine,
                    crossingLines);
            if (crossingLinesSize != crossingLines.size()) {
                i = -1;
            }
        }
    }

    private static ArrayList<Line2D.Double> calcClosestCrossingLineAndStoreInKnot(ArrayList<Line2D.Double> completlyOverlappingCrossingLines,
                                                                                  Line2D.Double line, ArrayList<Line2D.Double> crossingLines) {
        if (completlyOverlappingCrossingLines.size() == 0) {
            return crossingLines;
        }
        Point2D.Double startDouble = (Point2D.Double) line.getP1();

        Point start = new Point(
                new Double(startDouble.getX()).intValue(),
                new Double(startDouble.getY()).intValue());

        Point2D.Double endDouble = (Point2D.Double) line.getP2();

        Point end = new Point(
                new Double(endDouble.getX()).intValue(),
                new Double(endDouble.getY()).intValue());

        Line2D.Double closestToStartPoint = null;
        Line2D.Double closestToEndPoint = null;

        for (Line2D.Double temp : completlyOverlappingCrossingLines) {
            if (temp.ptSegDist(start) < temp.ptSegDist(end)
                    && (closestToStartPoint == null || temp.ptSegDist(start) <
                    closestToStartPoint.ptSegDist(start))) {
                if (line.ptSegDist(temp.getP1()) < line.ptSegDist(temp.getP2()) && canPointsBeConnectedWithOutIntersectingExistingEdge(
                        start, temp.getP1())) {
                    closestToStartPoint = new Line2D.Double(start, temp.getP1());
                } else if (line.ptSegDist(temp.getP1()) > line.ptSegDist(temp.getP2())
                        && canPointsBeConnectedWithOutIntersectingExistingEdge(start, temp.getP2())) {
                    closestToStartPoint = new Line2D.Double(start, temp.getP2());
                }
            } else if (temp.ptSegDist(start) > temp.ptSegDist(end)
                    && (closestToEndPoint == null || temp.ptSegDist(end) <
                    closestToEndPoint.ptSegDist(end))) {
                if (line.ptSegDist(temp.getP1()) < line.ptSegDist(temp.getP2()) && canPointsBeConnectedWithOutIntersectingExistingEdge(
                        end, temp.getP1())) {
                    closestToEndPoint = new Line2D.Double(end, temp.getP1());
                } else if (line.ptSegDist(temp.getP1()) > line.ptSegDist(temp.getP2())
                        && canPointsBeConnectedWithOutIntersectingExistingEdge(end, temp.getP2())) {
                    closestToEndPoint = new Line2D.Double(end, temp.getP2());
                }
            }
        }
        if (closestToStartPoint != null) {
            Point linePointOne = new Point(
                    new Double(closestToStartPoint.getX1()).intValue(),
                    new Double(closestToStartPoint.getY1()).intValue());
            Point linePointTwo = new Point(
                    new Double(closestToStartPoint.getX2()).intValue(),
                    new Double(closestToStartPoint.getY2()).intValue());
            StraightLine straightLineOne = new StraightLine(null, null,
                    linePointOne, linePointTwo);
            celticKnot.addKnotComponent(straightLineOne);
        }
        if (closestToEndPoint != null) {
            Point linePointOne = new Point(
                    new Double(closestToEndPoint.getX1()).intValue(),
                    new Double(closestToEndPoint.getY1()).intValue());
            Point linePointTwo = new Point(
                    new Double(closestToEndPoint.getX2()).intValue(),
                    new Double(closestToEndPoint.getY2()).intValue());
            StraightLine straightLineTwo = new StraightLine(null, null,
                    linePointOne, linePointTwo);
            celticKnot.addKnotComponent(straightLineTwo);
        }
        for (int i = 0; i < crossingLines.size(); i++) {
            Line2D.Double crossingLine = crossingLines.get(i);
            if (crossingLine.equals(closestToStartPoint)
                    || crossingLine.equals(closestToEndPoint) || crossingLine.equals(line)) {
                crossingLines.remove(i);
                i--;
            }
        }
        return crossingLines;
    }

    private static void calcCurvedLinesBetweenCrossings() {
        ArrayList<Point> edgesMidpoint = GridGraphView.getEdgesMidpoint();
        ArrayList<Point> edgesStart = GridGraphView.getEdgesStart();
        ArrayList<Point> edgesEnd = GridGraphView.getEdgesEnd();

        ArrayList<Line2D.Double> unconnectedCrossingPoints = celticKnot.getUnconnectedCrossingPointsInLineForm();

        for (int i = 0; i < unconnectedCrossingPoints.size(); i++) {
            Line2D.Double crossingLine = unconnectedCrossingPoints.get(i);
            Point unconnectedCrossingPoint = new Point(
                    new Double(crossingLine.getX1()).intValue(),
                    new Double(crossingLine.getY1()).intValue());
            Point otherPointOnLine = new Point(
                    new Double(crossingLine.getX2()).intValue(),
                    new Double(crossingLine.getY2()).intValue());
            Point midPoint = new Point(
                    (unconnectedCrossingPoint.x + otherPointOnLine.x) / 2,
                    (unconnectedCrossingPoint.y + otherPointOnLine.y) / 2);

            int positionInArray = findEdgeMidPointFromCrossingMidPoint(
                    midPoint, edgesMidpoint);

            Point edgeVertexOne = edgesStart.get(positionInArray);
            Point edgeVertexTwo = edgesEnd.get(positionInArray);
            Line2D.Double closestEdge;
            Point closestVertex;

            if (unconnectedCrossingPoint.distance(edgeVertexOne) <
                    unconnectedCrossingPoint
                            .distance(edgeVertexTwo)) {
                closestVertex = edgeVertexOne;
                closestEdge = getClosestEdgeToPointOnCrossing(
                        unconnectedCrossingPoint, edgeVertexOne, edgeVertexTwo);
            } else {
                closestVertex = edgeVertexTwo;
                closestEdge = getClosestEdgeToPointOnCrossing(
                        unconnectedCrossingPoint, edgeVertexTwo, edgeVertexOne);
            }
            if (closestEdge == null) {
                continue;
            }

            Point closestPointToUnconnectedCrossingPoint;

            if (closestEdge.ptLineDist(edgeVertexOne) == 0.0
                    && closestEdge.ptLineDist(edgeVertexTwo) == 0.0) {

                ArrayList<Line2D.Double> unconnectedCrossingPointsOnClosestEdge = findUnconnectedCrossingPointsOnEdge(
                        unconnectedCrossingPoints,
                        new Point(new Double(closestEdge.getX1()).intValue(),
                                new Double(closestEdge.getY1()).intValue()),
                        new Point(new Double(closestEdge.getX2()).intValue(), new Double(closestEdge.getY2()).intValue()));
                closestPointToUnconnectedCrossingPoint = findUnconnectedCrossingPointClosestToPoint(
                        unconnectedCrossingPointsOnClosestEdge,
                        unconnectedCrossingPoint);
            } else {
                Polygon searchArea = calculateSeachAreaFromUnconnectedCrossingPoint(
                        unconnectedCrossingPoint, closestEdge);

                closestPointToUnconnectedCrossingPoint = getClosestPointToUnconnectedPointInSearchArea(
                        unconnectedCrossingPoints, searchArea, unconnectedCrossingPoint, closestVertex);
            }
            if (closestPointToUnconnectedCrossingPoint == null) {
                continue;
            }

            Line2D.Double otherCrossingLine = findCrossingLineInUnconnectedCrossingsFromCrossingPoint(
                    unconnectedCrossingPoints,
                    closestPointToUnconnectedCrossingPoint);
            calculateCurveBetweenTwoCrossingPoints(unconnectedCrossingPoint,
                    closestPointToUnconnectedCrossingPoint, crossingLine, otherCrossingLine);
            unconnectedCrossingPoints =
                    removeConnectingPairFromUnconnectedCrossingPoints(
                            unconnectedCrossingPoints, unconnectedCrossingPoint,
                            closestPointToUnconnectedCrossingPoint);
            i = -1;
        }
    }

    private static ArrayList<Line2D.Double> removeConnectingPairFromUnconnectedCrossingPoints(
            ArrayList<Line2D.Double> unconnectedCrossingPoints,
            Point unconnectedCrossingPoint,
            Point closestPointToUnconnectedCrossingPoint) {

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < unconnectedCrossingPoints.size(); j++) {
                Line2D.Double temp = unconnectedCrossingPoints.get(j);
                if (temp.getP1().equals(unconnectedCrossingPoint) ||
                        temp.getP1().equals(closestPointToUnconnectedCrossingPoint)) {
                    temp = null;
                    unconnectedCrossingPoints.remove(j);
                    break;
                }
            }
        }

        return unconnectedCrossingPoints;
    }

    private static int findEdgeMidPointFromCrossingMidPoint(Point midPointOfCrossing,
                                                            ArrayList<Point> edgesMidpoint) {
        int numberOfEdgesMidpoints = edgesMidpoint.size();

        int positionInArray = -1;

        if (numberOfEdgesMidpoints == 1) {
            positionInArray = 0;
        } else if (edgesMidpoint.indexOf(midPointOfCrossing) != -1) {
            positionInArray = edgesMidpoint.indexOf(midPointOfCrossing);
        } else {
            Point currentNearest = edgesMidpoint.get(0);
            for (int i = 1; i < edgesMidpoint.size(); i++) {
                if (edgesMidpoint.get(i).distance(midPointOfCrossing) <
                        currentNearest.distance(midPointOfCrossing)) {
                    currentNearest = edgesMidpoint.get(i);
                }
            }
            positionInArray = edgesMidpoint.indexOf(currentNearest);
        }

        return positionInArray;
    }

    private static void calculateCurveBetweenTwoCrossingPoints(Point start, Point end, Line2D.Double crossingLineOne,
                                                               Line2D.Double crossingLineTwo) {
        ArrayList<Point2D.Double> controlPoints = calculateControlPointsForCurve(crossingLineOne, crossingLineTwo, start, end);
        CurvedLine curve;
        if (controlPoints.size() == 1) {
            Point2D.Double controlPoint = controlPoints.get(0);
            curve = new CurvedLine(null, null, start, end, controlPoint,
                    controlPoint);
        } else {
            Point2D.Double controlPointOne = controlPoints.get(0);
            Point2D.Double controlPointTwo = controlPoints.get(1);
            curve = new CurvedLine(null, null, start, end, controlPointOne,
                    controlPointTwo);
        }
        celticKnot.addKnotComponent(curve);
    }

    private static Point getClosestPointToUnconnectedPointInSearchArea(ArrayList<Line2D.Double> unconnectedCrossingPoints,
                                                                       Polygon searchArea, Point unconnectedCrossingPoint,
                                                                       Point closestVertexToUnconnectedCrossingPoint) {
        ArrayList<Point> possiblePointsToConnectTo = new ArrayList<>();

        for (Line2D.Double tempCrossingLine : unconnectedCrossingPoints) {
            Point tempCrossingPoint = new Point(
                    new Double(tempCrossingLine.getX1()).intValue(),
                    new Double(tempCrossingLine.getY1()).intValue());
            if (searchArea.contains(tempCrossingPoint)) {
                possiblePointsToConnectTo.add(tempCrossingPoint);
            }
        }

        possiblePointsToConnectTo.remove(unconnectedCrossingPoint);

        Point closestPointToUnconnectedCrossingPoint = null;
        for (Point pointToCompare : possiblePointsToConnectTo) {
            if (closestPointToUnconnectedCrossingPoint == null
                    && canPointsBeConnectedWithOutIntersectingExistingEdge(pointToCompare, unconnectedCrossingPoint)) {
                closestPointToUnconnectedCrossingPoint = pointToCompare;
            } else if (closestPointToUnconnectedCrossingPoint != null
                    && pointToCompare
                    .distance(closestVertexToUnconnectedCrossingPoint) < closestPointToUnconnectedCrossingPoint
                    .distance(closestVertexToUnconnectedCrossingPoint) && canPointsBeConnectedWithOutIntersectingExistingEdge(
                    pointToCompare, unconnectedCrossingPoint)) {
                closestPointToUnconnectedCrossingPoint = pointToCompare;
            }
        }
        return closestPointToUnconnectedCrossingPoint;
    }

    private static boolean canPointsBeConnectedWithOutIntersectingExistingEdge(Point2D firstPoint, Point2D secondPoint) {
        ArrayList<Point> edgesStart = GridGraphView.getEdgesStart();
        ArrayList<Point> edgesEnd = GridGraphView.getEdgesEnd();
        return canPointsBeConnectedWithOutIntersectingExistingGivenEdges(firstPoint, secondPoint, edgesStart, edgesEnd);
    }

    private static boolean
    canPointsBeConnectedWithOutIntersectingExistingEdgeMinusCurrentEdge(Point2D firstPoint, Point2D secondPoint, Line2D.Double edge) {
        ArrayList<Point> edgesStart = GridGraphView.getEdgesStart();
        ArrayList<Point> edgesEnd = GridGraphView.getEdgesEnd();

        ArrayList<Point> edgesStartToBeUsed = new ArrayList<>();
        ArrayList<Point> edgesEndToBeUsed = new ArrayList<>();

        Point vertexOne = new Point(new Double(edge.getX1()).intValue(), new Double(edge.getY1()).intValue());
        Point vertexTwo = new Point(new Double(edge.getX2()).intValue(), new Double(edge.getY2()).intValue());

        for (int i = 0; i < edgesStart.size(); i++) {
            if (!(edgesStart.get(i).equals(vertexOne) && edgesEnd.get(i).equals(vertexTwo))
                    && !(edgesEnd.get(i).equals(vertexOne) && edgesStart.get(i).equals(vertexTwo))) {
                edgesStartToBeUsed.add(edgesStart.get(i));
                edgesEndToBeUsed.add(edgesEnd.get(i));
            }
        }
        return canPointsBeConnectedWithOutIntersectingExistingGivenEdges(
                firstPoint, secondPoint, edgesStartToBeUsed, edgesEndToBeUsed);
    }

    private static boolean
    canPointsBeConnectedWithOutIntersectingExistingGivenEdges(Point2D firstPoint, Point2D secondPoint,
                                                              ArrayList<Point> edgesStart, ArrayList<Point> edgesEnd) {
        boolean intersects = false;
        Line2D.Double proposedLine = new Line2D.Double(firstPoint, secondPoint);

        for (int i = 0; i < edgesStart.size(); i++) {
            Line2D.Double edge = new Line2D.Double(edgesStart.get(i), edgesEnd
                    .get(i));
            if (proposedLine.intersectsLine(edge)
                    && !proposedLine.getP1().equals(edge.getP1())
                    && !proposedLine.getP1().equals(edge.getP2())
                    && !proposedLine.getP2().equals(edge.getP1())
                    && !proposedLine.getP2().equals(edge.getP2())) {
                intersects = true;
                break;
            }
        }
        return !intersects;
    }

    private static Polygon calculateSeachAreaFromUnconnectedCrossingPoint(Point unconnectedCrossingPoint, Line2D.Double closestEdge) {
        int[] xPoints = new int[3];
        int[] yPoints = new int[3];
        xPoints[0] = unconnectedCrossingPoint.x;
        yPoints[0] = unconnectedCrossingPoint.y;
        xPoints[1] = new Double(closestEdge.getX1()).intValue();
        yPoints[1] = new Double(closestEdge.getY1()).intValue();
        xPoints[2] = new Double(closestEdge.getX2()).intValue();
        yPoints[2] = new Double(closestEdge.getY2()).intValue();
        return new Polygon(xPoints, yPoints, 3);
    }

    private static ArrayList<Line2D.Double> getAllEdgesUsingVertex(Point vertex) {
        ArrayList<Point> edgesStart = GridGraphView.getEdgesStart();
        ArrayList<Point> edgesEnd = GridGraphView.getEdgesEnd();
        ArrayList<Line2D.Double> edges = new ArrayList<>();

        for (int i = 0; i < edgesStart.size(); i++) {
            Point start = edgesStart.get(i);
            Point end = edgesEnd.get(i);
            if (vertex.equals(start) || vertex.equals(end)) {
                Line2D.Double edge = new Line2D.Double(start, end);
                edges.add(edge);
            }
        }
        return edges;
    }

    private static ArrayList<Line2D.Double> getAllEdgesUsingVertexMinusCurrentEdge(Point startVertex, Point endVertex) {
        ArrayList<Line2D.Double> edges = getAllEdgesUsingVertex(startVertex);

        for (int i = 0; i < edges.size(); i++) {
            Line2D.Double edge = edges.get(i);
            Point start = new Point(
                    new Double(edge.getX1()).intValue(),
                    new Double(edge.getY1()).intValue());
            Point end = new Point(
                    new Double(edge.getX2()).intValue(),
                    new Double(edge.getY2()).intValue());
            if (endVertex.equals(start) || endVertex.equals(end)) {
                edges.remove(i);
                break;
            }
        }

        return edges;
    }

    private static Line2D.Double getClosestEdgeToPointOnCrossing(Point pointOnCrossing, Point closestEdgeVertex,
                                                                 Point otherEdgeVertex) {
        ArrayList<Line2D.Double> edges = getAllEdgesUsingVertexMinusCurrentEdge(closestEdgeVertex, otherEdgeVertex);
        Line2D.Double closestEdge = null;

        for (Line2D.Double edge : edges) {
            Point furthestVertex;
            if (pointOnCrossing.distance(edge.getP1()) < pointOnCrossing
                    .distance(edge.getP2())) {
                furthestVertex = new Point(
                        new Double(edge.getX2()).intValue(),
                        new Double(edge.getY2()).intValue());
            } else {
                furthestVertex = new Point(
                        new Double(edge.getX1()).intValue(),
                        new Double(edge.getY1()).intValue());
            }

            if (canPointsBeConnectedWithOutIntersectingExistingEdgeMinusCurrentEdge(pointOnCrossing, furthestVertex, edge) == false) {
                continue;
            }

            if (closestEdge == null) {
                closestEdge = edge;
            } else if (edge.ptLineDist(pointOnCrossing) < closestEdge.ptLineDist(pointOnCrossing)) {
                closestEdge = edge;
            }
        }
        return closestEdge;
    }

    private static void calcReturnSections() {
        ArrayList<Point> edgesMidpoint = GridGraphView.getEdgesMidpoint();
        ArrayList<Point> edgesStart = GridGraphView.getEdgesStart();
        ArrayList<Point> edgesEnd = GridGraphView.getEdgesEnd();

        ArrayList<Line2D.Double> unconnectedCrossingPointsInLineForm = celticKnot.getUnconnectedCrossingPointsInLineForm();

        for (int i = 0; i < unconnectedCrossingPointsInLineForm.size(); i++) {
            Line2D.Double crossingLine = unconnectedCrossingPointsInLineForm.get(i);
            Point unconnectedCrossingPoint = new Point(
                    new Double(crossingLine.getX1()).intValue(),
                    new Double(crossingLine.getY1()).intValue());

            Point otherPointOnLine = new Point(
                    new Double(crossingLine.getX2()).intValue(),
                    new Double(crossingLine.getY2()).intValue());

            Point midPoint = new Point(
                    (unconnectedCrossingPoint.x + otherPointOnLine.x) / 2,
                    (unconnectedCrossingPoint.y + otherPointOnLine.y) / 2);

            int positionInArray = findEdgeMidPointFromCrossingMidPoint(midPoint, edgesMidpoint);

            Point edgeVertexOne = edgesStart.get(positionInArray);
            Point edgeVertexTwo = edgesEnd.get(positionInArray);

            Point crossingPointToConnectTo;

            if (unconnectedCrossingPoint.distance(edgeVertexOne) <
                    unconnectedCrossingPoint.distance(edgeVertexTwo)) {
                crossingPointToConnectTo = findOtherCrossingPointForReturn(unconnectedCrossingPointsInLineForm,
                        unconnectedCrossingPoint,
                        edgeVertexOne,
                        edgeVertexTwo);

                if (crossingPointToConnectTo == null) {
                    continue;
                }

                Line2D.Double otherCrossingLine = findCrossingLineInUnconnectedCrossingsFromCrossingPoint(
                        unconnectedCrossingPointsInLineForm, crossingPointToConnectTo);
                int positionInArrayOfOtherEdge =
                        findEdgeMidPointFromCrossingMidPoint(
                                new Point(
                                        new Double(Math.floor((otherCrossingLine
                                                .getX1() + otherCrossingLine
                                                .getX1()) / 2))
                                                .intValue(),
                                        new Double(Math.floor((otherCrossingLine
                                                .getY1() + otherCrossingLine
                                                .getY1()) / 2))
                                                .intValue()), edgesMidpoint);
                Point otherEdgeVertexOne = edgesStart
                        .get(positionInArrayOfOtherEdge);
                Point otherEdgeVertexTwo = edgesEnd
                        .get(positionInArrayOfOtherEdge);
                Point otherEdgeVertexToUse;
                if (edgeVertexOne.equals(otherEdgeVertexOne)) {
                    otherEdgeVertexToUse = otherEdgeVertexTwo;
                } else {
                    otherEdgeVertexToUse = otherEdgeVertexOne;
                }
                calculateReturnPartPoint(unconnectedCrossingPoint,
                        crossingPointToConnectTo,
                        edgeVertexTwo,
                        edgeVertexOne,
                        otherEdgeVertexToUse, //called edgeVertexCommon in the method param list
                        crossingLine,
                        otherCrossingLine);
            } else {
                crossingPointToConnectTo = findOtherCrossingPointForReturn(
                        unconnectedCrossingPointsInLineForm, unconnectedCrossingPoint,
                        edgeVertexTwo, edgeVertexOne);
                if (crossingPointToConnectTo == null) {
                    continue;
                }
                Line2D.Double otherCrossingLine = findCrossingLineInUnconnectedCrossingsFromCrossingPoint(
                        unconnectedCrossingPointsInLineForm, crossingPointToConnectTo);
                int positionInArrayOfOtherEdge =
                        findEdgeMidPointFromCrossingMidPoint(
                                new Point(
                                        new Double(Math.floor((otherCrossingLine
                                                .getX1() + otherCrossingLine
                                                .getX1()) / 2)).intValue(),
                                        new Double(Math.floor((otherCrossingLine
                                                .getY1() + otherCrossingLine
                                                .getY1()) / 2)).intValue()), edgesMidpoint);
                Point otherEdgeVertexOne = edgesStart
                        .get(positionInArrayOfOtherEdge);
                Point otherEdgeVertexTwo = edgesEnd
                        .get(positionInArrayOfOtherEdge);
                Point otherEdgeVertexToUse;
                if (edgeVertexTwo.equals(otherEdgeVertexOne)) {
                    otherEdgeVertexToUse = otherEdgeVertexTwo;
                } else {
                    otherEdgeVertexToUse = otherEdgeVertexOne;
                }
                calculateReturnPartPoint(unconnectedCrossingPoint,
                        crossingPointToConnectTo,
                        edgeVertexOne,
                        edgeVertexTwo,
                        otherEdgeVertexToUse,
                        crossingLine,
                        otherCrossingLine);
            }
            unconnectedCrossingPointsInLineForm = removeConnectingPairFromUnconnectedCrossingPoints(
                    unconnectedCrossingPointsInLineForm, unconnectedCrossingPoint, crossingPointToConnectTo);
            i = -1;
        }
    }

    private static int numberOfEdgesInvolvingVertex(Point vertex) {
        int count = 0;

        ArrayList<Point> edgesStart = GridGraphView.getEdgesStart();
        ArrayList<Point> edgesEnd = GridGraphView.getEdgesEnd();

        for (int i = 0; i < edgesStart.size(); i++) {
            if (vertex.equals(edgesStart.get(i)) ||
                    vertex.equals(edgesEnd.get(i))) {
                count++;
            }
        }
        return count;
    }

    private static Point findOtherCrossingPointForReturn(ArrayList<Line2D.Double> unconnectedCrossingPoints,
                                                         Point crossingPoint,
                                                         Point closestVertexInEdge,
                                                         Point otherEdgeVertex) {
        Point vertexOfEdgeToConnectTo = getOtherEdgeVertexForReturnCrossingPoint(closestVertexInEdge, otherEdgeVertex);
        return findUnconnectedCrossingPointOnEdgeClosestToVertex(
                unconnectedCrossingPoints,
                closestVertexInEdge,
                vertexOfEdgeToConnectTo,
                crossingPoint);
    }

    private static Point findUnconnectedCrossingPointOnEdgeClosestToVertex(ArrayList<Line2D.Double> unconnectedCrossingPoints,
                                                                           Point vertex,
                                                                           Point otherEdgeVertex,
                                                                           Point crossingPoint) {
        ArrayList<Line2D.Double> unconnectedCrossingPointsOnThisEdge =
                findUnconnectedCrossingPointsOnEdgeMinusCrossingPointInUse(
                    unconnectedCrossingPoints,
                            vertex,
                            otherEdgeVertex,
                            crossingPoint);
        Point closestCrossingPointToVertex =
                findUnconnectedCrossingPointClosestToPoint(unconnectedCrossingPointsOnThisEdge, vertex);
        return closestCrossingPointToVertex;
    }

    private static Point findUnconnectedCrossingPointClosestToPoint(ArrayList<Line2D.Double> unconnectedCrossingPoints,
                                                                    Point point) {
        Point closestCrossingPointToPoint = null;

        for (Line2D.Double tempCrossingLine : unconnectedCrossingPoints) {
            Point tempCrossingPoint = new Point(
                    new Double(tempCrossingLine.getX1()).intValue(),
                    new Double(tempCrossingLine.getY1()).intValue());

            if (closestCrossingPointToPoint == null ||
                    closestCrossingPointToPoint.distance(point) > tempCrossingPoint.distance(point)) {
                closestCrossingPointToPoint = tempCrossingPoint;
            }
        }
        return closestCrossingPointToPoint;
    }

    private static ArrayList<Line2D.Double> findUnconnectedCrossingPointsOnEdgeMinusCrossingPointInUse(
            ArrayList<Line2D.Double> unconnectedCrossingPoints,
            Point vertex,
            Point otherEdgeVertex,
            Point crossingPoint) {
        ArrayList<Line2D.Double> unconnectedCrossingPointsOnThisEdge =
                findUnconnectedCrossingPointsOnEdge(unconnectedCrossingPoints, vertex, otherEdgeVertex);

        for (int i = 0; i < unconnectedCrossingPointsOnThisEdge.size(); i++) {
            if (unconnectedCrossingPointsOnThisEdge.get(i).getP1().equals(crossingPoint)) {
                unconnectedCrossingPointsOnThisEdge.remove(i);
                break;
            }
        }

        return unconnectedCrossingPointsOnThisEdge;
    }

    private static ArrayList<Line2D.Double> findUnconnectedCrossingPointsOnEdge(ArrayList<Line2D.Double> unconnectedCrossingPoints,
                                                                                Point vertex,
                                                                                Point otherEdgeVertex) {
        ArrayList<Line2D.Double> unconnectedCrossingPointsOnThisEdge = new ArrayList<>();

        Point midPoint = new Point(
                (vertex.x + otherEdgeVertex.x) / 2,
                (vertex.y + otherEdgeVertex.y) / 2);

        for (Line2D.Double tempCrossingPoint : unconnectedCrossingPoints) {
            Point start = new Point(
                    new Double(Math.floor(tempCrossingPoint.getX1())).intValue(),
                    new Double(Math.floor(tempCrossingPoint.getY1())).intValue());
            Point end = new Point(
                    new Double(Math.floor(tempCrossingPoint.getX2())).intValue(),
                    new Double(Math.floor(tempCrossingPoint.getY2())).intValue());

            Point midPointOfCrossing = new Point(
                    (start.x + end.x) / 2,
                    (start.y + end.y) / 2);

            if (midPointOfCrossing.distanceSq(midPoint) < 3.0) {
                unconnectedCrossingPointsOnThisEdge.add(tempCrossingPoint);
            }
        }

        return unconnectedCrossingPointsOnThisEdge;
    }

    private static Point getOtherEdgeVertexForReturnCrossingPoint(Point vertex, Point otherKnownEdgeVertex) {
        if (numberOfEdgesInvolvingVertex(vertex) == 1) {
            return otherKnownEdgeVertex;
        } else {
            ArrayList<Line2D.Double> edgesUsingCurrentVertex =
                    getAllEdgesUsingVertexMinusCurrentEdge(vertex, otherKnownEdgeVertex);

            double lengthOfMainEdge = vertex.distance(otherKnownEdgeVertex);
            double normalisingValOfMainEdge = 1 / lengthOfMainEdge;
            double normalisedXValOfMainEdge = normalisingValOfMainEdge * (otherKnownEdgeVertex.x - vertex.x);
            double normalisedYValOfMainEdge = normalisingValOfMainEdge * (otherKnownEdgeVertex.y - vertex.y);

            Point vertexOfOuterMostEdge = null;

            double angleBetweenMainEdgeAndOuterMostEdge = 0.0;

            for (Line2D.Double tempLine : edgesUsingCurrentVertex) {
                Point startPoint = new Point(
                        new Double(tempLine.getX1()).intValue(),
                        new Double(tempLine.getY1()).intValue());
                Point endPoint = new Point(
                        new Double(tempLine.getX2()).intValue(),
                        new Double(tempLine.getY2()).intValue());
                Point pointToUse;
                if (vertex.equals(startPoint)) {
                    pointToUse = endPoint;
                } else {
                    pointToUse = startPoint;
                }
                double lengthOfEdge = vertex.distance(pointToUse);
                double normalisingValOfEdge = 1 / lengthOfEdge;
                double normalisedXValOfEdge = normalisingValOfEdge * (pointToUse.x - vertex.x);
                double normalisedYValOfEdge = normalisingValOfEdge * (pointToUse.y - vertex.y);

                double dotProductOfVectors = (normalisedXValOfMainEdge * normalisedXValOfEdge)
                        + (normalisedYValOfMainEdge * normalisedYValOfEdge);
                double angleBetweenEdges = Math.toDegrees(Math
                        .acos(dotProductOfVectors));

                if (vertexOfOuterMostEdge == null
                        || angleBetweenMainEdgeAndOuterMostEdge < angleBetweenEdges) {
                    vertexOfOuterMostEdge = pointToUse;
                    angleBetweenMainEdgeAndOuterMostEdge = angleBetweenEdges;
                }
            }
            return vertexOfOuterMostEdge;
        }
    }

    private static Line2D.Double findCrossingLineInUnconnectedCrossingsFromCrossingPoint(
            ArrayList<Line2D.Double> unconnectedCrossingPoints,
            Point crossingPoint) {
        for (Line2D.Double tempLine : unconnectedCrossingPoints) {
            if (tempLine.getP1().equals(crossingPoint)) {
                return tempLine;
            }
        }
        return null;
    }

    private static ArrayList<Point2D.Double> calculateControlPointsForCurve(Line2D.Double lineOne,
                                                                            Line2D.Double lineTwo,
                                                                            Point crossingPointOne,
                                                                            Point crossingPointTwo) {
        ArrayList<Point2D.Double> controlPoints = new ArrayList<>();

        double firstEdgeYDiff;
        double firstEdgeXDiff;
        double secondEdgeYDiff;
        double secondEdgeXDiff;

        Point otherPointOnCrossingLineOne;
        Point otherPointOnCrossingLineTwo;

        if (crossingPointOne.equals(lineOne.getP1())) {
            otherPointOnCrossingLineOne = new Point(
                    new Double(lineOne.getX2()).intValue(),
                    new Double(lineOne.getY2()).intValue());
        } else {
            otherPointOnCrossingLineOne = new Point(
                    new Double(lineOne.getX1()).intValue(),
                    new Double(lineOne.getY1()).intValue());
        }

        if (crossingPointTwo.equals(lineTwo.getP1())) {
            otherPointOnCrossingLineTwo = new Point(
                    new Double(lineTwo.getX2()).intValue(),
                    new Double(lineTwo.getY2()).intValue());
        } else {
            otherPointOnCrossingLineTwo = new Point(
                    new Double(lineTwo.getX1()).intValue(),
                    new Double(lineTwo.getY1()).intValue());
        }

        double multiplingFactorOne = crossingPointOne.distance(crossingPointTwo)
                / crossingPointOne.distance(otherPointOnCrossingLineOne);
        double multiplingFactorTwo = crossingPointOne.distance(crossingPointTwo)
                / crossingPointTwo.distance(otherPointOnCrossingLineTwo);

        if (multiplingFactorOne > 2.0) {
            multiplingFactorOne = 2.0;
        }
        if (multiplingFactorTwo > 2.0) {
            multiplingFactorTwo = 2.0;
        }

        firstEdgeXDiff = (crossingPointOne.getX() - otherPointOnCrossingLineOne.getX()) * multiplingFactorOne;
        firstEdgeYDiff = (crossingPointOne.getY() - otherPointOnCrossingLineOne.getY()) * multiplingFactorOne;
        secondEdgeXDiff = (crossingPointTwo.getX() - otherPointOnCrossingLineTwo.getX()) * multiplingFactorTwo;
        secondEdgeYDiff = (crossingPointTwo.getY() - otherPointOnCrossingLineTwo.getY()) * multiplingFactorTwo;

        Point2D.Double controlPointOne = new Point2D.Double(
                crossingPointOne.getX() + firstEdgeXDiff,
                crossingPointOne.getY() + firstEdgeYDiff);
        Point2D.Double controlPointTwo = new Point2D.Double(
                crossingPointTwo.getX() + secondEdgeXDiff,
                crossingPointTwo.getY() + secondEdgeYDiff);

        controlPoints.add(controlPointOne);
        controlPoints.add(controlPointTwo);

        return controlPoints;
    }

    private static void calculateReturnPartPoint(Point crossingPointOne,
                                                 Point crossingPointTwo,
                                                 Point edgeVertexOne,
                                                 Point edgeVertexCommon,
                                                 Point edgeVertexTwo,
                                                 Line2D.Double crossingLineOne,
                                                 Line2D.Double crossingLineTwo) {

        Point commonReturnPoint;
        Point firstReturnPoint = null;
        Point secondReturnPoint = null;

        if (edgeVertexOne.equals(edgeVertexTwo)) {
            commonReturnPoint = calculateCommonPointForPointedReturn(edgeVertexOne, edgeVertexCommon);
            if (edgeVertexOne.x < edgeVertexCommon.x
                    && edgeVertexOne.y < edgeVertexCommon.y) {
                double differenceInY = edgeVertexCommon.y - edgeVertexOne.y;
                double differenceInX = edgeVertexCommon.x - edgeVertexOne.x;
                double gamma = Math.toDegrees(Math.atan(differenceInY
                        / differenceInX));
                double alpha = 45.0 - gamma;
                double offsetInX = pointedReturnDistance * Math.cos(Math.toRadians(alpha));
                double offsetInY = pointedReturnDistance * Math.sin(Math.toRadians(alpha));

                firstReturnPoint = new Point(
                        new Double(Math.floor(commonReturnPoint.x - offsetInX)).intValue(),
                        new Double(Math.floor(commonReturnPoint.y + offsetInY)).intValue());

                secondReturnPoint = new Point(
                        new Double(Math.floor(commonReturnPoint.x - offsetInY)).intValue(),
                        new Double(Math.floor(commonReturnPoint.y - offsetInX)).intValue());
            } else if (edgeVertexOne.x > edgeVertexCommon.x
                    && edgeVertexOne.y < edgeVertexCommon.y) {
                double differenceInY = edgeVertexCommon.y - edgeVertexOne.y;
                double differenceInX = edgeVertexOne.x - edgeVertexCommon.x;
                double gamma = Math.toDegrees(Math.atan(differenceInY
                        / differenceInX));
                double alpha = 45.0 - gamma;
                double offsetInX = pointedReturnDistance * Math.cos(Math.toRadians(alpha));
                double offsetInY = pointedReturnDistance * Math.sin(Math.toRadians(alpha));

                firstReturnPoint = new Point(
                        new Double(Math.floor(commonReturnPoint.x + offsetInX)).intValue(),
                        new Double(Math.floor(commonReturnPoint.y + offsetInY)).intValue());
                secondReturnPoint = new Point(
                        new Double(Math.floor(commonReturnPoint.x + offsetInY)).intValue(),
                        new Double(Math.floor(commonReturnPoint.y - offsetInX)).intValue());
            } else if (edgeVertexOne.x < edgeVertexCommon.x
                    && edgeVertexOne.y > edgeVertexCommon.y) {
                double differenceInY = edgeVertexOne.y - edgeVertexCommon.y;
                double differenceInX = edgeVertexCommon.x - edgeVertexOne.x;
                double gamma = Math.toDegrees(Math.atan(differenceInY
                        / differenceInX));
                double alpha = 45.0 - gamma;
                double offsetInX = pointedReturnDistance * Math.cos(Math.toRadians(alpha));
                double offsetInY = pointedReturnDistance * Math.sin(Math.toRadians(alpha));
                firstReturnPoint = new Point(
                        new Double(Math.floor(commonReturnPoint.x - offsetInX)).intValue(),
                        new Double(Math.floor(commonReturnPoint.y - offsetInY)).intValue());
                secondReturnPoint = new Point(
                        new Double(Math.floor(commonReturnPoint.x - offsetInY)).intValue(),
                        new Double(Math.floor(commonReturnPoint.y + offsetInX)).intValue());
            } else if (edgeVertexOne.x > edgeVertexCommon.x
                    && edgeVertexOne.y > edgeVertexCommon.y) {
                double differenceInY = edgeVertexOne.y - edgeVertexCommon.y;
                double differenceInX = edgeVertexOne.x - edgeVertexCommon.x;
                double gamma = Math.toDegrees(Math.atan(differenceInY
                        / differenceInX));
                double alpha = 45.0 - gamma;
                double offsetInX = pointedReturnDistance * Math.cos(Math.toRadians(alpha));
                double offsetInY = pointedReturnDistance * Math.sin(Math.toRadians(alpha));

                firstReturnPoint = new Point(
                        new Double(Math.floor(commonReturnPoint.x + offsetInX)).intValue(),
                        new Double(Math.floor(commonReturnPoint.y - offsetInY)).intValue());
                secondReturnPoint = new Point(
                        new Double(Math.floor(commonReturnPoint.x + offsetInY)).intValue(),
                        new Double(Math.floor(commonReturnPoint.y + offsetInX)).intValue());
            } else if (edgeVertexOne.x == edgeVertexCommon.x) {
                double offset = pointedReturnDistance * Math.sin(Math.toRadians(45));
                if (edgeVertexOne.y < edgeVertexCommon.y) {
                    firstReturnPoint = new Point(
                            new Double(Math.floor(commonReturnPoint.x - offset)).intValue(),
                            new Double(Math.floor(commonReturnPoint.y - offset))
                                    .intValue());
                    secondReturnPoint = new Point(
                            new Double(Math.floor(commonReturnPoint.x + offset)).intValue(),
                            new Double(Math.floor(commonReturnPoint.y - offset)).intValue());
                } else {
                    firstReturnPoint = new Point(
                            new Double(Math.floor(commonReturnPoint.x - offset)).intValue(),
                            new Double(Math.floor(commonReturnPoint.y + offset)).intValue());
                    secondReturnPoint = new Point(
                            new Double(Math.floor(commonReturnPoint.x + offset)).intValue(),
                            new Double(Math.floor(commonReturnPoint.y + offset)).intValue());
                }
            } else if (edgeVertexOne.y == edgeVertexCommon.y) {
                double offset = pointedReturnDistance * Math.sin(Math.toRadians(45));
                if (edgeVertexOne.x < edgeVertexCommon.x) {
                    firstReturnPoint = new Point(
                            new Double(Math.floor(commonReturnPoint.x - offset)).intValue(),
                            new Double(Math.floor(commonReturnPoint.y - offset)).intValue());
                    secondReturnPoint = new Point(
                            new Double(Math.floor(commonReturnPoint.x - offset)).intValue(),
                            new Double(Math.floor(commonReturnPoint.y + offset)).intValue());
                } else {
                    firstReturnPoint = new Point(
                            new Double(Math.floor(commonReturnPoint.x + offset)).intValue(),
                            new Double(Math.floor(commonReturnPoint.y - offset)).intValue());
                    secondReturnPoint = new Point(
                            new Double(Math.floor(commonReturnPoint.x + offset)).intValue(),
                            new Double(Math.floor(commonReturnPoint.y + offset)).intValue());
                }
            }
        } else {
            Point commonReturnPointForFirstEdge =
                    calculateCommonPointForPointedReturn(
                            edgeVertexOne, edgeVertexCommon);

            Point commonReturnPointForSecondEdge =
                    calculateCommonPointForPointedReturn(
                            edgeVertexTwo, edgeVertexCommon);
           Point midPointBetweenEdgeReturnPoints = new Point(
                    (commonReturnPointForFirstEdge.x +
                            commonReturnPointForSecondEdge.x) / 2,
                    (commonReturnPointForFirstEdge.y +
                            commonReturnPointForSecondEdge.y) / 2);
            commonReturnPoint =
                    calculateCommonPointForPointedReturn(
                        edgeVertexCommon, midPointBetweenEdgeReturnPoints);

            double lengthOfFirstEdge = edgeVertexOne.distance(edgeVertexCommon);
            double lengthOfSecondEdge = edgeVertexTwo.distance(edgeVertexCommon);

            double firstEdgeRatio = pointedReturnDistance / lengthOfFirstEdge;
            double secondEdgeRatio = pointedReturnDistance / lengthOfSecondEdge;
            double firstEdgeYDiff;
            double firstEdgeXDiff;
            double secondEdgeYDiff;
            double secondEdgeXDiff;

            firstEdgeXDiff = edgeVertexCommon.x - edgeVertexOne.x;
            firstEdgeYDiff = edgeVertexCommon.y - edgeVertexOne.y;
            secondEdgeXDiff = edgeVertexCommon.x - edgeVertexTwo.x;
            secondEdgeYDiff = edgeVertexCommon.y - edgeVertexTwo.y;

            firstEdgeYDiff = firstEdgeYDiff * firstEdgeRatio;
            firstEdgeXDiff = firstEdgeXDiff * firstEdgeRatio;
            secondEdgeYDiff = secondEdgeYDiff * secondEdgeRatio;
            secondEdgeXDiff = secondEdgeXDiff * secondEdgeRatio;

            firstReturnPoint = new Point(
                    new Double(Math.floor(commonReturnPoint.x - firstEdgeXDiff)).intValue(),
                    new Double(Math.floor(commonReturnPoint.y - firstEdgeYDiff)).intValue());
            secondReturnPoint = new Point(
                    new Double(Math.floor(commonReturnPoint.x - secondEdgeXDiff)).intValue(),
                    new Double(Math.floor(commonReturnPoint.y - secondEdgeYDiff)).intValue());
        }

        if (commonReturnPoint != null && firstReturnPoint != null && secondReturnPoint != null) {
            PointedReturn pointedReturn;
            ArrayList<Point2D.Double> firstSetOfControlPoints;
            ArrayList<Point2D.Double> secondSetOfControlPoints;
            Point2D.Double firstControlPoint;
            Point2D.Double secondControlPoint;
            Point2D.Double thirdControlPoint;
            Point2D.Double forthControlPoint;

            Line2D.Double a = new Line2D.Double(firstReturnPoint,
                    commonReturnPoint);
            Line2D.Double b = new Line2D.Double(secondReturnPoint,
                    commonReturnPoint);

            if ((edgeVertexOne.equals(edgeVertexTwo) && canPointsBeConnectedWithOutIntersectingExistingEdge(
                    crossingPointOne, firstReturnPoint)) ||
                    (!edgeVertexOne.equals(edgeVertexTwo) && crossingPointOne.distance(firstReturnPoint) < crossingPointOne
                            .distance(secondReturnPoint))) {
                firstSetOfControlPoints = calculateControlPointsForCurve(crossingLineOne, a, crossingPointOne, firstReturnPoint);
                firstSetOfControlPoints =
                        assignControlPointsToArrayPositions(firstSetOfControlPoints);
                firstControlPoint = firstSetOfControlPoints.get(0);
                secondControlPoint = firstSetOfControlPoints.get(1);

                secondSetOfControlPoints = calculateControlPointsForCurve(crossingLineTwo, b, crossingPointTwo, secondReturnPoint);
                secondSetOfControlPoints =
                        assignControlPointsToArrayPositions(secondSetOfControlPoints);
                thirdControlPoint = secondSetOfControlPoints.get(0);
                forthControlPoint = secondSetOfControlPoints.get(1);

                pointedReturn = new PointedReturn(null, null,
                        crossingPointOne,
                        crossingPointTwo,
                        firstReturnPoint,
                        commonReturnPoint,
                        secondReturnPoint,
                        firstControlPoint,
                        secondControlPoint,
                        thirdControlPoint,
                        forthControlPoint);
            } else {
                firstSetOfControlPoints = calculateControlPointsForCurve(
                        crossingLineOne, b, crossingPointOne, secondReturnPoint);
                firstSetOfControlPoints =
                        assignControlPointsToArrayPositions(firstSetOfControlPoints);
                firstControlPoint = firstSetOfControlPoints.get(0);
                secondControlPoint = firstSetOfControlPoints.get(1);
                secondSetOfControlPoints = calculateControlPointsForCurve(crossingLineTwo, a, crossingPointTwo, firstReturnPoint);
                secondSetOfControlPoints =
                        assignControlPointsToArrayPositions(secondSetOfControlPoints);
                thirdControlPoint = secondSetOfControlPoints.get(0);
                forthControlPoint = secondSetOfControlPoints.get(1);
                pointedReturn = new PointedReturn(null, null,
                        crossingPointOne,
                        crossingPointTwo,
                        secondReturnPoint, //todo note this is second return point here
                        commonReturnPoint,
                        firstReturnPoint,
                        firstControlPoint,
                        secondControlPoint,
                        thirdControlPoint,
                        forthControlPoint);
            }
            celticKnot.addKnotComponent(pointedReturn);
        }
    }

    private static ArrayList<Point2D.Double> assignControlPointsToArrayPositions(ArrayList<Point2D.Double> calculatedControlPoints) {
        ArrayList<Point2D.Double> controlPointsToAssign = new ArrayList<>();
        if (calculatedControlPoints.size() == 1) {
            controlPointsToAssign.add(0, calculatedControlPoints.get(0));
            controlPointsToAssign.add(1, calculatedControlPoints.get(0));
        } else {
            controlPointsToAssign.add(0, calculatedControlPoints.get(0));
            controlPointsToAssign.add(1, calculatedControlPoints.get(1));
        }
        return controlPointsToAssign;
    }

    private static Point calculateCommonPointForPointedReturn(Point edgeVertexOne, Point edgeVertexCommon) {
        Point commonReturnPoint;
        int distanceToUse = pointedReturnDistance * 2;

        if (edgeVertexOne.x < edgeVertexCommon.x && edgeVertexOne.y < edgeVertexCommon.y) {
            double differenceInY = edgeVertexCommon.y - edgeVertexOne.y;
            double differenceInX = edgeVertexCommon.x - edgeVertexOne.x;
            double edgeLength = Math.sqrt((differenceInX * differenceInX) + (differenceInY * differenceInY));
            double gamma = Math.toDegrees(Math.atan(differenceInY / differenceInX));
            double lengthToReturnPoint = edgeLength + distanceToUse;
            double newDifferenceInY = lengthToReturnPoint * Math.sin(Math.toRadians(gamma));
            double newDifferenceInX = lengthToReturnPoint * Math.cos(Math.toRadians(gamma));
            commonReturnPoint = new Point(
                    new Double((edgeVertexOne.x + newDifferenceInX)).intValue(),
                    new Double((edgeVertexOne.y + newDifferenceInY)).intValue());
        } else if (edgeVertexOne.x > edgeVertexCommon.x &&
                edgeVertexOne.y < edgeVertexCommon.y) {
            double differenceInY = edgeVertexCommon.y - edgeVertexOne.y;
            double differenceInX = edgeVertexOne.x - edgeVertexCommon.x;
            double edgeLength = Math.sqrt((differenceInX * differenceInX) + (differenceInY * differenceInY));
            double gamma = Math.toDegrees(Math.atan(differenceInY / differenceInX));
            double lengthToReturnPoint = edgeLength + distanceToUse;
            double newDifferenceInY = lengthToReturnPoint * Math.sin(Math.toRadians(gamma));
            double newDifferenceInX = lengthToReturnPoint * Math.cos(Math.toRadians(gamma));
            commonReturnPoint = new Point(
                    new Double((edgeVertexOne.x - newDifferenceInX)).intValue(),
                    new Double((edgeVertexOne.y + newDifferenceInY)).intValue());
        } else if (edgeVertexOne.x < edgeVertexCommon.x && edgeVertexOne.y > edgeVertexCommon.y) {
            double differenceInY = edgeVertexOne.y - edgeVertexCommon.y;
            double differenceInX = edgeVertexCommon.x - edgeVertexOne.x;
            double edgeLength = Math.sqrt((differenceInX * differenceInX) + (differenceInY * differenceInY));
            double gamma = Math.toDegrees(Math.atan(differenceInY / differenceInX));
            double lengthToReturnPoint = edgeLength + distanceToUse;
            double newDifferenceInY = lengthToReturnPoint * Math.sin(Math.toRadians(gamma));
            double newDifferenceInX = lengthToReturnPoint * Math.cos(Math.toRadians(gamma));

            commonReturnPoint = new Point(
                    new Double((edgeVertexOne.x + newDifferenceInX)).intValue(),
                    new Double((edgeVertexOne.y - newDifferenceInY)).intValue());
        } else if (edgeVertexOne.x > edgeVertexCommon.x
                && edgeVertexOne.y > edgeVertexCommon.y) {
            double differenceInY = edgeVertexOne.y - edgeVertexCommon.y;
            double differenceInX = edgeVertexOne.x - edgeVertexCommon.x;
            double edgeLength = Math.sqrt((differenceInX * differenceInX) + (differenceInY * differenceInY));
            double gamma = Math.toDegrees(Math.atan(differenceInY / differenceInX));
            double lengthToReturnPoint = edgeLength + distanceToUse;
            double newDifferenceInY = lengthToReturnPoint * Math.sin(Math.toRadians(gamma));
            double newDifferenceInX = lengthToReturnPoint * Math.cos(Math.toRadians(gamma));
            commonReturnPoint = new Point(
                    new Double((edgeVertexOne.x - newDifferenceInX)).intValue(),
                    new Double((edgeVertexOne.y - newDifferenceInY)).intValue());
        } else if (edgeVertexOne.x == edgeVertexCommon.x) {
            if (edgeVertexOne.y < edgeVertexCommon.y) {
                commonReturnPoint = new Point(
                        new Double((edgeVertexCommon.x)).intValue(),
                        new Double((edgeVertexCommon.y + distanceToUse)).intValue());
            } else {
                commonReturnPoint = new Point(
                        new Double((edgeVertexCommon.x)).intValue(),
                        new Double((edgeVertexCommon.y - distanceToUse)).intValue());
            }
        } else if (edgeVertexOne.y == edgeVertexCommon.y) {
            if (edgeVertexOne.x < edgeVertexCommon.x) {
                commonReturnPoint = new Point(new Double(
                        (edgeVertexCommon.x + distanceToUse)).intValue(), new Double((edgeVertexCommon.y)).intValue());
            } else {
                commonReturnPoint = new Point(new Double(
                        (edgeVertexCommon.x - distanceToUse)).intValue(), new Double((edgeVertexCommon.y)).intValue());

            }
        } else {
            String errorMessage = "unable to construct common point with edgeVertex.x=" + edgeVertexOne.getX() + "edgeVertex.y=" +
                    edgeVertexOne.getY() + " and edgeVertexCommon.x=" + edgeVertexCommon.getX() + " edgeVertexCommon.y=" + edgeVertexCommon.getY();
            throw new IllegalArgumentException(errorMessage);
        }
        return commonReturnPoint;
    }

    public static void paintKnot(Graphics2D g2, Paint outlinePaint,
                                 Paint innerPaint, Paint backgroundPaint, int outlineWidth, int innerWidth) {
        celticKnot.paintKnot(g2, outlinePaint, innerPaint, backgroundPaint, outlineWidth, innerWidth);
    }
}

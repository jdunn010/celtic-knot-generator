package structure;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class CelticKnot {
    private final HashSet<KnotComponent> knotComponents;

    public CelticKnot() {
        knotComponents = new HashSet<>();
    }

    public void addKnotComponent(KnotComponent knotComponentToBeAdded) {
        knotComponents.add(knotComponentToBeAdded);
    }

    private HashSet<KnotComponent> getKnotComponentUsingPoint(Point2D point) {
        HashSet<KnotComponent> knotComponentsUsingPoint = new HashSet<>();

        for (KnotComponent temp : knotComponents) {
            if ((temp.getFirstPoint() != null && point.equals(temp.getFirstPoint()))
                    || (temp.getSecondPoint() != null && point.equals(temp.getSecondPoint()))) {
                knotComponentsUsingPoint.add(temp);
            } else if (temp.getClass().getName().equals("structure.Crossing")) {
                Crossing crossingPoint = (Crossing) temp;
                if (crossingPoint.getFirstCrossingPoint().equals(point)
                        || crossingPoint.getSecondCrossingPoint().equals(point)
                        || crossingPoint.getThirdCrossingPoint().equals(point)
                        || crossingPoint.getForthCrossingPoint().equals(point)) {
                    knotComponentsUsingPoint.add(temp);
                }
            }
        }
        return knotComponentsUsingPoint;
    }

    public ArrayList<Line2D.Double> getAllCrossingLines() {
        ArrayList<Line2D.Double> crossingLines = new ArrayList<>();
        Iterator<KnotComponent> i = knotComponents.iterator();
        int crossingCount = 0;
        while (i.hasNext()) {
            KnotComponent temp = i.next();
            if (temp.getClass().getName().equals("structure.Crossing")) {
                Crossing crossingPoint = (Crossing) temp;
                crossingLines.add(crossingPoint.getFirstCrossingLine());
                crossingLines.add(crossingPoint.getSecondCrossingLine());
                crossingCount++;
            }
        }
        return crossingLines;

    }

    public ArrayList<Line2D.Double> getUnconnectedCrossingPointsInLineForm() {
        ArrayList<Line2D.Double> unconnectedCrossingPoints = new ArrayList<>();
        ArrayList<Line2D.Double> crossingLines = getAllCrossingLines();

        for (Line2D.Double crossingLine : crossingLines) {
            HashSet<KnotComponent> knotComponentsUsingFirstPoint =
                    getKnotComponentUsingPoint(crossingLine.getP1());
            HashSet<KnotComponent> knotComponentsUsingSecondPoint =
                    getKnotComponentUsingPoint(crossingLine.getP2());

            if (knotComponentsUsingFirstPoint.size() == 1) {
                unconnectedCrossingPoints.add(crossingLine);
            }
            if (knotComponentsUsingSecondPoint.size() == 1) {
                Line2D.Double reverseCrossingLine = new Line2D.Double(
                        crossingLine.getP2(), crossingLine.getP1());
                unconnectedCrossingPoints.add(reverseCrossingLine);
            }
        }

        return unconnectedCrossingPoints;
    }

    private HashSet<KnotComponent> getUnlinkedComponents() {
        HashSet<KnotComponent> unlinkedComponents = new HashSet<>();

        for (KnotComponent component : knotComponents) {
            if (!component.isFullyConnected()) {
                unlinkedComponents.add(component);
            }
        }
        return unlinkedComponents;
    }

    private HashSet<Crossing> getUntracedCrossings() {
        HashSet<Crossing> untracedCrossings = new HashSet<>();

        for (KnotComponent component : knotComponents) {
            if (component instanceof Crossing && !((Crossing) component).isFullyUsedInTrace()) {
                untracedCrossings.add((Crossing) component);
            }
        }
        return untracedCrossings;
    }

    private Crossing getFirstPartiallyTracedIfAvailable(HashSet<Crossing> untracedCrossings) {
        Iterator<Crossing> i = untracedCrossings.iterator();
        Crossing crossing = i.next();

        while (i.hasNext() &&
                (!crossing.hasFirstLineBeenUsedInTrace() ||
                !crossing.hasSecondLineBeenUsedInTrace())) { //todo why duplicated? hack to check for second line not used
            crossing = i.next();
        }
        return crossing;
    }

    public void calculateOverAndUnderPattern() {
        HashSet<Crossing> untracedCrossings = getUntracedCrossings();
        while (untracedCrossings.size() > 0) {
            boolean firstAndSecondOver = true;

            Crossing currentCrossing = getFirstPartiallyTracedIfAvailable(untracedCrossings);

            if (currentCrossing.isOverAndUnderSet() == false) {
                currentCrossing.setFirstAndSecondOver(firstAndSecondOver);
            } else {
                firstAndSecondOver = currentCrossing.isFirstAndSecondOver();
            }

            Point2D pointToBreakOn;
            Point2D currentConnectingPoint;
            KnotComponent nonCrossingComponent;
            boolean wasFirstLineBeingUsedOnLastCrossing;

            if (currentCrossing.hasFirstLineBeenUsedInTrace() == false) {
                currentConnectingPoint = currentCrossing.getFirstCrossingPoint();
                nonCrossingComponent = currentCrossing.getConnectedToFirstCP();
                pointToBreakOn = currentCrossing.getSecondCrossingPoint();
                wasFirstLineBeingUsedOnLastCrossing = true;
                currentCrossing.setHasFirstLineBeenUsedInTrace(true);
            } else if (currentCrossing.hasSecondLineBeenUsedInTrace() == false){ //todo hack verify second line explicitly not used
                currentConnectingPoint = currentCrossing.getThirdCrossingPoint();
                nonCrossingComponent = currentCrossing.getConnectedToThirdCP();
                pointToBreakOn = currentCrossing.getForthCrossingPoint();
                wasFirstLineBeingUsedOnLastCrossing = false;
                currentCrossing.setHasSecondLineBeenUsedInTrace(true);
            } else {
                throw new IllegalArgumentException();
            }

            while (!nonCrossingComponent.getFirstPoint().equals(pointToBreakOn) &&
                    !nonCrossingComponent.getSecondPoint().equals(pointToBreakOn)) {

                if (currentCrossing.equals(nonCrossingComponent.getFromCrossingPoint())) {
                    currentCrossing = nonCrossingComponent.getToCrossingPoint();
                } else {
                    currentCrossing = nonCrossingComponent.getFromCrossingPoint();
                }

                if (currentConnectingPoint.equals(nonCrossingComponent.getFirstPoint())) {
                    currentConnectingPoint = nonCrossingComponent.getSecondPoint();
                } else {
                    currentConnectingPoint = nonCrossingComponent.getFirstPoint();
                }

                if (((currentConnectingPoint.equals(currentCrossing.getFirstCrossingPoint()) ||
                        currentConnectingPoint.equals(currentCrossing.getSecondCrossingPoint())) &&
                        wasFirstLineBeingUsedOnLastCrossing == true)

                        ||

                        ((currentConnectingPoint.equals(currentCrossing.getThirdCrossingPoint()) || currentConnectingPoint.equals(currentCrossing.getForthCrossingPoint()))
                        && wasFirstLineBeingUsedOnLastCrossing == false)) {
                    firstAndSecondOver = !firstAndSecondOver;
                } else if (((currentConnectingPoint.equals(currentCrossing.getThirdCrossingPoint()) ||
                        currentConnectingPoint.equals(currentCrossing.getForthCrossingPoint())) &&
                        wasFirstLineBeingUsedOnLastCrossing == true)

                        ||

                        ((currentConnectingPoint.equals(currentCrossing.getFirstCrossingPoint()) ||
                        currentConnectingPoint.equals(currentCrossing.getSecondCrossingPoint())) &&
                                wasFirstLineBeingUsedOnLastCrossing == false)) {
                    wasFirstLineBeingUsedOnLastCrossing = !wasFirstLineBeingUsedOnLastCrossing;
                }

                if (currentCrossing.isOverAndUnderSet() == false) {
                    currentCrossing.setFirstAndSecondOver(firstAndSecondOver);
                }

                if (wasFirstLineBeingUsedOnLastCrossing == true) {
                    currentCrossing.setHasFirstLineBeenUsedInTrace(true);
                } else {
                    currentCrossing.setHasSecondLineBeenUsedInTrace(true);
                }

                if (currentConnectingPoint.equals(currentCrossing.getFirstCrossingPoint())) {
                    nonCrossingComponent = currentCrossing.getConnectedToSecondCP();
                    currentConnectingPoint = currentCrossing.getSecondCrossingPoint();
                } else if (currentConnectingPoint.equals(currentCrossing.getSecondCrossingPoint())) {
                    nonCrossingComponent = currentCrossing.getConnectedToFirstCP();
                    currentConnectingPoint = currentCrossing.getFirstCrossingPoint();
                } else if (currentConnectingPoint.equals(currentCrossing.getThirdCrossingPoint())) {
                    nonCrossingComponent = currentCrossing.getConnectedToForthCP();
                    currentConnectingPoint = currentCrossing.getForthCrossingPoint();
                } else if (currentConnectingPoint.equals(currentCrossing.getForthCrossingPoint())) {
                    nonCrossingComponent = currentCrossing.getConnectedToThirdCP();
                    currentConnectingPoint = currentCrossing.getThirdCrossingPoint();
                }
            }
            untracedCrossings = getUntracedCrossings();
        }
    }

    public void createLinksBetweenComponents() {
        HashSet<KnotComponent> unlinkedComponents = getUnlinkedComponents();

        while (unlinkedComponents.size() > 0) {
            Iterator<KnotComponent> i = unlinkedComponents.iterator();
            Crossing startingCrossing = null;
            while (i.hasNext()) {
                KnotComponent temp = i.next();
                if (temp.getClass().getName().equals(
                        "structure.Crossing")) {
                    startingCrossing = (Crossing) temp;
                    break;
                }
            }

            HashSet<KnotComponent> knotComponentsUsingStartingPoint;
            KnotComponent currentComponent = null;
            KnotComponent nextComponent = null;
            Point2D currentConnectingPoint;
            Point2D pointToBreakOn;

            if (startingCrossing.getConnectedToFirstCP() == null) {
                knotComponentsUsingStartingPoint = getKnotComponentUsingPoint(
                        startingCrossing.getFirstCrossingPoint());
                currentConnectingPoint = startingCrossing.getFirstCrossingPoint();
                pointToBreakOn = startingCrossing.getSecondCrossingPoint();
            } else {
                knotComponentsUsingStartingPoint =
                        getKnotComponentUsingPoint(startingCrossing.getThirdCrossingPoint());
                currentConnectingPoint = startingCrossing.getThirdCrossingPoint();
                pointToBreakOn = startingCrossing.getForthCrossingPoint();
            }

            i = knotComponentsUsingStartingPoint.iterator();
            while (i.hasNext()) {
                KnotComponent temp = i.next();
                if (!temp.getClass().getName().equals(
                        "structure.Crossing")) {
                    currentComponent = temp;
                    startingCrossing.connectPointToComponent(
                            currentConnectingPoint, currentComponent);
                    currentComponent.setFromCrossingPoint(startingCrossing);
                    break;
                }
            }
            while (!pointToBreakOn.equals(currentComponent.getSecondPoint())
                    && !pointToBreakOn.equals(currentComponent.getFirstPoint())) {
                if (nextComponent != null) {
                    currentComponent = nextComponent;
                    nextComponent = null;
                }
                HashSet<KnotComponent> knotComponentsUsingSecondPointOfCurrentComponent;

                if (currentComponent.getClass().getName().equals(
                        "structure.Crossing")) {
                    if (((Crossing) currentComponent).getFirstCrossingPoint()
                            .equals(currentConnectingPoint)) {
                        knotComponentsUsingSecondPointOfCurrentComponent =
                                getKnotComponentUsingPoint(((Crossing) currentComponent).getSecondCrossingPoint());
                        currentConnectingPoint = ((Crossing) currentComponent).getSecondCrossingPoint();
                    } else if (((Crossing) currentComponent).getSecondCrossingPoint().equals(
                                    currentConnectingPoint)) {
                        knotComponentsUsingSecondPointOfCurrentComponent =
                                getKnotComponentUsingPoint(((Crossing) currentComponent).getFirstCrossingPoint());
                        currentConnectingPoint = ((Crossing) currentComponent).getFirstCrossingPoint();
                    } else if (((Crossing) currentComponent).getThirdCrossingPoint().equals(
                            currentConnectingPoint)) {
                        knotComponentsUsingSecondPointOfCurrentComponent =
                                getKnotComponentUsingPoint(((Crossing) currentComponent).getForthCrossingPoint());
                        currentConnectingPoint = ((Crossing) currentComponent).getForthCrossingPoint();
                    } else {
                        knotComponentsUsingSecondPointOfCurrentComponent =
                                getKnotComponentUsingPoint(((Crossing) currentComponent).getThirdCrossingPoint());
                        currentConnectingPoint = ((Crossing) currentComponent).getThirdCrossingPoint();
                    }
                } else {
                    if (currentComponent.getFirstPoint().equals(currentConnectingPoint)) {
                        knotComponentsUsingSecondPointOfCurrentComponent = getKnotComponentUsingPoint(
                                currentComponent.getSecondPoint());
                        currentConnectingPoint = currentComponent.getSecondPoint();
                    } else {
                        knotComponentsUsingSecondPointOfCurrentComponent =
                                getKnotComponentUsingPoint(currentComponent.getFirstPoint());
                        currentConnectingPoint = currentComponent.getFirstPoint();
                    }
                }

                i = knotComponentsUsingSecondPointOfCurrentComponent.iterator();
                while (i.hasNext()) {
                    KnotComponent temp = i.next();
                    if (!currentComponent.equals(temp)) {
                        nextComponent = temp;
                        break;
                    }
                }

                if (nextComponent.getClass().getName().equals("structure.Crossing")) {
                    currentComponent.setToCrossingPoint((Crossing) nextComponent);
                    ((Crossing) nextComponent).connectPointToComponent(currentConnectingPoint, currentComponent);
                } else {
                    ((Crossing) currentComponent).connectPointToComponent(currentConnectingPoint, nextComponent);
                    nextComponent.setFromCrossingPoint((Crossing) currentComponent);
                }
            }
            unlinkedComponents = getUnlinkedComponents();
        }
    }

    public void paintKnot(Graphics2D g2,
                          Paint outlinePaint,
                          Paint innerPaint,
                          Paint backgroundPaint,
                          int outlineWidth,
                          int innerWidth) {
        Paint oldPaint = g2.getPaint();
        g2.setPaint(backgroundPaint);
        g2.fillRect(0, 0, 600, 600);
        g2.setPaint(oldPaint);
        Iterator<KnotComponent> i = knotComponents.iterator();
        while (i.hasNext()) {
            KnotComponent knotComponent = i.next();
            if (!knotComponent.getClass().getName().equals(
                    "structure.Crossing")) {
                knotComponent.paintComponent(g2,
                        outlinePaint,
                        innerPaint,
                        outlineWidth,
                        innerWidth);
            }
        }
        i = knotComponents.iterator();
        while (i.hasNext()) {
            KnotComponent knotComponent = i.next();
            if (knotComponent.getClass().getName().equals(
                    "structure.Crossing")) {
                knotComponent.paintComponent(g2,
                        outlinePaint,
                        innerPaint,
                        outlineWidth,
                        innerWidth);
            }
        }
    }
}
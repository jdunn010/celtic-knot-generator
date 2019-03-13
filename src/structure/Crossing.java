package structure;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;


public class Crossing extends KnotComponent {

    private final Point firstCrossingPoint;
    private final Point secondCrossingPoint;
    private final Point thirdCrossingPoint;
    private final Point forthCrossingPoint;

    private KnotComponent connectedToFirstCP;
    private KnotComponent connectedToSecondCP;
    private KnotComponent connectedToThirdCP;
    private KnotComponent connectedToForthCP;

    private boolean isFirstAndSecondOver;
    private boolean isOverAndUnderSet;
    private boolean hasFirstLineBeenUsedInTrace;
    private boolean hasSecondLineBeenUsedInTrace;

    public Crossing(Point firstCrossingPoint, Point secondCrossingPoint, Point thirdCrossingPoint, Point forthCrossingPoint) {
        super(null, null, null, null);
        this.firstCrossingPoint = firstCrossingPoint;
        this.secondCrossingPoint = secondCrossingPoint;
        this.thirdCrossingPoint = thirdCrossingPoint;
        this.forthCrossingPoint = forthCrossingPoint;
        connectedToFirstCP = null;
        connectedToSecondCP = null;
        connectedToThirdCP = null;
        connectedToForthCP = null;
        isFirstAndSecondOver = false;
        isOverAndUnderSet = false;
        hasFirstLineBeenUsedInTrace = false;
        hasSecondLineBeenUsedInTrace = false;
    }

    void connectPointToComponent(Point2D point, KnotComponent component) {
        if (point.equals(firstCrossingPoint)) {
            connectedToFirstCP = component;
        } else if (point.equals(secondCrossingPoint)) {
            connectedToSecondCP = component;
        } else if (point.equals(thirdCrossingPoint)) {
            connectedToThirdCP = component;
        } else if (point.equals(forthCrossingPoint)){
            connectedToForthCP = component;
        } else {
            throw new IllegalArgumentException();
        }
    }

    boolean isFullyUsedInTrace() {
        return hasFirstLineBeenUsedInTrace && hasSecondLineBeenUsedInTrace;
    }

    Line2D.Double getFirstCrossingLine() {
        return new Line2D.Double(firstCrossingPoint, secondCrossingPoint);
    }

    Line2D.Double getSecondCrossingLine() {
        return new Line2D.Double(thirdCrossingPoint, forthCrossingPoint);
    }

    Point getFirstCrossingPoint() {
        return firstCrossingPoint;
    }

    public void setFirstCrossingPoint(Point firstCrossingPoint) {
        throw new IllegalArgumentException();
        //this.firstCrossingPoint = firstCrossingPoint;
    }

    public Point getSecondCrossingPoint() {
        return secondCrossingPoint;
    }

    public void setSecondCrossingPoint(Point secondCrossingPoint) {
        throw new IllegalArgumentException();
        //this.secondCrossingPoint = secondCrossingPoint;
    }

    public Point getThirdCrossingPoint() {
        return thirdCrossingPoint;
    }

    public void setThirdCrossingPoint(Point thirdCrossingPoint) {
        throw new IllegalArgumentException();
        //this.thirdCrossingPoint = thirdCrossingPoint;
    }

    public Point getForthCrossingPoint() {
        return forthCrossingPoint;
    }

    public void setForthCrossingPoint(Point forthCrossingPoint) {
        throw new IllegalArgumentException();
        //this.forthCrossingPoint = forthCrossingPoint;
    }

    public KnotComponent getConnectedToFirstCP() {
        return connectedToFirstCP;
    }

    public void setConnectedToFirstCP(KnotComponent connectedToFirstCP) {
        this.connectedToFirstCP = connectedToFirstCP;
    }

    public KnotComponent getConnectedToSecondCP() {
        return connectedToSecondCP;
    }

    public void setConnectedToSecondCP(KnotComponent connectedToSecondCP) {
        this.connectedToSecondCP = connectedToSecondCP;
    }

    public KnotComponent getConnectedToThirdCP() {
        return connectedToThirdCP;
    }

    public void setConnectedToThirdCP(KnotComponent connectedToThirdCP) {
        this.connectedToThirdCP = connectedToThirdCP;
    }

    public KnotComponent getConnectedToForthCP() {
        return connectedToForthCP;
    }

    public void setConnectedToForthCP(KnotComponent connectedToForthCP) {
        this.connectedToForthCP = connectedToForthCP;
    }

    public boolean isFirstAndSecondOver() {
        return isFirstAndSecondOver;
    }

    public void setFirstAndSecondOver(boolean isFirstAndSecondOver) {
        this.isFirstAndSecondOver = isFirstAndSecondOver;
        setOverAndUnderSet(true);
    }

    boolean hasFirstLineBeenUsedInTrace() {
        return hasFirstLineBeenUsedInTrace;
    }

    void setHasFirstLineBeenUsedInTrace(boolean hasFirstBeenUsedInTrace) {
        this.hasFirstLineBeenUsedInTrace = hasFirstBeenUsedInTrace;
    }

    public boolean hasSecondLineBeenUsedInTrace() {
        return hasSecondLineBeenUsedInTrace;
    }

    void setHasSecondLineBeenUsedInTrace(boolean hasSecondBeenUsedInTrace) {
        this.hasSecondLineBeenUsedInTrace = hasSecondBeenUsedInTrace;
    }

    public void paintComponent(Graphics2D g2, Paint outlinePaint, Paint innerPaint, int outlineWidth, int innerWidth) {
        if (isFirstAndSecondOver == false) {
            g2.setPaint(outlinePaint);
            g2.setStroke(new BasicStroke(outlineWidth, BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_MITER));
            g2.draw(new Line2D.Double(firstCrossingPoint, secondCrossingPoint));

            g2.setPaint(innerPaint);
            g2.setStroke(new BasicStroke(innerWidth, BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_MITER));
            g2.draw(new Line2D.Double(firstCrossingPoint, secondCrossingPoint));

            g2.setPaint(outlinePaint);
            g2.setStroke(new BasicStroke(outlineWidth, BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_MITER));
            g2.draw(new Line2D.Double(thirdCrossingPoint, forthCrossingPoint));

            g2.setPaint(innerPaint);
            g2.setStroke(new BasicStroke(innerWidth, BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_MITER));
            g2.draw(new Line2D.Double(thirdCrossingPoint, forthCrossingPoint));
        } else {
            g2.setPaint(outlinePaint);
            g2.setStroke(new BasicStroke(outlineWidth, BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_MITER));
            g2.draw(new Line2D.Double(thirdCrossingPoint, forthCrossingPoint));

            g2.setPaint(innerPaint);
            g2.setStroke(new BasicStroke(innerWidth, BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_MITER));
            g2.draw(new Line2D.Double(thirdCrossingPoint, forthCrossingPoint));

            g2.setPaint(outlinePaint);
            g2.setStroke(new BasicStroke(outlineWidth, BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_MITER));
            g2.draw(new Line2D.Double(firstCrossingPoint, secondCrossingPoint));

            g2.setPaint(innerPaint);
            g2.setStroke(new BasicStroke(innerWidth, BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_MITER));
            g2.draw(new Line2D.Double(firstCrossingPoint, secondCrossingPoint));
        }
    }

    public boolean isFullyConnected() {
        boolean isFullyConnected = true;
        if (connectedToFirstCP == null ||
                connectedToSecondCP == null ||
                connectedToThirdCP == null ||
                connectedToForthCP == null) {
            isFullyConnected = false;
        }
        return isFullyConnected;
    }

    boolean isOverAndUnderSet() {
        return isOverAndUnderSet;
    }

    void setOverAndUnderSet(boolean isOverAndUnderSet) {
        this.isOverAndUnderSet = isOverAndUnderSet;
    }


    @Override
    public String toString() {
        return "Crossing{" +
                "firstCrossingPoint=" + firstCrossingPoint +
                ", secondCrossingPoint=" + secondCrossingPoint +
                ", thirdCrossingPoint=" + thirdCrossingPoint +
                ", forthCrossingPoint=" + forthCrossingPoint +
                ", connectedToFirstCP=" + connectedToFirstCP +
                ", connectedToSecondCP=" + connectedToSecondCP +
                ", connectedToThirdCP=" + connectedToThirdCP +
                ", connectedToForthCP=" + connectedToForthCP +
                ", isFirstAndSecondOver=" + isFirstAndSecondOver +
                ", isOverAndUnderSet=" + isOverAndUnderSet +
                ", hasFirstLineBeenUsedInTrace=" + hasFirstLineBeenUsedInTrace +
                ", hasSecondLineBeenUsedInTrace=" + hasSecondLineBeenUsedInTrace +
                '}';
    }
}


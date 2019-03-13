package structure;

import java.awt.*;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Point2D;

public class CurvedLine extends KnotComponent {
    private final Point2D firstControlPoint;
    private final Point2D secondControlPoint;


    public CurvedLine(Crossing fromCrossingPoint,
                      Crossing toCrossingPoint,
                      Point firstPoint,
                      Point secondPoint,
                      Point2D firstControlPoint,
                      Point2D secondControlPoint) {
        super(fromCrossingPoint, toCrossingPoint, firstPoint, secondPoint);
        this.firstControlPoint = firstControlPoint;
        this.secondControlPoint = secondControlPoint;
    }

    public void paintComponent(Graphics2D g2, Paint outlinePaint, Paint innerPaint, int outlineWidth, int innerWidth) {
        g2.setPaint(outlinePaint);
        g2.setStroke(new BasicStroke(outlineWidth, BasicStroke.CAP_SQUARE,
                BasicStroke.JOIN_MITER));
        g2.draw(new CubicCurve2D.Double(
                getFirstPoint().getX(),
                getFirstPoint().getY(),
                firstControlPoint.getX(),
                firstControlPoint.getY(),
                secondControlPoint.getX(),
                secondControlPoint.getY(),
                getSecondPoint().getX(),
                getSecondPoint().getY()));

        g2.setPaint(innerPaint);
        g2.setStroke(new BasicStroke(innerWidth, BasicStroke.CAP_SQUARE,
                BasicStroke.JOIN_MITER));
        g2.draw(new CubicCurve2D.Double(
                getFirstPoint().getX(),
                getFirstPoint().getY(),
                firstControlPoint.getX(),
                firstControlPoint.getY(),
                secondControlPoint.getX(),
                secondControlPoint.getY(),
                getSecondPoint().getX(),
                getSecondPoint().getY()));
    }

    public boolean isFullyConnected() {
        boolean isFullyConnected = true;
        if (getFromCrossingPoint() == null || getToCrossingPoint() == null) {
            isFullyConnected = false;
        }
        return isFullyConnected;
    }

}

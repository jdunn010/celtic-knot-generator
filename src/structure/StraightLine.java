package structure;

import java.awt.*;
import java.awt.geom.Line2D;

public class StraightLine extends KnotComponent {

    public StraightLine(Crossing fromCrossingPoint, Crossing toCrossingPoint, Point firstPoint, Point secondPoint) {
        super(fromCrossingPoint, toCrossingPoint, firstPoint, secondPoint);
    }

    public void paintComponent(Graphics2D g2, Paint outlinePaint, Paint innerPaint, int outlineWidth, int innerWidth) {

        g2.setPaint(outlinePaint);
        g2.setStroke(new BasicStroke(
                outlineWidth,
                BasicStroke.CAP_SQUARE,
                BasicStroke.JOIN_MITER)
        );
        g2.draw(new Line2D.Double(getFirstPoint(), getSecondPoint()));

        g2.setPaint(innerPaint);
        g2.setStroke(new BasicStroke(
                innerWidth,
                BasicStroke.CAP_SQUARE,
                BasicStroke.JOIN_MITER));
        g2.draw(new Line2D.Double(getFirstPoint(), getSecondPoint()));
    }

    public boolean isFullyConnected() {
        boolean isFullyConnected = true;
        if (getFromCrossingPoint() == null || getToCrossingPoint() == null) {
            isFullyConnected = false;
        }
        return isFullyConnected;
    }
}

package structure;

import java.awt.*;

public abstract class KnotComponent {
    private Crossing fromCrossingPoint;
    private Crossing toCrossingPoint;
    private Point firstPoint;
    private Point secondPoint;

    KnotComponent(Crossing fromCrossingPoint,
                         Crossing toCrossingPoint,
                         Point firstPoint,
                         Point secondPoint) {
        this.fromCrossingPoint = fromCrossingPoint;
        this.toCrossingPoint = toCrossingPoint;
        this.firstPoint = firstPoint;
        this.secondPoint = secondPoint;
    }


    Crossing getFromCrossingPoint() {
        return fromCrossingPoint;
    }

    void setFromCrossingPoint(Crossing fromCrossingPoint) {
        this.fromCrossingPoint = fromCrossingPoint;
    }

    Crossing getToCrossingPoint() {
        return toCrossingPoint;
    }

    void setToCrossingPoint(Crossing toCrossingPoint) {
        this.toCrossingPoint = toCrossingPoint;
    }

    Point getFirstPoint() {
        return firstPoint;
    }

    public void setFirstPoint(Point firstPoint) {
        this.firstPoint = firstPoint;
    }

    Point getSecondPoint() {
        return secondPoint;
    }

    public void setSecondPoint(Point secondPoint) {
        this.secondPoint = secondPoint;
    }

    public abstract void paintComponent(Graphics2D g2, Paint outlinePaint,
                                        Paint innerPaint, int outlineWidth, int innerWidth);

    public abstract boolean isFullyConnected();
}

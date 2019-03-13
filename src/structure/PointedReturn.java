package structure;


import java.awt.*;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

public class PointedReturn extends KnotComponent {

    private final Point2D firstReturnPoint;
    private final Point2D commonReturnPoint;
    private final Point2D secondReturnPoint;

    private final Point2D firstControlPoint;
    private final Point2D secondControlPoint;
    private final Point2D thirdControlPoint;
    private final Point2D forthControlPoint;

    public PointedReturn(Crossing fromCrossingPoint,
                         Crossing toCrossingPoint,
                         Point firstPoint,
                         Point secondPoint,

                         Point2D firstReturnPoint,
                         Point2D commonReturnPoint,
                         Point2D secondReturnPoint,

                         Point2D firstControlPoint,
                         Point2D secondControlPoint,
                         Point2D thirdControlPoint,
                         Point2D forthControlPoint) {
        super(fromCrossingPoint, toCrossingPoint, firstPoint, secondPoint);
        this.firstReturnPoint = firstReturnPoint;
        this.commonReturnPoint = commonReturnPoint;
        this.secondReturnPoint = secondReturnPoint;
        this.firstControlPoint = firstControlPoint;
        this.secondControlPoint = secondControlPoint;
        this.thirdControlPoint = thirdControlPoint;
        this.forthControlPoint = forthControlPoint;
    }

    public void paintComponent(Graphics2D g2, Paint outlinePaint, Paint innerPaint, int outlineWidth, int innerWidth) {
        g2.setPaint(outlinePaint);
        g2.setStroke(new BasicStroke(outlineWidth, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER));
        int xPointsOfOutlineReturn[] = {
                new Double(firstReturnPoint.getX()).intValue(),
                new Double(commonReturnPoint.getX()).intValue(),
                new Double(secondReturnPoint.getX()).intValue()
        };
        int yPointsOfOutlineReturn[] = {
                new Double(firstReturnPoint.getY()).intValue(),
                new Double(commonReturnPoint.getY()).intValue(),
                new Double(secondReturnPoint.getY()).intValue()};
        GeneralPath polylineOutline = new GeneralPath();
        polylineOutline.moveTo(xPointsOfOutlineReturn[0],
                yPointsOfOutlineReturn[0]);
        for (int index = 1; index < xPointsOfOutlineReturn.length; index++) {
            polylineOutline.lineTo(xPointsOfOutlineReturn[index], yPointsOfOutlineReturn[index]);
        }
        g2.draw(polylineOutline);

        g2.draw(new CubicCurve2D.Double(
                getFirstPoint().getX(),
                getFirstPoint().getY(),
                firstControlPoint.getX(),
                firstControlPoint.getY(),
                secondControlPoint.getX(),
                secondControlPoint.getY(),
                firstReturnPoint.getX(),
                firstReturnPoint.getY()));
        g2.draw(new CubicCurve2D.Double(
                getSecondPoint().getX(),
                getSecondPoint().getY(),
                thirdControlPoint.getX(),
                thirdControlPoint.getY(),
                forthControlPoint.getX(),
                forthControlPoint.getY(),
                secondReturnPoint.getX(),
                secondReturnPoint.getY()));


        g2.setPaint(innerPaint);
        g2.setStroke(new BasicStroke(innerWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));

        int xPointsOfInnerReturn[] = {
                new Double(firstReturnPoint.getX()).intValue(),
                new Double(commonReturnPoint.getX()).intValue(),
                new Double(secondReturnPoint.getX()).intValue()};
        int yPointsOfInnerReturn[] = {
                new Double(firstReturnPoint.getY()).intValue(),
                new Double(commonReturnPoint.getY()).intValue(),
                new Double(secondReturnPoint.getY()).intValue()};

        GeneralPath polylineInner = new GeneralPath();
        polylineInner.moveTo(xPointsOfInnerReturn[0], yPointsOfInnerReturn[0]);
        for (int index = 1; index < xPointsOfInnerReturn.length; index++) {
            polylineInner.lineTo(xPointsOfInnerReturn[index], yPointsOfInnerReturn[index]);
        }

        g2.draw(polylineInner);
        g2.draw(new CubicCurve2D.Double(
                getFirstPoint().getX(),
                getFirstPoint().getY(),
                firstControlPoint.getX(),
                firstControlPoint.getY(),
                secondControlPoint.getX(),
                secondControlPoint.getY(),
                firstReturnPoint.getX(),
                firstReturnPoint.getY()));
        g2.draw(new CubicCurve2D.Double(
                getSecondPoint().getX(),
                getSecondPoint().getY(),
                thirdControlPoint.getX(),
                thirdControlPoint.getY(),
                forthControlPoint.getX(),
                forthControlPoint.getY(),
                secondReturnPoint.getX(),
                secondReturnPoint.getY()));
    }

    public boolean isFullyConnected() {
        boolean isFullyConnected = true;
        if (getFromCrossingPoint() == null || getToCrossingPoint() == null) {
            isFullyConnected = false;
        }
        return isFullyConnected;
    }
}

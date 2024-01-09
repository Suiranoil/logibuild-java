package io.github.lionarius.engine.collision;

import lombok.experimental.UtilityClass;
import org.joml.Vector2f;
import org.joml.Vector3fc;

@UtilityClass
public class Collision2DUtils {
    public static boolean intersect(Polygon2D a, Polygon2D b) {
        var axisA = a.getAxis();
        var axisB = b.getAxis();

        for (var axis : axisA) {
            if (!Collision2DUtils.overlapOnAxis(a, b, axis))
                return false;
        }

        for (var axis : axisB) {
            if (!Collision2DUtils.overlapOnAxis(a, b, axis))
                return false;
        }

        return true;
    }

    private boolean overlapOnAxis(Polygon2D a, Polygon2D b, Vector3fc axis) {
        var intervalA = Collision2DUtils.getIntervalAxis(a.getPoints(), axis);
        var intervalB = Collision2DUtils.getIntervalAxis(b.getPoints(), axis);

        return (intervalB.x <= intervalA.y) && (intervalA.x <= intervalB.y);
    }

    private Vector2f getIntervalAxis(Vector3fc[] points, Vector3fc axis) {
        Vector2f result = new Vector2f();

        result.set(axis.dot(points[0]));
        for (int i = 1; i < points.length; i++) {
            var point = points[i];
            var projection = axis.dot(point);

            if (projection < result.x) {
                result.x = projection;
            }
            if (projection > result.y) {
                result.y = projection;
            }
        }

        return result;
    }
}

package io.github.lionarius.engine.collision;

import org.joml.Math;
import org.joml.*;

public class Polygon2D {
    private final Vector4fc[] points;
    private final Vector3f[] transformedPoints;
    private final Vector3f[] transformedAxis;

    public Polygon2D(Vector2fc[] points) {
        if (points.length < 2)
            throw new IllegalArgumentException("Cannot make a polygon with less than 2 points");

        this.points = new Vector4f[points.length];
        this.transformedPoints = new Vector3f[points.length];
        this.transformedAxis = new Vector3f[points.length];

        for (int i = 0; i < points.length; i++) {
            var point = points[i];
            this.points[i] = new Vector4f(point, 0, 1);
            this.transformedPoints[i] = new Vector3f(point, 0);
            this.transformedAxis[i] = new Vector3f();
        }

        this.calculateAllAxis();
    }

    public Vector3fc[] getPoints() {
        return this.transformedPoints;
    }

    public Vector3fc[] getAxis() {
        return this.transformedAxis;
    }

    public void applyTransform(Matrix4fc matrix) {
        for (int i = 0; i < this.points.length; i++) {
            this.points[i].mulProject(matrix, this.transformedPoints[i]).mul(1, 1, 0);
        }

        this.calculateAllAxis();
    }

    private void calculateAllAxis() {
        for (int i = 0; i < this.transformedPoints.length - 1; i++) {
            this.calculateAxis(this.transformedPoints[i], this.transformedPoints[i + 1], this.transformedAxis[i]);
        }

        this.calculateAxis(this.transformedPoints[this.transformedPoints.length - 1], this.transformedPoints[0], this.transformedAxis[this.transformedAxis.length - 1]);
    }

    private void calculateAxis(Vector3fc p1, Vector3fc p2, Vector3f dest) {
        p2.sub(p1, dest).rotateZ(Math.toRadians(90)).normalize();
    }
}

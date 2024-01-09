package io.github.lionarius.engine.scene.builtin.collision;

import io.github.lionarius.Logibuild;
import io.github.lionarius.engine.collision.Polygon2D;
import io.github.lionarius.engine.renderer.line.LineRenderer;
import io.github.lionarius.engine.scene.builtin.Transform;
import lombok.Getter;
import org.joml.Matrix4f;
import org.joml.Vector3fc;

public abstract class Polygon2DCollider extends Collider {
    private final transient LineRenderer lineRenderer = Logibuild.getInstance().getEngineRenderer().getLineRenderer();

    private Transform transform;
    @Getter
    private transient Polygon2D polygon2D;

    protected Polygon2DCollider() {
        this.polygon2D = this.makeBasePolygon();
    }

    @Override
    public final void onAwake() {
        this.transform = this.getGameObject().getTransform();
    }

    @Override
    public final void onUpdate(double delta) {
        this.updatePolygon(this.transform.getTransformMatrix());
    }

    @Override
    public final void onEditorUpdate(double delta) {
        var transform = this.getGameObject().getTransform();
        this.updatePolygon(transform.getTransformMatrix());
    }

    @Override
    public void onCollide(Collider other) {
        this.debugColor.set(1, 0, 0, 1);
    }

    @Override
    public final void onRender(double delta) {
        if (Logibuild.getInstance().isDebugDraw() && this.polygon2D != null) {
            Vector3fc[] points = this.polygon2D.getPoints();
            for (int i = 0; i < points.length - 1; i++) {
                this.lineRenderer.renderLine(points[i], points[i + 1], this.debugColor);
            }
            this.lineRenderer.renderLine(points[points.length - 1], points[0], this.debugColor);

            this.debugColor.set(0, 1, 0, 1);
        }
    }

    protected boolean isDirty() {
        return false;
    }

    protected abstract void applySettings(Matrix4f matrix);

    protected abstract Polygon2D makeBasePolygon();

    private void updatePolygon(Matrix4f matrix) {
        if (this.isDirty()) {
            this.polygon2D = this.makeBasePolygon();
        }

        if (this.polygon2D == null)
            return;

        this.applySettings(matrix);
        this.polygon2D.applyTransform(matrix);
    }
}

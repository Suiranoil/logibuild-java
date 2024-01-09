package io.github.lionarius.engine.scene.builtin.collision;

import io.github.lionarius.engine.collision.Polygon2D;
import io.github.lionarius.engine.editor.property.Min;
import io.github.lionarius.engine.editor.property.SerializeField;
import lombok.Setter;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector2f;

public class Circle2DCollider extends Polygon2DCollider {
    @SerializeField @Min(0) @Setter
    private float width = 0;
    @SerializeField @Min(0) @Setter
    private float height = 0;
    @SerializeField @Min(3) @Setter
    private int precision = 6;
    private int prevPrecision = 0;

    @Override
    protected boolean isDirty() {
        var result = this.prevPrecision != this.precision;
        this.prevPrecision = this.precision;

        return result;
    }

    @Override
    protected void applySettings(Matrix4f matrix) {
        matrix.scale(this.width, this.height, 1);
    }

    @Override
    protected Polygon2D makeBasePolygon() {
        if (this.precision < 2)
            return null;
        var points = new Vector2f[this.precision];

        float delta = (float) (2 * Math.PI / this.precision);
        for (int i = 0; i < this.precision; i++) {
            var x = Math.sin(delta * i) / 2;
            var y = Math.cos(delta * i) / 2;
            points[i] = new Vector2f(x, y);
        }

        return new Polygon2D(points);
    }
}

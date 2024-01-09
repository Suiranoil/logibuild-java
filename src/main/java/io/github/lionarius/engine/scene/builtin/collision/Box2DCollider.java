package io.github.lionarius.engine.scene.builtin.collision;

import io.github.lionarius.engine.collision.Polygon2D;
import io.github.lionarius.engine.editor.property.SerializeField;
import lombok.Setter;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector2fc;

public class Box2DCollider extends Polygon2DCollider {
    private static final Vector2fc[] BOX_POINTS = new Vector2fc[]{
            new Vector2f(-0.5f, -0.5f),
            new Vector2f(0.5f, -0.5f),
            new Vector2f(0.5f, 0.5f),
            new Vector2f(-0.5f, 0.5f)
    };

    @SerializeField @Setter
    private float width = 0;
    @SerializeField @Setter
    private float height = 0;

    @Override
    protected void applySettings(Matrix4f matrix) {
        matrix.scale(this.width, this.height, 1);
    }

    @Override
    protected Polygon2D makeBasePolygon() {
        return new Polygon2D(Box2DCollider.BOX_POINTS);
    }
}

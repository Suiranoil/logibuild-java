package io.github.lionarius.engine.resource.impl.font;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joml.Vector2f;
import org.joml.Vector2fc;

@Getter
@RequiredArgsConstructor
public class Glyph {
    private final int unicode;
    private final float advance;
    private final Bounds planeBounds;
    private final Bounds atlasBounds;

    private Vector2fc[] positions;
    private Vector2fc[] uvs;

    // 0 - left top vertex
    // 1 - right top vertex
    // 2 - right bottom vertex
    // 3 - left bottom vertex
    protected void init(Font.Metrics metrics, int width, int height) {
        if (this.atlasBounds == null)
            return;

        var size = new Vector2f(width, height);

        this.uvs = new Vector2fc[4];
        this.uvs[0] = new Vector2f(this.atlasBounds.left, this.atlasBounds.top).div(size);
        this.uvs[1] = new Vector2f(this.atlasBounds.left, this.atlasBounds.bottom).div(size);
        this.uvs[2] = new Vector2f(this.atlasBounds.right, this.atlasBounds.bottom).div(size);
        this.uvs[3] = new Vector2f(this.atlasBounds.right, this.atlasBounds.top).div(size);

        this.positions = new Vector2fc[4];
        this.positions[0] = new Vector2f(this.planeBounds.left, metrics.ascender() - this.planeBounds.top);
        this.positions[1] = new Vector2f(this.planeBounds.left, metrics.ascender() - this.planeBounds.bottom);
        this.positions[2] = new Vector2f(this.planeBounds.right, metrics.ascender() - this.planeBounds.bottom);
        this.positions[3] = new Vector2f(this.planeBounds.right, metrics.ascender() - this.planeBounds.top);
    }

    public record Bounds(float left, float bottom, float right, float top) {
    }
}

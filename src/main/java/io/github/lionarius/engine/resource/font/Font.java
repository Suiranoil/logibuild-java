package io.github.lionarius.engine.resource.font;

import io.github.lionarius.engine.resource.Resource;
import io.github.lionarius.engine.resource.texture.Texture;
import lombok.*;
import org.joml.Vector2f;
import org.joml.Vector2fc;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class Font implements Resource {
    @Getter
    @NonNull
    private final Atlas atlas;
    @Getter
    @NonNull
    private final Metrics metrics;
    @Getter
    private final Map<Integer, Glyph> glyphs = new HashMap<>();
    @Getter(lazy = true)
    private final Vector2fc unitRange = this.calculateUnitRange();
    @Getter
    @Setter(AccessLevel.PROTECTED)
    private Texture atlasTexture;

    public Glyph getGlyph(int codePoint) {
        return this.glyphs.get(codePoint);
    }

    @Override
    public void close() {
    }

    protected void init() {
        for (var glyph : this.glyphs.values())
            glyph.init(this.atlasTexture.getWidth(), this.atlasTexture.getHeight());
    }

    private Vector2fc calculateUnitRange() {
        assert this.atlas != null;
        assert this.atlasTexture != null;

        return new Vector2f(this.atlas.distanceRange).div(this.atlasTexture.getWidth(), this.atlasTexture.getHeight());
    }

    public record Atlas(float distanceRange, int size) {
    }

    public record Metrics(float emSize, float lineHeight, float ascender, float descender, float underlineY,
                          float underlineThickness) {
    }
}

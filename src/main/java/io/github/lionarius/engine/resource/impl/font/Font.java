package io.github.lionarius.engine.resource.impl.font;

import io.github.lionarius.engine.resource.Resource;
import io.github.lionarius.engine.resource.impl.texture.Texture;
import lombok.*;
import org.javatuples.Pair;
import org.joml.Vector2f;
import org.joml.Vector2fc;

import java.util.HashMap;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public class Font implements Resource {
    @Getter @Setter
    private String resourceName;

    @NonNull
    private final Atlas atlas;
    @NonNull
    private final Metrics metrics;
    private final Map<Integer, Glyph> glyphs = new HashMap<>();
    private final Map<Pair<Integer, Integer>, Kerning> kerning = new HashMap<>();
    @Getter(lazy = true)
    private final Vector2fc unitRange = this.calculateUnitRange();
    @Setter(AccessLevel.PROTECTED)
    private Texture atlasTexture;

    public Glyph getGlyph(int codePoint) {
        return this.glyphs.get(codePoint);
    }

    protected void init() {
        for (var glyph : this.glyphs.values())
            glyph.init(this.metrics, this.atlasTexture.getWidth(), this.atlasTexture.getHeight());
    }

    @Override
    public void close() {
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

    public record Kerning(int unicode1, int unicode2, float advance) {
    }
}

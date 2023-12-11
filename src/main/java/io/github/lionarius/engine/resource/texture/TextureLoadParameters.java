package io.github.lionarius.engine.resource.texture;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.lwjgl.opengl.GL46;

@Getter
@AllArgsConstructor
public final class TextureLoadParameters {
    public static final TextureLoadParameters DEFAULT = new TextureLoadParameters(GL46.GL_REPEAT, GL46.GL_NEAREST);

    private final int wrap;
    private final int filter;
}

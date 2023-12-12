package io.github.lionarius.engine.resource.texture;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.lwjgl.opengl.GL46;

@Getter
@AllArgsConstructor
public final class TextureCreateParameters {
    public static final TextureCreateParameters DEFAULT = new TextureCreateParameters(GL46.GL_REPEAT, GL46.GL_NEAREST);

    private final int wrap;
    private final int filter;
}

package io.github.lionarius.engine.resource.texture;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.lwjgl.opengl.GL46;

@Getter
@AllArgsConstructor
public final class TextureCreateParameters {
    public static final TextureCreateParameters PIXELATED = new TextureCreateParameters(GL46.GL_REPEAT, GL46.GL_NEAREST, GL46.GL_NEAREST);
    public static final TextureCreateParameters SMOOTH = new TextureCreateParameters(GL46.GL_REPEAT, GL46.GL_LINEAR, GL46.GL_LINEAR);

    private final int wrap;
    private final int minFilter;
    private final int magFilter;
}

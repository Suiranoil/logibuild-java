package io.github.lionarius.engine.resource.texture;

import org.lwjgl.opengl.GL46;

public record TextureLoadParameters(int wrap, int filter) {
    public static final TextureLoadParameters DEFAULT_PARAMETERS = new TextureLoadParameters(GL46.GL_REPEAT, GL46.GL_NEAREST);
}

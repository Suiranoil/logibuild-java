package io.github.lionarius.engine.renderer.texture;

import io.github.lionarius.engine.renderer.OpenGLObject;
import io.github.lionarius.engine.resource.Resource;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.lwjgl.opengl.GL46;

import java.nio.ByteBuffer;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class Texture extends OpenGLObject implements Resource {
    private final int width;
    private final int height;
    private final int channels;

    protected void init(@NonNull ByteBuffer data) {
        this.id = GL46.glCreateTextures(GL46.GL_TEXTURE_2D);

        this.bind();
        GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_WRAP_S, GL46.GL_REPEAT);
        GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_WRAP_T, GL46.GL_REPEAT);
        GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_MIN_FILTER, GL46.GL_NEAREST);
        GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_MAG_FILTER, GL46.GL_NEAREST);

        var format = switch (this.channels) {
            case 1 -> GL46.GL_R;
            case 2 -> GL46.GL_RG;
            case 3 -> GL46.GL_RGB;
            case 4 -> GL46.GL_RGBA;
            default -> throw new IllegalStateException("Unknown number of texture channels " + this.channels);
        };

        GL46.glTexImage2D(GL46.GL_TEXTURE_2D, 0, format, this.width, this.height, 0, format, GL46.GL_UNSIGNED_BYTE, data);
    }

    @Override
    public void bind() {
        GL46.glBindTexture(GL46.GL_TEXTURE_2D, this.id);
    }

    @Override
    public void unbind() {
        GL46.glBindTexture(GL46.GL_TEXTURE_2D, 0);
    }

    @Override
    public void close() {
        GL46.glDeleteTextures(this.id);
    }
}

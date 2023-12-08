package io.github.lionarius.engine.resource.texture;

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

    protected void init(@NonNull TextureLoadParameters parameters, @NonNull ByteBuffer data) {
        this.id = GL46.glCreateTextures(GL46.GL_TEXTURE_2D);

        GL46.glTextureParameteri(this.id, GL46.GL_TEXTURE_WRAP_S, parameters.wrap());
        GL46.glTextureParameteri(this.id, GL46.GL_TEXTURE_WRAP_T, parameters.wrap());
        GL46.glTextureParameteri(this.id, GL46.GL_TEXTURE_MIN_FILTER, parameters.filter());
        GL46.glTextureParameteri(this.id, GL46.GL_TEXTURE_MAG_FILTER, parameters.filter());

        var format = switch (this.channels) {
            case 1 -> GL46.GL_R;
            case 2 -> GL46.GL_RG;
            case 3 -> GL46.GL_RGB;
            case 4 -> GL46.GL_RGBA;
            default -> throw new IllegalStateException("Unknown number of texture channels " + this.channels);
        };

        GL46.glTextureStorage2D(this.id, 0, format, this.width, this.height);
        GL46.glTextureSubImage2D(this.id, 0, 0, 0, this.width, this.height, format, GL46.GL_UNSIGNED_BYTE, data);
    }

    public void bindUnit(int slot) {
        GL46.glBindTextureUnit(slot, this.id);
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

package io.github.lionarius.engine.resource.texture;

import io.github.lionarius.engine.renderer.OpenGLObject;
import io.github.lionarius.engine.resource.Resource;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.lwjgl.opengl.GL46;

import java.nio.ByteBuffer;

@Getter
@RequiredArgsConstructor
public class Texture extends OpenGLObject implements Resource {
    private final int width;
    private final int height;

    private boolean initialized = false;

    public void init(@NonNull TextureLoadParameters parameters, int internalFormat) {
        assert !this.initialized : "cannot initialize already initialized texture";

        this.id = GL46.glCreateTextures(GL46.GL_TEXTURE_2D);

        GL46.glTextureParameteri(this.id, GL46.GL_TEXTURE_WRAP_S, parameters.getWrap());
        GL46.glTextureParameteri(this.id, GL46.GL_TEXTURE_WRAP_T, parameters.getWrap());
        GL46.glTextureParameteri(this.id, GL46.GL_TEXTURE_MIN_FILTER, parameters.getFilter());
        GL46.glTextureParameteri(this.id, GL46.GL_TEXTURE_MAG_FILTER, parameters.getFilter());

        GL46.glTextureStorage2D(this.id, 1, internalFormat, this.width, this.height);

        this.initialized = true;
    }

    public void uploadPixels(int format, @NonNull ByteBuffer pixels) {
        this.uploadPixels(this.width, this.height, format, pixels);
    }

    public void uploadPixels(int width, int height, int format, @NonNull ByteBuffer pixels) {
        GL46.glTextureSubImage2D(this.id, 0, 0, 0, width, height, format, GL46.GL_UNSIGNED_BYTE, pixels);
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

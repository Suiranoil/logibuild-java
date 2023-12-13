package io.github.lionarius.engine.resource.texture;

import io.github.lionarius.engine.renderer.OpenGLObject;
import io.github.lionarius.engine.resource.Resource;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.lwjgl.opengl.GL46;

import java.nio.ByteBuffer;

@Getter
public class Texture extends OpenGLObject implements Resource {
    @Getter @Setter
    private String resourceName;

    private int width;
    private int height;

    private boolean initialized = false;
    private boolean dynamic = false;
    private int format;
//    private TextureLoadParameters parameters;

    public Texture(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void init(@NonNull TextureCreateParameters parameters, int format) {
        this.init(parameters, format, false);
    }
    public void init(@NonNull TextureCreateParameters parameters, int format, boolean dynamic) {
        assert !this.initialized : "cannot initialize already initialized texture";

        this.id = GL46.glCreateTextures(GL46.GL_TEXTURE_2D);

        GL46.glTextureParameteri(this.id, GL46.GL_TEXTURE_WRAP_S, parameters.getWrap());
        GL46.glTextureParameteri(this.id, GL46.GL_TEXTURE_WRAP_T, parameters.getWrap());
        GL46.glTextureParameteri(this.id, GL46.GL_TEXTURE_MIN_FILTER, parameters.getFilter());
        GL46.glTextureParameteri(this.id, GL46.GL_TEXTURE_MAG_FILTER, parameters.getFilter());

        if (dynamic) {
            this.bind();
            GL46.glTexImage2D(GL46.GL_TEXTURE_2D, 0, format, this.width, this.height, 0, format, GL46.GL_UNSIGNED_BYTE, 0);
        } else
            GL46.glTextureStorage2D(this.id, 1, format, this.width, this.height);

        this.initialized = true;
        this.dynamic = dynamic;
        this.format = format;
//        this.parameters = parameters;
    }

    public void uploadPixels(int format, @NonNull ByteBuffer pixels) {
        this.uploadPixels(this.width, this.height, format, pixels);
    }

    public void uploadPixels(int width, int height, int format, @NonNull ByteBuffer pixels) {
        GL46.glTextureSubImage2D(this.id, 0, 0, 0, width, height, format, GL46.GL_UNSIGNED_BYTE, pixels);
    }

    public void resize(int width, int height) {
        if (!this.dynamic)
            throw new IllegalStateException("Cannot resize non dynamic texture");

        this.width = width;
        this.height = height;

        this.bind();
        GL46.glTexImage2D(GL46.GL_TEXTURE_2D, 0, this.format, this.width, this.height, 0, this.format, GL46.GL_UNSIGNED_BYTE, 0);
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

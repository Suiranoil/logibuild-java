package io.github.lionarius.engine.renderer.framebuffer;

import io.github.lionarius.engine.renderer.OpenGLObject;
import lombok.Getter;
import org.lwjgl.opengl.GL46;

@Getter
public class Renderbuffer extends OpenGLObject {
    private int width;
    private int height;
    private int format;

    public Renderbuffer(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void init(int format) {
        this.id = GL46.glCreateRenderbuffers();
        this.format = format;

        GL46.glNamedRenderbufferStorage(this.id, this.format, this.width, this.height);
    }

    public void resize(int width, int height) {
        this.width = width;
        this.height = height;

        GL46.glNamedRenderbufferStorage(this.id, this.format, this.width, this.height);
    }

    @Override
    public void bind() {
        GL46.glBindRenderbuffer(GL46.GL_RENDERBUFFER, this.id);
    }

    @Override
    public void unbind() {
        GL46.glBindRenderbuffer(GL46.GL_RENDERBUFFER, 0);
    }

    @Override
    public void close() {
        GL46.glDeleteRenderbuffers(this.id);
    }
}

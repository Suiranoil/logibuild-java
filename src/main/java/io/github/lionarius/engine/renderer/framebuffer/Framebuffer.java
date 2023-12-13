package io.github.lionarius.engine.renderer.framebuffer;

import io.github.lionarius.engine.renderer.OpenGLObject;
import io.github.lionarius.engine.resource.texture.Texture;
import io.github.lionarius.engine.resource.texture.TextureCreateParameters;
import lombok.Getter;
import org.lwjgl.opengl.GL46;

public class Framebuffer extends OpenGLObject {
    @Getter
    private int width;
    @Getter
    private int height;

    @Getter
    private Texture texture;
    private Renderbuffer renderbuffer;

    public Framebuffer(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void init() {
        this.id = GL46.glCreateFramebuffers();

        this.texture = new Texture(this.width, this.height);
        this.texture.init(new TextureCreateParameters(GL46.GL_CLAMP, GL46.GL_LINEAR), GL46.GL_RGB, true);
        GL46.glNamedFramebufferTexture(this.id, GL46.GL_COLOR_ATTACHMENT0, this.texture.getId(), 0);

        this.renderbuffer = new Renderbuffer(this.width, this.height);
        this.renderbuffer.init(GL46.GL_DEPTH24_STENCIL8);
        GL46.glNamedFramebufferRenderbuffer(this.id, GL46.GL_DEPTH_STENCIL_ATTACHMENT, GL46.GL_RENDERBUFFER, this.renderbuffer.getId());

        if (GL46.glCheckNamedFramebufferStatus(this.id, GL46.GL_FRAMEBUFFER) != GL46.GL_FRAMEBUFFER_COMPLETE)
            throw new IllegalStateException("Could not create framebuffer");
    }

    public void resize(int width, int height) {
        if (this.width == width && this.height == height)
            return;

        this.width = width;
        this.height = height;

        this.texture.resize(this.width, this.height);
        this.renderbuffer.resize(this.width, this.height);
    }

    @Override
    public void bind() {
        GL46.glBindFramebuffer(GL46.GL_FRAMEBUFFER, this.id);
    }

    @Override
    public void unbind() {
        GL46.glBindFramebuffer(GL46.GL_FRAMEBUFFER, 0);
    }

    @Override
    public void close() {
        GL46.glDeleteFramebuffers(this.id);
    }
}

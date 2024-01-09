package io.github.lionarius.engine.renderer;

import io.github.lionarius.engine.util.Closeable;

public abstract class Renderer implements Closeable {
    protected RenderCamera camera;

    public abstract void init();

    public void beginFrame(RenderCamera camera) {
        this.camera = camera;
    }

    public abstract void endFrame();
}

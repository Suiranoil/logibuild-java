package io.github.lionarius.engine.renderer;

import io.github.lionarius.engine.renderer.batch.quad.QuadBatch;
import io.github.lionarius.engine.resource.ResourceManager;
import io.github.lionarius.engine.util.Closeable;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL46;

@RequiredArgsConstructor
public class Renderer implements Closeable {
    @NonNull
    private final ResourceManager resourceManager;
    private QuadBatch quadBatch;


    public void init() {
        this.quadBatch = new QuadBatch(8192 * 4, this.resourceManager);
        this.quadBatch.init();
    }

    public void beginFrame() {
        this.quadBatch.beginFrame();
    }

    public void renderQuad(float x, float y, float z, float width, float height, Vector4f color) {
        this.quadBatch.renderQuad(x, y, z, 0, new Vector3f(), width, height, color);
    }

    public void endFrame(Matrix4fc projection, Matrix4fc view) {
        GL46.glClear(GL46.GL_COLOR_BUFFER_BIT);

        this.quadBatch.endFrame(projection, view);
    }

    @Override
    public void close() {
        this.quadBatch.close();
    }
}

package io.github.lionarius.engine.renderer.buffer;

import io.github.lionarius.engine.renderer.OpenGLObject;
import org.lwjgl.opengl.GL45;
import org.lwjgl.opengl.GL46;

public class VertexArray extends OpenGLObject {
    private int enabledAttributes = 0;

    public VertexArray() {
        this.id = GL46.glCreateVertexArrays();
    }

    @Override
    public void bind() {
        GL46.glBindVertexArray(this.id);
    }

    @Override
    public void unbind() {
        GL46.glBindVertexArray(0);
    }

    @Override
    public void close() {
        GL46.glDeleteVertexArrays(this.id);
    }

    public void setIndexBuffer(IndexBuffer buffer) {
        GL45.glVertexArrayElementBuffer(this.id, buffer.getId());
    }

    public void setVertexBuffer(VertexBuffer buffer, VertexBufferLayout layout) {
        this.setVertexBuffer(0, -1, buffer, layout);
    }

    public void setVertexBuffer(int bindingIndex, int divisor, VertexBuffer buffer, VertexBufferLayout layout) {
        GL46.glVertexArrayVertexBuffer(this.id, bindingIndex, buffer.getId(), 0, layout.getStride());

        var elements = layout.getElements();
        var offset = 0;
        for (var element : elements) {
            GL46.glEnableVertexArrayAttrib(this.id, this.enabledAttributes);
            GL46.glVertexArrayAttribFormat(this.id, this.enabledAttributes, element.getCount(), element.getType(), element.isNormalized(), offset);
            GL46.glVertexArrayAttribBinding(this.id, this.enabledAttributes, bindingIndex);
            this.enabledAttributes += 1;

            offset += element.getCount() * element.getSize();
        }

        if (divisor >= 0)
            GL46.glVertexArrayBindingDivisor(this.id, bindingIndex, divisor);
    }
}

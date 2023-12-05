package io.github.lionarius.engine.renderer.buffer;

import io.github.lionarius.engine.renderer.OpenGLObject;
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

    public void setBuffer(VertexBuffer buffer, VertexBufferLayout layout) {
        this.setBuffer(0, 0, buffer, layout);
    }

    public void setBuffer(int bindingIndex, int divisor, VertexBuffer buffer, VertexBufferLayout layout) {
        this.bind();
        GL46.glBindVertexBuffer(bindingIndex, buffer.getId(), 0, layout.getStride());

        var elements = layout.getElements();
        var offset = 0;
        for (var element : elements) {
            GL46.glEnableVertexAttribArray(this.enabledAttributes);
            GL46.glVertexAttribFormat(this.enabledAttributes, element.getCount(), element.getType(), element.isNormalized(), offset);
            GL46.glVertexAttribBinding(this.enabledAttributes, bindingIndex);
            this.enabledAttributes += 1;

            offset += element.getCount() * element.getSize();
        }

        GL46.glVertexBindingDivisor(bindingIndex, divisor);
    }
}

package io.github.lionarius.engine.renderer.buffer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL46;

import java.util.ArrayList;
import java.util.List;

@Getter
public class VertexBufferLayout {
    private final List<LayoutElement> elements = new ArrayList<>();

    private int stride = 0;

    public <T> void push(Class<T> clazz, int count) {
        this.push(clazz, count, false);
    }

    public <T> void push(Class<T> clazz, int count, boolean normalized) {
        if (clazz == Boolean.class)
            this.push(new LayoutElement(GL46.GL_BOOL, count, 1, normalized));
        else if (clazz == Byte.class)
            this.push(new LayoutElement(GL46.GL_UNSIGNED_BYTE, count, Byte.BYTES, normalized));
        else if (clazz == Integer.class)
            this.push(new LayoutElement(GL46.GL_UNSIGNED_INT, count, Integer.BYTES, normalized));
        else if (clazz == Float.class)
            this.push(new LayoutElement(GL46.GL_FLOAT, count, Float.BYTES, normalized));
        else if (clazz == Matrix4f.class) {
            this.push(Float.class, 4 * count);
            this.push(Float.class, 4 * count);
            this.push(Float.class, 4 * count);
            this.push(Float.class, 4 * count);
        } else
            throw new IllegalArgumentException("Type is not supported");
    }

    private void push(LayoutElement layoutElement) {
        this.elements.add(layoutElement);
        this.stride += layoutElement.count * layoutElement.size;
    }

    @Getter
    @RequiredArgsConstructor
    protected static class LayoutElement {
        private final int type;
        private final int count;
        private final int size;
        private final boolean normalized;
    }
}

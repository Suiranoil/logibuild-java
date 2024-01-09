package io.github.lionarius.engine.util.buffer;

import lombok.experimental.UtilityClass;
import org.joml.Vector2fc;
import org.joml.Vector3fc;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryUtil;

import java.nio.Buffer;
import java.nio.ByteBuffer;

@UtilityClass
public final class BufferUtil {
    public static void movePosition(Buffer buffer, int offset) {
        var position = buffer.position();
        buffer.position(position + offset);
    }

    public static <T extends GetToByteBuffer> ByteBuffer objectArrayToBuffer(T[] array, int elementSize) {
        var buffer = BufferUtils.createByteBuffer(array.length * elementSize);
        for (T element : array) {
            element.get(buffer);
            BufferUtil.movePosition(buffer, elementSize);
        }

        return buffer;
    }

    public static <T extends GetToByteBuffer> void objectArrayToBuffer(T[] array, ByteBuffer buffer) {
        for (T element : array) {
            element.get(buffer);
        }
    }

    public static <T extends Vector2fc> ByteBuffer objectArrayToBuffer(T[] array) {
        var buffer = BufferUtils.createByteBuffer(2 * array.length * Float.BYTES);
        var position = 0;
        for (T element : array) {
            element.get(buffer);
            position += 2 * Float.BYTES;
            buffer.position(position);
        }

        return buffer;
    }

    public static <T extends Vector3fc> ByteBuffer objectArrayToBuffer(T[] array) {
        var buffer = BufferUtils.createByteBuffer(3 * array.length * Float.BYTES);
        var position = 0;
        for (T element : array) {
            element.get(buffer);
            position += 3 * Float.BYTES;
            buffer.position(position);
        }

        return buffer;
    }

    public static PointerBuffer stringArrayToPointerBuffer(String[] strings) {
        if (strings == null)
            return null;

        var buffer = BufferUtils.createPointerBuffer(strings.length);
        for (var string : strings) {
            var bytes = string.getBytes();
            var stringBuffer = BufferUtils.createByteBuffer(bytes.length + 1);
            stringBuffer.put(bytes);
            stringBuffer.put((byte) 0);
            stringBuffer.flip();
            buffer.put(MemoryUtil.memAddress(stringBuffer));
        }
        buffer.flip();
        return buffer;
    }
}

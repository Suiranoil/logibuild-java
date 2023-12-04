package io.github.lionarius.engine.util;

import lombok.experimental.UtilityClass;
import org.joml.Vector2fc;
import org.joml.Vector3fc;
import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;

@UtilityClass
public final class BufferUtil {
	public static <T extends GetToByteBuffer> ByteBuffer objectArrayToBuffer(T[] array, int elementSize) {
		var buffer = BufferUtils.createByteBuffer(array.length * elementSize);
		var position = 0;
		for (T element : array) {
			element.get(buffer);
			position += elementSize;
			buffer.position(position);
		}

		return buffer;
	}

	public static <T extends Vector2fc> ByteBuffer vectorArrayToBuffer(T[] array) {
		var buffer = BufferUtils.createByteBuffer(2 * array.length * Float.BYTES);
		var position = 0;
		for (T element : array) {
			element.get(buffer);
			position += 2 * Float.BYTES;
			buffer.position(position);
		}

		return buffer;
	}

	public static <T extends Vector3fc> ByteBuffer vectorArrayToBuffer(T[] array) {
		var buffer = BufferUtils.createByteBuffer(3 * array.length * Float.BYTES);
		var position = 0;
		for (T element : array) {
			element.get(buffer);
			position += 3 * Float.BYTES;
			buffer.position(position);
		}

		return buffer;
	}
}

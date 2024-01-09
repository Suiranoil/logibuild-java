package io.github.lionarius.engine.util.io;

import lombok.experimental.UtilityClass;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

@UtilityClass
public class StreamUtil {
    private static final int INITIAL_BUFFER_SIZE = 128;

    public static ByteBuffer readStreamToBuffer(InputStream stream) throws IOException {
        ByteBuffer buffer;

        ReadableByteChannel rbc = Channels.newChannel(stream);
        buffer = BufferUtils.createByteBuffer(StreamUtil.INITIAL_BUFFER_SIZE);

        while (true) {
            int bytes = rbc.read(buffer);
            if (bytes == -1) {
                break;
            }
            if (buffer.remaining() == 0) {
                buffer = resizeBuffer(buffer, buffer.capacity() * 2);
            }
        }


        buffer.flip();
        return buffer;
    }

    private static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
        ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
        buffer.flip();
        newBuffer.put(buffer);
        return newBuffer;
    }
}

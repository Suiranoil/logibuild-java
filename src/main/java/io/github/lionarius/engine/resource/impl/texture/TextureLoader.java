package io.github.lionarius.engine.resource.impl.texture;

import io.github.lionarius.engine.resource.ResourceLoader;
import io.github.lionarius.engine.resource.stream.ResourceStreamProvider;
import io.github.lionarius.engine.util.io.StreamUtil;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL46;
import org.lwjgl.stb.STBImage;

import java.io.IOException;
import java.nio.ByteBuffer;

public class TextureLoader implements ResourceLoader<Texture> {
    @Override
    public Texture loadFromFile(String name, ResourceStreamProvider streamProvider, Object parameters) throws IOException {
        var widthBuffer = BufferUtils.createIntBuffer(1);
        var heightBuffer = BufferUtils.createIntBuffer(1);
        var channelsBuffer = BufferUtils.createIntBuffer(1);

        ByteBuffer rawData;
        try (var stream = streamProvider.getStream(name)) {
            rawData = StreamUtil.readStreamToBuffer(stream);
        }

        STBImage.stbi_set_flip_vertically_on_load(true);
        var data = STBImage.stbi_load_from_memory(rawData, widthBuffer, heightBuffer, channelsBuffer, 0);

        var width = widthBuffer.get();
        var height = heightBuffer.get();
        var channels = channelsBuffer.get();

        if (data == null)
            throw new IOException(STBImage.stbi_failure_reason());

        TextureCreateParameters params;
        if (parameters == null)
            params = TextureCreateParameters.PIXELATED;
        else
            params = (TextureCreateParameters) parameters;

        var internalFormat = getInternalFormat(channels);
        var format = getFormat(channels);

        var texture = new Texture(width, height);
        texture.init(params, internalFormat);
        texture.uploadPixels(format, data);

        STBImage.stbi_image_free(data);

        return texture;
    }

    private static int getInternalFormat(int channels) {
        return switch (channels) {
            case 1 -> GL46.GL_R8;
            case 2 -> GL46.GL_RG8;
            case 3 -> GL46.GL_RGB8;
            case 4 -> GL46.GL_RGBA8;
            default -> throw new IllegalStateException("Unknown number of channels " + channels);
        };
    }

    private static int getFormat(int channels) {
        return switch (channels) {
            case 1 -> GL46.GL_R;
            case 2 -> GL46.GL_RG;
            case 3 -> GL46.GL_RGB;
            case 4 -> GL46.GL_RGBA;
            default -> throw new IllegalStateException("Unknown number of channels " + channels);
        };
    }
}

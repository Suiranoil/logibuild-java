package io.github.lionarius.engine.resource.impl.image;

import io.github.lionarius.engine.resource.ResourceLoader;
import io.github.lionarius.engine.resource.stream.ResourceStreamProvider;
import io.github.lionarius.engine.util.io.StreamUtil;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBImage;

import java.io.IOException;
import java.nio.ByteBuffer;

public class ImageLoader implements ResourceLoader<Image> {
    @Override
    public Image loadFromFile(String name, ResourceStreamProvider streamProvider, Object parameters) throws IOException {
        var widthBuffer = BufferUtils.createIntBuffer(1);
        var heightBuffer = BufferUtils.createIntBuffer(1);
        var channelsBuffer = BufferUtils.createIntBuffer(1);

        ByteBuffer rawData;
        try (var stream = streamProvider.getStream(name)) {
            rawData = StreamUtil.readStreamToBuffer(stream);
        }

        STBImage.stbi_set_flip_vertically_on_load(false);
        var data = STBImage.stbi_load_from_memory(rawData, widthBuffer, heightBuffer, channelsBuffer, 0);

        var width = widthBuffer.get();
        var height = heightBuffer.get();
        var channels = channelsBuffer.get();

        if (data == null)
            throw new IOException("Could not load image");

        var image = new Image(width, height, channels);
        image.init(data);

        data.rewind();
        STBImage.stbi_image_free(data);

        return image;
    }
}

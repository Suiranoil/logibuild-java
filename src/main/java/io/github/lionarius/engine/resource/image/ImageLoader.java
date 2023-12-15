package io.github.lionarius.engine.resource.image;

import io.github.lionarius.engine.resource.ResourceLoader;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBImage;

import java.io.IOException;

public class ImageLoader implements ResourceLoader<Image> {
    @Override
    public Image loadFromFile(String name, String filepath, Object parameters) throws IOException {
        var widthBuffer = BufferUtils.createIntBuffer(1);
        var heightBuffer = BufferUtils.createIntBuffer(1);
        var channelsBuffer = BufferUtils.createIntBuffer(1);

        STBImage.stbi_set_flip_vertically_on_load(false);
        var data = STBImage.stbi_load(filepath, widthBuffer, heightBuffer, channelsBuffer, 0);

        var width = widthBuffer.get();
        var height = heightBuffer.get();
        var channels = channelsBuffer.get();

        if (data == null)
            throw new IOException("Could not load image on path " + filepath);

        var image = new Image(width, height, channels);
        image.init(data);

        data.rewind();
        STBImage.stbi_image_free(data);

        return image;
    }
}

package io.github.lionarius.engine.renderer.texture;

import io.github.lionarius.engine.resource.ResourceLoader;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBImage;

import java.io.IOException;

public class TextureLoader implements ResourceLoader<Texture> {
	@Override
	public Texture loadFromFile(String filepath) throws IOException {
		var width = BufferUtils.createIntBuffer(1);
		var height = BufferUtils.createIntBuffer(1);
		var channels = BufferUtils.createIntBuffer(1);

		STBImage.stbi_set_flip_vertically_on_load(true);
		var data = STBImage.stbi_load(filepath, width, height, channels, 0);

		if (data == null)
			throw new IOException("Could not load image on path " + filepath);

		var texture = new Texture(width.get(), height.get(), channels.get());
		texture.init(data);
		STBImage.stbi_image_free(data);

		return texture;
	}
}

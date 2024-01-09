package io.github.lionarius.engine.resource.impl.font;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.lionarius.engine.resource.ResourceLoader;
import io.github.lionarius.engine.resource.ResourceManager;
import io.github.lionarius.engine.resource.impl.font.json.FontDeserializer;
import io.github.lionarius.engine.resource.impl.texture.Texture;
import io.github.lionarius.engine.resource.impl.texture.TextureCreateParameters;
import io.github.lionarius.engine.resource.stream.ResourceStreamProvider;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
public class FontLoader implements ResourceLoader<Font> {
    private static final Gson GSON = new GsonBuilder().registerTypeAdapter(Font.class, new FontDeserializer()).create();
    private static final String METADATA_EXTENSION = ".metadata.json";
    private static final String ATLAS_EXTENSION = ".atlas.png";
    private final ResourceManager resourceManager;

    @Override
    public Font loadFromFile(String name, ResourceStreamProvider streamProvider, Object parameters) throws IOException {
        String metadata;
        try (var stream = streamProvider.getStream(name + FontLoader.METADATA_EXTENSION)) {
            metadata = IOUtils.toString(stream, StandardCharsets.UTF_8);
        }

        try {
            var font = GSON.fromJson(metadata, Font.class);
            var atlasTexture = this.resourceManager.get(Texture.class, name + FontLoader.ATLAS_EXTENSION, TextureCreateParameters.SMOOTH);
            font.setAtlasTexture(atlasTexture);
            font.init();

            return font;
        } catch (RuntimeException e) {
            throw new IOException("Could not parse font JSON");
        }

    }
}

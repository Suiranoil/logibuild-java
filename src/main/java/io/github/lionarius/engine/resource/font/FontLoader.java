package io.github.lionarius.engine.resource.font;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.lionarius.engine.resource.ResourceLoader;
import io.github.lionarius.engine.resource.ResourceManager;
import io.github.lionarius.engine.resource.font.json.FontDeserializer;
import io.github.lionarius.engine.resource.texture.Texture;
import io.github.lionarius.engine.resource.texture.TextureCreateParameters;
import lombok.RequiredArgsConstructor;
import org.lwjgl.opengl.GL46;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RequiredArgsConstructor
public class FontLoader implements ResourceLoader<Font> {
    private static final Gson GSON = new GsonBuilder().registerTypeAdapter(Font.class, new FontDeserializer()).create();
    private static final TextureCreateParameters TEXTURE_LOAD_PARAMETERS = new TextureCreateParameters(GL46.GL_CLAMP_TO_EDGE, GL46.GL_LINEAR);
    private static final String METADATA_EXTENSION = ".metadata.json";
    private static final String ATLAS_EXTENSION = ".atlas.png";
    private final ResourceManager resourceManager;

    @Override
    public Font loadFromFile(String filepath, Object parameters) throws IOException {
        var metadata = Files.readString(Path.of(filepath + FontLoader.METADATA_EXTENSION));
        try {
            var font = GSON.fromJson(metadata, Font.class);
            var atlasTexture = this.resourceManager.get(Texture.class, "", filepath + FontLoader.ATLAS_EXTENSION, FontLoader.TEXTURE_LOAD_PARAMETERS);
            font.setAtlasTexture(atlasTexture);
            font.init();

            return font;
        } catch (RuntimeException e) {
            throw new IOException("Could not parse font JSON");
        }

    }
}

package io.github.lionarius.engine.resource.font.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import io.github.lionarius.engine.resource.font.Font;
import io.github.lionarius.engine.resource.font.Glyph;

import java.lang.reflect.Type;

public class FontDeserializer implements JsonDeserializer<Font> {
    @Override
    public Font deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        var jsonObject = json.getAsJsonObject();

        Font.Atlas atlas = context.deserialize(jsonObject.get("atlas"), Font.Atlas.class);
        Font.Metrics metrics = context.deserialize(jsonObject.get("metrics"), Font.Metrics.class);
        var font = new Font(atlas, metrics);

        Glyph[] glyphs = context.deserialize(jsonObject.get("glyphs"), Glyph[].class);
        for (var glyph : glyphs)
            font.getGlyphs().put(glyph.getUnicode(), glyph);

        return font;
    }
}

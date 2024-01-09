package io.github.lionarius.engine.resource.impl.shader;

import io.github.lionarius.engine.resource.ResourceLoader;
import io.github.lionarius.engine.resource.stream.ResourceStreamProvider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class ShaderLoader implements ResourceLoader<Shader> {
    private static final Pattern TYPE_REGEX = Pattern.compile("^//\\s+#type\\s+(?<type>[a-z]+)$");

    public Shader loadFromFile(String name, ResourceStreamProvider streamProvider, Object parameters) throws IOException {
        StringBuilder vertexSource = new StringBuilder();
        StringBuilder fragmentSource = new StringBuilder();
        try (var stream = streamProvider.getStream(name)) {
            var reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
            String line;
            String currentSource = "";
            while ((line = reader.readLine()) != null) {
                var matcher = TYPE_REGEX.matcher(line);
                if (matcher.matches()) {
                    currentSource = matcher.group("type");
                    continue;
                }

                switch (currentSource) {
                    case "vertex" -> vertexSource.append(line).append("\n");
                    case "fragment" -> fragmentSource.append(line).append("\n");
                    default -> {
                        assert false : "Unknown shader type " + currentSource;
                    }
                }
            }
        }
        var shader = new Shader(vertexSource.toString(), fragmentSource.toString());
        shader.init();

        return shader;
    }
}

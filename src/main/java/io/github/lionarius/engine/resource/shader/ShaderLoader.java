package io.github.lionarius.engine.resource.shader;

import io.github.lionarius.engine.resource.ResourceLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

public class ShaderLoader implements ResourceLoader<Shader> {
    private static final Pattern TYPE_REGEX = Pattern.compile("^//\\s+#type\\s+(?<type>[a-z]+)$");

    public Shader loadFromFile(String filepath, Object parameters) throws IOException {
        StringBuilder vertexSource = new StringBuilder();
        StringBuilder fragmentSource = new StringBuilder();
        var reader = Files.newBufferedReader(Path.of(filepath));

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
        var shader = new Shader(vertexSource.toString(), fragmentSource.toString());
        shader.init();

        return shader;
    }
}

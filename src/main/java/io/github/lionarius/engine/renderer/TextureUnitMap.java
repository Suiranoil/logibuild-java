package io.github.lionarius.engine.renderer;

import io.github.lionarius.engine.resource.impl.texture.Texture;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class TextureUnitMap implements Iterable<Map.Entry<Texture, Integer>> {
    private final Map<Texture, Integer> textureUnits = new HashMap<>();
    private final int capacity;

    public boolean hasUnits() {
        return this.textureUnits.size() < this.capacity;
    }

    public Optional<Integer> getUnit(Texture texture) {
        if (this.textureUnits.size() >= this.capacity)
            return Optional.empty();

        return Optional.of(this.textureUnits.computeIfAbsent(texture, v -> this.textureUnits.size()));
    }

    public void reset() {
        this.textureUnits.clear();
    }

    @Override
    public @NonNull Iterator<Map.Entry<Texture, Integer>> iterator() {
        return this.textureUnits.entrySet().iterator();
    }
}

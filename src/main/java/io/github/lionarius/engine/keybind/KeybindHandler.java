package io.github.lionarius.engine.keybind;

import io.github.lionarius.engine.InputHandler;
import io.github.lionarius.engine.Updatable;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;

@RequiredArgsConstructor
public class KeybindHandler implements Updatable {
    @NonNull
    private final InputHandler inputHandler;
    private final ArrayList<Keybind> keybinds = new ArrayList<>();

    @Override
    public void update(double delta) {

    }

    @Override
    public void editorUpdate(double delta) {
    }

    public void register(Keybind keybind) {

    }

    private static class KeybindState {

    }
}

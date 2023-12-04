package io.github.lionarius.engine.keybind;

import io.github.lionarius.engine.InputHandler;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;

@RequiredArgsConstructor
public class KeybindHandler {
	@NonNull
	private final InputHandler inputHandler;
	private final ArrayList<Keybind> keybinds = new ArrayList<>();

	public void update() {

	}

	public void register(Keybind keybind) {

	}

	private static class KeybindState {

	}
}

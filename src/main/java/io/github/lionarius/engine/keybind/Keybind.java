package io.github.lionarius.engine.keybind;

import lombok.AccessLevel;
import lombok.Getter;

@Getter(AccessLevel.PACKAGE)
public class Keybind {
	private final int[] keys;
	private final KeybindCallback callback;

	public Keybind(int[] keys, KeybindCallback callback) {
		this.keys = keys.clone();
		this.callback = callback;
	}
}

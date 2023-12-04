package io.github.lionarius.engine.renderer;

import io.github.lionarius.engine.resource.ResourceManager;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.Closeable;

@RequiredArgsConstructor
public class Renderer implements Closeable {
	@NonNull
	private final ResourceManager resourceManager;

	public void init() {

	}

	@Override
	public void close() {

	}
}

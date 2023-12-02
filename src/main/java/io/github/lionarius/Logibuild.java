package io.github.lionarius;

import io.github.lionarius.engine.InputHandler;
import io.github.lionarius.engine.Window;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

public final class Logibuild {
	private static final Logger LOGGER = LogManager.getLogger("Logibuild");
	@Getter
	private static Logibuild instance;

	private final Window window;
	@Getter
	private final InputHandler inputHandler;

	public Logibuild(String[] args) {
		if (instance != null)
			throw new IllegalStateException("Cannot create more than one game instance");

		instance = this;

		this.window = new Window(1280, 720, "Logibuild");
		this.window.init();

		this.inputHandler = new InputHandler(this.window);
		this.inputHandler.init();
	}

	public void run() {
		while (!this.window.shouldClose()) {
			this.window.update();
			this.inputHandler.update();
		}

		this.window.close();
	}
}

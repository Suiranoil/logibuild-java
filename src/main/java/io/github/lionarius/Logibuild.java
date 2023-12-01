package io.github.lionarius;

import io.github.lionarius.engine.Window;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Logibuild {
	@Getter
	private static Logibuild instance;
	private static final Logger LOGGER = LogManager.getLogger("Logibuild");
	private final Window window;

	public Logibuild(String[] args) {
		if (instance != null)
			throw new IllegalStateException("Cannot create more than one game instance");

		instance = this;

		this.window = new Window(1280, 720, "Logibuild");
		this.window.init();
	}

	public void run() {
		while (!this.window.shouldClose()) {
			this.window.update();
		}

		this.window.close();
	}
}

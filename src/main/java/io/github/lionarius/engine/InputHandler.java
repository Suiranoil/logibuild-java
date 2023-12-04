package io.github.lionarius.engine;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.joml.Vector2f;
import org.lwjgl.glfw.*;

import java.util.Arrays;

@RequiredArgsConstructor
public class InputHandler {
	@NonNull
	private final Window window;

	// Mouse
	@Getter
	private final Vector2f mousePosition = new Vector2f();
	@Getter
	private final Vector2f relativeMousePosition = new Vector2f();
	@Getter
	private boolean isMouseInsideWindow = false;
	private final boolean[] mousePressed = new boolean[GLFW.GLFW_MOUSE_BUTTON_LAST + 1];
	private final int[] mousePressedFrame = new int[GLFW.GLFW_MOUSE_BUTTON_LAST + 1];

	// Keyboard
	private final boolean[] keyPressed = new boolean[GLFW.GLFW_KEY_LAST + 1];
	private final int[] keyPressedFrame = new int[GLFW.GLFW_KEY_LAST + 1];
	private final boolean[] keyRepeat = new boolean[GLFW.GLFW_KEY_LAST + 1];

	private final GLFWMouseButtonCallback MOUSE_BUTTON_CALLBACK = new GLFWMouseButtonCallback() {
		@Override
		public void invoke(long window, int button, int action, int mods) {
			if (InputHandler.isMouseNotInBound(button))
				return;

			if (action == GLFW.GLFW_PRESS) {
				InputHandler.this.mousePressed[button] = true;
			} else {
				InputHandler.this.mousePressed[button] = false;
				InputHandler.this.mousePressedFrame[button] = 0;
			}
		}
	};

	private final GLFWCursorPosCallback CURSOR_POS_CALLBACK = new GLFWCursorPosCallback() {
		@Override
		public void invoke(long window, double xPos, double yPos) {
			InputHandler.this.mousePosition.set(xPos, yPos);
			InputHandler.this.mousePosition.div(
					InputHandler.this.window.getWidth(),
					InputHandler.this.window.getHeight(),
					InputHandler.this.relativeMousePosition
			);
		}
	};

	private final GLFWCursorEnterCallback CURSOR_ENTER_CALLBACK = new GLFWCursorEnterCallback() {
		@Override
		public void invoke(long window, boolean entered) {
			InputHandler.this.isMouseInsideWindow = entered;
		}
	};

	private final GLFWKeyCallback KEY_CALLBACK = new GLFWKeyCallback() {
		@Override
		public void invoke(long window, int key, int scancode, int action, int mods) {
			if (InputHandler.isKeyNotInBound(key))
				return;

			switch (action) {
				case GLFW.GLFW_PRESS -> InputHandler.this.keyPressed[key] = true;
				case GLFW.GLFW_REPEAT -> InputHandler.this.keyRepeat[key] = true;
				default -> {
					InputHandler.this.keyPressed[key] = false;
					InputHandler.this.keyPressedFrame[key] = 0;
					InputHandler.this.keyRepeat[key] = false;
				}
			}
		}
	};

	private static boolean isMouseNotInBound(int button) {
		return button < 0 || button > GLFW.GLFW_MOUSE_BUTTON_LAST;
	}

	private static boolean isKeyNotInBound(int key) {
		return key < 0 || key > GLFW.GLFW_KEY_LAST;
	}

	public void init() {
		Arrays.fill(this.mousePressedFrame, 0);
		Arrays.fill(this.keyPressedFrame, 0);

		GLFW.glfwSetKeyCallback(this.window.getHandle(), this.KEY_CALLBACK);
		GLFW.glfwSetMouseButtonCallback(this.window.getHandle(), this.MOUSE_BUTTON_CALLBACK);
		GLFW.glfwSetCursorPosCallback(this.window.getHandle(), this.CURSOR_POS_CALLBACK);
		GLFW.glfwSetCursorEnterCallback(this.window.getHandle(), this.CURSOR_ENTER_CALLBACK);
	}

	public void update() {
		for (int button = 0; button <= GLFW.GLFW_MOUSE_BUTTON_LAST; button++) {
			if (this.mousePressed[button])
				this.mousePressedFrame[button] += 1;
		}

		for (int key = 0; key <= GLFW.GLFW_KEY_LAST; key++) {
			if (this.keyPressed[key])
				this.keyPressedFrame[key] += 1;
		}
	}

	public boolean isMousePressed(int button) {
		if (InputHandler.isMouseNotInBound(button))
			return false;

		return this.mousePressed[button];
	}

	public boolean isMouseJustPressed(int button) {
		if (InputHandler.isMouseNotInBound(button))
			return false;

		return this.mousePressedFrame[button] == 1;
	}

	public boolean isKeyPressed(int key) {
		if (InputHandler.isKeyNotInBound(key))
			return false;

		return this.keyPressed[key];
	}

	public boolean isKeyJustPressed(int key) {
		if (InputHandler.isKeyNotInBound(key))
			return false;

		return this.keyPressedFrame[key] == 1;
	}

	public boolean isKeyRepeat(int key) {
		if (InputHandler.isKeyNotInBound(key))
			return false;

		return this.keyRepeat[key];
	}
}

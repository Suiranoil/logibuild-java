package io.github.lionarius.engine;

import lombok.AccessLevel;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryUtil;

import java.io.Closeable;

public class Window implements Closeable {
    private static final Logger LOGGER = LogManager.getLogger("Window");
    @Getter
    private int width;
    @Getter
    private int height;
    @Getter
    private String title;
    @Getter
    private boolean isVSync;

    @Getter(AccessLevel.PROTECTED)
    private long handle;

    public Window(int width, int height, String title) {
        this.height = height;
        this.width = width;
        this.title = title;
    }

    public void setTitle(String title) {
        this.title = title;
        GLFW.glfwSetWindowTitle(this.handle, this.title);
    }

    public void setVSync(boolean vSync) {
        this.isVSync = vSync;

        if (this.isVSync)
            GLFW.glfwSwapInterval(1);
        else
            GLFW.glfwSwapInterval(0);
    }

    public boolean shouldClose() {
        return GLFW.glfwWindowShouldClose(this.handle);
    }

    public void update() {
        GLFW.glfwSwapBuffers(this.handle);
        GLFW.glfwPollEvents();
    }

    public void init() {
        LOGGER.info("Starting with LWJGL {}", Version.getVersion());
        GLFWErrorCallback.createPrint(System.err).set();

        if (!GLFW.glfwInit())
            throw new IllegalStateException("Could not initialize GLFW");

        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);
//		GLFW.glfwWindowHint(GLFW.GLFW_MAXIMIZED, GLFW.GLFW_TRUE);

        this.handle = GLFW.glfwCreateWindow(this.width, this.height, this.title, MemoryUtil.NULL, MemoryUtil.NULL);
        if (this.handle == MemoryUtil.NULL)
            throw new IllegalStateException("Could not create GLFW window");

        GLFW.glfwMakeContextCurrent(this.handle);
        this.setVSync(true);

        GLFW.glfwShowWindow(this.handle);

        GL.createCapabilities();

        this.initCallbacks();

        LOGGER.info("Initialized GLFW window");
    }

    private void initCallbacks() {
        GLFW.glfwSetFramebufferSizeCallback(this.handle, (window, width, height) -> {
            this.width = width;
            this.height = height;

            GL46.glViewport(0, 0, this.width, this.height);
        });
    }

    @Override
    public void close() {
        GLFW.glfwDestroyWindow(this.handle);
        GLFW.glfwTerminate();
    }
}

package io.github.lionarius.engine;

import lombok.Getter;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;

public class Window implements Closeable {
    private static final Logger LOGGER = LoggerFactory.getLogger("Window");
    private final Vector2i size = new Vector2i();
    //    @Getter
//    private int width;
//    @Getter
//    private int height;
    @Getter
    private String title;
    @Getter
    private boolean isVSync;
    @Getter
    private boolean isFullscreen = false;

    private final Vector2i preFullscreenPosition = new Vector2i();
    private final Vector2i preFullscreenSize = new Vector2i();

    @Getter
    private long handle;

    public Window(int width, int height, String title) {
        this.size.set(width, height);
        this.title = title;
    }

    public Vector2ic getSize() {
        return this.size;
    }

    public int getWidth() {
        return this.size.x();
    }

    public int getHeight() {
        return this.size.y();
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

    public void setFullscreen(boolean fullscreen) {
        if (this.isFullscreen == fullscreen)
            return;

        if (fullscreen) {
            try (var stack = MemoryStack.stackPush()) {
                var i1 = stack.callocInt(1);
                var i2 = stack.callocInt(1);

                GLFW.glfwGetWindowPos(this.handle, i1, i2);
                this.preFullscreenPosition.set(i1.get(), i2.get());
                this.preFullscreenSize.set(this.size);
            }

            var monitor = GLFW.glfwGetPrimaryMonitor();
            var mode = GLFW.glfwGetVideoMode(monitor);
            assert mode != null;

            GLFW.glfwSetWindowMonitor(this.handle, monitor, 0, 0, mode.width(), mode.height(), mode.refreshRate());
        } else {
            GLFW.glfwSetWindowMonitor(this.handle, 0, this.preFullscreenPosition.x(), this.preFullscreenPosition.y(), this.preFullscreenSize.x(), this.preFullscreenSize.y(), 0);
        }

        this.isFullscreen = fullscreen;
    }

    public boolean shouldClose() {
        return GLFW.glfwWindowShouldClose(this.handle);
    }

    public void init() {
        LOGGER.info("Starting with LWJGL {}", Version.getVersion());
        GLFWErrorCallback.createPrint(System.err).set();

        if (!GLFW.glfwInit())
            throw new IllegalStateException("Could not initialize GLFW");

        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);

        this.handle = GLFW.glfwCreateWindow(this.size.x(), this.size.y(), this.title, MemoryUtil.NULL, MemoryUtil.NULL);
        if (this.handle == MemoryUtil.NULL)
            throw new IllegalStateException("Could not create GLFW window");

        GLFW.glfwMakeContextCurrent(this.handle);
        this.setVSync(true);

        GLFW.glfwShowWindow(this.handle);

        GL.createCapabilities();

        this.initCallbacks();

        LOGGER.info("Initialized GLFW window");
    }

    public void update() {
        GLFW.glfwSwapBuffers(this.handle);
        GLFW.glfwPollEvents();
    }

    @Override
    public void close() {
        GLFW.glfwDestroyWindow(this.handle);
        GLFW.glfwTerminate();
    }

    private void initCallbacks() {
        GLFW.glfwSetFramebufferSizeCallback(this.handle, (window, width, height) -> {
            this.size.set(width, height);

//            GL46.glViewport(0, 0, width, height);
        });
    }
}

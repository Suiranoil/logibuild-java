package io.github.lionarius;

import io.github.lionarius.engine.InputHandler;
import io.github.lionarius.engine.Window;
import io.github.lionarius.engine.editor.imgui.ImGuiLayer;
import io.github.lionarius.engine.keybind.KeybindHandler;
import io.github.lionarius.engine.renderer.EngineRenderer;
import io.github.lionarius.engine.renderer.ScreenspaceCamera;
import io.github.lionarius.engine.resource.ResourceManager;
import io.github.lionarius.engine.resource.font.Font;
import io.github.lionarius.engine.resource.font.FontLoader;
import io.github.lionarius.engine.resource.image.Image;
import io.github.lionarius.engine.resource.image.ImageLoader;
import io.github.lionarius.engine.resource.mesh.Mesh;
import io.github.lionarius.engine.resource.mesh.MeshLoader;
import io.github.lionarius.engine.resource.scene.SceneLoader;
import io.github.lionarius.engine.resource.shader.Shader;
import io.github.lionarius.engine.resource.shader.ShaderLoader;
import io.github.lionarius.engine.resource.texture.Texture;
import io.github.lionarius.engine.resource.texture.TextureLoader;
import io.github.lionarius.engine.scene.Scene;
import io.github.lionarius.engine.scene.SceneManager;
import io.github.lionarius.engine.util.Closeable;
import io.github.lionarius.engine.util.TimeUtil;
import lombok.Getter;
import lombok.Setter;
import org.joml.Quaternionf;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL46;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Logibuild implements Closeable {
    private static final Logger LOGGER = LoggerFactory.getLogger("Logibuild");
    @Getter
    private static Logibuild instance;

    private final Window window = new Window(1280, 720, "Logibuild");

    @Getter
    private final ResourceManager resourceManager = new ResourceManager(ClassLoader.getSystemResource("assets").getPath());
    @Getter
    private final InputHandler inputHandler = new InputHandler(this.window);
    @Getter
    private final KeybindHandler keybindHandler = new KeybindHandler(this.inputHandler);
    @Getter
    private final EngineRenderer engineRenderer = new EngineRenderer(this.resourceManager);
    private final ImGuiLayer imGuiLayer;
    private final boolean noEditor;
    @Getter @Setter
    private boolean debugDraw = false;

    @Getter
    private final SceneManager sceneManager;

    public Logibuild(String[] args) {
        if (Logibuild.instance != null)
            throw new IllegalStateException("Cannot create more than one game instance");
        Logibuild.instance = this;

        boolean noEditor = false;
        for (var arg : args) {
            if (arg.equals("--noEditor")) {
                noEditor = true;
            } else if (arg.equals("--debug")) {
                this.debugDraw = true;
            }
        }
        this.noEditor = noEditor;

        this.window.init();
        this.window.setVSync(true);

        this.inputHandler.init(!this.noEditor);

        this.resourceManager.register(Shader.class, new ShaderLoader());
        this.resourceManager.register(Texture.class, new TextureLoader());
        this.resourceManager.register(Font.class, new FontLoader(this.resourceManager));
        this.resourceManager.register(Image.class, new ImageLoader());
        this.resourceManager.register(Scene.class, new SceneLoader());
        this.resourceManager.register(Mesh.class, new MeshLoader());

        this.engineRenderer.init();

        this.sceneManager = new SceneManager();
        this.imGuiLayer = new ImGuiLayer(this.window);
        this.imGuiLayer.init();

        this.window.setIcon(this.resourceManager.get(Image.class, "icons/icon.png"));
    }

    public void run() {
        double prevTime;
        double currentTime = TimeUtil.getApplicationTime();
        double dt = -1.0;

        if (this.noEditor) {
            this.sceneManager.transitionTo(this.resourceManager.get(Scene.class, "asteroids/asteroids.scene"));
            this.sceneManager.startPlaying();
        } else
            this.sceneManager.transitionTo(new Scene());

        while (!this.window.shouldClose()) {
            if (dt >= 0) {
                this.update(dt);
                this.render(dt);
            }

            this.window.update();

            prevTime = currentTime;
            currentTime = TimeUtil.getApplicationTime();
            dt = currentTime - prevTime;
        }
    }

    @Override
    public void close() {
        this.imGuiLayer.close();
        this.engineRenderer.close();
        this.window.close();
    }

    private void update(double delta) {
        this.inputHandler.update(delta);
        this.keybindHandler.update(delta);

        this.sceneManager.update(delta);

        if (!this.noEditor) {
            this.imGuiLayer.beginFrame();
            this.imGuiLayer.begin();

            this.imGuiLayer.draw();

            this.imGuiLayer.end();
            this.imGuiLayer.endFrame();
        } else {
            var viewportSize = this.window.getSize();

            GL46.glViewport(0, 0, viewportSize.x(), viewportSize.y());
            var camera = this.sceneManager.getSceneCamera();
            var framebuffer = this.engineRenderer.getFramebuffer();
            if (camera != null) {
                camera.setFrameSize(new Vector2i(viewportSize.x(), viewportSize.y()));
                framebuffer.resize(viewportSize.x(), viewportSize.y());
            }
        }
    }

    private void render(double delta) {
        var sceneCamera = this.sceneManager.getSceneCamera();
        this.engineRenderer.beginFrame(sceneCamera);

        this.sceneManager.render(delta);

        if (sceneCamera == null) {
            LOGGER.warn("Scene does not have a camera!");
            this.engineRenderer.endEmptyFrame();
        } else
            this.engineRenderer.endFrame();

        this.engineRenderer.clear();
        if (!this.noEditor)
            this.imGuiLayer.render();
        else {
            this.engineRenderer.beginFrame(new ScreenspaceCamera());

            var framebuffer = this.engineRenderer.getFramebuffer();
            var quadRenderer = this.engineRenderer.getQuadRenderer();
            quadRenderer.renderQuad(
                    new Vector3f(0),
                    new Quaternionf(),
                    new Vector3f(1, -1, 1),
                    new Vector3f(2),
                    new Vector4f(1),
                    framebuffer.getTexture()
            );

            this.engineRenderer.endScreenFrame();
        }
    }
}

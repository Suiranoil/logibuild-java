package io.github.lionarius;

import io.github.lionarius.engine.InputHandler;
import io.github.lionarius.engine.Window;
import io.github.lionarius.engine.editor.imgui.ImGuiLayer;
import io.github.lionarius.engine.keybind.KeybindHandler;
import io.github.lionarius.engine.renderer.EngineRenderer;
import io.github.lionarius.engine.renderer.ScreenspaceCamera;
import io.github.lionarius.engine.resource.ResourceManager;
import io.github.lionarius.engine.resource.impl.font.Font;
import io.github.lionarius.engine.resource.impl.font.FontLoader;
import io.github.lionarius.engine.resource.impl.image.Image;
import io.github.lionarius.engine.resource.impl.image.ImageLoader;
import io.github.lionarius.engine.resource.impl.mesh.Mesh;
import io.github.lionarius.engine.resource.impl.mesh.MeshLoader;
import io.github.lionarius.engine.resource.impl.scene.SceneLoader;
import io.github.lionarius.engine.resource.impl.shader.Shader;
import io.github.lionarius.engine.resource.impl.shader.ShaderLoader;
import io.github.lionarius.engine.resource.impl.texture.Texture;
import io.github.lionarius.engine.resource.impl.texture.TextureLoader;
import io.github.lionarius.engine.resource.stream.ClasspathStreamProvider;
import io.github.lionarius.engine.resource.stream.FilesystemStreamProvider;
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

import java.nio.file.FileSystems;

public final class Logibuild implements Closeable {
    private static final Logger LOGGER = LoggerFactory.getLogger("Logibuild");
    @Getter
    private static Logibuild instance;

    private final Window window = new Window(1280, 720, "Logibuild");

    @Getter
    private final ResourceManager internalResourceManager = new ResourceManager(new ClasspathStreamProvider("assets/"));
    @Getter
    private final ResourceManager workspaceResourceManager;
    @Getter
    private final InputHandler inputHandler = new InputHandler(this.window);
    @Getter
    private final KeybindHandler keybindHandler = new KeybindHandler(this.inputHandler);
    @Getter
    private final EngineRenderer engineRenderer = new EngineRenderer(this.internalResourceManager);
    private final ImGuiLayer imGuiLayer;
    private final boolean noEditor;
    private final String noEditorScene;
    @Getter @Setter
    private boolean debugDraw = false;

    @Getter
    private final SceneManager sceneManager;

    public Logibuild(String[] args) {
        if (Logibuild.instance != null)
            throw new IllegalStateException("Cannot create more than one game instance");
        Logibuild.instance = this;

        this.workspaceResourceManager = new ResourceManager(new FilesystemStreamProvider(FileSystems.getDefault().getPath(".").toString()));

        var noEditor = false;
        String scenePath = null;
        for (var arg : args) {
            if (arg.equals("--noEditor")) {
                noEditor = true;
            } else if (arg.equals("--debug")) {
                this.debugDraw = true;
            } else if (arg.startsWith("--scene=")) {
                scenePath = arg.substring(8);
            }
        }
        this.noEditor = noEditor;
        this.noEditorScene = scenePath;

        if (this.noEditor && this.noEditorScene == null)
            throw new IllegalStateException("Tried to run no editor mode without starting scene");

        this.window.init();
        this.window.setVSync(true);

        this.inputHandler.init(!this.noEditor);

        this.internalResourceManager.register(Shader.class, new ShaderLoader());
        this.internalResourceManager.register(Texture.class, new TextureLoader());
        this.internalResourceManager.register(Font.class, new FontLoader(this.internalResourceManager));
        this.internalResourceManager.register(Image.class, new ImageLoader());
        this.internalResourceManager.register(Scene.class, new SceneLoader());
        this.internalResourceManager.register(Mesh.class, new MeshLoader());

        this.workspaceResourceManager.register(Shader.class, new ShaderLoader());
        this.workspaceResourceManager.register(Texture.class, new TextureLoader());
        this.workspaceResourceManager.register(Font.class, new FontLoader(this.workspaceResourceManager));
        this.workspaceResourceManager.register(Image.class, new ImageLoader());
        this.workspaceResourceManager.register(Scene.class, new SceneLoader());
        this.workspaceResourceManager.register(Mesh.class, new MeshLoader());

        this.engineRenderer.init();

        this.sceneManager = new SceneManager();
        this.imGuiLayer = new ImGuiLayer(this.window);
        this.imGuiLayer.init();

        this.window.setIcon(this.internalResourceManager.get(Image.class, "icons/icon.png"));
    }

    public void run() {
        double prevTime;
        double currentTime = TimeUtil.getApplicationTime();
        double dt = -1.0;

        if (this.noEditor) {
            this.sceneManager.transitionTo(this.workspaceResourceManager.get(Scene.class, this.noEditorScene));
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

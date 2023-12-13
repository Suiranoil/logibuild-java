package io.github.lionarius;

import imgui.ImGui;
import io.github.lionarius.client.component.FpsDisplay;
import io.github.lionarius.engine.InputHandler;
import io.github.lionarius.engine.Window;
import io.github.lionarius.engine.editor.ImGuiLayer;
import io.github.lionarius.engine.keybind.KeybindHandler;
import io.github.lionarius.engine.renderer.EngineRenderer;
import io.github.lionarius.engine.renderer.framebuffer.Framebuffer;
import io.github.lionarius.engine.resource.ResourceManager;
import io.github.lionarius.engine.resource.font.Font;
import io.github.lionarius.engine.resource.font.FontLoader;
import io.github.lionarius.engine.resource.shader.Shader;
import io.github.lionarius.engine.resource.shader.ShaderLoader;
import io.github.lionarius.engine.resource.texture.Texture;
import io.github.lionarius.engine.resource.texture.TextureLoader;
import io.github.lionarius.engine.scene.GameObject;
import io.github.lionarius.engine.scene.Scene;
import io.github.lionarius.engine.scene.SceneManager;
import io.github.lionarius.engine.scene.builtin.Box2DRenderer;
import io.github.lionarius.engine.scene.builtin.OrthoCamera;
import io.github.lionarius.engine.scene.builtin.SimpleMovement;
import io.github.lionarius.engine.scene.builtin.Text2DRenderer;
import io.github.lionarius.engine.util.Closeable;
import io.github.lionarius.engine.util.TimeUtil;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Vector2i;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL46;

import java.util.List;

public final class Logibuild implements Closeable {
    private static final Logger LOGGER = LogManager.getLogger("Logibuild");
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
    private final ImGuiLayer imGuiLayer = new ImGuiLayer(this.window);

    @Getter
    private final SceneManager sceneManager = new SceneManager();

    private final Framebuffer framebuffer;

    public Logibuild(String[] args) {
        if (instance != null)
            throw new IllegalStateException("Cannot create more than one game instance");
        instance = this;

        this.window.init();
//        this.window.setVSync(false);

        this.inputHandler.init(true);
        this.imGuiLayer.init();

        this.resourceManager.register(Shader.class, new ShaderLoader());
        this.resourceManager.register(Texture.class, new TextureLoader());
        this.resourceManager.register(Font.class, new FontLoader(this.resourceManager));

        this.engineRenderer.init();

        this.framebuffer = new Framebuffer(this.window.getWidth(), this.window.getHeight());
        this.framebuffer.init();
    }

    public void run() {
        double prevTime;
        double currentTime = TimeUtil.getApplicationTime();
        double dt = -1.0;

        this.sceneManager.transitionTo(this.setupInitialScene());

        while (!this.window.shouldClose()) {
            if (dt >= 0) {
                this.update(dt);
                this.render(dt);

//                LOGGER.info("FPS {}", 1 / dt);
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

        this.imGuiLayer.beginFrame();
        this.imGuiLayer.begin();

        ImGui.showDemoWindow();

        ImGui.begin("Scene");
        var viewportSize = ImGui.getContentRegionAvail();
        GL46.glViewport(0, 0, (int) viewportSize.x, (int) viewportSize.y);
        this.sceneManager.getSceneCamera().setFrameSize(new Vector2i((int) viewportSize.x, (int) viewportSize.y));
        this.framebuffer.resize((int) viewportSize.x, (int) viewportSize.y);
        ImGui.image(this.framebuffer.getTexture().getId(), this.framebuffer.getWidth(), this.framebuffer.getHeight(), 0, 1, 1, 0);
        ImGui.end();

        ImGui.begin("Hierarchy");
        this.sceneManager.getCurrentScene().imguiHierarchy();
        ImGui.end();

        ImGui.begin("Properties");
        var selected = this.sceneManager.getCurrentScene().getSelectedGameObject();
        if (selected != null)
            selected.imgui();
        ImGui.end();

        this.imGuiLayer.end();
        this.imGuiLayer.endFrame();
    }

    private void render(double delta) {
        this.framebuffer.bind();
        this.engineRenderer.beginFrame();

        this.sceneManager.render(delta);

        var sceneCamera = this.sceneManager.getSceneCamera();
        if (sceneCamera == null)
            LOGGER.warn("Scene does not have a camera!");
        else
            this.engineRenderer.endFrame(sceneCamera.getProjection(), sceneCamera.getView());

        this.framebuffer.unbind();

        this.engineRenderer.clear();
        this.imGuiLayer.render();
    }

    private Scene setupInitialScene() {
        var scene = new Scene();

        var camera = new GameObject(List.of(
                new OrthoCamera(new Vector2i(0)),
                new SimpleMovement()
        ));
        camera.setName("MainCamera");
        camera.getTransform().setPosition(0, 0, -50);

        var testObject2 = new GameObject();
        {
            var textComponent = new Text2DRenderer("Hello world!", new Vector4f(1));
            testObject2.addComponent(textComponent);
            textComponent.setFont(this.resourceManager.get(Font.class, "font/atlas/rubik"));
            var transform = testObject2.getTransform();
            transform.setPosition(0, 0, -1);
            transform.getSize().set(0, 100, 0);
        }

        var fpsDisplay = new GameObject(List.of(
                new FpsDisplay(0.025)
        ));
        {
            var textComponent = new Text2DRenderer("", new Vector4f(0, 1, 0, 1));
            textComponent.setFont(this.resourceManager.get(Font.class, "font/atlas/cascadia"));
            fpsDisplay.addComponent(textComponent);
            var transform = fpsDisplay.getTransform();
            transform.setPosition(-600, -300, 0);
            transform.getSize().set(0, 100, 0);

        }

        var testObject = new GameObject(List.of(new Box2DRenderer(new Vector4f(1))));
        {
            var transformComponent = testObject.getTransform();
            transformComponent.setSize(1000, 1000, 0);

            var box = testObject.getComponent(Box2DRenderer.class);
            assert box != null;
            box.setTexture(this.resourceManager.get(Texture.class, "font/atlas/cascadia.atlas.png"));
        }

        scene.addGameObject(testObject);
        scene.addGameObject(testObject2);
        scene.addGameObject(camera);
        scene.addGameObject(fpsDisplay);

        camera.addChild(fpsDisplay);

        return scene;
    }
}

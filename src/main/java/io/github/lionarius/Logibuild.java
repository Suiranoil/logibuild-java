package io.github.lionarius;

import io.github.lionarius.client.component.FpsDisplay;
import io.github.lionarius.engine.InputHandler;
import io.github.lionarius.engine.Window;
import io.github.lionarius.engine.editor.imgui.ImGuiLayer;
import io.github.lionarius.engine.keybind.KeybindHandler;
import io.github.lionarius.engine.renderer.EngineRenderer;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

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

    @Getter
    private final SceneManager sceneManager;

    public Logibuild(String[] args) {
        if (instance != null)
            throw new IllegalStateException("Cannot create more than one game instance");
        instance = this;

        this.window.init();
//        this.window.setVSync(false);

        this.inputHandler.init(true);

        this.resourceManager.register(Shader.class, new ShaderLoader());
        this.resourceManager.register(Texture.class, new TextureLoader());
        this.resourceManager.register(Font.class, new FontLoader(this.resourceManager));

        this.engineRenderer.init();

        this.sceneManager = new SceneManager();
        this.imGuiLayer = new ImGuiLayer(this.window, this.sceneManager, this.engineRenderer);
        this.imGuiLayer.init();
    }

    public void run() {
        double prevTime;
        double currentTime = TimeUtil.getApplicationTime();
        double dt = -1.0;

        var scene = this.setupInitialScene();
        this.sceneManager.transitionTo(scene);

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

        this.imGuiLayer.draw();

        this.imGuiLayer.end();
        this.imGuiLayer.endFrame();
    }

    private void render(double delta) {
        this.engineRenderer.beginFrame();

        this.sceneManager.render(delta);

        var sceneCamera = this.sceneManager.getSceneCamera();
        if (sceneCamera == null)
            LOGGER.warn("Scene does not have a camera!");
        else
            this.engineRenderer.endFrame(sceneCamera.getProjection(), sceneCamera.getView());

        this.engineRenderer.clear();
        this.imGuiLayer.render();
    }

    private Scene setupInitialScene() {
        var scene = new Scene();

        var camera = new GameObject(List.of(
                new OrthoCamera(),
                new SimpleMovement()
        ));
        camera.setName("MainCamera");
        camera.getTransform().setPosition(0, 0, -50);

        var testObject2 = new GameObject();
        {
            var textComponent = new Text2DRenderer();
            testObject2.addComponent(textComponent);
            textComponent.setFont(this.resourceManager.get(Font.class, "font/atlas/rubik"));
            var transform = testObject2.getTransform();
            transform.setPosition(0, 0, -1);
            transform.getSize().set(0, 100, 0);
        }

        var fpsDisplay = new GameObject(List.of(
                new FpsDisplay()
        ));
        {
            var textComponent = new Text2DRenderer();
            textComponent.setFont(this.resourceManager.get(Font.class, "font/atlas/cascadia"));
            fpsDisplay.addComponent(textComponent);
            var transform = fpsDisplay.getTransform();
            transform.setPosition(-600, -300, 0);
            transform.getSize().set(0, 100, 0);

        }

        var testObject = new GameObject(List.of(new Box2DRenderer()));
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

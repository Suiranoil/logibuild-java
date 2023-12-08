package io.github.lionarius;

import io.github.lionarius.engine.InputHandler;
import io.github.lionarius.engine.Window;
import io.github.lionarius.engine.keybind.KeybindHandler;
import io.github.lionarius.engine.renderer.EngineRenderer;
import io.github.lionarius.engine.renderer.shader.Shader;
import io.github.lionarius.engine.renderer.shader.ShaderLoader;
import io.github.lionarius.engine.renderer.texture.Texture;
import io.github.lionarius.engine.renderer.texture.TextureLoader;
import io.github.lionarius.engine.resource.ResourceManager;
import io.github.lionarius.engine.scene.GameObject;
import io.github.lionarius.engine.scene.Scene;
import io.github.lionarius.engine.scene.SceneManager;
import io.github.lionarius.engine.scene.builtin.Box2DRenderer;
import io.github.lionarius.engine.scene.builtin.OrthoCamera;
import io.github.lionarius.engine.scene.builtin.Transform;
import io.github.lionarius.engine.util.TimeUtil;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Vector4f;

import java.util.List;

public final class Logibuild {
    private static final Logger LOGGER = LogManager.getLogger("Logibuild");
    @Getter
    private static Logibuild instance;

    private final Window window = new Window(1280, 720, "Logibuild");

    @Getter
    private final ResourceManager resourceManager = new ResourceManager("assets");
    @Getter
    private final InputHandler inputHandler = new InputHandler(this.window);
    @Getter
    private final KeybindHandler keybindHandler = new KeybindHandler(this.inputHandler);
    @Getter
    private final EngineRenderer engineRenderer = new EngineRenderer(this.resourceManager);

    @Getter
    private final SceneManager sceneManager = new SceneManager();

    public Logibuild(String[] args) {
        if (instance != null)
            throw new IllegalStateException("Cannot create more than one game instance");
        instance = this;

        this.window.init();
        this.window.setVSync(false);
        this.inputHandler.init();

        this.resourceManager.register(Shader.class, new ShaderLoader());
        this.resourceManager.register(Texture.class, new TextureLoader());

        this.engineRenderer.init();
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

                LOGGER.info("FPS {}", 1 / dt);
            }

            this.window.update();

            prevTime = currentTime;
            currentTime = TimeUtil.getApplicationTime();
            dt = currentTime - prevTime;
        }

        this.engineRenderer.close();
        this.window.close();
    }

    private void update(double delta) {
        this.inputHandler.update(delta);
        this.keybindHandler.update(delta);

        this.sceneManager.update(delta);
    }

    private void render(double delta) {
        this.engineRenderer.beginFrame();

//        var mousePosition = this.inputHandler.getMousePosition();
//        this.engineRenderer.getQuadRenderer().renderQuad(new Vector3f(mousePosition, 0), new Quaternionf(), new Vector3f(10, 10, 0), new Vector3f(1), new Vector4f(1));

        this.sceneManager.render(delta);

        var sceneCamera = this.sceneManager.getSceneCamera();
        if (sceneCamera == null)
            LOGGER.warn("Scene does not have a camera!");
        else
            this.engineRenderer.endFrame(sceneCamera.getProjection(), sceneCamera.getView());
    }

    private Scene setupInitialScene() {
        var scene = new Scene();
        var camera = new GameObject(List.of(new OrthoCamera(this.window)));
        var cameraTransform = camera.getComponent(Transform.class);
        cameraTransform.getPosition().set(0, 300, 0);
        cameraTransform.getScale().set(1, -1, 1);
        scene.addGameObject(camera);

        var testObject = new GameObject();
        var renderComponent = new Box2DRenderer(this.engineRenderer.getQuadRenderer(), new Vector4f(1, 0, 0, 1));
        var transformComponent = testObject.getComponent(Transform.class);
        assert transformComponent != null;
        transformComponent.getSize().set(250, 300, 1);
        transformComponent.getPosition().set(0, 300, 0);
        testObject.addComponent(renderComponent);
        scene.addGameObject(testObject);

        return scene;
    }
}

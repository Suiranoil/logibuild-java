package io.github.lionarius;

import io.github.lionarius.engine.InputHandler;
import io.github.lionarius.engine.Window;
import io.github.lionarius.engine.editor.imgui.ImGuiLayer;
import io.github.lionarius.engine.keybind.KeybindHandler;
import io.github.lionarius.engine.renderer.EngineRenderer;
import io.github.lionarius.engine.resource.ResourceManager;
import io.github.lionarius.engine.resource.font.Font;
import io.github.lionarius.engine.resource.font.FontLoader;
import io.github.lionarius.engine.resource.image.Image;
import io.github.lionarius.engine.resource.image.ImageLoader;
import io.github.lionarius.engine.resource.shader.Shader;
import io.github.lionarius.engine.resource.shader.ShaderLoader;
import io.github.lionarius.engine.resource.texture.Texture;
import io.github.lionarius.engine.resource.texture.TextureLoader;
import io.github.lionarius.engine.scene.Scene;
import io.github.lionarius.engine.scene.SceneManager;
import io.github.lionarius.engine.util.Closeable;
import io.github.lionarius.engine.util.TimeUtil;
import lombok.Getter;
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

    @Getter
    private final SceneManager sceneManager;

    public Logibuild(String[] args) {
        if (Logibuild.instance != null)
            throw new IllegalStateException("Cannot create more than one game instance");
        Logibuild.instance = this;

        this.window.init();
//        this.window.setVSync(false);

        this.inputHandler.init(true);

        this.resourceManager.register(Shader.class, new ShaderLoader());
        this.resourceManager.register(Texture.class, new TextureLoader());
        this.resourceManager.register(Font.class, new FontLoader(this.resourceManager));
        this.resourceManager.register(Image.class, new ImageLoader());

        this.engineRenderer.init();

        this.sceneManager = new SceneManager();
        this.imGuiLayer = new ImGuiLayer(this.window, this.sceneManager, this.engineRenderer);
        this.imGuiLayer.init();

        this.window.setIcon(this.resourceManager.get(Image.class, "icon.png"));
    }

    public void run() {
        double prevTime;
        double currentTime = TimeUtil.getApplicationTime();
        double dt = -1.0;

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
        if (sceneCamera == null) {
            LOGGER.warn("Scene does not have a camera!");
            this.engineRenderer.endEmptyFrame();
        } else
            this.engineRenderer.endFrame(sceneCamera.getProjection(), sceneCamera.getView());

        this.engineRenderer.clear();
        this.imGuiLayer.render();
    }
}

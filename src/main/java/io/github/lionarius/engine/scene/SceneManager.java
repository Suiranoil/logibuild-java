package io.github.lionarius.engine.scene;

import com.google.gson.GsonBuilder;
import io.github.lionarius.engine.Renderable;
import io.github.lionarius.engine.Updatable;
import io.github.lionarius.engine.scene.builtin.Camera;
import io.github.lionarius.engine.scene.builtin.OrthoCamera;
import io.github.lionarius.engine.scene.builtin.SimpleMovement;
import io.github.lionarius.engine.scene.json.GameObjectSerializer;
import io.github.lionarius.engine.scene.json.SceneJsonIO;
import lombok.Getter;

import java.util.List;

public class SceneManager implements Updatable, Renderable {
    private Scene originalScene = null;
    @Getter
    private Scene currentScene = null;
    private Scene queuedScene = null;
    private SceneManagerState state = SceneManagerState.EDITOR;
    private final GameObject editorCamera;
    private final CameraObject camera;

    public SceneManager() {
        this.editorCamera = new GameObject(List.of(new SimpleMovement(), new OrthoCamera()));
        this.camera = this.editorCamera.getComponent(Camera.class);

        this.editorCamera.awake();
        this.editorCamera.start();
    }

    public boolean isPlaying() {
        return this.state == SceneManagerState.PLAYING;
    }

    public void transitionTo(Scene newScene) {
        if (this.state == SceneManagerState.PLAYING)
            this.queuedScene = newScene;
        else
            this.currentScene = newScene;

        if (newScene != null)
            newScene.verifyIntegrity();
    }

    public CameraObject getSceneCamera() {
        if (this.state == SceneManagerState.PLAYING) {
            if (this.currentScene != null)
                return this.currentScene.getMainCamera();
        }

        return this.camera;
    }

    public void startPlaying() {
        if (this.state != SceneManagerState.EDITOR)
            return;

        this.state = SceneManagerState.EDITOR_TO_PLAYING;
    }

    public void stopPlaying() {
        if (this.state != SceneManagerState.PLAYING)
            return;

        this.state = SceneManagerState.PLAYING_TO_EDITOR;
    }

    @Override
    public void update(double delta) {
        if (this.queuedScene != null)
            this.performTransition();

        if (this.state == SceneManagerState.EDITOR_TO_PLAYING) {
            this.originalScene = this.currentScene;

            var builder = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(GameObject.class, new GameObjectSerializer()).registerTypeAdapter(Scene.class, new SceneJsonIO());
            var gson = builder.create();
            var json = gson.toJson(this.currentScene);

            this.queuedScene = gson.fromJson(json, Scene.class);
            this.currentScene.setSelectedGameObject(null);

            this.performTransition();
            this.state = SceneManagerState.PLAYING;
        }

        if (this.state == SceneManagerState.PLAYING_TO_EDITOR) {
            var selectedObject = this.currentScene.getSelectedGameObject().getUuid();
            this.currentScene = this.originalScene;
            this.originalScene = null;
            this.currentScene.setSelectedGameObject(this.currentScene.findByUUID(selectedObject));
//            this.leaveScene(); // Not sure about that
            this.state = SceneManagerState.EDITOR;
        }

        if (this.currentScene != null && this.state == SceneManagerState.PLAYING)
            this.currentScene.update(delta);
        else {
            this.editorUpdate(delta);
        }
    }

    @Override
    public void editorUpdate(double delta) {
        if (this.currentScene != null)
            this.currentScene.editorUpdate(delta);
        this.editorCamera.update(delta);
    }

    @Override
    public void render(double delta) {
        if (this.currentScene != null)
            this.currentScene.render(delta);
    }

    private void performTransition() {
        this.leaveScene();

        this.currentScene = this.queuedScene;
        this.queuedScene = null;

        this.enterScene();
    }

    private void leaveScene() {
        if (this.currentScene != null)
            this.currentScene.leave();
    }

    private void enterScene() {
        if (this.currentScene != null)
            this.currentScene.enter();
    }
}

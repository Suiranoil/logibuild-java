package io.github.lionarius.engine.editor.imgui;

import imgui.ImGui;
import imgui.flag.ImGuiHoveredFlags;
import imgui.flag.ImGuiTreeNodeFlags;
import io.github.lionarius.engine.scene.GameObject;
import io.github.lionarius.engine.scene.Scene;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ImGuiScene {
    public static void drawHierarchyTree(Scene scene) {
        if (scene == null)
            return;

        ImGuiScene.drawHierarchyTreeRecursive(scene, null);
    }

    private static void drawHierarchyTreeRecursive(Scene scene, GameObject gameObject) {
        var children = scene.getHierarchy().getChildren(gameObject);

        var selected = scene.getSelectedGameObject() != null && scene.getSelectedGameObject() == gameObject;
        var isLeaf = !scene.getHierarchy().isParent(gameObject);

        var flags = ImGuiTreeNodeFlags.DefaultOpen | ImGuiTreeNodeFlags.OpenOnArrow | ImGuiTreeNodeFlags.OpenOnDoubleClick | ImGuiTreeNodeFlags.SpanAvailWidth;
        if (selected)
            flags |= ImGuiTreeNodeFlags.Selected;
        if (isLeaf)
            flags |= ImGuiTreeNodeFlags.Leaf | ImGuiTreeNodeFlags.NoTreePushOnOpen;

        var id = 0;
        var label = "Root";
        if (gameObject != null) {
            id = gameObject.getUuid().hashCode();
            label = gameObject.getName();
        }

        ImGui.pushID(id);

        var open = ImGui.treeNodeEx("item", flags, label) && !isLeaf;

        if (ImGui.beginPopupContextItem()) {
            if (ImGui.button("Create object")) {
                var object = new GameObject();
                scene.addGameObject(object);
                scene.getHierarchy().addChild(gameObject, object);

                ImGui.closeCurrentPopup();
            }

            ImGui.endPopup();
        }

        if (gameObject != null && ImGui.beginDragDropSource()) {
                ImGui.setDragDropPayload("game_object", gameObject);
                ImGui.text(gameObject.getName());

                ImGui.endDragDropSource();

        }

        if (ImGui.beginDragDropTarget()) {
            var payload = (GameObject) ImGui.acceptDragDropPayload("game_object");
            if (payload != null)
                scene.getHierarchy().setParent(payload, gameObject);

            ImGui.endDragDropTarget();
        }

        if (ImGui.getDragDropPayload() != null && ImGui.isItemHovered(ImGuiHoveredFlags.AllowWhenBlockedByActiveItem | (1 << 17)))
            scene.setSelectedGameObject(gameObject);

        if (ImGui.isItemClicked() && !ImGui.isItemToggledOpen() && ImGui.getDragDropPayload() == null)
            scene.setSelectedGameObject(gameObject);

        if (open) {
            for (var element : children) {
                ImGuiScene.drawHierarchyTreeRecursive(scene, element);
            }
            ImGui.treePop();
        }

        ImGui.popID();
    }
}

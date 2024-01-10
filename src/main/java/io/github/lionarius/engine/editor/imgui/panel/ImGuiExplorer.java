package io.github.lionarius.engine.editor.imgui.panel;

import imgui.ImGui;
import imgui.flag.ImGuiMouseButton;
import io.github.lionarius.Logibuild;
import io.github.lionarius.engine.resource.impl.texture.Texture;
import io.github.lionarius.engine.resource.impl.texture.TextureCreateParameters;
import org.apache.commons.io.FilenameUtils;

import java.io.File;

public class ImGuiExplorer {
    private static final float CELL_SIZE = 105.0f;
    private static final float THUMBNAIL_SIZE = 90.0f;

    private final File rootPath;
    private File currentPath;

    public ImGuiExplorer(File rootPath) {
        this.rootPath = rootPath;
        this.currentPath = rootPath;
    }

    public void drawExplorer() {
        this.drawCurrentDirectory();

        var panelWidth = ImGui.getContentRegionAvailX();
        var columns = (int) (panelWidth / CELL_SIZE);
        if (columns < 1)
            columns = 1;
        ImGui.columns(columns, "files", false);

        var list = this.currentPath.listFiles();
        assert list != null;
        for (var file : list) {
            if (file.isHidden())
                continue;

            this.drawFile(file);

            ImGui.nextColumn();
        }
    }

    private void drawFile(File file) {
        var icon = ImGuiExplorer.getFileIcon(file);

        ImGui.pushID(file.getName());

        ImGui.imageButton(icon.getId(), THUMBNAIL_SIZE, THUMBNAIL_SIZE, 0, 1, 1, 0);

        if (file.isDirectory()) {
            if (ImGui.isItemHovered() && ImGui.isMouseDoubleClicked(ImGuiMouseButton.Left))
                this.currentPath = new File(this.currentPath, file.getName());
        } else {
            if (ImGui.beginDragDropSource()) {
                ImGui.setDragDropPayload("explorer_file", file);
                ImGui.text(file.getName());
                ImGui.endDragDropSource();
            }
        }

        ImGui.textWrapped(file.getName());

        ImGui.popID();
    }

    private void drawCurrentDirectory() {
        if (!this.currentPath.equals(this.rootPath)) {
            if (ImGui.button("<-"))
                this.currentPath = this.currentPath.getParentFile();
            ImGui.sameLine();
        }
        ImGui.text("\\" + this.rootPath.toPath().relativize(this.currentPath.toPath()));
    }


    private static final Texture FILE_ICON = Logibuild.getInstance().getInternalResourceManager().get(Texture.class, "icons/explorer/file.png", TextureCreateParameters.SMOOTH);
    private static final Texture FOLDER_ICON = Logibuild.getInstance().getInternalResourceManager().get(Texture.class, "icons/explorer/folder.png", TextureCreateParameters.SMOOTH);
    private static final Texture SCENE_ICON = Logibuild.getInstance().getInternalResourceManager().get(Texture.class, "icons/explorer/scene.png", TextureCreateParameters.SMOOTH);
    private static final Texture IMAGE_ICON = Logibuild.getInstance().getInternalResourceManager().get(Texture.class, "icons/explorer/image.png", TextureCreateParameters.SMOOTH);


    private static Texture getFileIcon(File file) {
        if (file.isDirectory())
            return FOLDER_ICON;

        var extension = FilenameUtils.getExtension(file.getName());

        return switch (extension) {
            case "scene" -> SCENE_ICON;
            case "png" -> IMAGE_ICON;
            default -> FILE_ICON;
        };
    }
}

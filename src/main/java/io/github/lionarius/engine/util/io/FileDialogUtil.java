package io.github.lionarius.engine.util.io;

import io.github.lionarius.engine.util.buffer.BufferUtil;
import lombok.experimental.UtilityClass;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

import java.io.File;

@UtilityClass
public class FileDialogUtil {
    public static File saveFileDialog(String title, File defaultPath, String defaultFilename, String[] filters, String fileDescription) {
        var file = new File(defaultPath, defaultFilename);
        var filtersBuffer = BufferUtil.stringArrayToPointerBuffer(filters);
        var result = TinyFileDialogs.tinyfd_saveFileDialog(title, file.getAbsolutePath(), filtersBuffer, fileDescription);

        if (result == null)
            return null;
        return new File(result);
    }

    public static File[] openFileDialog(String title, File defaultPath, String[] filters, String fileDescription, boolean multipleSelect) {
        var filtersBuffer = BufferUtil.stringArrayToPointerBuffer(filters);
        var selected = TinyFileDialogs.tinyfd_openFileDialog(title, defaultPath.getAbsolutePath() + File.separatorChar, filtersBuffer, fileDescription, multipleSelect);
        if (selected == null)
            return null;

        var paths = selected.split("\\|");
        var result = new File[paths.length];
        for (int i = 0; i < paths.length; i++) {
            var path = paths[i];
            result[i] = new File(path);
        }

        return result;
    }
}

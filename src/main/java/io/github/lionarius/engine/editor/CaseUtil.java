package io.github.lionarius.engine.editor;

import lombok.experimental.UtilityClass;

import java.util.regex.Pattern;

@UtilityClass
public class CaseUtil {
    private static final Pattern UPPERCASE_LETTER = Pattern.compile("([A-Z]{2,})|([A-Z]|[0-9]+)");

    public static String toSentenceCase(String camelCaseString) {
        return camelCaseString.substring(0, 1).toUpperCase()
               + UPPERCASE_LETTER.matcher(camelCaseString.substring(1))
                       .replaceAll(m -> m.group(1) != null ? " " + m.group(1) : " " + m.group(2).toLowerCase());
    }
}

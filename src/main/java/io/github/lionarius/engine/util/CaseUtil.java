package io.github.lionarius.engine.util;

import lombok.experimental.UtilityClass;

import java.util.regex.Pattern;

@UtilityClass
public class CaseUtil {
    private static final Pattern UPPERCASE_LETTER = Pattern.compile("([A-Z]{2,})(?=[A-Z0-9]|$)|([A-Z])(?=[a-z0-9]|$)|\\d+");

    public static String toSentenceCase(String camelCaseString) {
        return camelCaseString.substring(0, 1).toUpperCase()
               + UPPERCASE_LETTER.matcher(camelCaseString.substring(1))
                       .replaceAll(m -> " " + m.group().toLowerCase());
    }
}

package io.github.lionarius.engine.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class TimeUtil {
    private static final long START_TIME = System.nanoTime();

    public static double getApplicationTime() {
        return (double) (System.nanoTime() - START_TIME) * 1E-9;
    }
}

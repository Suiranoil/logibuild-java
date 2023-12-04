package io.github.lionarius.engine.util;

import lombok.experimental.UtilityClass;
import org.joml.Matrix4f;

@UtilityClass
public class ProjectionUtil {

	public static Matrix4f getOrthoProjection(float left, float right, float top, float bottom, float far, float near) {
		return new Matrix4f(
				2.0f / (right - left), 0.0f, 0.0f, 0.0f,
				0.0f, 2.0f / (top - bottom), 0.0f, 0.0f,
				0.0f, 0.0f, 2.0f / (far - near), 0.0f,
				-(right + left) / (right - left), -(top + bottom) / (top - bottom), -(far + near) / (far - near), 1.0f
		);
	}

	public static Matrix4f getOrthoProjection(float x, float y, float width, float height) {
		return ProjectionUtil.getOrthoProjection(x, x + width, y, y + height, 1, -1);
	}

	public static Matrix4f getOrthoProjectionCentered(float centerX, float centerY, float width, float height) {
		return ProjectionUtil.getOrthoProjection(centerX - width / 2, centerY - height / 2, width, height);
	}
}

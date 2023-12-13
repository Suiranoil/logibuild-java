package io.github.lionarius.engine.resource.shader;

import io.github.lionarius.engine.renderer.OpenGLObject;
import io.github.lionarius.engine.resource.Resource;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL46;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class Shader extends OpenGLObject implements Resource {
    @Getter @Setter
    private String resourceName;

    private static final Logger LOGGER = LogManager.getLogger("Shader");
    private final String vertexSource;
    private final String fragmentSource;
    private final Map<String, Integer> uniforms = new HashMap<>();

    @Override
    public void bind() {
        GL46.glUseProgram(this.id);
    }

    @Override
    public void unbind() {
        GL46.glUseProgram(0);
    }

    @Override
    public void close() {
        GL46.glDeleteProgram(this.id);
    }

    public void setUniform(String name, int value) {
        var location = this.getUniformLocation(name);
        if (location != -1)
            GL46.glProgramUniform1i(this.id, location, value);
    }

    public void setUniform(String name, int[] value) {
        var location = this.getUniformLocation(name);
        if (location != -1)
            GL46.glProgramUniform1iv(this.id, location, value);
    }

    public void setUniform(String name, float value) {
        var location = this.getUniformLocation(name);
        if (location != -1)
            GL46.glProgramUniform1f(this.id, location, value);
    }

    public void setUniform(String name, Vector2fc value) {
        var location = this.getUniformLocation(name);
        if (location != -1)
            GL46.glProgramUniform2f(this.id, location, value.x(), value.y());
    }

    public void setUniform(String name, Vector3fc value) {
        var location = this.getUniformLocation(name);
        if (location != -1)
            GL46.glProgramUniform3f(this.id, location, value.x(), value.y(), value.z());
    }

    public void setUniform(String name, Vector4fc value) {
        var location = this.getUniformLocation(name);
        if (location != -1)
            GL46.glProgramUniform4f(this.id, location, value.x(), value.y(), value.z(), value.w());
    }

    public void setUniform(String name, Matrix3fc value) {
        var location = this.getUniformLocation(name);
        var buffer = BufferUtils.createFloatBuffer(3 * 3);
        value.get(buffer);
        if (location != -1)
            GL46.glProgramUniformMatrix3fv(this.id, location, false, buffer);
    }

    public void setUniform(String name, Matrix4fc value) {
        var location = this.getUniformLocation(name);
        var buffer = BufferUtils.createFloatBuffer(4 * 4);
        value.get(buffer);
        if (location != -1)
            GL46.glProgramUniformMatrix4fv(this.id, location, false, buffer);
    }

    protected void init() {
        int vertexId = Shader.compileSource(this.vertexSource, GL46.GL_VERTEX_SHADER);
        int fragmentId = Shader.compileSource(this.fragmentSource, GL46.GL_FRAGMENT_SHADER);

        this.id = Shader.linkProgram(vertexId, fragmentId);
    }

    private static int compileSource(String source, int type) {
        int id;
        id = GL46.glCreateShader(type);
        GL46.glShaderSource(id, source);
        GL46.glCompileShader(id);
        int success = GL46.glGetShaderi(id, GL46.GL_COMPILE_STATUS);
        if (success == GL46.GL_FALSE) {
            String info = GL46.glGetShaderInfoLog(id);
            LOGGER.error("Error compiling shader source: {}", info);
            throw new IllegalStateException("Error compiling shader source");
        }

        return id;
    }

    private static int linkProgram(int vertexId, int fragmentId) {
        int id = GL46.glCreateProgram();
        GL46.glAttachShader(id, vertexId);
        GL46.glAttachShader(id, fragmentId);
        GL46.glLinkProgram(id);

        int success = GL46.glGetProgrami(id, GL46.GL_LINK_STATUS);
        if (success == GL46.GL_FALSE) {
            String info = GL46.glGetProgramInfoLog(id);
            LOGGER.error("Error linking program: {}", info);

            throw new IllegalStateException("Error linking program");
        }

        GL46.glValidateProgram(id);
        success = GL46.glGetProgrami(id, GL46.GL_VALIDATE_STATUS);
        if (success == GL46.GL_FALSE) {
            String info = GL46.glGetProgramInfoLog(id);
            LOGGER.error("Error validating program: {}", info);

            throw new IllegalStateException("Error validating program");
        }

        GL46.glDeleteShader(vertexId);
        GL46.glDeleteShader(fragmentId);

        return id;
    }

    private int getUniformLocation(String name) {
        if (this.uniforms.containsKey(name))
            return this.uniforms.get(name);

        var location = GL46.glGetUniformLocation(this.id, name);
        this.uniforms.put(name, location);
        return location;
    }
}

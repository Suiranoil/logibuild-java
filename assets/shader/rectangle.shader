// #type vertex
#version 460 core
layout (location = 0) in vec4 a_Position;

uniform mat4 u_View;

void main()
{
    gl_Position = u_View * a_Position;
}

// #type fragment
#version 460 core

out vec4 color;

void main()
{
    color = vec4(1.0, 0.0, 0.0, 1.0);
}
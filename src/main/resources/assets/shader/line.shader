// #type vertex
#version 460 core
layout (location = 0) in vec4 a_Position;
layout (location = 1) in vec4 a_Color;

uniform mat4 u_Projection;
uniform mat4 u_View;

layout (location = 0) out vec4 o_Color;

void main()
{
    o_Color = a_Color;

    gl_Position = u_Projection * u_View * a_Position;
}

// #type fragment
#version 460 core

layout (location = 0) in vec4 i_Color;

out vec4 FragColor;

void main()
{
    FragColor = i_Color;
    if (FragColor.a == 0)
    discard;
}
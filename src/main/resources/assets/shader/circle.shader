// #type vertex
#version 460 core
layout (location = 0) in vec4 a_Position;
layout (location = 1) in vec4 a_Color;
layout (location = 2) in mat4 a_Model;

uniform mat4 u_Projection;
uniform mat4 u_View;

layout (location = 0) out vec2 o_Position;
layout (location = 1) out vec4 o_Color;

void main()
{
    o_Position = a_Position.xy - vec2(0.5f);
    o_Color = a_Color;

    gl_Position = u_Projection * u_View * a_Model * a_Position;
}

// #type fragment
#version 460 core

layout (location = 0) in vec2 i_Position;
layout (location = 1) in vec4 i_Color;

out vec4 gl_FragColor;

void main()
{
    float radius = 4 * dot(i_Position, i_Position);
    if (radius > 1)
    discard;
    float delta = fwidth(radius);
    float opacity = smoothstep(1.0, 1.0 - 2 * delta, radius);
    gl_FragColor = mix(vec4(0), i_Color, opacity);
}
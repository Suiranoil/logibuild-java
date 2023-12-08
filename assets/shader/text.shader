// #type vertex
#version 460 core
layout (location = 0) in vec4 a_Position;
layout (location = 1) in vec2 a_UV;
layout (location = 2) in vec4 a_Color;
layout (location = 3) in mat4 a_Model;

uniform mat4 u_Projection;
uniform mat4 u_View;

layout (location = 0) out vec4 o_Color;
layout (location = 1) out vec2 o_UV;

void main()
{
    o_Color = a_Color;
    o_UV = a_UV;

    gl_Position = u_Projection * u_View * a_Model * a_Position;
}

// #type fragment
#version 460 core

layout (location = 0) in vec4 i_Color;
layout (location = 1) in vec2 i_UV;

uniform vec2 u_unitRange;
uniform sampler2D u_Atlas;

out vec4 FragColor;

float median(float r, float g, float b) {
    return max(min(r, g), min(max(r, g), b));
}

float screenPxRange() {
    vec2 screenTexSize = vec2(1.0) / fwidth(i_UV);
    return max(0.5 * dot(u_unitRange, screenTexSize), 1.0);
}

void main()
{
    vec3 msd = texture(u_Atlas, i_UV).rgb;
    float sd = median(msd.r, msd.g, msd.b);
    float screenPxDistance = screenPxRange() * (sd - 0.5);
    float opacity = clamp(screenPxDistance + 0.5, 0.0, 1.0);
    //    FragColor = mix(vec4(0), i_Color, opacity);
    FragColor = vec4(1);
}
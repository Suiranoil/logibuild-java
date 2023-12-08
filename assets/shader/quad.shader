// #type vertex
#version 460 core
layout (location = 0) in vec4 a_Position;
layout (location = 1) in vec2 a_UV;
layout (location = 2) in vec4 a_Color;
layout (location = 3) in int a_TextureId;
layout (location = 4) in mat4 a_Model;

uniform mat4 u_Projection;
uniform mat4 u_View;

layout (location = 0) out vec4 o_Color;
layout (location = 1) out vec2 o_UV;
layout (location = 2) flat out int o_TextureId;

void main()
{
    o_Color = a_Color;
    o_UV = a_UV;
    o_TextureId = a_TextureId;

    gl_Position = u_Projection * u_View * a_Model * a_Position;
}

// #type fragment
#version 460 core

layout (location = 0) in vec4 i_Color;
layout (location = 1) in vec2 i_UV;
layout (location = 2) flat in int i_TextureId;

//uniform sampler2D u_Texture[8];

out vec4 FragColor;

void main()
{
    FragColor = i_Color;
    //if (i_TextureId != -1)
    //FragColor *= texture(u_Texture[i_TextureId], i_UV);
}
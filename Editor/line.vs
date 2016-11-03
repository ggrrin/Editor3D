#version 330 core

uniform mat4 MVP;

layout(location = 0) in vec3 position;
layout(location = 1) in vec3 b_color;

out vec3 fColor;

void main(){

	fColor = b_color;
    gl_Position =  MVP * vec4(position,1);
}


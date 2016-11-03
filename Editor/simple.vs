#version 330 core

uniform mat4 MVP;
uniform vec3 param;
uniform vec3 color;

layout(location = 0) in vec3 position;
//layout(location = 1) in vec3 b_color;

out vec3 vColor;
out float radius;
out float aspect;

void main(){
    aspect = param.y;
    radius = 6* param.x / (param.z / 2.0);    
    vColor = color;
    gl_Position =  MVP * vec4(position,1);
}


#version 330 core

uniform mat4 MVP;

layout(location = 0) in vec3 position;
layout(location = 1) in vec3 color;

//uniform bool globalColor;

uniform vec3 meshColor;
uniform vec3 wireColor;

out VertexData {
    vec3 color;
    vec3 vBC;
    vec3 wColor;
} VertexOut;

void main(){
	if(meshColor == vec3(-1))
		VertexOut.color = color;
	else
		VertexOut.color = meshColor;

        VertexOut.wColor = wireColor;
		
    gl_Position =  MVP * vec4(position,1);
}


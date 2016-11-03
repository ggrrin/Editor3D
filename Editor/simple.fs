#version 330 core

in vec3 fColor;

void main()
{	
	gl_FragColor.rgb = fColor;
	gl_FragColor.a = 1;
}
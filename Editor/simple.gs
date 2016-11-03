#version 150
 
layout(points) in;
layout (triangle_strip, max_vertices=21) out;
 

in vec3 vColor[];
in float radius[];
in float aspect[];

out vec3 fColor;
 
const float PI = 3.1415926;
 
void main()
{ 
	fColor = vColor[0];
	
	int count = 10;
	float r = radius[0];
	
	//vec4 offset1 = vec4(cos(0) * r , -sin(0) * r * aspect[0], 0.0, 0.0);
	//gl_Position = gl_in[0].gl_Position + offset1;
	//gl_Position.z = 1;
	//EmitVertex();
	
    for (int i = 0; i <= count; i++) {        
        float ang = PI * 2.0 / count * i;
        
        vec4 offset = vec4(cos(ang) * r * (1/ aspect[0]) , -sin(ang) * r , 0.0, 0.0);
        gl_Position = gl_in[0].gl_Position + offset;
		gl_Position.z = 1;
        EmitVertex();
		
		gl_Position = gl_in[0].gl_Position;
		gl_Position.z = 1;
		EmitVertex();
		
    }
	
    EndPrimitive();;
	
}
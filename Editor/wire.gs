#version 150
 
layout(triangles) in;
layout (triangle_strip, max_vertices=3) out;
 
in VertexData {
	vec3 color;
        vec3 vBC;
        vec3 wColor;
} VertexIn[3];
 
out VertexData {
	vec3 color;
        vec3 vBC;
        vec3 wColor;
} VertexOut;
 
void main()
{   
    gl_Position = gl_in[0].gl_Position;
    VertexOut.color = VertexIn[0].color;
	VertexOut.vBC = vec3(1,0,0);
        VertexOut.wColor = VertexIn[0].wColor;
	EmitVertex();
		
	gl_Position = gl_in[1].gl_Position;
    VertexOut.color = VertexIn[1].color;
	VertexOut.vBC = vec3(1,0,0);
	VertexOut.vBC = vec3(0,1,0);
        VertexOut.wColor = VertexIn[1].wColor;
	EmitVertex();
	
	gl_Position = gl_in[2].gl_Position;
    VertexOut.color = VertexIn[2].color;
	VertexOut.vBC = vec3(1,0,0);
	VertexOut.vBC = vec3(0,0,1);
        VertexOut.wColor = VertexIn[2].wColor;
	EmitVertex();
}
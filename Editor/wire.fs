#version 330 core

in VertexData {
	vec3 color;
        vec3 vBC;
        vec3 wColor;
} VertexOut;

#extension GL_OES_standard_derivatives : enable
float edgeFactor(){
	vec3 d = fwidth(VertexOut.vBC);
	vec3 a3 = smoothstep(vec3(0.0), d*0.95, VertexOut.vBC);
	return min(min(a3.x, a3.y), a3.z);
}

void main()
{	
	gl_FragColor.rgb = mix(VertexOut.wColor, VertexOut.color, edgeFactor()); 
	gl_FragColor.a = 1.0;
}
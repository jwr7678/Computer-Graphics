#version 410

out vec4 color;
in vec4 vertexColor;

uniform mat4 mv_matrix;
uniform mat4 proj_matrix;

void main(void)
{	
	//color = vec4(1.0, 0.0, 0.0, 1.0);
	color=vertexColor;
}

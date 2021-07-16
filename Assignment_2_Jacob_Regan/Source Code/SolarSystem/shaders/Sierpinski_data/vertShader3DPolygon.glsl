#version 410

layout (location=0) in vec3 position;

out vec4 vertexColor; //Create a color where R=x, G=y, B=z

uniform mat4 mv_matrix;
uniform mat4 proj_matrix;
uniform vec3 my_color;

void main(void)
{
	float length=1.5;
	gl_Position = proj_matrix * mv_matrix * vec4(position,1);
	vertexColor = vec4(my_color, 1.0);
} 

package jogl_shader_course;

import graphicslib3D.Shape3D;
import graphicslib3D.Vertex3D;

public class PentagonalPrism extends Shape3D{
	private int numVertices = 12;
	private Vertex3D[] vertices;
	private int[] indices;
	
	public PentagonalPrism() {
		
		double c1 = Math.cos((2 * Math.PI)/5);
		double c2 = Math.cos((Math.PI)/ 5);
		double s1 = Math.sin((2 * Math.PI)/ 5);
		double s2 = Math.sin((4*Math.PI)/5);
		double length = 0.5;
		
		vertices = new Vertex3D[numVertices];
		
		vertices[0] = new Vertex3D(0, 0, length);
		vertices[1] = new Vertex3D(0, 1, length);
		vertices[2] = new Vertex3D(s1, c1, length);
		vertices[3] = new Vertex3D(s2, -c2, length);
		vertices[4] = new Vertex3D(-s2, -c2, length);
		vertices[5] = new Vertex3D(-s1, c1, length);
		vertices[6] = new Vertex3D(0, 0, -length);
		vertices[7] = new Vertex3D(0, 1, -length);
		vertices[8] = new Vertex3D(s1, c1, -length);
		vertices[9] = new Vertex3D(s2, -c2, -length);
		vertices[10] = new Vertex3D(-s2, -c2, -length);
		vertices[11] = new Vertex3D(-s1, c1, -length);
		
		
		vertices[0].setST(0.5, 0.5);
		vertices[1].setST(0.5, 1);
		vertices[2].setST(1, 0.6);
		vertices[3].setST(0.8, 0);
		vertices[4].setST(0.2, 0);
		vertices[5].setST(0, 0.6);
		vertices[6].setST(0.5, 0.5);
		vertices[7].setST(0.5, 1);
		vertices[8].setST(1, 0.6);
		vertices[9].setST(0.8, 0);
		vertices[10].setST(0.2, 0);
		vertices[11].setST(0, 0.6);
		
		indices = new int[] {
				0, 1, 2, 	0, 2, 3, 	0, 3, 4, 	0, 4, 5, 	0, 5, 1,
				1, 7, 2, 	7, 8, 2,
				2, 8, 3, 	8, 9, 3,
				3, 9, 4, 	9, 10, 4,
				4, 10, 5, 	10, 11, 5,
				5, 11, 1, 	11, 7, 1,
				6, 7, 11, 	6, 11, 10, 	6, 10, 9, 	6, 9, 8, 	6, 8, 7};
		
	}

	public Vertex3D[] getVertices() {
		return vertices;
	}

	public int[] getIndices() {
		return indices;
	}
	
	
}

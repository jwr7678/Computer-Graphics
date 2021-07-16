package jogl_shader_course;

/*
 * Written by Jacob Regan
 * CS-4613 Project 1
 * Koch Snowflake
 * 
 * Snippets were used from example code provided by Dr. Mauricio Papa.
 */

import java.nio.*;
import javax.swing.*;

import static com.jogamp.opengl.GL.GL_ARRAY_BUFFER;
import static com.jogamp.opengl.GL.GL_FLOAT;
import static com.jogamp.opengl.GL.GL_STATIC_DRAW;
import static com.jogamp.opengl.GL.GL_TRIANGLES;
import static com.jogamp.opengl.GL4.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.common.nio.Buffers;
import java.util. Random;
public class KochSnowflake extends JFrame implements GLEventListener {
	private GLCanvas myCanvas;
	private int rendering_program;
	private int vao[] = new int[1];
	private int vbo[] = new int[1];
	
	//make these two parameters
	private int N; //Recursion level
	private float side_length; //Side length. 
	
	private float[] vertex_positions=new float[3*2];//Three points, two coordinates
	
	public KochSnowflake(int N, float side_length) {
		this.N = N;
		this.side_length = side_length;
		setTitle("Koch Snowflake");
		setSize(600, 600);
		GLProfile profile = GLProfile.get(GLProfile.GL4);
        GLCapabilities capabilities = new GLCapabilities(profile);
		myCanvas = new GLCanvas(capabilities);
 		//end GL4 context
		myCanvas.addGLEventListener(this);
		getContentPane().add(myCanvas);
		this.setVisible(true);
		for(int i = 0; i<=N; i++) {
			showCalculations(i);;
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int n= 3;
		float side_length = 1f;
		if(args.length >= 1)
		{
			try
			{
				n = Integer.parseInt(args[0]);
			}
			catch(NumberFormatException e)
			{
				System.out.println("Invalid input for n - depth of recursion.");
			}
		}
		if(args.length >= 2)
		{
			try
			{
				side_length = Float.parseFloat(args[1]);
			}
			catch(NumberFormatException e)
			{
				System.out.println("Invalid input for side length.");
			}
		}
		new KochSnowflake(n, side_length);

	}

	public void display(GLAutoDrawable arg0) {
		// TODO Need to make this compatible
		GL4 gl = (GL4) GLContext.getCurrentGL();
		//Define the triangle
		float[] v1=new float[2];//Two coordinates
		float[] v2=new float[2];//Two coordinates
		float[] v3=new float[2];//Two coordinates
		//The first three vertices define the starting triangle
		//Equilateral triangle centered at the origin
		//Top vertex - x and y
		v1[0]=0; 
		v1[1]=side_length*(float)Math.sqrt(3)/3;
		//Bottom left
		v2[0]=-0.5f*side_length; 
		v2[1]=-(float)Math.sqrt(3)*side_length/6;
		//Bottom right
		v3[0]=0.5f*side_length; 
		v3[1]=-(float)Math.sqrt(3)*side_length/6;
		//Done defining triangle
		
		gl.glUseProgram(rendering_program);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
		gl.glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);//We are only passing two components
		gl.glEnableVertexAttribArray(0);

		//processTriangle(v1,v2,v3,N);
		//drawTriangle(v1, v2, v3);
//		drawLine(v1, v2);
//		drawLine(v2, v3);
//		drawLine(v1, v3);
		
		performStep(1, v1, v2);
		performStep(1, v2, v3);
		performStep(1, v3, v1);
		

		
	}
	
	/*
	 * The Koch Snokeflake construction steps
	 * 1) divide the line segment into three segments of equal length.
	 * 2) draw an equilateral triangle that has the middle segment from step 1 as its base and points outward.
	 * 3) remove the line segment that is the base of the triangle from step 2.
	 * 
	 */
	private void performStep(int n, float[] v1, float[] v2) {
		if(n <= this.N) {
			float[] nv1 = new float[2];
			float[] nv2 = new float[2];
			float[] nv3 = new float[2];
			
			nv1[0] = v1[0] + ((float) 1/3) * (v2[0] - v1[0]);
			nv1[1] = v1[1] + ((float) 1/3) * (v2[1] - v1[1]);
			
			nv2[0] = v2[0] - ((float) 1/3) * (v2[0] - v1[0]);
			nv2[1] = v2[1] - ((float) 1/3) * (v2[1] - v1[1]);
			
			nv3[0] = ((float)1/2) * (v1[0] + v2[0]) + ((float) Math.sqrt(3)/6) * (v2[1] - v1[1]);
			nv3[1] = ((float)1/2) * (v1[1] + v2[1]) - ((float) Math.sqrt(3)/6) * (v2[0] - v1[0]);
			
			n++;
			performStep(n, v1, nv1);
			performStep(n, nv1, nv3);
			performStep(n, nv3, nv2);
			performStep(n, nv2, v2);
			
			
		} else {
			drawLine(v1, v2);
		}
	}

	public void dispose(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub
		
	}

	public void init(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub
		GL4 gl = (GL4) drawable.getGL();
		rendering_program = Utils.createShaderProgram(	"shaders/Sierpinski_data/vertShader2D.glsl",
														"shaders/Sierpinski_data/fragShader2D.glsl");
		gl.glGenVertexArrays(vao.length, vao, 0);
		gl.glBindVertexArray(vao[0]);
		gl.glGenBuffers(vbo.length, vbo, 0);
		
	}

	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		// TODO Auto-generated method stub
	}

	
	
	private void drawTriangle(float [] v1, float[] v2, float [] v3) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		//Store points in backing store
		vertex_positions[0]=v1[0];
		vertex_positions[1]=v1[1];
		vertex_positions[2]=v2[0];
		vertex_positions[3]=v2[1];
		vertex_positions[4]=v3[0];
		vertex_positions[5]=v3[1];

		FloatBuffer vertBuf = Buffers.newDirectFloatBuffer(vertex_positions);
		gl.glBufferData(GL_ARRAY_BUFFER, vertBuf.limit()*4, vertBuf, GL_STATIC_DRAW);

		//Draw now
		gl.glDrawArrays(GL_TRIANGLE_FAN, 0, 3);
		//gl.glDrawArrays(GL_LINE, 0, 3);

	}
	
	private void drawLine(float[] v1, float[] v2) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		//Store points in backing store
		vertex_positions[0]=v1[0];
		vertex_positions[1]=v1[1];
		vertex_positions[2]=v2[0];
		vertex_positions[3]=v2[1];
		FloatBuffer vertBuf = Buffers.newDirectFloatBuffer(vertex_positions);
		gl.glBufferData(GL_ARRAY_BUFFER, vertBuf.limit()*4, vertBuf, GL_STATIC_DRAW);
		gl.glDrawArrays(GL_LINES, 0, 2);
	}
	
	private void showCalculations(int n) {
		
		int sides_num = 3 * (int) Math.pow(4, n);
		double perimeter = sides_num * side_length / Math.pow(3, n);
		
		double area = (Math.pow(side_length,2f) * Math.sqrt(3f)/4f) * (.2f)*(8f-3f* Math.pow(4f/9f, n));
		System.out.println("Iteration (n): " + n);
		System.out.println("Number of segments: " + (int)sides_num);
		System.out.printf("Perimeter: %.4f\n", perimeter);
		System.out.printf("Area: %.10f\n", area);
		
		System.out.println("----------");
	}

}

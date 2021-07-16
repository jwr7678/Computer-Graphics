package jogl_shader_course;
/*
 * By Jacob Regan
 * 
 * Includes snippets from JOML and course content by Professor Papa
 */

import java.nio.*;
import javax.swing.*;

import static com.jogamp.opengl.GL.GL_ARRAY_BUFFER;
import static com.jogamp.opengl.GL.GL_DEPTH_BUFFER_BIT;
import static com.jogamp.opengl.GL.GL_FLOAT;
import static com.jogamp.opengl.GL.GL_STATIC_DRAW;
import static com.jogamp.opengl.GL.GL_TEXTURE0;
import static com.jogamp.opengl.GL.GL_TEXTURE_2D;
import static com.jogamp.opengl.GL.GL_TRIANGLES;
import static com.jogamp.opengl.GL2ES3.GL_COLOR;
import static com.jogamp.opengl.GL4.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.texture.*;
import com.jogamp.common.nio.Buffers;
import org.joml.*;
import org.joml.Math;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import graphicslib3D.*;
import graphicslib3D.shape.Sphere;

public class SolarSystem extends JFrame implements GLEventListener, KeyListener {
	private GLCanvas myCanvas;
	private int renderingProgram;
	private int vao[] = new int[1];
	private int vbo[] = new int[21];
	private float cameraX, cameraY, cameraZ;
//	private float sphLocX, sphLocY, sphLocZ;
	private Camera camera;
	private boolean showAxis = true;
	private MatrixStack mvStack = new MatrixStack(21);
	
	private float sunLocX, sunLocY, sunLocZ;
	private Sphere s_sun, s_planetOne, s_planetTwo, s_moonOne, s_moonTwo;
	private PentagonalPrism prismPlanet;
	
	private int sunTex, planetOneTex, planetTwoTex, moonOneTex, moonTwoTex;
	private int redTex, blueTex, greenTex;
	private int prismTex;
	
	
	public SolarSystem() {
		setTitle("Jacob Regan's Solar System");
		setSize(1600, 800);
		myCanvas = new GLCanvas();
        myCanvas.addGLEventListener(this);
        getContentPane().add(myCanvas);
        this.setVisible(true);
        
        
        FPSAnimator animator = new FPSAnimator(myCanvas, 50);
        animator.start();
        
        setFocusable(true);
        requestFocus();
        addKeyListener(this);
        camera = new Camera();
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new SolarSystem();

	}

	public void display(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub
		GL4 gl = (GL4) GLContext.getCurrentGL();
//		camera.updateInput();
		
		gl.glClear(GL_DEPTH_BUFFER_BIT);
        float bkg[] = { 0.0f, 0.0f, 0.0f, 1.0f };
        FloatBuffer bkgBuffer = Buffers.newDirectFloatBuffer(bkg);
        
        gl.glClearBufferfv(GL_COLOR, 0, bkgBuffer);

        gl.glClear(GL_DEPTH_BUFFER_BIT);
        
        gl.glUseProgram(renderingProgram);

        int mvLoc = gl.glGetUniformLocation(renderingProgram, "mv_matrix");
        int projLoc = gl.glGetUniformLocation(renderingProgram, "proj_matrix");
        
        float aspect = (float) myCanvas.getWidth() / (float) myCanvas.getHeight();
        Matrix3D pMat = perspective(60.0f, aspect, 0.1f, 1000.0f);
        
        Matrix3D camLook = camera.updateCam();
        
        mvStack.pushMatrix();
        mvStack.multMatrix(camLook);
//        mvStack.translate(-cameraX, -cameraY, -cameraZ);
        double amt = (double)(System.currentTimeMillis())/1000.0;
        gl.glUniformMatrix4fv(projLoc, 1, false, pMat.getFloatValues(), 0);
        
        //Based on powerpoint code
        // ---------------------- pyramid == sun
        mvStack.pushMatrix();
        mvStack.translate(sunLocX, sunLocY, sunLocZ);
        mvStack.pushMatrix();
        mvStack.rotate((System.currentTimeMillis())/10.0,1.0,0.0,0.0);
        
        gl.glUniformMatrix4fv(mvLoc, 1, false, mvStack.peek().getFloatValues(), 0);
        
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[1]);
		gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);
		
		gl.glActiveTexture(GL_TEXTURE0);
        gl.glBindTexture(GL_TEXTURE_2D, sunTex);
        
    	gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
		gl.glGenerateMipmap(GL_TEXTURE_2D);
		if(gl.isExtensionAvailable("GL_EXT_texture_filer_anisotropic"))
		{
			float max[] = new float[1];
			gl.glGetFloatv(GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, max, 0);
			gl.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAX_ANISOTROPY_EXT, max[0]);
		}
        
		gl.glEnable(GL_DEPTH_TEST);
		gl.glEnable(GL_CULL_FACE);
		gl.glFrontFace(GL_CCW);
        
        gl.glDrawArrays(GL_TRIANGLES, 0, s_sun.getIndices().length);
        mvStack.popMatrix();
//        mvStack.popMatrix();
        
        
		if(showAxis) {
        	mvStack.pushMatrix();
            gl.glUniformMatrix4fv(mvLoc, 1, false, mvStack.peek().getFloatValues(), 0);
            
            //X
            this.drawAxis(3, redTex);
            //Y
            this.drawAxis(4, greenTex);
            //Z
            this.drawAxis(5, blueTex);
            mvStack.popMatrix();
        }
		
		//Planet One
        mvStack.pushMatrix();
		mvStack.translate(Math.sin(amt) *5.0f, 0.0f, Math.cos(amt) * 5.0f);
		mvStack.pushMatrix();
//		mvStack.rotate(((System.currentTimeMillis()) / 50.0) % 360, 0.0, 1.0, 0.0);
		mvStack.rotate((System.currentTimeMillis())/10.0,0.5,0.0,0.0);
		mvStack.scale(0.6, 0.6, 0.6);
		
		// Pass the model-view matrix to a uniform in the shader.
		gl.glUniformMatrix4fv(mvLoc, 1, false, mvStack.peek().getFloatValues(), 0);
		
		// Bind the vertex buffer to a vertex attribute.
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[6]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);
		
		// Bind the texture buffer to a vertex attribute.
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[7]);
		gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);
		
		// Set up texture.
		gl.glActiveTexture(GL_TEXTURE0);
		gl.glBindTexture(GL_TEXTURE_2D, planetOneTex);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
		gl.glGenerateMipmap(GL_TEXTURE_2D);
		if(gl.isExtensionAvailable("GL_EXT_texture_filer_anisotropic"))
		{
			float max[] = new float[1];
			gl.glGetFloatv(GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, max, 0);
			gl.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAX_ANISOTROPY_EXT, max[0]);
		}
		
		// Enable depth test and face-culling.
		gl.glEnable(GL_DEPTH_TEST);
		gl.glEnable(GL_CULL_FACE);
		gl.glFrontFace(GL_CCW);
		
		// Draw the object.
		gl.glDrawArrays(GL_TRIANGLES, 0, s_planetOne.getIndices().length);
		mvStack.popMatrix();
		
		//---Planet 1 Moon
		mvStack.pushMatrix();
        mvStack.translate(0.0f, Math.sin(amt)*1.5f, Math.cos(amt)*1.5f);
        mvStack.rotate((System.currentTimeMillis())/10.0,1.0,0.0,1.0);
        mvStack.scale(0.25, 0.25, 0.25);
        gl.glUniformMatrix4fv(mvLoc, 1, false, mvStack.peek().getFloatValues(), 0);

        gl.glActiveTexture(GL_TEXTURE0);
        gl.glBindTexture(GL_TEXTURE_2D, moonOneTex);
        gl.glFrontFace(GL_CCW);
        gl.glDrawArrays(GL_TRIANGLES, 0, s_moonOne.getIndices().length);
        mvStack.popMatrix();
        mvStack.popMatrix();
        
      //Planet Two
        mvStack.pushMatrix();
		mvStack.translate(Math.sin(amt/2f) *9.0f, 0.0f, Math.cos(amt/2.0f) * 9.0f);
		mvStack.pushMatrix();
		mvStack.rotate((System.currentTimeMillis())/50.0,1,0.0,0.0);
		mvStack.scale(0.8, 0.8, 0.8);
		
		// Pass the model-view matrix to a uniform in the shader.
		gl.glUniformMatrix4fv(mvLoc, 1, false, mvStack.peek().getFloatValues(), 0);
		
		// Bind the vertex buffer to a vertex attribute.
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[12]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);
		
		// Bind the texture buffer to a vertex attribute.
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[13]);
		gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);
		
		// Set up texture.
		gl.glActiveTexture(GL_TEXTURE0);
		gl.glBindTexture(GL_TEXTURE_2D, planetTwoTex);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
		gl.glGenerateMipmap(GL_TEXTURE_2D);
		if(gl.isExtensionAvailable("GL_EXT_texture_filer_anisotropic"))
		{
			float max[] = new float[1];
			gl.glGetFloatv(GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, max, 0);
			gl.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAX_ANISOTROPY_EXT, max[0]);
		}
		
		gl.glEnable(GL_DEPTH_TEST);
		gl.glEnable(GL_CULL_FACE);
		gl.glFrontFace(GL_CCW);
		gl.glDrawArrays(GL_TRIANGLES, 0, s_planetTwo.getIndices().length);
		mvStack.popMatrix();
		
		
		//---Planet 2 Moon
		mvStack.pushMatrix();
        mvStack.translate(Math.sin(amt*1.5)*2f, Math.cos(amt*1.5)*1.5f,0.0f );
        mvStack.rotate((System.currentTimeMillis())/10.0,1.0,0.0,1.0);
        mvStack.scale(0.35, 0.35, 0.35);
        gl.glUniformMatrix4fv(mvLoc, 1, false, mvStack.peek().getFloatValues(), 0);

        gl.glActiveTexture(GL_TEXTURE0);
        gl.glBindTexture(GL_TEXTURE_2D, moonTwoTex);
        gl.glFrontFace(GL_CCW);
        gl.glDrawArrays(GL_TRIANGLES, 0, s_moonTwo.getIndices().length);
		mvStack.popMatrix();
		mvStack.popMatrix();
        
        //Pentagonal Prism
        mvStack.pushMatrix();
		mvStack.translate(Math.sin(amt) *11.0f, 0.0f, Math.cos(amt) * 11.0f);
		mvStack.pushMatrix();
		mvStack.rotate((System.currentTimeMillis())/50.0,1,0.0,0.0);
		mvStack.scale(0.8, 0.8, 0.8);
		
		// Pass the model-view matrix to a uniform in the shader.
		gl.glUniformMatrix4fv(mvLoc, 1, false, mvStack.peek().getFloatValues(), 0);
		
		// Bind the vertex buffer to a vertex attribute.
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[18]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);
		
		// Bind the texture buffer to a vertex attribute.
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[19]);
		gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);
		
		// Set up texture.
		gl.glActiveTexture(GL_TEXTURE0);
		gl.glBindTexture(GL_TEXTURE_2D, prismTex);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
		gl.glGenerateMipmap(GL_TEXTURE_2D);
		if(gl.isExtensionAvailable("GL_EXT_texture_filer_anisotropic"))
		{
			float max[] = new float[1];
			gl.glGetFloatv(GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, max, 0);
			gl.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAX_ANISOTROPY_EXT, max[0]);
		}
		
		gl.glEnable(GL_DEPTH_TEST);
		gl.glEnable(GL_CULL_FACE);
		gl.glFrontFace(GL_CCW);
		gl.glDrawArrays(GL_TRIANGLES, 0, prismPlanet.getIndices().length);
		mvStack.popMatrix();
		mvStack.popMatrix();
        
        mvStack.popMatrix();
		mvStack.popMatrix();
        

	}
	
	private void drawAxis(int vbo_i, int tex) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[vbo_i]);
        gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(0);
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[vbo_i]);
        gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(1);
        gl.glActiveTexture(GL_TEXTURE0);
        gl.glBindTexture(GL_TEXTURE_2D, tex);
        gl.glDrawArrays(GL_LINES, 0, 2);
        gl.glDrawArrays(GL_LINES, 0, 2);
	}

	public void dispose(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub
		
	}

	public void init(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub
		GL4 gl = (GL4) GLContext.getCurrentGL();
        renderingProgram = Utils.createShaderProgram("src/jogl_shader_course/vertShader.glsl", "src/jogl_shader_course/fragShader.glsl");
        s_sun = new Sphere(24);
        s_planetOne = new Sphere(24);
        s_moonOne = new Sphere(24);
        s_planetTwo = new Sphere(24);
        s_moonTwo = new Sphere(24);
        prismPlanet = new PentagonalPrism();
        
        cameraX = -10.0f; cameraY = 4.0f; cameraZ = 0f;
        sunLocX =0f; sunLocY = 0f; sunLocZ = 0f;
        camera.setMyEye(new Vector3D(cameraX, cameraY, cameraZ));
        setupVertices();
        sunTex = Utils.loadTexture("src/Resources/sun.jpg");
        planetOneTex = Utils.loadTexture("src/Resources/earth.jpg");
        redTex = Utils.loadTexture("src/Resources/red.jpg");
        greenTex = Utils.loadTexture("src/Resources/green.jpg");
        blueTex = Utils.loadTexture("src/Resources/blue.jpg");
        moonOneTex = Utils.loadTexture("src/Resources/moonOne.jpg");
        planetTwoTex = Utils.loadTexture("src/Resources/planetTwo.jpg");
        moonTwoTex = Utils.loadTexture("src/Resources/moonTwo.jpg");
        prismTex = Utils.loadTexture("src/Resources/me.jpg");
	}

	public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3, int arg4) {
		// TODO Auto-generated method stub
		
	}
	
	
	private void setupVertices(){
		GL4 gl = (GL4) GLContext.getCurrentGL();

	    gl.glGenVertexArrays(vao.length, vao, 0);
		gl.glBindVertexArray(vao[0]);
		gl.glGenBuffers(vbo.length, vbo, 0);
		
		
		float[] x_axis = {
                0.0f, 0.0f, 0.0f, 200.0f, 0.0f, 0.0f,
        };
        float[] y_axis = {
                0.0f, 0.0f, 0.0f, 0.0f, 200.0f, 0.0f,
        };
        float[] z_axis = {
                0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -200.0f
        };
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[3]);
        FloatBuffer xAxisBuf = Buffers.newDirectFloatBuffer(x_axis);
        gl.glBufferData(GL_ARRAY_BUFFER, xAxisBuf.limit()*4, xAxisBuf, GL_STATIC_DRAW);
        
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[4]);
        FloatBuffer yAxisBuf = Buffers.newDirectFloatBuffer(y_axis);
        gl.glBufferData(GL_ARRAY_BUFFER, yAxisBuf.limit()*4, yAxisBuf, GL_STATIC_DRAW);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[5]);
        FloatBuffer zAxisBuf = Buffers.newDirectFloatBuffer(z_axis);
        gl.glBufferData(GL_ARRAY_BUFFER, zAxisBuf.limit()*4, zAxisBuf, GL_STATIC_DRAW);
		
		this.initSphereVertices(s_sun, 0);
        this.initSphereVertices(s_planetOne, 6);
        this.initSphereVertices(s_moonOne, 9);
        this.initSphereVertices(s_planetTwo, 12);
        this.initSphereVertices(s_moonTwo, 15);
        this.initPrismVertices(prismPlanet, 18);
        
	}
	private void initPrismVertices(PentagonalPrism myPrism, int vbo_i) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		Vertex3D[] vert = myPrism.getVertices();
		int[] indices = myPrism.getIndices();
		
		
		float[] pvalues = new float[indices.length*3];
		float[] tvalues = new float[indices.length*2];
		
		for (int i=0; i<indices.length; i++)
		{	
			pvalues[i*3] = (float) (vert[indices[i]]).getX();
			pvalues[i*3+1] = (float) (vert[indices[i]]).getY();
			pvalues[i*3+2] = (float) (vert[indices[i]]).getZ();
			tvalues[i*2] = (float) (vert[indices[i]]).getS();
			tvalues[i*2+1] = (float) (vert[indices[i]]).getT();

		}
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[vbo_i]);
		FloatBuffer vertBuf = Buffers.newDirectFloatBuffer(pvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, vertBuf.limit()*4, vertBuf, GL_STATIC_DRAW);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[vbo_i + 1]);
		FloatBuffer texBuf = Buffers.newDirectFloatBuffer(tvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, texBuf.limit()*4, texBuf, GL_STATIC_DRAW);

	}
	private void initSphereVertices(Sphere mySphere, int vbo_i) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		Vertex3D[] vert = mySphere.getVertices();
		int[] indices = mySphere.getIndices();
		
		
		float[] pvalues = new float[indices.length*3];
		float[] tvalues = new float[indices.length*2];
		float[] nvalues = new float[indices.length*3];
		
		for (int i=0; i<indices.length; i++)
		{	
			Vector3D norm = (vert[indices[i]]).getNormal();
			pvalues[i*3] = (float) (vert[indices[i]]).getX();
			pvalues[i*3+1] = (float) (vert[indices[i]]).getY();
			pvalues[i*3+2] = (float) (vert[indices[i]]).getZ();
			tvalues[i*2] = (float) (vert[indices[i]]).getS();
			tvalues[i*2+1] = (float) (vert[indices[i]]).getT();
			nvalues[i*3] = (float)(norm.getX());
			nvalues[i*3+1]= (float)(norm.getY());
			nvalues[i*3+2]=(float)(norm.getZ());
		}
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[vbo_i]);
		FloatBuffer vertBuf = Buffers.newDirectFloatBuffer(pvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, vertBuf.limit()*4, vertBuf, GL_STATIC_DRAW);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[vbo_i + 1]);
		FloatBuffer texBuf = Buffers.newDirectFloatBuffer(tvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, texBuf.limit()*4, texBuf, GL_STATIC_DRAW);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[vbo_i + 2]);
		FloatBuffer norBuf = Buffers.newDirectFloatBuffer(nvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, norBuf.limit()*4,norBuf, GL_STATIC_DRAW);
		
	}
	
	private Matrix3D perspective(float fovy, float aspect, float n, float f)
	{	float q = 1.0f / ((float) Math.tan(Math.toRadians(0.5f * fovy)));
		float A = q / aspect;
		float B = (n + f) / (n - f);
		float C = (2.0f * n * f) / (n - f);
		Matrix3D r = new Matrix3D();
		r.setElementAt(0,0,A);
		r.setElementAt(1,1,q);
		r.setElementAt(2,2,B);
		r.setElementAt(3,2,-1.0f);
		r.setElementAt(2,3,C);
		r.setElementAt(3, 3, 0.0f);
		return r;
	}

	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
	}

	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		//Calculate camera forward, left and up vector
		
		int key = e.getKeyCode();
		
		if(key == KeyEvent.VK_W) {
			camera.setMoveForward(true);
		} else if(key == KeyEvent.VK_S) {
			camera.setMoveBack(true);
		} else if(key == KeyEvent.VK_A) {
			camera.setStrafeLeft(true);
		} else if(key == KeyEvent.VK_D) {
			camera.setStrafeRight(true);
		} else if(key == KeyEvent.VK_E) {
			camera.setMoveDown(true);
		} else if(key == KeyEvent.VK_Q) {
			camera.setMoveUp(true);
		} else if(key == KeyEvent.VK_LEFT) {
			camera.setPanLeft(true);
		} else if(key == KeyEvent.VK_RIGHT) {
			camera.setPanRight(true);
		} else if(key == KeyEvent.VK_UP) {
			camera.setPitchUp(true);
		} else if(key == KeyEvent.VK_DOWN) {
			camera.setPitchDown(true);
		} else if(key == KeyEvent.VK_SPACE) {
			showAxis = !showAxis;
		}
		
	}

	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();
		
		if(key == KeyEvent.VK_W) {
			camera.setMoveForward(false);
		} else if(key == KeyEvent.VK_S) {
			camera.setMoveBack(false);
		} else if(key == KeyEvent.VK_A) {
			camera.setStrafeLeft(false);
		} else if(key == KeyEvent.VK_D) {
			camera.setStrafeRight(false);
		} else if(key == KeyEvent.VK_E) {
			camera.setMoveDown(false);
		} else if(key == KeyEvent.VK_Q) {
			camera.setMoveUp(false);
		} else if(key == KeyEvent.VK_LEFT) {
			camera.setPanLeft(false);
		} else if(key == KeyEvent.VK_RIGHT) {
			camera.setPanRight(false);
		} else if(key == KeyEvent.VK_UP) {
			camera.setPitchUp(false);
		} else if(key == KeyEvent.VK_DOWN) {
			camera.setPitchDown(false);
		} else if(key == KeyEvent.VK_SPACE) {
			
		}
	}

}

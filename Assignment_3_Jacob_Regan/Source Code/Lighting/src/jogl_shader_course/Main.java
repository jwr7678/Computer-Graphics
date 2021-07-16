package jogl_shader_course;
/*
 * By Jacob Regan
 * 
 * Includes snippets from JOML and course content by Professor Papa
 */

import java.nio.*;
import javax.swing.*;

import static com.jogamp.opengl.GL.GL_ARRAY_BUFFER;
import static com.jogamp.opengl.GL.GL_CCW;
import static com.jogamp.opengl.GL.GL_CULL_FACE;
import static com.jogamp.opengl.GL.GL_DEPTH_ATTACHMENT;
import static com.jogamp.opengl.GL.GL_DEPTH_BUFFER_BIT;
import static com.jogamp.opengl.GL.GL_DEPTH_COMPONENT32;
import static com.jogamp.opengl.GL.GL_DEPTH_TEST;
import static com.jogamp.opengl.GL.GL_FLOAT;
import static com.jogamp.opengl.GL.GL_FRAMEBUFFER;
import static com.jogamp.opengl.GL.GL_FRONT;
import static com.jogamp.opengl.GL.GL_LEQUAL;
import static com.jogamp.opengl.GL.GL_LINEAR;
import static com.jogamp.opengl.GL.GL_LINEAR_MIPMAP_LINEAR;
import static com.jogamp.opengl.GL.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT;
import static com.jogamp.opengl.GL.GL_NONE;
import static com.jogamp.opengl.GL.GL_POLYGON_OFFSET_FILL;
import static com.jogamp.opengl.GL.GL_STATIC_DRAW;
import static com.jogamp.opengl.GL.GL_TEXTURE0;
import static com.jogamp.opengl.GL.GL_TEXTURE1;
import static com.jogamp.opengl.GL.GL_TEXTURE_2D;
import static com.jogamp.opengl.GL.GL_TEXTURE_MAG_FILTER;
import static com.jogamp.opengl.GL.GL_TEXTURE_MAX_ANISOTROPY_EXT;
import static com.jogamp.opengl.GL.GL_TEXTURE_MIN_FILTER;
import static com.jogamp.opengl.GL.GL_TRIANGLES;
import static com.jogamp.opengl.GL2ES2.GL_COMPARE_REF_TO_TEXTURE;
import static com.jogamp.opengl.GL2ES2.GL_DEPTH_COMPONENT;
import static com.jogamp.opengl.GL2ES2.GL_FRAGMENT_SHADER;
import static com.jogamp.opengl.GL2ES2.GL_TEXTURE_COMPARE_FUNC;
import static com.jogamp.opengl.GL2ES2.GL_TEXTURE_COMPARE_MODE;
import static com.jogamp.opengl.GL2ES2.GL_VERTEX_SHADER;
import static com.jogamp.opengl.GL2ES3.GL_COLOR;
import static com.jogamp.opengl.GL2GL3.GL_DOUBLE;
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
import graphicslib3D.light.AmbientLight;
import graphicslib3D.light.PositionalLight;
import graphicslib3D.shape.Sphere;

public class Main extends JFrame implements GLEventListener, KeyListener {
	private GLCanvas myCanvas;
	private int renderingProgram0, renderingProgram1, renderingProgram2;
	private int vao[] = new int[1];
	private int vbo[] = new int[21];
	private float cameraX, cameraY, cameraZ;
//	private float sphLocX, sphLocY, sphLocZ;
	private Camera camera;
	private boolean lightOn = true;
//	private MatrixStack mvStack = new MatrixStack(21);
	
	private float sunLocX, sunLocY, sunLocZ;
	private Sphere s_sun, s_planetOne, s_planetTwo;
	private ImportedModel spaceMan;
	private int spaceManTex;
	
	private int sunTex, planetOneTex, planetTwoTex, skyBoxTex;
	
	private PositionalLight posLight = new PositionalLight();
//	private Point3D ploc = new Point3D(5.0f, 2.0f, 3.0f);
	private Point3D ploc = new Point3D(0.0f, 5.0f, 0.0f);
	float [] amb = new float[] { 0.3f, 0f, 0f, 1.0f };
	float [] dif = new float[] { 0.7f, .70f, 0.7f, 0.7f };
	float [] spec = new float[] { 1f, 1f, 1f, 0.0f };
	private float [] globalAmbient = new float[] { 0.75f, 0.75f, 0.75f, 1f };
	private int ambLoc, diffLoc, specLoc, posLoc, globalAmbLoc;
	
	private Matrix3D mMat = new Matrix3D();
	private Matrix3D vMat = new Matrix3D();
	private Matrix3D mvMat = new Matrix3D();
	private Matrix3D pMat = new Matrix3D();
	
	private int screenSizeX, screenSizeY;
	private int [] shadowTex = new int [1];
	private int [] shadowBuffer = new int [1];
	private Matrix3D lightVMatrix = new Matrix3D();
	private Matrix3D lightPMatrix = new Matrix3D();
	private Matrix3D shadowMVP = new Matrix3D();
	private Matrix3D shadowMVP2 = new Matrix3D();
	private Matrix3D b = new Matrix3D();


	
	public Main() {
		setTitle("Jacob Regan's Project 3");
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
		new Main();

	}

	public void display(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub
		GL4 gl = (GL4) GLContext.getCurrentGL();
		
		posLight.setPosition(ploc);
		
//		vMat = camera.updateCam();
		
		gl.glClear(GL_DEPTH_BUFFER_BIT);
        float bkg[] = { 0.0f, 0.0f, 0.0f, 1.0f };
        FloatBuffer bkgBuffer = Buffers.newDirectFloatBuffer(bkg);
        gl.glClearBufferfv(GL_COLOR, 0, bkgBuffer);

        gl.glClear(GL_DEPTH_BUFFER_BIT);
        
//        gl.glUseProgram(renderingProgram);


     
//        vMat = camera.updateCam();
        
		gl.glBindFramebuffer(GL_FRAMEBUFFER, shadowBuffer[0]);
		gl.glFramebufferTexture(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, shadowTex[0], 0);

		gl.glDrawBuffer(GL_NONE);
		gl.glEnable(GL_DEPTH_TEST);
		gl.glEnable(GL_POLYGON_OFFSET_FILL);
		gl.glPolygonOffset(2.0f, 4.0f);
		
		passOne();
		
		gl.glDisable(GL_POLYGON_OFFSET_FILL);
		
		// Restore the default display buffer, and re-enable drawing.
		gl.glBindFramebuffer(GL_FRAMEBUFFER, 0);
		gl.glActiveTexture(GL_TEXTURE0);
		gl.glBindTexture(GL_TEXTURE_2D, shadowTex[0]);
		// Drawing only front faces allows back face culling.
		gl.glDrawBuffer(GL_FRONT);
		
		passTwo();
		

        

	}
	
	private void passOne(){
		GL4 gl = (GL4) GLContext.getCurrentGL();
		gl.glUseProgram(renderingProgram1);
		
		Point3D origin = new Point3D(0.0, 0.0, 0.0);
		Vector3D up = new Vector3D(0.0, 1.0, 0.0);
		lightVMatrix.setToIdentity();
		lightPMatrix.setToIdentity();
		// Vector from light to origin.
		lightVMatrix = lookAtLight(posLight.getPosition(), origin, up);
		float aspect = (float) myCanvas.getWidth() / (float)myCanvas.getHeight();
		lightPMatrix = perspective(60.0f, aspect, 0.1f, 1000.0f);
		
		int shadowLoc = gl.glGetUniformLocation(renderingProgram1, "shadowMVP");
		
		//Planet One
		mMat.setToIdentity();
		mMat.translate(0, 0, 4.0f);
		mMat.rotateY((System.currentTimeMillis())/10.0 % 360);
		mMat.scale(0.6, 0.6, 0.6);
		
		
		shadowMVP.setToIdentity();
		shadowMVP.concatenate(lightPMatrix);
		shadowMVP.concatenate(lightVMatrix);
		shadowMVP.concatenate(mMat);
		gl.glUniformMatrix4fv(shadowLoc, 1, false, shadowMVP.getFloatValues(), 0);
//		// Pass the model-view matrix to a uniform in the shader.
//		gl.glUniformMatrix4fv(mvLoc, 1, false, mvStack.peek().getFloatValues(), 0);
		
		// Bind the vertex buffer to a vertex attribute.
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[6]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);	
		
		// Enable depth test and face-culling.
		gl.glEnable(GL_DEPTH_TEST);
		gl.glEnable(GL_CULL_FACE);
		gl.glFrontFace(GL_CCW);
		gl.glDepthFunc(GL_LEQUAL);
		
		// Draw the object.
		gl.glDrawArrays(GL_TRIANGLES, 0, s_planetOne.getIndices().length);
		
      //Planet Two
		mMat.setToIdentity();
		mMat.translate(0, 0, 0f);
		mMat.rotateY((System.currentTimeMillis())/10.0 % 360);
		mMat.scale(.1, .1, .1);
		
		shadowMVP.setToIdentity();
		shadowMVP.concatenate(lightPMatrix);
		shadowMVP.concatenate(lightVMatrix);
		shadowMVP.concatenate(mMat);
		gl.glUniformMatrix4fv(shadowLoc, 1, false, shadowMVP.getFloatValues(), 0);
		
		
		// Bind the vertex buffer to a vertex attribute.
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[9]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);
		
		// Bind the texture buffer to a vertex attribute.
		gl.glEnable(GL_DEPTH_TEST);
		gl.glEnable(GL_CULL_FACE);
		gl.glFrontFace(GL_CCW);
		gl.glDepthFunc(GL_LEQUAL);
		gl.glDrawArrays(GL_TRIANGLES, 0, s_planetTwo.getIndices().length);
		
      //SPaceMan
		mMat.setToIdentity();
		
		shadowMVP.setToIdentity();
		shadowMVP.concatenate(lightPMatrix);
		shadowMVP.concatenate(lightVMatrix);
		shadowMVP.concatenate(mMat);
		gl.glUniformMatrix4fv(shadowLoc, 1, false, shadowMVP.getFloatValues(), 0);
		
		
		// Bind the vertex buffer to a vertex attribute.
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[12]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);
		
		// Bind the texture buffer to a vertex attribute.
		gl.glEnable(GL_DEPTH_TEST);
		gl.glEnable(GL_CULL_FACE);
		gl.glFrontFace(GL_CCW);
		gl.glDepthFunc(GL_LEQUAL);
		gl.glDrawArrays(GL_TRIANGLES, 0, spaceMan.getVertices().length);
		

	}
	
	private void passTwo() {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		gl.glUseProgram(renderingProgram2);
		
		int mvLoc = gl.glGetUniformLocation(renderingProgram2, "mv_matrix");
        int projLoc = gl.glGetUniformLocation(renderingProgram2, "proj_matrix");
        int nLoc = gl.glGetUniformLocation(renderingProgram2, "norm_matrix");
        
        
        
        float aspect = (float) myCanvas.getWidth() / (float) myCanvas.getHeight();
        pMat = perspective(60.0f, aspect, 0.1f, 1000.0f);
//      Matrix3D camLook = camera.updateCam();
        vMat.setToIdentity();
        vMat = camera.updateCam();
        
        gl.glUniformMatrix4fv(projLoc, 1, false, pMat.getFloatValues(), 0);
        
//      mvMat.setToIdentity();
     
        gl.glUniformMatrix4fv(mvLoc, 1, false, vMat.getFloatValues(), 0);
        
        //***Sky BOX
        mMat.setToIdentity();
        Vector3D eye = camera.myEye;
        mMat.translate(eye.getX(),eye.getY(),eye.getZ());
//		mMat.translate(10,0,0);

		//  build the MODEL-VIEW matrix
		mvMat.setToIdentity();
		mvMat.concatenate(vMat);
		mvMat.concatenate(mMat);
        gl.glUniformMatrix4fv(mvLoc, 1, false, mvMat.getFloatValues(), 0);
        gl.glUniformMatrix4fv(projLoc, 1, false, pMat.getFloatValues(), 0);
		
		// Bind the vertex buffer to a vertex attribute.
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[3]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);
		
		// Bind the texture buffer to a vertex attribute.
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[4]);
		gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);
		
		gl.glActiveTexture(GL_TEXTURE1);
		gl.glBindTexture(GL_TEXTURE_2D, skyBoxTex);
		gl.glEnable(GL_CULL_FACE);
		gl.glFrontFace(GL_CCW); // Cube is CW, but we are viewing its interior.
		gl.glDisable(GL_DEPTH_TEST);
		gl.glDrawArrays(GL_TRIANGLES, 0, 36);
		gl.glEnable(GL_DEPTH_TEST);
		
		
        //Positional Light
        if(lightOn) {
        	
        	mMat.setToIdentity();
        	mMat.translate(ploc.getX(), ploc.getY(), ploc.getZ());
        	mMat.scale(0.75, 0.75, 0.75);
        	
        	mvMat.setToIdentity();
        	mvMat.concatenate(vMat);
        	mvMat.concatenate(mMat);
        	
        	posLight.setAmbient(amb);
        	posLight.setDiffuse(dif);
        	posLight.setSpecular(spec);
        	posLight.setPosition(ploc);
        	
        	gl.glUniformMatrix4fv(mvLoc, 1, false, mvMat.getFloatValues(), 0);
            
            gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
    		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
    		gl.glEnableVertexAttribArray(0);

    		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[1]);
    		gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
    		gl.glEnableVertexAttribArray(1);
    		
    		gl.glActiveTexture(GL_TEXTURE1);
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
    		gl.glFrontFace(GL_CW);
            
            gl.glDrawArrays(GL_TRIANGLES, 0, s_sun.getIndices().length);
        } else {
        	float[] turnOff = {0.0f, 0.0f, 0.0f, 0.0f};
        	posLight.setAmbient(turnOff);
        	posLight.setDiffuse(turnOff);
        	posLight.setSpecular(turnOff);
        }
        
        gl.glUseProgram(renderingProgram0);
        installLights(vMat);
        
        mvLoc = gl.glGetUniformLocation(renderingProgram0, "mv_matrix");
        projLoc = gl.glGetUniformLocation(renderingProgram0, "proj_matrix");
        nLoc = gl.glGetUniformLocation(renderingProgram0, "norm_matrix");
        int shadowLoc = gl.glGetUniformLocation(renderingProgram0, "shadowMVP");
        
        gl.glUniformMatrix4fv(projLoc, 1, false, pMat.getFloatValues(), 0);
        
        //Planet One
        mMat.setToIdentity();
		
	
		// Apply transformations to the model-view matrix.
		mvMat.setToIdentity();
		mMat.translate(0, 0.0, 3);
		mMat.rotateY(((System.currentTimeMillis()) / 50.0) % 360);
		mMat.scale(0.75, 0.75, 0.75);
		mvMat.concatenate(vMat);
		mvMat.concatenate(mMat);
		
		// Build the MVP matrix from the light's point of view.
		shadowMVP2.setToIdentity();
		shadowMVP2.concatenate(b);
		shadowMVP2.concatenate(lightPMatrix);
		shadowMVP2.concatenate(lightVMatrix);
		shadowMVP2.concatenate(mMat);
		
		// Pass the model-view and normal matrices to uniforms in the shader.
		gl.glUniformMatrix4fv(mvLoc, 1, false, mvMat.getFloatValues(), 0);
		gl.glUniformMatrix4fv(nLoc, 1, false, mvMat.inverse().transpose().getFloatValues(), 0);
		gl.glUniformMatrix4fv(shadowLoc, 1, false,shadowMVP2.getFloatValues(), 0);
		
		// Bind the vertex buffer to a vertex attribute.
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[6]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);
		
		// Bind the texture buffer to a vertex attribute.
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[7]);
		gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);
		
		// Bind the normal buffer to a vertex attribute.
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[8]);
		gl.glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(2);
		
		// Set up texture.
		gl.glActiveTexture(GL_TEXTURE1);
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
		gl.glDepthFunc(GL_LEQUAL);
		
		// Draw the object.
		gl.glDrawArrays(GL_TRIANGLES, 0, s_planetOne.getIndices().length);
		
		//Planet Two
		
        mMat.setToIdentity();
		mvMat.setToIdentity();
		
		// Apply transformations to the model-view matrix.
		mMat.translate(0.0, 0.0, -3.0);
		mMat.rotateY(((System.currentTimeMillis()) / 50.0) % 360);
		mMat.scale(1, 1, 1);
		mvMat.concatenate(vMat);
		mvMat.concatenate(mMat);
		
		// Build the MVP matrix from the light's point of view.
		shadowMVP2.setToIdentity();
		shadowMVP2.concatenate(b);
		shadowMVP2.concatenate(lightPMatrix);
		shadowMVP2.concatenate(lightVMatrix);
		shadowMVP2.concatenate(mMat);
		
		// Pass the model-view and normal matrices to uniforms in the shader.
		gl.glUniformMatrix4fv(mvLoc, 1, false, mvMat.getFloatValues(), 0);
		gl.glUniformMatrix4fv(nLoc, 1, false, mvMat.inverse().transpose().getFloatValues(), 0);
		gl.glUniformMatrix4fv(shadowLoc, 1, false,shadowMVP2.getFloatValues(), 0);
		
		// Bind the vertex buffer to a vertex attribute.
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[9]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);
		
		// Bind the texture buffer to a vertex attribute.
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[10]);
		gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);
		
		// Bind the normal buffer to a vertex attribute.
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[11]);
		gl.glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(2);
		
		
		// Set up texture.
		gl.glActiveTexture(GL_TEXTURE1);
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
		gl.glDepthFunc(GL_LEQUAL);
		
		// Draw the object.
		gl.glDrawArrays(GL_TRIANGLES, 0, s_planetTwo.getIndices().length);
		
		//SPACEMAN
		
        mMat.setToIdentity();
        mMat.rotateY(((System.currentTimeMillis()) / 50.0) % 360);
        mMat.translate(0.0, 0.0, -5.0);
		mvMat.setToIdentity();
		
		// Apply transformations to the model-view matrix.
		mMat.translate(0.0, 0.0, 0.0);
//		mMat.rotateX(((System.currentTimeMillis()) / 50.0) % 360);
		mMat.rotateY(90);
		mMat.scale(2, 2, 2);
		mvMat.concatenate(vMat);
		mvMat.concatenate(mMat);
		
		// Build the MVP matrix from the light's point of view.
		shadowMVP2.setToIdentity();
		shadowMVP2.concatenate(b);
		shadowMVP2.concatenate(lightPMatrix);
		shadowMVP2.concatenate(lightVMatrix);
		shadowMVP2.concatenate(mMat);
		
		// Pass the model-view and normal matrices to uniforms in the shader.
		gl.glUniformMatrix4fv(mvLoc, 1, false, mvMat.getFloatValues(), 0);
		gl.glUniformMatrix4fv(nLoc, 1, false, mvMat.inverse().transpose().getFloatValues(), 0);
		gl.glUniformMatrix4fv(shadowLoc, 1, false,shadowMVP2.getFloatValues(), 0);
		
		// Bind the vertex buffer to a vertex attribute.
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[12]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);
		
		// Bind the texture buffer to a vertex attribute.
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[13]);
		gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);
		
		// Bind the normal buffer to a vertex attribute.
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[14]);
		gl.glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(2);
		
		
		// Set up texture.
		gl.glActiveTexture(GL_TEXTURE1);
		gl.glBindTexture(GL_TEXTURE_2D, spaceManTex);
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
		gl.glDepthFunc(GL_LEQUAL);
		
		// Draw the object.
		gl.glDrawArrays(GL_TRIANGLES, 0, spaceMan.getVertices().length);
		
		
	}
	
	private void installLights(Matrix3D vMatrix)
	{	GL4 gl = (GL4) GLContext.getCurrentGL();
	
		Point3D lightP = posLight.getPosition();
		Point3D lightPv = lightP.mult(vMatrix);
		float[] viewspaceLightPos = new float[] {(float) lightPv.getX(), (float) lightPv.getY(), (float) lightPv.getZ()};
		
		// Set the current globalAmbient settings.
		globalAmbLoc = gl.glGetUniformLocation(renderingProgram0, "globalAmbient");
		gl.glProgramUniform4fv(renderingProgram0, globalAmbLoc, 1, globalAmbient, 0);
		
		
		// Get the locations of the light fields in the shader.

		ambLoc = gl.glGetUniformLocation(renderingProgram0, "light.ambient");
		diffLoc = gl.glGetUniformLocation(renderingProgram0, "light.diffuse");
		specLoc = gl.glGetUniformLocation(renderingProgram0, "light.specular");
		posLoc = gl.glGetUniformLocation(renderingProgram0, "light.position");
		
		// Set the uniform light values in the shader.
		gl.glProgramUniform4fv(renderingProgram0, ambLoc, 1, posLight.getAmbient(), 0);
		gl.glProgramUniform4fv(renderingProgram0, diffLoc, 1, posLight.getDiffuse(), 0);
		gl.glProgramUniform4fv(renderingProgram0, specLoc, 1, posLight.getSpecular(), 0);
		gl.glProgramUniform3fv(renderingProgram0, posLoc, 1, viewspaceLightPos, 0);

		
		
	}
	//Adopted from LookAt Function provided
	private Matrix3D lookAtLight(Point3D eye, Point3D target, Vector3D y)
	{	Vector3D eyeV = new Vector3D(eye);
		Vector3D targetV = new Vector3D(target);
		Vector3D fwd = (targetV.minus(eyeV)).normalize();
		Vector3D side = (fwd.cross(y)).normalize();
		Vector3D up = (side.cross(fwd)).normalize();
		Matrix3D look = new Matrix3D();
		look.setElementAt(0,0, side.getX());
		look.setElementAt(1,0, up.getX());
		look.setElementAt(2,0, -fwd.getX());
		look.setElementAt(3,0, 0.0f);
		look.setElementAt(0,1, side.getY());
		look.setElementAt(1,1, up.getY());
		look.setElementAt(2,1, -fwd.getY());
		look.setElementAt(3,1, 0.0f);
		look.setElementAt(0,2, side.getZ());
		look.setElementAt(1,2, up.getZ());
		look.setElementAt(2,2, -fwd.getZ());
		look.setElementAt(3,2, 0.0f);
		look.setElementAt(0,3, side.dot(eyeV.mult(-1)));
		look.setElementAt(1,3, up.dot(eyeV.mult(-1)));
		look.setElementAt(2,3, (fwd.mult(-1)).dot(eyeV.mult(-1)));
		look.setElementAt(3,3, 1.0f);
		return(look);
	}
	

	public void dispose(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub
		
	}

	public void init(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub
		GL4 gl = (GL4) GLContext.getCurrentGL();
		
		s_sun = new Sphere(24);
		s_planetOne = new Sphere(24);
		s_planetTwo = new Sphere(24);
		spaceMan = new ImportedModel("src/Resources/shuttle.obj");
		
		createShaderPrograms();
		setupVertices();
		setupShadowBuffers();
		
		b.setElementAt(0,0,0.5f);
		b.setElementAt(0,1,0.0);
		b.setElementAt(0,2,0.0);
		b.setElementAt(0,3,0.5f);
		b.setElementAt(1,0,0.0);
		b.setElementAt(1,1,0.5);
		b.setElementAt(1,2,0.0);
		b.setElementAt(1,3,0.5f);
		b.setElementAt(2,0,0.0);
		b.setElementAt(2,1,0.0);
		b.setElementAt(2,2,0.5f);
		b.setElementAt(2,3,0.5f);
		b.setElementAt(3,0,0.0);
		b.setElementAt(3,1,0.0);
		b.setElementAt(3,2,0.0);
		b.setElementAt(3,3,1.0f);
		
        
        cameraX = -15.0f; cameraY = 4.0f; cameraZ = 0f;
        sunLocX =0f; sunLocY = 0f; sunLocZ = 0f;
        camera.setMyEye(new Vector3D(cameraX, cameraY, cameraZ));
        sunTex = Utils.loadTexture("src/Resources/sun.jpg");
        skyBoxTex = Utils.loadTexture("src/Resources/spaceSkyBox.jpg");
        planetOneTex = Utils.loadTexture("src/Resources/earth.jpg");
        planetTwoTex = Utils.loadTexture("src/Resources/moonOne.jpg");
        spaceManTex = Utils.loadTexture("src/Resources/spstob_1.jpg");
	}
	
	public void setupShadowBuffers()
	{
		GL4 gl = (GL4) GLContext.getCurrentGL();
		screenSizeX = myCanvas.getWidth(); screenSizeY = myCanvas.getHeight();
		
		// Create the custom frame buffer.
		gl.glGenFramebuffers(1, shadowBuffer, 0);
		
		// Create the shadow texture and configure it to hold depth information.
		// these steps are similar to those in Program 5.2
		gl.glGenTextures(1, shadowTex, 0);
		gl.glBindTexture(GL_TEXTURE_2D, shadowTex[0]);
		gl.glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT32, screenSizeX, screenSizeY, 0, GL_DEPTH_COMPONENT, GL_FLOAT, null);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_COMPARE_MODE, GL_COMPARE_REF_TO_TEXTURE);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_COMPARE_FUNC, GL_LEQUAL);
	}
	
	private void createShaderPrograms()
	{
		GL4 gl = (GL4) GLContext.getCurrentGL();
		
		String[] vshaderSource1 = GLSLUtils.readShaderSource("shaders/vertShader1.glsl");
		
		String[] vshaderSource2 = GLSLUtils.readShaderSource("shaders/vertShader2.glsl");
		String[] fshaderSource2 = GLSLUtils.readShaderSource("shaders/fragShader2.glsl");
//		String[] vshaderSource2 = GLSLUtils.readShaderSource("shaders/vert.shader");
//		String[] fshaderSource2 = GLSLUtils.readShaderSource("shaders/frag.shader");
		
		String[] vshaderSource0 = GLSLUtils.readShaderSource("shaders/vertShader0.glsl");
		String[] fshaderSource0 = GLSLUtils.readShaderSource("shaders/fragShader0.glsl");
		

		int vertexShader1 = gl.glCreateShader(GL_VERTEX_SHADER);
		int vertexShader2 = gl.glCreateShader(GL_VERTEX_SHADER);
		int fragmentShader2 = gl.glCreateShader(GL_FRAGMENT_SHADER);
		int vertexShader0 = gl.glCreateShader(GL_VERTEX_SHADER);
		int fragmentShader0 = gl.glCreateShader(GL_FRAGMENT_SHADER);

		gl.glShaderSource(vertexShader1, vshaderSource1.length, vshaderSource1, null, 0);
		gl.glShaderSource(vertexShader2, vshaderSource2.length, vshaderSource2, null, 0);
		gl.glShaderSource(fragmentShader2, fshaderSource2.length, fshaderSource2, null, 0);

		gl.glShaderSource(vertexShader0, vshaderSource0.length, vshaderSource0, null, 0);
		gl.glShaderSource(fragmentShader0, fshaderSource0.length, fshaderSource0, null, 0);

		gl.glCompileShader(vertexShader1);
		gl.glCompileShader(vertexShader2);
		gl.glCompileShader(fragmentShader2);

		gl.glCompileShader(vertexShader0);
		gl.glCompileShader(fragmentShader0);

		renderingProgram1 = gl.glCreateProgram();
		renderingProgram2 = gl.glCreateProgram();
		renderingProgram0 = gl.glCreateProgram();

		gl.glAttachShader(renderingProgram1, vertexShader1);
		gl.glAttachShader(renderingProgram2, vertexShader2);
		gl.glAttachShader(renderingProgram2, fragmentShader2);
		gl.glAttachShader(renderingProgram0, vertexShader0);
		gl.glAttachShader(renderingProgram0, fragmentShader0);

		gl.glLinkProgram(renderingProgram1);
		gl.glLinkProgram(renderingProgram2);
		gl.glLinkProgram(renderingProgram0);
	}

	public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3, int arg4) {
		// TODO Auto-generated method stub
		
	}
	
	
	private void setupVertices(){
		GL4 gl = (GL4) GLContext.getCurrentGL();

	    gl.glGenVertexArrays(vao.length, vao, 0);
		gl.glBindVertexArray(vao[0]);
		gl.glGenBuffers(vbo.length, vbo, 0);
		
	
		
		this.initSphereVertices(s_sun, 0);
		this.setupSkyVertices(3);
        this.initSphereVertices(s_planetOne, 6);
        this.initSphereVertices(s_planetTwo, 9);
        this.setupSpaceManVertices(12);
        
        
        
	}
	

	private void setupSkyVertices(int vbo_i)
	{	GL4 gl = (GL4) GLContext.getCurrentGL();
	
		// cube
		
		float[] cubeVertexPositions =
		{	-1.0f,  1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f,
			1.0f, -1.0f, -1.0f, 1.0f,  1.0f, -1.0f, -1.0f,  1.0f, -1.0f,
			1.0f, -1.0f, -1.0f, 1.0f, -1.0f,  1.0f, 1.0f,  1.0f, -1.0f,
			1.0f, -1.0f,  1.0f, 1.0f,  1.0f,  1.0f, 1.0f,  1.0f, -1.0f,
			1.0f, -1.0f,  1.0f, -1.0f, -1.0f,  1.0f, 1.0f,  1.0f,  1.0f,
			-1.0f, -1.0f,  1.0f, -1.0f,  1.0f,  1.0f, 1.0f,  1.0f,  1.0f,
			-1.0f, -1.0f,  1.0f, -1.0f, -1.0f, -1.0f, -1.0f,  1.0f,  1.0f,
			-1.0f, -1.0f, -1.0f, -1.0f,  1.0f, -1.0f, -1.0f,  1.0f,  1.0f,
			-1.0f, -1.0f,  1.0f,  1.0f, -1.0f,  1.0f,  1.0f, -1.0f, -1.0f,
			1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f,  1.0f,
			-1.0f,  1.0f, -1.0f, 1.0f,  1.0f, -1.0f, 1.0f,  1.0f,  1.0f,
			1.0f,  1.0f,  1.0f, -1.0f,  1.0f,  1.0f, -1.0f,  1.0f, -1.0f
		};
		
		float[] cube_texture_coord =
			{	.25f,  .666666666f, .25f, .3333333333f, .5f, .3333333333f,	// front face lower left
					.5f, .333333333333f, .5f,  .66666666666f, .25f,  .66666666666f,	// front face upper right
					.5f, .3333333333f, .75f, .33333333333f,  .5f,  .6666666666f,	// right face lower left
					.75f, .33333333333f,  .75f,  .66666666666f, .5f,  .6666666666f,	// right face upper right
					.75f, .3333333333f,  1.0f, .3333333333f, .75f,  .66666666666f,	// back face lower
					1.0f, .3333333333f, 1.0f,  .6666666666f, .75f,  .6666666666f,	// back face upper
					0.0f, .333333333f,  .25f, .333333333f, 0.0f,  .666666666f,	// left face lower
					.25f, .333333333f, .25f,  .666666666f, 0.0f,  .666666666f,	// left face upper
					.25f, 0.0f,  .5f, 0.0f,  .5f, .333333333f,			// bottom face front
					.5f, .333333333f, .25f, .333333333f, .25f, 0.0f,		// bottom face back
					.25f,  .666666666f, .5f,  .666666666f, .5f,  1.0f,		// top face back
					.5f,  1.0f,  .25f,  1.0f, .25f,  .666666666f			// top face front
			};
		
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[vbo_i]);
		FloatBuffer cvertBuf = Buffers.newDirectFloatBuffer(cubeVertexPositions);
		gl.glBufferData(GL_ARRAY_BUFFER, cvertBuf.limit()*4, cvertBuf, GL_STATIC_DRAW);
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[vbo_i+1]);
		FloatBuffer cubeTexBuf = Buffers.newDirectFloatBuffer(cube_texture_coord);
		gl.glBufferData(GL_ARRAY_BUFFER, cubeTexBuf.limit()*8, cubeTexBuf, GL_STATIC_DRAW);

	}
	
	private void setupSpaceManVertices(int vbo_i)
	{	GL4 gl = (GL4) GLContext.getCurrentGL();
	
		int numObjVertices = spaceMan.getNumVertices();
		Vertex3D[] vertices = spaceMan.getVertices();
//		Vector2f[] texCoords = spaceMan.getTexCoords();
//		Vector3f[] normals = spaceMan.getNormals();
		
		float[] pvalues = new float[numObjVertices*3];
		float[] tvalues = new float[numObjVertices*2];
		float[] nvalues = new float[numObjVertices*3];
		
		for (int i=0; i<numObjVertices; i++)
		{	pvalues[i*3]   = (float) (vertices[i]).getX();
			pvalues[i*3+1] = (float) (vertices[i]).getY();
			pvalues[i*3+2] = (float) (vertices[i]).getZ();
			tvalues[i*2] = (float) (vertices[i]).getS();
			tvalues[i*2+1] = (float) (vertices[i]).getT();
			nvalues[i*3] = (float) (vertices[i]).getNormalX();
			nvalues[i*3+1] = (float) (vertices[i]).getNormalY();
			nvalues[i*3+2] = (float) (vertices[i]).getNormalZ();
			
//			tvalues[i*2]   = (float) (texCoords[i]).x();
//			tvalues[i*2+1] = (float) (texCoords[i]).y();
//			nvalues[i*3]   = (float) (normals[i]).x();
//			nvalues[i*3+1] = (float) (normals[i]).y();
//			nvalues[i*3+2] = (float) (normals[i]).z();
		}
		
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[vbo_i]);
		FloatBuffer vertBuf = Buffers.newDirectFloatBuffer(pvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, vertBuf.limit()*4, vertBuf, GL_STATIC_DRAW);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[vbo_i+1]);
		FloatBuffer texBuf = Buffers.newDirectFloatBuffer(tvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, texBuf.limit()*4, texBuf, GL_STATIC_DRAW);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[vbo_i+2]);
		FloatBuffer norBuf = Buffers.newDirectFloatBuffer(nvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, norBuf.limit()*4,norBuf, GL_STATIC_DRAW);
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
			lightOn = !lightOn;
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

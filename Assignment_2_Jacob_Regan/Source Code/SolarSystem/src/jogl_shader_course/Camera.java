package jogl_shader_course;
import org.joml.Math;
import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;
import graphicslib3D.Point3D;

public class Camera {
	private Point3D cameraPos = new Point3D(0, 0,0 );
	private float pitch = 0f;
	private float yaw = 0f;
	private float speed = 0.1f;
	
	private boolean moveForward, moveBack, strafeLeft, strafeRight, moveDown, moveUp;


	private boolean panLeft, panRight, pitchUp, pitchDown, toggleVis;
	
	
	private Vector3D cameraUp;
    private Vector3D cameraRight;
    private Vector3D cameraFwd;
    
    private Vector3D myEye;

	private Vector3D myTarget = new Vector3D(0.0f, 0.0f, 0.0f);
    private Vector3D myUp = new Vector3D(0.0f, 1.0f, 0.0f);
    

	
	public Camera(float x, float y, float z) {
		cameraPos = new Point3D(x, y, z);
	}
	public Camera() {}
	
	public void updateCameraFwd() {
		float pitchRadians = Math.toRadians(pitch);
		float yawRadians = Math.toRadians(yaw);

		float sinPitch = Math.sin(pitchRadians);
		float cosPitch = Math.cos(pitchRadians);
		float sinYaw = Math.sin(yawRadians);
		float cosYaw = Math.cos(yawRadians);
		
		cameraFwd = new Vector3D(cosPitch * cosYaw, sinPitch, cosPitch * sinYaw);
	}
	
    private Matrix3D lookAt(Vector3D eye, Vector3D target, Vector3D y)
    {
        Matrix3D look = new Matrix3D();
        cameraRight = (cameraFwd.cross(y)).normalize();
        cameraUp = (cameraRight.cross(cameraFwd)).normalize();
//        System.out.println(cameraRight);
//        System.out.println(eye);

        look.setElementAt(0, 0, cameraRight.getX());
        look.setElementAt(1, 0, cameraUp.getX());
        look.setElementAt(2, 0, -cameraFwd.getX());
        look.setElementAt(3, 0, 0);
        look.setElementAt(0, 1, cameraRight.getY());
        look.setElementAt(1, 1, cameraUp.getY());
        look.setElementAt(2, 1, -cameraFwd.getY());
        look.setElementAt(3, 1, 0);
        look.setElementAt(0, 2, cameraRight.getZ());
        look.setElementAt(1, 2, cameraUp.getZ());
        look.setElementAt(2, 2, -cameraFwd.getZ());
        look.setElementAt(3, 2, 0);
        look.setElementAt(0, 3, cameraRight.dot(eye.mult(-1)));
        look.setElementAt(1, 3, cameraUp.dot(eye.mult(-1)));
        look.setElementAt(2, 3, (cameraFwd.mult(-1)).dot(eye.mult(-1)));
        look.setElementAt(3, 3, 1);
        return look;
    }
	
	public Matrix3D updateCam() {
		this.updateInput();
		this.updateCameraFwd();
		Matrix3D lookAt = this.lookAt(myEye, myTarget, myUp);
		
		return lookAt;
	}
	
	
	public void updateInput() {
		
		if(moveForward) {
			myEye = myEye.add(cameraFwd.mult(speed));
		}
		if(moveBack) {
			myEye = myEye.add(cameraFwd.mult(-speed));
		}
		if(strafeLeft) {
			myEye = myEye.add(cameraRight.mult(-speed));
		}
		if(strafeRight) {
			myEye = myEye.add(cameraRight.mult(speed));
		}
		if(moveDown) {
			myEye = myEye.add(cameraUp.mult(-speed));
		}
		if(moveUp) {
			myEye = myEye.add(cameraUp.mult(speed));
		}
		if(panLeft) {
			yaw--;
		}
		if(panRight) {
			yaw++;
		}
		if(pitchUp) {
			pitch++;
			if(pitch > 90f) { pitch = 90f;}
		}
		if(pitchDown) {
			pitch--;
			if(pitch < -90f) { pitch = -90f;}
		}
	}
	
	public void setMoveForward(boolean moveForward) {
		this.moveForward = moveForward;
	}
	public void setMoveBack(boolean moveBack) {
		this.moveBack = moveBack;
	}
	public void setStrafeLeft(boolean strafeLeft) {
		this.strafeLeft = strafeLeft;
	}
	public void setStrafeRight(boolean strafeRight) {
		this.strafeRight = strafeRight;
	}
	public void setMoveDown(boolean moveDown) {
		this.moveDown = moveDown;
	}
	public void setMoveUp(boolean moveUp) {
		this.moveUp = moveUp;
	}
	public void setPanLeft(boolean panLeft) {
		this.panLeft = panLeft;
	}
	public void setPanRight(boolean panRight) {
		this.panRight = panRight;
	}
	public void setPitchUp(boolean pitchUp) {
		this.pitchUp = pitchUp;
	}
	public void setPitchDown(boolean pitchDown) {
		this.pitchDown = pitchDown;
	}
	public void setToggleVis(boolean toggleVis) {
		this.toggleVis = toggleVis;
	}
	
    public Vector3D getMyEye() {
		return myEye;
	}
	public void setMyEye(Vector3D myEye) {
		this.myEye = myEye;
	}
	
	
	
}

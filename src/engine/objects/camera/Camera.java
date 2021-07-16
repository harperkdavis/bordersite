package engine.objects.camera;

import engine.math.Matrix4f;
import engine.math.Vector3f;

public abstract class Camera {

    private static Camera activeCamera;

    public abstract Matrix4f getViewMatrix();
    public abstract Matrix4f getProjectionMatrix();
    public abstract Vector3f getPosition();

    public static Camera getActiveCamera() {
        return activeCamera;
    }

    public static void setActiveCamera(Camera activeCamera) {
        Camera.activeCamera = activeCamera;
    }

    public static Matrix4f getActiveCameraView() {
        return activeCamera.getViewMatrix();
    }

    public static Matrix4f getActiveCameraProjection() {
        return activeCamera.getProjectionMatrix();
    }

    public static Vector3f getActiveCameraPosition() {
        return activeCamera.getPosition();
    }

}

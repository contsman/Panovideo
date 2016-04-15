package com.video.vrlib;

import android.content.res.Resources;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.view.MotionEvent;

/**
 *
 * response for model * view * projection
 */
public class MD360Director {

    private static final String TAG = "MD360Director";
    private static final float sDensity =  Resources.getSystem().getDisplayMetrics().density;
    private static final float sDamping = 5.0f;
    private float[] mModelMatrix = new float[16];
    private float[] mViewMatrix = new float[16];
    private float[] mProjectionMatrix = new float[16];

    private float[] mMVMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];

    private float mEyeZ = 12.5f;
    private float mAngle = 0;
    private float mRatio = 1.5f;
    private float mNear = 1.55f;
    private float[] mCurrentRotation = new float[16];
    private float[] mAccumulatedRotation = new float[16];
    private float[] mTemporaryMatrix = new float[16];

    public MD360Director() {
        initCamera();
        initModel();
    }

    private float mPreviousX;
    private float mPreviousY;

    private float mDeltaX;
    private float mDeltaY;


    /**
     * handle touch touch to rotate the model
     *
     * @param event
     * @return true if handled.
     */
    public boolean handleTouchEvent(MotionEvent event) {
        if (event != null) {
            float x = event.getX();
            float y = event.getY();

            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                float deltaX = (x - mPreviousX) / sDensity / sDamping;
                float deltaY = (y - mPreviousY) / sDensity / sDamping;
                mDeltaX -= deltaX;
                mDeltaY -= deltaY;
            }
            mPreviousX = x;
            mPreviousY = y;
            return true;

        } else {
            return false;
        }
    }

    private void initCamera() {
        // View Matrix
        updateCameraDistance(mEyeZ);
    }

    private void initModel(){
        Matrix.setIdentityM(mAccumulatedRotation, 0);
        // Model Matrix
        updateModelRotate(mAngle);
    }

    public void shot(MD360Program program) {

        Matrix.setIdentityM(mModelMatrix, 0);

        Matrix.setIdentityM(mCurrentRotation, 0);
        Matrix.rotateM(mCurrentRotation, 0, mDeltaX, 0.0f, 1.0f, 0.0f);
        Matrix.rotateM(mCurrentRotation, 0, mDeltaY, 1.0f, 0.0f, 0.0f);
        mDeltaX = 0.0f;
        mDeltaY = 0.0f;

        // Multiply the current rotation by the accumulated rotation, and then
        // set the accumulated rotation to the result.
        Matrix.multiplyMM(mTemporaryMatrix, 0, mCurrentRotation, 0, mAccumulatedRotation, 0);
        System.arraycopy(mTemporaryMatrix, 0, mAccumulatedRotation, 0, 16);

        // Rotate the cube taking the overall rotation into account.
        Matrix.multiplyMM(mTemporaryMatrix, 0, mModelMatrix, 0, mAccumulatedRotation, 0);
        System.arraycopy(mTemporaryMatrix, 0, mModelMatrix, 0, 16);

        // This multiplies the view matrix by the model matrix, and stores the result in the MVP matrix
        // (which currently contains model * view).
        Matrix.multiplyMM(mMVMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);

        // This multiplies the model view matrix by the projection matrix, and stores the result in the MVP matrix
        // (which now contains model * view * projection).
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVMatrix, 0);

        // Pass in the model view matrix
        GLES20.glUniformMatrix4fv(program.getMVMatrixHandle(), 1, false, mMVMatrix, 0);

        // Pass in the combined matrix.
        GLES20.glUniformMatrix4fv(program.getMVPMatrixHandle(), 1, false, mMVPMatrix, 0);
    }

    public void updateProjection(int width, int height){
        // Projection Matrix
        mRatio = width * 1.0f / height;
        updateProjectionNear(mNear);
    }

    private void updateCameraDistance(float z) {
        mEyeZ = z;
        final float eyeX = 0.0f;
        final float eyeY = 0.0f;
        final float eyeZ = mEyeZ;
        final float lookX = 0.0f;
        final float lookY = 0.0f;
        final float lookZ = 0.0f;
        final float upX = 0.0f;
        final float upY = 1.0f;
        final float upZ = 0.0f;
        Matrix.setIdentityM(mViewMatrix, 0);
        Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);
    }

    private void updateModelRotate(float a) {
        mAngle = a;
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.setRotateM(mModelMatrix,0,a,0,1,0);
    }

    private void updateProjectionNear(float near){
        mNear = near;
        final float left = -mRatio;
        final float right = mRatio;
        final float bottom = -0.5f;
        final float top = 0.5f;
        final float far = 500;
        Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, mNear, far);
    }
}

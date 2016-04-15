package com.video.vrlib;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.video.vrlib.common.GLUtil;


public class MDGLSurfaceView extends GLSurfaceView {

    private static final String TAG = "MDGLSurfaceView";
    private MD360Renderer mRender;
    private MD360Renderer.IOnSurfaceReadyListener mListener;

    public MDGLSurfaceView(Context context) {
        this(context,null);
    }

    public MDGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void init(MD360Renderer.IOnSurfaceReadyListener listener){
        mRender = MD360Renderer.with(getContext()).listenSurfaceReady(listener).build();
        if (GLUtil.supportsEs2(getContext())) {
            // Request an OpenGL ES 2.0 compatible context.
            this.setEGLContextClientVersion(2);

            // Set the renderer to our demo renderer, defined below.
            this.setRenderer(mRender);
        } else {
            Log.e(TAG,"this device do not support OpenGL ES 2!");
            this.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mRender.handleTouchEvent(event) || super.onTouchEvent(event);
    }

}

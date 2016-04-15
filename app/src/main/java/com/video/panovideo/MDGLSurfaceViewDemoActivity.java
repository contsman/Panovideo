package com.video.panovideo;

import android.os.Bundle;
import android.view.Surface;
import android.view.View;

import com.video.vrlib.MD360Renderer;
import com.video.vrlib.MDGLSurfaceView;

/**
 * using MDGLSurfaceView
 *
 */
public class MDGLSurfaceViewDemoActivity extends MediaPlayerActivity {

    private MDGLSurfaceView mGLSurfaceView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_md_gl_surface);

        mGLSurfaceView = (MDGLSurfaceView) findViewById(R.id.md_surface_view);
        mGLSurfaceView.init(new MD360Renderer.IOnSurfaceReadyListener() {
            @Override
            public void onSurfaceReady(Surface surface) {
                getPlayer().setSurface(surface);
            }
        });

        // play button
        findViewById(R.id.btn_play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play();
            }
        });
    }

    @Override
    protected void onResume() {
        // The activity must call the GL surface view's onResume() on activity onResume().
        super.onResume();
        mGLSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        // The activity must call the GL surface view's onPause() on activity onPause().
        super.onPause();
        mGLSurfaceView.onPause();
    }
}
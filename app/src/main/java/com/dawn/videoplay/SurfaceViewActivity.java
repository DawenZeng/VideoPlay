package com.dawn.videoplay;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.io.IOException;

public class SurfaceViewActivity extends AppCompatActivity implements View.OnClickListener {

    //    String dataPath = Environment.getExternalStorageDirectory().getPath() + "/Tencent/QQfile_recv/IMG_2811.mp4";
    String dataPath = "http://192.168.88.149:999/images/IC/IC0000054/IC0000054_180420163814230_iOS.mp4";
    private SurfaceView mSurfaceView;
    private MediaPlayer mMediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surface_view);

        setTitle("MediaPlayer+SurfaceView");

        mSurfaceView = findViewById(R.id.surface_view);
        findViewById(R.id.stop).setOnClickListener(this);
        findViewById(R.id.pasue).setOnClickListener(this);
        findViewById(R.id.play).setOnClickListener(this);

        init();
    }

    public void jumpToText(View v) {
        startActivity(new Intent(this, TextActivity.class));
    }

    private void init() {
        mMediaPlayer = new MediaPlayer();

        mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                play();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.stop:
                stop();
                break;
            case R.id.play:
                if (!mMediaPlayer.isPlaying()) {
                    play();
                }
                break;
            case R.id.pasue:
                pasue();
                break;
        }
    }


    public void stop() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }
    }

    public void pasue() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        } else {
            mMediaPlayer.start();
        }
    }

    public void play() {
        String path = "http://192.168.88.149:999/images/IC/IC0000054/IC0000054_180420163814230_iOS.mp4";
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            //设置需要播放的视频
            mMediaPlayer.setDataSource(this, Uri.parse(path));
            mMediaPlayer.setLooping(true);//循环播放
            //把视频画面输出到SurfaceView
            mMediaPlayer.setDisplay(mSurfaceView.getHolder());
            mMediaPlayer.prepare();//将资源同步缓存到内存中,一般加载本地较小的资源可以用这个
//            mMediaPlayer.prepareAsync();//较大的资源或者网络资源建议使用prepareAsync方法,异步加载
            mMediaPlayer.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

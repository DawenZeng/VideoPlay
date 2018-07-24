package com.dawn.videoplay;

import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

public class TextureViewActivity extends AppCompatActivity {

    private MediaPlayer mMediaPlayer;
    private Surface surface;
    private ImageView videoImage;
    private SeekBar seekBar;

    private Handler handler = new Handler();

    private final Runnable mTicker = new Runnable() {
        @Override
        public void run() {
            long now = SystemClock.uptimeMillis();
            long next = now + (1000 - now % 1000);

            handler.postAtTime(mTicker, next);//延迟一秒再次执行runnable,就跟计时器一样效果

            if (mMediaPlayer != null && mMediaPlayer.isPlaying())
                seekBar.setProgress(mMediaPlayer.getCurrentPosition());//更新播放进度
        }
    };

    public void jumpToText(View v) {
        startActivity(new Intent(this, TextActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_texture_view);

        setTitle("MediaPlayer+TextureView");

        TextureView textureView = findViewById(R.id.textureView);
        textureView.setSurfaceTextureListener(surfaceTezxtureListener);//设置监听函数  重写4个方法

        videoImage = findViewById(R.id.videoImage);
        seekBar = findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);//seekbar改变监听
    }

    private SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {//进度改变

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {//开始拖动seekbar

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {//停止拖动seekbar
            if (mMediaPlayer != null && mMediaPlayer.isPlaying())//播放中
                mMediaPlayer.seekTo(seekBar.getProgress());
        }
    };

    private TextureView.SurfaceTextureListener surfaceTezxtureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaces, int width, int height) {
            surface = new Surface(surfaces);
            new PlayerVideoThread().start();//开启一个线程去播放视频
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {//尺寸改变

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaces) {//销毁
            surface = null;
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {//更新

        }
    };

    private class PlayerVideoThread extends Thread {
        @Override
        public void run() {
            try {
                mMediaPlayer = new MediaPlayer();
                Uri uri = Uri.parse("http://192.168.88.149:999/images/IC/IC0000054/IC0000054_180420163814230_iOS.mp4");
                mMediaPlayer.setDataSource(TextureViewActivity.this, uri);//设置播放资源(可以是应用的资源文件／url／sdcard路径)
                mMediaPlayer.setSurface(surface);//设置渲染画板
                mMediaPlayer.setLooping(true);//循环播放
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);//设置播放类型
//                mMediaPlayer.setOnCompletionListener(onCompletionListener);//播放完成监听,有循环播放不会有此监听

                mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {//预加载监听
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        videoImage.setVisibility(View.GONE);
                        mMediaPlayer.start();//开始播放
                        seekBar.setMax(mMediaPlayer.getDuration());//设置总进度
                        handler.post(mTicker);//更新进度
                    }
                });
                mMediaPlayer.prepare();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {//播放完成
            videoImage.setVisibility(View.VISIBLE);
            seekBar.setProgress(0);
            handler.removeCallbacks(mTicker);//删除执行的Runnable 终止计时器
        }
    };
}

package com.dawn.videoplay;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.bumptech.glide.Glide;
import com.dawn.jiaozivideoplayer.JZVideoPlayerStandard;
import com.dawn.videoplay.CustomView.MyJZVideoPlayerStandard;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

public class JiaoZiVideoPlayerActivity extends AppCompatActivity {

    private String path = "http://192.168.88.149:999/images/IC/IC0000054/IC0000054_180420163814230_iOS.mp4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jiao_zi_video_player);

        MyJZVideoPlayerStandard myJZVideoPlayerStandard = findViewById(R.id.jz_video);

        MediaMetadataRetriever media = new MediaMetadataRetriever();
        if (Build.VERSION.SDK_INT >= 14) {
            media.setDataSource(path, new HashMap<String, String>());
        } else {
            media.setDataSource(path);
        }
        //获取第一帧
        Bitmap bitmap = media.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
        media.release();
        //用于Glide可以加载Bitmap图片
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] bytes = baos.toByteArray();

        myJZVideoPlayerStandard.setUp(path, JZVideoPlayerStandard.SCREEN_WINDOW_NORMAL, "视频播放");
//        Glide.with(this).load("http://jzvd-pic.nathen.cn/jzvd-pic/1bb2ebbe-140d-4e2e-abd2-9e7e564f71ac.png").into(myJZVideoPlayerStandard.thumbImageView);
        Glide.with(this).load(bytes).into(myJZVideoPlayerStandard.thumbImageView);
//        JZVideoPlayer.setJzUserAction(null);
    }

    public void jumpToText(View v) {
        startActivity(new Intent(this, TextActivity.class));
    }
}

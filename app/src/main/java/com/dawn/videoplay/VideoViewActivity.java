package com.dawn.videoplay;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import java.util.HashMap;

public class VideoViewActivity extends AppCompatActivity {

    private VideoView myVideoView;
    private ImageView imageView;
    //本地视频要手动获取权限
//    private String videopath = Environment.getExternalStorageDirectory().getPath() + "/Tencent/QQfile_recv/IMG_2811.mp4";//手机
    //    private String videopath = Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera/VCameraDemo/1523950354760.mp4";//平板
    private String videopath = "http://192.168.88.149:999/images/IC/IC0000054/IC0000054_180420163814230_iOS.mp4";//网络
    private Uri uri;
    private int playProgress;
    private Bitmap bitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_view);

        setTitle("VideoView");
        findViewById(R.id.surfaceView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SurfaceViewActivity.class));
            }
        });
        findViewById(R.id.textureView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), TextureViewActivity.class));
            }
        });
        findViewById(R.id.jiaozi_video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), JiaoZiVideoPlayerActivity.class));
            }
        });
        findViewById(R.id.mn_video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MNViderPlayActivity.class));
            }
        });

        findViewById(R.id.mn_download).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), DownloadActivity.class));
            }
        });

        myVideoView = findViewById(R.id.video);
        imageView = findViewById(R.id.image_view);
//        File file = new File(videopath);
//        uri = Uri.fromFile(file);
        uri = Uri.parse("file://" + videopath);

//        //1、使用其自带的播放器。指定Action为ACTION_VIEW,Data为Uri，Type为其MIME类型。
//        //调用系统自带的播放器
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.setDataAndType(uri, "video/*");
//        try {
//            startActivity(intent);
//        } catch (Exception e) {
//
//        }

        //Android6.0以上(包括6.0)系统，不能只是在AndroidManifest.xml中进行配置，还要在程序代码中进行动态设置相应的权限。版本号
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                //没有权限即动态申请 参数1：上下文，2：权限，3：请求码
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else {
                playVideo();
            }
        } else {
            playVideo();
        }

    }

    private void playVideo() {
//        //2、使用VideoView来播放。在布局文件中使用VideoView结合MediaController来实现对其控制。
//        //6.0要手动添加权限
//        myVideoView.setMediaController(new MediaController(this));//添加进度条
//        myVideoView.setVideoURI(uri);
//        myVideoView.start();
//        myVideoView.requestFocus();


        try {
            MediaMetadataRetriever media = new MediaMetadataRetriever();
            if (Build.VERSION.SDK_INT >= 14) {
                media.setDataSource(videopath, new HashMap<String, String>());
            } else {
                media.setDataSource(videopath);
            }
            //获取第一帧
            bitmap = media.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
            imageView.setImageBitmap(bitmap);
            media.release();

//        Glide.with(getApplicationContext())
//                .load(videopath)
//                .into(imageView);

            //视频循环播放
            //myVideoView.setVideoURI(uri);
            myVideoView.setVideoPath(videopath);
            myVideoView.start();//播放
//        myVideoView.pause();//暂停、播放
            MediaController mediaController = new MediaController(this);
//        mediaController.onFinishInflate();//一进来时显示进度条
            myVideoView.setMediaController(mediaController);//添加进度条

            //注册在媒体文件加载完毕，可以播放时调用的回调函数。可循环播放视频
            myVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
//                mp.start();//离开界面还回后可以重新播放
                    mp.setLooping(true);//设置是否对播放的东东进行循环播放。
                    mp.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                        @Override
                        public boolean onInfo(MediaPlayer mediaPlayer, int what, int i1) {
                            //开始播放时，就把显示第一帧的ImageView gone 掉
                            if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                                // video started; hide the placeholder.
                                imageView.setVisibility(View.GONE);
                                //videoView.seekTo(0);
                                return true;
                            }
                            Log.e("aaa", "00000000000000");
                            return false;
                        }
                    });
                }
            });
//
//        //注册在媒体文件播放完毕时调用的回调函数。可设置循环播放视频
//        myVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mp) {
//                //myVideoView.setVideoURI(uri);
//                myVideoView.setVideoPath(videopath);
//                myVideoView.start();
//                //mp.start();//这里设置该值，离开界面还回后不会重新播放
//            }
//        });
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "可能视频地址有问题", Toast.LENGTH_SHORT).show();
        }
    }

    // 获取权限的返回值
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {// 取得权限
                    playVideo();
                } else {// 未取得权限
                    Toast.makeText(this, "没有权限", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
//        //避免黑屏
        imageView.setVisibility(View.VISIBLE);
        imageView.setImageBitmap(bitmap);
        //记录播放的progress,避免黑屏
        myVideoView.pause();
        playProgress = myVideoView.getCurrentPosition();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (playProgress != 0) {
            myVideoView.seekTo(playProgress);
            myVideoView.start();
        }
        // playProgress = videoView.getCurrentPosition();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myVideoView != null) {
            myVideoView.suspend();//资源释放掉
        }
        // 销毁时调用
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
    }
}

package com.dawn.videoplay;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.dawn.mnvideoplayerlibrary.listener.OnCompletionListener;
import com.dawn.mnvideoplayerlibrary.listener.OnNetChangeListener;
import com.dawn.mnvideoplayerlibrary.listener.OnScreenOrientationListener;
import com.dawn.mnvideoplayerlibrary.player.MNViderPlayer;

public class MNViderPlayActivity extends AppCompatActivity {

    private final String url1 = "http://mp4.vjshi.com/2016-12-22/e54d476ad49891bd1adda49280a20692.mp4";
    private final String url2 = "http://mp4.vjshi.com/2016-12-22/e54d476ad49891bd1adda49280a20692.mp4";
    //这个地址是错误的
    private final String url3 = "http://weibo.com/p/23044451f0e5c4b762b9e1aa49c3091eea4d94";
    //本地视频
    private final String url4 = "/storage/emulated/0/Movies/Starry_Night.mp4";

    private MNViderPlayer mnViderPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mnvider_play);

        initViews();

        initPlayer();

        //请求权限:调节亮度
        requestPermission();
    }

    public void jumpToText(View v) {
        startActivity(new Intent(this, TextActivity.class));
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(this)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("提示");
                builder.setMessage("视频播放调节亮度需要申请权限");
                builder.setNegativeButton("取消", null);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS,
                                Uri.parse("package:" + getPackageName()));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivityForResult(intent, 100);
                    }
                });
                builder.show();
            }
        }
    }

    private void initViews() {
        mnViderPlayer = (MNViderPlayer) findViewById(R.id.mn_videoplay);
    }

    private void initPlayer() {

        //初始化相关参数(必须放在Play前面)
        mnViderPlayer.setWidthAndHeightProportion(16, 9);   //设置宽高比
        mnViderPlayer.setIsNeedBatteryListen(true);         //设置电量监听
        mnViderPlayer.setIsNeedNetChangeListen(true);       //设置网络监听
        //第一次进来先设置数据
        mnViderPlayer.setDataSource(url2, "标题");
        mnViderPlayer.setLooping(false);//可以重复播放

        //播放完成监听
        mnViderPlayer.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Log.i("aaa", "播放完成----");
            }
        });

        mnViderPlayer.setOnScreenOrientationListener(new OnScreenOrientationListener() {
            @Override
            public void orientation_landscape() {
                Toast.makeText(MNViderPlayActivity.this, "横屏", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void orientation_portrait() {
                Toast.makeText(MNViderPlayActivity.this, "竖屏", Toast.LENGTH_SHORT).show();
            }
        });

        //网络监听
        mnViderPlayer.setOnNetChangeListener(new OnNetChangeListener() {
            @Override
            public void onWifi(MediaPlayer mediaPlayer) {
            }

            @Override
            public void onMobile(MediaPlayer mediaPlayer) {
                Toast.makeText(MNViderPlayActivity.this, "请注意,当前网络状态切换为3G/4G网络", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNoAvailable(MediaPlayer mediaPlayer) {
                Toast.makeText(MNViderPlayActivity.this, "当前网络不可用,检查网络设置", Toast.LENGTH_LONG).show();
            }
        });

    }

    public void btn01(View view) {
        mnViderPlayer.playVideo(url1, "标题1");
    }

//    public void btn02(View view) {
//        //position表示需要跳转到的位置
//        mnViderPlayer.playVideo(url2, "标题2", 30000);
//    }
//
//    public void btn03(View view) {
//        mnViderPlayer.playVideo(url3, "标题3");
//    }
//
//    public void btn04(View view) {
//        if (hasPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE")) {
//            //判断本地有没有这个文件
//            File file = new File(url4);
//            if (file.exists()) {
//                mnViderPlayer.playVideo(url4, "标题4");
//            } else {
//                Toast.makeText(MNViderPlayActivity.this, "文件不存在", Toast.LENGTH_SHORT).show();
//            }
//        } else {
//            //请求权限
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
//            Toast.makeText(this, "没有存储权限", Toast.LENGTH_SHORT).show();
//        }
//    }

    @Override
    protected void onPause() {
        super.onPause();
        //暂停
        mnViderPlayer.pauseVideo();
    }

    @Override
    public void onBackPressed() {
        if (mnViderPlayer.isFullScreen()) {
            mnViderPlayer.setOrientationPortrait();
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        //一定要记得销毁View
        if (mnViderPlayer != null) {
            mnViderPlayer.destroyVideo();
            mnViderPlayer = null;
        }
        super.onDestroy();
    }


    public boolean hasPermission(Context context, String permission) {
        int perm = context.checkCallingOrSelfPermission(permission);
        return perm == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 100: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "存储权限申请成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "存储权限申请失败", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }
}

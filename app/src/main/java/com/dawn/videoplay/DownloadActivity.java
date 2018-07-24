package com.dawn.videoplay;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.LoopingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveVideoTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;

import java.io.File;

public class DownloadActivity extends AppCompatActivity {

    public static final String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/miniVideo";
    public static final String videoName = "IC0000054_180420163814230_iOS.mp4";
    private String videoUrl = "http://192.168.88.149:999/images/IC/IC0000054/" + videoName;
    private int i;
    private String videopath = "http://192.168.88.149:999/images/IC/IC0000054/IC0000054_180420163814230_iOS.mp4";//网络
    private SimpleExoPlayerView simpleExoPlayerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        setTitle("ExoPlayer视频简单播放");

        simpleExoPlayerView = findViewById(R.id.simpleExoPlayerView);

        // 1. 创建一个默认的TrackSelector
        Handler mainHandler = new Handler();
        // 创建带宽
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        // 创建轨道选择工厂
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveVideoTrackSelection.Factory(bandwidthMeter);
        // 创建轨道选择器实例
        TrackSelector trackSelector = new DefaultTrackSelector(mainHandler, videoTrackSelectionFactory);
        // 2. Create a default LoadControl
        LoadControl loadControl = new DefaultLoadControl();
        // 3. 创建播放器
        SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(getApplicationContext(), trackSelector, loadControl);
        simpleExoPlayerView.setPlayer(player);

        // Measures bandwidth during playback. Can be null if not required.
        DefaultBandwidthMeter band = new DefaultBandwidthMeter();
        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "yourApplicationName"), band);
        // Produces Extractor instances for parsing the media data.
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        // This is the MediaSource representing the media to be played.
        Uri mp4VideoUri = Uri.parse(videopath);
        MediaSource videoSource = new ExtractorMediaSource(mp4VideoUri, dataSourceFactory, extractorsFactory, null, null);
        //循环播放视频列表
        LoopingMediaSource loopingSource = new LoopingMediaSource(videoSource, 2);// 播放2次
//        MediaSource firstSource = new ExtractorMediaSource(firstVideoUri, ...);
//        MediaSource secondSource = new ExtractorMediaSource(secondVideoUri, ...);
//        ConcatenatingMediaSource concatenatedSource =new ConcatenatingMediaSource(videoSource, videoSource);// 连接多媒体source
        // Prepare the player with the source.
        player.prepare(videoSource);


    }

    /**
     * 检查并下载视频资源
     */
    public void downLoadVideo(View v) {
        File file = new File(path);
        Log.e("TAG", "===>" + path);
        //文件夹不存在，则创建它
        if (!file.exists()) {
            file.mkdir();
        }
        FileDownloader.setup(this);
        FileDownloader.getImpl().create(videoUrl)
                .setPath(path + "/" + videoName)
                .setForceReDownload(false)//强制重新下载，将会忽略检测文件是否健在
                .setListener(new FileDownloadListener() {
                    @Override
                    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        //等待，已经进入下载队列
                        Log.e("TAG", "pending");
                    }

                    @Override
                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        //下载进度
                        Log.e("TAG", "--" + i++);
                    }

                    @Override
                    protected void retry(final BaseDownloadTask task, final Throwable ex, final int retryingTimes, final int soFarBytes) {

                        Log.e("TAG", "retry");
                    }

                    @Override
                    protected void completed(BaseDownloadTask task) {
                        //完成整个下载过程
                        i = 0;
                    }

                    @Override
                    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        //暂停下载
                        Log.e("TAG", "paused");
                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        //下载出现错误
                        Log.e("TAG", "error===>" + e.getMessage());
                    }

                    @Override
                    protected void warn(BaseDownloadTask task) {
                        //在下载队列中(正在等待/正在下载)已经存在相同下载连接与相同存储路径的任务
                        Log.e("TAG", "warn");
                    }
                }).start();
    }
}

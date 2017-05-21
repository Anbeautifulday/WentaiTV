
package cn.edu.cqupt.wentaitv.activity;

import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import cn.edu.cqupt.wentaitv.R;
import cn.edu.cqupt.wentaitv.json.VideoInfo;
import cn.edu.cqupt.wentaitv.util.ToastUtil;

public class SurfaceActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener,
            MediaPlayer.OnErrorListener, OnBufferingUpdateListener, OnClickListener {

    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private SeekBar seekBar;

    private ImageView pre;
    private ImageView next;
    private ImageView playButton;
    private ImageView replayButton;

    private ProgressBar progressBar;
    private MediaPlayer mediaPlayer;
    //记录当前播放的位置
    private int playPosition = -1;
    //seekBar是否自动拖动
    private boolean seekBarAutoFlag = false;
    private TextView vedioTiemTextView;
    private String videoTimeString;
    private long videoTimeLong;

    private int keepTime;

    //播放路径
    private String pathString;

    //屏幕的宽度和高度
    private int screenWidth, screenHeight;

    private List<VideoInfo.ShowBody.PageBean.BodyInfo> videos;
    private int index;

    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surfaceview);
        actionBar = getSupportActionBar();
        //获取屏幕的宽度和高度
        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels / 2;

        initViews();
    }

    public void initViews() {
        String path = null;

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            // 存在获取外部文件路径
            path = Environment.getExternalStorageDirectory().getPath();
        } else {
            // 不存在获取内部存储
            path = Environment.getDataDirectory().getPath();
        }
        pathString = path + "/shen/x0200hkt1cg.p202.1.mp4";

        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        seekBar = (SeekBar) findViewById(R.id.seekbar);
        pre = (ImageView) findViewById(R.id.pre);
        next = (ImageView) findViewById(R.id.next);
        playButton = (ImageView) findViewById(R.id.button_play);

        replayButton = (ImageView) findViewById(R.id.button_replay);
        vedioTiemTextView = (TextView) findViewById(R.id.textView_showTime);

        // 设置surfaceHolder
        surfaceHolder = surfaceView.getHolder();
        // 设置Holder类型,该类型表示surfaceView自己不管理缓存区,虽然提示过时，但最好还是要设置
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        // 设置surface回调
        surfaceHolder.addCallback(new SurfaceCallback());

    }

    // SurfaceView的callBack
    private class SurfaceCallback implements SurfaceHolder.Callback {
        public void surfaceCreated(SurfaceHolder holder) {
            // surfaceView被创建
            // 设置播放资源
            playVideo();
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            // surfaceView销毁,同时销毁mediaPlayer
            if (null != mediaPlayer) {
                mediaPlayer.release();
                mediaPlayer = null;
            }
        }
    }

    public void playVideo() {
        // 初始化MediaPlayer
        mediaPlayer = new MediaPlayer();
        // 重置mediaPaly,在初始滑mediaplay立即调用。
        mediaPlayer.reset();
        // 设置声音效果
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        // 设置播放完成监听
        mediaPlayer.setOnCompletionListener(this);
        // 设置媒体加载完成以后回调函数。
        mediaPlayer.setOnPreparedListener(this);
        // 错误监听回调函数
        mediaPlayer.setOnErrorListener(this);
        // 设置缓存变化监听
        mediaPlayer.setOnBufferingUpdateListener(this);

        try {
            // mediaPlayer.reset();
            //mediaPlayer.setDataSource(pathString);
            Intent intent = getIntent();
            Uri uri = Uri.parse(intent.getStringExtra("videoUri"));
            videos = (List<VideoInfo.ShowBody.PageBean.BodyInfo>) intent.getSerializableExtra("videos");
            index = intent.getIntExtra("index", 2);    //获取数据用于。。。--
            mediaPlayer.setDataSource(this, uri);
            // 设置异步加载视频，包括两种方式 prepare()同步，prepareAsync()异步
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "加载视频错误！", Toast.LENGTH_LONG).show();
        }
    }

    //视频加载完
    @Override
    public void onPrepared(MediaPlayer mp) {
        // 当视频加载完毕以后，隐藏加载进度条
        progressBar.setVisibility(View.GONE);
        // 判断是否有保存的播放位置,防止屏幕旋转时，界面被重新构建，播放位置丢失。
        if (Constants.playPosition >= 0) {
            mediaPlayer.seekTo(Constants.playPosition);
            Constants.playPosition = -1;
            // surfaceHolder.unlockCanvasAndPost(Constants.getCanvas());
        }
        seekBarAutoFlag = true;
        // 设置控制条,放在加载完成以后设置，防止获取getDuration()错误
        seekBar.setMax(mediaPlayer.getDuration());
        // 设置播放时间
        videoTimeLong = mediaPlayer.getDuration();
        videoTimeString = getShowTime(videoTimeLong);
        vedioTiemTextView.setText("00:00:00/" + videoTimeString);
        // 设置拖动监听事件
        seekBar.setOnSeekBarChangeListener(new SeekBarChangeListener());

        replayButton.setOnClickListener(SurfaceActivity.this);
        playButton.setOnClickListener(SurfaceActivity.this);
        pre.setOnClickListener(SurfaceActivity.this);
        next.setOnClickListener(SurfaceActivity.this);

        // 播放视频
        mediaPlayer.start();
        // 设置显示到屏幕
        mediaPlayer.setDisplay(surfaceHolder);
        // 开启线程 刷新进度条
        new Thread(runnable).start();
        // 设置surfaceView保持在屏幕上
        mediaPlayer.setScreenOnWhilePlaying(true);
        surfaceHolder.setKeepScreenOn(true);

    }

    //滑动条变化
    private Runnable runnable = new Runnable() {

        public void run() {
            // TODO Auto-generated method stub
            // 增加对异常的捕获，防止在判断mediaPlayer.isPlaying的时候，报IllegalStateException异常
            try {
                while (seekBarAutoFlag) {
                    /*
                     * mediaPlayer不为空且处于正在播放状态时，使进度条滚动。
                     * 通过指定类名的方式判断mediaPlayer防止状态发生不一致
                     */

                    if (null != SurfaceActivity.this.mediaPlayer
                            && SurfaceActivity.this.mediaPlayer.isPlaying()) {
                        seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @SuppressWarnings("unused")
    private class SeekBarChangeListener implements OnSeekBarChangeListener {

        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (progress >= 0) {
                // 如果是用户手动拖动控件，则设置视频跳转
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                }
                // 设置当前播放时间
                vedioTiemTextView.setText(getShowTime(progress) + "/" + videoTimeString);
            }
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    }

    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.pre:
                try {
                    if (index > 0) {
                        Intent intent = new Intent(this, SurfaceActivity.class);
                        intent.putExtra("videoUri", videos.get(index - 1).getVideo_uri());
                        intent.putExtra("videos", (Serializable) videos);
                        intent.putExtra("index", index - 1);
                        overridePendingTransition(R.layout.activity_surfaceview, 0);
                        startActivity(intent);
                        finish();
                    } else {
                        ToastUtil.show(this, R.string.pre);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.next:
                try {
                    if (index < 21) {
                        Intent intent = new Intent(this, SurfaceActivity.class);
                        intent.putExtra("videoUri", videos.get(index + 1).getVideo_uri());
                        intent.putExtra("videos", (Serializable) videos);
                        intent.putExtra("index", index + 1);
                        startActivity(intent);
                        finish();
                    } else {
                        ToastUtil.show(this, R.string.next);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.button_replay :
                // mediaPlayer不空，则直接跳转
                if (null != mediaPlayer) {
                    // MediaPlayer和进度条都跳转到开始位置
                    mediaPlayer.seekTo(0);
                    seekBar.setProgress(0);
                    // 如果不处于播放状态，则开始播放
                    if (!mediaPlayer.isPlaying()) {
                        mediaPlayer.start();
                    }
                } else {
                    // 为空则重新设置mediaPlayer
                    playVideo();
                }
                break;
            case R.id.button_play :
                if (null != mediaPlayer) {
                    // 正在播放
                    if (mediaPlayer.isPlaying()) {
                        Constants.playPosition = mediaPlayer.getCurrentPosition();
                        // seekBarAutoFlag = false;
                        mediaPlayer.pause();
                        playButton.setImageResource(R.drawable.desk_play);
                    } else {
                        if (Constants.playPosition >= 0) {
                            // seekBarAutoFlag = true;
                            mediaPlayer.seekTo(Constants.playPosition);
                            mediaPlayer.start();
                            playButton.setImageResource(R.drawable.desk_pause);
                            Constants.playPosition = -1;
                        }
                    }

                }
                break;
        }
    }

    //播放完毕
    @Override
    public void onCompletion(MediaPlayer mp) {
        // 设置seeKbar跳转到最后位置
        seekBar.setProgress(Integer.parseInt(String.valueOf(videoTimeLong)));
        // 设置播放标记为false
        seekBarAutoFlag = false;
    }

    //视频缓存
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        // percent 表示缓存加载进度，0为没开始，100表示加载完成，在加载完成以后也会一直调用该方法
        Log.e("text", "onBufferingUpdate-->" + percent);
        seekBar.setSecondaryProgress(percent+20);

        // 可以根据大小的变化来
    }

    //错误监听
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                Toast.makeText(this, "MEDIA_ERROR_UNKNOWN", Toast.LENGTH_SHORT).show();
                break;
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                Toast.makeText(this, "MEDIA_ERROR_SERVER_DIED", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }

        switch (extra) {
            case MediaPlayer.MEDIA_ERROR_IO:
                Toast.makeText(this, "MEDIA_ERROR_IO", Toast.LENGTH_SHORT).show();
                break;
            case MediaPlayer.MEDIA_ERROR_MALFORMED:
                Toast.makeText(this, "MEDIA_ERROR_MALFORMED", Toast.LENGTH_SHORT).show();
                break;
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                Toast.makeText(this, "MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK",
                        Toast.LENGTH_SHORT).show();
                break;
            case MediaPlayer.MEDIA_ERROR_TIMED_OUT:
                Toast.makeText(this, "MEDIA_ERROR_TIMED_OUT", Toast.LENGTH_SHORT).show();
                break;
            case MediaPlayer.MEDIA_ERROR_UNSUPPORTED:
                Toast.makeText(this, "MEDIA_ERROR_UNSUPPORTED", Toast.LENGTH_SHORT).show();
                break;
        }
        return false;
    }

    //从暂停中恢复
    protected void onResume() {
        super.onResume();
        // 判断播放位置
        if (Constants.playPosition >= 0) {

            if (null != mediaPlayer) {
                seekBarAutoFlag = true;
                mediaPlayer.seekTo(Constants.playPosition);
                mediaPlayer.start();
            } else {
                playVideo();
            }

        }
    }

    //页面暂停
    @Override
    protected void onPause() {
        super.onPause();
        try {
            if (null != mediaPlayer && mediaPlayer.isPlaying()) {
                Constants.playPosition = mediaPlayer.getCurrentPosition();
                mediaPlayer.pause();
                seekBarAutoFlag = false;
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    //发生屏幕旋转时调用
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (null != mediaPlayer) {
            // 保存播放位置
            //Constants.playPosition = mediaPlayer.getCurrentPosition();
            keepTime = mediaPlayer.getCurrentPosition();
        }
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //newConfig.orientation获得当前屏幕状态是横向或者竖向
        if(newConfig.orientation==Configuration.ORIENTATION_PORTRAIT){
            actionBar.show();
        }
        if(newConfig.orientation==Configuration.ORIENTATION_LANDSCAPE){
            actionBar.hide();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 由于MediaPlay非常占用资源，所以建议屏幕当前activity销毁时，则直接销毁
        try {
            if (null != SurfaceActivity.this.mediaPlayer) {
                // 提前标志为false,防止在视频停止时，线程仍在运行。
                seekBarAutoFlag = false;
                // 如果正在播放，则停止。
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                Constants.playPosition = -1;
                // 释放mediaPlayer
                SurfaceActivity.this.mediaPlayer.release();
                SurfaceActivity.this.mediaPlayer = null;
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    //转换播放时间
    @SuppressLint("SimpleDateFormat")
    public String getShowTime(long milliseconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliseconds);
        SimpleDateFormat dateFormat = null;
        if (milliseconds / 60000 > 60) {
            dateFormat = new SimpleDateFormat("hh:mm:ss");
        } else {
            dateFormat = new SimpleDateFormat("mm:ss");
        }
        return dateFormat.format(calendar.getTime());
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return super.onRetainCustomNonConfigurationInstance();
    }
}

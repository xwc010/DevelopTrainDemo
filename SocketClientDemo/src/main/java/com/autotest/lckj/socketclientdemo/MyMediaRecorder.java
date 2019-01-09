package com.autotest.lckj.socketclientdemo;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaRecorder;
import android.os.Handler;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * Created by ouyangxiangqing on 2017/9/5.
 */

public class MyMediaRecorder {
    private final String TAG = "com.lckj.autotest";
    private MediaRecorder mMediaRecorder;
    public static final int MAX_LENGTH = 1000 * 60 * 10;// 最大录音时长1000*60*10;
    private String filePath;
    private int num = 0;
    private double sum = 0;
    private double average = 0;
    private double max = 0;
    private Context context;

    public MyMediaRecorder(Context context) {
        File file = FileUtil.createFile("temp.amr");
        this.filePath = file.getAbsolutePath();
        this.context = context;
    }

    private long startTime;
    private long endTime;

    /**
     * 开始录音 使用amr格式
     *
     *            录音文件
     * @return
     */
    public void startRecord() {
        num = 0;
        sum = 0;
        average = 0;
        max = 0;
        // 开始录音
        /* ①Initial：实例化MediaRecorder对象 */
        if (mMediaRecorder == null)
            mMediaRecorder = new MediaRecorder();
        try {
            /* ②setAudioSource/setVedioSource */
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);// 设置麦克风
            /* ②设置音频文件的编码：AAC/AMR_NB/AMR_MB/Default 声音的（波形）的采样 */
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
                        /*
             * ②设置输出文件的格式：THREE_GPP/MPEG-4/RAW_AMR/Default THREE_GPP(3gp格式
             * ，H263视频/ARM音频编码)、MPEG-4、RAW_AMR(只支持音频且音频编码要求为AMR_NB)
             */
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            /* ③准备 */
            mMediaRecorder.setOutputFile(filePath);
            mMediaRecorder.setMaxDuration(MAX_LENGTH);
            mMediaRecorder.prepare();
            /* ④开始 */
            mMediaRecorder.start();
            // AudioRecord audioRecord.
            /* 获取开始时间* */
            startTime = System.currentTimeMillis();
            updateMicStatus();
            Log.e(TAG, "startTime" + startTime);
        } catch (IllegalStateException e) {
            Log.e(TAG,
                    "call startAmr(File mRecAudioFile) failed!"
                            + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG,
                    "call startAmr(File mRecAudioFile) failed!"
                            + e.getMessage());
        }
    }

    /**
     * 停止录音
     *
     */
    public long stopRecord() {
        if (mMediaRecorder == null)
            return 0L;
        endTime = System.currentTimeMillis();
        Log.i("ACTION_END", "endTime" + endTime);
        mMediaRecorder.stop();
        mMediaRecorder.reset();
        mMediaRecorder.release();
        mMediaRecorder = null;
        Log.i("ACTION_LENGTH", "Time" + (endTime - startTime));

        SharedPreferences sharedPreferences = context.getSharedPreferences("test", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("average",(float) average);
        editor.putFloat("max",(float) max);
        editor.commit();

        return endTime - startTime;
    }

    private final Handler mHandler = new Handler();
    private Runnable mUpdateMicStatusTimer = new Runnable() {
        public void run() {
            updateMicStatus();
        }
    };

    /**
     * 更新话筒状态
     *
     */
    private int BASE = 1;
    private int SPACE = 100;// 间隔取样时间

    private void updateMicStatus() {
        if (mMediaRecorder != null) {
            num += 1;
            double ratio = (double)mMediaRecorder.getMaxAmplitude() /BASE;
            double db = 0;// 分贝
            if (ratio > 0)
                db = 20 * Math.log10(ratio);
            Log.e(TAG,"分贝值："+db);
            sum += db;
            average = sum / num;
            Log.e(TAG,"平均分贝值："+average);
            if (db > max){
                max  = db;
                Log.e(TAG,"最大分贝值："+max);
            }
            mHandler.postDelayed(mUpdateMicStatusTimer, SPACE);
        }
    }

}
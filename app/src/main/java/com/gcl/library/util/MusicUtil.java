package com.gcl.library.util;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

import com.gcl.library.activity.R;

/**
 * Created by gcl on 2017/3/14.
 */

public class MusicUtil {

    private static Context mContext;
    private static View mControlView;
    private static MediaPlayer mPlayer;

    private static RotateAnimation mRotateAnimation;

    // 是否播放
    public static boolean playMusic;

    // 是否暂停音乐
    public static boolean pauseMusicWithSystem;

    private static int i = 0;

    static {
        // music图标旋转动画
        mRotateAnimation = new RotateAnimation(0f, 360f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);

        mRotateAnimation.setRepeatMode(Animation.RESTART);
        mRotateAnimation.setDuration(1000);
        mRotateAnimation.setInterpolator(new LinearInterpolator());
        mRotateAnimation.setRepeatCount(Integer.MAX_VALUE);
    }

    public static void initContext(Context context, View controlView) {
        mContext = context;
        mControlView = controlView;
    }

    // 唉，歌曲列表
    private static int[] mMusicList = new int[]{
            R.raw.always_with_me,
            R.raw.city_in_the_sky,
            R.raw.the_dawn
    };

    private static void startNewMusic() {
        int musicId = getNextMusicId();

        mPlayer = MediaPlayer.create(mContext, musicId);
        mRotateAnimation.reset();
        mControlView.startAnimation(mRotateAnimation);
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mPlayer.release();
                mPlayer = null;
                playMusic = false;
                mRotateAnimation.cancel();
            }
        });
        mPlayer.start();
        playMusic = true;
        pauseMusicWithSystem = false;
    }

    public static void stop() {
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
            playMusic = false;
            mRotateAnimation.cancel();
        }
    }

    /**
     * 播放下一首
     */
    public static void next() {
        stop();
        startNewMusic();
    }


    /**
     * 播放下一首
     */
    private static int getNextMusicId() {
        i++;
        return mMusicList[i % mMusicList.length];
    }

    public static void pause() {
        if (mPlayer != null) {
            mPlayer.pause();
            playMusic = false;
            pauseMusicWithSystem = false;
            mRotateAnimation.cancel();
        }
    }

    public static void pauseWithSystem() {
        if (playMusic) {
            pauseMusicWithSystem = true;
            mPlayer.pause();
            playMusic = false;
            mRotateAnimation.cancel();
        }
    }

    public static void start() {
        if (mPlayer != null) {
            mPlayer.start();
            mRotateAnimation.reset();
            mControlView.startAnimation(mRotateAnimation);
        } else {
            startNewMusic();
        }
        pauseMusicWithSystem = false;
        playMusic = true;
    }

    public static void release() {
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
            playMusic = false;
            pauseMusicWithSystem = false;
            mRotateAnimation.cancel();
        }
    }
}

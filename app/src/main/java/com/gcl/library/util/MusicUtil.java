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

    private static MediaPlayer mPlayer;

    private static RotateAnimation mRotateAnimation;

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

    // 唉，歌曲列表
    private static int[] mMusicList = new int[]{
            R.raw.music_000,
            R.raw.music_001,
            R.raw.music_002,
            R.raw.music_003,
            R.raw.music_004,
            R.raw.music_005,
            R.raw.music_006,
            R.raw.music_007,
            R.raw.music_008,
            R.raw.music_009,
            R.raw.music_010,
            R.raw.music_011,
            R.raw.music_012,
            R.raw.music_013,
            R.raw.music_014,
            R.raw.music_015,
            R.raw.music_016,
            R.raw.music_017,
            R.raw.music_018,
            R.raw.music_019,
            R.raw.music_020,
            R.raw.music_021,
            R.raw.music_022,
            R.raw.music_023,
            R.raw.music_024,
            R.raw.music_025,
            R.raw.music_026,
            R.raw.music_027,
            R.raw.music_028,
            R.raw.music_029,
            R.raw.music_030,
            R.raw.music_031,
            R.raw.music_032,
            R.raw.music_033,
            R.raw.music_034,
            R.raw.music_035,
            R.raw.music_036,
            R.raw.music_037,
            R.raw.music_038,
            R.raw.music_039,
            R.raw.music_040,
            R.raw.music_041,
            R.raw.music_042,
            R.raw.music_043,
            R.raw.music_044,
            R.raw.music_045,
            R.raw.music_046
    };

    public static boolean playMusic;

    public static void start(Context context, final View view) {
        int musicId = getRandomMusic();
        if (musicId == -1) return;

        mPlayer = MediaPlayer.create(context, musicId);
        mRotateAnimation.reset();
        view.setVisibility(View.VISIBLE);
        view.startAnimation(mRotateAnimation);
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mPlayer.release();
                mPlayer = null;
                playMusic = false;
                mRotateAnimation.cancel();
                view.clearAnimation();
                view.setVisibility(View.GONE);
            }
        });
        mPlayer.start();
        playMusic = true;
    }

    public static void stop(View view) {
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
            playMusic = false;
            mRotateAnimation.cancel();
            view.clearAnimation();
            view.setVisibility(View.GONE);
        }
    }

    private static int getRandomMusic() {
        return mMusicList[(int) (Math.random() * 10000) % mMusicList.length];
    }
}

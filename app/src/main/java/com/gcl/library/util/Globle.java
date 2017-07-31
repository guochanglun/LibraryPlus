package com.gcl.library.util;

import com.gcl.library.bean.Catalog;

import java.util.List;

/**
 * 保存全局对象
 * Created by gcl on 2017/3/10.
 */

public class Globle {

    // 登录的用户名
    public static String USER_NAME = "辉夜姬";

    // 判断是否还在自己的app内部，就不用停掉音乐
    public static boolean IN_MY_APP;

    // 是否提示用户正在播放本地歌曲
    public static boolean IS_TOAST_USER_PLAY_NATIVE_MUSIC;
}

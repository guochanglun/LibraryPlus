package com.gcl.library.robot;

import java.util.List;

/**
 * 文本类消息
 * Created by gcl on 2017/3/7.
 */
public class NewsMsg {

    private int code;
    private String text;
    private List<News> list;

    public List<News> getList() {
        return list;
    }

    public void setList(List<News> list) {
        this.list = list;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

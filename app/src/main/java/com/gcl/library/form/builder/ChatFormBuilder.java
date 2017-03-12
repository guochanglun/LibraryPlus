package com.gcl.library.form.builder;

import com.gcl.library.util.Const;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class ChatFormBuilder {

    private String key;
    private String info;
    private String loc;
    private String userid;

    public ChatFormBuilder setKey(String key) {
        this.key = key;
        return this;
    }

    public ChatFormBuilder setInfo(String info) {
        this.info = info;
        return this;
    }

    public ChatFormBuilder setLoc(String loc) {
        this.loc = loc;
        return this;
    }

    public ChatFormBuilder setUserId(String userid) {
        this.userid = userid;
        return this;
    }

    public UrlEncodedFormEntity build() throws UnsupportedEncodingException {

        if (this.userid == null) {
            setUserId(Const.USER_ID);
        }
        if (this.loc == null) {
            setUserId(Const.USER_LOC);
        }

        if (this.key == null) {
            setUserId(Const.API_KEY);
        }
        ArrayList<NameValuePair> formparams = new ArrayList<>();
        formparams.add(new BasicNameValuePair("key", this.key));
        formparams.add(new BasicNameValuePair("info", this.info));
        formparams.add(new BasicNameValuePair("loc", this.loc));
        formparams.add(new BasicNameValuePair("userid", this.userid));

        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams);
        return entity;
    }

}

package com.gcl.library.form.builder;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class LoginFormBuilder {

    private String number;
    private String passwd;

    public LoginFormBuilder setNumber(String number) {
        this.number = number;
        return this;
    }

    public LoginFormBuilder setPasswd(String passwd) {
        this.passwd = passwd;
        return this;
    }

    public UrlEncodedFormEntity build() throws UnsupportedEncodingException {

        if (this.number == null) {
            throw new RuntimeException("用户名不能为空");
        }
        if (this.passwd == null) {
            throw new RuntimeException("密码不能为空");
        }
        ArrayList<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair("number", this.number));
        formparams.add(new BasicNameValuePair("passwd", this.passwd));
        formparams.add(new BasicNameValuePair("select", "cert_no"));
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams);
        return entity;
    }

}

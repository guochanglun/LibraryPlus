package com.gcl.library.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.gcl.library.db.DatabaseHelper;
import com.gcl.library.db.User;
import com.gcl.library.service.HtmlService;
import com.gcl.library.util.NetState;
import com.gcl.library.util.ToastUtil;

import java.sql.SQLException;

public class LoginActivity extends AppCompatActivity {

    private ImageView avatar;
    private EditText name;
    private EditText pwd;
    private Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // init
        avatar = (ImageView) findViewById(R.id.login_avatar);
        name = (EditText) findViewById(R.id.login_name);
        pwd = (EditText) findViewById(R.id.login_pwd);
        login = (Button) findViewById(R.id.login_btn);

        login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                // 检查网络连接
                if (!NetState.with(LoginActivity.this).detectNetState()) {
                    ToastUtil.showMsg(LoginActivity.this, "网络未连接");
                    return;
                }

                final String nameString = name.getText().toString();
                final String pwdString = pwd.getText().toString();

                if (nameString.trim().length() == 0 || pwdString.trim().length() == 0) {
                    return;
                }

                new AsyncTask<String, Integer, Boolean>() {
                    @Override
                    protected Boolean doInBackground(String... params) {
                        return HtmlService.login(params[0], params[1]);
                    }

                    @Override
                    protected void onPostExecute(Boolean result) {
                        if (result) {
                            ToastUtil.showMsg(LoginActivity.this, "登录成功");

                            // 保存用户名密码
                            User user = new User(1, nameString, pwdString);

                            try {
                                DatabaseHelper.getHelper(LoginActivity.this).getUserDao().createOrUpdate(user);
                            } catch (SQLException e) {
                                ToastUtil.showMsg(LoginActivity.this, "夭寿啦！不能保存账号密码!!");
                                e.printStackTrace();
                            }
                            // 跳转
                            LoginActivity.this.startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            LoginActivity.this.finish();
                        } else {
                            ToastUtil.showMsg(LoginActivity.this, "天啦撸！账号或密码错误！！");
                            pwd.setText("");
                        }
                    }
                }.execute(nameString, pwdString);
            }

        });
    }
}

package com.gcl.library.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.gcl.library.bean.Catalog;
import com.gcl.library.db.DatabaseHelper;
import com.gcl.library.db.User;
import com.gcl.library.service.HtmlService;
import com.gcl.library.util.Globle;
import com.gcl.library.util.NetState;
import com.gcl.library.util.ToastUtil;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        //获得当前窗体对象
        Window window = SplashScreenActivity.this.getWindow();
        //设置当前窗体为全屏显示
        window.setFlags(flag, flag);

        setContentView(R.layout.activity_splash_screen);

        // 检查网络连接
        if (!NetState.with(SplashScreenActivity.this).detectNetState()) {
            ToastUtil.showMsg(SplashScreenActivity.this, "喂，去联网！！");
            finish();
        }

        try {
            Dao<User, Integer> mUserDao = DatabaseHelper.getHelper(SplashScreenActivity.this).getUserDao();
            List<User> users = mUserDao.queryForAll();
            if (users.size() == 0) {
                startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
                finish();
            } else {
                final User user = users.get(0);

                new AsyncTask<String, Integer, Boolean>() {
                    @Override
                    protected Boolean doInBackground(String... params) {
                        boolean login = HtmlService.login(params[0], params[1]);
                        if (login) {
                            Globle.USER_NAME = HtmlService.getUserName();
                        }
                        return login;
                    }

                    @Override
                    protected void onPostExecute(Boolean aBoolean) {
                        if (aBoolean) {
                            startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                            finish();
                        } else {
                            ToastUtil.showMsg(SplashScreenActivity.this, "账号或密码错误！");
                            startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
                            finish();
                        }
                    }
                }.execute(user.getName(), user.getPwd());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

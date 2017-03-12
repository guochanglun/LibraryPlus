package com.gcl.library.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.gcl.library.util.ToastUtil;
import com.tencent.smtt.sdk.QbSdk;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //声明相关变量
    private Toolbar toolbar;
    private DrawerLayout mDrawerLayout;

    private TextView menuBorrowed;
    private TextView menuSaved;
    private TextView menuSearch;
    private TextView menuArticle;
    private TextView menuRobot;
    private TextView menuLogout;

    private List<TextView> menuList;

    private FragmentManager fragmentManager;

    // 当前显示的fragment的ID
    private int mCurrentFragmentId = R.id.menu_borrowed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化QQ X5内核
        QbSdk.initX5Environment(MainActivity.this, null);

        fragmentManager = getSupportFragmentManager();

        //获取控件
        findViews();

        //设置Toolbar标题
        toolbar.setTitle("图书馆");
        //设置标题颜色
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        setSupportActionBar(toolbar);
        //设置返回键可用
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //创建返回键，并实现打开关/闭监听
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.open, R.string.close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };

        mDrawerToggle.syncState();
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        initFragment();
        selectMenu(menuBorrowed, BorrowedFragment.newInstance());

        // 按钮添加监听
        for (TextView tv : menuList) {
            tv.setOnClickListener(this);
        }
    }

    // 添加fragment
    private void initFragment() {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.container, ArticleFragment.newInstance());
        fragmentTransaction.add(R.id.container, SearchFragment.newInstance());
        fragmentTransaction.add(R.id.container, RobotFragment.newInstance());
        fragmentTransaction.add(R.id.container, SavedFragment.newInstance());
        fragmentTransaction.add(R.id.container, BorrowedFragment.newInstance());
        fragmentTransaction.commit();
    }

    // 切换fragment
    private void hideAllAndShowFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.hide(ArticleFragment.newInstance());
        fragmentTransaction.hide(SearchFragment.newInstance());
        fragmentTransaction.hide(RobotFragment.newInstance());
        fragmentTransaction.hide(SavedFragment.newInstance());
        fragmentTransaction.hide(BorrowedFragment.newInstance());
        fragmentTransaction.show(fragment);
        fragmentTransaction.commit();
    }

    private void findViews() {
        toolbar = (Toolbar) findViewById(R.id.tl_custom);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.dl_left);

        // 初始化菜单
        menuList = new ArrayList<>(7);

        menuBorrowed = (TextView) findViewById(R.id.menu_borrowed);
        menuList.add(menuBorrowed);

        menuSaved = (TextView) findViewById(R.id.menu_saved);
        menuList.add(menuSaved);

        menuSearch = (TextView) findViewById(R.id.menu_search);
        menuList.add(menuSearch);

        menuArticle = (TextView) findViewById(R.id.menu_article);
        menuList.add(menuArticle);

        menuRobot = (TextView) findViewById(R.id.menu_robot);
        menuList.add(menuRobot);

        menuLogout = (TextView) findViewById(R.id.menu_logout);
        menuList.add(menuLogout);
    }

    @Override
    public void onClick(View view) {
        mCurrentFragmentId = view.getId();
        switch (view.getId()) {
            case R.id.menu_borrowed:
                selectMenu(menuBorrowed, BorrowedFragment.newInstance());
                break;
            case R.id.menu_search:
                selectMenu(menuSearch, SearchFragment.newInstance());
                break;
            case R.id.menu_saved:
                SavedFragment.newInstance().refreshData();
                selectMenu(menuSaved, SavedFragment.newInstance());
                break;
            case R.id.menu_article:
                selectMenu(menuArticle, ArticleFragment.newInstance());
                break;
            case R.id.menu_robot:
                selectMenu(menuRobot, RobotFragment.newInstance());
                break;
            case R.id.menu_logout:
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
                break;
        }
    }

    private void selectMenu(TextView view, Fragment fragment) {
        // 重置菜单颜色
        for (TextView tv : menuList) {
            tv.setBackground(getResources().getDrawable(R.drawable.menu_ripple));
            tv.setTextColor(getResources().getColor(R.color.menuTextColor));
        }

        toolbar.setTitle(view.getText());

        // 替换fragment
        hideAllAndShowFragment(fragment);

        // 关闭侧滑菜单
        mDrawerLayout.closeDrawer(GravityCompat.START);

        //设置选中菜单颜色
        view.setTextColor(getResources().getColor(R.color.menuSelectedTextColor));
    }

    /**
     * 监听back键，根据不同的fragment做出不同的相应
     */
    long time0 = 0;

    @Override
    public void onBackPressed() {
        switch (mCurrentFragmentId) {
            case R.id.menu_borrowed:
                long time = System.currentTimeMillis();
                if (time - time0 > 2000) {
                    ToastUtil.showMsg(this, "再按一次退出程序");
                    time0 = time;
                } else {
                    finish();
                }
                break;
            default:
                // 返回BorrowedFragment
                mCurrentFragmentId = R.id.menu_borrowed;
                selectMenu(menuBorrowed, BorrowedFragment.newInstance());
        }
    }
}

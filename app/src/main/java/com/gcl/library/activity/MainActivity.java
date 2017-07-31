package com.gcl.library.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gcl.library.db.DatabaseHelper;
import com.gcl.library.util.Globle;
import com.gcl.library.util.ToastUtil;
import com.tencent.smtt.sdk.QbSdk;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //声明相关变量
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;

    private ImageView mMenuAvatar;
    private TextView mMenuUserName;
    private TextView mMenuBorrowed;
    private TextView mMenuSaved;
    private TextView mMenuSearch;
    private TextView mMenuRobot;
    private TextView mMenuLogout;

    private List<TextView> mMenuList;

    private FragmentManager mFragmentManager;

    // 当前显示的fragment的ID
    private int mCurrentFragmentId = R.id.menu_borrowed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化QQ X5内核
        QbSdk.initX5Environment(MainActivity.this, null);

        mFragmentManager = getSupportFragmentManager();

        //获取控件
        findViews();

        //设置Toolbar标题
        mToolbar.setTitle("图书馆");
        //设置标题颜色
        mToolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        setSupportActionBar(mToolbar);
        //设置返回键可用
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //打开关/闭监听
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.open, R.string.close) {
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
        selectMenu(mMenuBorrowed, BorrowedFragment.newInstance());

        // 按钮添加监听
        for (TextView tv : mMenuList) {
            tv.setOnClickListener(this);
        }
    }

    // 添加fragment
    private void initFragment() {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.container, SearchFragment.newInstance());
        fragmentTransaction.add(R.id.container, RobotFragment.newInstance());
        fragmentTransaction.add(R.id.container, SavedFragment.newInstance());
        fragmentTransaction.add(R.id.container, BorrowedFragment.newInstance());
        fragmentTransaction.commit();
    }

    // 切换fragment
    private void hideAllAndShowFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.hide(SearchFragment.newInstance());
        fragmentTransaction.hide(RobotFragment.newInstance());
        fragmentTransaction.hide(SavedFragment.newInstance());
        fragmentTransaction.hide(BorrowedFragment.newInstance());
        fragmentTransaction.show(fragment);
        fragmentTransaction.commit();
    }

    private void findViews() {

        mToolbar = (Toolbar) findViewById(R.id.tl_custom);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.dl_left);

        // 初始化菜单
        mMenuList = new ArrayList<>(7);

        mMenuUserName = (TextView) findViewById(R.id.menu_username);
        mMenuUserName.setText(Globle.USER_NAME);

        mMenuAvatar = (ImageView) findViewById(R.id.menu_avatar);

        mMenuBorrowed = (TextView) findViewById(R.id.menu_borrowed);
        mMenuList.add(mMenuBorrowed);

        mMenuSaved = (TextView) findViewById(R.id.menu_saved);
        mMenuList.add(mMenuSaved);

        mMenuSearch = (TextView) findViewById(R.id.menu_search);
        mMenuList.add(mMenuSearch);

        mMenuRobot = (TextView) findViewById(R.id.menu_robot);
        mMenuList.add(mMenuRobot);

        mMenuLogout = (TextView) findViewById(R.id.menu_logout);
        mMenuList.add(mMenuLogout);
    }

    @Override
    public void onClick(View view) {
        mCurrentFragmentId = view.getId();
        switch (view.getId()) {
            case R.id.menu_borrowed:
                selectMenu(mMenuBorrowed, BorrowedFragment.newInstance());
                break;
            case R.id.menu_search:
                selectMenu(mMenuSearch, SearchFragment.newInstance());
                break;
            case R.id.menu_saved:
                SavedFragment.newInstance().refreshData();
                selectMenu(mMenuSaved, SavedFragment.newInstance());
                break;
            case R.id.menu_robot:
                selectMenu(mMenuRobot, RobotFragment.newInstance());
                break;
            case R.id.menu_logout:
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
                break;
        }
    }

    private void selectMenu(TextView view, Fragment fragment) {
        // 重置菜单颜色
        for (TextView tv : mMenuList) {
            tv.setBackground(getResources().getDrawable(R.drawable.menu_ripple));
            tv.setTextColor(getResources().getColor(R.color.menuTextColor));
        }

        mToolbar.setTitle(view.getText());

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
                selectMenu(mMenuBorrowed, BorrowedFragment.newInstance());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DatabaseHelper.getHelper(this).close();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Globle.IN_MY_APP = false;
    }
}

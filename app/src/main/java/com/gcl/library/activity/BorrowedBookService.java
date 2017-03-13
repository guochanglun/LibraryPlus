package com.gcl.library.activity;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.gcl.library.bean.BorrowBook;
import com.gcl.library.db.DatabaseHelper;
import com.gcl.library.db.User;
import com.gcl.library.service.HtmlService;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BorrowedBookService extends RemoteViewsService {

    public BorrowedBookService() {
    }

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewsFactory(this, intent);
    }

    private class ListRemoteViewsFactory implements RemoteViewsFactory {

        private Context mContext;
        private int mWidgetId;

        private Dao<User, Integer> userDao;

        private List<BorrowBook> mBorrowedBookList = new ArrayList<>(0);

        public ListRemoteViewsFactory(Context context, Intent intent) {
            mContext = context;
            mWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);

            try {
                userDao = DatabaseHelper.getHelper(context).getUserDao();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onCreate() {
        }

        @Override
        public void onDataSetChanged() {
            refreshData();
        }

        @Override
        public void onDestroy() {
            mBorrowedBookList.clear();
        }

        @Override
        public int getCount() {
            return mBorrowedBookList.size();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            BorrowBook book = mBorrowedBookList.get(position);
            RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.widget_borrowed_book_item);
            views.setTextViewText(R.id.widget_order, position + 1 + "");
            views.setTextViewText(R.id.widget_book_name, book.getBookName());
            views.setTextViewText(R.id.widget_book_in, "应还日期：" + book.getReturnDate());

            Intent intent = new Intent();
            intent.setAction(BorrowedBookWidget.BOOK_LIST_ITEM_CLICK_ACTION);
            views.setOnClickFillInIntent(R.id.borrowed_book_item_root_layout, intent);

            return views;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        private void refreshData() {
            // 从网络上获取并更新数据
            try {
                List<User> users = userDao.queryForAll();
                if (users.size() != 0) {
                    User user = users.get(0);
                    boolean result = HtmlService.login(user.getName(), user.getPwd());
                    if (result) {
                        mBorrowedBookList = HtmlService.getBorrowedBook();
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

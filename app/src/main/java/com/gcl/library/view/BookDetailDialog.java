package com.gcl.library.view;

import android.animation.Animator;
import android.app.Dialog;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gcl.library.activity.R;
import com.gcl.library.bean.Book;
import com.gcl.library.bean.DetailBook;
import com.gcl.library.db.DatabaseHelper;
import com.gcl.library.db.LovedBook;
import com.gcl.library.db.SavedBook;
import com.gcl.library.util.ToastUtil;
import com.j256.ormlite.dao.Dao;
import com.squareup.picasso.Picasso;

import java.sql.SQLException;
import java.util.List;

import static com.gcl.library.activity.R.id.book_detail_pic;

/**
 * Created by gcl on 2017/3/5.
 */

public class BookDetailDialog extends Dialog {

    private Context mContext;
    private DetailBook mBook;
    private Book mOriginBook;

    private boolean mLove;
    private boolean mSaved;

    private Dao<SavedBook, Integer> mSavedBookDao;
    private Dao<LovedBook, Integer> mLovedBookDao;

    private Animator mAnimator;
    private Rect mRect;
    private PointF mPoint;
    private View mDialogView;

    public BookDetailDialog(Context context, DetailBook detailBook, Book book, PointF point) {
        super(context, R.style.FullHeightDialog);
        this.mContext = context;
        this.mBook = detailBook;
        this.mOriginBook = book;
        this.mPoint = point;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 加载view
        final View view = LinearLayout.inflate(this.mContext, R.layout.book_item_detail, null);

        // 获取控件
        ImageView pic = (ImageView) view.findViewById(book_detail_pic);
        TextView title = (TextView) view.findViewById(R.id.book_detail_title);
        TextView author = (TextView) view.findViewById(R.id.book_detail_author);
        TextView publisher = (TextView) view.findViewById(R.id.book_detail_publisher);
        TextView pubdate = (TextView) view.findViewById(R.id.book_detail_pubdate);
        TextView price = (TextView) view.findViewById(R.id.book_detail_price);
        TextView summary = (TextView) view.findViewById(R.id.book_detail_summary);
        TextView catalog = (TextView) view.findViewById(R.id.book_detail_catalog);
        final ImageView love = (ImageView) view.findViewById(R.id.book_detail_love);
        final ImageView save = (ImageView) view.findViewById(R.id.book_detail_save);

        // 给控件赋值
        Picasso.with(mContext).load(mBook.getImage()).into(pic);
        title.setText(mBook.getTitle());
        String a = "";
        for (String s : mBook.getAuthor()) {
            a += s + " ";
        }
        author.setText("作者：" + a);
        pubdate.setText("出版时间：" + mBook.getPubdate());
        publisher.setText("出版社：" + mBook.getPublisher());
        price.setText("豆瓣出售：" + mBook.getPrice());
        summary.setText(mBook.getSummary());
        catalog.setText(mBook.getCatalog());

        // 检查是否收藏或稀罕
        try {
            mSavedBookDao = DatabaseHelper.getHelper(mContext).getSavedBookDao();
            mLovedBookDao = DatabaseHelper.getHelper(mContext).getLovedBookDao();

            List<SavedBook> result = mSavedBookDao.queryForEq("bookName", mBook.getTitle());
            List<LovedBook> result1 = mLovedBookDao.queryForEq("bookName", mBook.getTitle());

            if (result.size() != 0) {
                mSaved = true;
                save.setImageDrawable(getContext().getDrawable(R.mipmap.saved));
            }

            if (result1.size() != 0) {
                mLove = true;
                love.setImageDrawable(getContext().getDrawable(R.mipmap.loved));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        love.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (mLove) {
                        List<LovedBook> result = mLovedBookDao.queryForEq("bookName", mBook.getTitle());
                        if (result.size() != 0) {
                            mLovedBookDao.deleteById(result.get(0).getId());
                            love.setImageDrawable(getContext().getDrawable(R.mipmap.love));
                            mLove = false;
                        }
                    } else {
                        LovedBook lovedBook = new LovedBook();
                        lovedBook.setBookName(mBook.getTitle());
                        mLovedBookDao.create(lovedBook);
                        love.setImageDrawable(getContext().getDrawable(R.mipmap.loved));
                        ToastUtil.showMsg(mContext, "Wo喜欢这本书");
                        mLove = true;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 保存收藏的书籍
                try {
                    if (!mSaved) {
                        SavedBook book = new SavedBook();
                        book.setBookName(mBook.getTitle());
                        book.setAuthor(mBook.getOrigin_title());
                        book.setPublishInfo(mBook.getPublisher() + "/" + mBook.getPubdate());
                        book.setISBN(mOriginBook.getISBN());
                        book.setHref(mOriginBook.getHref());
                        book.setNum(mOriginBook.getNum());
                        mSavedBookDao.create(book);
                        save.setImageDrawable(getContext().getDrawable(R.mipmap.saved));
                        ToastUtil.showMsg(mContext, "收藏成功");

                        mSaved = true;
                    } else {
                        List<SavedBook> result = mSavedBookDao.queryForEq("bookName", mBook.getTitle());
                        if (result.size() != 0) {
                            mSavedBookDao.deleteById(result.get(0).getId());
                            save.setImageDrawable(getContext().getDrawable(R.mipmap.save));
                            ToastUtil.showMsg(mContext, "取消收藏");

                            mSaved = false;
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });

        // 设置窗口
        setContentView(view);
        Window window = getWindow();
        WindowManager windowManager = window.getWindowManager();
        WindowManager.LayoutParams lp = window.getAttributes();
        Display display = windowManager.getDefaultDisplay();
        mRect = new Rect();
        display.getRectSize(mRect);
        lp.height = (int) (mRect.height() * 0.95);
        lp.width = (int) (mRect.width() * 0.95);
        lp.horizontalMargin = 0;
        lp.verticalMargin = 0;
        window.setAttributes(lp);

        // 设置动画
        view.post(new Runnable() {
            @Override
            public void run() {
                mDialogView = view;
                // 设置动画
                mAnimator = ViewAnimationUtils.createCircularReveal(view, (int) mPoint.x, (int) mPoint.y, 0, mRect.height());
                mAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                mAnimator.setDuration(500);
                mAnimator.start();
            }
        });
    }

    @Override
    public void onBackPressed() {
        dismissMyDialog();
    }

    public void dismissMyDialog() {
        if (mAnimator.isRunning()) {
            mAnimator.cancel();
        }

        final Dialog dialog = this;

        if (this.isShowing()) {
            mAnimator = ViewAnimationUtils.createCircularReveal(mDialogView, (int) mPoint.x, (int) mPoint.y, mRect.height(), 0);
            mAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            mAnimator.setDuration(400);
            mAnimator.start();

            mAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    dialog.dismiss();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
    }
}

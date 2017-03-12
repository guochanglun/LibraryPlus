package com.gcl.library.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gcl.library.activity.R;
import com.gcl.library.bean.DetailBook;
import com.squareup.picasso.Picasso;

import static com.gcl.library.activity.R.id.book_detail_pic;

/**
 * Created by gcl on 2017/3/5.
 */

public class SavedDetailDialog extends Dialog {

    private Context mContext;
    private DetailBook mBook;

    public SavedDetailDialog(Context context, DetailBook detailBook) {
        super(context, R.style.FullHeightDialog);
        this.mContext = context;
        this.mBook = detailBook;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 加载view
        View view = LinearLayout.inflate(this.mContext, R.layout.saved_item_detail, null);

        // 获取控件
        ImageView pic = (ImageView) view.findViewById(book_detail_pic);
        TextView title = (TextView) view.findViewById(R.id.book_detail_title);
        TextView author = (TextView) view.findViewById(R.id.book_detail_author);
        TextView publisher = (TextView) view.findViewById(R.id.book_detail_publisher);
        TextView pubdate = (TextView) view.findViewById(R.id.book_detail_pubdate);
        TextView price = (TextView) view.findViewById(R.id.book_detail_price);
        TextView summary = (TextView) view.findViewById(R.id.book_detail_summary);
        TextView catalog = (TextView) view.findViewById(R.id.book_detail_catalog);

        // 给控件赋值
        Picasso.with(mContext).load(mBook.getImage()).into(pic);
        title.setText(mBook.getTitle());
        author.setText("作者：" + mBook.getOrigin_title());
        pubdate.setText("出版时间：" + mBook.getPubdate());
        publisher.setText("出版社：" + mBook.getPublisher());
        price.setText("豆瓣出售：" + mBook.getPrice());
        summary.setText(mBook.getSummary());
        catalog.setText(mBook.getCatalog());

        // 设置窗口
        setContentView(view);
        Window window = getWindow();
        WindowManager windowManager = window.getWindowManager();
        WindowManager.LayoutParams lp = window.getAttributes();
        Display display = windowManager.getDefaultDisplay();
        Rect rect = new Rect();
        display.getRectSize(rect);
        lp.height = (int) (rect.height() * 0.95);
        lp.width = (int) (rect.width() * 0.95);
        lp.horizontalMargin = 0;
        lp.verticalMargin = 0;
        window.setAttributes(lp);
    }
}

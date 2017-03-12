package com.gcl.library.activity;


import android.graphics.PointF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.ListViewCompat;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gcl.library.bean.Book;
import com.gcl.library.bean.DetailBook;
import com.gcl.library.service.HtmlService;
import com.gcl.library.util.NetState;
import com.gcl.library.util.ToastUtil;
import com.gcl.library.view.BookDetailDialog;
import com.gcl.library.view.LoadingDialog;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private static SearchFragment fragment = new SearchFragment();

    public static SearchFragment newInstance() {
        return fragment;
    }

    private SearchView mSearchView;
    private ListViewCompat mSearchListView;
    private List<Book> mBookList;

    // 是否加载图书详细信息
    private boolean mIsLoadBookDetail = false;

    // 保存触摸的坐标
    private PointF mTouchPoint = new PointF(0, 0);

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_search, container, false);

        // 获取控件
        mSearchView = (SearchView) view.findViewById(R.id.search_view);
        mSearchListView = (ListViewCompat) view.findViewById(R.id.search_book_list);

        // 设置mSearchListView
        final SearchAdapter adapter = new SearchAdapter();
        mSearchListView.setAdapter(adapter);

        // 书籍详细介绍
        mSearchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // 检查网络连接
                if (!NetState.with(getContext()).detectNetState()) {
                    ToastUtil.showMsg(getContext(), "网络未连接");
                    return;
                }

                // 如果有加载任务，方法返回
                if (mIsLoadBookDetail) return;

                // 记录坐标
                final PointF touchPoint = mTouchPoint;

                //加载任务开始
                mIsLoadBookDetail = true;

                final Book book = mBookList.get(position);
                String isbn = book.getISBN();
                String href = book.getHref();

                new AsyncTask<String, Integer, DetailBook>() {
                    @Override
                    protected DetailBook doInBackground(String... params) {
                        String isbn = params[0];
                        String href = params[1];

                        if (isbn == null) {
                            String result = HtmlService.getBookIsbn(href);
                            if (result != null) {
                                result = result.split("/")[0].split(" ")[0];
                                book.setISBN(result);
                            }
                        }

                        if (book.getISBN() != null) {
                            DetailBook detailBook = HtmlService.getBookDetail(book.getISBN());
                            return detailBook;
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(DetailBook detailBook) {

                        // 加载结束
                        mIsLoadBookDetail = false;

                        if (detailBook != null) {
                            BookDetailDialog dialog = dialog(detailBook, book, touchPoint);
                            dialog.show();
                        } else {
                            ToastUtil.showMsg(getContext(), "抱歉，没有找到详细介绍");
                        }
                    }
                }.execute(isbn, href);
            }
        });

        // 让mSearchView失去焦点，从而隐藏软键盘
        mSearchListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mSearchView.clearFocus();
                return false;
            }
        });

        // 设置mSearchView
        mSearchView.onActionViewExpanded();

        // 搜索书籍
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {

                mSearchView.clearFocus();

                //创建并显示加载框
                final LoadingDialog dialog = new LoadingDialog(getContext());
                dialog.show();

                // 检查网络连接
                if (!NetState.with(getContext()).detectNetState()) {
                    ToastUtil.showMsg(getContext(), "网络未连接");
                    return true;
                }

                // 异步任务获取搜索结果
                new AsyncTask<String, Integer, List<Book>>() {

                    @Override
                    protected List<Book> doInBackground(String... params) {
                        return HtmlService.getSearchBook(params[0]);
                    }

                    protected void onPostExecute(List<Book> result) {

                        // 隐藏加载框
                        dialog.dismiss();

                        if (result.size() > 0) {
                            mBookList = result;
                            adapter.setData(result);
                            // listview滚回顶部
                            mSearchListView.setSelection(0);
                        } else {
                            ToastUtil.showMsg(getContext(), "呵呵,没找到~");
                        }
                    }
                }.execute(query);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }

        });
        return view;
    }

    private BookDetailDialog dialog(DetailBook book, Book oBook, PointF point) {
        return new BookDetailDialog(getContext(), book, oBook, point);
    }

    /**
     * SearchAdapter
     */
    private class SearchAdapter extends BaseAdapter {


        private List<Book> list = new ArrayList<>(0);

        // 注入list
        public void setData(List<Book> bookList) {
            this.list = bookList;
            this.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();

                convertView = View.inflate(getContext(), R.layout.search_book_item, null);
                holder.rest = (TextView) convertView.findViewById(R.id.search_rest);
                holder.name = (TextView) convertView.findViewById(R.id.search_name);
                holder.author = (TextView) convertView.findViewById(R.id.search_author);
                holder.publish = (TextView) convertView.findViewById(R.id.search_publish);
                holder.total = (TextView) convertView.findViewById(R.id.search_total);
                holder.place = (TextView) convertView.findViewById(R.id.search_place);
                convertView.setTag(holder);

                convertView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        mTouchPoint.set(event.getRawX(), event.getRawY());
                        return false;
                    }
                });
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final Book book = list.get(position);
            holder.name.setText(book.getBookName());
            holder.rest.setText(book.getAvailable());
            holder.author.setText(book.getAuthor());
            holder.publish.setText(book.getPublishInfo());
            holder.total.setText(book.getCount());
            holder.place.setText(book.getNum());

            return convertView;
        }

    }

    class ViewHolder {
        TextView name;
        TextView rest;
        TextView total;
        TextView place;
        TextView author;
        TextView publish;
    }
}

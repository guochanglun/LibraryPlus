package com.gcl.library.activity;


import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.ListViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gcl.library.bean.DetailBook;
import com.gcl.library.db.DatabaseHelper;
import com.gcl.library.db.SavedBook;
import com.gcl.library.service.HtmlService;
import com.gcl.library.util.NetState;
import com.gcl.library.util.ToastUtil;
import com.gcl.library.view.SavedDetailDialog;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SavedFragment extends Fragment {

    private static SavedFragment fragment = new SavedFragment();

    private ListViewCompat mSavedListView;
    private List<SavedBook> mSavedBook = new ArrayList<>(0);
    private SavedAdapter mSavedAdapter;

    public static SavedFragment newInstance() {
        return fragment;
    }

    private Dao<SavedBook, Integer> mSavedBookDao;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_saved, container, false);
        mSavedListView = (ListViewCompat) view.findViewById(R.id.saved_book_list);

        try {
            mSavedBookDao = DatabaseHelper.getHelper(getContext()).getSavedBookDao();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // 点击查看详细信息
        mSavedListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // 检查网络连接
                if (!NetState.with(getContext()).detectNetState()) {
                    ToastUtil.showMsg(getContext(), "网络未连接");
                    return;
                }

                final SavedBook book = mSavedBook.get(position);
                String isbn = book.getISBN();
                String href = book.getHref();

                new AsyncTask<String, Integer, DetailBook>() {
                    @Override
                    protected DetailBook doInBackground(String... params) {
                        String isbn = params[0];
                        String href = params[1];

                        // 获取isbn
                        if (isbn == null) {
                            String result = HtmlService.getBookIsbn(href);
                            if (result != null) {
                                result = result.split("/")[0];
                                book.setISBN(result);
                            }
                        }

                        // 获取详情
                        if (book.getISBN() != null) {
                            DetailBook detailBook = HtmlService.getBookDetail(book.getISBN());
                            return detailBook;
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(DetailBook detailBook) {
                        if (detailBook != null) {
                            SavedDetailDialog dialog = new SavedDetailDialog(getContext(), detailBook);
                            dialog.show();
                        } else {
                            ToastUtil.showMsg(getContext(), "抱歉，没有找到详细介绍");
                        }
                    }
                }.execute(isbn, href);
            }
        });

        // longclick删除收藏
        mSavedListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                final SavedBook book = mSavedBook.get(position);

                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("呵呵");
                builder.setMessage("删除" + book.getBookName() + "?");
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            List<SavedBook> result = mSavedBookDao.queryForEq("bookName", book.getBookName());
                            if (result.size() != 0) {
                                SavedBook sb = result.get(0);
                                mSavedBookDao.deleteById(sb.getId());
                                mSavedBook.remove(position);
                                mSavedAdapter.setData(mSavedBook);
                            }
                            dialog.dismiss();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            }
        });

        // 设置adapter
        mSavedAdapter = new SavedAdapter();
        mSavedListView.setAdapter(mSavedAdapter);

        //刷新列表
        refreshData();

        return view;
    }

    // 刷新savedBook列表
    public void refreshData() {
        try { // 从数据库中获取信息
            mSavedBook = mSavedBookDao.queryForAll();
            mSavedAdapter.setData(mSavedBook);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * SearchAdapter
     */
    private class SavedAdapter extends BaseAdapter {

        private List<SavedBook> list = new ArrayList<>(0);

        // 注入list
        public void setData(List<SavedBook> bookList) {
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

                convertView = View.inflate(getContext(), R.layout.saved_book_item, null);
                holder.name = (TextView) convertView.findViewById(R.id.saved_name);
                holder.author = (TextView) convertView.findViewById(R.id.saved_author);
                holder.publish = (TextView) convertView.findViewById(R.id.saved_publish);
                holder.place = (TextView) convertView.findViewById(R.id.saved_place);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            SavedBook book = list.get(position);
            holder.name.setText(book.getBookName());
            holder.author.setText(book.getAuthor());
            holder.publish.setText(book.getPublishInfo());
            holder.place.setText(book.getNum());

            return convertView;
        }

    }

    class ViewHolder {
        TextView name;
        TextView place;
        TextView author;
        TextView publish;
    }
}

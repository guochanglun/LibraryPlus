package com.gcl.library.activity;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.ListViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gcl.library.bean.BorrowBook;
import com.gcl.library.service.HtmlService;
import com.gcl.library.util.NetState;
import com.gcl.library.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class BorrowedFragment extends Fragment {

    private static BorrowedFragment fragment = new BorrowedFragment();

    public static BorrowedFragment newInstance() {
        return fragment;
    }

    private ListViewCompat mBorrowedList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_borrowed, container, false);
        mBorrowedList = (ListViewCompat) view.findViewById(R.id.borrowed_book_list);

        // 获取借阅图书信息
        new AsyncTask<String, Integer, List<BorrowBook>>() {

            @Override
            protected List<BorrowBook> doInBackground(String... params) {

                // 检查网络连接
                if (!NetState.with(getContext()).detectNetState()) {
                    ToastUtil.showMsg(getContext(), "没联网，滚！！！");
                    return new ArrayList<BorrowBook>(0);
                }

                return HtmlService.getBorrowedBook();
            }

            @Override
            protected void onPostExecute(List<BorrowBook> result) {
                mBorrowedList.setAdapter(new BorrowAdapter(result));
            }
        }.execute("");

        return view;
    }

    /**
     * adapter
     */
    class BorrowAdapter extends BaseAdapter {

        private List<BorrowBook> list;

        public BorrowAdapter(List<BorrowBook> list) {
            this.list = list;
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

                convertView = View.inflate(getContext(), R.layout.borrowed_book_item, null);
                holder.order = (TextView) convertView.findViewById(R.id.order);
                holder.name = (TextView) convertView.findViewById(R.id.book_name);
                holder.in = (TextView) convertView.findViewById(R.id.book_in);
                holder.out = (TextView) convertView.findViewById(R.id.book_out);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            BorrowBook bBook = list.get(position);
            holder.order.setText((position + 1) + "");
            holder.in.setText("应还日期" + bBook.getReturnDate());
            holder.out.setText("借阅日期" + bBook.getBorrowDate());
            holder.name.setText(bBook.getBookName());
            return convertView;
        }

    }

    class ViewHolder {
        TextView name;
        TextView order;
        TextView in;
        TextView out;
    }
}

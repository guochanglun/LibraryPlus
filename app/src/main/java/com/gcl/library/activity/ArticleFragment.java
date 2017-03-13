package com.gcl.library.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.gcl.library.bean.Catalog;
import com.gcl.library.util.Const;
import com.gcl.library.util.Globle;
import com.gcl.library.util.NetState;
import com.gcl.library.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class ArticleFragment extends Fragment {

    private static ArticleFragment fragment = new ArticleFragment();

    public static ArticleFragment newInstance() {
        return fragment;
    }

    private ListView mListView;

    private ArticleAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_article, container, false);

        mListView = (ListView) view.findViewById(R.id.article_listview);

        View footerView = inflater.inflate(R.layout.list_footer, null);
        mListView.addFooterView(footerView);
        mAdapter = new ArticleAdapter(Globle.ARTCLE_CATALOG);
        mListView.setAdapter(mAdapter);

        // 检查网络连接
        if (!NetState.with(getContext()).detectNetState()) {
            ToastUtil.showMsg(getContext(), "喂，没联网");
            return view;
        }

        // 点击查看文章详情
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (position >= mAdapter.getData().size()) return;

                Catalog catalog = mAdapter.getData().get(position);

                long aId = catalog.getId();
                Intent intent = new Intent(getContext(), WebViewActivity.class);
                intent.putExtra("url", Const.ARTICLE_DETAIL + "?id=" + aId);
                intent.putExtra("title", catalog.getTitle());
                getContext().startActivity(intent);
            }
        });

        return view;
    }

    /**
     * SearchAdapter
     */
    private class ArticleAdapter extends BaseAdapter {

        private List<Catalog> list = new ArrayList<>(0);

        public ArticleAdapter(List<Catalog> list) {
            if (list != null) {
                this.list = list;
            }
        }

        // 注入list
        public void setData(List<Catalog> bookList) {
            this.list = bookList;
            this.notifyDataSetChanged();
        }

        public List<Catalog> getData() {
            return list;
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

                convertView = View.inflate(getContext(), R.layout.article_item, null);
                holder.title = (TextView) convertView.findViewById(R.id.article_title);
                holder.author = (TextView) convertView.findViewById(R.id.article_author);
                holder.time = (TextView) convertView.findViewById(R.id.article_time);
                holder.tag = (TextView) convertView.findViewById(R.id.article_tag);
                holder.summary = (TextView) convertView.findViewById(R.id.article_summary);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final Catalog catalog = list.get(position);
            holder.title.setText(catalog.getTitle());
            holder.author.setText(catalog.getAuthor());
            holder.time.setText(catalog.getTime());
            holder.tag.setText(catalog.getTag());
            holder.summary.setText(catalog.getSummary());

            return convertView;
        }

    }

    class ViewHolder {
        TextView title;
        TextView author;
        TextView time;
        TextView summary;
        TextView tag;
    }
}

package com.gcl.library.activity;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.ListViewCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gcl.library.robot.Food;
import com.gcl.library.robot.FoodMsg;
import com.gcl.library.robot.News;
import com.gcl.library.robot.NewsMsg;
import com.gcl.library.robot.SimpleUserMessage;
import com.gcl.library.robot.TextMsg;
import com.gcl.library.robot.UrlMsg;
import com.gcl.library.service.HtmlService;
import com.gcl.library.util.DateFormatUtil;
import com.gcl.library.util.DensityUtil;
import com.gcl.library.util.Globle;
import com.gcl.library.util.NetState;
import com.gcl.library.util.PhoneWindowUtil;
import com.gcl.library.util.ToastUtil;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

import static com.gcl.library.activity.R.id;

public class RobotFragment extends Fragment {

    private static RobotFragment fragment = new RobotFragment();

    public static RobotFragment newInstance() {
        return fragment;
    }

    private ListViewCompat mChatList;
    private EditText mEditText;
    private Button mSendButton;
    private RobotAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_robot, container, false);

        mChatList = (ListViewCompat) view.findViewById(R.id.robot_list_view);
        mEditText = (EditText) view.findViewById(R.id.robot_edit_text);
        mSendButton = (Button) view.findViewById(R.id.robot_send_button);

        mAdapter = new RobotAdapter();
        mChatList.setAdapter(mAdapter);

        // 打个招呼，消息
        TextMsg textMsg = new TextMsg();
        textMsg.setText("我是小游啦~~");
        mAdapter.getData().add(textMsg);

        // 隐藏软键盘
        mChatList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getActivity()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
                return false;
            }
        });

        // 点击按钮发送
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String chatStr = mEditText.getText().toString();
                if (chatStr.trim().length() == 0) {
                    return;
                }

                // 检查网络连接
                if (!NetState.with(getContext()).detectNetState()) {
                    ToastUtil.showMsg(getContext(), "你特么没联网，滚！！");
                    return;
                }

                mSendButton.setEnabled(false);

                // 添加用户消息
                final SimpleUserMessage simpleUserMessage = new SimpleUserMessage();
                simpleUserMessage.setInfo(chatStr);
                mAdapter.getData().add(simpleUserMessage);
                mChatList.setSelection(mAdapter.getData().size() - 1);

                new AsyncTask<String, Integer, Object>() {
                    @Override
                    protected Object doInBackground(String... params) {

                        return HtmlService.getChatMessage(simpleUserMessage);
                    }

                    @Override
                    protected void onPostExecute(Object o) {
                        if (o != null) {
                            mAdapter.getData().add(o);
                            mChatList.setSelection(mAdapter.getData().size() - 1);
                            mEditText.setText("");
                        }
                        mSendButton.setEnabled(true);
                    }
                }.execute(chatStr);
            }
        });
        return view;
    }

    /**
     * adapter
     */
    private class RobotAdapter extends BaseAdapter {

        private List<Object> list = new ArrayList<>(0);

        // 注入list
        public void setData(List<Object> bookList) {
            this.list = bookList;
            this.notifyDataSetChanged();
        }

        public List<Object> getData() {
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
                Object obj = this.list.get(position);

            // 用户发送的消息
            if (obj.getClass() == SimpleUserMessage.class) {
                SimpleUserMessage msg = (SimpleUserMessage) obj;
                convertView = LinearLayout.inflate(getContext(), R.layout.rebot_chat_to_item, null);
                TextView textView = (TextView) convertView.findViewById(id.id_to_msg_info);
                TextView time = (TextView) convertView.findViewById(id.id_to_msg_date);
                time.setText(DateFormatUtil.getCurrentDate());
                textView.setText(msg.getInfo());
            }

            // text msg
            else if (obj.getClass() == TextMsg.class) {
                convertView = LinearLayout.inflate(getContext(), R.layout.rebot_textmsg_item, null);
                TextView textView = (TextView) convertView.findViewById(id.id_from_msg_info);
                TextView time = (TextView) convertView.findViewById(id.id_form_msg_date);
                textView.setText(((TextMsg) obj).getText());
                time.setText(DateFormatUtil.getCurrentDate());
            }

            // UrlMsg
            else if (obj.getClass() == UrlMsg.class) {
                final UrlMsg msg = (UrlMsg) obj;
                convertView = LinearLayout.inflate(getContext(), R.layout.robot_urlmsg_item, null);
                TextView textView = (TextView) convertView.findViewById(id.id_from_msg_info);
                TextView url = (TextView) convertView.findViewById(id.id_from_msg_url);

                // 设置下划线
                url.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
                url.getPaint().setAntiAlias(true);

                textView.setText(msg.getText());

                // 点击链接打开url
                url.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), WebViewActivity.class);
                        intent.putExtra("url", msg.getUrl());
                        intent.putExtra("title", "网页链接");

                        // 控制音乐播放
                        Globle.IN_MY_APP = true;

                        getContext().startActivity(intent);
                    }
                });
            }

            // 新闻类消息
            else if (obj.getClass() == NewsMsg.class) {
                final NewsMsg msg = (NewsMsg) obj;

                ViewGroup container = (ViewGroup) LinearLayout.inflate(getContext(), R.layout.robot_newsmsg_container, null);

                container.setBackgroundColor(Color.parseColor("#ffeeeeee"));
                for (int i = 0; i < msg.getList().size(); i++) {

                    final News news = msg.getList().get(i);

                    // 先决条件，必须有图片
                    if (news.getIcon() == null || news.getIcon().length() == 0) {
                        continue;
                    }

                    View divider = LinearLayout.inflate(getContext(), R.layout.robot_divider_view, null);
                    View item = LinearLayout.inflate(getContext(), R.layout.robot_newsmsg_item, null);
                    // divider layput params
                    ViewGroup.LayoutParams dlp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(getContext(), 20));
                    divider.setLayoutParams(dlp);
                    divider.setBackgroundColor(Color.parseColor("#ffeeeeee"));

                    TextView title = (TextView) item.findViewById(id.robot_newsmsg_item_title);
                    final ImageView icon = (ImageView) item.findViewById(R.id.robot_newsmsg_item_icon);
                    TextView source = (TextView) item.findViewById(id.robot_newsmsg_item_source);

                    // 获取window信息
                    Rect windowRect = PhoneWindowUtil.getWindowRect(getActivity());
                    int width = (int) (windowRect.width() * 0.9);

                    // item layout params
                    ViewGroup.LayoutParams ilp = new ViewGroup.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT);
                    item.setLayoutParams(ilp);

                    // 设置title宽度
                    ViewGroup.LayoutParams tlp = title.getLayoutParams();
                    tlp.width = width;
                    title.setLayoutParams(tlp);

                    // 设置source宽度
                    ViewGroup.LayoutParams slp = source.getLayoutParams();
                    slp.width = width;
                    source.setLayoutParams(slp);

                    title.setText(news.getArticle());
                    source.setText("来源：" + news.getSource());

                    // 设置为背景图片
                    Picasso.with(getContext()).load(news.getIcon()).into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            icon.setBackground(new BitmapDrawable(getResources(), bitmap));
                        }

                        @Override
                        public void onBitmapFailed(Drawable errorDrawable) {

                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                        }
                    });

                    // 点击查看新闻
                    item.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getContext(), WebViewActivity.class);
                            intent.putExtra("url", news.getDetailurl());
                            intent.putExtra("title", news.getArticle());

                            // 控制音乐播放
                            Globle.IN_MY_APP = true;

                            getContext().startActivity(intent);
                        }
                    });

                    container.addView(item);

                    if (i != msg.getList().size() - 1) {
                        container.addView(divider);
                    }
                }
                // 赋值
                convertView = container;

            }

            // 菜谱类消息
            else if (obj.getClass() == FoodMsg.class) {

                final FoodMsg msg = (FoodMsg) obj;

                ViewGroup container = (ViewGroup) LinearLayout.inflate(getContext(), R.layout.robot_foodmsg_container, null);

                container.setBackgroundColor(Color.parseColor("#ffeeeeee"));

                for (int i = 0; i < msg.getList().size(); i++) {

                    final Food food = msg.getList().get(i);

                    View divider = LinearLayout.inflate(getContext(), R.layout.robot_divider_view, null);
                    View item = LinearLayout.inflate(getContext(), R.layout.robot_foodmsg_item, null);
                    // divider layput params
                    ViewGroup.LayoutParams dlp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(getContext(), 20));
                    divider.setLayoutParams(dlp);
                    divider.setBackgroundColor(Color.parseColor("#ffeeeeee"));

                    TextView title = (TextView) item.findViewById(id.robot_foodmsg_item_title);
                    TextView source = (TextView) item.findViewById(id.robot_foodmsg_item_info);

                    // 获取window信息
                    Rect windowRect = PhoneWindowUtil.getWindowRect(getActivity());

                    // 计算宽度值
                    int width = (int) (windowRect.width() * 0.9);

                    // item layout params
                    ViewGroup.LayoutParams ilp = new ViewGroup.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT);
                    item.setLayoutParams(ilp);

                    // 设置title宽度
                    ViewGroup.LayoutParams tlp = title.getLayoutParams();
                    tlp.width = width;
                    title.setLayoutParams(tlp);

                    // 设置source宽度
                    ViewGroup.LayoutParams slp = source.getLayoutParams();
                    slp.width = width;
                    source.setLayoutParams(slp);

                    title.setText(food.getName());
                    source.setText(food.getInfo());

                    // 点击查看新闻
                    item.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getContext(), WebViewActivity.class);
                            intent.putExtra("url", food.getDetailurl());
                            intent.putExtra("title", food.getName());

                            // 控制音乐播放
                            Globle.IN_MY_APP = true;

                            getContext().startActivity(intent);
                        }
                    });

                    container.addView(item);

                    if (i != msg.getList().size() - 1) {
                        container.addView(divider);
                    }
                }

                // 赋值
                convertView = container;
            }
            return convertView;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.setData(mAdapter.getData());
    }
}

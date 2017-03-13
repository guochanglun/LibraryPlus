package com.gcl.library.service;

import android.util.Log;

import com.gcl.library.form.Forms;
import com.gcl.library.robot.SimpleUserMessage;
import com.gcl.library.util.Client;
import com.gcl.library.util.Const;

import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.Date;

import static android.content.ContentValues.TAG;

/**
 * <h2>从网络上请求数据</h2>
 * <ul>
 * <li>登录系统</li>
 * <li>获取借阅的书的信息</li>
 * <li>搜索书籍</li>
 * <li>续借书籍</li>
 * </ul>
 */
public class NetService {

    /**
     * 登录到系统
     */
    public static String login(String loginuser, String passwd) {
        HttpPost post = new HttpPost(Const.LOGIN_URL);

        String result = null;
        try {
            UrlEncodedFormEntity entity = Forms.login().setNumber(loginuser)
                    .setPasswd(passwd).build();

            post.setEntity(entity);

            CloseableHttpResponse response = Client.client.execute(post);
            result = EntityUtils.toString(response.getEntity(), "utf-8");
            response.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    // 获取用户姓名
    public static String getUserName() {
        HttpGet get = new HttpGet(Const.NAME_URL);
        get.setHeader("Referer",
                "http://222.206.65.12/reader/redr_cust_result.php");
        String result = null;
        try {
            CloseableHttpResponse response = Client.client.execute(get);
            result = EntityUtils.toString(response.getEntity(), "utf-8");
            response.close();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取借阅的书的信息
     */
    public static String getBorrowedBook() {
        HttpGet get = new HttpGet(Const.BORROW_URL);
        get.setHeader("Referer",
                "http://222.206.65.12/reader/redr_cust_result.php");
        String result = null;
        try {
            CloseableHttpResponse response = Client.client.execute(get);
            result = EntityUtils.toString(response.getEntity(), "utf-8");
            response.close();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 搜索书籍
     */
    public static String search(String bookname)
            throws ClientProtocolException, IOException {
        String param = "?historyCount=1" + "&strSearchType=title"
                + "&match_flag=forward" + "&showmode=list" + "&displaypg=100"
                + "&sort=M_PUB_YEAR" + "&orderby=desc" + "&doctype=ALL"
                + "&strText=" + bookname;

        HttpGet get = new HttpGet(Const.SEARCH_URL + param);

        CloseableHttpResponse response = Client.client.execute(get);

        String result = EntityUtils.toString(response.getEntity(), "utf-8");

        return result;
    }

    /**
     * 续借书籍
     */
    public static String continueBorrow(String id) {
        String url = Const.RENEW_URL + "?bar_code=" + id + "&time="
                + new Date().getTime();
        HttpGet get = new HttpGet(url);
        String result = null;
        try {
            CloseableHttpResponse response = Client.client.execute(get);
            result = EntityUtils.toString(response.getEntity(), "utf-8");
            response.close();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 获取图书的isbn号
     *
     * @param href
     */
    public static String getBookIsbn(String href) {
        String url = Const.BOOK_ISBN_BASE_URL + href;
        HttpGet get = new HttpGet(url);
        String result = null;
        try {
            CloseableHttpResponse response = Client.client.execute(get);
            result = EntityUtils.toString(response.getEntity(), "utf-8");
            response.close();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 书籍详情
     *
     * @param isbn
     * @return
     */
    public static String getBookDetail(String isbn) {
        String url = Const.BOOK_DETAIL_URL + isbn;
        HttpGet get = new HttpGet(url);
        get.setHeader("Host", "api.douban.com");
        get.setHeader(
                "User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:52.0) Gecko/20100101 Firefox/52.0");
        String result = null;
        try {
            CloseableHttpResponse response = Client.client.execute(get);
            result = EntityUtils.toString(response.getEntity(), "utf-8");
            response.close();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取文章
     *
     * @return
     */
    public static String getArticle() {
        String url = Const.ARTICLE_CATALOG;
        HttpGet get = new HttpGet(url);
        String result = null;
        try {
            CloseableHttpResponse response = Client.client.execute(get);
            result = EntityUtils.toString(response.getEntity(), "utf-8");
            response.close();

            Log.i(TAG, "getArticle: " + result);

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getChatString(SimpleUserMessage msg) {
        return doGet(msg.getInfo());
    }

    public static String doGet(String msg) {
        String result = "";
        String url = setParams(msg);
        ByteArrayOutputStream baos = null;
        InputStream is = null;
        try {
            java.net.URL urlNet = new java.net.URL(url);
            HttpURLConnection conn = (HttpURLConnection) urlNet
                    .openConnection();
            conn.setReadTimeout(5 * 1000);
            conn.setConnectTimeout(5 * 1000);
            conn.setRequestMethod("GET");
            is = conn.getInputStream();
            int len = -1;
            byte[] buf = new byte[128];
            baos = new ByteArrayOutputStream();

            while ((len = is.read(buf)) != -1) {
                baos.write(buf, 0, len);
            }
            baos.flush();
            result = new String(baos.toByteArray());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null)
                    baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private static String setParams(String msg) {
        String url = "";
        try {
            url = Const.TULING_API_URL
                    + "?key=" + Const.API_KEY
                    + "&userid=" + Const.USER_ID
                    + "&loc=" + URLEncoder.encode(Const.USER_LOC, "UTF-8")
                    + "&info=" + URLEncoder.encode(msg, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return url;
    }
}

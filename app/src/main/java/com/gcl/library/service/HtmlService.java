package com.gcl.library.service;

import android.util.Log;

import com.gcl.library.bean.Book;
import com.gcl.library.bean.BorrowBook;
import com.gcl.library.bean.Catalog;
import com.gcl.library.bean.DetailBook;
import com.gcl.library.robot.FoodMsg;
import com.gcl.library.robot.NewsMsg;
import com.gcl.library.robot.SimpleUserMessage;
import com.gcl.library.robot.TextMsg;
import com.gcl.library.robot.UrlMsg;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.client.ClientProtocolException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * 解析html, 返回需要的数据
 */
public class HtmlService {

    /**
     * Gson对象，解析json字符创
     */
    private static Gson gson = new Gson();

    /**
     * 登录
     */
    public static boolean login(String name, String pwd) {
        String result = NetService.login(name, pwd);

        if (result == null || result.contains("<form action=\"redr_verify.php\" name=\"frm_login\" method=\"POST\">")) {
            return false;
        }
        return true;
    }

    /**
     * 获取用户姓名
     */
    public static String getUserName() {
        String result = NetService.getUserName();
        Document doc = Jsoup.parse(result);
        Element div = doc.select("#menu div").get(0);
        String name = div.text().split(" ")[0];
        return name;
    }

    /**
     * 获取已经借过的书
     */
    public static List<BorrowBook> getBorrowedBook() {

        String html = NetService.getBorrowedBook();
        List<BorrowBook> list = new ArrayList<BorrowBook>();

        Document doc = Jsoup.parse(html);
        Element ele = doc.select("table").get(0);
        Elements trEles = ele.getElementsByTag("tr");

        // 检查借的书籍是否为空
        if (ele == null || trEles == null) {
            return list;
        }

        for (int i = 1; i < trEles.size(); i++) {
            Elements tdEles = trEles.get(i).getElementsByTag("td");
            BorrowBook bb = new BorrowBook();
            bb.setBookName(tdEles.get(1).text());
            bb.setBorrowDate(tdEles.get(3).text());
            bb.setReturnDate(tdEles.get(4).text());
            bb.setNum(tdEles.get(0).text());
            list.add(bb);
        }
        return list;
    }

    /**
     * 得到搜索的书
     */
    public static List<Book> getSearchBook(String bookname) {

        String html = null;
        try {
            html = NetService.search(bookname);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Book> list = new ArrayList<Book>();
        if (html == null)
            return null;
        Document doc = Jsoup.parse(html);
        Elements eles = doc.select(".list_books");
        Book b = null;

        for (Element ele : eles) {
            b = new Book();
            Element h3 = ele.select("h3").get(0);
            Element p = ele.select("p").get(0);

            // 详情地址
            Element bookA = h3.select("a").get(0);
            String href = bookA.attr("href").toString();
            b.setHref(href);

            // 得到书名
            String bookName = h3.select("a").text();
            b.setBookName(bookName);

            // 得到编号
            h3.select("a, span").remove();
            String num = h3.text();
            b.setNum(num);

            // 得到书本个数
            String[] bb = p.select("span").get(0).text().split(" ");
            b.setCount(bb[0]);
            b.setAvailable(bb[1]);

            // 出版信息
            p.select("span").remove();
            String[] span = p.text().split(" ");
            int t = span.length;
            String author = "";
            for (int i = 0; i < t - 1; i++) {
                author += span[i];
            }
            b.setAuthor(author);
            b.setPublishInfo(span[t - 1]);

            list.add(b);
        }

        return list;
    }

    /**
     * 续借
     */
    public static Boolean renewBook(String id) {
        String html = NetService.continueBorrow(id);
        if (html == null) {
            return false;
        }

        Document doc = Jsoup.parse(html);

        Elements eles = doc.select("table");
        if (eles.size() == 0) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 获取图书的isbn号
     *
     * @param href
     */
    public static String getBookIsbn(String href) {
        String html = NetService.getBookIsbn(href);
        if (html == null)
            return null;
        Document doc = Jsoup.parse(html);
        Elements eles = doc.select(".booklist");
        for (Element ele : eles) {
            String str = ele.select("dt").get(0).text();
            if ("ISBN及定价:".equals(str)) {
                return ele.select("dd").get(0).text();
            }
        }
        return null;
    }

    /**
     * 从豆瓣获取书籍详情
     *
     * @param isbn
     * @return
     */
    public static DetailBook getBookDetail(String isbn) {
        String bookJson = NetService.getBookDetail(isbn);
        Log.i(TAG, "getBookDetail: " + bookJson);
        if (bookJson.contains("book_not_found")) {
            // 找不到详情信息
            return null;
        }
        return gson.fromJson(bookJson, DetailBook.class);
    }

    /**
     * 获取文章
     *
     * @return
     */
    public static List<Catalog> getArticle() {
        Type type = new TypeToken<List<Catalog>>() {
        }.getType();
        return gson.fromJson(NetService.getArticle(), type);
    }

    /**
     * 发送并接收消息
     */
    public static Object getChatMessage(SimpleUserMessage msg) {
        String json = NetService.getChatString(msg);

        Log.i(TAG, "getChatMessage: " + json);

        if (json.contains("100000")) {
            return gson.fromJson(json, TextMsg.class);
        } else if (json.contains("200000")) {
            return gson.fromJson(json, UrlMsg.class);
        } else if (json.contains("302000")) {
            return gson.fromJson(json, NewsMsg.class);
        } else if (json.contains("308000")) {
            return gson.fromJson(json, FoodMsg.class);
        }
        return null;
    }
}

package com.gcl.library.db;

import com.j256.ormlite.field.DatabaseField;

/**
 * Created by gcl on 2017/3/5.
 */

public class SavedBook {

    public SavedBook() {
    }

    /**
     * id
     */
    @DatabaseField(generatedId = true)
    private int id;

    /**
     * 书名
     */
    @DatabaseField(columnName = "bookName")
    private String bookName;

    /**
     * 书籍编号
     */
    @DatabaseField(columnName = "num")
    private String num;

    /**
     * 作者
     */
    @DatabaseField(columnName = "author")
    private String author;

    /**
     * 出版信息：出版社和出版日期
     */
    @DatabaseField(columnName = "publishInfo")
    private String publishInfo;

    /**
     * 详情url
     */
    @DatabaseField(columnName = "href")
    private String href;

    /**
     * ISBN
     */
    @DatabaseField(columnName = "ISBN")
    private String ISBN;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublishInfo() {
        return publishInfo;
    }

    public void setPublishInfo(String publishInfo) {
        this.publishInfo = publishInfo;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getISBN() {
        return ISBN;
    }

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

}

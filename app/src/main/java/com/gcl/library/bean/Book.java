package com.gcl.library.bean;

/**
 * Bean: 书籍
 *
 * @author gcl
 */
public class Book {
    /**
     * 书名
     */
    private String bookName;
    /**
     * 书籍编号
     */
    private String num;
    /**
     * 馆藏总数
     */
    private String count;
    /**
     * 可借总数
     */
    private String available;
    /**
     * 作者
     */
    private String author;
    /**
     * 出版信息：出版社和出版日期
     */
    private String publishInfo;
    /**
     * 详情url
     */
    private String href;

    /**
     * ISBN
     */
    private String ISBN;

    public Book() {

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

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getAvailable() {
        return available;
    }

    public void setAvailable(String available) {
        this.available = available;
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

    @Override
    public String toString() {
        return "Book{" +
                "bookName='" + bookName + '\'' +
                ", num='" + num + '\'' +
                ", count='" + count + '\'' +
                ", available='" + available + '\'' +
                ", author='" + author + '\'' +
                ", publishInfo='" + publishInfo + '\'' +
                ", href='" + href + '\'' +
                ", ISBN='" + ISBN + '\'' +
                '}';
    }
}

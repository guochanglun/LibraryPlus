package com.gcl.library.db;

import com.j256.ormlite.field.DatabaseField;

/**
 * Created by gcl on 2017/3/6.
 */

public class LovedBook {

    public LovedBook() {
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
}

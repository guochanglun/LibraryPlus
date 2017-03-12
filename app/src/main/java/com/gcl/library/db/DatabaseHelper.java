package com.gcl.library.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

/**
 * Created by gcl on 2017/3/5.
 */

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DB_NAME = "library.db";

    /**
     * userDao ，每张表对于一个
     */
    private Dao<User, Integer> userDao;

    /**
     * savedBookDao
     */
    private Dao<SavedBook, Integer> savedBookDao;

    /**
     * lovedBookDao
     */
    private Dao<LovedBook, Integer> lovedBookDao;

    private DatabaseHelper(Context context) {
        super(context, DB_NAME, null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, User.class);
            TableUtils.createTable(connectionSource, SavedBook.class);
            TableUtils.createTable(connectionSource, LovedBook.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database,
                          ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, User.class, true);
            onCreate(database, connectionSource);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static DatabaseHelper instance;

    /**
     * 单例获取该Helper
     *
     * @param context
     * @return
     */
    public static synchronized DatabaseHelper getHelper(Context context) {
        if (instance == null) {
            synchronized (DatabaseHelper.class) {
                if (instance == null)
                    instance = new DatabaseHelper(context);
            }
        }

        return instance;
    }

    /**
     * 获得userDao
     *
     * @return
     * @throws SQLException
     */
    public Dao<User, Integer> getUserDao() throws SQLException {
        if (userDao == null) {
            userDao = getDao(User.class);
        }
        return userDao;
    }

    /**
     * 获得savedBookDao
     *
     * @return
     * @throws SQLException
     */
    public Dao<SavedBook, Integer> getSavedBookDao() throws SQLException {
        if (savedBookDao == null) {
            savedBookDao = getDao(SavedBook.class);
        }
        return savedBookDao;
    }

    /**
     * 获得savedBookDao
     *
     * @return
     * @throws SQLException
     */
    public Dao<LovedBook, Integer> getLovedBookDao() throws SQLException {
        if (lovedBookDao == null) {
            lovedBookDao = getDao(LovedBook.class);
        }
        return lovedBookDao;
    }

    /**
     * 释放资源
     */
    @Override
    public void close() {
        super.close();
        userDao = null;
    }

}
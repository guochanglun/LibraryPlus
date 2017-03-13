package com.gcl.library.activity;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class BorrowedBookWidget extends AppWidgetProvider {

    private static int BOOK_LIST = R.id.widget_book_list;

    public static String BOOK_LIST_ITEM_CLICK_ACTION = "com.gcl.library.widget.book_item_click";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.borrowed_book_widget);

        // 设置list adapter
        Intent serviceIntent = new Intent(context, BorrowedBookService.class);
        views.setRemoteAdapter(BOOK_LIST, serviceIntent);

        // 设置list item intent模板
        Intent listIntent = new Intent();
        listIntent.setAction(BOOK_LIST_ITEM_CLICK_ACTION);
        listIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        PendingIntent templateIntent = PendingIntent.getBroadcast(context, 0, listIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(BOOK_LIST, templateIntent);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
    }

    @Override
    public void onDisabled(Context context) {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (BOOK_LIST_ITEM_CLICK_ACTION.equals(action)) {
            Intent it = new Intent(context, com.gcl.library.activity.SplashScreenActivity.class);
            it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pintent = PendingIntent.getActivity(context, 0, it, PendingIntent.FLAG_UPDATE_CURRENT);
            try {
                pintent.send();
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }
        } else {
            super.onReceive(context, intent);
        }
    }
}


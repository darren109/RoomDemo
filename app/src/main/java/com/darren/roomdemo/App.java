package com.darren.roomdemo;

import android.app.Application;

import com.darren.roomdemo.database.AppDatabase;

/**
 * @author Darren(Zeng Dongyang)
 * @date 2019-07-11
 */
public class App extends Application {

    private static App app;
    private static AppDatabase appDatabase;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        appDatabase = AppDatabase.getDatabase(this);
        // 创建数据库表
        appDatabase.getOpenHelper().getWritableDatabase();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        appDatabase.close();
    }

    public static App getApp() {
        return app;
    }

    public static AppDatabase getAppDatabase() {
        return appDatabase;
    }
}

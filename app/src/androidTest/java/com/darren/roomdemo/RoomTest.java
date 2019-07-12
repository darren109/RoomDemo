package com.darren.roomdemo;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import androidx.room.testing.MigrationTestHelper;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import com.darren.roomdemo.database.AppDatabase;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

/**
 * @author Darren(Zeng Dongyang)
 * @date 2019-07-08
 */
@RunWith(AndroidJUnit4.class)
public class RoomTest {
    private static final String TEST_DB = "migration-test";

    @Rule
    public MigrationTestHelper helper;

    public RoomTest() {
        helper = new MigrationTestHelper(InstrumentationRegistry.getInstrumentation(),
                AppDatabase.class.getCanonicalName(), new FrameworkSQLiteOpenHelperFactory());
    }

    @Test
    public void migrate1To2() throws IOException {
        SupportSQLiteDatabase db = helper.createDatabase(TEST_DB, 1);
        // 测试数据库 version 1
        final ContentValues values = new ContentValues();
        values.put("last_name", "aaa");
        values.put("first_name", "bbb");
        values.put("token", "vvvvvvvv");
        db.insert("users", SQLiteDatabase.CONFLICT_IGNORE, values);
        // ...
        // 关闭数据库
        db.close();

        // 打开新的数据库测试迁移后的数据库
        db = helper.runMigrationsAndValidate(TEST_DB, 2, true, AppDatabase.MIGRATION_1_TO_2);
        // ...
        // 关闭数据库
        db.close();
        // ...等操作
    }
}

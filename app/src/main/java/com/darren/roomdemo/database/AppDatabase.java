package com.darren.roomdemo.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.darren.roomdemo.database.dao.EmDao;
import com.darren.roomdemo.database.dao.UserDao;
import com.darren.roomdemo.database.entity.User;
import com.darren.roomdemo.database.entity.UserEm;
import com.darren.roomdemo.database.help.DateConverter;

/**
 * @author Darren(Zeng Dongyang)
 * @date 2019-07-07
 */
// @Database 表映射实体数据以及版本等信息
// 使用exportSchema = false, 可以关闭电子更新文档的导出
@Database(entities = {User.class, UserEm.class}, version = 3, exportSchema = true)
@TypeConverters(DateConverter.class) // 自定义数据类型转换
public abstract class AppDatabase extends RoomDatabase {

    // 由 Room 框架 实现
    public abstract UserDao getUserDao();

    public abstract EmDao getEmDao();

    // 数据库 示例
    private static volatile AppDatabase INSTANCE;

    //数据库变动添加Migration
    public static final Migration MIGRATION_1_TO_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            //数据库的具体变动，我是在之前的user表中添加了新的column，名字是age，类型是integer，不为空，默认值是0
            database.execSQL("ALTER TABLE users ADD COLUMN age INTEGER NOT NULL DEFAULT 0");
        }
    };
    //数据库变动添加Migration
    public static final Migration MIGRATION_2_TO_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            //数据库的具体变动，我是在之前的user表中添加了新的column，名字是address，类型是String，
            database.execSQL("ALTER TABLE users ADD COLUMN address TEXT DEFAULT ''");
            database.execSQL("ALTER TABLE users ADD COLUMN time INTEGER DEFAULT 0");
        }
    };
    public static final Migration MIGRATION_1_TO_3 = new Migration(1, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            //数据库的具体变动，我是在之前的user表中添加了新的column，名字是age，类型是integer，不为空，默认值是0;名字是address，类型是String
            database.execSQL("ALTER TABLE users ADD COLUMN age INTEGER NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE users ADD COLUMN address TEXT DEFAULT ''");
            database.execSQL("ALTER TABLE users ADD COLUMN time INTEGER DEFAULT 0");
        }
    };


    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    // 内存临时数据库
                    // INSTANCE = Room.inMemoryDatabaseBuilder(context.getApplicationContext(), UserRoomDatabase.class)
                    // .build();
                    // 持久性数据库
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "user_database.db")
                            // 如果当前版本与现有数据库版本缺少，差异更新项，会清空数据库，重新创建数据表
                            // .addMigrations(MIGRATION_1_TO_2)
                            // 数据库版本迁移，可以有多个差异更新项，例如：从1->2;后面有对数据表修改，从2->3
                            // .addMigrations(MIGRATION_1_TO_2, MIGRATION_2_TO_3)
                            // Room可以处理，使用 1->3 变更处理代替 1->2 和 2->3 的变更
                            .addMigrations(MIGRATION_1_TO_2, MIGRATION_2_TO_3, MIGRATION_1_TO_3)
                            // 如果没有找到将旧数据库模式迁移到最新模式版本的，允许Room破坏性地重新创建数据库表
                            .fallbackToDestructiveMigration()
                            // 当降级到旧版本时，如果不可用，允许Room破坏性地重新创建数据库表
                            .fallbackToDestructiveMigrationOnDowngrade()
                            // 下面注释表示允许主线程进行数据库操作，但是不推荐这样做，可能造成主线程lock以及anr，所以操作都是在新线程完成的
                            // .allowMainThreadQueries()
                            // 自定义查询线程池
                            // .setQueryExecutor(Executors.newCachedThreadPool())
                            // 数据库回调
                            // .addCallback(new Callback() {
                            //     @Override
                            //     public void onCreate(@NonNull SupportSQLiteDatabase db) {
                            //         super.onCreate(db);
                            //         Log.d(AppDatabase.class.getSimpleName(), "onCreate");
                            //     }
                            //
                            //     @Override
                            //     public void onOpen(@NonNull SupportSQLiteDatabase db) {
                            //         super.onOpen(db);
                            //         Log.d(AppDatabase.class.getSimpleName(), "onOpen");
                            //     }
                            // })
                            // 设置日志模式，默认：{@see JournalMode #AUTOMATI}
                            // .setJournalMode(JournalMode.AUTOMATIC)
                            // 启用多实例失效，不适用内存数据库
                            .enableMultiInstanceInvalidation()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
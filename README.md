# Google Room 数据库框架

## 简介

Google 为了帮助支持开发者，推出了自己的 Room 数据库框架，Room 是一个持久性数据，提供了 SQLite 的抽象层，以便在充分利用 SQLite 的同时允许流畅的数据库访问。

[Room 官网文档](https://developer.android.com/training/data-storage/room/index.html)

## Room 的优势

1. SQL 查询在编译时就会验证，在编译时检查每个 `@Query` 和 `@Entity` 等，这就意味着没有任何运行时错误的风险可能会导致应用程序崩溃（并且它不仅检查语法问题，还会检查是否有该表）
2. 较少的模板代码
3. 可以与 LiveData 集成
   - [官方 java 实现步骤](https://codelabs.developers.google.com/codelabs/android-room-with-a-view/#0)
   - [官方 kotlin 实现步骤](https://codelabs.developers.google.com/codelabs/android-room-with-a-view-kotlin/#0)
4. 可以与 RxJava 结合使用
   - [官方 java demo](https://github.com/googlesamples/android-architecture-components/tree/master/BasicRxJavaSample)
   - [官方 kotlin demo](https://github.com/googlesamples/android-architecture-components/tree/master/BasicRxJavaSampleKotlin)

## Room 的 3 主要组成部分

**数据库**： 包含数据库持有者，并作为应用程序持久关系数据的基础连接的主要访问点。

注释的类 `@Database` 应满足以下条件：

- 是一个扩展的抽象类 `RoomDatabase`。
- 在注释中包括与数据库关联的实体列表。
- 包含一个具有 0 个参数的抽象方法，并返回带注释的类 `@Dao`。
- 在运行时，您可以 Database 通过调用 `Room.databaseBuilder()` 或 获取实例 `Room.inMemoryDatabaseBuilder()`。

**实体**：表示数据库中的表。

**DAO**：包含用于访问数据库的方法。

## 使用步骤

### 1. 我们需要在项目的 build.gradlle 文件添加

```groovy
allprojects {
    repositories {
        google()
        jcenter()
    }
}

ext {
    roomVersion = '2.1.0'
    archLifecycleVersion = '2.0.0'
}
```

### 2. 在使用数据库的 module 中 build.gradle 文件添加如下依赖

```groovy
// Room
implementation "androidx.room:room-runtime:$rootProject.roomVersion"
annotationProcessor "androidx.room:room-compiler:$rootProject.roomVersion"
androidTestImplementation "androidx.room:room-testing:$rootProject.roomVersion"
// Rx java 2 结合使用（使用则添加）
implementation "androidx.room:room-rxjava2:$rootProject.roomVersion"
// Lifecycle liveData（使用则添加）
implementation "androidx.lifecycle:lifecycle-extensions:$rootProject.archLifecycleVersion"
annotationProcessor "androidx.lifecycle:lifecycle-compiler:$rootProject.archLifecycleVersion"
```

### 3. 创建 Entity

```java
// @Entity类代表一个表中的实体。注释您的类声明以指示它是一个实体。如果希望它与类的名称不同，请指定表的名称
// 有时，数据库中的某些字段或字段组必须是唯一的，可以通过将 注释的 unique 属性设置为来强制实施此唯一性属性
@Entity(tableName = "users", indices = {@Index(value = {"token"}, unique = true)})
public class User {
    @NonNull// 非空
    @PrimaryKey(autoGenerate = true)//设置主键，并设置自增长，默认为false
    //@ColumnInfo(name = "id")//如果希望它与成员变量的名称不同，请在表中指定列的名称
    private int id;
    @ColumnInfo(name = "first_name")
    private String firstName;
    @ColumnInfo(name = "last_name")
    private String lastName;
    @NonNull
    @ColumnInfo(name = "token")
    private String token;
    @Ignore//可以用字段，表示忽略
    private Bitmap picture;

    //必须指定一个构造方法，room框架需要。并且只能指定一个，如果有其他构造方法，则其他的构造方法必须添加@Ignore注解
    public User() {
    }

    @Ignore//其他构造方法要添加@Ignore注解
    public User(int id, String token) {
        this.id = id;
        this.token = token;
    }

    // 为了支持room工作 ，添加Setter、Getter -> 存储在数据库中的每个字段都必须是公共的或具有“getter”方法 【 ① 】
    // 这里省略的 getter Setter 方法
}
```

`注意：`

- 实体类需要使用 `@Entity` 来注解该类
- 至少要有一个主键 `@PrimaryKey`
- 注释 ①：当缺少 `setter` 方法时，构建会提示 `错误: Cannot find setter for field.`
- 注释 ①：当缺少 `getter` 方法时，构建会提示 `错误: Cannot find getter for field.`

### 4. 创建 Dao

> 在 DAO（数据访问对象）中，指定 SQL 查询并将它们与方法调用相关联。编译器检查 SQL 并从便捷注释生成查询以查找常见查询。

1. `DAO` 必须是接口或抽象类，使用`@Dao` 注解。
2. 默认情况下，所有查询都必须在单独的线程上执行。
3. `Room` 使用 `DAO` 为您的代码创建一个干净的 API。
4. `@Insert`, `@Update`, `@Delete`, `@Query` 代表我们常用的 `插入`、`更新`、`删除`、`查询` 数据库操作。

```java
@Dao
public interface UserDao {

    // 1.@Insert 插入:
    // 返回值Long：返回插入的row id
    // 参数为 (List<User> users) 或 (User... user) 时,返回 long[] 或 List<Long>
    @Insert
    Long insert(User user);

    // onConflict 当数据冲突时，可以自定义冲突处理策略
    // Use OnConflictStrategy.ABORT: (default) to roll back the transaction on conflict. --> 在冲突时回滚事务
    // Use OnConflictStrategy.REPLACE: to replace the existing rows with the new rows. --> 用新行替换现有行
    // Use OnConflictStrategy.IGNORE: to keep the existing rows. --> 保留现有行
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    Long[] insert(User... user);

    @Insert
    List<Long> insert(List<User> users);

    // 2.@Delete 删除：便捷方法从数据库中删除一组作为参数给出的实体。它使用主键来查找要删除的实体
    // 返回值：可以是void，可以是int：表示从数据库中删除的行数
    @Delete
    int delete(User... user);

    @Delete
    int delete(List<User> users);

    // 3.@Update 更新: 便捷方法修改数据库中作为参数给出的一组实体。它使用与每个实体的主键匹配的查询
    // 返回值：可以是void；可以是int：表示数据库中更新的行数
    @Update
    int update(User... user);

    @Update
    int update(List<User> user);

    // 4.@Query 查询：sql
    // 返回值：array、List 或 单个实体 等
    @Query("SELECT * FROM users WHERE id = :id")
    User findByUid(int id);

    @Query("SELECT * FROM users WHERE first_name LIKE :first AND last_name LIKE :last LIMIT 1")
    User findByName(String first, String last);

    //根据条件查询，方法参数和注解的sql语句参数一一对应
    @Query("SELECT * FROM users WHERE id IN (:userIds)")
    List<User> loadAllByIds(int[] userIds);

    // 字段查询
    @Query("SELECT first_name, last_name FROM users WHERE id IN (:userIds)")
    List<NameTuple> loadUsersFromId(List<Integer> userIds);

    @Query("SELECT first_name, last_name FROM users")
    List<NameTuple> loadFullName();

    @Query("SELECT * FROM users")
    User[] loadAllUsers();

    @Query("SELECT * FROM users")
    Cursor getUserCursor();
}
```

### 5. 创建 DataBase

> 简单实现

```java
// @Database 表映射实体数据以及版本等信息
// 使用exportSchema = false, 不然会报错，后面将具体这个参数使用
@Database(entities = {User.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    // 由 Room 框架 实现
    public abstract UserDao getUserDao();

    // 数据库 示例
    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "user_database.db")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
```

```java
// @Database 表映射实体数据以及版本等信息
// 使用exportSchema = false, 可以关闭电子更新文档的导出
@Database(entities = {User.class}, version = 3, exportSchema = true)
@TypeConverters(DateConverter.class) // 自定义数据类型转换
public abstract class AppDatabase extends RoomDatabase {

    // 由 Room 框架 实现
    public abstract UserDao getUserDao();

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
            database.execSQL("ALTER TABLE users ADD COLUMN time LONG DEFAULT 0");
        }
    };
    public static final Migration MIGRATION_1_TO_3 = new Migration(1, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            //数据库的具体变动，我是在之前的user表中添加了新的column，名字是age，类型是integer，不为空，默认值是0;名字是address，类型是String
            database.execSQL("ALTER TABLE users ADD COLUMN age INTEGER NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE users ADD COLUMN address TEXT DEFAULT ''");
            database.execSQL("ALTER TABLE users ADD COLUMN time LONG DEFAULT 0");
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
```

## 外键关联表

```java
// foreignKeys: @ForeignKey关联了 user 表，主键id，外键emp_id
// indices: 使用了 @Index 创建了唯一索引
// onDelete: 删除约束类型, CASCADE:当users表中数据删除时，对应数据
// onUpdate: 更新约束类型
@Entity(tableName = "user_em",
        foreignKeys = @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "emp_id", onDelete = CASCADE, onUpdate = NO_ACTION),
        indices = @Index(value = {"emp_id"}, unique = true))
public class UserEm {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "emp_id")
    private int empId;

    private String em;// 技能

    public UserEm(String em, int empId) {
        this.em = em;
        this.empId = empId;
    }
    // ...
}
```

## SQLite 数据版本变更电子记录

```java
// @Database 表映射实体数据以及版本等信息
// 使用exportSchema = false, 可以关闭电子更新文档的导出
@Database(entities = {User.class}, version = 3, exportSchema = true)
@TypeConverters(DateConverter.class) // 自定义数据类型转换
public abstract class AppDatabase extends RoomDatabase {
    //...
}
```

`exportSchema = true`, 表示记录数据库版本变更记录；

除了打开这个配置以外，我们还需要配置相关导出路径：

```groovy
android {
    defaultConfig {
        //...
        javaCompileOptions {
            annotationProcessorOptions {
                //room的数据库概要、记录
                arguments = ["room.schemaLocation": "$projectDir/schemas".toString()]
            }
        }
    }
    sourceSets {
        //数据库概要、记录存放位置
        androidTest.assets.srcDirs += files("$projectDir/schemas".toString())
    }
    //...
    }
}
```

## test 测试

````java
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

```

## 其他

[demo地址](http://code.sunmi.com/zengdongyang/RoomDemo)

```sql
数据库更新：

增加字段语法：alter table tablename add (column datatype [default value][null/not null],….);

说明：alter table 表名 add (字段名 字段类型 默认值 是否为空);

例：alter table sf_users add (HeadPIC blob);

例：alter table sf_users add (userName varchar2(30) default '空' not null);

修改字段的语法：alter table tablename modify (column datatype [default value][null/not null],….);

说明：alter table 表名 modify (字段名 字段类型 默认值 是否为空);

例：alter table sf_InvoiceApply modify (BILLCODE number(4));

删除字段的语法：alter table tablename drop (column);

说明：alter table 表名 drop column 字段名;

例：alter table sf_users drop column HeadPIC;

字段的重命名：

说明：alter table 表名 rename column 列名 to 新列名 （其中：column 是关键字）

例：alter table sf_InvoiceApply rename column PIC to NEWPIC;

表的重命名：

说明：alter table 表名 rename to 新表名

例：alter table sf_InvoiceApply rename to sf_New_InvoiceApply;

````

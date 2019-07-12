package com.darren.roomdemo.database.dao;

import android.database.Cursor;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.TypeConverters;
import androidx.room.Update;

import com.darren.roomdemo.database.entity.User;
import com.darren.roomdemo.database.help.DateConverter;
import com.darren.roomdemo.database.help.NameTuple;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * @author Darren(Zeng Dongyang)
 * @date 2019-07-06
 */
@Dao
//@TypeConverters(DateConverter.class)// 自定义数据类型转换，只有Dao内的数据对应的数据类型会处理
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

    // Emits the number of users added to the database.
    @Insert
    Maybe<List<Long>> insertLargeNumberOfUsers(List<User> users);

    // Rxjava2:Makes sure that the operation finishes successfully.
    @Insert
    Completable insertLargeNumberOfUsers(User... users);

    // 2.@Delete 删除：便捷方法从数据库中删除一组作为参数给出的实体。它使用主键来查找要删除的实体
    // 返回值：可以是void，可以是int：表示从数据库中删除的行数
    @Delete
    int delete(User... user);

    @Delete
    int delete(List<User> users);

    // Emits the number of users removed from the database. Always emits at least one user.
    @Delete
    Single<Integer> deleteUsers(List<User> users);

    @Query("delete FROM users")
    int deleteAll();

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

    // LiveData 结合
    @Query("SELECT first_name, last_name FROM users WHERE id IN (:userIds)")
    LiveData<List<NameTuple>> loadUsersFromRegionsSync(List<Integer> userIds);

    // RxJava2 结合
    @Query("SELECT * from users where id = :id LIMIT 1")
    Observable<User> loadUserById(int id);

    @Query("SELECT * FROM users")
    Flowable<List<User>> getAll();

    //.....
}

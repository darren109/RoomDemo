package com.darren.roomdemo.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.darren.roomdemo.database.entity.UserEm;
import com.darren.roomdemo.database.help.NameEmBean;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Maybe;

/**
 * @author Darren(Zeng Dongyang)
 * @date 2019-07-10
 */
@Dao
public interface EmDao {

    @Insert
    Long insert(UserEm userEm);

    @Insert
    List<Long> insert(List<UserEm> userEm);

    @Delete
    Maybe<Integer> delete(UserEm em);

    @Update
    Integer update(UserEm em);

    @Query("DELETE FROM user_em WHERE emp_id=:empid")
    int deleteForEmpId(int empid);

    @Query("SELECT * FROM user_em WHERE emp_id=:empid")
    UserEm getForEmpId(int empid);

    @Query("SELECT * FROM user_em")
    Flowable<List<UserEm>> getAllEm();

    //使用内连接查询
    @Query("SELECT emp_id,em,first_name,last_name FROM user_em INNER JOIN users ON users.id=user_em.emp_id")
    List<NameEmBean> getEmFromUser();
}

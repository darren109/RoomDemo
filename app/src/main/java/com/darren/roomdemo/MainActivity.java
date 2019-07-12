package com.darren.roomdemo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

import com.darren.roomdemo.database.dao.EmDao;
import com.darren.roomdemo.database.dao.UserDao;
import com.darren.roomdemo.database.entity.User;
import com.darren.roomdemo.database.entity.UserEm;
import com.darren.roomdemo.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    private ActivityMainBinding mBinding;
    private UserDao userDao;
    private EmDao emDao;
    private final CompositeDisposable mDisposable = new CompositeDisposable();
    private String[] emStr = {"建筑设计师", "电子设备设计师", "电工操作员", "外语教师", "架构设计师", "计算机工程师"};
    private SupportSQLiteOpenHelper helper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        helper = App.getAppDatabase().getOpenHelper();
        userDao = App.getAppDatabase().getUserDao();
        emDao = App.getAppDatabase().getEmDao();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
    }

    public void insert(View view) {
        mDisposable.add(Schedulers.newThread().scheduleDirect(() -> {
            try {
                final User user = new User("aaa", "bbb", UUID.randomUUID().toString(), 22, "", new Date());
                long aLong = userDao.insert(user);
                final UserEm em = new UserEm("建筑设计师", (int) aLong);
                final Long b = emDao.insert(em);
                AndroidSchedulers.mainThread().scheduleDirect(() -> mBinding.msg.setText("insert users id: " + aLong + "\n insert user_em id:" + b));
                Log.d(TAG, "insert: users: " + aLong + ", user:" + user.toString());
                Log.d(TAG, "insert: user_em: " + aLong + ", user_em:" + em.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));
    }

    public void insertRxList(View view) {
        List<User> list = new ArrayList<>();
        list.add(new User("a1", "bbb", UUID.randomUUID().toString(), 22, "", new Date()));
        list.add(new User("a2", "bbb", UUID.randomUUID().toString(), 21, "", new Date()));
        list.add(new User("a3", "bbb", UUID.randomUUID().toString(), 24, "", new Date()));
        list.add(new User("a4", "bbb", UUID.randomUUID().toString(), 19, "", new Date()));
        list.add(new User("a5", "bbb", UUID.randomUUID().toString(), 26, "", new Date()));
        Log.d(TAG, "insertRxList: user:" + list.toString());
        mDisposable.add(userDao.insertLargeNumberOfUsers(list)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(aLong -> {
                    AndroidSchedulers.mainThread().scheduleDirect(() -> mBinding.msg.setText("insertRxList users id: " + aLong));
                    Log.d(TAG, "insertRxList: users id:" + aLong.toString());
                    List<UserEm> emList = new ArrayList<>();
                    Log.d(TAG, "insertRxList: UserEm" + emList.toString());
                    for (long a : aLong) {
                        emList.add(new UserEm(emStr[(int) (a % 6)] + a, (int) a));
                    }
                    Log.d(TAG, "insertRxList: user_em: " + emList.toString());
                    return emDao.insert(emList);
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    mBinding.msg.setText(mBinding.msg.getText() + "\ninsert: user_em id:" + aLong.toString());
                    Log.d(TAG, "insert: user_em id:" + aLong.toString());
                }, e -> Log.e(TAG, "insertRxList: error", e)));
    }


    public void updateUser(View view) {
        mDisposable.add(Observable
                .defer(() -> {
                    final User user = userDao.findByUid(2);
                    Log.d(TAG, "updateUser: user" + user.toString());
                    AndroidSchedulers.mainThread().scheduleDirect(() -> mBinding.msg.setText("updateUser: user before:" + user.toString()));
                    user.setAddress("shanghai China");
                    user.setAge(36);
                    return Observable.just(userDao.update(user));
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(a -> {
                    mBinding.msg.setText(mBinding.msg.getText() + "\nupdateUser: count:" + a);
                    Log.d(TAG, "updateUser: count:" + a);
                }, e -> Log.e(TAG, "updateUser: error", e)));
    }

    public void updateUserEm(View view) {
        mDisposable.add(Observable
                .defer(() -> {
                    final User user = userDao.findByUid(2);
                    AndroidSchedulers.mainThread().scheduleDirect(() -> mBinding.msg.setText("updateUserEm: user:" + user.toString()));
                    Log.d(TAG, "updateUserEm: user:" + user.toString());
                    final UserEm em = emDao.getForEmpId(user.getId());
                    AndroidSchedulers.mainThread().scheduleDirect(() -> mBinding.msg.setText(mBinding.msg.getText() + "\nupdateUserEm: user_em:" + em.toString()));
                    Log.d(TAG, "updateUserEm: user_em:" + em.toString());
                    em.setEm("程序员...");
                    return Observable.just(emDao.update(em));
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(a -> {
                    mBinding.msg.setText(mBinding.msg.getText() + "\nupdateUserEm: count:" + a);
                    Log.d(TAG, "updateUserEm: count:" + a);
                }, e -> Log.e(TAG, "updateUserEm: error", e)));
    }

    public void deleteUserEm(View view) {
        mDisposable.add(Observable
                .defer(() -> {
                    final User user = userDao.findByUid(2);
                    Log.d(TAG, "deleteUserEm: user:" + (user != null ? user.toString() : null));
                    AndroidSchedulers.mainThread().scheduleDirect(() -> mBinding.msg.setText("deleteUserEm: user:" + (user != null ? user.toString() : null)));
                    if (user != null) {
                        return Observable.just(emDao.deleteForEmpId(user.getId()));
                    }
                    return Observable.empty();
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(a -> {
                    mBinding.msg.setText(mBinding.msg.getText() + "\ndeleteUserEm: count:" + a);
                    Log.d(TAG, "deleteUserEm: count:" + a);
                }, e -> Log.e(TAG, "deleteUserEm: error", e)));
    }

    public void deleteUser(View view) {
        mDisposable.add(Observable
                .defer(() -> {
                    final User user = userDao.findByUid(2);
                    Log.d(TAG, "deleteUser: user:" + (user != null ? user.toString() : null));
                    AndroidSchedulers.mainThread().scheduleDirect(() -> mBinding.msg.setText("deleteUser: user:" + (user != null ? user.toString() : null)));
                    if (user != null) {
                        return Observable.just(userDao.delete(user));
                    }
                    return Observable.empty();
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(a -> {
                    mBinding.msg.setText(mBinding.msg.getText() + "\ndeleteUser: count:" + a);
                    Log.d(TAG, "deleteUser: count:" + a);
                }, e -> Log.e(TAG, "deleteUser: error", e)));
    }


    public void selectAllUsers(View view) {
        mDisposable.add(Flowable
                .defer(() -> userDao.getAll())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> {
                    mBinding.msg.setText("selectAllUsers: " + list.toString());
                    Log.d(TAG, "selectAllUsers: " + list.toString());
                }, e -> Log.e(TAG, "selectAllUsers: error", e)));
    }

    public void selectAllUserEm(View view) {
        mDisposable.add(Flowable
                .defer(() -> emDao.getAllEm())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> {
                    mBinding.msg.setText("selectAllUserEm: " + list.toString());
                    Log.d(TAG, "selectAllUserEm: " + list.toString());
                }, e -> Log.e(TAG, "selectAllUserEm: error", e)));
    }

    public void clearAllUser(View view) {
        mDisposable.add(Flowable.defer(() -> Flowable.just(userDao.deleteAll()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(a -> {
                    mBinding.msg.setText("clearAllUser: count:" + a);
                    Log.d(TAG, "clearAllUser: count:" + a);
                }, e -> Log.e(TAG, "clearAllUser: error", e)));
    }
}

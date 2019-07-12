package com.darren.roomdemo.database.entity;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.darren.roomdemo.database.help.DateConverter;

import java.util.Date;

/**
 * @author Darren(Zeng Dongyang)
 * @date 2019-07-06
 */
// @Entity类代表一个表中的实体。注释您的类声明以指示它是一个实体。如果希望它与类的名称不同，请指定表的名称
// 有时，数据库中的某些字段或字段组必须是唯一的，可以通过将 注释的 unique 属性设置为来强制实施此唯一性属性
// primaryKeys: 复合主键，可以使用两个字段组合成主键
// ignoredColumns = {"picture"} 忽略的列
@Entity(tableName = "users",
        // primaryKeys = {},
        // ignoredColumns = {"picture"},
        indices = {@Index(value = {"token"}, unique = true)})
public class User {

    @NonNull//非空
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

    @NonNull
    @ColumnInfo(name = "age")
    private int age;

    @ColumnInfo(name = "address")
    private String address;

    @ColumnInfo(name = "time")
    private Date time;

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

    @Ignore
    public User(String firstName, String lastName, @NonNull String token, int age, String address, Date time) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.token = token;
        this.age = age;
        this.address = address;
        this.time = time;
    }

    // 为了支持room工作 ，添加Setter、Getter -> 存储在数据库中的每个字段都必须是公共的或具有“getter”方法
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @NonNull
    public String getToken() {
        return token;
    }

    public void setToken(@NonNull String token) {
        this.token = token;
    }

    public Bitmap getPicture() {
        return picture;
    }

    public void setPicture(Bitmap picture) {
        this.picture = picture;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", token='" + token + '\'' +
                ", age=" + age +
                ", address='" + address + '\'' +
                ", time=" + time +
                ", picture=" + picture +
                '}';
    }
}

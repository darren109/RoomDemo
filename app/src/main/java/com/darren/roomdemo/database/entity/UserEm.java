package com.darren.roomdemo.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;
import static androidx.room.ForeignKey.NO_ACTION;

/**
 * @author Darren(Zeng Dongyang)
 * @date 2019-07-10
 */
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEmpId() {
        return empId;
    }

    public void setEmpId(int empId) {
        this.empId = empId;
    }

    public String getEm() {
        return em;
    }

    public void setEm(String em) {
        this.em = em;
    }

    @Override
    public String toString() {
        return "UserEm{" +
                "id=" + id +
                ", empId=" + empId +
                ", em='" + em + '\'' +
                '}';
    }
}
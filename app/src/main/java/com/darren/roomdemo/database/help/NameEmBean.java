package com.darren.roomdemo.database.help;

import androidx.room.ColumnInfo;

/**
 * @author Darren(Zeng Dongyang)
 * @date 2019-07-10
 */
public class NameEmBean {

    @ColumnInfo(name = "emp_id")
    public int emp_id;

    @ColumnInfo(name = "first_name")
    public String firstName;

    @ColumnInfo(name = "last_name")
    public String lastName;

    public String em;


}

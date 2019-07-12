package com.darren.roomdemo.database.help;

import androidx.room.TypeConverter;

import java.util.Date;

/**
 * @author Darren(Zeng Dongyang)
 * @date 2019-07-08
 */

public class DateConverter {
    @TypeConverter
    public static Date fromTimestamp(long value) {
        return new Date(value);
    }

    @TypeConverter
    public static long dateToTimestamp(Date value) {
        return value.getTime();
    }
}

package com.example.timetable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import java.util.Date;

@Entity(tableName = "work_repo")
public class Work {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String classCode;

    private String title;

    private String description;

    @TypeConverters({Converters.class})
    private Date due;

    public Work(String classCode, String title, String description, Date due) {
        this.classCode = classCode;
        this.title = title;
        this.description = description;
        this.due = due;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getClassCode() {
        return classCode;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Date getDue() {
        return due;
    }

    public static class Converters {
        @TypeConverter
        public static Date fromTimestamp(Long value) {
            return value == null ? null : new Date(value);
        }

        @TypeConverter
        public static Long dateToTimestamp(Date date) {
            return date == null ? null : date.getTime();
        }
    }



}

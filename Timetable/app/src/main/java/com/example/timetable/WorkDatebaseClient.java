package com.example.timetable;

import android.content.Context;

import androidx.room.Room;

public class WorkDatebaseClient {
    private Context mCtx;
    private static WorkDatebaseClient instance;

    //our app database object
    private WorkDatabaseForCalendar workDatabases;

    private WorkDatebaseClient(Context mCtx) {
        this.mCtx = mCtx;
        workDatabases = Room.databaseBuilder(mCtx, WorkDatabaseForCalendar.class, "work_repo")
                .fallbackToDestructiveMigration()
                .build();
    }

    public static synchronized WorkDatebaseClient getInstance(Context mCtx) {
        if (instance == null) {
            instance = new WorkDatebaseClient(mCtx);
        }
        return instance;
    }

    public WorkDatabaseForCalendar getAppDatabase() {
        return workDatabases;
    }
}

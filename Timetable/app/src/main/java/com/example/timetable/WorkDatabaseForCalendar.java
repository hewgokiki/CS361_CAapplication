package com.example.timetable;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities =  {Work.class}, version = 1)
public abstract class WorkDatabaseForCalendar extends RoomDatabase {

    public abstract WorkDaoForCalendar dataBaseAction();
    private static volatile WorkDatabaseForCalendar workDatabaseForCalendar;

    /*private static WorkDatabaseForCalendar instance;

    public abstract WorkDaoForCalendar workDao();
    private WorkDatabaseForCalendar workDatabases;

    public static  synchronized WorkDatabaseForCalendar getInstance(Context context){
        if (instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    WorkDatabaseForCalendar.class, "work_repo")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallBack)
                    .build();
        }
        return instance;
    }

    private static  Callback roomCallBack = new Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDbAsyncTask(instance).execute();
        }
    };

    private static  class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {
        private WorkDaoForCalendar workDaoForCalendar;

        private PopulateDbAsyncTask(WorkDatabaseForCalendar db){
            workDaoForCalendar = db.workDao();
        }

        @Override
        protected Void doInBackground(Void... voids){
            return null;
        }
    }

    public WorkDatabaseForCalendar getAppDatabase() {
        return workDatabases;
    }*/
}

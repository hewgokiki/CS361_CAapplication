package com.example.timetable;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.Date;

@Database(entities =  {Work.class}, version = 1)
public abstract class WorkDatabase extends RoomDatabase {

    private static  WorkDatabase instance;

    public abstract WorkDao workDao();

    public static  synchronized WorkDatabase getInstance(Context context){
        if (instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    WorkDatabase.class, "work_repo")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallBack)
                    .build();
        }
        return instance;
    }

    private static  RoomDatabase.Callback roomCallBack = new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDbAsyncTask(instance).execute();
        }
    };

    private static  class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {
        private WorkDao workDao;

        private PopulateDbAsyncTask(WorkDatabase db){
            workDao = db.workDao();
        }

        @Override
        protected Void doInBackground(Void... voids){
            return null;
        }
    }
}

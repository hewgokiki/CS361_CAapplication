package com.example.timetable.presenter;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;

import com.example.timetable.EditActivity;
import com.example.timetable.R;
import com.example.timetable.contract.MainContract;
import com.example.timetable.model.PrefManager;
import com.example.timetable.timetableview.Schedule;
import com.example.timetable.timetableview.TimetableView;

public class MainPresenter implements MainContract.UserActions {
    final MainContract.View mainView;
    private PrefManager prefManager;

    public MainPresenter(MainContract.View mainView){
        this.mainView = mainView;
    }

    @Override
    public void setPrefManager(PrefManager prefManager) {
        this.prefManager = prefManager;
    }

    @Override
    public void addMenuClick() {
        mainView.startEditActivityForAdd();
    }

    @Override
    public void selectSticker(int idx, ArrayList<Schedule> schedules, TimetableView timetables) {
        EditActivity.exit = false;
        mainView.startEditActivityForInfo(idx,schedules, timetables);
        //mainView.startEditActivityForEdit(idx,schedules);
    }

    @Override
    public void prepare() {
        String savedData = prefManager.get((Context)mainView);
        if(savedData == null || savedData.equals("")) return;
        mainView.restoreTimetable(savedData);

        mainView.setDayHighlight(today());
    }

    private int today() {
        /** DAY_OF_WEEK
         * 1 : Sun, 7 : Sat
         */
        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DAY_OF_WEEK) -1;
        Log.d("Day", String.valueOf(day));
        if(day==0) return 7;
        if(day > 0 && day < 8) return day;
        return -1;
    }

    @Override
    public void save(String data) {
        prefManager.save((Context)mainView,data);
    }
}

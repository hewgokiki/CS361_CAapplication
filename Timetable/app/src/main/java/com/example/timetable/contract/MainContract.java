package com.example.timetable.contract;

import java.util.ArrayList;

import com.example.timetable.model.PrefManager;
import com.example.timetable.timetableview.Schedule;
import com.example.timetable.timetableview.TimetableView;


public interface MainContract {
    interface View{
        void startEditActivityForAdd();
        void startEditActivityForEdit(int idx, ArrayList<Schedule> schedules);
        void startEditActivityForInfo(int idx, ArrayList<Schedule> schedules, TimetableView timetable);
        void restoreTimetable(String data);
        void setDayHighlight(int day);
    }

    interface UserActions{
        void addMenuClick();
        void selectSticker(int idx, ArrayList<Schedule> schedules, TimetableView timetables);
        void prepare();
        void save(String data);
        void setPrefManager(PrefManager prefManager);
    }
}

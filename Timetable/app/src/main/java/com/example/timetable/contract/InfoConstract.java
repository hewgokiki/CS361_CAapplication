package com.example.timetable.contract;

import com.example.timetable.model.PrefManager;
import com.example.timetable.timetableview.Schedule;
import com.example.timetable.timetableview.TimetableView;

import java.util.ArrayList;


public interface InfoConstract {
    interface View{
        void startEditActivityForClassEdit(int idx, ArrayList<Schedule> schedules, ArrayList<Schedule> allSchedules, int REQUEST_EDIT);
    }

    interface UserActions{
        //
    }
}

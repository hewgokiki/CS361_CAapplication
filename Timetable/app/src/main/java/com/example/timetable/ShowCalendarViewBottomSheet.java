package com.example.timetable;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;

import com.applandeo.materialcalendarview.CalendarView;
/*import com.codegama.todolistapplication.database.DatabaseClient;
import com.codegama.todolistapplication.model.Task;*/
import com.applandeo.materialcalendarview.EventDay;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class ShowCalendarViewBottomSheet extends BottomSheetDialogFragment {

    Unbinder unbinder;
    MainActivity activity;
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.calendarView)
    CalendarView calendarView;
    private List<Work> works;


    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint({"RestrictedApi", "ClickableViewAccessibility"})
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.fragment_calendar_view, null);
        unbinder = ButterKnife.bind(this, contentView);
        dialog.setContentView(contentView);
        calendarView.setHeaderColor(R.color.colorAccent);
        getSavedTasks();
        back.setOnClickListener(view -> dialog.dismiss());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void getSavedTasks() {

        class GetSavedTasks extends AsyncTask<Void, Void, List<Work>> {
            @Override
            protected List<Work> doInBackground(Void... voids) {
                works = WorkDatebaseClient
                        .getInstance(getActivity())
                        .getAppDatabase()
                        .dataBaseAction().getAllWorks();
                return works;
            }

            @Override
            protected void onPostExecute(List<Work> works) {
                super.onPostExecute(works);
                calendarView.setEvents(getHighlitedDays());
            }
        }

        GetSavedTasks savedTasks = new GetSavedTasks();
        savedTasks.execute();
    }

    public List<EventDay> getHighlitedDays() {
        List<EventDay> events = new ArrayList<>();


        for(int i = 0; i < works.size(); i++) {
            Calendar calendar = Calendar.getInstance();
            String[] items1 = works.get(i).getDue().toString().split(" ");
            SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
            Date datex = null;
            try {
                datex = sdf.parse(works.get(i).getDue().toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            SimpleDateFormat sdf2=new SimpleDateFormat("dd-MM-yyyy");
            String [] dates = sdf2.format(datex.getTime()).toString().split("-");
            String dd = dates[0];
            String month = dates[1];
            String year = dates[2];

            /*String dd = "1";
            String month = "11";
            String year = "2021";*/

            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dd));
            calendar.set(Calendar.MONTH, Integer.parseInt(month) - 1);
            calendar.set(Calendar.YEAR, Integer.parseInt(year));
            events.add(new EventDay(calendar, R.drawable.dot));
        }
        return events;
    }

}

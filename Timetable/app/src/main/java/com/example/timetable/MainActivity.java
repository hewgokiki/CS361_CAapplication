package com.example.timetable;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.timetable.timetableview.Schedule;
import com.example.timetable.timetableview.Sticker;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private Date d;
    BottomNavigationView bottomNav;
    LinearLayout timetable;

    TextView dayText;
    TextView monthText;

    LinearLayout menu_calendar;
    List<Work> works = new ArrayList<>();
    WorkAdapter taskAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //setScreenOrientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        TextView recentClass = (TextView) findViewById(R.id.dayText);
        dayText = (TextView) findViewById(R.id.dayText);
        monthText = (TextView) findViewById(R.id.monthText);
        d = new Date();
        dayText.setText(String.valueOf(d.getDate()));
        monthText.setText(String.valueOf(new SimpleDateFormat("MMMM").format(d.getTime())));

        timetable = (LinearLayout) findViewById(R.id.menu_timetable);
        bottomNav = findViewById(R.id.bottomNavigationView);
        ConstraintLayout work = (ConstraintLayout) findViewById(R.id.work_menu);

        work.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.animation_imageview));
                startActivity(new Intent(MainActivity.this, WorksActivity.class));
                overridePendingTransition(0, 0);
            }
        });

        timetable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.animation_imageview));
                Intent timetableIntent = new Intent(MainActivity.this, TimetableActivity.class);
                startActivity(timetableIntent);
                overridePendingTransition(0, 0);
            }
        });

        bottomNav.setSelectedItemId(R.id.menuHome);

        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                menuSelector(item);
                return false;
            }
        });

        getSavedTasks();
        menu_calendar = (LinearLayout) findViewById(R.id.menu_calendar);

        menu_calendar.setOnClickListener(view -> {
            ShowCalendarViewBottomSheet showCalendarViewBottomSheet = new ShowCalendarViewBottomSheet();
            showCalendarViewBottomSheet.show(getSupportFragmentManager(), showCalendarViewBottomSheet.getTag());
        });

    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        getDataTimeRecentSet();
    }

    @Override
    protected void onResume() {
        super.onResume();
        d = new Date();
        dayText.setText(String.valueOf(d.getDate()));
        monthText.setText(String.valueOf(new SimpleDateFormat("MMMM").format(d.getTime())));
    }

    private boolean menuSelector(MenuItem item){
        switch (item.getItemId()){
            case R.id.menuHome:
                /*startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(0,0);
                return true;*/
            case R.id.menuTimetable:
                startActivity(new Intent(getApplicationContext(), TimetableActivity.class));
                overridePendingTransition(0,0);
                return true;
            case R.id.menuWorks:
                startActivity(new Intent(getApplicationContext(), WorksActivity.class));
                overridePendingTransition(0,0);
                return true;
            case R.id.menuSetting:
                startActivity(new Intent(getApplicationContext(), SettingActivity.class));
                overridePendingTransition(0,0);
                return true;
        }
        return false;
    }

    /*public void setUpAdapter() {
        taskAdapter = new WorkAdapter();
        taskRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        taskRecycler.setAdapter(taskAdapter);
    }*/

    private void getSavedTasks() {

        class GetSavedTasks extends AsyncTask<Void, Void, List<Work>> {
            @Override
            protected List<Work> doInBackground(Void... voids) {
                works = WorkDatebaseClient
                        .getInstance(getApplicationContext())
                        .getAppDatabase()
                        .dataBaseAction().getAllWorks();
                return works;
            }

            @Override
            protected void onPostExecute(List<Work> tasks) {
                super.onPostExecute(tasks);
                /*noDataImage.setVisibility(tasks.isEmpty() ? View.VISIBLE : View.GONE);*/
                //setUpAdapter();
            }
        }

        GetSavedTasks savedTasks = new GetSavedTasks();
        savedTasks.execute();
    }

    public void getDataTimeRecentSet(){

        try {
            SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(this);

            TextView recentClass = (TextView) findViewById(R.id.recent_act);

            String savedData = mPref.getString(this.getResources().getString(R.string.timetable_repo),"");
            JsonParser parser = new JsonParser();
            JsonObject obj1 = (JsonObject)parser.parse(savedData);
            JsonArray arr1 = obj1.getAsJsonArray("sticker");
            if(arr1.size()!=0){
                Set<String> classTitles = new HashSet<String>();

                for(int i = 0 ; i < arr1.size(); i++){
                    Sticker sticker = new Sticker();
                    JsonObject obj2 = (JsonObject)arr1.get(i);
                    int idx = obj2.get("idx").getAsInt();
                    JsonArray arr2 = (JsonArray)obj2.get("schedule");
                    for(int k = 0 ; k < arr2.size(); k++) {
                        Schedule schedule = new Schedule();
                        JsonObject obj3 = (JsonObject) arr2.get(k);

                        classTitles.add(obj3.get("classTitle").getAsString());
                        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
                        String[] time = null;
                        try {
                            time = dateFormat.format(new Date()).split(":");
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        Calendar calendar = Calendar.getInstance();
                        int day = calendar.get(Calendar.DAY_OF_WEEK);

                        JsonObject obj4 = (JsonObject)obj3.get("startTime");
                        JsonObject obj5 = (JsonObject)obj3.get("endTime");

                        int currentHour = Integer.valueOf(time[0]);
                        int currentMinute = Integer.valueOf(time[1]);

                        int classStartHour = obj4.get("hour").getAsInt();
                        int classStartMinute = obj4.get("minute").getAsInt();

                        int classEndHour = obj5.get("hour").getAsInt();
                        int classEndMinute = obj5.get("minute").getAsInt();

                        int convertDay = obj3.get("day").getAsInt();

                        switch (obj3.get("day").getAsInt()) {
                            case 0: convertDay = 2; break;
                            case 1: convertDay = 3; break;
                            case 2: convertDay = 4; break;
                            case 3: convertDay = 5; break;
                            case 4: convertDay = 6; break;
                            case 5: convertDay = 7; break;
                            case 6: convertDay = 1; break;
                        }

                        if(convertDay==day){
                            if(((classStartHour==currentHour&&classStartMinute<=currentMinute)||
                                    (classStartHour<currentHour))&&((classEndHour>currentHour)||
                                    (classEndHour==currentHour&&classEndMinute>=currentMinute))){
                                recentClass.setText("Now class: "+obj3.get("classCode").getAsString()+" | "+
                                        classStartHour+":"+classStartMinute+" - "+classEndHour+":"+classEndMinute);
                                break;

                            }else{

                            }
                        }else{

                        }

                    }
                }
            }else{
                return;
            }

        }catch (Exception e){
            return;
        }


    }

}
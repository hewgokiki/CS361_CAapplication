package com.example.timetable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.Observer;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.timetable.contract.InfoConstract;
import com.example.timetable.contract.MainContract;
import com.example.timetable.presenter.EditPresenter;
import com.example.timetable.timetableview.Schedule;
import com.example.timetable.timetableview.Sticker;
import com.example.timetable.timetableview.TimetableView;
import com.example.timetable.view.TimeBoxView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.w3c.dom.Text;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InfoActivity extends AppCompatActivity implements InfoConstract.View, InfoConstract.UserActions {

    private int idx;

    ArrayList<Schedule> allSchedules = new ArrayList<Schedule>();
    ArrayList<Schedule> schedules;

    private WorkViewModel workViewModel;
    private String currentClassCode;

    TextView classCode;
    TextView className;
    TextView classRoom;
    TextView classSchedule;

    LinearLayout editClass;
    LinearLayout backBtn;
    Context context;

    private TimetableView timetable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        backBtn = findViewById(R.id.info_back);

        idx = getIntent().getIntExtra("idx",-1);

        int REQUEST_EDIT = getIntent().getIntExtra("mode", -1);
        allSchedules = (ArrayList<Schedule>) getIntent().getSerializableExtra("allSchedules");
        schedules = (ArrayList<Schedule>) getIntent().getSerializableExtra("schedules");
        timetable = (TimetableView) getIntent().getSerializableExtra("timetable");

        editClass = (LinearLayout) findViewById(R.id.edit_class);
        editClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimetableActivity.getInstanceActivity().startEditActivityForEdit(idx, schedules);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditActivity.edit = false;
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        init();

        //Recycle_view_card_works

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(false);

        final WorkAdapter adapter = new WorkAdapter();
        recyclerView.setAdapter(adapter);

        workViewModel = ViewModelProviders.of(this).get(WorkViewModel.class);
        workViewModel.getAllWorks().observe(this, new Observer<List<Work>>() {
            @Override
            public void onChanged(List<Work> works) {
                List<Work> newWorkList = new ArrayList<Work>();
                for(Work work : works){
                    if(work.getClassCode().equals(currentClassCode)){
                        newWorkList.add(work);
                    }

                }
                if(newWorkList.size()==0){
                    LinearLayout noWorkYet = (LinearLayout) findViewById(R.id.noWorkYet);
                    noWorkYet.setVisibility(View.VISIBLE);
                    return;
                }
                adapter.submitList(newWorkList);
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                workViewModel.delete(adapter.getWorkAt(viewHolder.getAdapterPosition()));
                Toast.makeText(InfoActivity.this, "Work deleted", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(recyclerView);

        adapter.setOnItemClickListener(new WorkAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Work work) {
                Intent intent = new Intent(InfoActivity.this, AddEditWorkActivity.class);
                intent.putExtra(AddEditWorkActivity.EXTRA_ID, work.getId());
                intent.putExtra(AddEditWorkActivity.EXTRA_CODE, work.getClassCode());
                intent.putExtra(AddEditWorkActivity.EXTRA_TITLE, work.getTitle());
                intent.putExtra(AddEditWorkActivity.EXTRA_DESCRIPTION, work.getDescription());
                intent.putExtra(AddEditWorkActivity.EXTRA_DUE, work.getDue());
                startActivityForResult(intent, 2);
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            String title = data.getStringExtra(AddEditWorkActivity.EXTRA_TITLE);
            String code = data.getStringExtra(AddEditWorkActivity.EXTRA_CODE);
            String description = data.getStringExtra(AddEditWorkActivity.EXTRA_DESCRIPTION);
            Date due = (Date) data.getSerializableExtra(AddEditWorkActivity.EXTRA_DUE);

            Work work = new Work(code, title, description, due);
            workViewModel.insert(work);

            Toast.makeText(this, "Work saved", Toast.LENGTH_SHORT).show();
        } else if (requestCode == 2 && resultCode == RESULT_OK) {
            int id = data.getIntExtra(AddEditWorkActivity.EXTRA_ID, -1);

            if (id == -1) {
                Toast.makeText(this, "Work can't be updated", Toast.LENGTH_SHORT).show();
                return;
            }
            String title = data.getStringExtra(AddEditWorkActivity.EXTRA_TITLE);
            String code = data.getStringExtra(AddEditWorkActivity.EXTRA_CODE);
            String description = data.getStringExtra(AddEditWorkActivity.EXTRA_DESCRIPTION);
            Date due = (Date) data.getSerializableExtra(AddEditWorkActivity.EXTRA_DUE);

            Work work = new Work(code, title, description, due);
            work.setId(id);
            workViewModel.update(work);
        } else {
            Toast.makeText(this, "Work not saved", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (EditActivity.exit) {
            finish();
            return;
        }
    }

    @Override
    public void startEditActivityForClassEdit(int idx, ArrayList<Schedule> schedules, ArrayList<Schedule> allSchedulesSent, int REQUEST_EDIT) {
        Log.d("classEdit", "edit");
        Intent i = new Intent(this, TimetableActivity.class);
        i.putExtra("idx",idx);
        i.putExtra("mode", REQUEST_EDIT);
        i.putExtra("allSchedules", getIntent().getSerializableExtra("allSchedules"));
        i.putExtra("schedules", schedules);
        startActivityForResult(i, REQUEST_EDIT);
    }

    private void init() {

        classCode = (TextView) findViewById(R.id.class_code);
        className = (TextView) findViewById(R.id.class_name);
        classRoom = (TextView) findViewById(R.id.class_room);
        classSchedule = (TextView) findViewById(R.id.class_schedule);

        if(schedules!=null) {
            className.setText(schedules.get(0).getClassTitle());
            this.currentClassCode = schedules.get(0).getClassCode();
            classCode.setText(schedules.get(0).getClassCode());
            classRoom.setText(schedules.get(0).getClassPlace());
            if(schedules.size()==1){
                String allSchedulesData = "";
                switch (schedules.get(0).getDay()) {
                    case 0: allSchedulesData += "Mon"; break;
                    case 1: allSchedulesData += "Tue"; break;
                    case 2: allSchedulesData += "Wed"; break;
                    case 3: allSchedulesData += "Thu"; break;
                    case 4: allSchedulesData += "Fri"; break;
                    case 5: allSchedulesData += "Sat"; break;
                    case 6: allSchedulesData += "Sun"; break;
                }
                allSchedulesData +=" "+schedules.get(0).getStartTime().getHour()+":"+schedules.get(0).getStartTime().getMinute();
                if(schedules.get(0).getStartTime().getMinute()==0){
                    allSchedulesData+="0";
                }
                allSchedulesData+=" - "+schedules.get(0).getEndTime().getHour()+":"+schedules.get(0).getEndTime().getMinute();
                if(schedules.get(0).getEndTime().getMinute()==0){
                    allSchedulesData+="0";
                }
                classSchedule.setText(allSchedulesData);
            }else{
                String allSchedulesData = "";
                for (Schedule schedule : schedules) {
                    switch (schedule.getDay()) {
                        case 0: allSchedulesData += "Mon"; break;
                        case 1: allSchedulesData += "Tue"; break;
                        case 2: allSchedulesData += "Wed"; break;
                        case 3: allSchedulesData += "Thu"; break;
                        case 4: allSchedulesData += "Fri"; break;
                        case 5: allSchedulesData += "Sat"; break;
                        case 6: allSchedulesData += "Sun"; break;
                    }
                    allSchedulesData +=" "+schedules.get(0).getStartTime().getHour()+":"+schedules.get(0).getStartTime().getMinute();
                    if(schedules.get(0).getStartTime().getMinute()==0){
                        allSchedulesData+="0 ";
                    }
                    allSchedulesData+=" - "+schedules.get(0).getEndTime().getHour()+":"+schedules.get(0).getEndTime().getMinute();
                    if(schedules.get(0).getEndTime().getMinute()==0){
                        allSchedulesData+="0 ";
                    }
                    if(schedules.size()<=2&&schedule!=schedules.get(1)){
                        allSchedulesData+=" | ";
                    }else if(schedule!=schedules.get(schedules.size()-1)&&schedules.size()>2){
                        allSchedulesData+=" | ";
                    }
                }
                classSchedule.setText(allSchedulesData);
            }

        }


    }

}
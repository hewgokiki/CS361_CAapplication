package com.example.timetable;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.timetable.contract.EditContract;
import com.example.timetable.presenter.EditPresenter;
import com.example.timetable.timetableview.Schedule;
import com.example.timetable.view.TimeBoxView;

import java.util.ArrayList;

public class EditActivity extends AppCompatActivity implements EditContract.View {
    public static final int RESULT_OK_ADD = 1;
    public static final int RESULT_OK_EDIT = 2;
    public static final int RESULT_OK_DELETE = 3;

    public static boolean exit = false;
    public static boolean edit = false;

    private EditPresenter editPresenter;

    ArrayList<Schedule> allSchedules = new ArrayList<Schedule>();

    TextView titleView;

    EditText classTitle, classCode, classPlace, professorName;
    TimeBoxView timeBox;
    Button addTimeBtn;

    LinearLayout backBtn;
    LinearLayout addBtn;
    LinearLayout deleteBtn;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_picker);
        context = this;

        editPresenter = new EditPresenter(this);

        titleView = findViewById(R.id.title);
        classTitle = findViewById(R.id.class_title);
        classCode= findViewById(R.id.class_code);
        classPlace = findViewById(R.id.class_place);
        professorName = findViewById(R.id.professor_name);
        timeBox = findViewById(R.id.time_box);

        addTimeBtn = findViewById(R.id.add_time);
        backBtn = findViewById(R.id.back);
        addBtn = findViewById(R.id.add);
        deleteBtn = findViewById(R.id.delete);

        addTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editPresenter.clickAddTimeBtn();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editPresenter.submit(allSchedules,timeBox.getSchedules());
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editPresenter.clickDeleteBtn();
            }
        });

        init();
    }

    private void init() {
        allSchedules = (ArrayList<Schedule>) getIntent().getSerializableExtra("allSchedules");
        if (isEditMode()) {
            ArrayList<Schedule> itemSchedules = (ArrayList<Schedule>) getIntent().getSerializableExtra("schedules");
            editPresenter.prepare(true, itemSchedules);
        } else {
            editPresenter.prepare(false, null);
        }
    }

    private boolean isEditMode() {
        int mode = getIntent().getIntExtra("mode", -1);
        if (mode == TimetableActivity.REQUEST_EDIT) return true;
        return false;
    }

    @Override
    public void restoreViews(ArrayList<Schedule> schedules) {
        for(Schedule schedule : schedules) {
            classTitle.setText(schedule.getClassTitle());
            classCode.setText(schedule.getClassCode());
            classPlace.setText(schedule.getClassPlace());
            professorName.setText(schedule.getProfessorName());
            timeBox.add(schedule);
        }
    }

    @Override
    public void createTimeView(Schedule schedule) {
        timeBox.add(schedule);
    }

    private ArrayList<Schedule> addMetaData(ArrayList<Schedule> resultSchedule){
        for(Schedule schedule : resultSchedule){
            schedule.setClassTitle(classTitle.getText().toString());
            Log.d("classTitle", classTitle.getText().toString());
            schedule.setClassCode(classCode.getText().toString());
            schedule.setClassPlace(classPlace.getText().toString());
            Log.d("classPlace", classPlace.getText().toString());
            schedule.setProfessorName(professorName.getText().toString());
            Log.d("professorName", professorName.getText().toString());
        }
        return resultSchedule;
    }

    @Override
    public void setResult(ArrayList<Schedule> resultSchedule) {
        Intent i = new Intent();
        if(resultSchedule == null){
            i.putExtra("idx",getIntent().getIntExtra("idx",-1));
            i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            exit = true;
            setResult(RESULT_OK_DELETE,i);
            Log.d("Delete", "Finished");
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            return;
        }
        resultSchedule = addMetaData(resultSchedule);
        i.putExtra("schedules", resultSchedule);
        Log.d("Mode", String.valueOf(isEditMode()));
        if(isEditMode()){
            i.putExtra("idx",getIntent().getIntExtra("idx",-1));
            edit = true;
            setResult(RESULT_OK_EDIT, i);
            Log.d("Edit", "Finished");
        }
        else{
            setResult(RESULT_OK_ADD, i);
            Log.d("Add", "Finished");
        }
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void hideDeleteBtn() {
        deleteBtn.setVisibility(View.GONE);
    }

    @Override
    public void showDeleteBtn() {
        deleteBtn.setVisibility(View.VISIBLE);
    }

    @Override
    public void setActivityTitle(String title) {
        titleView.setText(title);
    }

    @Override
    public void showToastMessage(String msg) {
        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
    }


}

package com.example.timetable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.timetable.contract.EditContract;
import com.example.timetable.presenter.EditPresenter;
import com.example.timetable.timetableview.Schedule;
import com.example.timetable.timetableview.Sticker;
import com.example.timetable.view.TimeBoxView;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class AddEditWorkActivity extends AppCompatActivity implements EditContract.View, DatePickerDialog.OnDateSetListener {
    public static final String EXTRA_ID =
            "com.example.timetable.EXTRA_ID";
    public static final String EXTRA_TITLE =
            "com.example.timetable.EXTRA_TITLE";
    public static final String EXTRA_CODE =
            "com.example.timetable.EXTRA_CODE";
    public static final String EXTRA_DESCRIPTION =
            "com.example.timetable.EXTRA_DESCRIPTION";
    public static final String EXTRA_DUE =
            "com.example.timetable.EXTRA_DUE";


    private EditText editTextTitle, editTextClassCode, editTextDescription;
    private DatePicker editPickerDate;

    public static final int RESULT_OK_ADD = 1;
    public static final int RESULT_OK_EDIT = 2;
    public static final int RESULT_OK_DELETE = 3;

    //private String currentDateString;
    private Date currentDate;
    private Date dateEdit;

    private EditPresenter editPresenter;

    ArrayList<Schedule> allSchedules = new ArrayList<Schedule>();

    AutoCompleteTextView autoCompleteTxt;
    ArrayAdapter<String> adapterItems;
    DialogFragment datePicker;
    Intent intent;

    TextView titleView;


    TimeBoxView timeBox;
    Button addTimeBtn;

    LinearLayout backBtn;
    LinearLayout addBtn;
    LinearLayout deleteBtn;
    Button selectDate;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_picker);
        context=this;

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //editPresenter = new EditPresenter(this);

        titleView = findViewById(R.id.work_header_title);

        editTextTitle = findViewById(R.id.edit_text_title);
        editTextClassCode= findViewById(R.id.auto_complete_txt);
        editTextDescription = findViewById(R.id.edit_text_description);
        /*editPickerDate = findViewById(R.id.edit_picker_date);
        editPickerDate.setVisibility(View.GONE);

        Calendar c = Calendar.getInstance();
        c.add(Calendar.YEAR, -2);
        editPickerDate.setMinDate(c.getTimeInMillis());
        c.add(Calendar.YEAR, 4);
        editPickerDate.setMaxDate(c.getTimeInMillis());*/

        //timeBox = findViewById(R.id.time_box);

        addBtn = findViewById(R.id.work_add);
        backBtn = findViewById(R.id.work_back);

        addTimeBtn = findViewById(R.id.add_time);
        deleteBtn = findViewById(R.id.work_delete);

        autoCompleteTxt = findViewById(R.id.auto_complete_txt);
        /*autoCompleteTxt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                Toast.makeText(getApplicationContext(),"Item: "+item,Toast.LENGTH_SHORT).show();
            }
        });*/

        TextView editTitle = findViewById(R.id.work_header_title);

        intent = getIntent();
        if (intent.hasExtra(EXTRA_ID)){
            editTitle.setText("Edit Work");
            editTextTitle.setText(intent.getStringExtra(EXTRA_TITLE));
            editTextClassCode.setText(intent.getStringExtra(EXTRA_CODE));
            editTextDescription.setText(intent.getStringExtra(EXTRA_DESCRIPTION));
            Date date = (Date) intent.getSerializableExtra(EXTRA_DUE);
            dateEdit = date;
            Log.d("Date from Edit Work: ", date.toString());
            //editPickerDate.updateDate(date.getYear(), date.getMonth(), date.getDay());
        }else{
            editTitle.setText("Add Work");
        }

        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_work_menu, menu);
        return true;
    }

    private void saveWork(){
        String title = editTextTitle.getText().toString();
        String code = editTextClassCode.getText().toString();
        String description = editTextDescription.getText().toString();

        //Log.d("DateFromTimePicker", currentDateString);
        /*int day = editPickerDate.getDayOfMonth();
        int month = editPickerDate.getMonth();
        int year =  editPickerDate.getYear();*/

        if(title.trim().isEmpty() || description.trim().isEmpty() || code.trim().isEmpty()){
            Toast.makeText(this, "Please insert an all values", Toast.LENGTH_SHORT).show();
            return;
        }else if(currentDate==null){
            Toast.makeText(this, "Please choose date", Toast.LENGTH_SHORT).show();
            return;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(currentDate.getYear(), currentDate.getMonth(), currentDate.getDay());
        Date due = currentDate;

        Log.d("DataValues", title+" "+code+" "+description+" "+due.toString());

        Intent data = new Intent();
        data.putExtra(EXTRA_TITLE, title);
        data.putExtra(EXTRA_CODE, code);
        data.putExtra(EXTRA_DESCRIPTION, description);
        data.putExtra(EXTRA_DUE, due);

        int id = getIntent().getIntExtra(EXTRA_ID, -1);
        if (id != -1){
            data.putExtra(EXTRA_ID, id);
        }

        setResult(RESULT_OK, data);
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.save_work:
                //saveWork();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        Set<String> classSet = getClassesCode();
        String[] classes = new String[classSet.size()];
        classSet.toArray(classes);
        //String[] classes = getResources().getStringArray(R.array.days);
        adapterItems = new ArrayAdapter<String>(this,R.layout.list_item,classes);
        autoCompleteTxt.setAdapter(adapterItems);

    }

    public Set<String> getClassesCode(){
        Set<String> classesTitle = new HashSet<String>();
        try {
            SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(this);

            String savedData = mPref.getString(this.getResources().getString(R.string.timetable_repo),"");
            JsonParser parser = new JsonParser();
            JsonObject obj1 = (JsonObject)parser.parse(savedData);
            JsonArray arr1 = obj1.getAsJsonArray("sticker");

            if(arr1.size()!=0){

                for(int i = 0 ; i < arr1.size(); i++){

                    JsonObject obj2 = (JsonObject)arr1.get(i);
                    JsonArray arr2 = (JsonArray)obj2.get("schedule");

                    for(int k = 0 ; k < arr2.size(); k++) {

                        JsonObject obj3 = (JsonObject) arr2.get(k);
                        classesTitle.add(obj3.get("classCode").getAsString());

                    }
                }
                //classesTitle.toArray(classes2);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return classesTitle;
    }

    public void init(){

        selectDate = (Button) findViewById(R.id.select_time_btn);
        selectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //editPickerDate.setVisibility(View.VISIBLE);

                datePicker = new DatePickerFragment();
                if(intent.hasExtra(EXTRA_ID)){

                    SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
                    Date datex = null;
                    try {
                        datex = sdf.parse(dateEdit.toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    SimpleDateFormat sdf2=new SimpleDateFormat("dd-MM-yyyy");
                    String [] dates = sdf2.format(datex.getTime()).toString().split("-");

                    DatePickerDialog datePickerEdit = new DatePickerDialog( context, (DatePickerDialog.OnDateSetListener) AddEditWorkActivity.this, Integer.valueOf(dates[2]), Integer.valueOf(dates[1])-1, Integer.valueOf(dates[0]));
                    datePickerEdit.show();
                }else{
                    datePicker.show(getSupportFragmentManager(), "date picker");
                }
            }
        });

        /*addTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editPresenter.clickAddTimeBtn();
            }
        });*/

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
                saveWork();
                //editPresenter.submitWork(allSchedules,timeBox.getSchedules());
                //finish();
            }
        });

        /*deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editPresenter.clickDeleteBtn();
            }
        });*/
    }

    private boolean isEditMode() {
        int mode = getIntent().getIntExtra("mode", -1);
        if (mode == TimetableActivity.REQUEST_EDIT) return true;
        return false;
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

    @Override
    public void createTimeView(Schedule schedule) {
        timeBox.add(schedule);
    }

    private ArrayList<Schedule> addMetaData(ArrayList<Schedule> resultSchedule){
        for(Schedule schedule : resultSchedule){
            schedule.setWorkTitle(editTextTitle.getText().toString());
            schedule.setClassCode(editTextClassCode.getText().toString());
            schedule.setWorkDescription(editTextDescription.getText().toString());
        }
        return resultSchedule;
    }

    @Override
    public void setResult(ArrayList<Schedule> resultSchedule) {
        Intent i = new Intent();
        if(resultSchedule == null){
            i.putExtra("idx",getIntent().getIntExtra("idx",-1));
            i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            setResult(RESULT_OK_DELETE,i);
            finish();
            overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_left);
            return;
        }
        resultSchedule = addMetaData(resultSchedule);
        i.putExtra("schedules", resultSchedule);
        Log.d("Mode", String.valueOf(isEditMode()));
        if(isEditMode()){
            i.putExtra("idx",getIntent().getIntExtra("idx",-1));
            setResult(RESULT_OK_EDIT, i);
        }
        else{
            setResult(RESULT_OK_ADD, i);

        }
        finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void restoreViews(ArrayList<Schedule> schedules) {
        /*for(Schedule schedule : schedules) {
            classTitle.setText(schedule.getClassTitle());
            classCode.setText(schedule.getClassCode());
            classPlace.setText(schedule.getClassPlace());
            professorName.setText(schedule.getProfessorName());
            timeBox.add(schedule);
        }*/
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        currentDate=c.getTime();

        //currentDateString = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());
        //currentDateString = DateFormat.getDateInstance().format(c.getTime());

        Log.d("currentDateString", currentDate.toString());

        /*TextView textView*/
    }
}
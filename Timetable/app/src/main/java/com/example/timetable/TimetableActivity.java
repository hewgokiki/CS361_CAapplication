package com.example.timetable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.timetable.contract.MainContract;
import com.example.timetable.model.PrefManager;
import com.example.timetable.presenter.MainPresenter;
import com.example.timetable.timetableview.Schedule;
import com.example.timetable.timetableview.Sticker;
import com.example.timetable.timetableview.TimetableView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class TimetableActivity extends AppCompatActivity implements MainContract.View {
    public static WeakReference<TimetableActivity> weakActivity;

    private static final String TAG = "TimetableActivity";

    private static final int REQUEST_ADD = 1;
    public static final int REQUEST_EDIT = 2;

    private LinearLayout addBtn;
    private MainContract.UserActions mainPresenter;
    private Context context;

    private TimetableView timetable;

    BottomNavigationView bottomNav;

    public static TimetableActivity getInstanceActivity() {
        return weakActivity.get();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);
        weakActivity = new WeakReference<>(TimetableActivity.this);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setSelectedItemId(R.id.menuTimetable);
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                menuSelector(item);
                return false;
            }
        });

        //timetable_field

        context = this;
        mainPresenter = new MainPresenter(this);
        mainPresenter.setPrefManager(PrefManager.getInstance());

        timetable = findViewById(R.id.timetable);
        timetable.setOnStickerSelectEventListener(new TimetableView.OnStickerSelectedListener() {
            @Override
            public void OnStickerSelected(int idx, ArrayList<Schedule> schedules) {
                Log.d("MainStickerCalledHere", "sticker");
                mainPresenter.selectSticker(idx,schedules, timetable);
            }
        });
        addBtn = findViewById(R.id.addBtn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainPresenter.addMenuClick();
            }
        });

        mainPresenter.prepare();
    }

    private boolean menuSelector(MenuItem item){
        switch (item.getItemId()){
            case R.id.menuHome:
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(0,0);
                return true;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_ADD:
                if (resultCode == EditActivity.RESULT_OK_ADD) {
                    ArrayList<Schedule> item = (ArrayList<Schedule>) data.getSerializableExtra("schedules");
                    timetable.add(item);
                }
                break;
            case REQUEST_EDIT:
                if (resultCode == EditActivity.RESULT_OK_EDIT) {
                    int idx = data.getIntExtra("idx", -1);
                    ArrayList<Schedule> item = (ArrayList<Schedule>) data.getSerializableExtra("schedules");
                    timetable.edit(idx, item);
                } else if (resultCode == EditActivity.RESULT_OK_DELETE) {
                    int idx = data.getIntExtra("idx", -1);
                    timetable.remove(idx);
                }
                break;
        }
        mainPresenter.save(timetable.createSaveData());
    }

    @Override
    public void startEditActivityForAdd() {
        Intent i = new Intent(context, EditActivity.class);
        i.putExtra("allSchedules",timetable.getAllSchedulesInStickers());
        startActivityForResult(i, REQUEST_ADD);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void startEditActivityForEdit(int idx, ArrayList<Schedule> schedules) {
        Intent i = new Intent(this, EditActivity.class);
        i.putExtra("idx",idx);
        i.putExtra("mode", REQUEST_EDIT);
        i.putExtra("allSchedules", timetable.getAllSchedulesInStickersExceptIdx(idx));
        i.putExtra("schedules", schedules);
        startActivityForResult(i, REQUEST_EDIT);
    }

    @Override
    public void startEditActivityForInfo(int idx, ArrayList<Schedule> schedules, TimetableView timetables) {
        getStickerColor();
        Intent i = new Intent(this, InfoActivity.class);
        i.putExtra("idx",idx);
        i.putExtra("mode", REQUEST_EDIT);
        i.putExtra("allSchedules", timetables.getAllSchedulesInStickersExceptIdx(idx));
        i.putExtra("schedules", schedules);
        startActivityForResult(i, REQUEST_EDIT);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void restoreTimetable(String data) {
        timetable.load(data);
    }

    @Override
    public void setDayHighlight(int day) {
        if(day > 0) timetable.setHeaderHighlight(day);
    }

    private void getStickerColor() {
        HashMap<Integer, Sticker> stickers = new HashMap<Integer, Sticker>();

        String[] stickerColors = context.getResources().getStringArray(R.array.default_sticker_color);

        int size = stickers.size();
        int[] orders = new int[size];
        int i = 0;
        for (int key : stickers.keySet()) {
            orders[i++] = key;
        }
        Log.d("Colors", String.valueOf(orders.length));
        /*Arrays.sort(orders);

        int colorSize = stickerColors.length;

        for (i = 0; i < size; i++) {
            for (TextView v : stickers.get(orders[i]).getView()) {
                v.setBackgroundColor(Color.parseColor(stickerColors[i % (colorSize)]));
            }
        }*/

    }

}
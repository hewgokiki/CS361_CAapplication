package com.example.timetable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.timetable.contract.MainContract;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Date;
import java.util.List;

public class WorksActivity extends AppCompatActivity {
    public static final int ADD_WORK_REQUEST = 1;
    public static final int EDIT_WORK_REQUEST = 2;

    private WorkViewModel workViewModel;

    AutoCompleteTextView autoCompleteTxt;
    ArrayAdapter<String> adapterItems;

    private static final int REQUEST_ADD = 1;
    public static final int REQUEST_EDIT = 2;

    private LinearLayout addBtn;
    private LinearLayout layout;
    AlertDialog dialog;
    BottomNavigationView bottomNav;

    private WorkAdapter adapter;

    LinearLayout noWorkYet;
    private boolean noWork = false;

    private MainContract.UserActions mainPresenter;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_works);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(false);

        adapter = new WorkAdapter();
        recyclerView.setAdapter(adapter);

        noWorkYet = (LinearLayout) findViewById(R.id.noWorkYet);

        workViewModel = ViewModelProviders.of(this).get(WorkViewModel.class);
        workViewModel.getAllWorks().observe(this, new Observer<List<Work>>() {
            @Override
            public void onChanged(List<Work> works) {
                if(works.size()==0||noWork==true){
                    noWork=true;
                    noWorkYet.setVisibility(View.VISIBLE);
                    return;
                }else{
                    noWork=false;
                    noWorkYet.setVisibility(View.INVISIBLE);
                    adapter.submitList(works);
                }
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
                Toast.makeText(WorksActivity.this, "Work deleted", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(recyclerView);

        adapter.setOnItemClickListener(new WorkAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Work work) {
                Intent intent = new Intent(WorksActivity.this, AddEditWorkActivity.class);
                intent.putExtra(AddEditWorkActivity.EXTRA_ID, work.getId());
                intent.putExtra(AddEditWorkActivity.EXTRA_CODE, work.getClassCode());
                intent.putExtra(AddEditWorkActivity.EXTRA_TITLE, work.getTitle());
                intent.putExtra(AddEditWorkActivity.EXTRA_DESCRIPTION, work.getDescription());
                intent.putExtra(AddEditWorkActivity.EXTRA_DUE, work.getDue());
                startActivityForResult(intent, EDIT_WORK_REQUEST);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }

        });

        context = this;

        init();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_WORK_REQUEST && resultCode == RESULT_OK){
            String title = data.getStringExtra(AddEditWorkActivity.EXTRA_TITLE);
            String code = data.getStringExtra(AddEditWorkActivity.EXTRA_CODE);
            String description = data.getStringExtra(AddEditWorkActivity.EXTRA_DESCRIPTION);
            Date due = (Date) data.getSerializableExtra(AddEditWorkActivity.EXTRA_DUE);

            Work work = new Work(code, title,  description, due);
            workViewModel.insert(work);
            noWork=false;

            Toast.makeText(this, "Work saved", Toast.LENGTH_SHORT).show();
        }else if (requestCode == EDIT_WORK_REQUEST && resultCode == RESULT_OK){
            int id = data.getIntExtra(AddEditWorkActivity.EXTRA_ID, -1);

            if (id == -1){
                Toast.makeText(this, "Work can't be updated", Toast.LENGTH_SHORT).show();
                return;
            }
            String title = data.getStringExtra(AddEditWorkActivity.EXTRA_TITLE);
            String code = data.getStringExtra(AddEditWorkActivity.EXTRA_CODE);
            String description = data.getStringExtra(AddEditWorkActivity.EXTRA_DESCRIPTION);
            Date due = (Date) data.getSerializableExtra(AddEditWorkActivity.EXTRA_DUE);

            Work work = new Work(code, title,  description, due);
            work.setId(id);
            workViewModel.update(work);
        } else{
            Toast.makeText(this, "Work not saved", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.delete_all_works:
                workViewModel.deleteAllWorks();
                Toast.makeText(this, "All Works deleted", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public void init(){

        addBtn = findViewById(R.id.addBtn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopUpMenu(view);
                //Intent intent = new Intent(WorksActivity.this, AddEditWorkActivity.class);
                //startActivityForResult(intent, ADD_WORK_REQUEST);
            }
        });

        bottomNav = findViewById(R.id.bottomNavigationView);

        bottomNav.setSelectedItemId(R.id.menuWorks);

        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                menuSelector(item);
                return false;
            }
        });

    }

    /*protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_ADD:
                if (resultCode == EditActivity.RESULT_OK_ADD) {
                    ArrayList<Schedule> item = (ArrayList<Schedule>) data.getSerializableExtra("schedules");
                    //save(item.toString());
                }
                //Log.d("WorksActivity", "REQUEST_ADD");
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
        //mainPresenter.save(timetable.createSaveData());
    }*/

    /*public void save(String data) {
        save(this,data);
    }*/

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
                /*startActivity(new Intent(getApplicationContext(), WorksActivity.class));
                overridePendingTransition(0,0);
                return true;*/
            case R.id.menuSetting:
                startActivity(new Intent(getApplicationContext(), SettingActivity.class));
                overridePendingTransition(0,0);
                return true;
        }
        return false;
    }

    public void startEditActivityForAdd() {
        Intent i = new Intent(context, AddEditWorkActivity.class);
        i.putExtra("allSchedules", "");
        startActivityForResult(i, REQUEST_ADD);
    }

    public void showPopUpMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.getMenuInflater().inflate(R.menu.works_action_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.menuDeleteAll:
                    android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(context, R.style.AppTheme_Dialog);
                    alertDialogBuilder.setTitle(R.string.delete_confirmation).setMessage(R.string.sureToDelete).
                            setPositiveButton(R.string.yes, (dialog, which) -> {
                                workViewModel.deleteAllWorks();
                                adapter.submitList(null);
                            })
                            .setNegativeButton(R.string.no, (dialog, which) -> dialog.cancel()).show();
                    break;
                case R.id.menuAdd:
                    Intent intent = new Intent(WorksActivity.this, AddEditWorkActivity.class);
                    startActivityForResult(intent, ADD_WORK_REQUEST);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    break;
                /*case R.id.menuComplete:
                    android.app.AlertDialog.Builder completeAlertDialog = new android.app.AlertDialog.Builder(context, R.style.AppTheme_Dialog);
                    completeAlertDialog.setTitle(R.string.confirmation).setMessage(R.string.sureToMarkAsComplete).
                            setPositiveButton(R.string.yes, (dialog, which) -> showCompleteDialog(task.getTaskId(), position))
                            .setNegativeButton(R.string.no, (dialog, which) -> dialog.cancel()).show();
                    break;*/
            }
            return false;
        });
        popupMenu.show();
    }
}
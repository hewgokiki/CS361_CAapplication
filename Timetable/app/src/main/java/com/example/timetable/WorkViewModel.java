package com.example.timetable;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class WorkViewModel extends AndroidViewModel {

    private WorkRepository repository;
    private LiveData<List<Work>> allWorks;

    public WorkViewModel(@NonNull Application application) {
        super(application);
        repository = new WorkRepository(application);
        allWorks = repository.getAllWorks();
    }

    public void insert(Work work){
        repository.insert(work);
    }

    public void update(Work work){
        repository.update(work);
    }

    public void delete(Work work){
        repository.delete(work);
    }

    public void deleteAllWorks(){
        repository.deleteAllWorks();
    }

    public LiveData<List<Work>> getAllWorks(){
        return allWorks;
    }
}

package com.example.timetable;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface WorkDaoForCalendar {

    @Insert
    void insert(Work work);

    @Update
    void update(Work work);

    @Delete
    void delete(Work work);

    @Query("DELETE FROM work_repo")
    void deleteAllWorks();

    @Query("SELECT * FROM work_repo ORDER BY due ASC")
    List<Work> getAllWorks();



}

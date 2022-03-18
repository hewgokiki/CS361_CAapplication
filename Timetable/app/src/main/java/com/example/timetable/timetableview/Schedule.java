package com.example.timetable.timetableview;

import java.io.Serializable;
import java.util.Date;

public class Schedule implements Serializable {
    static final int MON = 0;
    static final int TUE = 1;
    static final int WED = 2;
    static final int THU = 3;
    static final int FRI = 4;
    static final int SAT = 5;
    static final int SUN = 6;

    String classTitle="";
    String classCode="";
    String classPlace="";
    String professorName="";
    String workTitle="";
    String workDescription="";
    Date workDue;

    private int day = 0;
    private Time startTime;
    private Time endTime;

    private int dueDay = 0;
    private int dueMonth = 0;
    private int dueYear = 0;

    public Schedule() {
        this.workDue = new Date();
        this.startTime = new Time();
        this.endTime = new Time();
    }

    public String getProfessorName() {
        return professorName;
    }

    public void setProfessorName(String professorName) {
        this.professorName = professorName;
    }

    public String getClassTitle() {
        return classTitle;
    }

    public void setClassTitle(String classTitle) {
        this.classTitle = classTitle;
    }

    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    public String getClassPlace() {
        return classPlace;
    }

    public void setClassPlace(String classPlace) {
        this.classPlace = classPlace;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    public String getWorkTitle() {
        return workTitle;
    }

    public void setWorkTitle(String workTitle) {
        this.workTitle = workTitle;
    }

    public String getWorkDescription() {
        return workDescription;
    }

    public void setWorkDescription(String workDescription) {
        this.workDescription = workDescription;
    }

    public Date getWorkDue() {
        return workDue;
    }

    public void setWorkDue(Date workDue) {
        this.workDue = workDue;
    }
}

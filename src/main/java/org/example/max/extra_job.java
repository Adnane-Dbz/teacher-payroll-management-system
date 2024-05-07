package org.example.max;

import java.util.Calendar;
public class extra_job {
    private int id;
    private String job_name;
    private int job_bonus;
    private Calendar effectiveStartDate;
    private Calendar effectiveEndDate;

    public extra_job(int id, String job_name, int job_bonus) {
        this.id = id;
        this.job_name = job_name;
        this.job_bonus = job_bonus;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getJob_name() {
        return job_name;
    }

    public void setJob_name(String job_name) {
        this.job_name = job_name;
    }

    public int getJob_bonus() {
        return job_bonus;
    }

    public void setJob_bonus(int job_bonus) {
        this.job_bonus = job_bonus;
    }

    public Calendar getEffectiveStartDate() {
        return effectiveStartDate;
    }

    public void setEffectiveStartDate(Calendar effectiveStartDate) {
        this.effectiveStartDate = effectiveStartDate;
    }

    public Calendar getEffectiveEndDate() {
        return effectiveEndDate;
    }

    public void setEffectiveEndDate(Calendar effectiveEndDate) {
        this.effectiveEndDate = effectiveEndDate;
    }
}
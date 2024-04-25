package org.example.max;

import java.util.Calendar;

public class Grade {
    int id;
    String  grade;

    public Grade(int id, String grade, Calendar effectiveStartDate, Calendar effectiveEndDate) {
        this.id = id;
        this.grade = grade;
        this.effectiveStartDate = effectiveStartDate;
        this.effectiveEndDate = effectiveEndDate;
    }

    private Calendar effectiveStartDate; // Added effective start date
    private Calendar effectiveEndDate; // Added effective end date
    public Calendar getEffectiveStartDate() {
        return effectiveStartDate;
    }

    public void setEffectiveStartDate(Calendar effectiveStartDate) {
        this.effectiveStartDate = effectiveStartDate;
    }

    // Getters and setters for effective end date
    public Calendar getEffectiveEndDate() {
        return effectiveEndDate;
    }

    public void setEffectiveEndDate(Calendar effectiveEndDate) {
        this.effectiveEndDate = effectiveEndDate;
    }

}


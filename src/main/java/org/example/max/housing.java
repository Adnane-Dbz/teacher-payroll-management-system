package org.example.max;

import java.util.Calendar;

public class housing {

    int id;
    String adresse;
    char residence_type;

    public housing( String adresse, char residence_type) {
        this.adresse = adresse;
        this.residence_type = residence_type;
    }


    private Calendar effectiveStartDate; // Added effective start date
    private Calendar effectiveEndDate; // Added effective end date

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public char getResidence_type() {
        return residence_type;
    }

    public void setResidence_type(char residence_type) {
        this.residence_type = residence_type;
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

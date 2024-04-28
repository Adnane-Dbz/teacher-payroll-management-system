package org.example.max;

import javafx.scene.control.DatePicker;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

public abstract class Personne {


    private String nom;
    private String prenom;
    private Calendar dateNaissance;
    private LocalDate localDateNaissance;
    private String sex;
    private Calendar effectiveStartDate;
    private Calendar effectiveEndDate;

    // Constructor accepting either DatePicker or Calendar for dateNaissance
    public Personne(String nom, String prenom, String sex, Object dateNaissance) {
        this.nom = nom;
        this.prenom = prenom;
        this.sex = sex;

        if (dateNaissance instanceof DatePicker) {
            setDateNaissance((DatePicker) dateNaissance);
        } else if (dateNaissance instanceof Calendar) {
            setDateNaissance((Calendar) dateNaissance);
        } else if (dateNaissance instanceof Date) {
            setDateNaissance((Date) dateNaissance);
        } else if (dateNaissance instanceof LocalDate) {
            setDateNaissance((LocalDate) dateNaissance);
        } else {
            throw new IllegalArgumentException("Unsupported type for dateNaissance");
        }

    }

    public Calendar getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(Calendar dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public void setDateNaissance(DatePicker datePicker) {
        LocalDate localDate = datePicker.getValue();
        if (localDate != null) {
            Instant instant = Instant.from(localDate.atStartOfDay(ZoneId.systemDefault()));
            Date date = Date.from(instant);
            this.dateNaissance = Calendar.getInstance();
            this.dateNaissance.setTime(date);
        }
    }

    public void setDateNaissance(Date date) {
        if (date != null) {
            this.dateNaissance = Calendar.getInstance();
            this.dateNaissance.setTime(date);
        }
    }

    public void setDateNaissance(LocalDate localDate) {
        this.localDateNaissance = localDate;
    }

    public LocalDate getLocalDateNaissance() {
        return localDateNaissance;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
      this.sex =sex;
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

    public int getAge() {
        if (localDateNaissance != null) {
            LocalDate now = LocalDate.now();
            return Period.between(localDateNaissance, now).getYears();
        } else if (dateNaissance != null) {
            LocalDate now = LocalDate.now();
            int yearNow = now.getYear();
            int monthNow = now.getMonthValue();
            int dayOfMonthNow = now.getDayOfMonth();

            int yearOfBirth = dateNaissance.get(Calendar.YEAR);
            int monthOfBirth = dateNaissance.get(Calendar.MONTH) + 1; // Month is 0-based in Calendar, so adding 1
            int dayOfMonthOfBirth = dateNaissance.get(Calendar.DAY_OF_MONTH);

            // Calculate age
            int age = yearNow - yearOfBirth;
            if (monthNow < monthOfBirth || (monthNow == monthOfBirth && dayOfMonthNow < dayOfMonthOfBirth)) {
                age--; // Not had birthday yet this year
            }
            return age;
        } else {
            throw new IllegalStateException("Date of birth not set");
        }
    }



}

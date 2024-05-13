package org.example.max;

public class partner extends Personne{

    int id;
    public void setId(int  id) {
        this.id=  id;
    }
    public int getId() {
        return id;
    }
    boolean work;
    public boolean isWork() {
        return work;
    }
    public void setWork(boolean a) {
        this.work=a;
    }


    public partner(String nom, String prenom, String sex, Object calendar ,boolean work) {
        super(nom, prenom, sex, calendar );this.work = work;
    }



}

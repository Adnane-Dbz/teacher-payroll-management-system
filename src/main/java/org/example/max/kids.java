package org.example.max;

public class kids extends Personne{

    private boolean condition;
    int id;
    public void setId(int  id) {
        this.id=  id;
    }
    public int getId() {
        return id;
    }


    boolean adoupted;

    public boolean isAdoupted(){
        return adoupted;
    }

    public void setAdoupted(boolean adoupted) {
        this.adoupted = adoupted;
    }

    public kids(String nom, String prenom, String sex, Object a, boolean condition, boolean adoupted) {
        super(nom, prenom, sex,a); this.condition = condition;this.adoupted = adoupted;id=-1;
    }

    public void setCondition(boolean condition) {
        this.condition = condition;
    }



    public boolean isCondition() {
        return condition;
    }

}

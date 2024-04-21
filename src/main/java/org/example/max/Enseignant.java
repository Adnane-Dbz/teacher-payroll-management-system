package org.example.max;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class Enseignant extends Personne {
    public String  dateb;
    public String getFormattedDateNaissance() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        return dateFormat.format(this.getDateNaissance().getTime());
    }
    public void getbdate() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

        dateb= dateFormat.format(this.getDateNaissance().getTime());
    }
    public void printDetails() {
        StringBuilder details = new StringBuilder();

        details.append("Enseignant Details:\n");
        details.append("-------------------\n");
        details.append("Name: ").append(super.getNom()).append(" ").append(super.getPrenom()).append("\n");
        details.append("Sex: ").append(super.getSex()).append("\n");
        details.append("Date of Birth: ").append(this.dateb).append("\n");
        details.append("Married: ").append(married ? "Yes" : "No").append("\n");
        details.append("Email: ").append(adresseEmail).append("\n");
        details.append("Phone: ").append(tel).append("\n");
        details.append("Year of Bac: ").append(anneeDeBac).append("\n");
        details.append("Department: ").append(department).append("\n");
        details.append("Faculty: ").append(faculte).append("\n");
        details.append("Residence Type: ").append(residence_type).append("\n");
        details.append("Grade: ").append(grade).append("\n");
        details.append("Echelon: ").append(echlont).append("\n");
        details.append("Extra Job: ").append(extra_job).append("\n");
        details.append("Extra Job Bonus: ").append(extra_job_bonus).append("\n");
        details.append("Monthly Absence: ").append(monthly_absense).append("\n");
        details.append("ID Enseignant: ").append(idEnseignant).append("\n");
        details.append("CCP Number: ").append(numéroDeCcp).append("\n");
        details.append("CCP Type: ").append(typeDeCcp).append("\n");

        if (partner != null) {
            details.append("\nPartner Details:\n");
            details.append("-----------------\n");
            details.append("Name: ").append(partner.getNom()).append(" ").append(partner.getPrenom()).append("\n");
            details.append("Sex: ").append(partner.getSex()).append("\n");
            details.append("Date of Birth: ").append(partner.getDateNaissance()).append("\n");
            details.append("Working: ").append(partner.isWork() ? "Yes" : "No").append("\n");
        }

        if (!kidsList.isEmpty()) {
            details.append("\nKids Details:\n");
            details.append("-------------\n");
            for (kids kid : kidsList) {
                details.append("Name: ").append(kid.getNom()).append(" ").append(kid.getPrenom()).append("\n");
                details.append("Sex: ").append(kid.getSex()).append("\n");
                details.append("Date of Birth: ").append(kid.getDateNaissance()).append("\n");
                details.append("Condition: ").append(kid.isCondition() ? "Yes" : "No").append("\n");
                details.append("Adopted: ").append(kid.isAdoupted() ? "Yes" : "No").append("\n");
                details.append("\n");
            }
        }else{}

        System.out.println(details.toString());
    }


    int monthly_absense=0;

    public int getEchlont() {
        return echlont;
    }

    public void setEchlont(int echlont) {
        this.echlont = echlont;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getExtra_job() {
        return extra_job;
    }

    public void setExtra_job(String extra_job) {
        this.extra_job = extra_job;
    }

    int echlont;
    String grade;

    public int getExtra_job_bonus() {
        return extra_job_bonus;
    }

    public void setExtra_job_bonus(int extra_job_bonus) {
        this.extra_job_bonus = extra_job_bonus;
    }
int extra_job_id;
    int extra_job_bonus;
    String extra_job;
    public char getResidence_type() {
        return residence_type;
    }

    public void setResidence_type(char residence_type) {
        this.residence_type = residence_type;
    }

    char residence_type;
    public String idEnseignant;
    public String numéroDeCcp;
    public String typeDeCcp;
    public boolean married;
    public String adresseEmail;
    public int anneeDeBac;
    public String adresse;
    public int tel;



    public String department;
    public String faculte;


    public ArrayList<kids> kidsList;
    public partner partner;

    public Enseignant(String nom, String prenom, String sex, Object dateNaissance, boolean married,
                      String adresseEmail, int tel, int anneeDeBac, String department) {
        super(nom, prenom, validateSex(sex), dateNaissance);
        getbdate();
        this.married = married;
        this.adresseEmail = adresseEmail;
        this.tel = tel;
        this.anneeDeBac = anneeDeBac;
        this.department = department;
        this.kidsList=new ArrayList<>();
        echlont=-1;
        grade=null;
        residence_type='0';
        typeDeCcp=null;
        adresse=null;
        extra_job_bonus=0;
        extra_job=null;
        partner=null;

    }
     String getresidencetype_sentence(){String s;

                if( this.getResidence_type()=='+')s="has no house";
                   else if( this.getResidence_type()=='-')s="has a univ_house";
       else s="has his own house"; return s;
    }

    private static String validateSex(String sex) {
        if (sex == null || (!sex.equals("M") && !sex.equals("F"))) {
            throw new IllegalArgumentException("Invalid sex value. Must be 'M' or 'F'.");

        }
        return sex;
    }

    public String getIdEnseignant() {
        return idEnseignant;
    }

    public void setIdEnseignant(String idEnseignant) {
        this.idEnseignant = idEnseignant;
    }

    public String gettypeDeCcp() {
        return typeDeCcp;
    }

    public void settypeDeCcp(String numéroDeCcp) {
        this.typeDeCcp = numéroDeCcp;
    }
    public String getNuméroDeCcp() {
        return numéroDeCcp;
    }

    public void setNuméroDeCcp(String numéroDeCcp) {
        this.numéroDeCcp = numéroDeCcp;
    }

    public boolean isMarried() {
        return married;
    }

    public void setMarried(boolean married) {
        this.married = married;
    }

    public String getAdresseEmail() {
        return adresseEmail;
    }

    public void setAdresseEmail(String adresseEmail) {
        this.adresseEmail = adresseEmail;
    }

    public int getAnneeDeBac() {
        return anneeDeBac;
    }

    public void setAnneeDeBac(int anneeDeBac) {
        this.anneeDeBac = anneeDeBac;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public int getTel() {
        return tel;
    }

    public void setTel(int tel) {
        this.tel = tel;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getFaculte() {
        return faculte;
    }

    public void setFaculte(String faculte) {
        this.faculte = faculte;
    }

    public ArrayList<kids> getKidsList() {
        return kidsList;
    }

    public void setKidsList(ArrayList<kids> kidsList) {
        this.kidsList = kidsList;
    }

    public partner getPartner() {
        return partner;
    }

    public void setPartner(partner partner) {
        this.partner = partner;}

    public void setFamilyMembers(ArrayList<Personne> familyMembers) {
        if (familyMembers == null || familyMembers.isEmpty()) {
            return;
        }
        partner = null;
        kidsList.clear();

        for (Personne member : familyMembers) {
            if (member instanceof partner) {
                if (partner == null) {
                    partner = (partner) member;
                } else {
                    throw new IllegalArgumentException("There can be only one partner.");
                }
            } else if (member instanceof kids) {
                kidsList.add((kids) member);
            } else {
                throw new IllegalArgumentException("Family member must be either a partner or a kid.");
            }
        }
    }

void set_job(extra_job job) {
        this.extra_job=job.getJob_name();
        this.extra_job_bonus=job.getJob_bonus();
        this.extra_job_id=job.getId();
}


}

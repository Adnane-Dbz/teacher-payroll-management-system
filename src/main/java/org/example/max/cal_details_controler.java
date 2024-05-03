package org.example.max;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import static org.example.max.TypeExtraPostManager.add_or_update_details_static;
import static org.example.max.TypeExtraPostManager.get_salary_info;
import static org.example.max.work_space.showAlert;

public class cal_details_controler {
    String currentGrade;


    @FXML
    private TextField UPDATE_SALLARY_DETAILS_basic_sallary;

    @FXML
    private TextField UPDATE_SALLARY_DETAILS_calification_scientifique;

    @FXML
    private TextField UPDATE_SALLARY_DETAILS_documentation;

    @FXML
    private TextField UPDATE_SALLARY_DETAILS_echelon01;

    @FXML
    private TextField UPDATE_SALLARY_DETAILS_echelon02;

    @FXML
    private TextField UPDATE_SALLARY_DETAILS_echelon03;

    @FXML
    private TextField UPDATE_SALLARY_DETAILS_echelon04;

    @FXML
    private TextField UPDATE_SALLARY_DETAILS_echelon05;

    @FXML
    private TextField UPDATE_SALLARY_DETAILS_echelon06;

    @FXML
    private TextField UPDATE_SALLARY_DETAILS_echelon07;

    @FXML
    private TextField UPDATE_SALLARY_DETAILS_echelon08;

    @FXML
    private TextField UPDATE_SALLARY_DETAILS_echelon09;

    @FXML
    private TextField UPDATE_SALLARY_DETAILS_echelon10;

    @FXML
    private TextField UPDATE_SALLARY_DETAILS_echelon11;

    @FXML
    private TextField UPDATE_SALLARY_DETAILS_echelon12;

    @FXML
    private TextField UPDATE_SALLARY_DETAILS_encadrement;

    @FXML
    private Button UPDATE_SALLARY_DETAILS_grade_maa;

    @FXML
    private Button UPDATE_SALLARY_DETAILS_grade_mab;

    @FXML
    private Button UPDATE_SALLARY_DETAILS_grade_mca;

    @FXML
    private Button UPDATE_SALLARY_DETAILS_grade_mcb;

    @FXML
    private Button UPDATE_SALLARY_DETAILS_grade_professor;

    @FXML
    private Button UPDATE_SALLARY_DETAILS_update_button;
























    @FXML
    public void setPRO() {
        currentGrade = "PR";
        int[] salary=null; salary = get_salary_info("PR", null);

        if (salary != null){
            UPDATE_SALLARY_DETAILS_basic_sallary.setText(String.valueOf(salary[0]));
            UPDATE_SALLARY_DETAILS_echelon01.setText(String.valueOf(salary[1]));
            UPDATE_SALLARY_DETAILS_echelon02.setText(String.valueOf(salary[2]));
            UPDATE_SALLARY_DETAILS_echelon03.setText(String.valueOf(salary[3]));
            UPDATE_SALLARY_DETAILS_echelon04.setText(String.valueOf(salary[4]));
            UPDATE_SALLARY_DETAILS_echelon05.setText(String.valueOf(salary[5]));
            UPDATE_SALLARY_DETAILS_echelon06.setText(String.valueOf(salary[6]));
            UPDATE_SALLARY_DETAILS_echelon07.setText(String.valueOf(salary[7]));
            UPDATE_SALLARY_DETAILS_echelon08.setText(String.valueOf(salary[8]));
            UPDATE_SALLARY_DETAILS_echelon09.setText(String.valueOf(salary[9]));
            UPDATE_SALLARY_DETAILS_echelon10.setText(String.valueOf(salary[10]));
            UPDATE_SALLARY_DETAILS_echelon11.setText(String.valueOf(salary[11]));
            UPDATE_SALLARY_DETAILS_echelon12.setText(String.valueOf(salary[12]));
            UPDATE_SALLARY_DETAILS_encadrement.setText(String.valueOf(salary[13]));
            UPDATE_SALLARY_DETAILS_documentation.setText(String.valueOf(salary[14]));
            UPDATE_SALLARY_DETAILS_calification_scientifique.setText(String.valueOf(salary[15]));

        } else {
            showAlert("No salary details found for the given grade.maybe this is you first time");
        }
    }

    @FXML
    public void setMAA() {
        currentGrade = "AA";
        int[] salary=null; salary= get_salary_info("AA", null);

        if (salary != null){
            UPDATE_SALLARY_DETAILS_basic_sallary.setText(String.valueOf(salary[0]));
            UPDATE_SALLARY_DETAILS_echelon01.setText(String.valueOf(salary[1]));
            UPDATE_SALLARY_DETAILS_echelon02.setText(String.valueOf(salary[2]));
            UPDATE_SALLARY_DETAILS_echelon03.setText(String.valueOf(salary[3]));
            UPDATE_SALLARY_DETAILS_echelon04.setText(String.valueOf(salary[4]));
            UPDATE_SALLARY_DETAILS_echelon05.setText(String.valueOf(salary[5]));
            UPDATE_SALLARY_DETAILS_echelon06.setText(String.valueOf(salary[6]));
            UPDATE_SALLARY_DETAILS_echelon07.setText(String.valueOf(salary[7]));
            UPDATE_SALLARY_DETAILS_echelon08.setText(String.valueOf(salary[8]));
            UPDATE_SALLARY_DETAILS_echelon09.setText(String.valueOf(salary[9]));
            UPDATE_SALLARY_DETAILS_echelon10.setText(String.valueOf(salary[10]));
            UPDATE_SALLARY_DETAILS_echelon11.setText(String.valueOf(salary[11]));
            UPDATE_SALLARY_DETAILS_echelon12.setText(String.valueOf(salary[12]));
            UPDATE_SALLARY_DETAILS_encadrement.setText(String.valueOf(salary[13]));
            UPDATE_SALLARY_DETAILS_documentation.setText(String.valueOf(salary[14]));
            UPDATE_SALLARY_DETAILS_calification_scientifique.setText(String.valueOf(salary[15]));

        } else {
            showAlert("No salary details found for the given grade.maybe this is you first time");
        }
    }

    @FXML
    public void setMCA() {
        currentGrade = "CA";
        int[] salary=null; salary = get_salary_info("CA", null);

            if (salary != null){
                UPDATE_SALLARY_DETAILS_basic_sallary.setText(String.valueOf(salary[0]));
                UPDATE_SALLARY_DETAILS_echelon01.setText(String.valueOf(salary[1]));
                UPDATE_SALLARY_DETAILS_echelon02.setText(String.valueOf(salary[2]));
                UPDATE_SALLARY_DETAILS_echelon03.setText(String.valueOf(salary[3]));
                UPDATE_SALLARY_DETAILS_echelon04.setText(String.valueOf(salary[4]));
                UPDATE_SALLARY_DETAILS_echelon05.setText(String.valueOf(salary[5]));
                UPDATE_SALLARY_DETAILS_echelon06.setText(String.valueOf(salary[6]));
                UPDATE_SALLARY_DETAILS_echelon07.setText(String.valueOf(salary[7]));
                UPDATE_SALLARY_DETAILS_echelon08.setText(String.valueOf(salary[8]));
                UPDATE_SALLARY_DETAILS_echelon09.setText(String.valueOf(salary[9]));
                UPDATE_SALLARY_DETAILS_echelon10.setText(String.valueOf(salary[10]));
                UPDATE_SALLARY_DETAILS_echelon11.setText(String.valueOf(salary[11]));
                UPDATE_SALLARY_DETAILS_echelon12.setText(String.valueOf(salary[12]));
                UPDATE_SALLARY_DETAILS_encadrement.setText(String.valueOf(salary[13]));
                UPDATE_SALLARY_DETAILS_documentation.setText(String.valueOf(salary[14]));
                UPDATE_SALLARY_DETAILS_calification_scientifique.setText(String.valueOf(salary[15]));

            } else {
            showAlert("No salary details found for the given grade.maybe this is you first time");
        }
    }

    @FXML
    public void setMAB() {
        currentGrade = "AB";
        int[] salary=null; salary= get_salary_info("AB", null);

        if (salary != null){
            UPDATE_SALLARY_DETAILS_basic_sallary.setText(String.valueOf(salary[0]));
            UPDATE_SALLARY_DETAILS_echelon01.setText(String.valueOf(salary[1]));
            UPDATE_SALLARY_DETAILS_echelon02.setText(String.valueOf(salary[2]));
            UPDATE_SALLARY_DETAILS_echelon03.setText(String.valueOf(salary[3]));
            UPDATE_SALLARY_DETAILS_echelon04.setText(String.valueOf(salary[4]));
            UPDATE_SALLARY_DETAILS_echelon05.setText(String.valueOf(salary[5]));
            UPDATE_SALLARY_DETAILS_echelon06.setText(String.valueOf(salary[6]));
            UPDATE_SALLARY_DETAILS_echelon07.setText(String.valueOf(salary[7]));
            UPDATE_SALLARY_DETAILS_echelon08.setText(String.valueOf(salary[8]));
            UPDATE_SALLARY_DETAILS_echelon09.setText(String.valueOf(salary[9]));
            UPDATE_SALLARY_DETAILS_echelon10.setText(String.valueOf(salary[10]));
            UPDATE_SALLARY_DETAILS_echelon11.setText(String.valueOf(salary[11]));
            UPDATE_SALLARY_DETAILS_echelon12.setText(String.valueOf(salary[12]));
            UPDATE_SALLARY_DETAILS_encadrement.setText(String.valueOf(salary[13]));
            UPDATE_SALLARY_DETAILS_documentation.setText(String.valueOf(salary[14]));
            UPDATE_SALLARY_DETAILS_calification_scientifique.setText(String.valueOf(salary[15]));

        } else {
            showAlert("No salary details found for the given grade.");
        }
    }
    @FXML
    public void setMCB() {
        currentGrade = "CB";
        int[] salary=null; salary= get_salary_info("CB", null);


             // Assuming you want to display the first set of salaries
if (salary != null){
            UPDATE_SALLARY_DETAILS_basic_sallary.setText(String.valueOf(salary[0]));
            UPDATE_SALLARY_DETAILS_echelon01.setText(String.valueOf(salary[1]));
            UPDATE_SALLARY_DETAILS_echelon02.setText(String.valueOf(salary[2]));
            UPDATE_SALLARY_DETAILS_echelon03.setText(String.valueOf(salary[3]));
            UPDATE_SALLARY_DETAILS_echelon04.setText(String.valueOf(salary[4]));
            UPDATE_SALLARY_DETAILS_echelon05.setText(String.valueOf(salary[5]));
            UPDATE_SALLARY_DETAILS_echelon06.setText(String.valueOf(salary[6]));
            UPDATE_SALLARY_DETAILS_echelon07.setText(String.valueOf(salary[7]));
            UPDATE_SALLARY_DETAILS_echelon08.setText(String.valueOf(salary[8]));
            UPDATE_SALLARY_DETAILS_echelon09.setText(String.valueOf(salary[9]));
            UPDATE_SALLARY_DETAILS_echelon10.setText(String.valueOf(salary[10]));
            UPDATE_SALLARY_DETAILS_echelon11.setText(String.valueOf(salary[11]));
            UPDATE_SALLARY_DETAILS_echelon12.setText(String.valueOf(salary[12]));
            UPDATE_SALLARY_DETAILS_encadrement.setText(String.valueOf(salary[13]));
            UPDATE_SALLARY_DETAILS_documentation.setText(String.valueOf(salary[14]));
            UPDATE_SALLARY_DETAILS_calification_scientifique.setText(String.valueOf(salary[15]));

        } else {
            showAlert("No salary details found for the given grade.");
        }
    }












    public void initialize() {
        addNumberFilter(UPDATE_SALLARY_DETAILS_basic_sallary);
        addNumberFilter(UPDATE_SALLARY_DETAILS_calification_scientifique);
        addNumberFilter(UPDATE_SALLARY_DETAILS_documentation);
        addNumberFilter(UPDATE_SALLARY_DETAILS_echelon01);
        addNumberFilter(UPDATE_SALLARY_DETAILS_echelon02);
        addNumberFilter(UPDATE_SALLARY_DETAILS_echelon03);
        addNumberFilter(UPDATE_SALLARY_DETAILS_echelon04);
        addNumberFilter(UPDATE_SALLARY_DETAILS_echelon05);
        addNumberFilter(UPDATE_SALLARY_DETAILS_echelon06);
        addNumberFilter(UPDATE_SALLARY_DETAILS_echelon07);
        addNumberFilter(UPDATE_SALLARY_DETAILS_echelon08);
        addNumberFilter(UPDATE_SALLARY_DETAILS_echelon09);
        addNumberFilter(UPDATE_SALLARY_DETAILS_echelon10);
        addNumberFilter(UPDATE_SALLARY_DETAILS_echelon11);
        addNumberFilter(UPDATE_SALLARY_DETAILS_echelon12);
        addNumberFilter(UPDATE_SALLARY_DETAILS_encadrement);


    }


    private void handleGradeButtonSelection(String selectedGrade) {
        // Update the currentGrade variable with the selected grade
        currentGrade = selectedGrade;

        // Add logic here to handle the selection, for example, changing styles or updating other UI components
        System.out.println("Selected grade: " + currentGrade);
    }

    public void addNumberFilter(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                textField.setText(oldValue);
            }
        });
    }



    @FXML
    public boolean check_cal_info() {
        TextField[] textFields = {
                UPDATE_SALLARY_DETAILS_basic_sallary,
                UPDATE_SALLARY_DETAILS_calification_scientifique,
                UPDATE_SALLARY_DETAILS_documentation,
                UPDATE_SALLARY_DETAILS_echelon01,
                UPDATE_SALLARY_DETAILS_echelon02,
                UPDATE_SALLARY_DETAILS_echelon03,
                UPDATE_SALLARY_DETAILS_echelon04,
                UPDATE_SALLARY_DETAILS_echelon05,
                UPDATE_SALLARY_DETAILS_echelon06,
                UPDATE_SALLARY_DETAILS_echelon07,
                UPDATE_SALLARY_DETAILS_echelon08,
                UPDATE_SALLARY_DETAILS_echelon09,
                UPDATE_SALLARY_DETAILS_echelon10,
                UPDATE_SALLARY_DETAILS_echelon11,
                UPDATE_SALLARY_DETAILS_echelon12,
                UPDATE_SALLARY_DETAILS_encadrement
        };

        for (TextField textField : textFields) {
            if (textField.getText().isEmpty()) {
                showAlert("Please fill in all fields.");
                return true;
            }
        }

        showAlert("All fields are filled.");
        return false;
    }




    @FXML
    public void UPDATE_cal_info() {
        if(check_cal_info()) return;


        try {
            double basicSalary = Double.parseDouble(UPDATE_SALLARY_DETAILS_basic_sallary.getText());
            int echelon1 = Integer.parseInt(UPDATE_SALLARY_DETAILS_echelon01.getText());
            int echelon2 = Integer.parseInt(UPDATE_SALLARY_DETAILS_echelon02.getText());
            int echelon3 = Integer.parseInt(UPDATE_SALLARY_DETAILS_echelon03.getText());
            int echelon4 = Integer.parseInt(UPDATE_SALLARY_DETAILS_echelon04.getText());
            int echelon5 = Integer.parseInt(UPDATE_SALLARY_DETAILS_echelon05.getText());
            int echelon6 = Integer.parseInt(UPDATE_SALLARY_DETAILS_echelon06.getText());
            int echelon7 = Integer.parseInt(UPDATE_SALLARY_DETAILS_echelon07.getText());
            int echelon8 = Integer.parseInt(UPDATE_SALLARY_DETAILS_echelon08.getText());
            int echelon9 = Integer.parseInt(UPDATE_SALLARY_DETAILS_echelon09.getText());
            int echelon10 = Integer.parseInt(UPDATE_SALLARY_DETAILS_echelon10.getText());
            int echelon11 = Integer.parseInt(UPDATE_SALLARY_DETAILS_echelon11.getText());
            int echelon12 = Integer.parseInt(UPDATE_SALLARY_DETAILS_echelon12.getText());
            int encadrement = Integer.parseInt(UPDATE_SALLARY_DETAILS_encadrement.getText());
            int documentation = Integer.parseInt(UPDATE_SALLARY_DETAILS_documentation.getText());
            int responsabilite = Integer.parseInt(UPDATE_SALLARY_DETAILS_calification_scientifique.getText());

            add_or_update_details_static(
                    currentGrade,
                    basicSalary,
                    echelon1,
                    echelon2,
                    echelon3,
                    echelon4,
                    echelon5,
                    echelon6,
                    echelon7,
                    echelon8,
                    echelon9,
                    echelon10,
                    echelon11,
                    echelon12,
                    encadrement,
                    documentation,
                    responsabilite
            );

            showAlert("Details updated successfully.");

        } catch (NumberFormatException e) {
            showAlert("Please enter valid numeric values.");
        }






}














}

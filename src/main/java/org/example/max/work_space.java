package org.example.max;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.*;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.MonthDay;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.example.max.CustomButtonController.CustomCloseButtons;
import static org.example.max.CustomButtonController.CustomMinButton;
import static org.example.max.LoginController.current_username;
import static org.example.max.PDFModifier.*;
import static org.example.max.TypeExtraPostManager.*;

public class work_space {

    @FXML
    private ComboBox<String> monthComboBox;
    @FXML
    private Label periodLabel;

    @FXML
    private  Label  username;










    public  void general_reset_for_add_update_page_info() {
        addOptions(Update_add_page_teacher_bank_account_type, getTypeComptIds());
        addOptions(Update_add_page_extra_job_choice_box, getavailableExtraPostsnames());
        addOptions(Update_add_page_faculty_choice_box, getFacultes());
        addOptions(Update_add_page_department_choice_box, getDepartements());
        Update_add_page_extra_job_choice_box.getItems().addAll("non");
        Update_add_page_extra_job_choice_box.setValue("non");


    }






    public static List<Double> getTeacherDetails(String grade, int echelon) {
        List<Double> teacherDetails = new ArrayList<>();
        int[] salaryInfo = get_salary_info(grade, null);

        if (salaryInfo != null) {
            double basicSalary = salaryInfo[0];
            double echelonValue = 0; // Assumes echelon is between 1 and 12
            if(echelon!=0)echelonValue=salaryInfo[echelon];
            double encadrement = salaryInfo[13];
            double documentation = salaryInfo[14];
            double responsabilite = salaryInfo[15];

            teacherDetails.add(basicSalary);
            teacherDetails.add(echelonValue);
            teacherDetails.add(encadrement);
            teacherDetails.add(documentation);
            teacherDetails.add(responsabilite);
        }

        return teacherDetails;
    }


    public static List<Double> getTeacherDetails_past(String grade, int echelon, int year, int month) {
        List<Double> teacherDetails = new ArrayList<>();
        int[] salaryInfo = get_salary_info(grade,   year,  month);

        if (salaryInfo != null) {
            double basicSalary = salaryInfo[0];
            double echelonValue = 0; // Assumes echelon is between 1 and 12
            if(echelon!=0)echelonValue=salaryInfo[echelon];
            double encadrement = salaryInfo[13];
            double documentation = salaryInfo[14];
            double responsabilite = salaryInfo[15];

            teacherDetails.add(basicSalary);
            teacherDetails.add(echelonValue);
            teacherDetails.add(encadrement);
            teacherDetails.add(documentation);
            teacherDetails.add(responsabilite);
        }

        return teacherDetails;
    }

    String id_update;
    boolean updating_or_inserting = false;

















    final static String irgfile = "src/main/resources/irg.txt";
    final static String irgfile2 = "src/main/resources/irg2.txt";
    //call stuff
    static public void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void get_DEP_FEC() {
        try {
            Stage newStage = new Stage();
            newStage.initModality(Modality.APPLICATION_MODAL);
            newStage.initOwner(HelloApplication.getPrimaryStage());
            FacDep FacDep = new FacDep();
            FacDep.start(newStage);
//            newStage.setTitle("MANAGE FACULTIES/DEPARTMENTS");
//            newStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void get_and_show_bank_types() {
        try {
            Stage newStage = new Stage();
            newStage.initModality(Modality.APPLICATION_MODAL);
            newStage.initOwner(HelloApplication.getPrimaryStage());
            bankmanager bankmanager = new bankmanager();
            bankmanager.start(newStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void get_Grades_Cal_Info() {
        try {
            Stage newStage = new Stage();
            newStage.initModality(Modality.APPLICATION_MODAL);
            newStage.initOwner(HelloApplication.getPrimaryStage());
            cal_details cal_details = new cal_details();
            cal_details.start(newStage);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void get_Static_Cal_Info() {
        try {
            Stage newStage = new Stage();
            newStage.initModality(Modality.APPLICATION_MODAL);
            newStage.initOwner(HelloApplication.getPrimaryStage());
            cal_details_static calDetailsApp = new cal_details_static();
            calDetailsApp.start(newStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void get_extra_job_Info() {
        try {Stage newStage = new Stage();
            newStage.initModality(Modality.APPLICATION_MODAL);
            newStage.initOwner(HelloApplication.getPrimaryStage());
            JobManagementApp JobManagementApp = new JobManagementApp();
            JobManagementApp.start(newStage);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static int page_I_am_in = 0;
    @FXML
    public void get_help() {

        if(teachers_look_up_page.isVisible()){page_I_am_in = 1;}
        else if(printstuff.isVisible()){page_I_am_in = 2;}
        else if(general.isVisible()){page_I_am_in = 3;}
        else  if(settings.isVisible()){page_I_am_in = 4;}
        else if(addmodifydelet.isVisible()){page_I_am_in = 5;}
        if (page_I_am_in==0|| page_I_am_in==0 ||  page_I_am_in==0 || page_I_am_in==0  ||page_I_am_in==0){ showAlert("there are no help guide for this page its obvious");
            page_I_am_in=0;}
        try {
            Stage newStage = new Stage();
            newStage.initModality(Modality.APPLICATION_MODAL);
            newStage.initOwner(HelloApplication.getPrimaryStage());
            help_app help_app = new help_app();
            help_app.start(newStage);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    public void get_CongeDeMaterniteIntoday() {
        idx=  getSelectedEnseignantId();
        if (idx==null) { showAlert("you didnt select any Enseignant from the table"); return;}
        Enseignant xxx=null;
        try {
            xxx= load_curent_Enseignant(idx);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if (xxx.getSex().equals("M")) { showAlert("a man cant get a (CongeDeMaterniteIntoday)"); return;}

        try {Stage newStage = new Stage();
            newStage.initModality(Modality.APPLICATION_MODAL);
            newStage.initOwner(HelloApplication.getPrimaryStage());
            AddCongeDeMaterniteApp AddCongeDeMaterniteApp = new AddCongeDeMaterniteApp();
            AddCongeDeMaterniteApp.start(newStage);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }





    //////////////////////////////////////////////////////////////

    @FXML
    private TextField  Update_add_page_teacher_nb_social;
    @FXML
    private TableView<Enseignant> enseignantTable;

    @FXML
    private TableColumn<Enseignant, String> idColumn;
    @FXML
    private TableColumn<Enseignant, String> fullNameColumn;
    @FXML
    private TableColumn<Enseignant, String> lastNameColumn;
    @FXML
    private TableColumn<Enseignant, String> birthdayColumn;
    @FXML
    private TableColumn<Enseignant, String> gradeColumn;
    @FXML
    private TableColumn<Enseignant, String> echlontColumn;
    @FXML
    private TableColumn<Enseignant, String> extraPostColumn;
    @FXML
    private TableColumn<Enseignant, String> gmailColumn;
    @FXML
    private TableColumn<Enseignant, String> addressColumn;
    @FXML
    private TableColumn<Enseignant, String> contactNumColumn;
    @FXML
    private TextField teachersLookUpPageSearchWithThis;
    @FXML
    private Button searchButton;

    @FXML
    private TextField   missed_hours_add;
    @FXML
    private ChoiceBox<String> teachersLookUpPageSearchOptionChoiceBox;
    @FXML
    private ChoiceBox<String> teachersLookUpPageSearchDepartment;
    @FXML
    private ChoiceBox<String> teachersLookUpPageSearchGrade;
    @FXML
    private ChoiceBox<String> teachersLookUpPageSearchEchlont;
    @FXML
    private ChoiceBox<String> teachersLookUpPageSearchHousingType;
    @FXML
    private CheckBox teachersLookUpPageSearchMarried;
    @FXML
    private CheckBox teachersLookUpPageSearchHasWife;
    @FXML
    private CheckBox teachersLookUpPageSearchHasKids;

    @FXML
    private CheckBox inactive;
    @FXML
    private CheckBox active;
    @FXML
    private CheckBox all;


    @FXML
    private TextField printstuff_full_name ;
    @FXML
    private TextField printstuff_grade   ;
    @FXML
    private TextField printstuff_num_echlent   ;
    @FXML
    private TextField  printstuff_job    ;
    @FXML
    private CheckBox printstuff_married    ;
    @FXML
    private TextField  printstuff_final_salary    ;
    @FXML
    private TextField printstuff_RETENUE_LOGEMENT    ;
    @FXML
    private TextField  printstuff_I_R_G  ;

    @FXML
    private TextField  printstuff_A_S    ;
    @FXML
    private TextField  printstuff_BRUT    ;
    @FXML
    private TextField  printstuff_ALLOCATION_FAMILIAL  ;
    @FXML
    private TextField  printstuff_LOGEMENT   ;
    @FXML
    private TextField  printstuff_CAl_SCIENTIFIQUE     ;
    @FXML
    private TextField  printstuff_ENCADREMENT    ;
    @FXML
    private TextField  printstuff_DOCUMENTATION   ;
    @FXML
    private TextField  printstuff_RESPONSABILITE    ;
    @FXML
    private TextField  printstuff_IND_Q_POSTE   ;
    @FXML
    private TextField  printstuff_I_E_P    ;
    @FXML
    private TextField  printstuff_SALAIRE_DE_BASE    ;
    @FXML
    private TextField  printstuff_kids_number    ;
    @FXML
    private CheckBox  printstuff_wife_work    ;
    @FXML
    private TextField  printstuff_wife_bonus    ;
    @FXML
    private TextField  printstuff_wife_full_name    ;
    @FXML
    private TextField  printstuff_S_FAM    ;
    @FXML
    private TextField  printstuff_ccp    ;
    @FXML
    private TextField  printstuff_kids_bonus;


    private void resetPageComponents() {
        printstuff_full_name.clear();
        printstuff_grade.clear();
        printstuff_num_echlent.clear();
        printstuff_job.clear();
        printstuff_married.setSelected(false);
        printstuff_final_salary.clear();
        monthComboBox.getSelectionModel().clearSelection();
        monthComboBox.getItems().clear();


        printstuff_wife_bonus.clear();
        printstuff_kids_number.clear();
        printstuff_wife_work.setSelected(false);
        printstuff_ccp.clear();
        printstuff_S_FAM.clear();
        printstuff_SALAIRE_DE_BASE.clear();
        printstuff_I_E_P.clear();
        printstuff_IND_Q_POSTE.clear();
        printstuff_BRUT.clear();
        printstuff_kids_bonus.clear();
        printstuff_RESPONSABILITE.clear();
        printstuff_DOCUMENTATION.clear();
        printstuff_ENCADREMENT.clear();
        printstuff_CAl_SCIENTIFIQUE.clear();
        printstuff_LOGEMENT.clear();
        printstuff_ALLOCATION_FAMILIAL.clear();
        printstuff_A_S.clear();
        printstuff_I_R_G.clear();
        printstuff_RETENUE_LOGEMENT.clear();
        printstuff_wife_full_name.clear();
    }



    boolean document =false;





    public static int getCurrentDateCode() {

        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMM");
        String formattedDate = now.format(formatter);
        return Integer.parseInt(formattedDate);
    }
    boolean past;
    @FXML
    public void generateFich() {
        int year=0;
        int month=0;
        if(!past){int a=    getCurrentDateCode();
            month =  getMonthFromYearMonth(a);
            year=  getYearFromYearMonth(a);
        }
        else {past=false;
            String selectedMonthYear = monthComboBox.getValue();
            if (selectedMonthYear != null) {

                int result = generateResult(selectedMonthYear);
                month=getMonthFromYearMonth(result);
                year=getYearFromYearMonth(result);}}

setTeachers_page();
        resetPageComponents();
        if(document==false)
        {
            showAlert("pdf null error");
        }
        try {
            SAVE( month ,year);
            document=false;


        } catch (Exception e) {
            e.printStackTrace();
        }
    }



















    static String idx =null;
    @FXML
    private String getSelectedEnseignantId() {
        Enseignant selectedEnseignant = enseignantTable.getSelectionModel().getSelectedItem();
        if (selectedEnseignant != null) {
            return selectedEnseignant.getIdEnseignant();
        } else {
            return null;
        }
    }


    @FXML
    private void save_missed_hours(){

        String id= getSelectedEnseignantId();
        if (id != null) {
            if(isinCongeDeMaterniteIntoday(id)){ showAlert("seriously i told you you cant add extra absence when .. you know ");return;}
            int a=getNombre_of_absence(id,0,0);
            if(getnbtextfld(missed_hours_add)==a) { showAlert("you didnt change the absence value ");return;}
            else  if (getnbtextfld(missed_hours_add) >= 30) {
                missed_hours_add.setText(String.valueOf(getNombre_of_absence(id,0,0)));
                showAlert("the absence value cant exceed 30 (there are only 30 day of work ");return;
            }
            createOrUpdateMonthlyAbsentRecord(id,getnbtextfld(missed_hours_add));

            missed_hours_add.setText(String.valueOf(getNombre_of_absence(id,0,0)));
        }
        else {
            showAlert("you didnt select any Enseignant from the table");
        }

    }
    @FXML
    private void add_missed_hours() {

        String id= getSelectedEnseignantId();
        if (id != null) {
            if (isinCongeDeMaterniteIntoday(id)){showAlert("this Enseignant in 'Conge De Maternite' NOW you cant add now");
                missed_hours_add.setText(String.valueOf(getNombre_of_absence(id,0,0))+getDaysOfCongeDeMaterniteInWorkPeriods(id,0,0));
                return;
            }

            missed_hours_add.setText(String.valueOf(getNombre_of_absence(id,0,0)));

        } else {
            showAlert("you didnt select any Enseignant from the table");
        }
    }


    @FXML
    private void UPDATE_EnseignantId() {
        String id= getSelectedEnseignantId();
        if (id != null) {
            id_update=id;
            start_update_mode();
            try {
                fillPageFromEnseignant(id);

            } catch (SQLException e) {
                showAlert("there is a error with this specific Enseignant");
                end_update_mode(); showAlert("we will delete this Enseignant for the betterment of future proccess");
            }

            setAdd_modify_page();

        } else {
            showAlert("you didnt select any Enseignant from the table");end_update_mode();
        }
    }
    @FXML
    private void DELET_EnseignantId() {
        String id= getSelectedEnseignantId();
        if (id != null) {
            try {
                set_effective_end_date_Enseignant(id);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            showAlert("you didnt select any Enseignant from the table");
        }
    }




    @FXML
    private void onSearchButtonClick() throws SQLException {
        String searchTerm = teachersLookUpPageSearchWithThis.getText();
        String searchOption = teachersLookUpPageSearchOptionChoiceBox.getValue();
        String department = teachersLookUpPageSearchDepartment.getValue();
        String grade = teachersLookUpPageSearchGrade.getValue();
        String echent = teachersLookUpPageSearchEchlont.getValue();
        String housingType = teachersLookUpPageSearchHousingType.getValue();
        boolean married = teachersLookUpPageSearchMarried.isSelected();
        boolean hasWife = teachersLookUpPageSearchHasWife.isSelected();
        boolean hasKids = teachersLookUpPageSearchHasKids.isSelected();
        boolean inactiveid = inactive.isSelected();
        boolean activeid = active.isSelected();




        ArrayList<String> activeEnseignantIds = null;

        if (activeid) {
            activeEnseignantIds = getAllActiveEnseignantIds();

            if (activeEnseignantIds == null || activeEnseignantIds.isEmpty()) {
                showAlert("There are no active Enseignants yet");

            }
        } else if (inactiveid) {
            activeEnseignantIds = getAllDisactiveEnseignantIds();

            if (activeEnseignantIds == null || activeEnseignantIds.isEmpty()) {
                showAlert("There are no inactive Enseignants yet");

            }
        } else {
            activeEnseignantIds = getAllEnseignantIds();

            if (activeEnseignantIds == null || activeEnseignantIds.isEmpty()) {
                showAlert("There are no Enseignants at all in the database");

            }
        }


        // List to store all Enseignants
        ArrayList<Enseignant> allEnseignants = new ArrayList<>();

        // Retrieve Enseignant objects for each ID
        for (String id : activeEnseignantIds) {
            try {
                Enseignant enseignant = load_curent_Enseignant(id);
                if (enseignant != null) {
                    enseignant.printDetails();
                    allEnseignants.add(enseignant);
                }
            } catch (SQLException e) {
                // Handle individual SQL exceptions
                e.printStackTrace();
            }
        }


        ArrayList<Enseignant> filteredEnseignants = new FilteredEnseignant().filterEnseignants(
                allEnseignants, searchTerm, searchOption, department, grade, echent, housingType, married, hasWife, hasKids);
        fillTableViewWithEnseignants(filteredEnseignants);
    }

    public void fillTableViewWithEnseignants(ArrayList<Enseignant> enseignants) {
        // Center the text in each column

        for (Enseignant a : enseignants) {System.out.println(a.dateb);}
        enseignantTable.getItems().clear();
        enseignantTable.getItems().addAll(enseignants);
        centerTableColumns();
    }


    private void centerTableColumns() {
        for (TableColumn<Enseignant, ?> column : enseignantTable.getColumns()) {
            centerColumnText(column);
        }
    }

    private <T> void centerColumnText(TableColumn<Enseignant, T> column) {
        column.setCellFactory(tc -> {
            TableCell<Enseignant, T> cell = new TableCell<Enseignant, T>() {
                @Override
                protected void updateItem(T item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                    } else {
                        setText(item == null ? "" : item.toString());
                        setAlignment(Pos.CENTER); // Center align the text
                    }
                }
            };
            return cell;
        });
    }




    // Inner class for filtering
    class FilteredEnseignant {
        public ArrayList<Enseignant> filterEnseignants(ArrayList<Enseignant> allEnseignants, String searchTerm, String searchOption,
                                                       String department, String grade, String echelon, String housingType,
                                                       boolean married, boolean hasWife, boolean hasKids) {
            ArrayList<Enseignant> filteredEnseignants = new ArrayList<>(allEnseignants);

            if (searchOption != null && !searchOption.isEmpty() && searchTerm != null && !searchTerm.isEmpty()) {
                filteredEnseignants = filterBySearchOption(filteredEnseignants, searchTerm, searchOption);
            }
            if (department != null && !department.equals("all")) {
                filteredEnseignants = filterByDepartment(filteredEnseignants, department);
            }
            if (grade != null && !grade.equals("all")) {
                filteredEnseignants = filterByGrade(filteredEnseignants, grade);
            }
            if (echelon != null && !echelon.equals("all")) {
                filteredEnseignants = filterByEchelon(filteredEnseignants, echelon);
            }
            if (housingType != null && !housingType.equals("all")) {
                filteredEnseignants = filterByHousingType(filteredEnseignants, housingType);
            }
            if (married) {
                filteredEnseignants = filterByMaritalStatus(filteredEnseignants, married);
            }
            if (hasWife) {
                filteredEnseignants = filterByHasWife(filteredEnseignants);
            }
            if (hasKids) {
                filteredEnseignants = filterByHasKids(filteredEnseignants);
            }

            return filteredEnseignants;
        }

        private ArrayList<Enseignant> filterBySearchOption(ArrayList<Enseignant> enseignants, String searchTerm, String searchOption) {
            return enseignants.stream()
                    .filter(enseignant -> {
                        switch (searchOption) {
                            case "name":
                                return enseignant.getPrenom().toLowerCase().contains(searchTerm.toLowerCase());
                            case "last name":
                                return enseignant.getNom().toLowerCase().contains(searchTerm.toLowerCase());
                            case "date of birth":
                                return enseignant.getDateNaissance().toString().equals(searchTerm);
                            case "sex":
                                return enseignant.getSex().equalsIgnoreCase(searchTerm);
                            case "address":
                                return enseignant.getAdresse().toLowerCase().contains(searchTerm.toLowerCase());
                            case "email":
                                return enseignant.getAdresseEmail().toLowerCase().contains(searchTerm.toLowerCase());
                            default:
                                return false;
                        }
                    })
                    .collect(Collectors.toCollection(ArrayList::new));
        }

        private ArrayList<Enseignant> filterByDepartment(ArrayList<Enseignant> enseignants, String department) {
            return enseignants.stream()
                    .filter(enseignant -> enseignant.getDepartment().equalsIgnoreCase(department))
                    .collect(Collectors.toCollection(ArrayList::new));
        }

        private ArrayList<Enseignant> filterByGrade(ArrayList<Enseignant> enseignants, String grade) {
            return enseignants.stream()
                    .filter(enseignant -> enseignant.grade.equalsIgnoreCase(grade))
                    .collect(Collectors.toCollection(ArrayList::new));
        }

        private ArrayList<Enseignant> filterByEchelon(ArrayList<Enseignant> enseignants, String echelon) {
            return enseignants.stream()
                    .filter(enseignant -> String.valueOf(enseignant.echlont).equalsIgnoreCase(echelon))
                    .collect(Collectors.toCollection(ArrayList::new));
        }

        private ArrayList<Enseignant> filterByHousingType(ArrayList<Enseignant> enseignants, String housingType) {
            return enseignants.stream()
                    .filter( enseignant -> enseignant.getresidencetype_sentence().equalsIgnoreCase(housingType))



                    .collect(Collectors.toCollection(ArrayList::new));
        }

        private ArrayList<Enseignant> filterByMaritalStatus(ArrayList<Enseignant> enseignants, boolean married) {
            return enseignants.stream()
                    .filter(enseignant -> enseignant.isMarried() == married)
                    .collect(Collectors.toCollection(ArrayList::new));
        }

        private ArrayList<Enseignant> filterByHasWife(ArrayList<Enseignant> enseignants) {
            return enseignants.stream()
                    .filter(enseignant -> enseignant.partner != null)
                    .collect(Collectors.toCollection(ArrayList::new));
        }

        private ArrayList<Enseignant> filterByHasKids(ArrayList<Enseignant> enseignants) {
            return enseignants.stream()
                    .filter(enseignant -> !enseignant.getKidsList().isEmpty())
                    .collect(Collectors.toCollection(ArrayList::new));
        }

    }
































































































//////////////////////////////////////////////////////////////













































    @FXML
    private ChoiceBox<Integer> Update_add_page_echlent_choice_box;

    @FXML
    private ChoiceBox<String> Update_add_page_department_choice_box;

    @FXML
    private ChoiceBox<String> Update_add_page_grade_choice_box;


    @FXML
    private Button Update_add_page_teacher_add_female_kid_tab;

    @FXML
    private Button Update_add_page_teacher_add_male_kid_tab;

    @FXML
    private Button Update_add_page_teacher_add_wife_tab;

    @FXML
    private TextField Update_add_page_teacher_address;



    @FXML
    private DatePicker Update_add_page_teacher_birthday;

    @FXML
    private TextField Update_add_page_teacher_bank_account_details;

    @FXML
    private AnchorPane Update_add_page_teacher_familly_info;

    @FXML
    private TabPane Update_add_page_teacher_familly_tab;

    @FXML
    private TextField Update_add_page_teacher_female_kids_number;

    @FXML
    private TextField Update_add_page_teacher_full_name;

    @FXML
    private Button Update_add_page_teacher_generate_add_familly_tabs;

    @FXML
    private TextField Update_add_page_teacher_gmail;

    @FXML
    private TextField Update_add_page_teacher_bac_year;

    @FXML
    private CheckBox Update_add_page_teacher_has_univ_housing;

    @FXML
    private TextField Update_add_page_teacher_last_name;

    @FXML
    private TextField Update_add_page_teacher_male_kids_numbers;

    @FXML
    private CheckBox Update_add_page_teacher_married;

    @FXML
    private TextField Update_add_page_teacher_phone_num;

    @FXML
    private CheckBox Update_add_page_teacher_sex_female;

    @FXML
    private CheckBox Update_add_page_teacher_sex_male;

    @FXML
    private TextField Update_add_page_teacher_wife_numbers;

    @FXML
    private Button add_modify_page;

    @FXML
    private AnchorPane addmodifydelet;

    @FXML
    private AnchorPane settings;
    @FXML
    private Button edit;

    @FXML
    private AnchorPane general;

    @FXML
    private Button general_details_page;

    @FXML
    private AnchorPane navbar;

    @FXML
    private AnchorPane page1;

    @FXML
    private AnchorPane printstuff;

    @FXML
    private Button printstufft;

    @FXML
    private Button student1;

    @FXML
    private Button teachers_page;

    @FXML
    private Button teachers_page_add;

    @FXML
    private Button teachers_page_delete;

    @FXML
    private AnchorPane teachers_look_up_page;



    @FXML
    private Button teachers_page_update;

    @FXML
    private Button teachers_page_update_add_missed_hours;

    @FXML
    private Button update_add_teacher;

    @FXML
    private Label user;


    @FXML
    private ChoiceBox<String> Update_add_page_extra_job_choice_box;

    @FXML
    private ChoiceBox<String> Update_add_page_faculty_choice_box;

    @FXML
    private ChoiceBox<String> Update_add_page_teacher_bank_account_type;



    @FXML
    private CheckBox Update_add_page_teacher_has_his_own_house;

    @FXML
    private CheckBox Update_add_page_teacher_has_no_house;































    //teachers_page_pageprintstuffgeneraladdmodifydelet
//filters


    @FXML
    private void addNumericInputFilter0or1(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[01]?")) {
                textField.setText(oldValue);
            }
        });
    }


    @FXML
    private AnchorPane max; // Ensure this matches the fx:id in your FXML file



    private boolean isValidBinary(String input) {
        return input.matches("[01]*");
    }
    @FXML
    private void addLetterInputFilter(TextField textField) {
        textField.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            char inputChar = event.getCharacter().charAt(0);
            if (!Character.isLetter(inputChar)) {
                event.consume();
            }
        });
    }
    @FXML
    private void addNumericInputFilter(TextField textField) {
        textField.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            char inputChar = event.getCharacter().charAt(0);
            if (!Character.isDigit(inputChar)) {
                event.consume();}});}


// load cal details




    private void addOptions(ChoiceBox<String> choiceBox, List<String> choiceList) {
        ObservableList<String> observableChoiceList = FXCollections.observableArrayList(choiceList);
        choiceBox.setItems(observableChoiceList);
    }


    //



    @FXML
    private void extractPDFText(ActionEvent event) {

        Stage stage = (Stage) ((javafx.scene.control.Button) event.getSource()).getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select PDF File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        File pdfFile = fileChooser.showOpenDialog(stage);

        if (pdfFile != null) {
            try {
                List<String> numberLines = new ArrayList<>();
                try (PDDocument document = PDDocument.load(pdfFile)) {
                    PDFTextStripper pdfStripper = new PDFTextStripper();
                    for (int i = 1; i <= document.getNumberOfPages(); i++) {
                        pdfStripper.setStartPage(i);
                        pdfStripper.setEndPage(i);
                        String text = pdfStripper.getText(document);
                        List<String> lines = extractNumberLinesFromText(text);
                        numberLines.addAll(lines);
                    }
                }
                try (FileWriter writer = new FileWriter(irgfile)) {

                    for (String line : numberLines) {
                        writer.write(line + System.lineSeparator());
                    }
                }

                // Call the method to filter the file in place
                filterFileInPlace(irgfile);
                addIRGFile();
                showAlert(Alert.AlertType.INFORMATION, "Information", null,
                        "Lines containing numbers extracted and saved to file: " + irgfile);

            } catch (IOException e) {
                showErrorDialog("Error reading PDF file or writing to output file: " + e.getMessage());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.out.println("File selection canceled.");
        }
    }

    private void filterFileInPlace(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            // Read all lines from the file into memory
            List<String> lines = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }

            // Close the file
            reader.close();

            // Open the file for writing
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                // Iterate through the lines, modify them, and write them back to the file
                for (String originalLine : lines) {
                    // Split the line by spaces
                    String[] values = originalLine.split("\\s+");
                    // Extract the first two columns from the line
                    String firstTwoColumns = values[0] + " " + values[1];
                    // Extract the first number from the line
                    double firstNumber = Double.parseDouble(values[0].replace(",", "."));
                    // Check if the first number is between 60000 and 250000
                    if (60000 <= firstNumber && firstNumber <= 300000) {
                        // Write the first two columns to the output file
                        writer.write(firstTwoColumns);
                        writer.newLine();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<String> extractNumberLinesFromText(String text) {
        List<String> numberLines = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\b\\d{5},\\d{2}\\b");
        for (String line : text.split("\\r?\\n")) {
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                numberLines.add(line);
            }
        }
        return numberLines;
    }

    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }




    private void centerTextInColumn(TableColumn<Enseignant, String> column) {
        column.setCellFactory(new Callback<TableColumn<Enseignant, String>, TableCell<Enseignant, String>>() {
            @Override
            public TableCell<Enseignant, String> call(TableColumn<Enseignant, String> param) {
                TableCell<Enseignant, String> cell = new TableCell<Enseignant, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(item);
                            setStyle("-fx-alignment: CENTER;");
                        }
                    }
                };
                return cell;
            }
        });
    }










    Map<String, String> monthPeriods = new HashMap<>();
    Map<String, String> monthNumbers = new HashMap<>();

    private LocalDate startDate ;  private LocalDate currentDate;

    public static String getCurrentMonth() {
        Map<String, MonthDay[]> monthPeriods = new HashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM");

        monthPeriods.put("January", new MonthDay[]{MonthDay.parse("20 November", formatter), MonthDay.parse("20 December", formatter)});
        monthPeriods.put("February", new MonthDay[]{MonthDay.parse("20 December", formatter), MonthDay.parse("20 January", formatter)});
        monthPeriods.put("March", new MonthDay[]{MonthDay.parse("20 January", formatter), MonthDay.parse("20 February", formatter)});
        monthPeriods.put("April", new MonthDay[]{MonthDay.parse("20 February", formatter), MonthDay.parse("20 March", formatter)});
        monthPeriods.put("May", new MonthDay[]{MonthDay.parse("20 March", formatter), MonthDay.parse("20 April", formatter)});
        monthPeriods.put("June", new MonthDay[]{MonthDay.parse("20 April", formatter), MonthDay.parse("20 May", formatter)});
        monthPeriods.put("July", new MonthDay[]{MonthDay.parse("20 May", formatter), MonthDay.parse("20 June", formatter)});
        monthPeriods.put("August", new MonthDay[]{MonthDay.parse("20 June", formatter), MonthDay.parse("20 July", formatter)});
        monthPeriods.put("September", new MonthDay[]{MonthDay.parse("20 July", formatter), MonthDay.parse("20 August", formatter)});
        monthPeriods.put("October", new MonthDay[]{MonthDay.parse("20 August", formatter), MonthDay.parse("20 September", formatter)});
        monthPeriods.put("November", new MonthDay[]{MonthDay.parse("20 September", formatter), MonthDay.parse("20 October", formatter)});
        monthPeriods.put("December", new MonthDay[]{MonthDay.parse("20 October", formatter), MonthDay.parse("20 November", formatter)});

        LocalDate currentDate = LocalDate.now();
        MonthDay currentMonthDay = MonthDay.from(currentDate);

        for (Map.Entry<String, MonthDay[]> entry : monthPeriods.entrySet()) {
            MonthDay start = entry.getValue()[0];
            MonthDay end = entry.getValue()[1];

            if ((currentMonthDay.isAfter(start) || currentMonthDay.equals(start)) && currentMonthDay.isBefore(end)) {
                return entry.getKey();
            }
        }

        // Edge case handling for end of December period
        MonthDay decemberStart = monthPeriods.get("December")[0];
        MonthDay januaryEnd = monthPeriods.get("February")[1];
        if ((currentMonthDay.isAfter(decemberStart) || currentMonthDay.equals(decemberStart)) || currentMonthDay.isBefore(januaryEnd)) {
            return "December";
        }

        return "Unknown";
    }

    @FXML
    private void handleMonthSelection() {
        String selectedMonthYear = monthComboBox.getValue();

        if (selectedMonthYear != null&& selectedMonthYear.equals("current") ) {

            String month = getCurrentMonth();

            String period = monthPeriods.get(month);
            periodLabel.setText("(period still ongoing so any changes will effect it) from:" + period);


        }
       else if (selectedMonthYear != null) {
            // Split the selected value into month and year
            String[] parts = selectedMonthYear.split(" ");
            String month = parts[0];

            String period = monthPeriods.get(month);
            periodLabel.setText("You selected the period from " + period);

            int result = generateResult(selectedMonthYear);
            System.out.println("Generated result: " + result);
        }
    }

    private int generateResult(String monthYear) {
        String[] parts = monthYear.split(" ");
        String currentYear = parts[1];
        String monthNumber = monthNumbers.get(parts[0]);

        // Ensure the result is always in the form of six digits (e.g., 202005)
        return Integer.parseInt(currentYear + String.format("%02d", Integer.parseInt(monthNumber)));
    }



    private void initializeMonthPeriods() {
        monthPeriods.put("January", "20 November to 20 December");
        monthPeriods.put("February", "20 December to 20 January");
        monthPeriods.put("March", "20 January to 20 February");
        monthPeriods.put("April", "20 February to 20 March");
        monthPeriods.put("May", "20 March to 20 April");
        monthPeriods.put("June", "20 April to 20 May");
        monthPeriods.put("July", "20 May to 20 June");
        monthPeriods.put("August", "20 June to 20 July");
        monthPeriods.put("September", "20 July to 20 August");
        monthPeriods.put("October", "20 August to 20 September");
        monthPeriods.put("November", "20 September to 20 October");
        monthPeriods.put("December", "20 October to 20 November");
    }

    private void initializeMonthNumbers() {
        monthNumbers.put("January", "01");
        monthNumbers.put("February", "02");
        monthNumbers.put("March", "03");
        monthNumbers.put("April", "04");
        monthNumbers.put("May", "05");
        monthNumbers.put("June", "06");
        monthNumbers.put("July", "07");
        monthNumbers.put("August", "08");
        monthNumbers.put("September", "09");
        monthNumbers.put("October", "10");
        monthNumbers.put("November", "11");
        monthNumbers.put("December", "12");
    }

    private void populateComboBox() {
        LocalDate date = startDate;

        // Check if startDate is null and handle accordingly
        if (date == null) {
            System.out.println("startDate is null, assigning default value.");
            date = LocalDate.now(); // Assign a default value, e.g., current date
        }

        monthComboBox.getItems().add("current");

        // Check if currentDate is null to avoid potential NullPointerException
        if (currentDate == null) {
            System.out.println("currentDate is null, assigning default value.");
            currentDate = LocalDate.now(); // Assign a default value, e.g., current date
        }

        while (!date.isAfter(currentDate)) {
            String monthYear = date.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " " + date.getYear();
            monthComboBox.getItems().add(monthYear);
            date = date.plusMonths(1);
        }

        monthComboBox.setValue("current");
    }


    @FXML
    CheckBox Update_add_page_teacher_use_husband_bank;
    @FXML
    TextField Update_add_page_teacher_husband_bank_account_details;
    @FXML
    Label maxxxx;


    void woman_bank_details_husband( boolean a){
        Update_add_page_teacher_husband_bank_account_details.setDisable(!a);
        Update_add_page_teacher_use_husband_bank.setDisable(!a);
        maxxxx.setDisable(!a);
        Update_add_page_teacher_husband_bank_account_details.setVisible(a);
        Update_add_page_teacher_use_husband_bank.setVisible(a);
        maxxxx.setVisible(a);

    }


    public  String mix(String a, String b, boolean x) {

        if (b.equals("0")) {
            System.out.println("The second string is null.");
            return a + "©" + x;
        }
        return a + "©" + b + "©" + x;
    }

    public  String[] split(String a) {
        String[] parts = a.split("©");
        if (parts.length < 3 || parts[1].equals("0")) {
            System.out.println("The second part or the state is null.");
            Update_add_page_teacher_use_husband_bank.setSelected( Boolean.parseBoolean(parts[parts.length - 1]));
            return new String[] { parts[0], null };
        }
        Update_add_page_teacher_use_husband_bank.setSelected( Boolean.parseBoolean(parts[2]));
        return new String[] { parts[0], parts[1] };
    }





    @FXML
    public void initialize() {
        CustomCloseButtons(max);
        CustomMinButton(max);

        if (current_username!=null )
            username.setText(current_username);
        else username.setText("mr nobody");

        past=false;



        try {checkAndUpdateActiveRecords();
            getIRGFile(null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        addOptions(Update_add_page_teacher_bank_account_type, getTypeComptIds());
        addOptions(Update_add_page_extra_job_choice_box, getavailableExtraPostsnames());addOptions(Update_add_page_faculty_choice_box, getFacultes());
        addOptions(Update_add_page_department_choice_box, getDepartements());
        addOptions(Update_add_page_faculty_choice_box, getFacultes());
        Update_add_page_grade_choice_box.getItems().addAll("CA", "CB", "PR", "BA");

        Update_add_page_extra_job_choice_box.getItems().addAll("non");
        Update_add_page_extra_job_choice_box.setValue("non");
        //////////////////////////

        // Initialize choice boxes
        teachersLookUpPageSearchOptionChoiceBox.getItems().addAll(
                "name", "last name", "date of birth", "sex", "address", "email"
        );
        teachersLookUpPageSearchOptionChoiceBox.setValue("name"); // Set default value

        teachersLookUpPageSearchDepartment.getItems().addAll(getDepartements());
        teachersLookUpPageSearchDepartment.getItems().add(0, "all"); // Add "all" option
        teachersLookUpPageSearchDepartment.setValue("all"); // Set default value

        teachersLookUpPageSearchGrade.getItems().addAll("all", "AB", "AA", "CB","CA","PR");
        teachersLookUpPageSearchGrade.setValue("all"); // Set default value

        teachersLookUpPageSearchEchlont.getItems().addAll("all", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12");
        teachersLookUpPageSearchEchlont.setValue("all"); // Set default value

        teachersLookUpPageSearchHousingType.getItems().addAll("all", "has no house", "has his own house", "has a univ_house");
        teachersLookUpPageSearchHousingType.setValue("all"); // Set default value

        ///////////////////////////


        try {
            reset_Update_add_page_teacher_generate_add_familly_tabs();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Initialize the table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("idEnseignant"));
        fullNameColumn.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        birthdayColumn.setCellValueFactory(cellData -> {
            Enseignant enseignant = cellData.getValue();
            return new SimpleStringProperty(enseignant.getFormattedDateNaissance());
        });
        gradeColumn.setCellValueFactory(cellData -> {
            Enseignant enseignant = cellData.getValue();
            return new SimpleStringProperty(enseignant.grade);
        });
        echlontColumn.setCellValueFactory(new PropertyValueFactory<>("echlont"));
        extraPostColumn.setCellValueFactory(new PropertyValueFactory<>("extra_job"));
        gmailColumn.setCellValueFactory(new PropertyValueFactory<>("adresseEmail"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("adresse"));
        contactNumColumn.setCellValueFactory(new PropertyValueFactory<>("tel"));





        addNumericInputFilter(Update_add_page_teacher_phone_num);
        addNumericInputFilter(Update_add_page_teacher_female_kids_number);
        addNumericInputFilter( Update_add_page_teacher_male_kids_numbers);
        addNumericInputFilter0or1(Update_add_page_teacher_wife_numbers);
        addLetterInputFilter(Update_add_page_teacher_full_name);
        addLetterInputFilter(Update_add_page_teacher_last_name);
        Update_add_page_teacher_familly_info.setDisable(false);

//


        inactive.selectedProperty().addListener((observable, oldValue, Selected) -> {

            boolean bothSelected = Selected && active.isSelected();

            addmodifydelet.setDisable(Selected);

            if (bothSelected) {
                active.setSelected(false);

            }
            if(!Selected && !active.isSelected()) active.setSelected(true);

        });

        active.selectedProperty().addListener((observable, oldValue, Selected) -> {
            boolean bothSelected = Selected && active.isSelected();
            if (bothSelected) {
                inactive.setSelected(false);

            }
            if(!Selected && !inactive.isSelected()) inactive.setSelected(true);
        });
        active.setSelected(true);
        teachersLookUpPageSearchMarried.selectedProperty().addListener((observable, oldValue, Selected) -> {
            boolean bothSelected = !Selected && (teachersLookUpPageSearchHasWife.isSelected()||teachersLookUpPageSearchHasKids.isSelected());
            if (bothSelected) {
                teachersLookUpPageSearchHasWife.setSelected(false);
                teachersLookUpPageSearchHasKids.setSelected(false);

            }
        });

        teachersLookUpPageSearchHasKids.selectedProperty().addListener((observable, oldValue, Selected) -> {
            boolean bothSelected = !Selected && !(teachersLookUpPageSearchMarried.isSelected());
            if (bothSelected) {
                teachersLookUpPageSearchMarried.setSelected(true);

            }
        });
        teachersLookUpPageSearchHasWife.selectedProperty().addListener((observable, oldValue, Selected) -> {
            boolean bothSelected = Selected && !(teachersLookUpPageSearchMarried.isSelected());
            if (bothSelected) {
                teachersLookUpPageSearchMarried.setSelected(true);

            }
        });










        Update_add_page_teacher_married.selectedProperty().addListener((observable, oldValue, marriedSelected) -> {


            boolean bothSelected = (marriedSelected && Update_add_page_teacher_sex_male.isSelected())||(marriedSelected && Update_add_page_teacher_sex_female.isSelected());

            boolean bothSelectedfemale = marriedSelected && Update_add_page_teacher_sex_female.isSelected();


            Update_add_page_teacher_familly_info.setDisable(!bothSelected);

            if (bothSelectedfemale){
                Update_add_page_teacher_add_wife_tab.setDisable(bothSelectedfemale);

                Update_add_page_teacher_wife_numbers.setDisable(bothSelectedfemale);}
            woman_bank_details_husband(bothSelectedfemale);


            if (bothSelected)  try {
                reset_Update_add_page_teacher_generate_add_familly_tabs();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        Update_add_page_teacher_sex_male.selectedProperty().addListener((observable, oldValue, sexMaleSelected) -> {
            boolean bothSelected = sexMaleSelected && Update_add_page_teacher_married.isSelected();
            woman_bank_details_husband(false);
            Update_add_page_teacher_familly_info.setDisable(!bothSelected);if (bothSelected)  try {
                reset_Update_add_page_teacher_generate_add_familly_tabs();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });


        Update_add_page_teacher_sex_female.selectedProperty().addListener((observable, oldValue, sexFemaleSelected) -> {
            boolean bothSelected = sexFemaleSelected && Update_add_page_teacher_married.isSelected();
            Update_add_page_teacher_familly_info.setDisable(!bothSelected);
            Update_add_page_teacher_add_wife_tab.setDisable(bothSelected);
            Update_add_page_teacher_wife_numbers.setDisable(bothSelected);
            woman_bank_details_husband(bothSelected);
            if (bothSelected)  try {
                reset_Update_add_page_teacher_generate_add_familly_tabs();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });







//
        ObservableList<Integer> choices1 = FXCollections.observableArrayList();
        for (int i = 0; i <= 12; i++) {
            choices1.add(i);
        }// Set the choices to the ChoiceBox
        Update_add_page_echlent_choice_box.setItems(choices1);
//
        ObservableList<String> choices2 = FXCollections.observableArrayList("AB", "AA", "CB","CA","PR");
        Update_add_page_grade_choice_box.setItems(choices2);
//
        Update_add_page_teacher_sex_female.setOnAction(event -> {
            if (Update_add_page_teacher_sex_female.isSelected()) {
                Update_add_page_teacher_sex_male.setSelected(false);

            }
        });

        // Add listener to checkBox2
        Update_add_page_teacher_sex_male.setOnAction(event -> {
            if (Update_add_page_teacher_sex_male.isSelected()) {
                Update_add_page_teacher_sex_female.setSelected(false);
            }
        });


        Update_add_page_teacher_has_univ_housing.setOnAction(event -> {
            if (Update_add_page_teacher_has_univ_housing.isSelected()) {
                Update_add_page_teacher_has_his_own_house.setSelected(false);
                Update_add_page_teacher_has_no_house.setSelected(false);
            }
        });

        Update_add_page_teacher_has_his_own_house.setOnAction(event -> {
            if (Update_add_page_teacher_has_his_own_house.isSelected()) {
                Update_add_page_teacher_has_univ_housing.setSelected(false);
                Update_add_page_teacher_has_no_house.setSelected(false);
            }
        });

        Update_add_page_teacher_has_no_house.setOnAction(event -> {
            if (Update_add_page_teacher_has_no_house.isSelected()) {
                Update_add_page_teacher_has_univ_housing.setSelected(false);
                Update_add_page_teacher_has_his_own_house.setSelected(false);
            }
        });

    }


    //.....................................................
    @FXML
    public void setTeachers_page() {
        teachers_look_up_page.setVisible(true);
        printstuff.setVisible(false);
        general.setVisible(false);
        if( addmodifydelet.isVisible())
            Update_add_page_teacher_RESET();
        addmodifydelet.setVisible(false);
        settings.setVisible(false);

        updating_or_inserting=false;
        end_update_mode();
    }

    @FXML
    public void setPrintstuff_page() {
        teachers_look_up_page.setVisible(false);
        printstuff.setVisible(true);
        general.setVisible(false);
        if( addmodifydelet.isVisible())
            Update_add_page_teacher_RESET();
        addmodifydelet.setVisible(false);
        settings.setVisible(false);
        Update_add_page_teacher_RESET();
        updating_or_inserting=false;
        end_update_mode();
    }

    @FXML
    public void setAdd_modify_page() {

        teachers_look_up_page.setVisible(false);
        printstuff.setVisible(false);
        general.setVisible(false);
        settings.setVisible(false);
        if(!addmodifydelet.isVisible())
            addmodifydelet.setVisible(true);

    }
    @FXML
    public void set_settings() {
        teachers_look_up_page.setVisible(false);
        printstuff.setVisible(false);
        general.setVisible(false);
        if( addmodifydelet.isVisible())
            Update_add_page_teacher_RESET();
        addmodifydelet.setVisible(false);
        settings.setVisible(true);
        updating_or_inserting=false;
        end_update_mode();
        resetPageComponents();
    }

    @FXML
    public void setgeneral_page() {
        teachers_look_up_page.setVisible(false);
        printstuff.setVisible(false);
        general.setVisible(true);
        if( addmodifydelet.isVisible())
            Update_add_page_teacher_RESET();
        addmodifydelet.setVisible(false);
        settings.setVisible(false);
        updating_or_inserting=false;
        end_update_mode();
    }




    //................................................................
// married check box tab pane is related


    //
    private int increaseNumberAndGet( TextField numberField ) {
        String input = numberField.getText();
        int a=0;

        if (input.isEmpty()){  numberField.setText(Integer.toString(1)); return 1;}
        a = Integer.parseInt(input) + 1;

        numberField.setText(Integer.toString(a));
        return a; // Return the increased number
    }

    private int getnbtextfld( TextField numberField ) {
        String input = numberField.getText();
        System.out.println(input);
        int number = 0;
        if (input.isEmpty()){  numberField.setText(Integer.toString(number)); return 0;}
        number = Integer.parseInt(input) ;
        return number;
    }


    @FXML public void  set_Update_add_page_teacher_generate_add_familly_tabs_add_male_kid_tab() throws IOException {
        int a;
        a=  increaseNumberAndGet(Update_add_page_teacher_male_kids_numbers);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("kid_male_data.fxml"));
        Parent root = loader.load();
        Tab tab = new Tab("son"+a);
        tab.setContent(root);
        Update_add_page_teacher_familly_tab.getTabs().addAll(tab);

    }
    @FXML public void  set_Update_add_page_teacher_generate_add_familly_tabs_add_female_kid_tab() throws IOException {
        int a;
        a=  increaseNumberAndGet(Update_add_page_teacher_female_kids_number);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("kid_female_data.fxml"));
        Parent root = loader.load();
        Tab tab = new Tab("daughter#"+a);
        tab.setContent(root);
        Update_add_page_teacher_familly_tab.getTabs().addAll(tab);

    }


    @FXML public void  set_Update_add_page_teacher_generate_add_familly_tabs_add_wife_tab() throws IOException {
        int a;
        if (getnbtextfld(Update_add_page_teacher_wife_numbers)==1) return;
        a=  increaseNumberAndGet(Update_add_page_teacher_wife_numbers);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("wife_data_info.fxml"));
        Parent root = loader.load();
        Tab tab = new Tab("wife");
        tab.setContent(root);
        Update_add_page_teacher_familly_tab.getTabs().addAll(tab);

    }

    @FXML public void  set_Update_add_page_teacher_generate_add_familly_tabs() throws IOException {
        int m=getnbtextfld(Update_add_page_teacher_wife_numbers);
        int s=getnbtextfld(Update_add_page_teacher_male_kids_numbers);
        int d=getnbtextfld(Update_add_page_teacher_female_kids_number);
        for (int i = 0; i <m;i++) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("wife_data_info.fxml"));
            Parent root = loader.load();
            Tab tab = new Tab("wife");
            tab.setContent(root);
            Update_add_page_teacher_familly_tab.getTabs().addAll(tab);
        }
        for (int i = 0; i <s;i++) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("kid_male_data.fxml"));
            Parent root = loader.load();
            Tab tab = new Tab("son#"+i);
            tab.setContent(root);
            Update_add_page_teacher_familly_tab.getTabs().addAll(tab);
        }
        for (int i = 0; i <d;i++) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("kid_female_data.fxml"));
            Parent root = loader.load();
            Tab tab = new Tab("daughter#"+i);
            tab.setContent(root);
            Update_add_page_teacher_familly_tab.getTabs().addAll(tab);
        }}

    @FXML public void  reset_Update_add_page_teacher_generate_add_familly_tabs() throws IOException {
        Update_add_page_teacher_wife_numbers.setText(Integer.toString(0));
        Update_add_page_teacher_male_kids_numbers .setText(Integer.toString(0));
        Update_add_page_teacher_female_kids_number.setText(Integer.toString(0));
        Update_add_page_teacher_familly_tab.getTabs().clear();
    }
    void printFamilyInfo(ArrayList<Personne> family) {
        for (Personne personne : family) {
            System.out.println("Nom: " + personne.getNom());
            System.out.println("Prénom: " + personne.getPrenom());
            System.out.println("Sexe: " + personne.getSex());
            System.out.println("Date de naissance: " + personne.getDateNaissance().getTime());
            System.out.println("Age: " + personne.getAge());
            if (personne instanceof partner) {
                partner partner = (partner) personne;
                System.out.println("Travaille: " + partner.isWork());
            } else if (personne instanceof kids) {
                kids kid = (kids) personne;
                System.out.println("Condition: " + kid.isCondition());
                System.out.println("Adopté: " + kid.isAdoupted());
            }
            System.out.println(); // Empty line for separation
        }
    }
    public static void printFamilyInfo(Personne member) {
        System.out.println("Nom: " + member.getNom());
        System.out.println("Prénom: " + member.getPrenom());
        System.out.println("Sexe: " + member.getSex());
        System.out.println("Date de naissance: " + member.getDateNaissance().getTime());
        System.out.println("Age: " + member.getAge());
        if (member instanceof partner) {
            partner partner = (partner) member;
            System.out.println("Travaille: " + partner.isWork());
        } else if (member instanceof kids) {
            kids kid = (kids) member;
            System.out.println("Condition: " + kid.isCondition());
            System.out.println("Adopté: " + kid.isAdoupted());
        }}
    public ArrayList<Personne> get_info_Update_add_page_teacher_from_familly_tabs() throws IOException {
        ArrayList<Personne> family = new ArrayList<>();
        TextField textField;
        DatePicker datePicker;
        CheckBox checkBox;

        for (Tab tab : Update_add_page_teacher_familly_tab.getTabs()) {
            String s = tab.getText();
            if (s.contains("wife")) {
                AnchorPane anchorPane = (AnchorPane) tab.getContent();
                textField = (TextField) anchorPane.lookup("#name");
                String a = textField.getText();
                textField = (TextField) anchorPane.lookup("#lastname");
                String c = textField.getText();
                datePicker = (DatePicker) anchorPane.lookup("#birthday");
                checkBox = (CheckBox) anchorPane.lookup("#employed");

                if (textField != null && datePicker != null && checkBox != null) {
                    boolean work = checkBox.isSelected();
                    String sex = teacherGetSex().equals("M") ? "F" : "M";
                    partner p = new partner(a, c, sex, convertDatePickerToCalendar(datePicker), work);
                    family.add(p);  // Add partner to family list
                    printFamilyInfo(p);

                } else {
                    System.out.println("One or more fields not found in wife tab");
                }
            } else if (s.contains("daughter")) {  // Fixed typo: "dougther" to "daughter"
                AnchorPane anchorPane = (AnchorPane) tab.getContent();
                textField = (TextField) anchorPane.lookup("#name");
                String a = textField.getText();
                datePicker = (DatePicker) anchorPane.lookup("#birthday");
                checkBox = (CheckBox) anchorPane.lookup("#married");

                if (textField != null && datePicker != null && checkBox != null) {
                    boolean selected = checkBox.isSelected();
                    checkBox = (CheckBox) anchorPane.lookup("#adopted");
                    boolean adopted = checkBox.isSelected();

                    kids kid = new kids(a, Update_add_page_teacher_last_name.getText(), "F", convertDatePickerToCalendar(datePicker), selected, adopted);
                    family.add(kid);  // Add kid to family list
                    printFamilyInfo(kid);

                } else {
                    System.out.println("One or more fields not found in daughter tab");
                }
            } else if (s.contains("son")) {
                AnchorPane anchorPane = (AnchorPane) tab.getContent();
                textField = (TextField) anchorPane.lookup("#name");
                String a = textField.getText();
                datePicker = (DatePicker) anchorPane.lookup("#birthday");
                checkBox = (CheckBox) anchorPane.lookup("#studuying");

                if (textField != null && datePicker != null && checkBox != null) {
                    boolean selected = checkBox.isSelected();
                    checkBox = (CheckBox) anchorPane.lookup("#adopted");
                    boolean adopted = checkBox.isSelected();

                    kids kid = new kids(a, Update_add_page_teacher_last_name.getText(), "M", datePicker, selected, adopted);
                    family.add(kid);  // Add kid to family list
                    printFamilyInfo(kid);

                } else {
                    System.out.println("One or more fields not found in son tab");
                }
            }
        }
        return family;
    }

    @FXML
    public void set_Update_add_page_teacher_generate_add_familly_tabs_with_pre_data(partner p, ArrayList<kids> kidsList) {
        int numberOfWives = 0;
        int numberOfMaleKids = 0;
        int numberOfFemaleKids = 0;

        if (p != null) {
            // Adding partner tab
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("wife_data_info.fxml"));
                Parent root = loader.load();
                Tab tab = new Tab("wife");
                tab.setContent(root);
                Update_add_page_teacher_familly_tab.getTabs().add(tab);

                // Populate data in wife tab
                TextField name = (TextField) root.lookup("#name");
                TextField lastname = (TextField) root.lookup("#lastname");
                DatePicker birthday = (DatePicker) root.lookup("#birthday");
                CheckBox employed = (CheckBox) root.lookup("#employed");

                if (name != null) name.setText(p.getNom());
                if (lastname != null) lastname.setText(p.getPrenom());
                if (birthday != null && p.getDateNaissance() != null) {
                    Date date = p.getDateNaissance().getTime();
                    birthday.setValue(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                }
                if (employed != null) employed.setSelected(p.isWork());

                numberOfWives++;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (kidsList != null && !kidsList.isEmpty()) {
            for (kids kid : kidsList) {
                try {
                    FXMLLoader loader;
                    Parent root;
                    Tab tab;

                    if (kid.getSex().equals("M")) {
                        loader = new FXMLLoader(getClass().getResource("kid_male_data.fxml"));
                        root = loader.load();
                        tab = new Tab("son#" + numberOfMaleKids);
                        numberOfMaleKids++;
                    } else {
                        loader = new FXMLLoader(getClass().getResource("kid_female_data.fxml"));
                        root = loader.load();
                        tab = new Tab("daughter#" + numberOfFemaleKids);
                        numberOfFemaleKids++;
                    }

                    tab.setContent(root);
                    Update_add_page_teacher_familly_tab.getTabs().add(tab);

                    // Populate data in kid tab
                    TextField name = (TextField) root.lookup("#name");
                    DatePicker birthday = (DatePicker) root.lookup("#birthday");
                    CheckBox adopted  = (CheckBox) root.lookup("#studuying"); // Corrected typo "studuying" to "studying"
                    CheckBox studying = (CheckBox) root.lookup("#adopted");

                    if (name != null) name.setText(kid.getNom());
                    if (birthday != null && kid.getDateNaissance() != null) {
                        Date date = kid.getDateNaissance().getTime();
                        birthday.setValue(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                    }
                    if (studying != null) studying.setSelected(kid.isCondition());
                    if (adopted != null) adopted.setSelected(kid.isAdoupted());

                    if (kid.getSex().equals("F")) {
                        CheckBox married = (CheckBox) root.lookup("#married");
                        if (married != null) married.setSelected(kid.isCondition());
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // Set the text fields for current numbers of wives, male kids, and female kids
        Update_add_page_teacher_wife_numbers.setText(Integer.toString(numberOfWives));
        Update_add_page_teacher_male_kids_numbers.setText(Integer.toString(numberOfMaleKids));
        Update_add_page_teacher_female_kids_number.setText(Integer.toString(numberOfFemaleKids));
    }

    /////////////validate tabs
    @FXML
    public boolean validateTabs()throws IOException {

        StringBuilder message = new StringBuilder("detected issues:\n");
        boolean anyerrors=false;if (Update_add_page_teacher_familly_tab.getTabs().isEmpty()){showAlert("notice", "No Tabs to Validate", "you didnt create any tabs."); return true;}
        for (Tab tab : Update_add_page_teacher_familly_tab.getTabs()) {
            String tabName = tab.getText();
            Pane tabContent = (Pane) tab.getContent();

            if (tabName.contains("wife")) {
                if (!validateWifeTab(tabContent)) {
                    TextField nameField = (TextField) tabContent.lookup("#name");anyerrors=true;
                    TextField lastNameField = (TextField) tabContent.lookup("#lastname");
                    DatePicker birthdayPicker = (DatePicker) tabContent.lookup("#birthday");
                    String name = nameField.getText().trim();
                    String lastName = lastNameField.getText().trim();
                    message.append("+ wife information arent correct: \n");
                    if (!isValidName(name) ) {
                        message.append("-the first name is wrong\n");
                    }if ( !isValidName(lastName)) {
                        message.append("-the last name is wrong\n");
                    }
                    if (birthdayPicker.getValue()==null) {
                        message.append("-you didnt choose a birthday\n");
                    }
                    if (calculateAge(birthdayPicker) < 18) {
                        message.append("- the wife age is too young(change the birthday)\n");
                    }
                    message.append("-the wife age is too young\n\n");

                }
            } else if (tabName.contains("son") || tabName.contains("daughter")) {
                if (!validateChildTab(tabContent, Update_add_page_teacher_birthday)) {anyerrors=true;
                    TextField nameField = (TextField) tabContent.lookup("#name");
                    DatePicker birthdayPicker = (DatePicker) tabContent.lookup("#birthday");
                    CheckBox checkBox = (CheckBox) tabContent.lookup("#adopted");
                    String name = nameField.getText().trim();

                    message.append("+ "+tabName+" information arent correct: \n");
                    if (!isValidName(name)) {
                        message.append("-the first name is wrong\n");
                    }
                    if (birthdayPicker.getValue()==null) {
                        message.append("-you didnt choose a birthday\n");
                    }
                    if ((calculateAge(Update_add_page_teacher_birthday)-calculateAge(birthdayPicker)) < 18 ) {
                        message.append("-the kid age is too young(change the birthday)\n");
                    }

                    int husbandAge = calculateAge(Update_add_page_teacher_birthday);
                    if (husbandAge - calculateAge(birthdayPicker) < 16&& !checkBox.isSelected()) {
                        message.append("-wrong age(kids must be at least 18 years younger\nthan the father unless they were adopted)\n\n");
                    }
                }
            }
        }

        if (anyerrors==true){ showAlert("Validation Error", "Please ensure all information in the child tab is correct.", message.toString()); return false;
        }
        showAlert("Success", "All Tabs Validated", "All information is correct.");
        return true;
    }

    public boolean validateWifeTab(Pane tabContent) {
        TextField nameField = (TextField) tabContent.lookup("#name");
        TextField lastNameField = (TextField) tabContent.lookup("#lastname");
        DatePicker birthdayPicker = (DatePicker) tabContent.lookup("#birthday");

        String name = nameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        LocalDate birthday = birthdayPicker.getValue();

        if (!isValidName(name) || !isValidName(lastName) || birthday == null) {
            return false;
        }

        if (calculateAge(birthdayPicker) < 18) {
            return false;
        }

        return true;
    }

    public boolean validateChildTab(Pane tabContent, DatePicker husbandBirthday) {
        TextField nameField = (TextField) tabContent.lookup("#name");
        DatePicker birthdayPicker = (DatePicker) tabContent.lookup("#birthday");

        String name = nameField.getText().trim();
        LocalDate birthday = birthdayPicker.getValue();

        if (!isValidName(name) || birthday == null) {
            return false;
        }


        int husbandAge = calculateAge(husbandBirthday);

        if (husbandAge - calculateAge(birthdayPicker) < 16) {
            return false;
        }

        return true;
    }

    public boolean isValidName(String name) {
        return name.matches("[a-zA-Z]+");
    }

    public static int calculateAge(DatePicker dateNaissance) {
        LocalDate now = LocalDate.now();
        LocalDate birthday = dateNaissance.getValue();

        if (birthday == null) {
            return -1; // or throw an exception, handle it based on your requirement
        }

        return Period.between(birthday, now).getYears();
    }

    public static void showAlert(String title, String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(headerText); Text contentTextNode = new Text(contentText); ScrollPane scrollPane = new ScrollPane(contentTextNode);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefSize(400, 200);  alert.getDialogPane().setContent(scrollPane);  Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.setResizable(true);

        alert.showAndWait();
    }
////////////////////////////////

    // get data methods
    private String teacherGetSex() {
        return Update_add_page_teacher_sex_female.isSelected() ? "F" : "M";
    }


    public boolean verifyFieldsAndHighlight(int a) {
        boolean isEmptyField = false;
        boolean minorisEmptyField = false;
        StringBuilder message = new StringBuilder("The following fields are empty:\n");
        StringBuilder message2 = new StringBuilder("The following fields are empty:\n");
        if (isEmpty(Update_add_page_teacher_full_name)) {
            message.append("- Teacher first name isnt added\n");
            highlightComponent(Update_add_page_teacher_full_name);
            isEmptyField = true;
        }
        if (isEmpty(Update_add_page_teacher_last_name)) {
            message.append("- Teacher last name is empty\n");
            highlightComponent(Update_add_page_teacher_last_name);
            isEmptyField = true;
        }
        if (isEmpty(Update_add_page_teacher_gmail)) {
            message.append("- Teacher Gmail hasnt been added\n");
            highlightComponent(Update_add_page_teacher_gmail);
            isEmptyField = true;
        }
        if (isEmpty(Update_add_page_department_choice_box)) {
            message.append("- Teacher department isnt added\n");
            highlightComponent(Update_add_page_department_choice_box);
            isEmptyField = true;
        }
        if (isEmpty(Update_add_page_faculty_choice_box)) {
            message.append("- Teacher faculty isnt added\n");
            highlightComponent(Update_add_page_faculty_choice_box);
            isEmptyField = true;
        }
        if (isEmpty(Update_add_page_grade_choice_box)) {
            message.append("- Teacher grade isnt added\n");
            highlightComponent(Update_add_page_grade_choice_box);
            isEmptyField = true;
        }
        if (isEmpty(Update_add_page_echlent_choice_box)) {
            message.append("- Teacher echlent isnt added\n");
            highlightComponent(Update_add_page_echlent_choice_box);
            isEmptyField = true;
        }

        if (isEmpty(Update_add_page_teacher_nb_social)||Update_add_page_teacher_nb_social.getText().length()!=2) {

            message2.append("- you didnt enter social number \n");
            highlightComponent(Update_add_page_teacher_nb_social);
            isEmptyField = true;
        }

        if (isEmpty(Update_add_page_extra_job_choice_box)) {
            minorisEmptyField = true;
            message2.append("- you didnt choose any extra jobs\n");
            highlightComponent(Update_add_page_extra_job_choice_box);

        }
        if (isEmpty(Update_add_page_teacher_phone_num)) {
            message2.append("- Teacher Phone Number hasnt been added\n");
            highlightComponent(Update_add_page_teacher_phone_num);
            minorisEmptyField = true;

        }
        if (isEmpty(Update_add_page_teacher_bank_account_type)) {
            message.append("- Teacher Bank Account Type isnt specified\n");
            highlightComponent(Update_add_page_teacher_bank_account_type);
            isEmptyField = true;
        }
        if (isEmpty(Update_add_page_teacher_bac_year)) {
            message2.append("- Teacher Bank Account Type isnt specified\n");
            highlightComponent(Update_add_page_teacher_bac_year);
            minorisEmptyField = true;
        }
        if (Update_add_page_teacher_birthday.getValue() == null) {
            message.append("- Teacher Birthday is not added\n");
            highlightComponent(Update_add_page_teacher_birthday);
            isEmptyField = true;
        }
        if (Update_add_page_teacher_birthday.getValue() == null) {
            message.append("- Teacher Birthday is not added\n");
            highlightComponent(Update_add_page_teacher_birthday);
            isEmptyField = true;
        }
        if (isEmpty(Update_add_page_teacher_address)) {
            minorisEmptyField = true;
            message2.append("-  you didnt enter Teacher CCP\n");
            highlightComponent(Update_add_page_teacher_address);

        }
        if (Update_add_page_teacher_has_univ_housing.isSelected() == false &&
                Update_add_page_teacher_has_his_own_house.isSelected() == false &&
                Update_add_page_teacher_has_no_house.isSelected() == false
        ) {
            message.append("- you didnt enter Teachers type CCP\n");
            highlightComponent(Update_add_page_teacher_has_univ_housing);
            highlightComponent(Update_add_page_teacher_has_his_own_house);
            highlightComponent(Update_add_page_teacher_has_no_house);
            isEmptyField = true;
        }
        if (Update_add_page_teacher_sex_male.isSelected() == false &&
                Update_add_page_teacher_sex_female.isSelected() == false
        ) {
            message.append("- you didnt choose the sex\n");
            highlightComponent(Update_add_page_teacher_sex_male);
            highlightComponent(Update_add_page_teacher_sex_female);
            isEmptyField = true;
        }
        if (isEmpty(Update_add_page_teacher_bank_account_details)) {
            message.append("you didnt add any bank account details\n");
            highlightComponent(Update_add_page_teacher_bank_account_details);
            isEmptyField = true;
        }
        if(a==0) return false;
        if (minorisEmptyField==true) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Empty Fields that can be left empty");
            alert.setHeaderText(null);
            alert.setContentText(message2.toString());
            alert.showAndWait();
            // Add event handler to the OK button
            alert.setOnCloseRequest(event -> {
                if (alert.getResult() == ButtonType.OK) {
                    verifyFieldsAndHighlight(0);
                }
            });
        }

        if (isEmptyField == true) {

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Empty Fields");
            alert.setHeaderText(null);
            alert.setContentText(message.toString());
            alert.showAndWait();
            // Add event handler to the OK button
            alert.setOnCloseRequest(event -> {
                if (alert.getResult() == ButtonType.OK) {
                    verifyFieldsAndHighlight(0);
                }
            });


            return true;
        }
        return false;





    }

    private boolean isEmpty(TextField textField) {
        if (textField.getText()==null) return true;
        return textField.getText().isEmpty();
    }

    private boolean isEmpty(ChoiceBox<?> choiceBox) {
        return choiceBox.getValue() == null || choiceBox.getValue().toString().isEmpty();
    }


    private void highlightComponent(Control component) {
        // Create a timeline for animating the component's layout
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0),
                        new KeyValue(component.translateXProperty(), 0)
                ),
                new KeyFrame(Duration.seconds(0.1),
                        new KeyValue(component.translateXProperty(), -5)
                ),
                new KeyFrame(Duration.seconds(0.2),
                        new KeyValue(component.translateXProperty(), 5)
                ),
                new KeyFrame(Duration.seconds(0.3),
                        new KeyValue(component.translateXProperty(), -5)
                ),
                new KeyFrame(Duration.seconds(0.4),
                        new KeyValue(component.translateXProperty(), 5)
                ),
                new KeyFrame(Duration.seconds(0.5),
                        new KeyValue(component.translateXProperty(), 0)
                )
        );
        timeline.play();
    }


    void Update_add_page_teacher_RESET() {
        Update_add_page_teacher_full_name.clear();
        Update_add_page_teacher_nb_social.clear();
        Update_add_page_teacher_last_name.clear();
        Update_add_page_teacher_gmail.clear();
        Update_add_page_teacher_bac_year.clear();

        Update_add_page_teacher_address.clear();
        Update_add_page_teacher_birthday.setValue(LocalDate.now());
        Update_add_page_teacher_bank_account_details.clear();
        Update_add_page_faculty_choice_box.getSelectionModel().clearSelection();
        Update_add_page_department_choice_box.getSelectionModel().clearSelection();
        Update_add_page_extra_job_choice_box.getSelectionModel().clearSelection();
        Update_add_page_echlent_choice_box.getSelectionModel().clearSelection();
        Update_add_page_grade_choice_box.getSelectionModel().clearSelection();
        Update_add_page_teacher_bank_account_type.getSelectionModel().clearSelection();
        Update_add_page_teacher_sex_female.setSelected(false);
        Update_add_page_teacher_sex_male.setSelected(false);
        Update_add_page_teacher_married.setSelected(false);
        Update_add_page_teacher_has_univ_housing.setSelected(false);
        Update_add_page_teacher_has_his_own_house.setSelected(false);
        Update_add_page_teacher_has_no_house.setSelected(false);
        Update_add_page_teacher_bac_year.clear();

        Update_add_page_teacher_husband_bank_account_details.clear();
        Update_add_page_teacher_use_husband_bank.setSelected(false);
        woman_bank_details_husband( false);
        general_reset_for_add_update_page_info();
        try {
            reset_Update_add_page_teacher_generate_add_familly_tabs();
            Update_add_page_teacher_familly_info.setDisable(true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void fillPageFromEnseignant(String idenseignant) throws SQLException {
        Enseignant enseignant=load_curent_Enseignant(idenseignant);
        general_reset_for_add_update_page_info();
        Update_add_page_teacher_nb_social.setText(idenseignant);
        Update_add_page_teacher_full_name.setText(enseignant.getNom());
        Update_add_page_teacher_last_name.setText(enseignant.getPrenom());
        Update_add_page_teacher_gmail.setText(enseignant.getAdresseEmail());
        Update_add_page_teacher_address.setText(enseignant.getAdresse());
        Update_add_page_teacher_bac_year.setText(Integer.toString(enseignant.anneeDeBac));

        Update_add_page_teacher_phone_num.setText(String.valueOf(enseignant.tel));

        if (enseignant.getDateNaissance() != null) {
            Update_add_page_teacher_birthday.setValue(enseignant.getDateNaissance()
                    .toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        }
        if(enseignant.married&&enseignant.getSex().equals("F")&&(enseignant.gettypeDeCcp() != null)){
            String[] splitStrings = split(enseignant.numéroDeCcp);
            System.out.println("s1: " + splitStrings[0]);
            System.out.println("s2: " + splitStrings[1]);
            Update_add_page_teacher_bank_account_details.setText(splitStrings[0]);
            if ((splitStrings[1]) != null) {
                Update_add_page_teacher_husband_bank_account_details.setText(splitStrings[1]);}
            else Update_add_page_teacher_husband_bank_account_details.clear();

        }else
        if (enseignant.getNuméroDeCcp() != null) {

            Update_add_page_teacher_bank_account_details.setText(enseignant.numéroDeCcp); }



        if (enseignant.getExtra_job() != null) {
            Update_add_page_extra_job_choice_box.setValue(enseignant.getExtra_job());
        }

        if (enseignant.getEchlont() != 0) {
            Update_add_page_echlent_choice_box.setValue(enseignant.getEchlont());
        }

        Update_add_page_department_choice_box.setValue(enseignant.department);

        if (getFacultes().get(0)!= null) {
            Update_add_page_faculty_choice_box.setValue(getFacultes().get(0));
        }

        if (enseignant.getGrade() != null) {
            Update_add_page_grade_choice_box.setValue(enseignant.getGrade());
        }

        if (enseignant.gettypeDeCcp() != null) {
            Update_add_page_teacher_bank_account_type.setValue(enseignant.gettypeDeCcp());
        }

        if (enseignant.getSex().equals("F")) {
            Update_add_page_teacher_sex_female.setSelected(true);
        } else if (enseignant.getSex().equals("M")) {
            Update_add_page_teacher_sex_male.setSelected(true);
        }

        Update_add_page_teacher_married.setSelected(enseignant.isMarried());

        Update_add_page_teacher_bank_account_type.setValue(enseignant.typeDeCcp);
        char s=enseignant.getResidence_type();
        if (s == '-') {
            Update_add_page_teacher_has_univ_housing.setSelected(true);
        } else if ( s == '0') {
            Update_add_page_teacher_has_his_own_house.setSelected(true);
        } else {
            Update_add_page_teacher_has_no_house.setSelected(true);

        }
        set_Update_add_page_teacher_generate_add_familly_tabs_with_pre_data(enseignant.partner,enseignant.getKidsList());

    }

    @FXML
    void    getTeacherInfo(){
        if(verifyFieldsAndHighlight(0) )return;
        System.out.println("max");
        if (updating_or_inserting) getTeacherInfoo_modify();
        else getTeacherInfo_update();



        updating_or_inserting=false;
        add_modify_page.setText("s");

    }

    public void changeText_update_add_teacher(String newText) {
        update_add_teacher.setText(newText);
        if(!updating_or_inserting)add_modify_page.setText("ADDIND");
        else add_modify_page.setText("UPDATING");
    }

    void    start_update_mode(){
        Update_add_page_teacher_nb_social.setDisable(true);
        Update_add_page_teacher_sex_female.setDisable(true);
        Update_add_page_teacher_sex_male.setDisable(true);
        Update_add_page_teacher_bac_year.setDisable(true);

        if (id_update==null) return;
        updating_or_inserting=true;
        changeText_update_add_teacher("update Enseignant");
    }
    void    end_update_mode(){
        Update_add_page_teacher_nb_social.setDisable(false);
        Update_add_page_teacher_sex_female.setDisable(false);
        Update_add_page_teacher_sex_male.setDisable(false);
        Update_add_page_teacher_bac_year.setDisable(false);
        id_update=null;
        updating_or_inserting=false;
        changeText_update_add_teacher("add new Enseignant");
    }


    public void getTeacherInfo_update() {
        try {


            char s=(Update_add_page_teacher_has_univ_housing.isSelected()) ? '-' : ((Update_add_page_teacher_has_his_own_house.isSelected()) ? '0' : '+');
            Enseignant enseignant = new Enseignant(
                    Update_add_page_teacher_full_name.getText(),
                    Update_add_page_teacher_last_name.getText(),
                    teacherGetSex(),
                    Update_add_page_teacher_birthday,
                    Update_add_page_teacher_married.isSelected(),
                    Update_add_page_teacher_gmail.getText(),
                    Integer.parseInt(getTextFromComponent(Update_add_page_teacher_phone_num)),
                    Integer.parseInt(getTextFromComponent(Update_add_page_teacher_bac_year)),
                    Update_add_page_department_choice_box.getValue());


            enseignant.setAdresse( Update_add_page_teacher_address.getText());


            if(enseignant.married&&enseignant.getSex().equals("F")){
                String a=mix(getTextFromComponent(Update_add_page_teacher_bank_account_details),getTextFromComponent( Update_add_page_teacher_husband_bank_account_details), Update_add_page_teacher_use_husband_bank.isSelected());
                enseignant.setNuméroDeCcp(a);


            }else

                enseignant.setNuméroDeCcp(Update_add_page_teacher_bank_account_details.getText());
            enseignant.settypeDeCcp(Update_add_page_teacher_bank_account_type.getValue());

            enseignant.setFamilyMembers(get_info_Update_add_page_teacher_from_familly_tabs());

            enseignant.setResidence_type(s);

            enseignant.setEchlont( Update_add_page_echlent_choice_box.getValue() );

            enseignant.setGrade(Update_add_page_grade_choice_box.getValue() );

            enseignant.setExtra_job( Update_add_page_extra_job_choice_box.getValue());

            enseignant.setSex(  teacherGetSex());

            enseignant.setIdEnseignant(  getTextFromComponent(Update_add_page_teacher_nb_social));
            add_or_update_Enseignant(enseignant.getIdEnseignant(),enseignant);

            if(!(Update_add_page_extra_job_choice_box.getValue().equals("non")) ) addJob(enseignant.getIdEnseignant(), enseignant.extra_job);
            addResidence(enseignant.getIdEnseignant(), enseignant.adresse,enseignant.getResidence_type());
            addEchelon(enseignant.getIdEnseignant(), enseignant.echlont);
            System.out.println("in class"+enseignant.getGrade()+"data"+Update_add_page_grade_choice_box.getValue());
            addGrade(enseignant.getIdEnseignant(),enseignant.getGrade());
            addUpdateCompte(enseignant.getIdEnseignant(),enseignant.typeDeCcp, enseignant.numéroDeCcp);

            if(enseignant.married==true&& teacherGetSex().equals("M")) {
                if( Integer.parseInt(getTextFromComponent(Update_add_page_teacher_wife_numbers))==1) {
                    System.out.println( enseignant.partner.getNom());
                    add_partner(enseignant.getIdEnseignant(), enseignant.partner );
                    partner now =getPartner(enseignant.getIdEnseignant(),null);
                    int a =now.getId();
                    set_work_status(a, enseignant.partner.work);}
                if( (Integer.parseInt(getTextFromComponent(Update_add_page_teacher_male_kids_numbers)+Integer.parseInt(getTextFromComponent(Update_add_page_teacher_female_kids_number))))!=0){
                    add_or_update_kids(enseignant.getIdEnseignant(), enseignant.kidsList);}
            }

            Update_add_page_teacher_RESET();
            setTeachers_page() ;
            end_update_mode();

        } catch (Exception e) {
            showAlert("Error occurred while retrieving teacher info");
            System.out.println("Error occurred while retrieving teacher info: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public void getTeacherInfoo_modify() {
        try {if(id_update==null){ showAlert("there is a error in id_update is null"); return;}

            Enseignant enseignantOG=load_curent_Enseignant(Update_add_page_teacher_nb_social.getText());
            if(!id_update.equals(enseignantOG.idEnseignant)){ showAlert("there is a error in id_update is isnt the same as enseignantOG id"); return;}

            // filling the new updated version
            char s=(Update_add_page_teacher_has_univ_housing.isSelected()) ? '-' : ((Update_add_page_teacher_has_his_own_house.isSelected()) ? '0' : '+');
            Enseignant enseignant = new Enseignant(
                    Update_add_page_teacher_full_name.getText(),
                    Update_add_page_teacher_last_name.getText(),
                    teacherGetSex(),
                    Update_add_page_teacher_birthday,
                    Update_add_page_teacher_married.isSelected(),
                    Update_add_page_teacher_gmail.getText(),
                    Integer.parseInt(getTextFromComponent(Update_add_page_teacher_phone_num)),
                    Integer.parseInt(getTextFromComponent(Update_add_page_teacher_bac_year)),
                    Update_add_page_department_choice_box.getValue());
            add_or_update_Enseignant(enseignantOG.getIdEnseignant(),enseignant);
            if(!enseignant.getSex().equals(enseignantOG.getSex())){showAlert("please you cant change the Teacher's sex");
                highlightComponent(Update_add_page_teacher_sex_female);
                highlightComponent(Update_add_page_teacher_sex_male);
                if(enseignant.getSex().equals("M")){ Update_add_page_teacher_sex_male.setSelected(true);
                    Update_add_page_teacher_sex_female.setSelected(false);}
                if(enseignant.getSex().equals("F")){ Update_add_page_teacher_sex_male.setSelected(false);
                    Update_add_page_teacher_sex_female.setSelected(true);}
                return;}

            enseignant.setAdresse( Update_add_page_teacher_address.getText());
            enseignant.settypeDeCcp(Update_add_page_teacher_bank_account_type.getValue());
            enseignant.setResidence_type(s);
            enseignant.setEchlont( Update_add_page_echlent_choice_box.getValue() );
            enseignant.setGrade(Update_add_page_grade_choice_box.getValue() );
            enseignant.setExtra_job( Update_add_page_extra_job_choice_box.getValue());
            System.out.println(""+enseignant.echlont+enseignantOG.echlont
                    +enseignant.grade+enseignantOG.grade);


            if(enseignant.married&&enseignant.getSex().equals("F")){
                String a=mix(getTextFromComponent(Update_add_page_teacher_bank_account_details),getTextFromComponent( Update_add_page_teacher_husband_bank_account_details), Update_add_page_teacher_use_husband_bank.isSelected());
                enseignant.numéroDeCcp=a;


            }else enseignant.numéroDeCcp=Update_add_page_teacher_bank_account_details.getText();



            if(!(enseignant.typeDeCcp.equals(enseignantOG.typeDeCcp))||!(enseignant.numéroDeCcp.equals(enseignantOG.numéroDeCcp)))
                addUpdateCompte(id_update,enseignant.typeDeCcp, enseignant.numéroDeCcp);


            if (enseignant.echlont!=enseignantOG.echlont)
                addEchelon(id_update, enseignant.echlont);
            if(!(enseignant.grade.equals(enseignantOG.grade)))
                addGrade(id_update,enseignant.grade);


            enseignant.setFamilyMembers(get_info_Update_add_page_teacher_from_familly_tabs());
            //
            if(Update_add_page_extra_job_choice_box.getValue().equals("non")||!Update_add_page_extra_job_choice_box.getValue().equals(enseignantOG.extra_job) )setJobEndDate(enseignantOG.extra_job_id);
            if(!Update_add_page_extra_job_choice_box.getValue().equals("non")) addJob(id_update, enseignant.extra_job);
            addResidence(id_update, enseignant.adresse,enseignant.getResidence_type());



            partner newPartner = enseignant.getPartner();
            partner oldPartner =enseignantOG.getPartner();
            if (oldPartner == null && newPartner == null);
            else if (oldPartner == null && newPartner != null)add_partner(id_update, newPartner);
            else if (oldPartner != null && newPartner == null) {
                setEpouxTravailleEndDate(oldPartner.getId());
                setEpouxEndDate(oldPartner.getId());
            }
            else if (!oldPartner.getNom().equals(newPartner.getNom()) || !oldPartner.getPrenom().equals(newPartner.getPrenom()) ||
                    oldPartner.getDateNaissance().compareTo(newPartner.getDateNaissance()) != 0 ) {
                setEpouxTravailleEndDate(oldPartner.getId());
                setEpouxEndDate(oldPartner.getId());
                add_partner(id_update,newPartner );
                partner a=getPartner(id_update,null);
                set_work_status(a.id,newPartner.work);
            } else if (oldPartner.isWork() != newPartner.isWork()) {
                set_work_status(oldPartner.id,newPartner.isWork());
            } else {
                System.out.println("Partner comparison result: no difrent " );
            }




            ArrayList<kids> new_version =enseignant.getKidsList();
            ArrayList<kids> old_version =   enseignantOG.getKidsList();


            ArrayList<kids> newKids = new ArrayList<>();
            ArrayList<kids> unchangedKids = new ArrayList<>();
            ArrayList<Integer> removedKidsIds = new ArrayList<>();
            ArrayList<kids> modifiedKids = new ArrayList<>();

            for (kids newKid : new_version) {
                boolean found = false;
                for (kids oldKid : old_version) {
                    if (newKid.getNom().equals(oldKid.getNom()) && newKid.getPrenom().equals(oldKid.getPrenom()) &&
                            newKid.getDateNaissance().compareTo(oldKid.getDateNaissance()) == 0) {
                        found = true;
                        if (newKid.isCondition() != oldKid.isCondition() || newKid.isAdoupted() != oldKid.isAdoupted()) {
                            newKid.setId(oldKid.getId());
                            modifiedKids.add(newKid);
                        } else {
                            unchangedKids.add(newKid);
                        }
                        break;
                    }
                }
                if (!found) {
                    newKids.add(newKid);
                }
            }

            for (kids oldKid : old_version) {
                boolean found = false;
                for (kids newKid : new_version) {
                    if (newKid.getNom().equals(oldKid.getNom()) && newKid.getPrenom().equals(oldKid.getPrenom()) &&
                            newKid.getDateNaissance().compareTo(oldKid.getDateNaissance()) == 0) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    removedKidsIds.add(oldKid.getId());
                }
            }

            for (int  a : removedKidsIds) { setEnfantEndDate(a)  ;   }
            for (kids  a : modifiedKids) {setEnfantEndDate(a.getId()) ; insertNewKid( DriverManager.getConnection(URL), id_update, a) ;   }
            add_or_update_kids(id_update,newKids);



            end_update_mode();
            Update_add_page_teacher_RESET();
            setTeachers_page() ;
        } catch (Exception e) {
            showAlert("Error occurred while retrieving teacher info");
            System.out.println("Error occurred while retrieving teacher info: " + e.getMessage());
            e.printStackTrace();
        }
    }



    public  String getTextFromComponent(TextInputControl component) {
        if (component.getText().isEmpty()) {
            return "0";
        } else {
            return component.getText();
        }
    }

    @FXML
    private void calculateSalary_getSelectedEnseignantId() throws IOException, SQLException {

        String id= getSelectedEnseignantId();

            currentDate = LocalDate.now();
            startDate = getOldestStartDate(id);

        initializeMonthPeriods();
        initializeMonthNumbers();
        populateComboBox();


        if (id != null) {
            String[] cla_deyails=calculateSalarys(id);
            document =true;
            change(  cla_deyails);


            printstuff_full_name.setText(cla_deyails[0] + " " + cla_deyails[1]);
            printstuff_grade.setText(cla_deyails[2]);
            printstuff_num_echlent.setText(cla_deyails[4]);
            printstuff_job.setText(cla_deyails[26]);
            printstuff_married.setSelected(!Boolean.parseBoolean(cla_deyails[25]));
            printstuff_final_salary.setText(cla_deyails[21]);

            printstuff_RETENUE_LOGEMENT.setText(cla_deyails[20]);
            printstuff_I_R_G.setText(cla_deyails[19]);

            printstuff_A_S.setText(cla_deyails[18]);
            printstuff_BRUT.setText(cla_deyails[17]);
            printstuff_ALLOCATION_FAMILIAL.setText(cla_deyails[16]);
            printstuff_LOGEMENT.setText(cla_deyails[15]);
            printstuff_CAl_SCIENTIFIQUE.setText(cla_deyails[14]);
            printstuff_ENCADREMENT.setText(cla_deyails[13]);
            printstuff_DOCUMENTATION.setText(cla_deyails[12]);
            printstuff_RESPONSABILITE.setText(cla_deyails[10]);
            printstuff_IND_Q_POSTE.setText(cla_deyails[11]);
            printstuff_I_E_P.setText(cla_deyails[8]);
            printstuff_SALAIRE_DE_BASE.setText(cla_deyails[7]);
            printstuff_ccp.setText(cla_deyails[6]);

            printstuff_kids_number.setText(cla_deyails[22]);
            printstuff_wife_work.setSelected(Boolean.parseBoolean(cla_deyails[24]));
            printstuff_wife_bonus.setText(cla_deyails[29]);
            printstuff_wife_full_name.setText(cla_deyails[23]);
            printstuff_S_FAM.setText(cla_deyails[5]);
            printstuff_kids_bonus.setText(cla_deyails[28]);


            System.out.println(""+calculateSalary(id));


            setPrintstuff_page();
        } else {
            showAlert("you didnt select any Enseignant from the table");
        }
    }

    String[] calculateSalarys(String idEnseignant) throws IOException {
        ArrayList<int[]> specificDetails = get_details_static(null);

        double Zone = 0;
        double Index = 0;
        double Percentage = 0;
        for (int[] detail : specificDetails) {
            System.out.println("Zone: " + detail[0] + ", Index: " + detail[1] + ", Percentage for EXP: " + detail[2]);
            Zone = (double) detail[0];
            Index = (double) detail[1];
            Percentage = (double) detail[2];
        }
        if (Index == 0 || Zone == 0 || Percentage == 0) {
            showAlert("there isnt any recent static details(Zone/Index/Percentage for EXP)");
            return null;
        }

        Enseignant enseignant = null;
        try {
            enseignant = load_curent_Enseignant(idEnseignant);
            System.out.println("" + idEnseignant);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        List<Double> teacherDetails = getTeacherDetails(enseignant.getGrade(), enseignant.getEchlont());
        if (teacherDetails == null) {
            showAlert("there isnt any details for this grade (" + enseignant.getGrade() + ") please add them");
            return null;
        }

        // Extract details from the fetched list
        double basicSalary = teacherDetails.get(0);
        double echelonValue = teacherDetails.get(1);
        double encadrement = teacherDetails.get(2);
        double documentation = teacherDetails.get(3);
        double clification_scen = teacherDetails.get(4);

        List<String> steps = new ArrayList<>();
        steps.add(enseignant.getNom()); // Index 0
        steps.add(enseignant.getPrenom()); // Index 1

        // Index 2
        if (enseignant.grade.equals("PR")){
            steps.add("PROFESSEUR");
        } else if (enseignant.grade.equals("AB")){
            steps.add("MAITRE DE ASSISTANT(B)");
        } else if (enseignant.grade.equals("AA")){
            steps.add("MAITRE DE ASSISTANT(A)");
        } else if (enseignant.grade.equals("CA")){
            steps.add("MAITRE DE CONFIRANCE (A)");
        } else if (enseignant.grade.equals("CB")){
            steps.add("MAITRE DE CONFIRANCE (B)");
        } else {
            steps.add("error");
        }

        // Index 3
        if (enseignant.grade.equals("PR")){
            steps.add("7");
        } else if (enseignant.grade.equals("AB")){
            steps.add("1");
        } else if (enseignant.grade.equals("AA")){
            steps.add("3");
        } else if (enseignant.grade.equals("CA")){
            steps.add("6");
        } else if (enseignant.grade.equals("CB")){
            steps.add("4");
        } else {
            steps.add("error");
        }

        steps.add("" + enseignant.echlont); // Index 4

        // Index 5
        String S_FAM;
        int ALLOCATION_FAMILIAL = 0;
        int kidsnb = 0;
        for (kids child : enseignant.getKidsList()) {
            if (((child.getSex().equals("M") && child.getAge() < 18 && child.isCondition())
                    || (child.getSex().equals("M") && child.getAge() < 21 && child.isCondition())
                    || (child.getSex().equals("F") && !child.isCondition())) && (!child.isAdoupted())) {
                ALLOCATION_FAMILIAL += 300;
                kidsnb++;
            }
        }
        double kidsbn = ALLOCATION_FAMILIAL;
        int xxx = 0;
        if (!enseignant.getSex().equals("F") && enseignant.getPartner() != null && enseignant.getPartner().isWork()) {
            ALLOCATION_FAMILIAL += 1500;
            xxx = 1500;
        }

        if (enseignant.getPartner() != null) {
            S_FAM = "M/";
        } else if (enseignant.getPartner() == null && enseignant.getKidsList().isEmpty()) {
            S_FAM = "D/";
        } else {
            S_FAM = "D/";
        }
        S_FAM = S_FAM + "" + kidsnb;
        steps.add(S_FAM); // Index 5

        // Index 6
        boolean husband = false;
        String husband_ccp = "non";
        if (enseignant.married && enseignant.getSex().equals("F") && (enseignant.gettypeDeCcp() != null)) {
            String[] splitStrings = split(enseignant.numéroDeCcp);
            System.out.println("s1: " + splitStrings[0]);
            steps.add(splitStrings[0]);
            husband = Boolean.parseBoolean(splitStrings[splitStrings.length - 1]);
            if (husband) {
                husband_ccp = splitStrings[0];
            }
        } else {
            steps.add(enseignant.numéroDeCcp);
        }

        // Calculate the salary components
        double salaireDeBase = basicSalary * Index;
        steps.add(String.format("%.2f", salaireDeBase)); // Index 7

        double IEP = echelonValue * Index; // Assuming IEP calculation
        steps.add(String.format("%.2f", IEP)); // Index 8


        double salarbasic = salaireDeBase + IEP;

        double zone = salarbasic * (Zone / 100.0);
        steps.add(String.format("%.2f", zone)); // Index 9

        double responsabilite = enseignant.extra_job_bonus; // Define how postsup is calculated
        steps.add(String.format("%.2f", responsabilite)); // Index 10

        double exp = (enseignant.getEchlont() * Percentage) * salaireDeBase / 100;
        steps.add(String.format("%.2f", exp)); // Index 11
        steps.add(String.format("%.2f", documentation)); // Index 12

        encadrement = (encadrement * salarbasic) / 100;
        clification_scen = (clification_scen * salarbasic) / 100;
        steps.add(String.format("%.2f", encadrement)); // Index 13
        steps.add(String.format("%.2f", clification_scen)); // Index 14

        double LOGEMENT = 0;
        if (enseignant.getResidence_type() == '-') {
            LOGEMENT = -4338.96;
        } else if (enseignant.getResidence_type() == '+') {
            LOGEMENT = 1500;
        }
        steps.add(String.format("%.2f", LOGEMENT)); // Index 15

        steps.add("" + ALLOCATION_FAMILIAL); // Index 16

        double brut_general = salarbasic + zone + responsabilite + exp + documentation + encadrement + clification_scen + LOGEMENT + ALLOCATION_FAMILIAL;
        steps.add(String.format("%.2f", brut_general)); // Index 17

        double as = 0.09 * brut_general;
        steps.add(String.format("%.2f", as)); // Index 18

        double brut_BASE_irg = brut_general - as;

        // Truncate the last digit to get a clean number
        brut_BASE_irg = ((int) (brut_BASE_irg / 10)) * 10;

        // Table lookup for IRG based on brut_imposable
        double irg = get_IRG(brut_BASE_irg, irgfile); // Look up the IRG value from the table based on brut_imposable
        steps.add(String.format("%.2f", irg)); // Index 19
        steps.add("" + 00000); // Index 20

        double net = brut_general - irg - as - LOGEMENT;

        double a = (net / 30);
        a = enseignant.monthly_absense * a;
        net = net - a;
        steps.add(String.format("%.2f", net)); // Index 21

        steps.add("" + kidsnb); // Index 22

        if (enseignant.partner == null && enseignant.getSex().equals("F") && enseignant.isMarried() && husband) {
            steps.add("has a husband (his bank details: " + husband_ccp + ")"); // Index 23
            steps.add("1"); // Index 24
        }
        else if (enseignant.partner == null && enseignant.getSex().equals("F") && enseignant.isMarried() && !husband) {
            steps.add("has a husband (his bank details: " + husband_ccp + ")"); // Index 23
            steps.add("1"); // Index 24
        }
        else if (enseignant.getSex().equals("F") && !enseignant.isMarried()) {
            steps.add("non " + husband_ccp + ")"); // Index 23
            steps.add("0"); // Index 24
        }
        else if (enseignant.partner != null&& enseignant.getSex().equals("M") && enseignant.isMarried()) {
            steps.add("" + enseignant.partner.getNom() + " " + enseignant.partner.getPrenom()); // Index 23
            steps.add(enseignant.partner.isWork() ? "1" : "0"); // Index 24
        }
        else {
            steps.add("NON"); // Index 23
            steps.add("0"); // Index 24
        }
        // Index 25
if (enseignant.isMarried())steps.add("0");
else steps.add("1");


        if (enseignant.extra_job != null || enseignant.extra_job_bonus != 0) {
            steps.add("" + enseignant.extra_job + " (" + enseignant.extra_job_bonus + ")"); // Index 26
        } else {
            steps.add("NON (0)"); // Index 26
        }

        steps.add("" + kidsnb); // Index 27
        steps.add("" + kidsbn); // Index 28
        steps.add("" + xxx); // Index 29

        return steps.toArray(new String[0]);
    }


    public static double get_IRG(double a, String filePath) throws IOException {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(filePath));
            String line;
            while ((line = reader.readLine()) != null) {
                // Debugging: Print each line


                // Split the line based on whitespace
                String[] parts = line.split("\\s+");
                if (parts.length >= 2) {
                    // Parse the first column value
                    String firstColumn = parts[0].replace(',', '.');
                    double column1Value = Double.parseDouble(firstColumn);




                    if (Double.compare(column1Value, a) == 0) {

                        String secondColumn = parts[1].replace(',', '.');
                        double column2Value = Double.parseDouble(secondColumn);

                        return column2Value;
                    }
                }
            }
            return 0.0;
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }
    public static double calculateSalary(String idEnseignant) throws IOException {
        ArrayList<int[]> specificDetails = get_details_static(null);

        double Zone=0;
        double Index=0;
        double Percentage=0;
        for (int[] detail : specificDetails) {
            System.out.println("Zone: " + detail[0] + ", Index: " + detail[1] + ", Percentage for EXP: " + detail[2]);
            Zone=(double) detail[0];
            Index=(double)detail[1];
            Percentage=(double)detail[2];
        }
        if(Index==0||  Zone==0 ||  Percentage==0||specificDetails==null||specificDetails.isEmpty()) {

            showAlert("there isnt any recent static details(Zone/Index/Percentage for EXP)");
            return 0; }
        Enseignant enseignant = null;
        try {
            enseignant = load_curent_Enseignant(idEnseignant);
            if(enseignant==null){

                showAlert("error acuured while retreving the enseignant data try agaib ");
                return 0; }
            System.out.println(""+idEnseignant);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        List<Double> teacherDetails = getTeacherDetails(enseignant.getGrade(), enseignant.getEchlont());
        if(teacherDetails==null||teacherDetails.isEmpty()) {
            showAlert("there isnt any details for this grade (" + enseignant.getGrade() + ") please add them");
            return 0 ;  }
        // Extract details from the fetched list
        double basicSalary = teacherDetails.get(0);
        double echelonValue = teacherDetails.get(1);
        double encadrement = teacherDetails.get(2);// (teacherDetails.get(1)*)/100;
        double documentation = teacherDetails.get(3);
        double clification_scen = teacherDetails.get(4);// (teacherDetails.get(1)*)/100;

        // Print extracted details
        System.out.println("Basic Salary: " + basicSalary);
        System.out.println("Echelon Value: " + echelonValue);
        System.out.println("Encadrement: " + encadrement);
        System.out.println("Documentation: " + documentation);
        System.out.println("clification_scen: " + clification_scen);
        System.out.println("indicial: " +Index);
        System.out.println("zone: " +  Zone);
        // Calculate the salary components
        double salaireDeBase = basicSalary * Index;
        System.out.println("Salaire De Base: " + salaireDeBase);

        double IEP = echelonValue *Index; // Assuming IEP calculation
        System.out.println("IEP: " + IEP);

        double salarbasic = salaireDeBase + IEP;
        System.out.println("Salarbasic: " + salarbasic);

        double zone = salarbasic *( Zone / 100.0);
        System.out.println("Zone: " + zone);

        double responsabilite= 0; // Define how postsup is calculated
        System.out.println("responsabilite: " + responsabilite);

        double exp = (enseignant.getEchlont() *Percentage) * salaireDeBase/100;
        System.out.println("Exp: " + exp);
        encadrement =  (encadrement*salarbasic)/100;
        clification_scen = (clification_scen*salarbasic)/100;
        System.out.println("encadrement: " + encadrement);
        System.out.println("clification_scen: " + clification_scen);
        System.out.println("getResidence_type: " + enseignant.getResidence_type());
        double LOGEMENT = 0;
        if (enseignant.getResidence_type() == '-') { // Assuming 'A' indicates housing provided
            LOGEMENT = -4000;
        } else if (enseignant.getResidence_type() == '+') { // Assuming 'A' indicates housing provided
            LOGEMENT = 1500;
        }

        System.out.println("LOGEMENT: " + LOGEMENT);

        double ALLOCATION_FAMILIAL = 0;
        for (kids child : enseignant.getKidsList()) {
            if ((child.getSex().equals("M") && child.getAge() < 18 && child.isCondition())
                    || (child.getSex().equals("M") && child.getAge() < 21 && child.isCondition())
                    || (!child.getSex().equals("F") && child.isCondition())) {
                ALLOCATION_FAMILIAL += 300;
            }
        }
        System.out.println("ALLOCATION_FAMILIAL (before partner check): " + ALLOCATION_FAMILIAL);

        if (!enseignant.getSex().equals("F") && enseignant.getPartner() != null && enseignant.getPartner().isWork()) {
            ALLOCATION_FAMILIAL += 1500;
        }
        System.out.println("ALLOCATION_FAMILIAL (after partner check): " + ALLOCATION_FAMILIAL);

        double brut_general = salarbasic + zone + responsabilite + exp + documentation + encadrement + clification_scen + LOGEMENT + ALLOCATION_FAMILIAL;
        System.out.println("Brut General: " + brut_general+"="+ "zone:"+zone + "+responsabilite:"+ responsabilite+ "+exp:" + exp+ "+documentation:" + documentation+ "+encadrement:" + encadrement+ "+clification_scen:" + clification_scen + "+LOGEMENT:"+ LOGEMENT + "+ALLOCATION_FAMILIAL:"+ ALLOCATION_FAMILIAL);

        double brut_imposable = brut_general - (LOGEMENT + ALLOCATION_FAMILIAL );
        System.out.println("Brut Imposable    dfsrhsrgdfbdfg (before truncation): " + brut_imposable);



        double as = 0.09 *( brut_imposable);
        System.out.println("AS: " + as);



        double brut_BASE_irg = brut_general - (LOGEMENT + ALLOCATION_FAMILIAL+as );
        System.out.println("brut_BASE_irg (before truncation): " + brut_BASE_irg);

        // Truncate the last digit to get a clean number
        brut_BASE_irg = ((int) (brut_BASE_irg / 10)) * 10;
        System.out.println("brut_BASE_irg (after truncation): " + brut_BASE_irg);

        // Table lookup for IRG based on brut_imposable
        double irg = get_IRG( brut_BASE_irg, irgfile); // Look up the IRG value from the table based on brut_imposable
        System.out.println("IRG: " + irg);

        double net = brut_general - irg - as - LOGEMENT;
        System.out.println("Net: " + net);


        double a = (net/30);
        a=getNombre_of_absence(idEnseignant,0,0) *a  ;      net=net-a; System.out.println("abs: "+getNombre_of_absence(idEnseignant,0,0)+"Net: " + net);
        return net;


    }

    @FXML
    private void calculateSalary__getSelectedEnseignantId() {
        String id= getSelectedEnseignantId();
        if (id != null) {


            setAdd_modify_page();
            Update_add_page_teacher_familly_info.setDisable(false);
        } else {
            showAlert("you didnt select any Enseignant from the table");
        }
    }

    static Enseignant   load_curent_Enseignant(String IdEnseignant ) throws SQLException {
        try {
            Enseignant enseignant = get_Enseignant(IdEnseignant);
            Grade grade= getGrade(IdEnseignant, null);
            Ehclent ehclent= getEchlon( IdEnseignant, null);
            enseignant.setEchlont(ehclent.ehclent);
            enseignant.setGrade(grade.grade);
            housing house=  getResidence( IdEnseignant, null);
            enseignant.setAdresse( house.getAdresse());
            enseignant.setResidence_type(house.getResidence_type());
            extra_job job;
            if((job=  getJob( IdEnseignant,null))!=null){
                enseignant.set_job(job);

            }else { enseignant.setExtra_job(null);
                enseignant.setExtra_job_bonus(0);}

            if(enseignant.married){
                enseignant.setKidsList(getKids(IdEnseignant, null));
                enseignant.setPartner(getPartner(IdEnseignant, null));
                if(enseignant.partner!=null)
                    enseignant.partner.setWork(get_job_status( enseignant.partner.getId(), null));

            }
            enseignant.monthly_absense=getNombre_of_absence(enseignant.getIdEnseignant(),0,0);
            String[] compteInfo = getCompteInfo(enseignant.getIdEnseignant());
            enseignant.setNuméroDeCcp(compteInfo[1]);
            enseignant.settypeDeCcp(compteInfo[0]);
            return enseignant;
        }catch (Exception e){ showAlert("there is a error inside the database");return null;}


    }

    static Enseignant   load_curent_Enseignant(String IdEnseignant , int year, int month) throws SQLException {
        System.out.println("Please fuckkkkkkkkkkkkkkk"+year+""+month);
        Enseignant enseignant = get_Enseignant(IdEnseignant);
        Grade grade= getGrade(IdEnseignant , year, month);
        Ehclent ehclent= getEchlon( IdEnseignant,  year, month);
        enseignant.setEchlont(ehclent.ehclent);
        enseignant.setGrade(grade.grade);
        housing house=  getResidence( IdEnseignant,  year, month);
        enseignant.setAdresse( house.getAdresse());
        enseignant.setResidence_type(house.getResidence_type());
        extra_job job;
        if((job=  getJob( IdEnseignant, year, month))!=null){
            enseignant.setExtra_job(job.getJob_name());
            enseignant.setExtra_job_bonus(job.getJob_bonus());}else { enseignant.setExtra_job(null);
            enseignant.setExtra_job_bonus(0);}


//for (int i = 1; i <12;i++){ job=  getJob( IdEnseignant, 2024, i);
//    if(job!=null)
//System.out.println(i+":"+job.getJob_name()+"("+job.getJob_bonus()+")");
//}


            enseignant.setKidsList(getKids(IdEnseignant,  year, month));



        if (!getKids(IdEnseignant,  year, month).isEmpty()) {    StringBuilder details = new StringBuilder();

            details.append("\nKids Details:\n");
            details.append("-------------\n");
            for (kids kid : getKids(IdEnseignant,  year, month)) {
                details.append("Name: ").append(kid.getNom()).append(" ").append(kid.getPrenom()).append("\n");
                details.append("Sex: ").append(kid.getSex()).append("\n");
                details.append("Date of Birth: ").append(kid.getDateNaissance()).append("\n");
                details.append("Condition: ").append(kid.isCondition() ? "Yes" : "No").append("\n");
                details.append("Adopted: ").append(kid.isAdoupted() ? "Yes" : "No").append("\n");
                details.append("\n");
                details.toString();
            }}else{ System.out.println("Please fuckkkkkkkkkkkkkkk");}











            enseignant.setPartner(getPartner(IdEnseignant,  year, month));
            if(enseignant.partner!=null)
                enseignant.partner.setWork(get_job_status( enseignant.partner.getId(),  year, month));


        enseignant.monthly_absense=getNombre_of_absence(IdEnseignant, year, month);
        String[] compteInfo = getCompteInfo(enseignant.getIdEnseignant());
        enseignant.setNuméroDeCcp(compteInfo[1]);
        enseignant.settypeDeCcp(compteInfo[0]);
        enseignant.printDetails();
        return enseignant;



    }

    private int getYearFromYearMonth(int yearMonth) {
        String yearMonthStr = String.format("%06d", yearMonth);  // Ensure it's a 6-digit string
        return Integer.parseInt(yearMonthStr.substring(0, 4));  // First four characters
    }

    private int getMonthFromYearMonth(int yearMonth) {
        String yearMonthStr = String.format("%06d", yearMonth);  // Ensure it's a 6-digit string
        return Integer.parseInt(yearMonthStr.substring(4, 6));  // Last two characters
    }






    String[] calculateSalarys_past(String idEnseignant, int year, int month) throws IOException {
        ArrayList<int[]> specificDetails = get_details_static(year, month);

        double Zone = 0;
        double Index = 0;
        double Percentage = 0;
        for (int[] detail : specificDetails) {
            System.out.println("Zone: " + detail[0] + ", Index: " + detail[1] + ", Percentage for EXP: " + detail[2]);
            Zone = (double) detail[0];
            Index = (double) detail[1];
            Percentage = (double) detail[2];
        }
        if (Index == 0 || Zone == 0 || Percentage == 0) {
            showAlert("there isnt any recent static details(Zone/Index/Percentage for EXP)");
            return null;
        }
        try {
            getIRGFile(year, month);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        Enseignant enseignant = null;
        try {
            enseignant = load_curent_Enseignant(idEnseignant, year, month);
            System.out.println("" + idEnseignant);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }



        List<Double> teacherDetails = getTeacherDetails_past(enseignant.getGrade(), enseignant.getEchlont(),year,month);



        if (teacherDetails == null) {
            showAlert("there isnt any details for this grade (" + enseignant.getGrade() + ") please add them");
            return null;
        }

        // Extract details from the fetched list
        double basicSalary = teacherDetails.get(0);
        double echelonValue = teacherDetails.get(1);
        double encadrement = teacherDetails.get(2);
        double documentation = teacherDetails.get(3);
        double clification_scen = teacherDetails.get(4);

        List<String> steps = new ArrayList<>();
        steps.add(enseignant.getNom()); // Index 0
        steps.add(enseignant.getPrenom()); // Index 1

        // Index 2
        if (enseignant.grade.equals("PR")){
            steps.add("PROFESSEUR");
        } else if (enseignant.grade.equals("AB")){
            steps.add("MAITRE DE ASSISTANT(B)");
        } else if (enseignant.grade.equals("AA")){
            steps.add("MAITRE DE ASSISTANT(A)");
        } else if (enseignant.grade.equals("CA")){
            steps.add("MAITRE DE CONFIRANCE (A)");
        } else if (enseignant.grade.equals("CB")){
            steps.add("MAITRE DE CONFIRANCE (B)");
        } else {
            steps.add("error");
        }

        // Index 3
        if (enseignant.grade.equals("PR")){
            steps.add("7");
        } else if (enseignant.grade.equals("AB")){
            steps.add("1");
        } else if (enseignant.grade.equals("AA")){
            steps.add("3");
        } else if (enseignant.grade.equals("CA")){
            steps.add("6");
        } else if (enseignant.grade.equals("CB")){
            steps.add("4");
        } else {
            steps.add("error");
        }

        steps.add("" + enseignant.echlont); // Index 4

        // Index 5
        String S_FAM;
        int ALLOCATION_FAMILIAL = 0;
        int kidsnb = 0;
        for (kids child : enseignant.getKidsList()) {
            if (((child.getSex().equals("M") && child.getAge() < 18 && child.isCondition())
                    || (child.getSex().equals("M") && child.getAge() < 21 && child.isCondition())
                    || (child.getSex().equals("F") && !child.isCondition())) && (!child.isAdoupted())) {
                ALLOCATION_FAMILIAL += 300;
                kidsnb++;
            }
        }
        double kidsbn = ALLOCATION_FAMILIAL;
        int xxx = 0;
        if (!enseignant.getSex().equals("F") && enseignant.getPartner() != null && enseignant.getPartner().isWork()) {
            ALLOCATION_FAMILIAL += 1500;
            xxx = 1500;
        }

        if (enseignant.getPartner() != null) {
            S_FAM = "M/";
        } else if (enseignant.getPartner() == null && enseignant.getKidsList().isEmpty()) {
            S_FAM = "D/";
        } else {
            S_FAM = "D/";
        }
        S_FAM = S_FAM + "" + kidsnb;
        steps.add(S_FAM); // Index 5

        // Index 6
        boolean husband = false;
        String husband_ccp = "non";
        if (enseignant.married && enseignant.getSex().equals("F") && (enseignant.gettypeDeCcp() != null)) {
            String[] splitStrings = split(enseignant.numéroDeCcp);
            System.out.println("s1: " + splitStrings[0]);
            steps.add(splitStrings[0]);
            husband = Boolean.parseBoolean(splitStrings[splitStrings.length - 1]);
            if (husband) {
                husband_ccp = splitStrings[0];
            }
        } else {
            steps.add(enseignant.numéroDeCcp);
        }

        // Calculate the salary components
        double salaireDeBase = basicSalary * Index;
        steps.add(String.format("%.2f", salaireDeBase)); // Index 7

        double IEP = echelonValue * Index; // Assuming IEP calculation
        steps.add(String.format("%.2f", IEP)); // Index 8


        double salarbasic = salaireDeBase + IEP;

        double zone = salarbasic * (Zone / 100.0);
        steps.add(String.format("%.2f", zone)); // Index 9

        double responsabilite = enseignant.extra_job_bonus; // Define how postsup is calculated
        steps.add(String.format("%.2f", responsabilite)); // Index 10

        double exp = (enseignant.getEchlont() * Percentage) * salaireDeBase / 100;
        steps.add(String.format("%.2f", exp)); // Index 11
        steps.add(String.format("%.2f", documentation)); // Index 12

        encadrement = (encadrement * salarbasic) / 100;
        clification_scen = (clification_scen * salarbasic) / 100;
        steps.add(String.format("%.2f", encadrement)); // Index 13
        steps.add(String.format("%.2f", clification_scen)); // Index 14

        double LOGEMENT = 0;
        if (enseignant.getResidence_type() == '-') {
            LOGEMENT = -4338.96;
        } else if (enseignant.getResidence_type() == '+') {
            LOGEMENT = 1500;
        }
        steps.add(String.format("%.2f", LOGEMENT)); // Index 15

        steps.add("" + ALLOCATION_FAMILIAL); // Index 16

        double brut_general = salarbasic + zone + responsabilite + exp + documentation + encadrement + clification_scen + LOGEMENT + ALLOCATION_FAMILIAL;
        steps.add(String.format("%.2f", brut_general)); // Index 17

        double as = 0.09 * brut_general;
        steps.add(String.format("%.2f", as)); // Index 18

        double brut_BASE_irg = brut_general - as;

        // Truncate the last digit to get a clean number
        brut_BASE_irg = ((int) (brut_BASE_irg / 10)) * 10;

        // Table lookup for IRG based on brut_imposable
        double irg = get_IRG(brut_BASE_irg, irgfile2); // Look up the IRG value from the table based on brut_imposable
        steps.add(String.format("%.2f", irg)); // Index 19
        steps.add("" + 00000); // Index 20

        double net = brut_general - irg - as - LOGEMENT;

        double a = (net / 30);
        a = enseignant.monthly_absense * a;
        net = net - a;
        steps.add(String.format("%.2f", net)); // Index 21

        steps.add("" + kidsnb); // Index 22

        if (enseignant.partner == null && enseignant.getSex().equals("F") && enseignant.isMarried() && husband) {
            steps.add("has a husband (his bank details: " + husband_ccp + ")"); // Index 23
            steps.add("1"); // Index 24
        }
        else if (enseignant.partner == null && enseignant.getSex().equals("F") && enseignant.isMarried() && !husband) {
            steps.add("has a husband (his bank details: " + husband_ccp + ")"); // Index 23
            steps.add("1"); // Index 24
        }
        else if (enseignant.getSex().equals("F") && !enseignant.isMarried()) {
            steps.add("non " + husband_ccp + ")"); // Index 23
            steps.add("0"); // Index 24
        }
        else if (enseignant.partner != null&& enseignant.getSex().equals("M") && enseignant.isMarried()) {
            steps.add("" + enseignant.partner.getNom() + " " + enseignant.partner.getPrenom()); // Index 23
            steps.add(enseignant.partner.isWork() ? "1" : "0"); // Index 24
        }
        else {
            steps.add("NON"); // Index 23
            steps.add("0"); // Index 24
        }

        steps.add(enseignant.isMarried() ? "0" : "1"); // Index 25

        if (enseignant.extra_job != null || enseignant.extra_job_bonus != 0) {
            steps.add("" + enseignant.extra_job + " (" + enseignant.extra_job_bonus + ")"); // Index 26
        } else {
            steps.add("NON (0)"); // Index 26
        }

        steps.add("" + kidsnb); // Index 27
        steps.add("" + kidsbn); // Index 28
        steps.add("" + xxx); // Index 29

        return steps.toArray(new String[0]);
    }





    @FXML
    private void calculateSalary_getSelectedEnseignantId_past() throws IOException, SQLException {
        String id= getSelectedEnseignantId();

        document=true;
        past=true;
        int year=0;
        int month=0;
        String selectedMonthYear = monthComboBox.getValue();

        if (selectedMonthYear != null && selectedMonthYear.equals("current") ) {

        }
      else  if (selectedMonthYear != null && !selectedMonthYear.equals("current") ) {

            int result = generateResult(selectedMonthYear);
            month=getMonthFromYearMonth(result);
            year=getYearFromYearMonth(result);

        }
        else{  showAlert("you didnt select any specific Month and Year"); return;}
        if (id != null) {String[] cla_deyails;
           if (!selectedMonthYear.equals("current")) cla_deyails=calculateSalarys_past(id,year,month);
           else cla_deyails=calculateSalarys(id);
            change( cla_deyails);




            printstuff_full_name.setText(cla_deyails[0] + " " + cla_deyails[1]);
            printstuff_grade.setText(cla_deyails[2]);
            printstuff_num_echlent.setText(cla_deyails[4]);
            printstuff_job.setText(cla_deyails[26]);
            printstuff_married.setSelected(!Boolean.parseBoolean(cla_deyails[25]));
            printstuff_final_salary.setText(cla_deyails[21]);

            printstuff_RETENUE_LOGEMENT.setText(cla_deyails[20]);
            printstuff_I_R_G.setText(cla_deyails[19]);

            printstuff_A_S.setText(cla_deyails[18]);
            printstuff_BRUT.setText(cla_deyails[17]);
            printstuff_ALLOCATION_FAMILIAL.setText(cla_deyails[16]);
            printstuff_LOGEMENT.setText(cla_deyails[15]);
            printstuff_CAl_SCIENTIFIQUE.setText(cla_deyails[14]);
            printstuff_ENCADREMENT.setText(cla_deyails[13]);
            printstuff_DOCUMENTATION.setText(cla_deyails[12]);
            printstuff_RESPONSABILITE.setText(cla_deyails[10]);
            printstuff_IND_Q_POSTE.setText(cla_deyails[11]);
            printstuff_I_E_P.setText(cla_deyails[8]);
            printstuff_SALAIRE_DE_BASE.setText(cla_deyails[7]);
            printstuff_ccp.setText(cla_deyails[6]);

            printstuff_kids_number.setText(cla_deyails[22]);
            printstuff_wife_work.setSelected(Boolean.parseBoolean(cla_deyails[24]));
            printstuff_wife_bonus.setText(cla_deyails[29]);
            printstuff_wife_full_name.setText(cla_deyails[23]);
            printstuff_S_FAM.setText(cla_deyails[5]);
            printstuff_kids_bonus.setText(cla_deyails[28]);






















            setPrintstuff_page();
        } else {
            showAlert("you didnt select any Enseignant from the table");
        }
    }



    public static Calendar convertDatePickerToCalendar(DatePicker datePicker) {
        Calendar calendar = null;
        if (datePicker != null) {
            LocalDate localDate = datePicker.getValue();
            if (localDate != null) {
                calendar = Calendar.getInstance();
                calendar.setTime(java.util.Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            }
        }
        return calendar;
    }

}

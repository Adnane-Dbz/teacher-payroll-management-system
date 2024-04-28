package org.example.max;

import java.io.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

import static org.example.max.DatabaseConfigController.loadConfig;
import static org.example.max.LoginController.database_check;
import static org.example.max.work_space.irgfile;
import static org.example.max.work_space.irgfile2;


public class TypeExtraPostManager {

    // JDBC URL for database connection
 public static  String URL = "jdbc:mysql://127.0.0.1:3306/salary?user=root&password=qwedsa";

 static void seturl(){
     Properties config = loadConfig();
     String user=config.getProperty("user", "");
     String password=config.getProperty("password", "");
     String port=config.getProperty("port", "3306");
     URL="jdbc:mysql://127.0.0.1:" + port + "/salary?user=" + user + "&password=" + password;



 }



    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        Properties config = loadConfig();
        String user=config.getProperty("user", "");
        String password=config.getProperty("password", "");
        String port=config.getProperty("port", "3306");
        return DriverManager.getConnection("jdbc:mysql://127.0.0.1:" + port + "/salary?user=" + user + "&password=" + password);

    }


    static int getDaysOfCongeDeMaterniteInWorkPeriods(String idEnseignant, int year, int month) {
        int[] absences = new int[12]; // Array to store absences for each month

        // Determine the start and end dates for the monthly period based on the year and month
        LocalDate startDate = LocalDate.of(year, month, 20);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);

        // Adjust the year if crossing into the next or previous year
        if (month == 12) {
            endDate = LocalDate.of(year + 1, 1, 19); // 19th to account for the 20th start of next month
        } else if (month == 1) {
            startDate = LocalDate.of(year - 1, 12, 20); // 20th December of previous year
        }

        // Query to get the CongeDeMaternite periods for the given IdEnseignant
        String query = "SELECT effective_start_date_conge_de_maternite, effective_end_date_conge_de_maternite " +
                "FROM conge_de_maternite " +
                "WHERE Id_Enseignant = ? " +
                "AND effective_end_date_conge_de_maternite >= ? " +
                "AND effective_start_date_conge_de_maternite <= ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, idEnseignant);
            stmt.setDate(2, Date.valueOf(startDate));
            stmt.setDate(3, Date.valueOf(endDate));

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                LocalDate congeStartDate = rs.getDate("effective_start_date_conge_de_maternite").toLocalDate();
                LocalDate congeEndDate = rs.getDate("effective_end_date_conge_de_maternite").toLocalDate();

                // Calculate overlap between the monthly work period and CongeDeMaternite period
                LocalDate overlapStartDate = congeStartDate.isAfter(startDate) ? congeStartDate : startDate;
                LocalDate overlapEndDate = congeEndDate.isBefore(endDate) ? congeEndDate : endDate.plusDays(1);

                int daysInOverlap = (int) overlapStartDate.datesUntil(overlapEndDate).count();

                // Determine which months overlap falls into and accumulate absences
                LocalDate currentStartDate = startDate;
                LocalDate currentEndDate = YearMonth.of(year, month).atEndOfMonth().minusDays(1);
                for (int i = 0; i < 12; i++) {
                    if (overlapStartDate.isBefore(currentEndDate) && overlapEndDate.isAfter(currentStartDate)) {
                        absences[i] += daysInOverlap;
                    }
                    currentStartDate = currentStartDate.plusMonths(1).withDayOfMonth(20);
                    currentEndDate = currentStartDate.plusMonths(1).minusDays(1);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Return the number of absences for the specified month
        return absences[month - 1];
    }  // Sample usage
    static boolean isinCongeDeMaterniteIntoday(String idEnseignant) {
        LocalDate today = LocalDate.now(); // Get today's date

        String query = "SELECT COUNT(*) AS count FROM conge_de_maternite " +
                "WHERE Id_Enseignant = ? " +
                "AND effective_start_date_conge_de_maternite <= ? " +
                "AND effective_end_date_conge_de_maternite >= ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, idEnseignant);
            stmt.setDate(2, Date.valueOf(today));
            stmt.setDate(3, Date.valueOf(today));

            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getInt("count") > 0) {
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }




    public static LocalDate getOldestStartDate() {
        String query = "SELECT MIN(effective_start_date_Enseignant) AS oldest_start_date FROM Enseignant";

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            if (resultSet.next()) {
                return resultSet.getDate("oldest_start_date").toLocalDate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null; // Return null if no date is found or an exception occurs
    }

    public static LocalDate getOldestStartDate(String id) throws SQLException {
        // SQL query to get the oldest effective_start_date_Enseignant for the given Id_Enseignant
        String sql = "SELECT MIN(effective_start_date_Enseignant) AS oldest_date FROM Enseignant WHERE Id_Enseignant = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Set the id parameter in the query
            pstmt.setString(1, id);

            // Execute the query and retrieve the result
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Get the oldest date from the result set
                    Date oldestDate = rs.getDate("oldest_date");
                    // Convert the SQL Date to LocalDate and return it
                    return oldestDate != null ? oldestDate.toLocalDate() : null;
                }
            }
        }
        // Return null if no result found
        return null;
    }



    public static void initializeDatabase() {
        if(!databaseExists("salary")){
        String sqlScript = """
          CREATE DATABASE IF NOT EXISTS salary;
                  USE salary;
                  
                  CREATE TABLE faculte (
                 nom_faculte VARCHAR(250) PRIMARY KEY NOT NULL
                                  );
                                 
                                  CREATE TABLE departement (
                                      nom_departement VARCHAR(150) PRIMARY KEY NOT NULL,
                                      nom_faculte VARCHAR(250),
                                      FOREIGN KEY (nom_faculte) REFERENCES faculte(nom_faculte)
                                  );
                                 
                                  CREATE TABLE Enseignant (
                                      Id_Enseignant VARCHAR(60) PRIMARY KEY NOT NULL,
                                      nom_ensg VARCHAR(50) NOT NULL,
                                      prenom_ensg VARCHAR(50) NOT NULL,
                                      date_de_naissance_ensg DATE NOT NULL,
                                      sex_ensg CHAR(1) NOT NULL,
                                      situation_sociale VARCHAR(10) NOT NULL,
                                      adresse_email VARCHAR(50),
                                      annee_de_bac INT NOT NULL,
                                      Tel BIGINT NOT NULL,
                                      effective_start_date_Enseignant DATE NOT NULL,
                                      effective_end_date_Enseignant DATE,
                                      nom_departement VARCHAR(150),
                                      FOREIGN KEY (nom_departement) REFERENCES departement(nom_departement)
                                  );
                                 
                                  CREATE TABLE Grade (
                                      id_Grade INT AUTO_INCREMENT PRIMARY KEY NOT NULL,
                                      nom_Grade VARCHAR(2) NOT NULL,
                                      effective_start_date_Grade DATE NOT NULL,
                                      effective_end_date_Grade DATE,
                                      Id_Enseignant VARCHAR(60),
                                      FOREIGN KEY (Id_Enseignant) REFERENCES Enseignant(Id_Enseignant),
                                      CHECK (nom_Grade IN ('AB', 'AA', 'CB', 'CA', 'PR'))
                                  );
                                 
                                  CREATE TABLE echelon (
                                      id_echelon INT AUTO_INCREMENT PRIMARY KEY NOT NULL,
                                      nom_echelon INT NOT NULL,
                                      effective_start_date_echelon DATE NOT NULL,
                                      effective_end_date_echelon DATE,
                                      Id_Enseignant VARCHAR(60),
                                      FOREIGN KEY (Id_Enseignant) REFERENCES Enseignant(Id_Enseignant),
                                      CHECK (nom_echelon >= 0 AND nom_echelon <= 12)
                                  );
                                 
                                  CREATE TABLE users (
                                              id INT AUTO_INCREMENT PRIMARY KEY,
                                              username VARCHAR(50) NOT NULL UNIQUE,
                                              password VARCHAR(50) NOT NULL,
                                              lastname VARCHAR(50) NOT NULL,
                                              question_number INT NOT NULL,
                                              answer VARCHAR(50) NOT NULL
                                          );
                                 
                                  CREATE TABLE type_compt (
                                      nom_type VARCHAR(15) PRIMARY KEY NOT NULL
                                  );
                                
                                  CREATE TABLE residence (
                                      id_residence INT AUTO_INCREMENT PRIMARY KEY NOT NULL,
                                      adresse VARCHAR(170) NOT NULL,
                                      residence_type VARCHAR(1),
                                      effective_start_date_residence DATE NOT NULL,
                                      effective_end_date_residence DATE,
                                      Id_Enseignant VARCHAR(60),
                                      FOREIGN KEY (Id_Enseignant) REFERENCES Enseignant(Id_Enseignant)
                                  );
                                 
                                  CREATE TABLE Compt_bancaire (
                                      id_compte INT AUTO_INCREMENT PRIMARY KEY NOT NULL,
                                      Compt_bancaire_details VARCHAR(170) NOT NULL,
                                      Id_Enseignant VARCHAR(60),
                                      nom_type VARCHAR(15),
                                      FOREIGN KEY (Id_Enseignant) REFERENCES Enseignant(Id_Enseignant),
                                      FOREIGN KEY (nom_type) REFERENCES type_compt(nom_type)
                                  );
                                 
                                  CREATE TABLE Epoux (
                                      id_Epoux INT AUTO_INCREMENT PRIMARY KEY NOT NULL,
                                      nom_epoux VARCHAR(50) NOT NULL,
                                      prenom_epoux VARCHAR(50) NOT NULL,
                                      date_de_naissance_epoux DATE NOT NULL,
                                      sex_epoux CHAR(1) NOT NULL,
                                      effective_start_date_Epoux DATE NOT NULL,
                                      effective_end_date_Epoux DATE,
                                      Id_Enseignant VARCHAR(60),
                                      FOREIGN KEY (Id_Enseignant) REFERENCES Enseignant(Id_Enseignant)
                                  );
                                 
                                  CREATE TABLE Epoux_travaille (
                                      id_work INT AUTO_INCREMENT PRIMARY KEY NOT NULL,
                                      id_Epoux INT,
                                      work TINYINT NOT NULL,
                                      effective_start_date_Epoux_travaille DATE NOT NULL,
                                      effective_end_date_Epoux_travaille DATE,
                                      FOREIGN KEY (id_Epoux) REFERENCES Epoux(id_Epoux)
                                  );
                                 
                                  CREATE TABLE Enfant (
                                      id_Enfant INT AUTO_INCREMENT PRIMARY KEY NOT NULL,
                                      nom_enfant VARCHAR(50) NOT NULL,
                                      Adoptde BOOLEAN NOT NULL,
                                      condition_Enfant BOOLEAN NOT NULL,
                                      prenom_enfant VARCHAR(50) NOT NULL,
                                      date_de_naissance_enfant DATE NOT NULL,
                                      sex_enfant CHAR(1) NOT NULL,
                                      effective_start_date_Enfant DATE NOT NULL,
                                      effective_end_date_Enfant DATE,
                                      Id_Enseignant VARCHAR(60),
                                      FOREIGN KEY (Id_Enseignant) REFERENCES Enseignant(Id_Enseignant)
                                  );
                 
                                  CREATE TABLE Absent (
                                      id_Absent INT AUTO_INCREMENT PRIMARY KEY NOT NULL,
                                      nombre INT ,
                                      effective_monthly_Absent DATE,
                                      Id_Enseignant VARCHAR(60),
                                      FOREIGN KEY (Id_Enseignant) REFERENCES Enseignant(Id_Enseignant)
                                  );
                                  
                                     CREATE TABLE conge_de_maternite (
                                      id_conge_de_maternite INT AUTO_INCREMENT PRIMARY KEY NOT NULL,
                                      effective_start_date_conge_de_maternite DATE NOT NULL,
                                      effective_end_date_conge_de_maternite DATE,
                                      Id_Enseignant VARCHAR(60),
                                      FOREIGN KEY (Id_Enseignant) REFERENCES Enseignant(Id_Enseignant)
                                  );
                                 
                                  --
                                  CREATE TABLE salary_info (
                                      salary_id INT AUTO_INCREMENT PRIMARY KEY,
                                      nom_Grade VARCHAR(2) NOT NULL,
                                      basic_salary DECIMAL(10, 2) NOT NULL,
                                      echelon_1 INT NOT NULL,
                                      echelon_2 INT NOT NULL,
                                      echelon_3 INT NOT NULL,
                                      echelon_4 INT NOT NULL,
                                      echelon_5 INT NOT NULL,
                                      echelon_6 INT NOT NULL,
                                      echelon_7 INT NOT NULL,
                                      echelon_8 INT NOT NULL,
                                      echelon_9 INT NOT NULL,
                                      echelon_10 INT NOT NULL,
                                      echelon_11 INT NOT NULL,
                                      echelon_12 INT NOT NULL,
                                      encadrement INT NOT NULL,
                                      documentation INT NOT NULL,
                                      responsabilite INT NOT NULL,
                                      effective_start_date_salary_info DATE NOT NULL,
                                      effective_end_date_salary_info DATE,
                                      CONSTRAINT chk_basic_salary CHECK (basic_salary >= 0)
                                  );
                                  --
                                  CREATE TABLE details_static (
                                      details_static_id INT AUTO_INCREMENT PRIMARY KEY,
                                      point_index INT NOT NULL,
                                      prime_zone INT NOT NULL,
                                      persantage_for_EXP INT NOT NULL,
                                      effective_start_date_details_static DATE NOT NULL,
                                      effective_end_date_details_static DATE
                                  );
                                  --
                                  CREATE TABLE type_extra_post (
                                  id_type_extra_post INT AUTO_INCREMENT PRIMARY KEY NOT NULL,
                                  nom_Extra_post VARCHAR(50) ,
                                  bonus INT,
                                   effective_start_date_type_extra_post DATE NOT NULL,
                                   effective_end_date_type_extra_post DATE
                                  );
          
                                  --
                                  CREATE TABLE Extra_post_for_each_Enseignant (
                                      id_Extra_post INT AUTO_INCREMENT PRIMARY KEY NOT NULL,
                                      nom_Extra_post VARCHAR(50),
                                      Id_Enseignant VARCHAR(60),
                                      id_type_extra_post INT,
                                      effective_start_date_Extra_post_for_each_Enseignant DATE NOT NULL,
                                      effective_end_date_Extra_post_for_each_Enseignant DATE,
                                      FOREIGN KEY (Id_Enseignant) REFERENCES Enseignant(Id_Enseignant),
                                      FOREIGN KEY (id_type_extra_post) REFERENCES type_extra_post(id_type_extra_post)
                                     );
                                
                                
                                  --
                                  CREATE TABLE table_IRG (
                                      document_id INT AUTO_INCREMENT PRIMARY KEY,
                                      document_content LONGTEXT,
                                      effective_start_date_table_IRG DATE NOT NULL,
                                      effective_end_date_table_IRG DATE
                                  );     
                  
                  
        """;
            Properties config = loadConfig();
            String user = config.getProperty("user", "");
            String password = config.getProperty("password", "");
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/?user="+user+"&password="+password);
             Statement stmt = conn.createStatement()) {
            for (String sql : sqlScript.split(";")) {
                if (!sql.trim().isEmpty()) {
                    stmt.execute(sql);
                }
            }
            System.out.println("Database and tables created successfully.");
        } catch (SQLException e) {
            database_check();

            e.printStackTrace();
        }
        }

    }



    public static boolean databaseExists(String databaseName) {
        Properties config = loadConfig();
        String user=config.getProperty("user", "");
        String password=config.getProperty("password", "");
        String port=config.getProperty("port", "3306");
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:" + port + "/?user=" + user + "&password=" + password);
             Statement stmt = conn.createStatement()) {

            // Check if the database exists
            String sql = "SHOW DATABASES LIKE '" + databaseName + "'";
            ResultSet resultSet = stmt.executeQuery(sql);

            // If the resultSet has any rows, the database exists
            return resultSet.next();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }











     public static void deleteAllFromEnseignant() throws SQLException {
         String[] relatedTables = {
                 "Enfant", "Epoux_travaille", "Epoux", "Compt_bancaire",
                 "residence", "echelon", "Grade", "Extra_post_for_each_Enseignant",
                 "conge_de_maternite", "Absent"
         };

         try (Connection connection = getConnection();
              Statement stmt = connection.createStatement()) {

             // Disable foreign key checks to avoid constraint violations during deletion
             stmt.executeUpdate("SET FOREIGN_KEY_CHECKS = 0");

             // Delete from related tables first
             for (String table : relatedTables) {
                 int rowsAffected = stmt.executeUpdate("DELETE FROM " + table);
                 System.out.println("Deleted " + rowsAffected + " rows from " + table);
             }

             // Delete from Enseignant table
             int enseignantRowsAffected = stmt.executeUpdate("DELETE FROM Enseignant");
             System.out.println("Deleted " + enseignantRowsAffected + " rows from Enseignant");

             // Enable foreign key checks after deletion
             stmt.executeUpdate("SET FOREIGN_KEY_CHECKS = 1");
         }
     }
    public static void deleteEnseignant(String id) throws SQLException {
        String[] relatedTables = {
                "Enfant", "Epoux_travaille", "Epoux", "Compt_bancaire",
                "residence", "echelon", "Grade", "Extra_post_for_each_Enseignant",
                "conge_de_maternite", "Absent"
        };

        try (Connection connection = getConnection();
             Statement stmt = connection.createStatement()) {

            // Disable foreign key checks to avoid constraint violations during deletion
            stmt.executeUpdate("SET FOREIGN_KEY_CHECKS = 0");

            // Delete from related tables
            for (String table : relatedTables) {
                int rowsAffected = stmt.executeUpdate("DELETE FROM " + table + " WHERE Id_Enseignant = '" + id + "'");
                System.out.println("Deleted " + rowsAffected + " rows from " + table + " for Id_Enseignant: " + id);
            }

            // Delete from Enseignant table
            int enseignantRowsAffected = stmt.executeUpdate("DELETE FROM Enseignant WHERE Id_Enseignant = '" + id + "'");
            System.out.println("Deleted " + enseignantRowsAffected + " rows from Enseignant for Id_Enseignant: " + id);

            // Enable foreign key checks after deletion
            stmt.executeUpdate("SET FOREIGN_KEY_CHECKS = 1");
        }
    }



    public static List<String> getDepartements(String faculte) {
        List<String> departments = new ArrayList<>();
        try (Connection conn = getConnection()) {
            String query = "SELECT nom_departement FROM departement WHERE nom_faculte = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, faculte);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                departments.add(rs.getString("nom_departement"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return departments;
    }

    public static boolean canDeleteDepartment(String departement) {
        boolean canDelete = true;
        try (Connection conn = getConnection()) {
            String query = "SELECT COUNT(*) AS count FROM Enseignant WHERE nom_departement = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, departement);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next() && rs.getInt("count") > 0) {
                canDelete = false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return canDelete;
    }

    public static void deleteDepartment(String departement) {
        try (Connection conn = getConnection()) {
            String query = "DELETE FROM departement WHERE nom_departement = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, departement);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }













    //to decide if we crasi or archife
    public static boolean isWithinSamePeriod(Calendar prevCalendar, Calendar currentCalendar) {

        if (prevCalendar.get(Calendar.YEAR) == currentCalendar.get(Calendar.YEAR) &&
                prevCalendar.get(Calendar.MONTH) == currentCalendar.get(Calendar.MONTH)) {

            if ((prevCalendar.get(Calendar.DAY_OF_MONTH) >= 1 && prevCalendar.get(Calendar.DAY_OF_MONTH) <= 20) &&
                    (currentCalendar.get(Calendar.DAY_OF_MONTH) >= 1 && currentCalendar.get(Calendar.DAY_OF_MONTH) <= 20)) {
                return true;
            }
            if ((prevCalendar.get(Calendar.DAY_OF_MONTH) > 20 && prevCalendar.get(Calendar.DAY_OF_MONTH) <= prevCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)) &&
                    (currentCalendar.get(Calendar.DAY_OF_MONTH) > 20 || currentCalendar.get(Calendar.DAY_OF_MONTH) == 1)) {
                return true;
            }
        } else if (prevCalendar.get(Calendar.YEAR) == currentCalendar.get(Calendar.YEAR) &&
                (prevCalendar.get(Calendar.MONTH) == currentCalendar.get(Calendar.MONTH) + 1 ||
                        (prevCalendar.get(Calendar.MONTH) == Calendar.DECEMBER && currentCalendar.get(Calendar.MONTH) == Calendar.JANUARY))) {
            if (prevCalendar.get(Calendar.DAY_OF_MONTH) > 20 && currentCalendar.get(Calendar.DAY_OF_MONTH) < 20) {
                return true;
            }
        }
        return false;
    }

    public static boolean addJob(String Id_Enseignant, String nom_Extra_post) {
        try (Connection conn =getConnection()) {
            Calendar currentCalendar = Calendar.getInstance();
            Calendar prevCalendar = Calendar.getInstance();

            // Fetch the latest effective_end_date_Extra_post_for_each_Enseignant for the given Id_Enseignant and nom_Extra_post
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT effective_start_date_Extra_post_for_each_Enseignant " +
                            "FROM Extra_post_for_each_Enseignant " +
                            "WHERE Id_Enseignant = ? AND nom_Extra_post = ? AND effective_end_date_Extra_post_for_each_Enseignant IS NULL " +
                            "ORDER BY effective_start_date_Extra_post_for_each_Enseignant DESC LIMIT 1");
            stmt.setString(1, Id_Enseignant);
            stmt.setString(2, nom_Extra_post);
            ResultSet rs = stmt.executeQuery();

            // Check if there's an existing record
            if (rs.next()) {
                // Extract the previous start date to check if it falls within the same period
                Date prevStartDate = rs.getDate("effective_start_date_Extra_post_for_each_Enseignant");
                prevCalendar.setTime(prevStartDate);

                if (isWithinSamePeriod(prevCalendar, currentCalendar)) {
                    // Update the existing record with the current date
                    PreparedStatement updateStmt = conn.prepareStatement(
                            "UPDATE Extra_post_for_each_Enseignant " +
                                    "SET effective_start_date_Extra_post_for_each_Enseignant = ? " +
                                    "WHERE Id_Enseignant = ? AND nom_Extra_post = ? AND effective_end_date_Extra_post_for_each_Enseignant IS NULL");
                    updateStmt.setDate(1, new Date(System.currentTimeMillis()));
                    updateStmt.setString(2, Id_Enseignant);
                    updateStmt.setString(3, nom_Extra_post);
                    updateStmt.executeUpdate();
                    return true;
                } else {
                    // Close the result set and statement
                    rs.close();
                    stmt.close();

                    // Set the previous record's end date to the current date
                    PreparedStatement endStmt = conn.prepareStatement(
                            "UPDATE Extra_post_for_each_Enseignant " +
                                    "SET effective_end_date_Extra_post_for_each_Enseignant = ? " +
                                    "WHERE Id_Enseignant = ? AND nom_Extra_post = ? AND effective_end_date_Extra_post_for_each_Enseignant IS NULL");
                    endStmt.setDate(1, new Date(System.currentTimeMillis()));
                    endStmt.setString(2, Id_Enseignant);
                    endStmt.setString(3, nom_Extra_post);
                    endStmt.executeUpdate();
                }
            }

            // Fetch the latest id_type_extra_post for the given nom_Extra_post
            PreparedStatement typeStmt = conn.prepareStatement(
                    "SELECT id_type_extra_post FROM type_extra_post WHERE nom_Extra_post = ? ORDER BY id_type_extra_post DESC LIMIT 1");
            typeStmt.setString(1, nom_Extra_post);
            ResultSet typeRs = typeStmt.executeQuery();
            int id_type_extra_post = 0;
            if (typeRs.next()) {
                id_type_extra_post = typeRs.getInt("id_type_extra_post");
            }
            typeRs.close();
            typeStmt.close();

            // Insert a new record with the current date
            PreparedStatement insertStmt = conn.prepareStatement(
                    "INSERT INTO Extra_post_for_each_Enseignant (Id_Enseignant, nom_Extra_post, id_type_extra_post, effective_start_date_Extra_post_for_each_Enseignant) " +
                            "VALUES (?, ?, ?, ?)");
            insertStmt.setString(1, Id_Enseignant);
            insertStmt.setString(2, nom_Extra_post);
            insertStmt.setInt(3, id_type_extra_post);
            insertStmt.setDate(4, new Date(System.currentTimeMillis()));
            insertStmt.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public static List<String> getavailableExtraPostsnames() {
        List<String> uniqueExtraPosts = new ArrayList<>();

        try (Connection conn =getConnection()) {
            String sql = "SELECT DISTINCT nom_Extra_post FROM type_extra_post WHERE effective_end_date_type_extra_post IS NULL";
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String extraPost = rs.getString("nom_Extra_post");
                    uniqueExtraPosts.add(extraPost.trim());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();   }

        return uniqueExtraPosts;
    }



    public static extra_job getJob(String Id_Enseignant) {
        extra_job job = null;
        try (Connection conn =getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT e.id_Extra_post, e.nom_Extra_post, t.bonus, e.effective_start_date_Extra_post_for_each_Enseignant " +
                            "FROM Extra_post_for_each_Enseignant e " +
                            "JOIN type_extra_post t ON e.id_type_extra_post = t.id_type_extra_post " +
                            "WHERE e.Id_Enseignant = ? AND e.effective_end_date_Extra_post_for_each_Enseignant IS NULL");
            stmt.setString(1, Id_Enseignant);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("id_Extra_post");
                String job_name = rs.getString("nom_Extra_post");
                int job_bonus = rs.getInt("bonus");

                // Get the effective start date
                Date startDate = rs.getDate("effective_start_date_Extra_post_for_each_Enseignant");
                Calendar effectiveStartDate = Calendar.getInstance();
                effectiveStartDate.setTime(startDate);

                // Create the extra_job instance
                job = new extra_job(id, job_name, job_bonus);
                job.setEffectiveStartDate(effectiveStartDate);
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return job;
    }

    public static extra_job getJob(String Id_Enseignant, Calendar specific_time) {
        extra_job job = null;
        if (specific_time == null) specific_time = Calendar.getInstance();
        try (Connection conn =getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT e.id_Extra_post, e.nom_Extra_post, t.bonus, e.effective_start_date_Extra_post_for_each_Enseignant, e.effective_end_date_Extra_post_for_each_Enseignant " +
                            "FROM Extra_post_for_each_Enseignant e " +
                            "JOIN type_extra_post t ON e.id_type_extra_post = t.id_type_extra_post " +
                            "WHERE e.Id_Enseignant = ? AND ? BETWEEN e.effective_start_date_Extra_post_for_each_Enseignant AND COALESCE(e.effective_end_date_Extra_post_for_each_Enseignant, '9999-12-31')");
            stmt.setString(1, Id_Enseignant);
            stmt.setDate(2, new java.sql.Date(specific_time.getTimeInMillis()));
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("id_Extra_post");
                String job_name = rs.getString("nom_Extra_post");
                int job_bonus = rs.getInt("bonus");

                // Get the effective start date
                Date startDate = rs.getDate("effective_start_date_Extra_post_for_each_Enseignant");
                Calendar effectiveStartDate = Calendar.getInstance();
                effectiveStartDate.setTime(startDate);

                // Get the effective end date
                Date endDate = rs.getDate("effective_end_date_Extra_post_for_each_Enseignant");
                Calendar effectiveEndDate = null;
                if (endDate != null) {
                    effectiveEndDate = Calendar.getInstance();
                    effectiveEndDate.setTime(endDate);
                }

                // Check if specific_time is between effective start and end dates
                if (isWithinEffectivePeriod(specific_time, effectiveStartDate, effectiveEndDate)) {
                    job = new extra_job(id, job_name, job_bonus);
                    job.setEffectiveStartDate(effectiveStartDate);
                    job.setEffectiveEndDate(effectiveEndDate);
                }
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return job;
    }

    public static extra_job getJob(String Id_Enseignant, int year, int month) {
        extra_job job = null;
        Calendar specificTime = Calendar.getInstance();

        // Check if year and month are set to current month and year
        if (year == 0 && month == 0) {
            year = specificTime.get(Calendar.YEAR);
            month = specificTime.get(Calendar.MONTH) + 1; // Calendar.MONTH is zero-based
        }

        specificTime.set(year, month - 1, 1); // Set specific time to the first day of the given month

        try (Connection conn = getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT e.id_Extra_post, e.nom_Extra_post, t.bonus, e.effective_start_date_Extra_post_for_each_Enseignant, e.effective_end_date_Extra_post_for_each_Enseignant " +
                            "FROM Extra_post_for_each_Enseignant e " +
                            "JOIN type_extra_post t ON e.id_type_extra_post = t.id_type_extra_post " +
                            "WHERE e.Id_Enseignant = ? AND ? BETWEEN e.effective_start_date_Extra_post_for_each_Enseignant AND COALESCE(e.effective_end_date_Extra_post_for_each_Enseignant, '9999-12-31')");
            stmt.setString(1, Id_Enseignant);
            stmt.setDate(2, new java.sql.Date(specificTime.getTimeInMillis())); // Set specific time in the SQL query
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("id_Extra_post");
                String jobName = rs.getString("nom_Extra_post");
                int jobBonus = rs.getInt("bonus");

                // Get the effective start date
                Calendar startDate = Calendar.getInstance();
                startDate.setTime(rs.getDate("effective_start_date_Extra_post_for_each_Enseignant"));

                // Get the effective end date
                Calendar endDate = null;
                if (rs.getDate("effective_end_date_Extra_post_for_each_Enseignant") != null) {
                    endDate = Calendar.getInstance();
                    endDate.setTime(rs.getDate("effective_end_date_Extra_post_for_each_Enseignant"));
                }

                // Check if specificTime is between effective start and end dates
                if (isWithinEffectivePeriod(specificTime, startDate, endDate)) {
                    job = new extra_job(id, jobName, jobBonus);
                    job.setEffectiveStartDate(startDate);
                    job.setEffectiveEndDate(endDate);
                }
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return job;
    }


    private static boolean isWithinEffectivePeriod(Calendar specificTime, Calendar startDate, Calendar endDate) {
        if (startDate != null && specificTime.before(startDate)) {
            return false;
        }
        if (endDate != null && specificTime.after(endDate)) {
            return false;
        }
        return true;
    }




    public List<extra_job> getJobs(String Id_Enseignant) {
        List<extra_job> jobs = new ArrayList<>();

        try (Connection conn =getConnection();
             Statement stmt = conn.createStatement()) {
            String query = "SELECT id_Extra_post, nom_Extra_post, job_bonus, effective_start_date_Extra_post_for_each_Enseignant, effective_end_date_Extra_post_for_each_Enseignant " +
                    "FROM Extra_post_for_each_Enseignant " +
                    "WHERE Id_Enseignant = '" + Id_Enseignant + "'";

            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                int id = rs.getInt("id_Extra_post");
                String jobName = rs.getString("nom_Extra_post");
                int jobBonus = rs.getInt("job_bonus");

                // Retrieve effective start date
                Timestamp startTimestamp = rs.getTimestamp("effective_start_date_Extra_post_for_each_Enseignant");
                Calendar startCalendar = Calendar.getInstance();
                if (startTimestamp != null) {
                    startCalendar.setTimeInMillis(startTimestamp.getTime());
                }

                // Retrieve effective end date
                Timestamp endTimestamp = rs.getTimestamp("effective_end_date_Extra_post_for_each_Enseignant");
                Calendar endCalendar = Calendar.getInstance();
                if (endTimestamp != null) {
                    endCalendar.setTimeInMillis(endTimestamp.getTime());
                }

                extra_job job = new extra_job(id, jobName, jobBonus);
                job.setEffectiveStartDate(startCalendar);
                job.setEffectiveEndDate(endCalendar);

                jobs.add(job);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return jobs;
    }





    public static List<extra_job> getavailableExtraPosts() {
        List<extra_job> extraPosts = new ArrayList<>();

        try (Connection conn = getConnection()) {
            String sql = "SELECT id_type_extra_post, nom_Extra_post, bonus, effective_start_date_type_extra_post, effective_end_date_type_extra_post FROM type_extra_post WHERE effective_end_date_type_extra_post IS NULL";
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id_type_extra_post");
                    String name = rs.getString("nom_Extra_post").trim();
                    int bonus = rs.getInt("bonus");
                    Date startDate = rs.getDate("effective_start_date_type_extra_post");
                    Date endDate = rs.getDate("effective_end_date_type_extra_post");

                    Calendar startCal = Calendar.getInstance();
                    startCal.setTime(startDate);
                    Calendar endCal = null;
                    if (endDate != null) {
                        endCal = Calendar.getInstance();
                        endCal.setTime(endDate);
                    }

                    extra_job job = new extra_job(id, name, bonus);
                    job.setEffectiveStartDate(startCal);
                    job.setEffectiveEndDate(endCal);

                    extraPosts.add(job);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return extraPosts;
    }


    public static void addResidence(String Id_Enseignant, String adresse, char residence_type) {
        try (Connection conn =getConnection()) {
            // Get the current date
            Calendar currentDate = Calendar.getInstance();

            // Check if there is an existing record with the same Id_Enseignant and no end date
            String selectQuery = "SELECT id_residence, effective_start_date_residence FROM residence WHERE Id_Enseignant = ? AND effective_end_date_residence IS NULL";
            try (PreparedStatement selectStmt = conn.prepareStatement(selectQuery)) {
                selectStmt.setString(1, Id_Enseignant);
                ResultSet resultSet = selectStmt.executeQuery();

                if (resultSet.next()) {
                    // There is an existing record with no end date
                    int residenceId = resultSet.getInt("id_residence");
                    Calendar startDate = Calendar.getInstance();
                    startDate.setTime(resultSet.getDate("effective_start_date_residence"));

                    if (isWithinSamePeriod(startDate, currentDate)) {
                        // Update the existing record
                        String updateQuery = "UPDATE residence SET adresse = ?, residence_type = ? WHERE id_residence = ?";
                        try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                            updateStmt.setString(1, adresse);
                            updateStmt.setString(2, String.valueOf(residence_type));
                            updateStmt.setInt(3, residenceId);
                            updateStmt.executeUpdate();
                        }
                    } else {
                        // The current date is outside the same period, so set the end date to current date
                        String updateQuery = "UPDATE residence SET effective_end_date_residence = ? WHERE id_residence = ?";
                        try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                            updateStmt.setDate(1, new java.sql.Date(currentDate.getTimeInMillis()));
                            updateStmt.setInt(2, residenceId);
                            updateStmt.executeUpdate();
                        }

                        // Insert a new record for the current date
                        String insertQuery = "INSERT INTO residence (adresse, residence_type, effective_start_date_residence, Id_Enseignant) VALUES (?, ?, ?, ?)";
                        try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                            insertStmt.setString(1, adresse);
                            insertStmt.setString(2, String.valueOf(residence_type));
                            insertStmt.setDate(3, new java.sql.Date(currentDate.getTimeInMillis()));
                            insertStmt.setString(4, Id_Enseignant);
                            insertStmt.executeUpdate();
                        }
                    }
                } else {
                    // There is no existing record, insert a new one
                    String insertQuery = "INSERT INTO residence (adresse, residence_type, effective_start_date_residence, Id_Enseignant) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                        insertStmt.setString(1, adresse);
                        insertStmt.setString(2, String.valueOf(residence_type));
                        insertStmt.setDate(3, new java.sql.Date(currentDate.getTimeInMillis()));
                        insertStmt.setString(4, Id_Enseignant);
                        insertStmt.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static housing getResidence(String Id_Enseignant, Calendar date) {
        housing residence = null;
        try (Connection conn =getConnection()) {
            // Construct the SQL query based on the date parameter
            String selectQuery;
            if (date == null) {
                selectQuery = "SELECT * FROM residence WHERE Id_Enseignant = ? AND effective_end_date_residence IS NULL";
            } else {
                selectQuery = "SELECT * FROM residence WHERE Id_Enseignant = ? AND effective_start_date_residence <= ? AND (effective_end_date_residence IS NULL OR effective_end_date_residence >= ?)";
            }

            // Execute the SQL query
            try (PreparedStatement selectStmt = conn.prepareStatement(selectQuery)) {
                selectStmt.setString(1, Id_Enseignant);
                if (date != null) {
                    selectStmt.setDate(2, new Date(date.getTimeInMillis()));
                    selectStmt.setDate(3, new Date(date.getTimeInMillis()));
                }
                ResultSet resultSet = selectStmt.executeQuery();

                // Process the query result
                if (resultSet.next()) {
                    int id = resultSet.getInt("id_residence");
                    String adresse = resultSet.getString("adresse");
                    char residenceType = resultSet.getString("residence_type").charAt(0);
                    residence = new housing(adresse, residenceType);
                    residence.setId(id);
                    // Set effective start and end dates
                    Calendar startDate = Calendar.getInstance();
                    startDate.setTime(resultSet.getDate("effective_start_date_residence"));
                    residence.setEffectiveStartDate(startDate);
                    Date endDate = resultSet.getDate("effective_end_date_residence");
                    if (endDate != null) {
                        Calendar endDateCalendar = Calendar.getInstance();
                        endDateCalendar.setTime(endDate);
                        residence.setEffectiveEndDate(endDateCalendar);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return residence;
    }
    public static housing getResidence(String Id_Enseignant, int year, int month) {
        housing residence = null;

        // Handle case where year and month are 0
        if (year == 0 && month == 0) {
            year = LocalDate.now().getYear();
            month = LocalDate.now().getMonthValue();
        }

        // Calculate the start and end of the period
        LocalDate startOfPeriod;
        LocalDate endOfPeriod;
        if (month == 1) {
            startOfPeriod = LocalDate.of(year - 1, 12, 20);
            endOfPeriod = LocalDate.of(year, 1, 19);
        } else {
            startOfPeriod = LocalDate.of(year, month - 1, 20);
            endOfPeriod = LocalDate.of(year, month, 19);
        }

        try (Connection conn = getConnection()) {
            // Construct the SQL query
            String selectQuery = "SELECT * FROM residence WHERE Id_Enseignant = ? AND effective_start_date_residence <= ? AND (effective_end_date_residence IS NULL OR effective_end_date_residence >= ?)";

            // Execute the SQL query
            try (PreparedStatement selectStmt = conn.prepareStatement(selectQuery)) {
                selectStmt.setString(1, Id_Enseignant);
                selectStmt.setDate(2, Date.valueOf(endOfPeriod));
                selectStmt.setDate(3, Date.valueOf(startOfPeriod));
                ResultSet resultSet = selectStmt.executeQuery();

                // Process the query result
                if (resultSet.next()) {
                    int id = resultSet.getInt("id_residence");
                    String adresse = resultSet.getString("adresse");
                    char residenceType = resultSet.getString("residence_type").charAt(0);
                    residence = new housing(adresse, residenceType);
                    residence.setId(id);

                    // Set effective start and end dates
                    Calendar startDate = Calendar.getInstance();
                    startDate.setTime(resultSet.getDate("effective_start_date_residence"));
                    residence.setEffectiveStartDate(startDate);

                    Date endDate = resultSet.getDate("effective_end_date_residence");
                    if (endDate != null) {
                        Calendar endDateCalendar = Calendar.getInstance();
                        endDateCalendar.setTime(endDate);
                        residence.setEffectiveEndDate(endDateCalendar);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return residence;
    }

    public static List<housing> getResidences(String Id_Enseignant) {
        List<housing> residences = new ArrayList<>();
        try (Connection conn =getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM residence WHERE Id_Enseignant = ?")) {

            pstmt.setString(1, Id_Enseignant);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id_residence");
                String adresse = rs.getString("adresse");
                char residenceType = rs.getString("residence_type").charAt(0);
                Calendar startDate = Calendar.getInstance();
                startDate.setTime(rs.getDate("effective_start_date_residence"));
                Calendar endDate = null;
                if (rs.getDate("effective_end_date_residence") != null) {
                    endDate = Calendar.getInstance();
                    endDate.setTime(rs.getDate("effective_end_date_residence"));
                }

                housing residence = new housing( adresse, residenceType);
               residence.setId(id);
                residence.setEffectiveStartDate(startDate);
                residence.setEffectiveEndDate(endDate);
                residences.add(residence);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return residences;
    }


    public static void addEchelon(String Id_Enseignant, int echelon) {
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);

            String selectQuery = "SELECT * FROM echelon WHERE Id_Enseignant = ? AND effective_end_date_echelon IS NULL ORDER BY effective_start_date_echelon DESC LIMIT 1";
            try (PreparedStatement stmt = conn.prepareStatement(selectQuery)) {
                stmt.setString(1, Id_Enseignant);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        Date startDate = rs.getDate("effective_start_date_echelon");
                        Calendar prevCalendar = Calendar.getInstance();
                        prevCalendar.setTime(startDate);

                        Calendar currentCalendar = Calendar.getInstance();

                        if (isWithinSamePeriod(prevCalendar, currentCalendar)) {
                            int id = rs.getInt("id_echelon");

                            String updateQuery = "UPDATE echelon SET nom_echelon = ? WHERE id_echelon = ?";
                            try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                                updateStmt.setInt(1, echelon);
                                updateStmt.setInt(2, id);
                                updateStmt.executeUpdate();
                            }
                        } else {
                            int id = rs.getInt("id_echelon");

                            String updateEndDateQuery = "UPDATE echelon SET effective_end_date_echelon = ? WHERE id_echelon = ?";
                            try (PreparedStatement updateEndDateStmt = conn.prepareStatement(updateEndDateQuery)) {
                                updateEndDateStmt.setDate(1, new Date(currentCalendar.getTimeInMillis()));
                                updateEndDateStmt.setInt(2, id);
                                updateEndDateStmt.executeUpdate();
                            }

                            String insertQuery = "INSERT INTO echelon (nom_echelon, effective_start_date_echelon, Id_Enseignant) VALUES (?, ?, ?)";
                            try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                                insertStmt.setInt(1, echelon);
                                insertStmt.setDate(2, new Date(currentCalendar.getTimeInMillis()));
                                insertStmt.setString(3, Id_Enseignant);
                                insertStmt.executeUpdate();
                            }
                        }
                    } else {
                        String insertQuery = "INSERT INTO echelon (nom_echelon, effective_start_date_echelon, Id_Enseignant) VALUES (?, ?, ?)";
                        try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                            Calendar calendar = Calendar.getInstance();
                            insertStmt.setInt(1, echelon);
                            insertStmt.setDate(2, new Date(calendar.getTimeInMillis()));
                            insertStmt.setString(3, Id_Enseignant);
                            insertStmt.executeUpdate();
                        }
                    }
                }
            }

            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            try (Connection conn = getConnection()) {
                conn.rollback();
            } catch (SQLException rollbackException) {
                rollbackException.printStackTrace();
            }
        }
    }

    public static void addGrade(String Id_Enseignant, String grade) {
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);

            String selectQuery = "SELECT * FROM Grade WHERE Id_Enseignant = ? AND effective_end_date_Grade IS NULL ORDER BY effective_start_date_Grade DESC LIMIT 1";
            try (PreparedStatement stmt = conn.prepareStatement(selectQuery)) {
                stmt.setString(1, Id_Enseignant);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        Date startDate = rs.getDate("effective_start_date_Grade");
                        Calendar prevCalendar = Calendar.getInstance();
                        prevCalendar.setTime(startDate);

                        Calendar currentCalendar = Calendar.getInstance();

                        if (isWithinSamePeriod(prevCalendar, currentCalendar)) {
                            int id = rs.getInt("id_Grade");

                            String updateQuery = "UPDATE Grade SET nom_Grade = ? WHERE id_Grade = ?";
                            try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                                updateStmt.setString(1, grade);
                                updateStmt.setInt(2, id);
                                updateStmt.executeUpdate();
                            }
                        } else {
                            int id = rs.getInt("id_Grade");

                            String updateEndDateQuery = "UPDATE Grade SET effective_end_date_Grade = ? WHERE id_Grade = ?";
                            try (PreparedStatement updateEndDateStmt = conn.prepareStatement(updateEndDateQuery)) {
                                updateEndDateStmt.setDate(1, new Date(currentCalendar.getTimeInMillis()));
                                updateEndDateStmt.setInt(2, id);
                                updateEndDateStmt.executeUpdate();
                            }

                            String insertQuery = "INSERT INTO Grade (nom_Grade, effective_start_date_Grade, Id_Enseignant) VALUES (?, ?, ?)";
                            try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                                insertStmt.setString(1, grade);
                                insertStmt.setDate(2, new Date(currentCalendar.getTimeInMillis()));
                                insertStmt.setString(3, Id_Enseignant);
                                insertStmt.executeUpdate();
                            }
                        }
                    } else {
                        String insertQuery = "INSERT INTO Grade (nom_Grade, effective_start_date_Grade, Id_Enseignant) VALUES (?, ?, ?)";
                        try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                            Calendar calendar = Calendar.getInstance();
                            insertStmt.setString(1, grade);
                            insertStmt.setDate(2, new Date(calendar.getTimeInMillis()));
                            insertStmt.setString(3, Id_Enseignant);
                            insertStmt.executeUpdate();
                        }
                    }
                }
            }

            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            try (Connection conn = getConnection()) {
                conn.rollback();
            } catch (SQLException rollbackException) {
                rollbackException.printStackTrace();
            }
        }
    }



    public static Grade getGrade(String Id_Enseignant, Calendar date) {
        Grade grade = null;
        try (Connection conn =getConnection()) {
            String selectQuery;
            if (date == null) {
                selectQuery = "SELECT * FROM Grade WHERE Id_Enseignant = ? AND effective_end_date_Grade IS NULL";
            } else {
                selectQuery = "SELECT * FROM Grade WHERE Id_Enseignant = ? AND effective_start_date_Grade <= ? AND (effective_end_date_Grade IS NULL OR effective_end_date_Grade >= ?)";
            }

            try (PreparedStatement selectStmt = conn.prepareStatement(selectQuery)) {
                selectStmt.setString(1, Id_Enseignant);
                if (date != null) {
                    selectStmt.setDate(2, new Date(date.getTimeInMillis()));
                    selectStmt.setDate(3, new Date(date.getTimeInMillis()));
                }
                ResultSet resultSet = selectStmt.executeQuery();

                if (resultSet.next()) {
                    int id = resultSet.getInt("id_Grade");
                    String gradeName = resultSet.getString("nom_Grade");
                    grade = new Grade(id, gradeName, null, null); // Effective dates are not retrieved in this method
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return grade;
    }
    public static Grade getGrade(String Id_Enseignant, int year, int month) {
        Grade grade = null;
        try (Connection conn =getConnection()) {
            PreparedStatement selectStmt;

            if (year == 0 && month == 0) {
                selectStmt = conn.prepareStatement("SELECT * FROM Grade WHERE Id_Enseignant = ? AND effective_end_date_Grade IS NULL");
                selectStmt.setString(1, Id_Enseignant);
            } else {
                Calendar startDateRange = Calendar.getInstance();
                Calendar endDateRange = Calendar.getInstance();

                startDateRange.set(year, month - 2, 20); // 20th of the previous month
                endDateRange.set(year, month - 1, 20);   // 20th of the current month

                selectStmt = conn.prepareStatement(
                        "SELECT * FROM Grade WHERE Id_Enseignant = ? AND effective_start_date_Grade <= ? AND (effective_end_date_Grade IS NULL OR effective_end_date_Grade >= ?)"
                );
                selectStmt.setString(1, Id_Enseignant);
                selectStmt.setDate(2, new java.sql.Date(endDateRange.getTimeInMillis()));
                selectStmt.setDate(3, new java.sql.Date(startDateRange.getTimeInMillis()));
            }

            ResultSet resultSet = selectStmt.executeQuery();

            if (resultSet.next()) {
                int id = resultSet.getInt("id_Grade");
                String gradeName = resultSet.getString("nom_Grade");
                grade = new Grade(id, gradeName, null, null);

                Calendar effectiveStartDate = Calendar.getInstance();
                effectiveStartDate.setTime(resultSet.getDate("effective_start_date_Grade"));
                grade.setEffectiveStartDate(effectiveStartDate);

                if (resultSet.getDate("effective_end_date_Grade") != null) {
                    Calendar effectiveEndDate = Calendar.getInstance();
                    effectiveEndDate.setTime(resultSet.getDate("effective_end_date_Grade"));
                    grade.setEffectiveEndDate(effectiveEndDate);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return grade;
    }


    public static Ehclent getEchlon(String Id_Enseignant, Calendar date) {
        Ehclent echlon = null;
        try (Connection conn =getConnection()) {
            String selectQuery;
            if (date == null) {
                selectQuery = "SELECT * FROM echelon WHERE Id_Enseignant = ? AND effective_end_date_echelon IS NULL";
            } else {
                selectQuery = "SELECT * FROM echelon WHERE Id_Enseignant = ? AND effective_start_date_echelon <= ? AND (effective_end_date_echelon IS NULL OR effective_end_date_echelon >= ?)";
            }

            try (PreparedStatement selectStmt = conn.prepareStatement(selectQuery)) {
                selectStmt.setString(1, Id_Enseignant);
                if (date != null) {
                    selectStmt.setDate(2, new Date(date.getTimeInMillis()));
                    selectStmt.setDate(3, new Date(date.getTimeInMillis()));
                }
                ResultSet resultSet = selectStmt.executeQuery();

                if (resultSet.next()) {
                    int id = resultSet.getInt("id_echelon");
                    int echlonValue = resultSet.getInt("nom_echelon");
                    echlon = new Ehclent(id, echlonValue, null, null); // Effective dates are not retrieved in this method
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return echlon;
    }
    public static Ehclent getEchlon(String Id_Enseignant, int year, int month) {
        Ehclent echelon = null;
        try (Connection conn =getConnection()) {
            PreparedStatement selectStmt;

            if (year == 0 && month == 0) {
                selectStmt = conn.prepareStatement("SELECT * FROM echelon WHERE Id_Enseignant = ? AND effective_end_date_echelon IS NULL");
                selectStmt.setString(1, Id_Enseignant);
            } else {
                Calendar startDateRange = Calendar.getInstance();
                Calendar endDateRange = Calendar.getInstance();

                startDateRange.set(year, month - 2, 20); // 20th of the previous month
                endDateRange.set(year, month - 1, 20);   // 20th of the current month

                selectStmt = conn.prepareStatement(
                        "SELECT * FROM echelon WHERE Id_Enseignant = ? AND effective_start_date_echelon <= ? AND (effective_end_date_echelon IS NULL OR effective_end_date_echelon >= ?)"
                );
                selectStmt.setString(1, Id_Enseignant);
                selectStmt.setDate(2, new java.sql.Date(endDateRange.getTimeInMillis()));
                selectStmt.setDate(3, new java.sql.Date(startDateRange.getTimeInMillis()));
            }

            ResultSet resultSet = selectStmt.executeQuery();

            if (resultSet.next()) {
                int id = resultSet.getInt("id_echelon");
                int echelonValue = resultSet.getInt("nom_echelon");
                echelon = new Ehclent(id, echelonValue, null, null);

                Calendar effectiveStartDate = Calendar.getInstance();
                effectiveStartDate.setTime(resultSet.getDate("effective_start_date_echelon"));
                echelon.setEffectiveStartDate(effectiveStartDate);

                if (resultSet.getDate("effective_end_date_echelon") != null) {
                    Calendar effectiveEndDate = Calendar.getInstance();
                    effectiveEndDate.setTime(resultSet.getDate("effective_end_date_echelon"));
                    echelon.setEffectiveEndDate(effectiveEndDate);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return echelon;
    }

    public static ArrayList<Ehclent> getEchelons(String Id_Enseignant) {
        ArrayList<Ehclent> echelons = new ArrayList<Ehclent>();
        try {
            Connection connection =getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM echelon WHERE Id_Enseignant = '" + Id_Enseignant + "'");
            while (resultSet.next()) {
                int id_echelon = resultSet.getInt("id_echelon");
                int nom_echelon = resultSet.getInt("nom_echelon");
                Date effective_start_date_echelon = resultSet.getDate("effective_start_date_echelon");
                Calendar effectiveStartDate = Calendar.getInstance();
                effectiveStartDate.setTime(effective_start_date_echelon);
                Date effective_end_date_echelon = resultSet.getDate("effective_end_date_echelon");
                Calendar effectiveEndDate = resultSet.getDate("effective_end_date_echelon") == null ? null : Calendar.getInstance();
                effectiveEndDate.setTime(effective_end_date_echelon != null ? effective_end_date_echelon : new Date(0));
                Ehclent ehclent = new Ehclent(id_echelon, nom_echelon, effectiveStartDate, effectiveEndDate);
                echelons.add(ehclent);
            }
            resultSet.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return echelons;
    }

    public static ArrayList<Grade> getGrades(String Id_Enseignant) {
        ArrayList<Grade> grades = new ArrayList<Grade>();
        try {
            Connection connection =getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Grade WHERE Id_Enseignant = '" + Id_Enseignant + "'");
            while (resultSet.next()) {
                int id_Grade = resultSet.getInt("id_Grade");
                String nom_Grade = resultSet.getString("nom_Grade");
                Date effective_start_date_Grade = resultSet.getDate("effective_start_date_Grade");
                Calendar effectiveStartDate = Calendar.getInstance();
                effectiveStartDate.setTime(effective_start_date_Grade);
                Date effective_end_date_Grade = resultSet.getDate("effective_end_date_Grade");
                Calendar effectiveEndDate = resultSet.getDate("effective_end_date_Grade") == null ? null : Calendar.getInstance();
                effectiveEndDate.setTime(effective_end_date_Grade != null ? effective_end_date_Grade : new Date(0));
                Grade grade = new Grade(id_Grade, nom_Grade, effectiveStartDate, effectiveEndDate);
                grades.add(grade);
            }
            resultSet.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return grades;
    }



    public static void addTypeAccount(String nomType) {
        String sql = "INSERT INTO type_compt (nom_type) VALUES (?)";

        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nomType);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("A new type account was inserted successfully!");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static ArrayList<String> getTypeComptIds() {
        ArrayList<String> typeComptIds = new ArrayList<String>();
        try {
            Connection connection =getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM type_compt");
            while (resultSet.next()) {
                String nom_type = resultSet.getString("nom_type");
                typeComptIds.add(nom_type);
            }
            resultSet.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return typeComptIds;
    }


    public  static  void addUpdateCompte(String Id_Enseignant, String typeCompt, String details) {
            String sql = "UPDATE Compt_bancaire SET Compt_bancaire_details = ?, nom_type = ? WHERE Id_Enseignant = ?";
            try (Connection connection =getConnection()) {
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, details);
                statement.setString(2, typeCompt);
                statement.setString(3, Id_Enseignant);

                int rowsAffected = statement.executeUpdate();
                if (rowsAffected == 0) {
                    sql = "INSERT INTO Compt_bancaire (Compt_bancaire_details, Id_Enseignant, nom_type) VALUES (?, ?, ?)";
                    statement = connection.prepareStatement(sql);
                    statement.setString(1, details);
                    statement.setString(2, Id_Enseignant);
                    statement.setString(3, typeCompt);
                    statement.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    public static String[] getCompteInfo(String Id_Enseignant) {
        String sql = "SELECT nom_type, Compt_bancaire_details FROM Compt_bancaire WHERE Id_Enseignant = ?";
        try (Connection connection =getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, Id_Enseignant);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String nomType = resultSet.getString("nom_type");
                String bankDetails = resultSet.getString("Compt_bancaire_details");
                return new String[] { nomType, bankDetails };
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }





    public static  void set_work_status(int id_Epoux, boolean working) {
        Calendar currentCalendar = Calendar.getInstance();
        Date currentDate = new Date(currentCalendar.getTimeInMillis());
        int workValue = working ? 1 : 0;

        try (Connection connection =getConnection()) {
            PreparedStatement selectStatement = connection.prepareStatement("SELECT * FROM Epoux_travaille WHERE id_Epoux = ?");
            selectStatement.setInt(1, id_Epoux);
            ResultSet resultSet = selectStatement.executeQuery();

            if (resultSet.next()) {
                // Update the existing record if it falls within the same period
                PreparedStatement updateStatement = connection.prepareStatement("UPDATE Epoux_travaille SET work = ?, effective_start_date_Epoux_travaille = ? WHERE id_Epoux = ? AND effective_end_date_Epoux_travaille IS NULL");
                updateStatement.setInt(1, workValue);
                updateStatement.setDate(2, currentDate);
                updateStatement.setInt(3, id_Epoux);

                Calendar prevCalendar = Calendar.getInstance();
                prevCalendar.setTime(resultSet.getDate("effective_start_date_Epoux_travaille"));

                if (!isWithinSamePeriod(prevCalendar, currentCalendar)) {
                    // Insert a new record if the existing record falls outside the same period
                    PreparedStatement insertStatement = connection.prepareStatement("INSERT INTO Epoux_travaille (id_Epoux, work, effective_start_date_Epoux_travaille) VALUES (?, ?, ?)");
                    insertStatement.setInt(1, id_Epoux);
                    insertStatement.setInt(2, workValue);
                    insertStatement.setDate(3, currentDate);
                    insertStatement.executeUpdate();
                } else {
                    updateStatement.executeUpdate();
                }
            } else {
                // Insert a new record if there's no existing record
                PreparedStatement insertStatement = connection.prepareStatement("INSERT INTO Epoux_travaille (id_Epoux, work, effective_start_date_Epoux_travaille) VALUES (?, ?, ?)");
                insertStatement.setInt(1, id_Epoux);
                insertStatement.setInt(2, workValue);
                insertStatement.setDate(3, currentDate);
                insertStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static boolean get_job_status(int id_Epoux, int year, int month) {
        try (Connection connection =getConnection()) {
            PreparedStatement selectStatement;

            if (year == 0 && month == 0) {
                selectStatement = connection.prepareStatement("SELECT work FROM Epoux_travaille WHERE id_Epoux = ? AND effective_end_date_Epoux_travaille IS NULL");
                selectStatement.setInt(1, id_Epoux);
            } else {
                Calendar startDateRange = Calendar.getInstance();
                Calendar endDateRange = Calendar.getInstance();

                startDateRange.set(year, month - 2, 20); // 20th of the previous month
                endDateRange.set(year, month - 1, 20);   // 20th of the current month

                selectStatement = connection.prepareStatement(
                        "SELECT work FROM Epoux_travaille WHERE id_Epoux = ? AND effective_start_date_Epoux_travaille <= ? AND (effective_end_date_Epoux_travaille IS NULL OR effective_end_date_Epoux_travaille >= ?)"
                );
                selectStatement.setInt(1, id_Epoux);
                selectStatement.setDate(2, new java.sql.Date(endDateRange.getTimeInMillis()));
                selectStatement.setDate(3, new java.sql.Date(startDateRange.getTimeInMillis()));
            }

            ResultSet resultSet = selectStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getBoolean("work");
            } else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static  boolean get_job_status(int id_Epoux, Calendar date) {
        try (Connection connection =getConnection()) {
            PreparedStatement selectStatement;
            if (date == null) {
                // If the date is null, return the boolean value of the work column for the record with no end effective date
                selectStatement = connection.prepareStatement("SELECT work FROM Epoux_travaille WHERE id_Epoux = ? AND effective_end_date_Epoux_travaille IS NULL");
            } else {
                // If the date is not null, return the boolean value of the work column for the record where the effective start date is less than or equal to the given date
                // and the effective end date is null or greater than the given date
                selectStatement = connection.prepareStatement("SELECT work FROM Epoux_travaille WHERE id_Epoux = ? AND effective_start_date_Epoux_travaille <= ? AND (effective_end_date_Epoux_travaille IS NULL OR effective_end_date_Epoux_travaille >= ?)");
                selectStatement.setDate(2, new java.sql.Date(date.getTimeInMillis()));
                selectStatement.setDate(3, new java.sql.Date(date.getTimeInMillis()));
            }
            selectStatement.setInt(1, id_Epoux);
            ResultSet resultSet = selectStatement.executeQuery();

            if (resultSet.next()) {
                // Return the boolean value of the work column
                return resultSet.getBoolean("work");
            } else {
                // Return false if no record is found
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }



    public static  void add_partner(String Id_Enseignant, partner partner) {
        Calendar currentCalendar = Calendar.getInstance();
        Date currentDate = new Date(currentCalendar.getTimeInMillis());

        try (Connection connection =getConnection()) {
            PreparedStatement selectStatement = connection.prepareStatement("SELECT * FROM Epoux WHERE Id_Enseignant = ?");
            selectStatement.setString(1, Id_Enseignant);
            ResultSet resultSet = selectStatement.executeQuery();

            if (resultSet.next()) {
                // Update the existing record if it falls within the same period
                PreparedStatement updateStatement = connection.prepareStatement("UPDATE Epoux SET nom_epoux = ?, prenom_epoux = ?, date_de_naissance_epoux = ?, sex_epoux = ?, effective_start_date_Epoux = ? WHERE Id_Enseignant = ? AND effective_end_date_Epoux IS NULL");
                updateStatement.setString(1, partner.getNom());
                updateStatement.setString(2, partner.getPrenom());
                updateStatement.setDate(3, new java.sql.Date(partner.getDateNaissance().getTimeInMillis()));
                updateStatement.setString(4, partner.getSex());
                updateStatement.setDate(5, currentDate);
                updateStatement.setString(6, Id_Enseignant);

                Calendar prevCalendar = Calendar.getInstance();
                prevCalendar.setTime(resultSet.getDate("effective_start_date_Epoux"));

                if (isWithinSamePeriod(prevCalendar, currentCalendar)) {
                    // If the existing record falls within the same period, update it
                    updateStatement.executeUpdate();
                } else {
                    // If the existing record does not fall within the same period, insert a new record
                    PreparedStatement insertStatement = connection.prepareStatement("INSERT INTO Epoux (nom_epoux, prenom_epoux, date_de_naissance_epoux, sex_epoux, effective_start_date_Epoux, Id_Enseignant, effective_end_date_Epoux) VALUES (?, ?, ?, ?, ?, ?, ?)");
                    insertStatement.setString(1, partner.getNom());
                    insertStatement.setString(2, partner.getPrenom());
                    insertStatement.setDate(3, new java.sql.Date(partner.getDateNaissance().getTimeInMillis()));
                    insertStatement.setString(4, partner.getSex());
                    insertStatement.setDate(5, currentDate);
                    insertStatement.setString(6, Id_Enseignant);
                    insertStatement.setDate(7, null); // Set end date to null
                    insertStatement.executeUpdate();
                }
            } else {
                // Insert a new record if there's no existing record
                PreparedStatement insertStatement = connection.prepareStatement("INSERT INTO Epoux (nom_epoux, prenom_epoux, date_de_naissance_epoux, sex_epoux, effective_start_date_Epoux, Id_Enseignant, effective_end_date_Epoux) VALUES (?, ?, ?, ?, ?, ?, ?)");
                insertStatement.setString(1, partner.getNom());
                insertStatement.setString(2, partner.getPrenom());
                insertStatement.setDate(3, new java.sql.Date(partner.getDateNaissance().getTimeInMillis()));
                insertStatement.setString(4, partner.getSex());
                insertStatement.setDate(5, currentDate);
                insertStatement.setString(6, Id_Enseignant);
                insertStatement.setDate(7, null); // Set end date to null
                insertStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public  static partner getPartner(String Id_Enseignant, Calendar date) {
        try (Connection connection =getConnection()) {
            PreparedStatement selectStatement;
            if (date == null) {
                // If the date is null, return the record with the same Id_Enseignant and end effective date is null
                selectStatement = connection.prepareStatement("SELECT * FROM Epoux WHERE Id_Enseignant = ? AND effective_end_date_Epoux IS NULL");
                selectStatement.setString(1, Id_Enseignant);
            } else {
                // If the date is not null, return the record where the effective start date is less than or equal to the given date
                // and the effective end date is null or greater than the given date
                selectStatement = connection.prepareStatement("SELECT * FROM Epoux WHERE Id_Enseignant = ? AND effective_start_date_Epoux <= ? AND (effective_end_date_Epoux IS NULL OR effective_end_date_Epoux >= ?)");
                selectStatement.setString(1, Id_Enseignant);
                selectStatement.setDate(2, new java.sql.Date(date.getTimeInMillis()));
                selectStatement.setDate(3, new java.sql.Date(date.getTimeInMillis()));
            }
            ResultSet resultSet = selectStatement.executeQuery();

            if (resultSet.next()) {
                // Create a partner object from the result set
                String nom = resultSet.getString("nom_epoux");
                String prenom = resultSet.getString("prenom_epoux");
                String sex = resultSet.getString("sex_epoux");
                Calendar dateNaissance = Calendar.getInstance();
                dateNaissance.setTime(resultSet.getDate("date_de_naissance_epoux"));
                boolean work = get_job_status(resultSet.getInt("id_Epoux"),date);

                partner partner = new partner(nom, prenom, sex, dateNaissance, work);
                partner.setId(resultSet.getInt("id_Epoux"));


                    // If the date is not null, set the effective start and end dates of the partner object
                    Calendar effectiveStartDate = Calendar.getInstance();
                    effectiveStartDate.setTime(resultSet.getDate("effective_start_date_Epoux"));
                    partner.setEffectiveStartDate(effectiveStartDate);
                    Calendar effectiveEndDate = null;
                if (resultSet.getDate("effective_end_date_Epoux") != null) {
                        effectiveEndDate = Calendar.getInstance();
                        effectiveEndDate.setTime(resultSet.getDate("effective_end_date_Epoux"));
                    }
                    partner.setEffectiveStartDate(effectiveEndDate);


                return partner;
            } else {
                return null; // Return null if no record is found
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static partner getPartner(String Id_Enseignant, int year, int month) {
        try (Connection connection = getConnection()) {
            PreparedStatement selectStatement;
            LocalDate startOfPeriod, endOfPeriod;

            // If both year and month are 0, use the current month
            if (year == 0 && month == 0) {
                year = LocalDate.now().getYear();
                month = LocalDate.now().getMonthValue();
            }

            // Calculate the start and end of the period
            if (month == 1) {
                startOfPeriod = LocalDate.of(year - 1, 12, 20);
                endOfPeriod = LocalDate.of(year, 1, 19);
            } else {
                startOfPeriod = LocalDate.of(year, month - 1, 20);
                endOfPeriod = LocalDate.of(year, month, 19);
            }

            // Prepare the SQL query
            selectStatement = connection.prepareStatement(
                    "SELECT * FROM Epoux WHERE Id_Enseignant = ? AND effective_start_date_Epoux <= ? AND (effective_end_date_Epoux IS NULL OR effective_end_date_Epoux >= ?)"
            );
            selectStatement.setString(1, Id_Enseignant);
            selectStatement.setDate(2, Date.valueOf(endOfPeriod));
            selectStatement.setDate(3, Date.valueOf(startOfPeriod));

            ResultSet resultSet = selectStatement.executeQuery();

            if (resultSet.next()) {
                // Create a partner object from the result set
                String nom = resultSet.getString("nom_epoux");
                String prenom = resultSet.getString("prenom_epoux");
                String sex = resultSet.getString("sex_epoux");
                Calendar dateNaissance = Calendar.getInstance();
                dateNaissance.setTime(resultSet.getDate("date_de_naissance_epoux"));
                boolean work = get_job_status(resultSet.getInt("id_Epoux"), year, month);

                partner partner = new partner(nom, prenom, sex, dateNaissance, work);
                partner.setId(resultSet.getInt("id_Epoux"));

                // Set the effective start and end dates of the partner object
                Calendar effectiveStartDate = Calendar.getInstance();
                effectiveStartDate.setTime(resultSet.getDate("effective_start_date_Epoux"));
                partner.setEffectiveStartDate(effectiveStartDate);

                Calendar effectiveEndDate = null;
                if (resultSet.getDate("effective_end_date_Epoux") != null) {
                    effectiveEndDate = Calendar.getInstance();
                    effectiveEndDate.setTime(resultSet.getDate("effective_end_date_Epoux"));
                }
                partner.setEffectiveEndDate(effectiveEndDate);

                return partner;
            } else {
                return null; // Return null if no record is found
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void Update_partner(int id_Epoux, String nom, String prenom, Calendar dateNaissance, char sex) {
        Calendar currentCalendar = Calendar.getInstance();
        Date currentDate = new Date(currentCalendar.getTimeInMillis());

        try (Connection connection = getConnection()) {
            // Check if there's an existing record for the given id_Epoux
            PreparedStatement selectStatement = connection.prepareStatement("SELECT * FROM Epoux WHERE id_Epoux = ?");
            selectStatement.setInt(1, id_Epoux);
            ResultSet resultSet = selectStatement.executeQuery();

            if (resultSet.next()) {
                // Update the existing record if it falls within the same period
                PreparedStatement updateStatement = connection.prepareStatement(
                        "UPDATE Epoux SET nom_epoux = ?, prenom_epoux = ?, date_de_naissance_epoux = ?, sex_epoux = ? WHERE id_Epoux = ?"
                );
                updateStatement.setString(1, nom);
                updateStatement.setString(2, prenom);
                updateStatement.setDate(3, new java.sql.Date(dateNaissance.getTimeInMillis()));
                updateStatement.setString(4, String.valueOf(sex));
                updateStatement.setInt(5, id_Epoux);

                Calendar prevCalendar = Calendar.getInstance();
                prevCalendar.setTime(resultSet.getDate("effective_start_date_Epoux"));

                if (isWithinSamePeriod(prevCalendar, currentCalendar)) {
                    // If the existing record falls within the same period, update it
                    updateStatement.executeUpdate();
                } else {
                    // If the existing record does not fall within the same period, insert a new record
                    PreparedStatement insertStatement = connection.prepareStatement(
                            "INSERT INTO Epoux (nom_epoux, prenom_epoux, date_de_naissance_epoux, sex_epoux, effective_start_date_Epoux, effective_end_date_Epoux) VALUES (?, ?, ?, ?, ?, ?)"
                    );
                    insertStatement.setString(1, nom);
                    insertStatement.setString(2, prenom);
                    insertStatement.setDate(3, new java.sql.Date(dateNaissance.getTimeInMillis()));
                    insertStatement.setString(4, String.valueOf(sex));
                    insertStatement.setDate(5, currentDate);
                    insertStatement.setDate(6, null); // effective_end_date_Epoux should be null for active partnerships
                    insertStatement.executeUpdate();
                }
            } else {
                // Insert a new record if there's no existing record
                PreparedStatement insertStatement = connection.prepareStatement(
                        "INSERT INTO Epoux (nom_epoux, prenom_epoux, date_de_naissance_epoux, sex_epoux, effective_start_date_Epoux, effective_end_date_Epoux) VALUES (?, ?, ?, ?, ?, ?)"
                );
                insertStatement.setString(1, nom);
                insertStatement.setString(2, prenom);
                insertStatement.setDate(3, new java.sql.Date(dateNaissance.getTimeInMillis()));
                insertStatement.setString(4, String.valueOf(sex));
                insertStatement.setDate(5, currentDate);
                insertStatement.setDate(6, null); // effective_end_date_Epoux should be null for active partnerships
                insertStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void setoff_partner(String Id_Enseignant) {
        LocalDate currentDate = LocalDate.now();
        LocalDate startOfCurrentMonth = currentDate.withDayOfMonth(1);
        LocalDate startOfPreviousMonth = startOfCurrentMonth.minusMonths(1);

        try (Connection connection = getConnection()) {
            // Select the record with the given Id_Enseignant and no effective end date
            PreparedStatement selectStatement = connection.prepareStatement(
                    "SELECT id_Epoux, effective_start_date_Epoux FROM Epoux WHERE Id_Enseignant = ? AND effective_end_date_Epoux IS NULL"
            );
            selectStatement.setString(1, Id_Enseignant);
            ResultSet resultSet = selectStatement.executeQuery();

            while (resultSet.next()) {
                int idEpoux = resultSet.getInt("id_Epoux");
                LocalDate effectiveStartDate = resultSet.getDate("effective_start_date_Epoux").toLocalDate();

                if ((effectiveStartDate.isAfter(startOfPreviousMonth) || effectiveStartDate.isEqual(startOfPreviousMonth)) &&
                        (effectiveStartDate.isBefore(startOfCurrentMonth) || effectiveStartDate.isEqual(startOfCurrentMonth))) {
                    // Delete the record if the start date is within the previous or current month
                    PreparedStatement deleteStatement = connection.prepareStatement(
                            "DELETE FROM Epoux WHERE id_Epoux = ?"
                    );
                    deleteStatement.setInt(1, idEpoux);
                    deleteStatement.executeUpdate();
                } else {
                    // Set the effective end date to the first day of the previous month if the start date is older
                    LocalDate endDate = startOfPreviousMonth.with(TemporalAdjusters.firstDayOfMonth());

                    PreparedStatement updateStatement = connection.prepareStatement(
                            "UPDATE Epoux SET effective_end_date_Epoux = ? WHERE id_Epoux = ?"
                    );
                    updateStatement.setDate(1, Date.valueOf(endDate));
                    updateStatement.setInt(2, idEpoux);
                    updateStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void setoff_partner_my(String Id_Enseignant) {
        Calendar currentCalendar = Calendar.getInstance();
        Date currentDate = new Date(currentCalendar.getTimeInMillis());

        try (Connection connection = getConnection()) {
            // Update the effective_end_date_Epoux for all records with the given Id_Enseignant
            PreparedStatement updateStatement = connection.prepareStatement(
                    "UPDATE Epoux SET effective_end_date_Epoux = ? WHERE Id_Enseignant = ? AND effective_end_date_Epoux IS NULL"
            );
            updateStatement.setDate(1, currentDate);
            updateStatement.setString(2, Id_Enseignant);
            updateStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }














    public  static ArrayList<partner> getPartners(String Id_Enseignant) {
        ArrayList<partner> partners = new ArrayList<>();

        try (Connection connection =getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(
                    "SELECT * FROM Epoux WHERE Id_Enseignant = '" + Id_Enseignant + "'");

            while (resultSet.next()) {
                int id = resultSet.getInt("id_Epoux");
                String nom = resultSet.getString("nom_epoux");
                String prenom = resultSet.getString("prenom_epoux");
                String sex = resultSet.getString("sex_epoux");
                java.sql.Date dateNaissance = resultSet.getDate("date_de_naissance_epoux");
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(dateNaissance);

                partner partner = new partner(nom, prenom, sex, calendar, true); // Assuming 'work' is true by default
                partner.setId(id);
                partners.add(partner);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return partners;
    }
    public static void add_or_update_kids(String Id_Enseignant, ArrayList<kids> kidsList) throws SQLException {
        try (Connection connection =getConnection()) {
            for (Personne person : kidsList) {
                if (person instanceof kids) {
                    kids kid = (kids) person;
                    if (kid.getId() == -1) { // New record
                        insertNewKid(connection, Id_Enseignant, kid);
                    }
                    else { // Update existing record
                        updateExistingKid(connection, Id_Enseignant, kid);
                    }
                }
            }
        }
    }

    public static void insertNewKid(Connection connection, String Id_Enseignant, kids kid) throws SQLException {
        String insertSQL = "INSERT INTO Enfant (nom_enfant, prenom_enfant, sex_enfant, date_de_naissance_enfant, " +
                "Adoptde, condition_enfant, effective_start_date_Enfant, Id_Enseignant) " +
                "VALUES (?, ?, ?, ?, ?, ?, CURRENT_DATE, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
            pstmt.setString(1, kid.getNom());
            pstmt.setString(2, kid.getPrenom());
            pstmt.setString(3, kid.getSex());
            pstmt.setDate(4, new java.sql.Date(kid.getDateNaissance().getTimeInMillis()));
            pstmt.setBoolean(5, kid.isAdoupted());
            pstmt.setBoolean(6, kid.isCondition());
            pstmt.setString(7, Id_Enseignant);
            pstmt.executeUpdate();
        }
    }






    private static void updateExistingKid(Connection connection, String Id_Enseignant, kids kid) throws SQLException {
        Calendar currentDate = Calendar.getInstance();
        Calendar effectiveStartDate = getEffectiveStartDate(connection, kid.getId());

        if (effectiveStartDate != null && isWithinSamePeriod(effectiveStartDate, currentDate)) {
            // Update the existing record
            String updateSQL = "UPDATE Enfant SET nom_enfant = ?, prenom_enfant = ?, sex_enfant = ?, " +
                    "date_de_naissance_enfant = ?, Adoptde = ?, condition_enfant = ?, " +
                    "effective_start_date_Enfant = CURRENT_DATE WHERE id_Enfant = ?";

            try (PreparedStatement pstmt = connection.prepareStatement(updateSQL)) {
                pstmt.setString(1, kid.getNom());
                pstmt.setString(2, kid.getPrenom());
                pstmt.setString(3, kid.getSex());
                pstmt.setDate(4, new java.sql.Date(kid.getDateNaissance().getTimeInMillis()));
                pstmt.setBoolean(5, kid.isAdoupted());
                pstmt.setBoolean(6, kid.isCondition());
                pstmt.setInt(7, kid.getId());
                pstmt.executeUpdate();
            }
        } else {
            // Insert a new record and update the old record's end date
            setEnfantEndDate( kid.getId());
            insertNewKid(connection, Id_Enseignant, kid);
        }
    }

    private static Calendar getEffectiveStartDate(Connection connection, int id_Enfant) throws SQLException {
        String query = "SELECT effective_start_date_Enfant FROM Enfant WHERE id_Enfant = ? AND effective_end_date_Enfant IS NULL";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id_Enfant);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(rs.getDate(1));
                    return calendar;
                }
            }
        }
        return null;
    }

    private static void setKidOff(Connection connection, int id_Enfant) throws SQLException {
        String updateSQL = "UPDATE Enfant SET effective_end_date_Enfant = CURRENT_DATE WHERE id_Enfant = ? AND effective_end_date_Enfant IS NULL";

        try (PreparedStatement pstmt = connection.prepareStatement(updateSQL)) {
            pstmt.setInt(1, id_Enfant);
            pstmt.executeUpdate();
        }
    }


    public static ArrayList<kids> getKids(String Id_Enseignant, Calendar date) {
        ArrayList<kids> kidsList = new ArrayList<>();

        try (Connection connection =getConnection()) {
            PreparedStatement selectStatement;
            if (date == null) {
                selectStatement = connection.prepareStatement("SELECT * FROM Enfant WHERE Id_Enseignant = ? AND effective_end_date_Enfant IS NULL");
                selectStatement.setString(1, Id_Enseignant);
            } else {
                selectStatement = connection.prepareStatement("SELECT * FROM Enfant WHERE Id_Enseignant = ? AND ((effective_start_date_Enfant <= ? AND effective_end_date_Enfant >= ?) OR (effective_start_date_Enfant <= ? AND effective_end_date_Enfant IS NULL))");
                selectStatement.setString(1, Id_Enseignant);
                selectStatement.setDate(2, new java.sql.Date(date.getTimeInMillis()));
                selectStatement.setDate(3, new java.sql.Date(date.getTimeInMillis()));
                selectStatement.setDate(4, new java.sql.Date(date.getTimeInMillis()));
            }

            ResultSet resultSet = selectStatement.executeQuery();

            while (resultSet.next()) {
                kids kid = new kids(resultSet.getString("nom_enfant"),
                        resultSet.getString("prenom_enfant"),
                        resultSet.getString("sex_enfant"),
                        resultSet.getDate("date_de_naissance_enfant"),
                        resultSet.getBoolean("Adoptde"),
                        resultSet.getBoolean("condition_enfant"));

                kid.setId(resultSet.getInt("id_Enfant"));

                Calendar startDate = Calendar.getInstance();
                startDate.setTime(resultSet.getDate("effective_start_date_Enfant"));
                kid.setEffectiveStartDate(startDate);

                if (resultSet.getDate("effective_end_date_Enfant") != null) {
                    Calendar endDate = Calendar.getInstance();
                    endDate.setTime(resultSet.getDate("effective_end_date_Enfant"));
                    kid.setEffectiveStartDate(endDate);
                }

                kidsList.add(kid);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return kidsList;
    }
    public static ArrayList<kids> getKids(String Id_Enseignant, int year, int month) {
        ArrayList<kids> kidsList = new ArrayList<>();

        try (Connection connection =getConnection()) {
            PreparedStatement selectStatement;

            if (year == 0 && month == 0) {
                // No specific date range provided, retrieve active kids
                selectStatement = connection.prepareStatement("SELECT * FROM Enfant WHERE Id_Enseignant = ? AND effective_end_date_Enfant IS NULL");
                selectStatement.setString(1, Id_Enseignant);
            } else {
                // Calculate the date range
                Calendar startDateRange = Calendar.getInstance();
                Calendar endDateRange = Calendar.getInstance();

                // Set the start date to the previous month's 20th day
                startDateRange.set(year, month - 2, 20);

                // Set the end date to the current month's 20th day
                endDateRange.set(year, month - 1, 20);

                // Construct the SQL query to fetch kids within the specified date range
                selectStatement = connection.prepareStatement(
                        "SELECT * FROM Enfant " +
                                "WHERE Id_Enseignant = ? " +
                                "AND (effective_start_date_Enfant <= ? AND (effective_end_date_Enfant IS NULL OR effective_end_date_Enfant >= ?))"
                );

                selectStatement.setString(1, Id_Enseignant);
                selectStatement.setDate(2, new java.sql.Date(endDateRange.getTimeInMillis()));
                selectStatement.setDate(3, new java.sql.Date(startDateRange.getTimeInMillis()));
            }

            ResultSet resultSet = selectStatement.executeQuery();

            while (resultSet.next()) {
                kids kid = new kids(
                        resultSet.getString("nom_enfant"),
                        resultSet.getString("prenom_enfant"),
                        resultSet.getString("sex_enfant"),
                        resultSet.getDate("date_de_naissance_enfant"),
                        resultSet.getBoolean("Adoptde"),
                        resultSet.getBoolean("condition_enfant")
                );

                kid.setId(resultSet.getInt("id_Enfant"));

                Calendar startDate = Calendar.getInstance();
                startDate.setTime(resultSet.getDate("effective_start_date_Enfant"));
                kid.setEffectiveStartDate(startDate);

                if (resultSet.getDate("effective_end_date_Enfant") != null) {
                    Calendar endDate = Calendar.getInstance();
                    endDate.setTime(resultSet.getDate("effective_end_date_Enfant"));
                    kid.setEffectiveEndDate(endDate);
                }

                kidsList.add(kid);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return kidsList;
    }

    public  static void setFaculte(String faculte) {
        try (Connection connection =getConnection()) {
            // Check if the faculty already exists
            PreparedStatement selectStatement = connection.prepareStatement("SELECT COUNT(*) FROM faculte WHERE nom_faculte = ?");
            selectStatement.setString(1, faculte);
            ResultSet resultSet = selectStatement.executeQuery();
            resultSet.next();
            int count = resultSet.getInt(1);

            if (count == 0) {
                // If the faculty doesn't exist, insert it into the table
                PreparedStatement insertStatement = connection.prepareStatement("INSERT INTO faculte (nom_faculte) VALUES (?)");
                insertStatement.setString(1, faculte);
                insertStatement.executeUpdate();
                System.out.println("Faculty added successfully.");
            } else {
                // If the faculty already exists, do nothing
                System.out.println("Faculty already exists.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<String> getFacultes() {
        List<String> faculties = new ArrayList<>();

        try {
            Connection connection =getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT nom_faculte FROM faculte");

            while (resultSet.next()) {
                String faculty = resultSet.getString("nom_faculte");
                faculties.add(faculty);
            }
        } catch (SQLException e) {
            // Handle SQL exceptions
            e.printStackTrace(); // Consider logging the exception
            // Optionally, throw a custom exception or return null/empty list
        } catch (NullPointerException e) {
            // Handle NullPointerException if URL is not initialized
            e.printStackTrace(); // Consider logging the exception
            // Optionally, throw a custom exception or return null/empty list
        }

        return faculties;
    }


    public static  void setDepartement(String faculte, String departement) {
        try (Connection connection =getConnection()) {
            // Check if the faculty exists
            PreparedStatement selectStatement = connection.prepareStatement("SELECT COUNT(*) FROM faculte WHERE nom_faculte = ?");
            selectStatement.setString(1, faculte);
            ResultSet resultSet = selectStatement.executeQuery();
            resultSet.next();
            int count = resultSet.getInt(1);

            if (count == 0) {
                // If the faculty doesn't exist, print an error message
                System.out.println("Faculty does not exist.");
            } else {
                // If the faculty exists, check if the department already exists
                PreparedStatement selectDeptStatement = connection.prepareStatement("SELECT COUNT(*) FROM departement WHERE nom_departement = ?");
                selectDeptStatement.setString(1, departement);
                ResultSet deptResultSet = selectDeptStatement.executeQuery();
                deptResultSet.next();
                int deptCount = deptResultSet.getInt(1);

                if (deptCount == 0) {
                    // If the department doesn't exist, insert it into the table
                    PreparedStatement insertStatement = connection.prepareStatement("INSERT INTO departement (nom_departement, nom_faculte) VALUES (?, ?)");
                    insertStatement.setString(1, departement);
                    insertStatement.setString(2, faculte);
                    insertStatement.executeUpdate();
                    System.out.println("Department added successfully.");
                } else {
                    // If the department already exists, print an error message
                    System.out.println("Department already exists.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static List<String> getDepartements() {
        List<String> departements = new ArrayList<>();

        try {
            Connection connection =getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT nom_departement FROM departement");

            while (resultSet.next()) {
                String departement = resultSet.getString("nom_departement");
                departements.add(departement);
            }
        } catch (SQLException e) {
            // Handle SQL exceptions
            e.printStackTrace(); // Consider logging the exception
            // Optionally, throw a custom exception or return null/empty list
        } catch (NullPointerException e) {
            // Handle NullPointerException if URL is not initialized
            e.printStackTrace(); // Consider logging the exception
            // Optionally, throw a custom exception or return null/empty list
        }

        return departements;
    }
    public static void createOrUpdateMonthlyAbsentRecord(String Id_Enseignant, int nombreAbsent) {
        LocalDate currentDate = LocalDate.now();
        LocalDate startOfPeriod;
        LocalDate effectiveDate;

        if (currentDate.getDayOfMonth() >= 20) {
            startOfPeriod = currentDate.withDayOfMonth(20);
            effectiveDate = currentDate.plusMonths(1).withDayOfMonth(1);
        } else {
            startOfPeriod = currentDate.minusMonths(1).withDayOfMonth(20);
            effectiveDate = currentDate.withDayOfMonth(1);
        }

        LocalDate endOfPeriod = startOfPeriod.plusMonths(1).withDayOfMonth(19);

        try (Connection connection =getConnection()) {
            // Check if there is already a record for the current period
            PreparedStatement selectStatement = connection.prepareStatement(
                    "SELECT id_Absent, nombre FROM Absent WHERE Id_Enseignant = ? AND effective_monthly_Absent BETWEEN ? AND ?");
            selectStatement.setString(1, Id_Enseignant);
            selectStatement.setDate(2, java.sql.Date.valueOf(startOfPeriod));
            selectStatement.setDate(3, java.sql.Date.valueOf(endOfPeriod));
            ResultSet resultSet = selectStatement.executeQuery();

            if (resultSet.next()) {
                // If a record exists for the current period, update the nombre
                int idAbsent = resultSet.getInt("id_Absent");
                PreparedStatement updateStatement = connection.prepareStatement(
                        "UPDATE Absent SET nombre = ? WHERE id_Absent = ?");
                updateStatement.setInt(1, nombreAbsent);
                updateStatement.setInt(2, idAbsent);
                updateStatement.executeUpdate();
            } else {
                // If no record exists for the current period, create a new record
                PreparedStatement insertStatement = connection.prepareStatement(
                        "INSERT INTO Absent (nombre, effective_monthly_Absent, Id_Enseignant) VALUES (?, ?, ?)");
                insertStatement.setInt(1, nombreAbsent);
                insertStatement.setDate(2, java.sql.Date.valueOf(effectiveDate));
                insertStatement.setString(3, Id_Enseignant);
                insertStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static int getNombre_of_absence(String Id_Enseignant, int year, int month) {
        LocalDate startOfPeriod;
        LocalDate endOfPeriod;

        // Determine the period based on the provided year and month
        if (year == 0 && month == 0) {
            // Determine the ongoing period
            LocalDate currentDate = LocalDate.now();
            if (currentDate.getDayOfMonth() >= 20) {
                startOfPeriod = currentDate.withDayOfMonth(20);
                endOfPeriod = startOfPeriod.plusMonths(1).withDayOfMonth(19);
            } else {
                startOfPeriod = currentDate.minusMonths(1).withDayOfMonth(20);
                endOfPeriod = startOfPeriod.plusMonths(1).withDayOfMonth(19);
            }
        } else {
            // Determine the specific period for the given year and month
            startOfPeriod = LocalDate.of(year, month, 1).withDayOfMonth(20);
            endOfPeriod = startOfPeriod.plusMonths(1).withDayOfMonth(19);
        }

        try (Connection connection =getConnection()) {
            PreparedStatement selectStatement = connection.prepareStatement(
                    "SELECT SUM(nombre) as totalAbsences FROM Absent WHERE Id_Enseignant = ? AND effective_monthly_Absent BETWEEN ? AND ?");
            selectStatement.setString(1, Id_Enseignant);
            selectStatement.setDate(2, java.sql.Date.valueOf(startOfPeriod));
            selectStatement.setDate(3, java.sql.Date.valueOf(endOfPeriod));
            ResultSet resultSet = selectStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("totalAbsences");
            } else {
                return 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1; // Indicate an error
        }
    }


    public static void checkAndUpdateActiveRecords() {
        LocalDate currentDate = LocalDate.now();
        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.setTime(java.sql.Date.valueOf(currentDate));

        try (Connection connection =getConnection()) {
            PreparedStatement selectStatement = connection.prepareStatement(
                    "SELECT id_Absent, effective_monthly_Absent FROM Absent WHERE effective_monthly_Absent IS NULL"
            );
            ResultSet resultSet = selectStatement.executeQuery();

            while (resultSet.next()) {
                int idAbsent = resultSet.getInt("id_Absent");
                Date effectiveMonthlyAbsentDate = resultSet.getDate("effective_monthly_Absent");
                Calendar effectiveCalendar = Calendar.getInstance();
                if (effectiveMonthlyAbsentDate != null) {
                    effectiveCalendar.setTime(effectiveMonthlyAbsentDate);
                }

                if (!isWithinSamePeriod(effectiveCalendar, currentCalendar)) {
                    PreparedStatement updateStatement = connection.prepareStatement(
                            "UPDATE Absent SET effective_monthly_Absent = ? WHERE id_Absent = ?"
                    );
                    updateStatement.setDate(1, java.sql.Date.valueOf(currentDate));
                    updateStatement.setInt(2, idAbsent);
                    updateStatement.executeUpdate();
                    updateStatement.close();
                }
            }

            resultSet.close();
            selectStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }




    private static String readFileContent(String fileName) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                contentBuilder.append(line).append("\n");
            }
        }
        return contentBuilder.toString();
    }


    public static void addIRGFile() throws IOException, SQLException {
        String fileName = irgfile;
        Connection conn = null;
        PreparedStatement insertStmt = null;
        PreparedStatement updateStmt = null;
        try {
            conn = getConnection();
            String newDocumentContent = readFileContent(fileName);
            Calendar currentDate = Calendar.getInstance();

            // Check if there's an existing record with effective_end_date_table_IRG is null
            String selectSql = "SELECT COUNT(*) AS count, MAX(effective_start_date_table_IRG) AS last_start_date FROM table_IRG WHERE effective_end_date_table_IRG IS NULL";
            PreparedStatement countStmt = conn.prepareStatement(selectSql);
            ResultSet rs = countStmt.executeQuery();
            rs.next();
            int count = rs.getInt("count");
            Date lastStartDate = rs.getDate("last_start_date");
            countStmt.close();

            if (count > 0) {
                Calendar lastStartCal = Calendar.getInstance();
                lastStartCal.setTime(lastStartDate);
                // Update the existing record if it falls within the same period
                if (isWithinSamePeriod(lastStartCal, currentDate)) {
                    String updatePreviousRecordSql = "UPDATE table_IRG SET document_content = ?, effective_start_date_table_IRG = ? WHERE effective_end_date_table_IRG IS NULL";
                    updateStmt = conn.prepareStatement(updatePreviousRecordSql);
                    updateStmt.setString(1, newDocumentContent);
                    updateStmt.setDate(2, new java.sql.Date(currentDate.getTimeInMillis()));
                    updateStmt.executeUpdate();

                    System.out.println("Le document existant a été mis à jour avec succès.");
                } else {
                    // Set effective_end_date_table_IRG of the existing record to the current date
                    String updatePreviousRecordSql = "UPDATE table_IRG SET effective_end_date_table_IRG = ? WHERE effective_end_date_table_IRG IS NULL";
                    updateStmt = conn.prepareStatement(updatePreviousRecordSql);
                    updateStmt.setDate(1, new java.sql.Date(currentDate.getTimeInMillis()));
                    updateStmt.executeUpdate();

                    // Insert new record
                    String insertNewRecordSql = "INSERT INTO table_IRG (document_content, effective_start_date_table_IRG) VALUES (?, ?)";
                    insertStmt = conn.prepareStatement(insertNewRecordSql);
                    insertStmt.setString(1, newDocumentContent);
                    insertStmt.setDate(2, new java.sql.Date(currentDate.getTimeInMillis()));
                    insertStmt.executeUpdate();

                    System.out.println("Le nouveau document a été ajouté avec succès.");
                }
            } else {
                // Insert new record if no existing record with null effective_end_date_table_IRG
                String insertNewRecordSql = "INSERT INTO table_IRG (document_content, effective_start_date_table_IRG) VALUES (?, ?)";
                insertStmt = conn.prepareStatement(insertNewRecordSql);
                insertStmt.setString(1, newDocumentContent);
                insertStmt.setDate(2, new java.sql.Date(currentDate.getTimeInMillis()));
                insertStmt.executeUpdate();

                System.out.println("Le nouveau document a été ajouté avec succès.");
            }
        } finally {
            try {
                if (insertStmt != null) insertStmt.close();
                if (updateStmt != null) updateStmt.close();
                if (conn != null) conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    public static void getIRGFile(Date date) throws IOException, SQLException {
        Connection conn = null;
        PreparedStatement selectStmt = null;
        BufferedWriter writer = null;
        try {
            conn = getConnection();

            String selectSql;
            if (date == null) {
                // If the date is null, retrieve the latest document (where effective_end_date_table_IRG is null)
                selectSql = "SELECT document_content FROM table_IRG WHERE effective_end_date_table_IRG IS NULL";
                selectStmt = conn.prepareStatement(selectSql);
            } else {
                // If the date is provided, retrieve the document where the date falls within the start and end effective dates
                selectSql = "SELECT document_content FROM table_IRG WHERE effective_start_date_table_IRG <= ? AND (effective_end_date_table_IRG IS NULL OR effective_end_date_table_IRG >= ?) LIMIT 1";
                selectStmt = conn.prepareStatement(selectSql);
                selectStmt.setDate(1, date);
                selectStmt.setDate(2, date);
            }

            ResultSet rs = selectStmt.executeQuery();

            writer = new BufferedWriter(new FileWriter(irgfile));
            if (rs.next()) {
                String documentContent = rs.getString("document_content");
                writer.write(documentContent);
                writer.newLine();
                System.out.println("Les données ont été récupérées et écrites dans le fichier IRG avec succès.");
            } else {
                System.out.println("Aucune donnée trouvée pour la date spécifiée.");
            }
        } finally {
            try {
                if (writer != null) writer.close();
                if (selectStmt != null) selectStmt.close();
                if (conn != null) conn.close();
            } catch (IOException | SQLException e) {
                e.printStackTrace();
            }
        }

    }
    public static void getIRGFile(int year, int month) throws IOException, SQLException {
        Connection conn = null;
        PreparedStatement selectStmt = null;
        BufferedWriter writer = null;
        try {
            conn = getConnection();

            String selectSql;
            if (year == 0 && month == 0) {
                // If the year and month are 0, retrieve the latest document (where effective_end_date_table_IRG is null)
                selectSql = "SELECT document_content FROM table_IRG WHERE effective_end_date_table_IRG IS NULL";
                selectStmt = conn.prepareStatement(selectSql);
            } else {
                // If the year and month are provided, retrieve the document where the date falls within the start and end effective dates
                Calendar startDateRange = Calendar.getInstance();
                Calendar endDateRange = Calendar.getInstance();
                startDateRange.set(year, month - 1, 20);  // Previous month's 20th day
                endDateRange.set(year, month, 20);  // Current month's 20th day

                selectSql = "SELECT document_content FROM table_IRG WHERE effective_start_date_table_IRG <= ? AND (effective_end_date_table_IRG IS NULL OR effective_end_date_table_IRG >= ?) LIMIT 1";
                selectStmt = conn.prepareStatement(selectSql);
                selectStmt.setDate(1, new java.sql.Date(endDateRange.getTimeInMillis()));
                selectStmt.setDate(2, new java.sql.Date(startDateRange.getTimeInMillis()));
            }

            ResultSet rs = selectStmt.executeQuery();

            writer = new BufferedWriter(new FileWriter(irgfile2));
            if (rs.next()) {
                String documentContent = rs.getString("document_content");
                writer.write(documentContent);
                writer.newLine();
                System.out.println("Les données ont été récupérées et écrites dans le fichier IRG avec succès.");
            } else {
                System.out.println("Aucune donnée trouvée pour la date spécifiée.");
            }
        } finally {
            try {
                if (writer != null) writer.close();
                if (selectStmt != null) selectStmt.close();
                if (conn != null) conn.close();
            } catch (IOException | SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public static boolean add_or_update_Enseignant(String Id_Enseignant, Enseignant teacher) throws SQLException {
        boolean isNewRecord = false;

        try (Connection connection = getConnection()) {
            // Check if the teacher already exists
            PreparedStatement selectStatement = connection.prepareStatement("SELECT * FROM Enseignant WHERE Id_Enseignant = ?");
            selectStatement.setString(1, Id_Enseignant);
            ResultSet resultSet = selectStatement.executeQuery();

            if (resultSet.next()) {
                // Teacher exists, update the record if necessary
                Enseignant existingTeacher = extractEnseignantFromResultSet(resultSet);
                if (!teacher.equals(existingTeacher)) {
                    PreparedStatement updateStatement = connection.prepareStatement(
                            "UPDATE Enseignant SET nom_ensg = ?, prenom_ensg = ?, date_de_naissance_ensg = ?, sex_ensg = ?, situation_sociale = ?, adresse_email = ?, Tel = ?, annee_de_bac = ?, nom_departement = ? WHERE Id_Enseignant = ?");
                    updateStatement.setString(1, teacher.getNom());
                    updateStatement.setString(2, teacher.getPrenom());
                    updateStatement.setDate(3, new java.sql.Date(teacher.getDateNaissance().getTimeInMillis()));
                    updateStatement.setString(4, validateSex(teacher.getSex()));
                    updateStatement.setBoolean(5, teacher.isMarried());
                    updateStatement.setString(6, teacher.getAdresseEmail());
                    updateStatement.setLong(7, teacher.getTel());
                    updateStatement.setInt(8, teacher.getAnneeDeBac());
                    updateStatement.setString(9, teacher.getDepartment());
                    updateStatement.setString(10, Id_Enseignant);
                    updateStatement.executeUpdate();
                }
            } else {
                // Teacher does not exist, insert a new record
                isNewRecord = true;
                PreparedStatement insertStatement = connection.prepareStatement(
                        "INSERT INTO Enseignant (Id_Enseignant, nom_ensg, prenom_ensg, date_de_naissance_ensg, sex_ensg, situation_sociale, adresse_email, Tel, annee_de_bac, nom_departement, effective_start_date_Enseignant) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURDATE())");
                insertStatement.setString(1, Id_Enseignant);
                insertStatement.setString(2, teacher.getNom());
                insertStatement.setString(3, teacher.getPrenom());
                insertStatement.setDate(4,new java.sql.Date(teacher.getDateNaissance().getTimeInMillis()));
                insertStatement.setString(5, validateSex(teacher.getSex()));
                insertStatement.setBoolean(6, teacher.isMarried());
                insertStatement.setString(7, teacher.getAdresseEmail());
                insertStatement.setLong(8, teacher.getTel());
                insertStatement.setInt(9, teacher.getAnneeDeBac());
                insertStatement.setString(10, teacher.getDepartment());
                insertStatement.executeUpdate();
            }
        }
        return isNewRecord;
    }

    private static String validateSex(String sex) {
        System.out.println(sex);
        if (sex == null || (!sex.equals("M") && !sex.equals("F"))) {
            throw new IllegalArgumentException("Invalid sex value. Must be 'M' or 'F'.");

        }
        return sex;
    }

    public static void set_effective_end_date_Enseignant(String Id_Enseignant) throws SQLException {
        try (Connection connection = getConnection()) {
            PreparedStatement updateStatement = connection.prepareStatement(
                    "UPDATE Enseignant SET effective_end_date_Enseignant = CURDATE() WHERE Id_Enseignant = ?");
            updateStatement.setString(1, Id_Enseignant);
            updateStatement.executeUpdate();
        }
    }

    public static Enseignant get_Enseignant(String Id_Enseignant) throws SQLException {
        Enseignant teacher = null;

        try (Connection connection = getConnection()) {
            PreparedStatement selectStatement = connection.prepareStatement("SELECT * FROM Enseignant WHERE Id_Enseignant = ?");
            selectStatement.setString(1, Id_Enseignant);
            ResultSet resultSet = selectStatement.executeQuery();

            if (resultSet.next()) {
                teacher = extractEnseignantFromResultSet(resultSet);
            }
        }

        return teacher;
    }

    public static ArrayList<Enseignant> get_all_active_Enseignant() throws SQLException {
        ArrayList<Enseignant> enseignants = new ArrayList<>();

        try (Connection connection = getConnection()) {
            PreparedStatement selectStatement = connection.prepareStatement("SELECT * FROM Enseignant WHERE effective_end_date_Enseignant IS NULL");
            ResultSet resultSet = selectStatement.executeQuery();

            while (resultSet.next()) {
                Enseignant teacher = extractEnseignantFromResultSet(resultSet);
                enseignants.add(teacher);
            }
        }

        return enseignants;
    }
    public static ArrayList<String> getAllActiveEnseignantIds() throws SQLException {
        ArrayList<String> enseignantIds = new ArrayList<>();

        try (Connection connection = getConnection()) {
            PreparedStatement selectStatement = connection.prepareStatement("SELECT Id_Enseignant FROM Enseignant WHERE effective_end_date_Enseignant  IS NULL");
            ResultSet resultSet = selectStatement.executeQuery();

            while (resultSet.next()) {
                String idEnseignant = resultSet.getString("Id_Enseignant");
                enseignantIds.add(idEnseignant);
            }
        }

        return enseignantIds;
    }

    public static ArrayList<String> getAllEnseignantIds() throws SQLException {
        ArrayList<String> enseignantIds = new ArrayList<>();

        try (Connection connection = getConnection()) {
            PreparedStatement selectStatement = connection.prepareStatement("SELECT Id_Enseignant FROM Enseignant");
            ResultSet resultSet = selectStatement.executeQuery();

            while (resultSet.next()) {
                String idEnseignant = resultSet.getString("Id_Enseignant");
                enseignantIds.add(idEnseignant);
            }
        }

        return enseignantIds;
    }

    public static ArrayList<String> getAllDisactiveEnseignantIds() throws SQLException {
        ArrayList<String> enseignantIds = new ArrayList<>();

        try (Connection connection = getConnection()) {
            PreparedStatement selectStatement = connection.prepareStatement("SELECT Id_Enseignant FROM Enseignant WHERE effective_end_date_Enseignant IS NOT NULL");
            ResultSet resultSet = selectStatement.executeQuery();

            while (resultSet.next()) {
                String idEnseignant = resultSet.getString("Id_Enseignant");
                enseignantIds.add(idEnseignant);
            }
        }

        return enseignantIds;
    }


    private static Enseignant extractEnseignantFromResultSet(ResultSet resultSet) throws SQLException {
        String Id_Enseignant = resultSet.getString("Id_Enseignant");
        String nom = resultSet.getString("nom_ensg");
        String prenom = resultSet.getString("prenom_ensg");
        String sex = resultSet.getString("sex_ensg");
        java.sql.Date dateNaissance = resultSet.getDate("date_de_naissance_ensg");
        boolean married = resultSet.getBoolean("situation_sociale");
        String adresseEmail = resultSet.getString("adresse_email");
        int tel = resultSet.getInt("Tel");
        int anneeDeBac = resultSet.getInt("annee_de_bac");
        String department = resultSet.getString("nom_departement");

        Enseignant teacher = new Enseignant(nom, prenom, sex, dateNaissance, married, adresseEmail, tel, anneeDeBac, department);
        teacher.setIdEnseignant(Id_Enseignant);
        return teacher;
    }

    public static void add_or_update_details_static(int zone, int index, int percentageForEXP) {
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);

            String selectSQL = "SELECT * FROM details_static WHERE effective_end_date_details_static IS NULL";
            String insertSQL = "INSERT INTO details_static (point_index, prime_zone, persantage_for_EXP, effective_start_date_details_static, effective_end_date_details_static) VALUES (?, ?, ?, ?, ?)";
            String updateSQL = "UPDATE details_static SET prime_zone = ?, point_index = ?, persantage_for_EXP = ?, effective_start_date_details_static = ? WHERE details_static_id = ?";
            String updateEndDateSQL = "UPDATE details_static SET effective_end_date_details_static = ? WHERE details_static_id = ?";

            try (PreparedStatement selectStmt = conn.prepareStatement(selectSQL, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)) {
                ResultSet rs = selectStmt.executeQuery();

                Calendar currentCalendar = Calendar.getInstance();
                Date currentDate = new Date(currentCalendar.getTimeInMillis());

                if (rs.next()) {
                    // Existing active record found
                    Date effectiveStartDate = rs.getDate("effective_start_date_details_static");
                    Calendar prevCalendar = Calendar.getInstance();
                    prevCalendar.setTime(effectiveStartDate);

                    // Check if the periods are the same (e.g., same month and year)
                    if (isWithinSamePeriod(prevCalendar, currentCalendar)) {
                        // Update existing record
                        try (PreparedStatement updateStmt = conn.prepareStatement(updateSQL)) {
                            updateStmt.setInt(1, zone);
                            updateStmt.setInt(2, index);
                            updateStmt.setInt(3, percentageForEXP);
                            updateStmt.setDate(4, new java.sql.Date(currentDate.getTime()));
                            updateStmt.setInt(5, rs.getInt("details_static_id"));
                            updateStmt.executeUpdate();
                            System.out.println("Existing record updated with ID: " + rs.getInt("details_static_id"));
                        }
                    } else {
                        // End the previous record and insert a new one
                        try (PreparedStatement updateEndDateStmt = conn.prepareStatement(updateEndDateSQL);
                             PreparedStatement insertStmt = conn.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS)) {
                            updateEndDateStmt.setDate(1, new java.sql.Date(currentDate.getTime()));
                            updateEndDateStmt.setInt(2, rs.getInt("details_static_id"));
                            updateEndDateStmt.executeUpdate();
                            System.out.println("Existing record ended with ID: " + rs.getInt("details_static_id"));

                            // Insert a new record
                            insertStmt.setInt(1, index);
                            insertStmt.setInt(2, zone);
                            insertStmt.setInt(3, percentageForEXP);
                            insertStmt.setDate(4, new java.sql.Date(currentDate.getTime()));
                            insertStmt.setNull(5, Types.DATE); // effective_end_date_details_static initially null
                            insertStmt.executeUpdate();

                            // Retrieve the generated details_static_id
                            ResultSet generatedKeys = insertStmt.getGeneratedKeys();
                            if (generatedKeys.next()) {
                                int generatedId = generatedKeys.getInt(1);
                                System.out.println("New record inserted with ID: " + generatedId);
                            }
                        }
                    }
                } else {
                    // No active record found, insert a new one
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS)) {
                        insertStmt.setInt(1, index);
                        insertStmt.setInt(2, zone);
                        insertStmt.setInt(3, percentageForEXP);
                        insertStmt.setDate(4, new java.sql.Date(currentDate.getTime()));
                        insertStmt.setNull(5, Types.DATE); // effective_end_date_details_static initially null
                        insertStmt.executeUpdate();

                        // Retrieve the generated details_static_id
                        ResultSet generatedKeys = insertStmt.getGeneratedKeys();
                        if (generatedKeys.next()) {
                            int generatedId = generatedKeys.getInt(1);
                            System.out.println("New record inserted with ID: " + generatedId);
                        }
                    }
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<int[]> get_details_static(Date date) {
        ArrayList<int[]> detailsList = new ArrayList<>();
        try (Connection conn = getConnection()) {
            String querySQL = date == null ?
                    "SELECT prime_zone, point_index, persantage_for_EXP FROM details_static WHERE effective_end_date_details_static IS NULL" :
                    "SELECT prime_zone, point_index, persantage_for_EXP FROM details_static WHERE ? BETWEEN effective_start_date_details_static AND COALESCE(effective_end_date_details_static, '9999-12-31')";

            try (PreparedStatement stmt = conn.prepareStatement(querySQL)) {
                if (date != null) {
                    stmt.setDate(1, new java.sql.Date(date.getTime()));
                }
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    int[] detail = new int[3];
                    detail[0] = rs.getInt("prime_zone");
                    detail[1] = rs.getInt("point_index");
                    detail[2] = rs.getInt("persantage_for_EXP");
                    detailsList.add(detail);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return detailsList;
    }
    public static ArrayList<int[]> get_details_static(int year, int month) {
        ArrayList<int[]> detailsList = new ArrayList<>();
        try (Connection conn = getConnection()) {
            String querySQL;
            if (year == 0 && month == 0) {
                querySQL = "SELECT prime_zone, point_index, persantage_for_EXP FROM details_static WHERE effective_end_date_details_static IS NULL";
            } else {
                Calendar startDateRange = Calendar.getInstance();
                Calendar endDateRange = Calendar.getInstance();
                startDateRange.set(year, month - 1, 20);  // Previous month's 20th day
                endDateRange.set(year, month, 20);  // Current month's 20th day

                querySQL = "SELECT prime_zone, point_index, persantage_for_EXP FROM details_static WHERE ? BETWEEN effective_start_date_details_static AND COALESCE(effective_end_date_details_static, '9999-12-31')";
            }

            try (PreparedStatement stmt = conn.prepareStatement(querySQL)) {
                if (year != 0 && month != 0) {
                    Calendar date = Calendar.getInstance();
                    date.set(year, month - 1, 20);  // Set the day to 20th for consistency with the period logic
                    stmt.setDate(1, new java.sql.Date(date.getTimeInMillis()));
                }

                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    int[] detail = new int[3];
                    detail[0] = rs.getInt("prime_zone");
                    detail[1] = rs.getInt("point_index");
                    detail[2] = rs.getInt("persantage_for_EXP");
                    detailsList.add(detail);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return detailsList;
    }


    public static void add_or_update_details_static(String nomGrade, double basicSalary, int echelon1,int echelon2, int echelon3, int echelon4, int echelon5, int echelon6, int echelon7, int echelon8, int echelon9, int echelon10,int echelon11, int echelon12, int encadrement, int documentation, int responsabilite) {

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);

            String selectSQL = "SELECT * FROM salary_info WHERE nom_Grade = ? AND effective_end_date_salary_info IS NULL";
            String insertSQL = "INSERT INTO salary_info (nom_Grade, basic_salary, echelon_1, echelon_2, echelon_3, echelon_4, echelon_5, echelon_6, echelon_7, echelon_8, echelon_9, echelon_10, echelon_11, echelon_12, encadrement, documentation, responsabilite, effective_start_date_salary_info, effective_end_date_salary_info) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            String updateSQL = "UPDATE salary_info SET basic_salary = ?, echelon_1 = ?, echelon_2 = ?, echelon_3 = ?, echelon_4 = ?, echelon_5 = ?, echelon_6 = ?, echelon_7 = ?, echelon_8 = ?, echelon_9 = ?, echelon_10 = ?, echelon_11 = ?, echelon_12 = ?, encadrement = ?, documentation = ?, responsabilite = ?, effective_start_date_salary_info = ? WHERE salary_id = ?";
            String updateEndDateSQL = "UPDATE salary_info SET effective_end_date_salary_info = ? WHERE salary_id = ?";

            try (PreparedStatement selectStmt = conn.prepareStatement(selectSQL)) {
                selectStmt.setString(1, nomGrade);
                ResultSet rs = selectStmt.executeQuery();

                Calendar currentCalendar = Calendar.getInstance();
                Date currentDate = new Date(currentCalendar.getTimeInMillis());

                boolean recordUpdated = false;

                while (rs.next()) {
                    Date effectiveStartDate = rs.getDate("effective_start_date_salary_info");
                    Calendar prevCalendar = Calendar.getInstance();
                    prevCalendar.setTime(effectiveStartDate);

                    if (isWithinSamePeriod(prevCalendar, currentCalendar)) {
                        try (PreparedStatement updateStmt = conn.prepareStatement(updateSQL)) {
                            updateStmt.setDouble(1, basicSalary);
                            updateStmt.setInt(2, echelon1);
                            updateStmt.setInt(3, echelon2);
                            updateStmt.setInt(4, echelon3);
                            updateStmt.setInt(5, echelon4);
                            updateStmt.setInt(6, echelon5);
                            updateStmt.setInt(7, echelon6);
                            updateStmt.setInt(8, echelon7);
                            updateStmt.setInt(9, echelon8);
                            updateStmt.setInt(10, echelon9);
                            updateStmt.setInt(11, echelon10);
                            updateStmt.setInt(12, echelon11);
                            updateStmt.setInt(13, echelon12);
                            updateStmt.setInt(14, encadrement);
                            updateStmt.setInt(15, documentation);
                            updateStmt.setInt(16, responsabilite);
                            updateStmt.setDate(17, new java.sql.Date(currentDate.getTime()));
                            updateStmt.setInt(18, rs.getInt("salary_id"));
                            updateStmt.executeUpdate();
                            recordUpdated = true;
                            break;
                        }
                    } else {
                        try (PreparedStatement updateEndDateStmt = conn.prepareStatement(updateEndDateSQL)) {
                            updateEndDateStmt.setDate(1, new java.sql.Date(currentDate.getTime()));
                            updateEndDateStmt.setInt(2, rs.getInt("salary_id"));
                            updateEndDateStmt.executeUpdate();
                        }
                    }
                }

                if (!recordUpdated) {
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS)) {
                        insertStmt.setString(1, nomGrade);
                        insertStmt.setDouble(2, basicSalary);
                        insertStmt.setInt(3, echelon1);
                        insertStmt.setInt(4, echelon2);
                        insertStmt.setInt(5, echelon3);
                        insertStmt.setInt(6, echelon4);
                        insertStmt.setInt(7, echelon5);
                        insertStmt.setInt(8, echelon6);
                        insertStmt.setInt(9, echelon7);
                        insertStmt.setInt(10, echelon8);
                        insertStmt.setInt(11, echelon9);
                        insertStmt.setInt(12, echelon10);
                        insertStmt.setInt(13, echelon11);
                        insertStmt.setInt(14, echelon12);
                        insertStmt.setInt(15, encadrement);
                        insertStmt.setInt(16, documentation);
                        insertStmt.setInt(17, responsabilite);
                        insertStmt.setDate(18, new java.sql.Date(currentDate.getTime()));
                        insertStmt.setNull(19, Types.DATE); // effective_end_date_salary_info initially null
                        insertStmt.executeUpdate();

                        // Retrieve the generated salary_id
                        ResultSet generatedKeys = insertStmt.getGeneratedKeys();
                        if (generatedKeys.next()) {
                            int generatedId = generatedKeys.getInt(1);
                            System.out.println("New record inserted with ID: " + generatedId);
                        }
                    }
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public static int[] get_salary_info(String nomGrade, Date date) {
        int[] salaryDetail = null;
        try (Connection conn = getConnection()) {
            String querySQL = date == null ?
                    "SELECT * FROM salary_info WHERE nom_Grade = ? AND effective_end_date_salary_info IS NULL" :
                    "SELECT * FROM salary_info WHERE nom_Grade = ? AND ? BETWEEN effective_start_date_salary_info AND COALESCE(effective_end_date_salary_info, '9999-12-31')";

            try (PreparedStatement stmt = conn.prepareStatement(querySQL)) {
                stmt.setString(1, nomGrade);
                if (date != null) {
                    stmt.setDate(2, new java.sql.Date(date.getTime()));
                }
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    salaryDetail = new int[16];
                    salaryDetail[0] = rs.getInt("basic_salary");
                    salaryDetail[1] = rs.getInt("echelon_1");
                    salaryDetail[2] = rs.getInt("echelon_2");
                    salaryDetail[3] = rs.getInt("echelon_3");
                    salaryDetail[4] = rs.getInt("echelon_4");
                    salaryDetail[5] = rs.getInt("echelon_5");
                    salaryDetail[6] = rs.getInt("echelon_6");
                    salaryDetail[7] = rs.getInt("echelon_7");
                    salaryDetail[8] = rs.getInt("echelon_8");
                    salaryDetail[9] = rs.getInt("echelon_9");
                    salaryDetail[10] = rs.getInt("echelon_10");
                    salaryDetail[11] = rs.getInt("echelon_11");
                    salaryDetail[12] = rs.getInt("echelon_12");
                    salaryDetail[13] = rs.getInt("encadrement");
                    salaryDetail[14] = rs.getInt("documentation");
                    salaryDetail[15] = rs.getInt("responsabilite");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return salaryDetail;
    }
    public static int[] get_salary_info(String nomGrade, int year, int month) {
        int[] salaryDetail = null;
        try (Connection conn = getConnection()) {
            String querySQL;
            if (year == 0 && month == 0) {
                querySQL = "SELECT * FROM salary_info WHERE nom_Grade = ? AND effective_end_date_salary_info IS NULL";
            } else {
                Calendar startDateRange = Calendar.getInstance();
                Calendar endDateRange = Calendar.getInstance();
                startDateRange.set(year, month - 1, 20);  // Previous month's 20th day
                endDateRange.set(year, month, 20);  // Current month's 20th day

                querySQL = "SELECT * FROM salary_info WHERE nom_Grade = ? AND ? BETWEEN effective_start_date_salary_info AND COALESCE(effective_end_date_salary_info, '9999-12-31')";
            }

            try (PreparedStatement stmt = conn.prepareStatement(querySQL)) {
                stmt.setString(1, nomGrade);
                if (year != 0 && month != 0) {
                    Calendar date = Calendar.getInstance();
                    date.set(year, month - 1, 20);  // Set the day to 20th for consistency with the period logic
                    stmt.setDate(2, new java.sql.Date(date.getTimeInMillis()));
                }

                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    salaryDetail = new int[16];
                    salaryDetail[0] = rs.getInt("basic_salary");
                    salaryDetail[1] = rs.getInt("echelon_1");
                    salaryDetail[2] = rs.getInt("echelon_2");
                    salaryDetail[3] = rs.getInt("echelon_3");
                    salaryDetail[4] = rs.getInt("echelon_4");
                    salaryDetail[5] = rs.getInt("echelon_5");
                    salaryDetail[6] = rs.getInt("echelon_6");
                    salaryDetail[7] = rs.getInt("echelon_7");
                    salaryDetail[8] = rs.getInt("echelon_8");
                    salaryDetail[9] = rs.getInt("echelon_9");
                    salaryDetail[10] = rs.getInt("echelon_10");
                    salaryDetail[11] = rs.getInt("echelon_11");
                    salaryDetail[12] = rs.getInt("echelon_12");
                    salaryDetail[13] = rs.getInt("encadrement");
                    salaryDetail[14] = rs.getInt("documentation");
                    salaryDetail[15] = rs.getInt("responsabilite");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return salaryDetail;
    }



//    public static void main(String[] args) {
//        // Example usage of add_or_update_details_static
//        add_or_update_details_static("A1", 3000.00, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 1, 1, 1);
//
//        // Fetching the latest record with nomGrade "A1" and effective_end_date_salary_info is null
//        ArrayList<int[]> salaries = get_salary_info("A1", null);
//
//        // Printing out the fetched salary details
//        for (int[] salary : salaries) {
//            System.out.println("Basic Salary: " + salary[0]);
//            System.out.println("Echelon 1: " + salary[1]);
//            System.out.println("Echelon 2: " + salary[2]);
//            System.out.println("Echelon 3: " + salary[3]);
//            System.out.println("Echelon 4: " + salary[4]);
//            System.out.println("Echelon 5: " + salary[5]);
//            System.out.println("Echelon 6: " + salary[6]);
//            System.out.println("Echelon 7: " + salary[7]);
//            System.out.println("Echelon 8: " + salary[8]);
//            System.out.println("Echelon 9: " + salary[9]);
//            System.out.println("Echelon 10: " + salary[10]);
//            System.out.println("Echelon 11: " + salary[11]);
//            System.out.println("Echelon 12: " + salary[12]);
//            System.out.println("Encadrement: " + salary[13]);
//            System.out.println("Documentation: " + salary[14]);
//            System.out.println("Responsabilite: " + salary[15]);
//            System.out.println();
//        }
//
//        // Example usage of get_salary_info with a specific date
//        Date specificDate = new Date(); // Replace with your specific date
//        ArrayList<int[]> salariesOnDate = get_salary_info("A1", specificDate);
//
//        // Printing out the fetched salary details on the specific date
//        for (int[] salary : salariesOnDate) {
//            System.out.println("Basic Salary: " + salary[0]);
//            System.out.println("Echelon 1: " + salary[1]);
//            System.out.println("Echelon 2: " + salary[2]);
//            System.out.println("Echelon 3: " + salary[3]);
//            System.out.println("Echelon 4: " + salary[4]);
//            System.out.println("Echelon 5: " + salary[5]);
//            System.out.println("Echelon 6: " + salary[6]);
//            System.out.println("Echelon 7: " + salary[7]);
//            System.out.println("Echelon 8: " + salary[8]);
//            System.out.println("Echelon 9: " + salary[9]);
//            System.out.println("Echelon 10: " + salary[10]);
//            System.out.println("Echelon 11: " + salary[11]);
//            System.out.println("Echelon 12: " + salary[12]);
//            System.out.println("Encadrement: " + salary[13]);
//            System.out.println("Documentation: " + salary[14]);
//            System.out.println("Responsabilite: " + salary[15]);
//            System.out.println();
//        }
//    }










    public static boolean setEnfantEndDate(int id) {
        try (Connection conn = getConnection()) {
            // Retrieve the start date
            String selectStartDateQuery = "SELECT effective_start_date_Enfant FROM Enfant WHERE id_Enfant = ?";
            LocalDate startDate;
            try (PreparedStatement selectStmt = conn.prepareStatement(selectStartDateQuery)) {
                selectStmt.setInt(1, id);
                ResultSet rs = selectStmt.executeQuery();
                if (rs.next()) {
                    startDate = rs.getDate("effective_start_date_Enfant").toLocalDate();
                } else {
                    return false; // Record not found
                }
            }

            LocalDate currentDate = LocalDate.now();
            LocalDate startOfCurrentPeriod = (currentDate.getDayOfMonth() >= 20) ? LocalDate.of(currentDate.getYear(), currentDate.getMonthValue(), 20)
                    : LocalDate.of(currentDate.getYear(), currentDate.getMonthValue() - 1, 20);
            LocalDate startOfPreviousPeriod = startOfCurrentPeriod.minusMonths(1);
            LocalDate startOfNextPeriod = startOfCurrentPeriod.plusMonths(1);

            // Check the start date and decide the action
            if ((startDate.isAfter(startOfPreviousPeriod) || startDate.isEqual(startOfPreviousPeriod)) && startDate.isBefore(startOfCurrentPeriod)) {
                // Delete the record if the start date is between 20th of the previous month and 19th of the current month
                String deleteQuery = "DELETE FROM Enfant WHERE id_Enfant = ?";
                try (PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {
                    deleteStmt.setInt(1, id);
                    deleteStmt.executeUpdate();
                    System.out.println("delet kid");

                }
            } else if ((startDate.isAfter(startOfCurrentPeriod) || startDate.isEqual(startOfCurrentPeriod)) && startDate.isBefore(startOfNextPeriod)) {
                // Delete the record if the start date is between 20th of the current month and 19th of the next month
                String deleteQuery = "DELETE FROM Enfant WHERE id_Enfant = ?";
                try (PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {
                    deleteStmt.setInt(1, id);
                    deleteStmt.executeUpdate();
                    System.out.println("delet kid");
                }
            } else if (startDate.isBefore(startOfPreviousPeriod)) {
                LocalDate endOfPreviousMonth = LocalDate.now();
                String updateEndDateQuery = "UPDATE Enfant SET effective_end_date_Enfant = ? WHERE id_Enfant = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateEndDateQuery)) {
                    updateStmt.setDate(1, Date.valueOf(endOfPreviousMonth));
                    updateStmt.setInt(2, id);
                    updateStmt.executeUpdate();
                    System.out.println("arshif kid");
                }
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }









    public static boolean setEpouxTravailleEndDate(int id) {
        try (Connection conn = getConnection()) {
            // Retrieve the start date
            String selectStartDateQuery = "SELECT effective_start_date_Epoux_travaille FROM Epoux_travaille WHERE id_Epoux_travaille = ?";
            LocalDate startDate;
            try (PreparedStatement selectStmt = conn.prepareStatement(selectStartDateQuery)) {
                selectStmt.setInt(1, id);
                ResultSet rs = selectStmt.executeQuery();
                if (rs.next()) {
                    startDate = rs.getDate("effective_start_date_Epoux_travaille").toLocalDate();
                } else {
                    return false; // Record not found
                }
            }

            LocalDate currentDate = LocalDate.now();
            LocalDate startOfCurrentPeriod = (currentDate.getDayOfMonth() >= 20) ? LocalDate.of(currentDate.getYear(), currentDate.getMonthValue(), 20)
                    : LocalDate.of(currentDate.getYear(), currentDate.getMonthValue() - 1, 20);
            LocalDate startOfPreviousPeriod = startOfCurrentPeriod.minusMonths(1);
            LocalDate startOfNextPeriod = startOfCurrentPeriod.plusMonths(1);

            // Check the start date and decide the action
            if ((startDate.isAfter(startOfPreviousPeriod) || startDate.isEqual(startOfPreviousPeriod)) && startDate.isBefore(startOfCurrentPeriod)) {
                // Delete the record if the start date is between 20th of the previous month and 19th of the current month
                String deleteQuery = "DELETE FROM Epoux_travaille WHERE id_Epoux_travaille = ?";
                try (PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {
                    deleteStmt.setInt(1, id);
                    deleteStmt.executeUpdate();
                }
            } else if ((startDate.isAfter(startOfCurrentPeriod) || startDate.isEqual(startOfCurrentPeriod)) && startDate.isBefore(startOfNextPeriod)) {
                // Delete the record if the start date is between 20th of the current month and 19th of the next month
                String deleteQuery = "DELETE FROM Epoux_travaille WHERE id_Epoux_travaille = ?";
                try (PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {
                    deleteStmt.setInt(1, id);
                    deleteStmt.executeUpdate();
                }
            } else if (startDate.isBefore(startOfPreviousPeriod)) {
                LocalDate endOfPreviousMonth = LocalDate.now();
                String updateEndDateQuery = "UPDATE Epoux_travaille SET effective_end_date_Epoux_travaille = ? WHERE id_Epoux_travaille = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateEndDateQuery)) {
                    updateStmt.setDate(1, Date.valueOf(endOfPreviousMonth));
                    updateStmt.setInt(2, id);
                    updateStmt.executeUpdate();
                }
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static boolean setEpouxEndDate(int id) {
        try (Connection conn = getConnection()) {
            // Retrieve the start date
            String selectStartDateQuery = "SELECT effective_start_date_Epoux FROM Epoux WHERE id_Epoux = ?";
            LocalDate startDate;
            try (PreparedStatement selectStmt = conn.prepareStatement(selectStartDateQuery)) {
                selectStmt.setInt(1, id);
                ResultSet rs = selectStmt.executeQuery();
                if (rs.next()) {
                    startDate = rs.getDate("effective_start_date_Epoux").toLocalDate();
                } else {
                    return false; // Record not found
                }
            }

            LocalDate currentDate = LocalDate.now();
            LocalDate startOfCurrentPeriod = (currentDate.getDayOfMonth() >= 20) ? LocalDate.of(currentDate.getYear(), currentDate.getMonthValue(), 20)
                    : LocalDate.of(currentDate.getYear(), currentDate.getMonthValue() - 1, 20);
            LocalDate startOfPreviousPeriod = startOfCurrentPeriod.minusMonths(1);
            LocalDate startOfNextPeriod = startOfCurrentPeriod.plusMonths(1);

            // Check the start date and decide the action
            if ((startDate.isAfter(startOfPreviousPeriod) || startDate.isEqual(startOfPreviousPeriod)) && startDate.isBefore(startOfCurrentPeriod)) {
                // Delete the record if the start date is between 20th of the previous month and 19th of the current month
                String deleteQuery = "DELETE FROM Epoux WHERE id_Epoux = ?";
                try (PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {
                    deleteStmt.setInt(1, id);
                    deleteStmt.executeUpdate();
                }
            } else if ((startDate.isAfter(startOfCurrentPeriod) || startDate.isEqual(startOfCurrentPeriod)) && startDate.isBefore(startOfNextPeriod)) {
                // Delete the record if the start date is between 20th of the current month and 19th of the next month
                String deleteQuery = "DELETE FROM Epoux WHERE id_Epoux = ?";
                try (PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {
                    deleteStmt.setInt(1, id);
                    deleteStmt.executeUpdate();
                }
            } else if (startDate.isBefore(startOfPreviousPeriod)) {
                LocalDate endOfPreviousMonth = LocalDate.now();
                String updateEndDateQuery = "UPDATE Epoux SET effective_end_date_Epoux = ? WHERE id_Epoux = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateEndDateQuery)) {
                    updateStmt.setDate(1, Date.valueOf(endOfPreviousMonth));
                    updateStmt.setInt(2, id);
                    updateStmt.executeUpdate();
                }
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static boolean setJobEndDate(int id) {
        try (Connection conn = getConnection()) {
            // Retrieve the start date
            String selectStartDateQuery = "SELECT effective_start_date_Extra_post_for_each_Enseignant FROM Extra_post_for_each_Enseignant WHERE id_Extra_post = ?";
            LocalDate startDate;
            try (PreparedStatement selectStmt = conn.prepareStatement(selectStartDateQuery)) {
                selectStmt.setInt(1, id);
                ResultSet rs = selectStmt.executeQuery();
                if (rs.next()) {
                    startDate = rs.getDate("effective_start_date_Extra_post_for_each_Enseignant").toLocalDate();
                } else {
                    return false; // Record not found
                }
            }


            LocalDate currentDate = LocalDate.now();
            LocalDate startOfCurrentPeriod = (currentDate.getDayOfMonth() >= 20) ? LocalDate.of(currentDate.getYear(), currentDate.getMonthValue(), 20)
                    : LocalDate.of(currentDate.getYear(), currentDate.getMonthValue() - 1, 20);
            LocalDate startOfPreviousPeriod = startOfCurrentPeriod.minusMonths(1);
            LocalDate startOfNextPeriod = startOfCurrentPeriod.plusMonths(1);

            // Check the start date and decide the action
            if ((startDate.isAfter(startOfPreviousPeriod) || startDate.isEqual(startOfPreviousPeriod)) && startDate.isBefore(startOfCurrentPeriod)) {
                // Delete the record if the start date is between 20th of the previous month and 19th of the current month
                String deleteQuery = "DELETE FROM Extra_post_for_each_Enseignant WHERE id_Extra_post = ?";
                try (PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {
                    deleteStmt.setInt(1, id);
                    deleteStmt.executeUpdate();
                }
            } else if ((startDate.isAfter(startOfCurrentPeriod) || startDate.isEqual(startOfCurrentPeriod)) && startDate.isBefore(startOfNextPeriod)) {
                // Delete the record if the start date is between 20th of the current month and 19th of the next month
                String deleteQuery = "DELETE FROM Extra_post_for_each_Enseignant WHERE id_Extra_post = ?";
                try (PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {
                    deleteStmt.setInt(1, id);
                    deleteStmt.executeUpdate();
                }
            } else if (startDate.isBefore(startOfPreviousPeriod)) {
                LocalDate endOfPreviousMonth = LocalDate.now();
                String updateEndDateQuery = "UPDATE Extra_post_for_each_Enseignant SET effective_end_date_Extra_post_for_each_Enseignant = ? WHERE id_Extra_post = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateEndDateQuery)) {
                    updateStmt.setDate(1, Date.valueOf(endOfPreviousMonth));
                    updateStmt.setInt(2, id);
                    updateStmt.executeUpdate();
                }
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public static ArrayList<String> delete_or_archive_records(int id_type_extra_post) {
        ArrayList<String> enseignantsToArchive = new ArrayList<>();

        try (Connection conn = getConnection()) {
            // Retrieve all records with the given id_type_extra_post
            String selectQuery = "SELECT * FROM Extra_post_for_each_Enseignant WHERE id_type_extra_post = ?  AND effective_end_date_Extra_post_for_each_Enseignant IS NULL";
            try (PreparedStatement selectStmt = conn.prepareStatement(selectQuery)) {
                selectStmt.setInt(1, id_type_extra_post);
                ResultSet rs = selectStmt.executeQuery();

                LocalDate currentDate = LocalDate.now();
                LocalDate startOfCurrentPeriod = (currentDate.getDayOfMonth() >= 20) ? LocalDate.of(currentDate.getYear(), currentDate.getMonthValue(), 20)
                        : LocalDate.of(currentDate.getYear(), currentDate.getMonthValue() - 1, 20);
                LocalDate startOfPreviousPeriod = startOfCurrentPeriod.minusMonths(1);
                LocalDate startOfNextPeriod = startOfCurrentPeriod.plusMonths(1);

                while (rs.next()) {
                    int idExtraPost = rs.getInt("id_Extra_post");
                    String idEnseignant = rs.getString("Id_Enseignant");
                    LocalDate startDate = rs.getDate("effective_start_date_Extra_post_for_each_Enseignant").toLocalDate();
                    enseignantsToArchive.add(idEnseignant);
                    // Apply the deletion logic
                    if ((startDate.isAfter(startOfPreviousPeriod) || startDate.isEqual(startOfPreviousPeriod)) && startDate.isBefore(startOfCurrentPeriod)) {
                        // Delete the record
                        String deleteQuery = "DELETE FROM Extra_post_for_each_Enseignant WHERE id_Extra_post = ? AND effective_end_date_Extra_post_for_each_Enseignant IS NULL";
                        try (PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {
                            deleteStmt.setInt(1, idExtraPost);
                            deleteStmt.executeUpdate();
                        }
                    } else if ((startDate.isAfter(startOfCurrentPeriod) || startDate.isEqual(startOfCurrentPeriod)) && startDate.isBefore(startOfNextPeriod)) {
                        // Delete the record
                        String deleteQuery = "DELETE FROM Extra_post_for_each_Enseignant WHERE id_Extra_post = ? AND effective_end_date_Extra_post_for_each_Enseignant IS NULL";
                        try (PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {
                            deleteStmt.setInt(1, idExtraPost);
                            deleteStmt.executeUpdate();
                        }
                    } else if (startDate.isBefore(startOfPreviousPeriod)) {
                        // Update the end date
                        String updateEndDateQuery = "UPDATE Extra_post_for_each_Enseignant SET effective_end_date_Extra_post_for_each_Enseignant = ? WHERE id_Extra_post = ?  AND effective_end_date_Extra_post_for_each_Enseignant IS NULL";
                        try (PreparedStatement updateStmt = conn.prepareStatement(updateEndDateQuery)) {
                            updateStmt.setDate(1, Date.valueOf(currentDate));
                            updateStmt.setInt(2, idExtraPost);
                            updateStmt.executeUpdate();
                        }

                    }
                     }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return enseignantsToArchive;
    }

    public static boolean hasRecordWithIdTypeExtraPost(int id_type_extra_post) {
        String query = "SELECT COUNT(*) FROM Extra_post_for_each_Enseignant WHERE id_type_extra_post = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id_type_extra_post);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int count = rs.getInt(1);
                return count > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean setTypeExtraPostEndDate(int id) {
        boolean a =hasRecordWithIdTypeExtraPost(id);
        try (Connection conn = getConnection()) {
            // Retrieve the start date from type_extra_post table
            String selectStartDateQuery = "SELECT effective_start_date_type_extra_post FROM type_extra_post WHERE id_type_extra_post = ?";
            LocalDate startDate;
            try (PreparedStatement selectStmt = conn.prepareStatement(selectStartDateQuery)) {
                selectStmt.setInt(1, id);
                ResultSet rs = selectStmt.executeQuery();
                if (rs.next()) {
                    startDate = rs.getDate("effective_start_date_type_extra_post").toLocalDate();
                } else {
                    return false; // Record not found
                }
            }

            LocalDate currentDate = LocalDate.now();
            LocalDate startOfCurrentPeriod = (currentDate.getDayOfMonth() >= 20) ? LocalDate.of(currentDate.getYear(), currentDate.getMonthValue(), 20)
                    : LocalDate.of(currentDate.getYear(), currentDate.getMonthValue() - 1, 20);
            LocalDate startOfPreviousPeriod = startOfCurrentPeriod.minusMonths(1);
            LocalDate startOfNextPeriod = startOfCurrentPeriod.plusMonths(1);
            delete_or_archive_records(id);
if (!a) {
    // Delete the record if the start date is between 20th of the previous month and 19th of the current month
    String deleteQuery = "DELETE FROM type_extra_post WHERE id_type_extra_post = ? AND effective_end_date_type_extra_post IS null";
    try (PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {
        deleteStmt.setInt(1, id);
        deleteStmt.executeUpdate();
    }

}
            // Check the start date and decide the action
           else if ((startDate.isAfter(startOfPreviousPeriod) || startDate.isEqual(startOfPreviousPeriod)) && startDate.isBefore(startOfCurrentPeriod)) {
                // Delete the record if the start date is between 20th of the previous month and 19th of the current month
                String deleteQuery = "DELETE FROM type_extra_post WHERE id_type_extra_post = ?";
                try (PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {
                    deleteStmt.setInt(1, id);
                    deleteStmt.executeUpdate();
                }
            } else if ((startDate.isAfter(startOfCurrentPeriod) || startDate.isEqual(startOfCurrentPeriod)) && startDate.isBefore(startOfNextPeriod)) {
                // Delete the record if the start date is between 20th of the current month and 19th of the next month
                String deleteQuery = "DELETE FROM type_extra_post WHERE id_type_extra_post = ?";
                try (PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {
                    deleteStmt.setInt(1, id);
                    deleteStmt.executeUpdate();
                }
            } else if (startDate.isBefore(startOfPreviousPeriod)) {
                LocalDate endOfPreviousMonth = LocalDate.now();
                String updateEndDateQuery = "UPDATE type_extra_post SET effective_end_date_type_extra_post = ? WHERE id_type_extra_post = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateEndDateQuery)) {
                    updateStmt.setDate(1, Date.valueOf(endOfPreviousMonth));
                    updateStmt.setInt(2, id);
                    updateStmt.executeUpdate();
                }
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }




    public static void createTypeExtraPostEndDate(String job) {

int x=-1;
        for (extra_job b :getavailableExtraPosts()) {
         if (b.getJob_name().equals(job)) {x=b.getId(); break;}
        }
        if (x==-1)return;
        ArrayList<String> id_enseignant_list=   delete_or_archive_records(x);
if (!id_enseignant_list.isEmpty()) {
for (String a :id_enseignant_list) {
        addJob(a, job);}

    }

    }


















































    public static void addOrUpdateTypeExtraPost(String name, int bonus) {
        ArrayList<String> archivedJobs = null;
        int originalId = -1;boolean must= false;

        for (extra_job job : getavailableExtraPosts()) {
            if (job.getJob_name().equals(name)) {
                originalId = job.getId();
                break;
            }
        }

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);

            // Check if the record already exists
            String selectQuery = "SELECT * FROM type_extra_post WHERE nom_Extra_post = ? AND effective_end_date_type_extra_post IS NULL ORDER BY effective_start_date_type_extra_post DESC LIMIT 1";
            try (PreparedStatement stmt = conn.prepareStatement(selectQuery)) {
                stmt.setString(1, name);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        Date startDate = rs.getDate("effective_start_date_type_extra_post");
                        Calendar prevCalendar = Calendar.getInstance();
                        prevCalendar.setTime(startDate);

                        Calendar currentCalendar = Calendar.getInstance();

                        // Check if both records are within the same period (20th day of the month)
                        if (isWithinSamePeriod(prevCalendar, currentCalendar)) {
                            // Update the existing record with new bonus
                            int id = rs.getInt("id_type_extra_post");

                            String updateQuery = "UPDATE type_extra_post SET bonus = ? WHERE id_type_extra_post = ?";
                            try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                                updateStmt.setInt(1, bonus);
                                updateStmt.setInt(2, id);
                                updateStmt.executeUpdate();
                            }
                        } else {
                            // Archive old records
                            int id = rs.getInt("id_type_extra_post");
                            archivedJobs = delete_or_archive_records(id);

                            // Set the effective end date of the old record to the current date
                            String updateEndDateQuery = "UPDATE type_extra_post SET effective_end_date_type_extra_post = ? WHERE id_type_extra_post = ?";
                            try (PreparedStatement updateEndDateStmt = conn.prepareStatement(updateEndDateQuery)) {
                                Calendar endDate = Calendar.getInstance();
                                Date endDateSql = new Date(endDate.getTimeInMillis());
                                updateEndDateStmt.setDate(1, endDateSql);
                                updateEndDateStmt.setInt(2, id);
                                updateEndDateStmt.executeUpdate();
                            }

                            // Insert a new record with the current date as the start date
                            String insertQuery = "INSERT INTO type_extra_post (nom_Extra_post, bonus, effective_start_date_type_extra_post, effective_end_date_type_extra_post) VALUES (?, ?, ?, NULL)";
                            try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                                // Set the effective start date to the current time
                                Calendar calendar = Calendar.getInstance();
                                Date startDateSql = new Date(calendar.getTimeInMillis());

                                insertStmt.setString(1, name);
                                insertStmt.setInt(2, bonus);
                                insertStmt.setDate(3, startDateSql);
                                insertStmt.executeUpdate();
                            }

                            // Ensure all jobs are re-added if needed
                            int newId = -1;
                            for (extra_job job : getavailableExtraPosts()) {
                                if (job.getJob_name().equals(name) && job.getId() != id) {
                                    newId = job.getId();
                                    break;
                                }
                            }

                            if (archivedJobs != null && !archivedJobs.isEmpty() && originalId != newId) {
                                must = true;
                            }
                        }
                    } else {
                        // Insert a new record if it doesn't exist
                        String insertQuery = "INSERT INTO type_extra_post (nom_Extra_post, bonus, effective_start_date_type_extra_post, effective_end_date_type_extra_post) VALUES (?, ?, ?, NULL)";
                        try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                            // Set the effective start date to the current time
                            Calendar calendar = Calendar.getInstance();
                            Date startDateSql = new Date(calendar.getTimeInMillis());

                            insertStmt.setString(1, name);
                            insertStmt.setInt(2, bonus);
                            insertStmt.setDate(3, startDateSql);
                            insertStmt.executeUpdate();
                        }
                    }
                }
            }

            conn.commit();
          if(must)  for (String archivedJob : archivedJobs) {
                addJob(archivedJob, name);
            }  } catch (SQLException e) {
            e.printStackTrace();
            try (Connection conn = getConnection()) {
                conn.rollback();
            } catch (SQLException rollbackException) {
                rollbackException.printStackTrace();
            }
        }
    }


















}

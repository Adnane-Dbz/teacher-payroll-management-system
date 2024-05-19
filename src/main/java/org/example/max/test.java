package org.example.max;

import java.sql.*;
import java.time.LocalDate;

import static org.example.max.TypeExtraPostManager.getConnection;

public class test {
    // Method to check if an IdEnseignant is on CongeDeMaternite during a specific work period
    // Method to check if the Enseignant is currently on CongeDeMaternite
    public boolean isinCongeDeMaterniteIntoday(String idEnseignant) {
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

    // Sample usage
    public static void main(String[] args) {
        test service = new test();
        String idEnseignant = "77";

        boolean isInCongeToday = service.isinCongeDeMaterniteIntoday(idEnseignant);
        System.out.println("Is the Enseignant in CongeDeMaternite today? " + isInCongeToday);
    }
}
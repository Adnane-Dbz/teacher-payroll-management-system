package org.example.max;


import java.sql.*;

public class mm {
    public static final String URL = "jdbc:mysql://127.0.0.1:3306/salary?user=root&password=qwedsa";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static boolean login(String nom, String motDePasse) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            String sql = "SELECT * FROM User WHERE nom_user = ? AND mot_de_pass = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, nom);
            pstmt.setString(2, motDePasse);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                System.out.println("Connexion réussie !");
                return true;
            } else {
                System.out.println("Nom d'utilisateur ou mot de passe incorrect.");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeResources(rs, pstmt, conn);
        }
    }

    public static boolean inscrireUtilisateur(String nom, String motDePasse, String confirmation_mot_p, String reponse, int nombreQuestion) {
        if (motDePasse.equals(confirmation_mot_p)) {
            Connection conn = null;
            PreparedStatement pstmt = null;
            ResultSet rs = null;
            try {
                conn = getConnection();

                String checkUserSql = "SELECT * FROM User WHERE nom_user = ? AND mot_de_pass = ?";
                pstmt = conn.prepareStatement(checkUserSql);
                pstmt.setString(1, nom);
                pstmt.setString(2, motDePasse);
                rs = pstmt.executeQuery();
                if (rs.next()) {
                    System.out.println("Un utilisateur avec le même nom et le même mot de passe existe déjà.");
                    return false;
                }

                String insertUserSql = "INSERT INTO User (nom_user, mot_de_pass, reponse, nombre_qustion) VALUES (?, ?, ?, ?)";
                pstmt = conn.prepareStatement(insertUserSql);
                pstmt.setString(1, nom);
                pstmt.setString(2, motDePasse);
                pstmt.setString(3, reponse);
                pstmt.setInt(4, nombreQuestion);
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Utilisateur inscrit avec succès !");
                    return true;
                } else {
                    System.out.println("Erreur lors de l'inscription de l'utilisateur.");
                    return false;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            } finally {
                closeResources(rs, pstmt, conn);
            }
        } else {
            System.out.println("Le mot de passe et sa confirmation ne correspondent pas.");
            return false;
        }
    }

    public static boolean verifierReponse(String nom, String reponse, int nombreQuestion) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            String sql = "SELECT * FROM User WHERE nom_user = ? AND reponse = ? AND nombre_qustion = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, nom);
            pstmt.setString(2, reponse);
            pstmt.setInt(3, nombreQuestion);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                System.out.println("Réponse correcte !");
                return true;
            } else {
                System.out.println("Nom d'utilisateur, réponse ou nombre de questions incorrect(s).");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeResources(rs, pstmt, conn);
        }
    }

    private static void closeResources(ResultSet rs, PreparedStatement pstmt, Connection conn) {
        try {
            if (rs != null) {
                rs.close();
            }
            if (pstmt != null) {
                pstmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

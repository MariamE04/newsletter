package app.persistence;

import app.entities.Newsletters;
import app.exceptions.DatabaseException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NewsletterMapper {
    public static List<Newsletters> getAllNewsletters(MyConnectionPool myConnectionPool) throws DatabaseException {
        List<Newsletters> newsletters = new ArrayList<>();
        String sql = "SELECT * FROM newsletters";

        try (Connection conn = myConnectionPool.getConnection();  // Sørg for, at connection poolen er korrekt brugt
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Newsletters newsletter = new Newsletters(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("filename"),
                        rs.getString("teasertext"),
                        rs.getString("thumbnail_name"),
                        rs.getDate("published")
                );
                newsletters.add(newsletter);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Fejl ved hentning af nyhedsbreve", e.getMessage());
        }

        return newsletters;
    }

    public static void createNewsletter(Newsletters newsletter, MyConnectionPool myConnectionPool) throws DatabaseException {
        String sql = "INSERT INTO newsletters (title, filename, teasertext, thumbnail_name, published) VALUES (?, ?, ?, ?, ?)";

        try (
                Connection connection = myConnectionPool.getConnection();  // Brug connection poolen
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {
            ps.setString(1, newsletter.getTitle());
            ps.setString(2, newsletter.getFilename());
            ps.setString(3, newsletter.getTeasertext());
            ps.setString(4, newsletter.getThumbnail_name());
            ps.setDate(5, new java.sql.Date(newsletter.getPublished().getTime()));

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected != 1) {
                throw new DatabaseException("Fejl ved oprettelse af nyhedsbrev");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Fejl ved indsættelse af nyhedsbrev: " + e.getMessage());
        }
    }


    public static int subscribe(String email, MyConnectionPool connectionPool) throws DatabaseException {

        String sql = "INSERT INTO subscriber (email, created) VALUES (?, CURRENT_DATE) ON CONFLICT (email) DO NOTHING";
        try (
                Connection connection = connectionPool.getConnection(); PreparedStatement ps = connection.prepareStatement(sql)
        ) {
            ps.setString(1, email);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected;
        }
        catch (SQLException e) {
            String msg = "Der er sket en fejl under din tilmelding til nyhedsbrev. Prøv igen";
            throw new DatabaseException(msg, e.getMessage());
        }
    }


    public static Newsletters getLatestNewsletter(MyConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT * FROM newsletters ORDER BY published DESC LIMIT 1";

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {
            if (rs.next()) {
                return new Newsletters(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("filename"),
                        rs.getString("teasertext"),
                        rs.getString("thumbnail_name"),
                        rs.getDate("published")
                );
            } else {
                throw new DatabaseException("Ingen nyhedsbreve fundet");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Fejl ved hentning af seneste nyhedsbrev: " + e.getMessage());
        }
    }

    //Tilføj en metode, der søger efter nyhedsbreve baseret på en tekststreng i teasertext
    public static List<Newsletters> searchNewsletters(String searchTerm, MyConnectionPool connectionPool) throws DatabaseException {
        List<Newsletters> results = new ArrayList<>();
        String sql = "SELECT * FROM newsletters WHERE teasertext ILIKE ?"; //ILIKE = Søgningen case-insensitive (PostgreSQL)

        try(Connection connection = connectionPool.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
        ){
            ps.setString(1, "%" + searchTerm + "%"); //bruger wildcard (%) for at søge på delord i teasertext
            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                results.add(new Newsletters(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("filename"),
                        rs.getString("teasertext"),
                        rs.getString("thumbnail_name"),
                        rs.getDate("published")
                ));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Fejl ved søgning efter nyhedsbreve: " + e.getMessage());
        }
        return results;
    }

}

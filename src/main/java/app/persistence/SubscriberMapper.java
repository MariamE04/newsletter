package app.persistence;

import app.entities.Subscriber;
import app.exceptions.DatabaseException;


import java.sql.*;
import java.time.DateTimeException;

public class SubscriberMapper
{
    public static Subscriber login(String email, MyConnectionPool connectionPool) throws DatabaseException
    {
        String sql = "SELECT * FROM users WHERE email=?";

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        )
        {
            ps.setString(1, email);

            ResultSet rs = ps.executeQuery();
            if (rs.next())
            {
                boolean created = rs.getBoolean("created");
                return new Subscriber(email, created);
            } else
            {
                throw new DatabaseException("Fejl i login. Prøv igen");
            }
        }
        catch (SQLException e)
        {
            throw new DatabaseException("DB fejl: " + e.getMessage());
        }
    }

    public static void createUser(String email, MyConnectionPool connectionPool) throws DatabaseException
    {
        String sql = "INSERT INTO users (email, created) VALUES (?, ?)";

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        )
        {
            ps.setString(1, email);
            ps.setBoolean(2, false); // Ny bruger har created=false

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected != 1)
            {
                throw new DatabaseException("Fejl ved oprettelse af ny bruger");
            }
        }
        catch (SQLException e)
        {
            String msg = "Der er sket en fejl. Prøv igen";
            if (e.getMessage().startsWith("ERROR: duplicate key value "))
            {
                msg = "Brugernavnet findes allerede. Vælg et andet";
            }
            throw new DatabaseException(msg, e.getMessage());
        }
    }



    public static Subscriber signUp(String email, MyConnectionPool myConnectionPool) {
        String sql = "INSERT INTO subscriber (email,created) VALUES (?,?) ON CONFLICT (email) DO NOTHING";

        try (
                Connection connection = myConnectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, email);
            ps.setBoolean(2,true);

            int rowsAffected = ps.executeUpdate();
            return new Subscriber(email, true);
        } catch (SQLException e) {
            throw new DateTimeException("DB fejl: " + e.getMessage(), e);
        }
    }
}

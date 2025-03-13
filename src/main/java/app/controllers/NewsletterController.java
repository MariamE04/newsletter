package app.controllers;

import app.entities.Newsletters;
import app.exceptions.DatabaseException;
import app.persistence.NewsletterMapper;
import app.persistence.MyConnectionPool;
import io.javalin.http.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class NewsletterController {

    private static final Logger LOGGER = Logger.getLogger(NewsletterController.class.getName());

    private final MyConnectionPool connectionPool;

    public NewsletterController(MyConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    public void viewNewsletters(Context ctx) throws DatabaseException {
        // Hent nyhedsbreve fra databasen
        List<Newsletters> newsletters = null;

        try {
            newsletters = NewsletterMapper.getAllNewsletters(connectionPool);
        } catch (DatabaseException e) {
            LOGGER.severe("Fejl ved hentning af nyhedsbreve: " + e.getMessage());
            ctx.result("Der opstod en fejl ved hentning af nyhedsbreve.");
            return;
        }

        // Hvis der ikke er nyhedsbreve, log en besked
        if (newsletters == null || newsletters.isEmpty()) {
            newsletters = new ArrayList<>(); // Sørg for at sende en tom liste, hvis intet blev fundet
        }

        // Sæt newsletters som attribut i konteksten
        ctx.attribute("newsletters", newsletters);

        // Render Thymeleaf-skabelonen
        ctx.render("newsletters.html");
    }

}

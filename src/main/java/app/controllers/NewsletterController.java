package app.controllers;

import app.entities.Newsletters;
import app.exceptions.DatabaseException;
import app.persistence.NewsletterMapper;
import app.persistence.MyConnectionPool;
import io.javalin.http.Context;
import io.javalin.http.UploadedFile;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NewsletterController {

    private static final Logger LOGGER = Logger.getLogger(NewsletterController.class.getName());
    private final MyConnectionPool connectionPool;

    public NewsletterController(MyConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    public void viewNewsletters(Context ctx) {
        try {
            List<Newsletters> newsletters = NewsletterMapper.getAllNewsletters(connectionPool);
            ctx.attribute("newsletters", newsletters);
            ctx.render("newsletters.html");
        } catch (DatabaseException e) {
            LOGGER.severe("Fejl ved hentning af nyhedsbreve: " + e.getMessage());
            ctx.result("Der opstod en fejl ved hentning af nyhedsbreve.");
        }
    }

    public void addNewsletter(Context ctx) {
        try {
            // Opret nødvendige mapper, hvis de ikke eksisterer
            Path newslettersDir = Path.of("files");
            Path thumbnailsDir = Path.of("files");

            if (!Files.exists(newslettersDir)) {
                Files.createDirectories(newslettersDir);
            }
            if (!Files.exists(thumbnailsDir)) {
                Files.createDirectories(thumbnailsDir);
            }

            // Hent og valider formular-data
            String title = ctx.formParam("title");
            String teaserText = ctx.formParam("teaser_text");
            String publishedDateString = ctx.formParam("published_date");
            String password = ctx.formParam("password");

            if (password == null || !password.equals("1234")) {
                ctx.status(403).result("Forkert adgangskode!");
                return;
            }
            if (title == null || teaserText == null || publishedDateString == null) {
                ctx.status(400).result("Manglende påkrævede felter.");
                return;
            }

            // Konverter dato korrekt
            LocalDate publishedDate;
            try {
                publishedDate = LocalDate.parse(publishedDateString, DateTimeFormatter.ISO_LOCAL_DATE);
            } catch (Exception e) {
                ctx.status(400).result("Ugyldigt datoformat. Brug YYYY-MM-DD.");
                return;
            }

            // Håndter uploadede filer
            UploadedFile pdfFile = ctx.uploadedFile("newsletter_file");
            UploadedFile thumbnailFile = ctx.uploadedFile("thumbnail_file");

            if (pdfFile == null || thumbnailFile == null) {
                ctx.status(400).result("Både PDF og thumbnail er påkrævet.");
                return;
            }

            // Validér filtyper
            if (!"application/pdf".equalsIgnoreCase(pdfFile.contentType())) {
                ctx.status(400).result("Kun PDF-filer er tilladt.");
                return;
            }
            if (!"image/png".equalsIgnoreCase(thumbnailFile.contentType())) {
                ctx.status(400).result("Kun PNG-billeder er tilladt.");
                return;
            }

            String pdfFilename = Path.of(title).getFileName().toString().replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
            if (!pdfFilename.toLowerCase().endsWith(".pdf")) {
                pdfFilename += ".pdf";
            }


            String thumbnailFilename = title.replaceAll("[^a-zA-Z0-9\\.\\-]", "_") + ".png";
            Path pdfPath = newslettersDir.resolve(pdfFilename);
            Path thumbnailPath = thumbnailsDir.resolve(thumbnailFilename);

            // Kopier filer til de respektive mapper
            try (InputStream pdfInputStream = pdfFile.content();
                 InputStream thumbnailInputStream = thumbnailFile.content()) {
                Files.copy(pdfInputStream, pdfPath, StandardCopyOption.REPLACE_EXISTING);
                Files.copy(thumbnailInputStream, thumbnailPath, StandardCopyOption.REPLACE_EXISTING);
            }

            // Konvertering fra LocalDate til Date
            Date publishedDateAsDate = Date.from(publishedDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

            // Opret og gem nyhedsbrev i databasen
            Newsletters newsletter = new Newsletters(title, pdfFilename, teaserText, thumbnailFilename, publishedDateAsDate);
            NewsletterMapper.createNewsletter(newsletter, connectionPool);

            ctx.redirect("/newsletters");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Fejl ved upload af nyhedsbrev: " + e.getMessage(), e);
            ctx.status(500).result("Der opstod en fejl ved upload af nyhedsbrev.");
        }
    }

    public void viewLatestNewsletter(Context ctx) {
        try {
            Newsletters latestNewsletter = NewsletterMapper.getLatestNewsletter(connectionPool);
            ctx.attribute("newsletter", latestNewsletter);  // Gem det nyeste nyhedsbrev i attributten
            ctx.render("latest_newsletter.html");  // Opret en HTML-side, der viser det nyeste nyhedsbrev
        } catch (DatabaseException e) {
            LOGGER.severe("Fejl ved hentning af nyeste nyhedsbrev: " + e.getMessage());
            ctx.result("Der opstod en fejl ved hentning af det nyeste nyhedsbrev.");
        }
    }

    public void searchNewsletters(Context ctx) {
        String searchTerm = ctx.queryParam("query");

        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            ctx.redirect("/newsletters");
            return;
        }

        try {
            List<Newsletters> searchResults = NewsletterMapper.searchNewsletters(searchTerm, connectionPool);
            ctx.attribute("newsletters", searchResults);
            ctx.attribute("searchQuery", searchTerm); // Gem søgeteksten så den kan vises i inputfeltet
            ctx.render("newsletters.html");
        } catch (DatabaseException e) {
            LOGGER.severe("Fejl ved søgning: " + e.getMessage());
            ctx.result("Der opstod en fejl ved søgning.");
        }
    }
}


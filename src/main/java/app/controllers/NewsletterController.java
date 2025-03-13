package app.controllers;

import app.entities.Newsletters;
import app.exceptions.DatabaseException;
import app.persistence.NewsletterMapper;
import app.persistence.MyConnectionPool;
import io.javalin.http.Context;
import io.javalin.http.UploadedFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
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
            // 1. Hent og valider formular-data
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

            // 2. Konverter dato korrekt
            LocalDate publishedDate;
            try {
                publishedDate = LocalDate.parse(publishedDateString, DateTimeFormatter.ISO_LOCAL_DATE);
            } catch (Exception e) {
                ctx.status(400).result("Ugyldigt datoformat. Brug YYYY-MM-DD.");
                return;
            }

            // 3. Håndter uploadede filer
            UploadedFile pdfFile = ctx.uploadedFile("newsletter_file");
            UploadedFile thumbnailFile = ctx.uploadedFile("thumbnail_file");

            if (pdfFile == null || thumbnailFile == null) {
                ctx.status(400).result("Både PDF og thumbnail er påkrævet.");
                return;
            }

            // 4. Validér filtyper
            if (!"application/pdf".equalsIgnoreCase(pdfFile.contentType())) {
                ctx.status(400).result("Kun PDF-filer er tilladt.");
                return;
            }
            if (!"image/png".equalsIgnoreCase(thumbnailFile.contentType())) {
                ctx.status(400).result("Kun PNG-billeder er tilladt.");
                return;
            }

            // 5. Gem filer i upload-mapper
            String pdfFilename = pdfFile.filename();
            String thumbnailFilename = thumbnailFile.filename();
            Path pdfPath = Path.of("uploads/newsletters", pdfFilename);
            Path thumbnailPath = Path.of("uploads/thumbnails", thumbnailFilename);

            Files.copy(pdfFile.content(), pdfPath, StandardCopyOption.REPLACE_EXISTING);
            Files.copy(thumbnailFile.content(), thumbnailPath, StandardCopyOption.REPLACE_EXISTING);

            // Konvertering fra LocalDate til Date
            Date publishedDateAsDate = Date.from(publishedDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

            // 6. Opret og gem nyhedsbrev i databasen
            Newsletters newsletter = new Newsletters(title, teaserText, pdfFilename, thumbnailFilename, publishedDateAsDate);
            NewsletterMapper.createNewsletter(newsletter, connectionPool);

            ctx.redirect("/newsletters");
        } catch (Exception e) {
            LOGGER.severe("Fejl ved upload af nyhedsbrev: " + e.getMessage());
            ctx.status(500).result("Der opstod en fejl ved upload af nyhedsbrev.");
        }
    }
}

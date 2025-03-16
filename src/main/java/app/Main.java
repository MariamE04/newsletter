package app;

import app.config.SessionConfig;
import app.config.ThymeleafConfig;
import app.controllers.HomeController;
import app.controllers.NewsletterController;
import app.exceptions.DatabaseException;
import app.persistence.MyConnectionPool;
import app.persistence.NewsletterMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.staticfiles.Location;
import io.javalin.rendering.template.JavalinThymeleaf;
import java.util.logging.Logger;

public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";
    private static final String URL = "jdbc:postgresql://localhost:5432/%s?currentSchema=public";
    private static final String DB = "nyhedsbreve";

    private static final MyConnectionPool connectionPool = MyConnectionPool.getInstance(USER, PASSWORD, URL, DB);
    private static final NewsletterController newsletterController = new NewsletterController(connectionPool);
    private static final HomeController homeController = new HomeController(connectionPool);


    public static void main(String[] args)
    {

        // Initializing Javalin and Jetty webserver
        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/public");
            config.staticFiles.add(staticFiles -> {
                staticFiles.hostedPath = "/files";   // Serve at http://localhost:7000/files
                staticFiles.directory = "files";    // Serve from "files" folder in working directory
                staticFiles.location = Location.EXTERNAL; // Load from outside the JAR
            });
            config.jetty.modifyServletContextHandler(handler ->  handler.setSessionHandler(SessionConfig.sessionConfig()));
            config.fileRenderer(new JavalinThymeleaf(ThymeleafConfig.templateEngine()));
        }).start(7070);


        // Routing
        app.get("/", ctx -> ctx.render("index.html"));
        app.get("/signup", ctx -> viewSignupPage(ctx));
        app.post("/signup", homeController::subscribe);

        // Routing for nyhedsbreve, som nu bruger controllerens viewNewsletters metode
        app.get("/newsletters", newsletterController::viewNewsletters);
        app.post("/upload", ctx -> newsletterController.addNewsletter(ctx));  // Add a new newsletter
        app.get("/upload", ctx -> ctx.render("upload.html"));
        app.get("/latest-newsletter", newsletterController::viewLatestNewsletter);
    }

    private static void viewSignupPage(Context ctx) {
        String message = "Lær mere via Campus Lyngbys nyhedsbrev";
        ctx.attribute("message", message);
        ctx.render("signup.html");
    }
}
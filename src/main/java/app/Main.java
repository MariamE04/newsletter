package app;

import app.config.SessionConfig;
import app.config.ThymeleafConfig;
import app.entities.Newsletters;
import app.entities.Subscriber;
import app.exceptions.DatabaseException;
import app.persistence.MyConnectionPool;
import app.persistence.NewsletterMapper;
import app.persistence.SubscriberMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.rendering.template.JavalinThymeleaf;

import java.util.List;
import java.util.logging.Logger;

public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";
    private static final String URL = "jdbc:postgresql://localhost:5432/%s?currentSchema=public";
    private static final String DB = "nyhedsbreve";

    private static final MyConnectionPool connectionPool = MyConnectionPool.getInstance(USER, PASSWORD, URL, DB);


    public static void main(String[] args)
    {

        // Initializing Javalin and Jetty webserver
        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/public");
            config.jetty.modifyServletContextHandler(handler ->  handler.setSessionHandler(SessionConfig.sessionConfig()));
            config.fileRenderer(new JavalinThymeleaf(ThymeleafConfig.templateEngine()));
        }).start(7070);

        // Routing

        app.get("/", ctx -> ctx.render("index.html"));
        app.get("/signup", ctx -> viewSignupPage(ctx));
        app.post("/signup", ctx -> SignUp(ctx));
        app.get("/newsletters", ctx -> viewNewsletters(ctx));

    }

    private static void viewNewsletters(Context ctx) throws DatabaseException {
        // Hent nyhedsbreve fra databasen
        List<Newsletters> newsletters = NewsletterMapper.getAllNewsletters(connectionPool);

        // Log nyhedsbrevenes størrelse for debugging
        System.out.println("Antal nyhedsbreve fundet: " + newsletters.size());

        // Hvis der ikke er nyhedsbreve, log en besked
        if (newsletters == null || newsletters.isEmpty()) {
            System.out.println("Der er ingen nyhedsbreve at vise.");
        }

        // Sæt newsletters som attribut i konteksten
        ctx.attribute("newsletters", newsletters);

        // Render Thymeleaf-skabelonen
        ctx.render("newsletters.html");
    }


    private static void viewSignupPage(Context ctx) {
        String message = "Lær mere via Campus Lyngbys nyhedsbrev";
        ctx.attribute("message", message);
        ctx.render("signup.html");
    }

    private static void SignUp(Context ctx) {
        String email = ctx.formParam("email");

        // Tjek om email er ikke null og ikke tom
        if (email != null && !email.isEmpty()) {

            // Hent subscriber fra databasen via SubscriberMapper
            Subscriber subscriber = SubscriberMapper.signUp(email, connectionPool);

            // Gem information og vis success med besked
            ctx.attribute("you have subscribed to Campus Lyngby Newsletter", subscriber + " " + email);
            ctx.render("signup.html");
        } else {
            // Hvis email er tom, vis en fejlbesked
            ctx.attribute("message", "Email cannot be empty");
            ctx.render("signup.html");
        }
    }

    private static void login(Context ctx){
        String userName = ctx.formParam("username");
        String password = ctx.formParam("password");
        if (!password.equals("1234")) {
            ctx.redirect("login.html");
        }
        ctx.attribute("username", userName);
        ctx.render("index.html");
    }

}
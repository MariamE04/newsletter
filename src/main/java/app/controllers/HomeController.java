package app.controllers;

import app.exceptions.DatabaseException;
import app.persistence.MyConnectionPool;
import app.persistence.NewsletterMapper;
import io.javalin.http.Context;


public class HomeController {

    private static MyConnectionPool connectionPool;

    public HomeController(MyConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    public void subscribe(Context ctx) throws DatabaseException {
        String email = ctx.formParam("email");
        String message = "";
        if (email != null) {
            int result = NewsletterMapper.subscribe(email, connectionPool );
            if (result == 1) {
                message = "Tak for din tilmelding";
            } else if (result == 0) {
                message = "Tak, men du var allerede tilmeldt";
            }
            ctx.attribute("message", message);
            ctx.render("index.html");
        }
    }

}
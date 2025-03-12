package app.entities;

public class Users {
    private String email;
    private int password;

    public Users(String email, int password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public int getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "users{" +
                "email='" + email + '\'' +
                ", password=" + password +
                '}';
    }
}

package app.entities;

public class Subsciber {
    private String email;
    private boolean created;

    public Subsciber(String email, boolean created) {
        this.email = email;
        this.created = false;
    }

    public String getEmail() {
        return email;
    }

    public boolean isCreated() {
        return created;
    }

    @Override
    public String toString() {
        return "subsciber{" +
                "email='" + email + '\'' +
                ", created=" + created +
                '}';
    }
}

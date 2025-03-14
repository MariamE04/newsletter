package app.entities;

public class Subscriber {
    private String email;
    private boolean created;

    public Subscriber(String email, boolean created) {
        this.email = email;
        this.created = created;
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

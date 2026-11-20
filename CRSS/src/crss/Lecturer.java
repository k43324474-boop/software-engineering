package crss;

/** A lecturer. The CoD is a kind of Lecturer (see CoD). */
public class Lecturer {
    private final String id;
    private final String name;
    private final String password;

    public Lecturer(String id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public boolean checkPassword(String p) { return password.equals(p); }
    @Override public String toString() { return name + " (" + id + ")"; }
}

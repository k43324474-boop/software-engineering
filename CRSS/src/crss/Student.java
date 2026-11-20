package crss;

import java.util.HashSet;
import java.util.Set;

/** A student: identity plus the courses already passed (used for prerequisite checks). */
public class Student {
    private final String id;
    private final String name;
    private final String password;
    private final Set<String> completedCourses = new HashSet<>();

    public Student(String id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public boolean checkPassword(String p) { return password.equals(p); }
    public Set<String> getCompletedCourses() { return completedCourses; }
    public void addCompletedCourse(String code) { completedCourses.add(code); }
    public boolean hasCompleted(String code) { return completedCourses.contains(code); }
}

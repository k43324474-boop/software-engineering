package crss;

import java.util.ArrayList;
import java.util.List;

/** A course offering in one semester. */
public class Course {
    private final String code;
    private String title;
    private int credits;
    private int capacity;
    private final String semester;
    private final List<String> prerequisites = new ArrayList<>();
    private Lecturer lecturer;

    public Course(String code, String title, int credits, int capacity, String semester) {
        this.code = code;
        this.title = title;
        this.credits = credits;
        this.capacity = capacity;
        this.semester = semester;
    }

    public String getCode() { return code; }
    public String getTitle() { return title; }
    public int getCredits() { return credits; }
    public int getCapacity() { return capacity; }
    public String getSemester() { return semester; }
    public List<String> getPrerequisites() { return prerequisites; }
    public Lecturer getLecturer() { return lecturer; }

    public void setTitle(String title) { this.title = title; }
    public void setCredits(int credits) { this.credits = credits; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public void setLecturer(Lecturer lecturer) { this.lecturer = lecturer; }
    public void addPrerequisite(String code) { prerequisites.add(code); }

    @Override public String toString() {
        return code + " " + title + " (" + credits + " cr, cap " + capacity + ", "
                + (lecturer == null ? "no lecturer" : lecturer.getName()) + ")";
    }
}

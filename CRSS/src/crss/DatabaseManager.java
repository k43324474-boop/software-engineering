package crss;

import java.util.*;

/** Stores and retrieves all data. In-memory with seed data; swap internals for SQLite later if needed. */
public class DatabaseManager {
    private final Map<String, Student> students = new LinkedHashMap<>();
    private final Map<String, Lecturer> lecturers = new LinkedHashMap<>();
    private final Map<String, Course> courses = new LinkedHashMap<>();
    private final List<Room> rooms = new ArrayList<>();
    private final List<Registration> registrations = new ArrayList<>();
    private final Map<String, Schedule> schedules = new LinkedHashMap<>(); // keyed by course code

    // ---- users ----
    public void addStudent(Student s) { students.put(s.getId(), s); }
    public void addLecturer(Lecturer l) { lecturers.put(l.getId(), l); }
    public Student getStudent(String id) { return students.get(id); }
    public Lecturer getLecturer(String id) { return lecturers.get(id); }
    public Collection<Student> getAllStudents() { return students.values(); }
    public Collection<Lecturer> getAllLecturers() { return lecturers.values(); }

    // ---- courses ----
    public void saveCourse(Course c) { courses.put(c.getCode(), c); }
    public Course getCourse(String code) { return courses.get(code); }
    public List<Course> getCourses(String semester) {
        List<Course> out = new ArrayList<>();
        for (Course c : courses.values()) if (c.getSemester().equals(semester)) out.add(c);
        return out;
    }
    public List<Course> getCoursesByLecturer(Lecturer l) {
        List<Course> out = new ArrayList<>();
        for (Course c : courses.values()) if (c.getLecturer() != null && c.getLecturer().getId().equals(l.getId())) out.add(c);
        return out;
    }

    // ---- rooms ----
    public void addRoom(Room r) { rooms.add(r); }
    public List<Room> getRooms() { return rooms; }

    // ---- registrations ----
    public void saveRegistration(Registration r) { registrations.add(r); }
    public boolean isRegistered(Student s, Course c) {
        for (Registration r : registrations)
            if (r.getStatus() == Registration.Status.CONFIRMED
                    && r.getStudent() == s && r.getCourse() == c) return true;
        return false;
    }
    public int countConfirmed(Course c) {
        int n = 0;
        for (Registration r : registrations)
            if (r.getStatus() == Registration.Status.CONFIRMED && r.getCourse() == c) n++;
        return n;
    }
    public List<Registration> getRegistrations(Student s, String semester) {
        List<Registration> out = new ArrayList<>();
        for (Registration r : registrations)
            if (r.getStatus() == Registration.Status.CONFIRMED
                    && r.getStudent() == s && r.getSemester().equals(semester)) out.add(r);
        return out;
    }
    public List<Student> getStudentsIn(Course c) {
        List<Student> out = new ArrayList<>();
        for (Registration r : registrations)
            if (r.getStatus() == Registration.Status.CONFIRMED && r.getCourse() == c) out.add(r.getStudent());
        return out;
    }
    public boolean dropRegistration(Student s, Course c) {
        for (Registration r : registrations)
            if (r.getStatus() == Registration.Status.CONFIRMED && r.getStudent() == s && r.getCourse() == c) {
                r.drop();
                return true;
            }
        return false;
    }

    // ---- schedule ----
    public Schedule getScheduleFor(Course c) { return schedules.get(c.getCode()); }
    public void saveSchedule(List<Schedule> entries) {
        schedules.clear();
        for (Schedule e : entries) schedules.put(e.getCourse().getCode(), e);
    }
    public List<Schedule> getSchedule() { return new ArrayList<>(schedules.values()); }

    // ---- login (returns Student, CoD, Lecturer, or null) ----
    public Object login(String id, String password) {
        Student s = students.get(id);
        if (s != null && s.checkPassword(password)) return s;
        Lecturer l = lecturers.get(id);
        if (l != null && l.checkPassword(password)) return l;
        return null;
    }

    /** Seed data so the demo works immediately. Replace with your own. */
    public static DatabaseManager withSeedData() {
        DatabaseManager db = new DatabaseManager();
        db.addLecturer(new CoD("C001", "Dr. Chairman", "cod123"));
        Lecturer l1 = new Lecturer("L001", "Ms. Wanjiru", "lec123");
        Lecturer l2 = new Lecturer("L002", "Mr. Otieno", "lec123");
        db.addLecturer(l1);
        db.addLecturer(l2);

        Student s1 = new Student("S001", "Amina", "stu123");
        Student s2 = new Student("S002", "Brian", "stu123");
        s1.addCompletedCourse("CCS1101");
        db.addStudent(s1);
        db.addStudent(s2);

        String sem = "2026-S2";
        Course c1 = new Course("CCS1101", "Intro to Programming", 3, 40, sem);
        Course c2 = new Course("CCS2101", "Data Structures", 3, 2, sem);
        c2.addPrerequisite("CCS1101");
        Course c3 = new Course("CCS3201", "Software Engineering", 3, 40, sem);
        Course c4 = new Course("CCS2202", "Databases", 3, 40, sem);
        c1.setLecturer(l1); c2.setLecturer(l1); c3.setLecturer(l2); c4.setLecturer(l2);
        for (Course c : List.of(c1, c2, c3, c4)) db.saveCourse(c);

        db.addRoom(new Room("Lab A", 30));
        db.addRoom(new Room("Hall 1", 100));
        db.addRoom(new Room("Room 12", 50));
        return db;
    }
}

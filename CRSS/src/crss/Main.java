package crss;

import java.util.List;
import java.util.Scanner;

/** Console UI: login, then a menu per role. */
public class Main {
    static final String SEMESTER = "2026-S2";
    static final DatabaseManager db = DatabaseManager.withSeedData();
    static final ScheduleGenerator generator = new ScheduleGenerator();
    static Scanner in;

    public static void main(String[] args) {
        in = new Scanner(System.in);
        System.out.println("=== SPAS Course Registration and Scheduling System ===");
        while (true) {
            System.out.print("\nUser ID (or 'quit'): ");
            if (!in.hasNextLine()) return;
            String id = in.nextLine().trim();
            if (id.equalsIgnoreCase("quit")) return;
            System.out.print("Password: ");
            if (!in.hasNextLine()) return;
            Object user = db.login(id, in.nextLine().trim());
            if (user == null) { System.out.println("Wrong ID or password."); continue; }
            if (user instanceof Student s) studentMenu(s);
            else if (user instanceof CoD c) codMenu(c);
            else if (user instanceof Lecturer l) lecturerMenu(l);
        }
    }

    static String ask(String prompt) {
        System.out.print(prompt);
        return in.hasNextLine() ? in.nextLine().trim() : "";
    }

    static void studentMenu(Student s) {
        System.out.println("\nWelcome, " + s.getName());
        while (true) {
            System.out.println("\n1 View courses  2 Register  3 Drop  4 Weekly timetable  0 Logout");
            switch (ask("> ")) {
                case "1" -> db.getCourses(SEMESTER).forEach(c -> System.out.println("  " + c
                        + " seats left: " + (c.getCapacity() - db.countConfirmed(c))));
                case "2" -> {
                    Course c = db.getCourse(ask("Course code: ").toUpperCase());
                    if (c == null) { System.out.println("No such course."); break; }
                    try {
                        Registration.register(s, c, db);
                        System.out.println("Registered for " + c.getCode() + ".");
                    } catch (RegistrationException e) { System.out.println("Rejected: " + e.getMessage()); }
                }
                case "3" -> {
                    Course c = db.getCourse(ask("Course code: ").toUpperCase());
                    System.out.println(c != null && db.dropRegistration(s, c) ? "Dropped." : "Not registered for that course.");
                }
                case "4" -> showTimetable(s);
                case "0" -> { return; }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    static void showTimetable(Student s) {
        List<Registration> regs = db.getRegistrations(s, SEMESTER);
        if (regs.isEmpty()) { System.out.println("You have no registered courses."); return; }
        for (Registration r : regs) {
            Schedule sc = db.getScheduleFor(r.getCourse());
            System.out.println("  " + (sc != null ? sc.toString()
                    : r.getCourse().getCode() + " " + r.getCourse().getTitle() + "  (not yet scheduled)"));
        }
    }

    static void codMenu(CoD c) {
        System.out.println("\nWelcome, " + c.getName());
        while (true) {
            System.out.println("\n1 Add course  2 Edit course  3 Assign lecturer  4 Generate schedule"
                    + "  5 View schedule  6 Manage enrolment (remove student)  0 Logout");
            switch (ask("> ")) {
                case "1" -> addCourse();
                case "2" -> editCourse();
                case "3" -> {
                    Course course = db.getCourse(ask("Course code: ").toUpperCase());
                    Lecturer l = db.getLecturer(ask("Lecturer ID: ").toUpperCase());
                    if (course == null || l == null) System.out.println("Unknown course or lecturer.");
                    else { course.setLecturer(l); System.out.println("Assigned " + l.getName() + " to " + course.getCode()); }
                }
                case "4" -> {
                    try {
                        List<Schedule> out = generator.generate(db, SEMESTER);
                        System.out.println("Schedule generated (" + out.size() + " classes).");
                    } catch (ScheduleException e) { System.out.println(e.getMessage()); }
                }
                case "5" -> {
                    List<Schedule> all = db.getSchedule();
                    if (all.isEmpty()) System.out.println("No schedule generated yet.");
                    all.forEach(e -> System.out.println("  " + e + "  [" + e.getLecturer().getName() + "]"));
                }
                case "6" -> {
                    Course course = db.getCourse(ask("Course code: ").toUpperCase());
                    Student st = db.getStudent(ask("Student ID: ").toUpperCase());
                    System.out.println(course != null && st != null && db.dropRegistration(st, course)
                            ? "Student removed from course." : "No such enrolment.");
                }
                case "0" -> { return; }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    static void addCourse() {
        String code = ask("Code: ").toUpperCase();
        if (db.getCourse(code) != null) { System.out.println("Code already exists."); return; }
        try {
            String title = ask("Title: ");
            int credits = Integer.parseInt(ask("Credit hours: "));
            int cap = Integer.parseInt(ask("Capacity: "));
            if (credits <= 0 || cap <= 0 || title.isEmpty()) throw new NumberFormatException();
            Course c = new Course(code, title, credits, cap, SEMESTER);
            String pre = ask("Prerequisite code (blank for none): ").toUpperCase();
            if (!pre.isEmpty()) c.addPrerequisite(pre);
            db.saveCourse(c);
            System.out.println("Course added.");
        } catch (NumberFormatException e) { System.out.println("Invalid input; course not added."); }
    }

    static void editCourse() {
        Course c = db.getCourse(ask("Course code: ").toUpperCase());
        if (c == null) { System.out.println("No such course."); return; }
        try {
            String t = ask("New title (blank = keep): ");
            if (!t.isEmpty()) c.setTitle(t);
            String cap = ask("New capacity (blank = keep): ");
            if (!cap.isEmpty()) {
                int n = Integer.parseInt(cap);
                if (n < db.countConfirmed(c)) { System.out.println("Capacity below current enrolment; not changed."); return; }
                c.setCapacity(n);
            }
            System.out.println("Updated: " + c);
        } catch (NumberFormatException e) { System.out.println("Invalid number; not changed."); }
    }

    static void lecturerMenu(Lecturer l) {
        System.out.println("\nWelcome, " + l.getName());
        while (true) {
            System.out.println("\n1 My courses  2 Student list  0 Logout");
            switch (ask("> ")) {
                case "1" -> {
                    List<Course> mine = db.getCoursesByLecturer(l);
                    if (mine.isEmpty()) System.out.println("No courses assigned.");
                    mine.forEach(c -> System.out.println("  " + c));
                }
                case "2" -> {
                    Course c = db.getCourse(ask("Course code: ").toUpperCase());
                    if (c == null || c.getLecturer() == null || !c.getLecturer().getId().equals(l.getId()))
                        System.out.println("That course is not assigned to you.");
                    else {
                        List<Student> list = db.getStudentsIn(c);
                        if (list.isEmpty()) System.out.println("No students registered.");
                        list.forEach(s -> System.out.println("  " + s.getId() + " " + s.getName()));
                    }
                }
                case "0" -> { return; }
                default -> System.out.println("Invalid choice.");
            }
        }
    }
}

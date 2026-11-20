package crss;

import java.util.List;

/** Links a student to a course in a semester, and enforces the registration rules. */
public class Registration {
    public enum Status { CONFIRMED, DROPPED }

    public static final int MAX_CREDITS = 18; // assumption DR: unconfirmed, ask the department

    private final Student student;
    private final Course course;
    private final String semester;
    private Status status = Status.CONFIRMED;

    private Registration(Student student, Course course) {
        this.student = student;
        this.course = course;
        this.semester = course.getSemester();
    }

    public Student getStudent() { return student; }
    public Course getCourse() { return course; }
    public String getSemester() { return semester; }
    public Status getStatus() { return status; }
    public void drop() { status = Status.DROPPED; }

    /**
     * Registers a student for a course or throws RegistrationException with the reason.
     * Checks, in order: duplicate, course full, prerequisites, credit limit, schedule clash.
     */
    public static Registration register(Student s, Course c, DatabaseManager db)
            throws RegistrationException {
        if (db.isRegistered(s, c))
            throw new RegistrationException("Already registered for " + c.getCode() + ".");
        if (db.countConfirmed(c) >= c.getCapacity())
            throw new RegistrationException("Course " + c.getCode() + " is full.");
        for (String pre : c.getPrerequisites())
            if (!s.hasCompleted(pre))
                throw new RegistrationException("Prerequisite not met: " + pre + ".");
        int load = c.getCredits();
        for (Registration r : db.getRegistrations(s, c.getSemester())) load += r.getCourse().getCredits();
        if (load > MAX_CREDITS)
            throw new RegistrationException("Credit limit of " + MAX_CREDITS + " would be exceeded.");
        Schedule wanted = db.getScheduleFor(c);
        if (wanted != null) {
            for (Registration r : db.getRegistrations(s, c.getSemester())) {
                Schedule other = db.getScheduleFor(r.getCourse());
                if (other != null && wanted.overlaps(other))
                    throw new RegistrationException("Schedule clash with " + r.getCourse().getCode() + ".");
            }
        }
        Registration reg = new Registration(s, c);
        db.saveRegistration(reg);
        return reg;
    }
}

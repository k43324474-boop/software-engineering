package crss;

/** One timetable entry: which course, lecturer, room, day and start hour (2-hour blocks). */
public class Schedule {
    public static final int DURATION_HOURS = 2;

    private final Course course;
    private final Lecturer lecturer;
    private final Room room;
    private final String day;
    private final int startHour;

    public Schedule(Course course, Lecturer lecturer, Room room, String day, int startHour) {
        this.course = course;
        this.lecturer = lecturer;
        this.room = room;
        this.day = day;
        this.startHour = startHour;
    }

    public Course getCourse() { return course; }
    public Lecturer getLecturer() { return lecturer; }
    public Room getRoom() { return room; }
    public String getDay() { return day; }
    public int getStartHour() { return startHour; }

    /** True if the two entries are on the same day and their hours overlap. */
    public boolean overlaps(Schedule other) {
        return day.equals(other.day)
                && startHour < other.startHour + DURATION_HOURS
                && other.startHour < startHour + DURATION_HOURS;
    }

    @Override public String toString() {
        return String.format("%-4s %02d:00-%02d:00  %-8s %-28s %s",
                day, startHour, startHour + DURATION_HOURS, course.getCode(),
                course.getTitle(), room.name());
    }
}

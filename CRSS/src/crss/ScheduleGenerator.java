package crss;

import java.util.*;

/**
 * Builds a timetable for a semester. Rules: a lecturer cannot teach two classes at once,
 * a room cannot host two classes at once, and room capacity must cover course capacity.
 * All-or-nothing: if any course cannot be placed, a ScheduleException lists why and nothing is saved.
 */
public class ScheduleGenerator {
    static final String[] DAYS = {"Mon", "Tue", "Wed", "Thu", "Fri"};
    static final int[] START_HOURS = {8, 10, 14, 16};

    public List<Schedule> generate(DatabaseManager db, String semester) throws ScheduleException {
        List<Schedule> result = new ArrayList<>();
        List<String> problems = new ArrayList<>();

        for (Course c : db.getCourses(semester)) {
            if (c.getLecturer() == null) {
                problems.add(c.getCode() + ": no lecturer assigned.");
                continue;
            }
            Schedule placed = findSlot(db, c, result);
            if (placed == null)
                problems.add(c.getCode() + ": no free slot with a lecturer-free, large-enough room.");
            else
                result.add(placed);
        }
        if (!problems.isEmpty())
            throw new ScheduleException("No valid schedule:\n  " + String.join("\n  ", problems));
        db.saveSchedule(result);
        return result;
    }

    private Schedule findSlot(DatabaseManager db, Course c, List<Schedule> placed) {
        for (String day : DAYS)
            for (int hour : START_HOURS)
                for (Room room : db.getRooms()) {
                    if (room.capacity() < c.getCapacity()) continue;
                    Schedule cand = new Schedule(c, c.getLecturer(), room, day, hour);
                    boolean clash = false;
                    for (Schedule p : placed) {
                        boolean sameLecturer = p.getLecturer().getId().equals(cand.getLecturer().getId());
                        boolean sameRoom = p.getRoom().equals(cand.getRoom());
                        if ((sameLecturer || sameRoom) && p.overlaps(cand)) { clash = true; break; }
                    }
                    if (!clash) return cand;
                }
        return null;
    }
}

import crss.*;

/** Plain-Java tests (no libraries). Run: java -cp out test/CrssTests.java  -- each check prints PASS/FAIL. */
public class CrssTests {
    static int failures = 0;

    static void check(String name, boolean ok) {
        System.out.println((ok ? "PASS  " : "FAIL  ") + name);
        if (!ok) failures++;
    }

    static String tryRegister(Student s, Course c, DatabaseManager db) {
        try { Registration.register(s, c, db); return "OK"; }
        catch (RegistrationException e) { return e.getMessage(); }
    }

    public static void main(String[] args) throws Exception {
        DatabaseManager db = DatabaseManager.withSeedData();
        Student amina = db.getStudent("S001"), brian = db.getStudent("S002");
        Course intro = db.getCourse("CCS1101"), ds = db.getCourse("CCS2101"), se = db.getCourse("CCS3201");

        check("TC-01 valid login returns user", db.login("S001", "stu123") == amina);
        check("TC-02 wrong password rejected", db.login("S001", "bad") == null);
        check("TC-03 CoD login is a CoD", db.login("C001", "cod123") instanceof CoD);

        check("TC-04 register succeeds", tryRegister(amina, intro, db).equals("OK"));
        check("TC-05 duplicate rejected", tryRegister(amina, intro, db).startsWith("Already"));
        check("TC-06 prerequisite not met", tryRegister(brian, ds, db).startsWith("Prerequisite"));
        check("TC-07 prerequisite met", tryRegister(amina, ds, db).equals("OK"));

        Student s3 = new Student("S003", "Carol", "x"); s3.addCompletedCourse("CCS1101");
        Student s4 = new Student("S004", "Dan", "x");   s4.addCompletedCourse("CCS1101");
        db.addStudent(s3); db.addStudent(s4);
        tryRegister(s3, ds, db); // CCS2101 capacity is 2: amina + carol
        check("TC-08 course full rejected", tryRegister(s4, ds, db).contains("full"));

        check("TC-09 schedule generates", !new ScheduleGenerator().generate(db, "2026-S2").isEmpty());
        Schedule a = db.getScheduleFor(intro), b = db.getScheduleFor(ds);
        check("TC-10 same-lecturer courses do not overlap", !a.overlaps(b));
        check("TC-11 room capacity respected",
                db.getSchedule().stream().allMatch(e -> e.getRoom().capacity() >= e.getCourse().getCapacity()));

        check("TC-12 timetable shows registered course", db.getRegistrations(amina, "2026-S2").size() == 2);
        check("TC-13 lecturer student list", db.getStudentsIn(intro).contains(amina));
        check("TC-14 drop works", db.dropRegistration(amina, intro) && !db.isRegistered(amina, intro));

        // clash: two courses forced into the same slot
        DatabaseManager d2 = DatabaseManager.withSeedData();
        Student z = d2.getStudent("S001");
        Course x = d2.getCourse("CCS1101"), y = d2.getCourse("CCS3201");
        Room r1 = new Room("R1", 50), r2 = new Room("R2", 50);
        d2.saveSchedule(java.util.List.of(
                new Schedule(x, x.getLecturer(), r1, "Mon", 8),
                new Schedule(y, y.getLecturer(), r2, "Mon", 9)));
        tryRegister(z, x, d2);
        check("TC-15 schedule clash rejected", tryRegister(z, y, d2).startsWith("Schedule clash"));

        // credit limit: 18 max; six 3-credit courses OK, seventh rejected
        DatabaseManager d3 = DatabaseManager.withSeedData();
        Student q = new Student("S9", "Q", "x"); d3.addStudent(q);
        for (int i = 1; i <= 7; i++) d3.saveCourse(new Course("X" + i, "Extra " + i, 3, 50, "2026-S2"));
        String last = "";
        for (int i = 1; i <= 7; i++) last = tryRegister(q, d3.getCourse("X" + i), d3);
        check("TC-16 credit limit enforced", last.contains("Credit limit"));

        // no valid schedule: course without lecturer
        DatabaseManager d4 = DatabaseManager.withSeedData();
        d4.saveCourse(new Course("NOLEC1", "No Lecturer", 3, 10, "2026-S2"));
        boolean threw = false; String msg = "";
        try { new ScheduleGenerator().generate(d4, "2026-S2"); } catch (ScheduleException e) { threw = true; msg = e.getMessage(); }
        check("TC-17 no valid schedule reported", threw && msg.contains("NOLEC1") && d4.getSchedule().isEmpty());

        // no room big enough
        DatabaseManager d5 = DatabaseManager.withSeedData();
        Course big = new Course("BIG1", "Huge Class", 3, 500, "2026-S2"); big.setLecturer(d5.getLecturer("L001"));
        d5.saveCourse(big);
        threw = false;
        try { new ScheduleGenerator().generate(d5, "2026-S2"); } catch (ScheduleException e) { threw = true; }
        check("TC-18 oversize course fails scheduling", threw);

        System.out.println(failures == 0 ? "\nALL TESTS PASSED" : "\n" + failures + " TEST(S) FAILED");
        System.exit(failures == 0 ? 0 : 1);
    }
}

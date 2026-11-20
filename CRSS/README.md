# SPAS CRSS (Java 17+, console)

Folder: src/crss/*.java (one file per class), test/CrssTests.java (18 test cases).

## Compile and run (IntelliJ: mark `src` as Sources Root, run crss.Main)
    javac -d out src/crss/*.java
    java -cp out crss.Main

## Run tests
    java -cp out test/CrssTests.java

## Demo logins (seed data in DatabaseManager.withSeedData)
    Student   S001 / stu123   (has passed CCS1101)    S002 / stu123 (has not)
    CoD       C001 / cod123
    Lecturer  L001, L002 / lec123

## Assumptions to confirm with your lecturer (list them in the SRS)
- Max credit load 18 (Registration.MAX_CREDITS); classes are 2-hour blocks, Mon-Fri,
  starting 08:00, 10:00, 14:00, 16:00; one meeting per course per week.
- Data is in memory and resets on restart (swap DatabaseManager internals for SQLite/files if needed).
- Passwords are plain text in seed data: acceptable for a demo only; say so in the architecture/security section.
- Schedule generation is all-or-nothing; students are not checked against each other when scheduling.

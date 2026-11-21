# software-engineering
guide on CRSS code file

What it does

Students can view courses with seats left, register, drop, and see their weekly timetable.
Registration checks: it rejects a duplicate, a full course, an unmet prerequisite, a credit limit overrun (18 max) or a schedule clash. Each rejection shows the reason.
The CoD can add and edit courses, assign lecturers, generate and view the schedule, and remove a student from a course.
The schedule generator avoids lecturer and room clashes and respects room capacity. If any course can't be placed, it saves nothing and lists why. That covers the "no valid schedule" path in your sequence diagram.
Lecturers can view their assigned courses and student lists, but only for their own courses.
Login has three roles. Demo logins are in the README.

How it maps to your plan

It follows the eight classes in the brief. It adds Room and two exception classes, and CoD extends Lecturer, so your class diagram should show that generalisation.
Each file matches a member's slice. Members can extend their own file: M1 Registration and the student menu, M2 the CoD menu, M3 login and the lecturer menu, M4 ScheduleGenerator and Schedule, M5 DatabaseManager, and M6 Main and integration.
CrssTests.java holds TC-01 to TC-18, so M3 can use them as the base for the test plan and report.

What to do with it

Open it: in IntelliJ, mark src as the Sources Root and run crss.Main.
Diagrams: make your sequence-diagram message names match the method names here, such as Registration.register, generate and getScheduleFor.
Assumptions: list them in the SRS as unconfirmed. These are the 18-credit limit, the two-hour Mon–Fri slots, in-memory data that resets on restart, and plain-text demo passwords.
Make it your own: the lab sheet penalises unexplained copying, so each member should read, change and be able to explain their own file before the presentation. Change the seed data and the course names, and extend it, for example with a persistent store or a stronger login.
Not included: there is no GUI. If your wireframes show windows, a simple Swing screen can sit on top of these classes later, but with three days left the console demo is the safer choice.

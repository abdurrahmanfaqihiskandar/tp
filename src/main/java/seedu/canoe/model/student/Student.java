package seedu.canoe.model.student;

import static seedu.canoe.commons.util.CollectionUtil.requireAllNonNull;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import seedu.canoe.model.student.time.Day;
import seedu.canoe.model.tag.Tag;

/**
 * Represents a Student in the canoe coach book.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Student {

    // Identity fields
    private final Id id;
    private final Name name;
    private final Phone phone;
    private final Email email;
    private final AcademicYear academicYear;

    // Dismissal Times
    private final Day mondayDismissal;
    private final Day tuesdayDismissal;
    private final Day wednesdayDismissal;
    private final Day thursdayDismissal;
    private final Day fridayDismissal;

    private final Set<Tag> tags = new HashSet<>();

    class TreeSetComparator implements Comparator<Attend> {
        public int compare(Attend attend1, Attend attend2) {
            return attend1.compareTo(attend2);
        }
    }

    //Collection of scheduled training dates tagged to the particular student
    private final TreeSet<Attend> trainingSchedules = new TreeSet<>(new TreeSetComparator());

    /**
     * Constructs the {@code Student} with a given id.
     * Every field must be present and not null.
     */
    public Student(Name name, Phone phone, Email email, AcademicYear academicYear, Set<Tag> tags, Day mondayDismissal,
                   Day tuesdayDismissal, Day wednesdayDismissal, Day thursdayDismissal,
                   Day fridayDismissal, Id id) {
        requireAllNonNull(name, phone, email, tags, academicYear, mondayDismissal, tuesdayDismissal, wednesdayDismissal,
                thursdayDismissal, fridayDismissal, id);
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.academicYear = academicYear;
        this.mondayDismissal = mondayDismissal;
        this.tuesdayDismissal = tuesdayDismissal;
        this.wednesdayDismissal = wednesdayDismissal;
        this.thursdayDismissal = thursdayDismissal;
        this.fridayDismissal = fridayDismissal;
        this.tags.addAll(tags);
        this.id = id;
    }

    /**
     * Constructs the {@code Student} with a given id and training schedules.
     * Every field must be present and not null.
     */
    public Student(Name name, Phone phone, Email email, AcademicYear academicYear, Set<Tag> tags, Day mondayDismissal,
                   Day tuesdayDismissal, Day wednesdayDismissal, Day thursdayDismissal,
                   Day fridayDismissal, List<Attend> trainingSchedules, Id id) {
        requireAllNonNull(name, phone, email, tags, academicYear, mondayDismissal, tuesdayDismissal, wednesdayDismissal,
                thursdayDismissal, fridayDismissal, trainingSchedules, id);
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.academicYear = academicYear;
        this.mondayDismissal = mondayDismissal;
        this.tuesdayDismissal = tuesdayDismissal;
        this.wednesdayDismissal = wednesdayDismissal;
        this.thursdayDismissal = thursdayDismissal;
        this.fridayDismissal = fridayDismissal;
        this.tags.addAll(tags);
        this.trainingSchedules.addAll(trainingSchedules);
        this.id = id;
    }

    /**
     * Clones a student.
     */
    public Student cloneStudent() {
        return new Student(getName(), getPhone(), getEmail(), getAcademicYear(), getTags(),
                getMondayDismissal(), getTuesdayDismissal(), getWednesdayDismissal(), getThursdayDismissal(),
                getFridayDismissal(), new ArrayList<>(getTrainingSchedule()), getId());
    }

    /**
     * Creates a student with a new valid Id if it was constructed with placeholder Id.
     * If the student already has a valid Id, the same student is returned.
     */
    public Student createStudentWithValidId() {
        if (getId().equals(Id.getPlaceHolderId())) {
            return new Student(getName(), getPhone(), getEmail(), getAcademicYear(), getTags(),
                    getMondayDismissal(), getTuesdayDismissal(), getWednesdayDismissal(), getThursdayDismissal(),
                    getFridayDismissal(), new ArrayList<>(getTrainingSchedule()), Id.newId());
        }
        return this;
    }

    /**
     * Adds a training session to the student's schedule.
     * Training sessions are automatically sorted by their respective date and times.
     *
     * @param attendingTraining Training that the student is attending.
     * Duplicates are not allowed and will not be added.
     */
    public void addTraining(Attend attendingTraining) {
        trainingSchedules.add(attendingTraining);
    }

    /**
     * Adds a list of training sessions that the student is attending to the student's schedule.
     * Training sessions are automatically sorted by their respective date and times.
     *
     * @param attendingTrainings List of Trainings that the student is going to attending.
     * Duplicates are not allowed and will not be added.
     */
    public void addAllTraining(List<Attend> attendingTrainings) {
        trainingSchedules.addAll(attendingTrainings);
    }

    /**
     * Checks if student has a training scheduled at the specified date and start time.
     *
     * @param trainingDateTime LocalDateTime corresponding to the training's date and start time.
     * Specified training must exist in the student's training schedule.
     * @return true If a training has been scheduled at the specified date and time.
     */
    public boolean containsTraining(Attend trainingDateTime) {
        return trainingSchedules.contains(trainingDateTime);
    }

    /**
     * Removes a scheduled training from the student's training schedule.
     *
     * @param trainingDateTime LocalDateTime corresponding to the training's date and start time.
     */
    public void removeTraining(Attend trainingDateTime) {
        if (containsTraining(trainingDateTime)) {
            trainingSchedules.remove(trainingDateTime);
        }
    }

    /**
     * Removes all trainings scheduled for the student.
     */
    public void removeAllTraining() {
        trainingSchedules.clear();
    }

    public Id getId() {
        return id;
    }

    public Name getName() {
        return name;
    }

    public Phone getPhone() {
        return phone;
    }

    public Email getEmail() {
        return email;
    }

    public Day getMondayDismissal() {
        return mondayDismissal;
    }

    public Day getTuesdayDismissal() {
        return tuesdayDismissal;
    }

    public Day getWednesdayDismissal() {
        return wednesdayDismissal;
    }

    public Day getThursdayDismissal() {
        return thursdayDismissal;
    }

    public Day getFridayDismissal() {
        return fridayDismissal;
    }

    public AcademicYear getAcademicYear() {
        return academicYear;
    }

    /**
     * Returns an immutable tag set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    /**
     * Returns an immutable training schedule set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Attend> getTrainingSchedule() {
        return Collections.unmodifiableSet(trainingSchedules);
    }

    /**
     * Returns true if both students of the same name have at least one other identity field that is the same.
     * This defines a weaker notion of equality between two students.
     */
    public boolean isSameStudent(Student otherStudent) {
        if (otherStudent == this) {
            return true;
        }

        return otherStudent != null
                && otherStudent.getName().equals(getName())
                && otherStudent.getAcademicYear().equals(getAcademicYear())
                && (otherStudent.getPhone().equals(getPhone()) || otherStudent.getEmail().equals(getEmail()));
    }

    /**
     * Returns true if both students have the same identity and data fields.
     * This defines a stronger notion of equality between two students.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof Student)) {
            return false;
        }

        Student otherStudent = (Student) other;
        return otherStudent.getName().equals(getName())
                && otherStudent.getPhone().equals(getPhone())
                && otherStudent.getEmail().equals(getEmail())
                && otherStudent.getMondayDismissal().equals(getMondayDismissal())
                && otherStudent.getTuesdayDismissal().equals(getTuesdayDismissal())
                && otherStudent.getWednesdayDismissal().equals(getWednesdayDismissal())
                && otherStudent.getThursdayDismissal().equals(getThursdayDismissal())
                && otherStudent.getFridayDismissal().equals(getFridayDismissal())
                && otherStudent.getAcademicYear().equals(getAcademicYear())
                && otherStudent.getTags().equals(getTags())
                && otherStudent.getTrainingSchedule().equals(getTrainingSchedule());
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, phone, email, academicYear, mondayDismissal, tuesdayDismissal, wednesdayDismissal,
                thursdayDismissal, fridayDismissal, tags, id, trainingSchedules);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(getName())
                .append(" Id: ")
                .append(getId())
                .append(" Phone: ")
                .append(getPhone())
                .append(" Email: ")
                .append(getEmail())
                .append(" Dismissal Times: ")
                .append(getMondayDismissal())
                .append(" ")
                .append(getTuesdayDismissal())
                .append(" ")
                .append(getWednesdayDismissal())
                .append(" ")
                .append(getThursdayDismissal())
                .append(" ")
                .append(getFridayDismissal())
                .append(" ")
                .append(" Academic  Year: ")
                .append(getAcademicYear())
                .append(" Tags: ");
        getTags().forEach(builder::append);
        builder.append(" Training Schedules: ");
        getTrainingSchedule().forEach(builder::append);
        return builder.toString();
    }

    /**
     * Returns only the essential information of Students (Name, Id, Phone, Email, Academic Year).
     * @return Name, Phone, Email, Academic Year
     */
    public String studentEssentialPrinter() {
        final StringBuilder builder = new StringBuilder();
        builder.append("\n" + getName())
                .append("\nId: ")
                .append(getId())
                .append("\nPhone: ")
                .append(getPhone())
                .append("\nEmail: ")
                .append(getEmail())
                .append("\nAcademic  Year: ")
                .append(getAcademicYear());
        return builder.toString();
    }

    /**
     * Returns whether a student has a training at a particular DateTime already
     */
    public boolean hasTrainingAtDateTime(LocalDateTime dateTime) {
        //Has a training scheduled on the same date already
        for (Attend training: trainingSchedules) {
            if (LocalDate.from(training.getTrainingTime()).isEqual(LocalDate.from(dateTime))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns whether a student is able to attend a training at a particular DateTime
     */
    public boolean isAvailableAtDateTime(LocalDateTime dateTime) {
        DayOfWeek day = dateTime.getDayOfWeek();

        LocalTime queryTime = LocalTime.from(dateTime);
        LocalTime studentTime;

        switch (day) {
        case MONDAY:
            studentTime = mondayDismissal.dismissalTime;
            return studentTime.equals(queryTime) || studentTime.isBefore(queryTime);
        case TUESDAY:
            studentTime = tuesdayDismissal.dismissalTime;
            return studentTime.equals(queryTime) || studentTime.isBefore(queryTime);
        case WEDNESDAY:
            studentTime = wednesdayDismissal.dismissalTime;
            return studentTime.equals(queryTime) || studentTime.isBefore(queryTime);
        case THURSDAY:
            studentTime = thursdayDismissal.dismissalTime;
            return studentTime.equals(queryTime) || studentTime.isBefore(queryTime);
        case FRIDAY:
            studentTime = fridayDismissal.dismissalTime;
            return studentTime.equals(queryTime) || studentTime.isBefore(queryTime);
        default:
            //Students do not have lessons on Saturday and Sunday, thus able to attend training.
            return true;
        }

    }

    /**
     * Returns whether a student is able to attend all of his/her scheduled trainings
     */
    public boolean isAvailableForAllTrainingsScheduled() {
        if (trainingSchedules.isEmpty()) {
            return true;
        }

        boolean isAvailable = true;
        for (Attend trainingSession: trainingSchedules) {
            if (!isAvailableAtDateTime(trainingSession.getTrainingTime())) {
                isAvailable = false;
            }
        }

        return isAvailable;
    }

    /**
     * Mark student's attendance for a training session as attended.
     *
     * @param trainingSessionToAttend training session to mark as attended.
     */
    public void attendTrainingSession(Attend trainingSessionToAttend, Attend trainingSessionAttended) {
        assert(containsTraining(trainingSessionToAttend));

        trainingSchedules.remove(trainingSessionToAttend);
        trainingSchedules.add(trainingSessionAttended);
    }
}

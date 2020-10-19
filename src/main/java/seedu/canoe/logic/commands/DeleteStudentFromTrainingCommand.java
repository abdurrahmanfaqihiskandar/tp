package seedu.canoe.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.canoe.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.canoe.logic.parser.CliSyntax.PREFIX_ID;
import static seedu.canoe.model.Model.PREDICATE_SHOW_ALL_STUDENTS;
import static seedu.canoe.model.Model.PREDICATE_SHOW_ALL_TRAININGS;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import seedu.canoe.commons.core.Messages;
import seedu.canoe.commons.core.index.Index;
import seedu.canoe.commons.util.StringUtil;
import seedu.canoe.logic.commands.exceptions.CommandException;
import seedu.canoe.model.Model;
import seedu.canoe.model.student.AcademicYear;
import seedu.canoe.model.student.Email;
import seedu.canoe.model.student.Id;
import seedu.canoe.model.student.Name;
import seedu.canoe.model.student.Phone;
import seedu.canoe.model.student.Student;
import seedu.canoe.model.student.Training;
import seedu.canoe.model.student.time.Day;
import seedu.canoe.model.tag.Tag;

/**
 * Deletes an existing student from a training.
 */
public class DeleteStudentFromTrainingCommand extends Command {

    public static final String COMMAND_WORD = "ts-delete";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Deletes the corresponding Students "
            + "from the specified Training Session\n"
            + "Parameters: Training_Session-ID " + PREFIX_ID + "Student_ID..."
            + "\nExample: "
            + COMMAND_WORD + "1 " + PREFIX_ID + "3,5,7";

    public static final String MESSAGE_INVALID_STUDENT = "One of the"
            + " Students provided is not inside of the training specified!";
    public static final String MESSAGE_DELETE_STUDENT_SUCCESS = "Deleted Student: %1$s";
    public static final String MESSAGE_NO_STUDENTS_SPECIFIED = "At least one student to be deleted must be specified.";

    private final Index index;
    private final String[] studentsToDelete;

    /**
     * @param index of the training in the filtered training list to delete
     * @param studentsToDelete corresponding Id of Students to delete
     */
    public DeleteStudentFromTrainingCommand(Index index, String[] studentsToDelete) {
        requireNonNull(index);
        requireNonNull(studentsToDelete);

        this.index = index;
        this.studentsToDelete = studentsToDelete;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        //Added in case previous command is find
        model.updateFilteredStudentList(PREDICATE_SHOW_ALL_STUDENTS);
        model.updateFilteredTrainingList(PREDICATE_SHOW_ALL_TRAININGS);

        List<Training> lastShownList = model.getFilteredTrainingList();
        List<Student> studentList = model.getFilteredStudentList();

        //Ensures that command contains at least one student to delete
        if (studentsToDelete == null) {
            throw new CommandException(MESSAGE_NO_STUDENTS_SPECIFIED);
        }

        //Ensures correct training index
        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_TRAINING_DISPLAYED_INDEX);
        }

        Training trainingToEdit = lastShownList.get(index.getZeroBased());
        Training editedTraining = new Training(trainingToEdit.getDateTime(), trainingToEdit.getStudents());

        //Student ID Checks - not invalid index, numbered index and exists in student list and exists inside of training
        List<Student> targetStudentList = new ArrayList<>();
        List<Student> editedStudentList = new ArrayList<>();
        for (String str : studentsToDelete) {
            if (str.length() != 1) {
                throw new CommandException(String
                        .format(MESSAGE_INVALID_COMMAND_FORMAT, AddStudentToTrainingCommand.MESSAGE_USAGE));
            }

            if (!StringUtil.isNonZeroUnsignedInteger(str)) {
                throw new CommandException(String
                        .format(MESSAGE_INVALID_COMMAND_FORMAT, AddStudentToTrainingCommand.MESSAGE_USAGE));
            }

            if (Integer.parseInt(str) > studentList.size() || Integer.parseInt(str) <= 0) {
                throw new CommandException(Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);
            }

            Student studentToEdit = studentList.get(Integer.parseInt(str) - 1);
            Student editedStudent = createEditedStudent(studentToEdit, editedTraining);

            if (!containStudentChecker(editedTraining, studentToEdit)) {
                throw new CommandException(MESSAGE_INVALID_STUDENT);
            }

            /*
            Add students that have passed all checks to the corresponding lists first without
            modifying the model.
             */
            editedTraining.removeStudent(editedStudent);
            targetStudentList.add(studentToEdit);
            editedStudentList.add(editedStudent);
        }

        //All checked passed at this point, iterate through student lists to update the model.
        for (int i = 0; i < targetStudentList.size(); i++) {
            model.setStudentInUniqueStudentList(targetStudentList.get(i), editedStudentList.get(i));
        }

        model.setTraining(trainingToEdit, editedTraining);
        model.updateFilteredStudentList(PREDICATE_SHOW_ALL_STUDENTS);

        String result = getStudentsDeleted();

        return new CommandResult(String.format(MESSAGE_DELETE_STUDENT_SUCCESS, result));
    }

    /**
     * Checks that the Training Specified contains the Student to be removed.
     *
     * @param trainingToCheck
     * @param check Student to check
     * @return boolean that indicates whether Student to be removed is contained within the training.
     * @throws CommandException
     */
    public boolean containStudentChecker(Training trainingToCheck, Student check) throws CommandException {
        boolean containsStudent = false;
        for (Student student : trainingToCheck.getStudents()) {
            if (student.getId().equals(check.getId())) {
                containsStudent = true;
            }
        }
        return containsStudent;
    }

    private static Student createEditedStudent(Student studentToEdit, Training editedTraining) {
        assert studentToEdit != null;

        Name updatedName = studentToEdit.getName();
        Phone updatedPhone = studentToEdit.getPhone();
        Email updatedEmail = studentToEdit.getEmail();
        AcademicYear updatedAcademicYear = studentToEdit.getAcademicYear();
        Day mondayDismissal = studentToEdit.getMondayDismissal();
        Day tuesdayDismissal = studentToEdit.getTuesdayDismissal();
        Day wednesdayDismissal = studentToEdit.getWednesdayDismissal();
        Day thursdayDismissal = studentToEdit.getThursdayDismissal();
        Day fridayDismissal = studentToEdit.getFridayDismissal();
        Set<Tag> updatedTags = studentToEdit.getTags();
        List<LocalDateTime> trainingSchedules = studentToEdit.getTrainingSchedule().stream()
                .collect(Collectors.toList());
        trainingSchedules.remove(editedTraining.getDateTime());
        Id id = studentToEdit.getId();

        Student newStudent = new Student(updatedName, updatedPhone, updatedEmail, updatedAcademicYear, updatedTags,
                mondayDismissal, tuesdayDismissal, wednesdayDismissal, thursdayDismissal, fridayDismissal, id);
        newStudent.addAllTraining(trainingSchedules);
        return newStudent;
    }

    /**
     * Returns a String with the IDs of the students deleted, removing duplicate IDs.
     * @return String with Unique Ids of Students deleted
     */
    public String getStudentsDeleted() {
        String result = "";
        for (String str : studentsToDelete) {
            if (result.contains(str)) {
                continue;
            }
            result += str + " ";
        }
        result = result.trim();
        return result;
    }

    /**
     * Return the index of the Training.
     * @return index of Training
     */
    public Index getIndex() {
        return this.index;
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof DeleteStudentFromTrainingCommand)) {
            return false;
        }

        // state check
        DeleteStudentFromTrainingCommand e = (DeleteStudentFromTrainingCommand) other;
        return index.equals(e.index)
                && studentsToDelete.equals(e.studentsToDelete);
    }
}

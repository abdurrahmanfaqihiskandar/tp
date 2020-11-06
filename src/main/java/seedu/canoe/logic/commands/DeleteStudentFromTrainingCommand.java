package seedu.canoe.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.canoe.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.canoe.logic.parser.CliSyntax.PREFIX_ID;
import static seedu.canoe.model.Model.PREDICATE_SHOW_ALL_STUDENTS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import seedu.canoe.commons.core.LogsCenter;
import seedu.canoe.commons.core.Messages;
import seedu.canoe.commons.core.index.Index;
import seedu.canoe.logic.commands.exceptions.CommandException;
import seedu.canoe.model.Model;
import seedu.canoe.model.student.Attendance;
import seedu.canoe.model.student.Id;
import seedu.canoe.model.student.Student;
import seedu.canoe.model.training.Training;

/**
 * Deletes an existing student from a training.
 */
public class DeleteStudentFromTrainingCommand extends Command {
    public static final Logger LOGGER = LogsCenter.getLogger(DeleteStudentFromTrainingCommand.class);

    public static final String COMMAND_WORD = "ts-delete";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Deletes the corresponding Students "
            + "from the specified Training Session\n"
            + "Parameters: Training_Session-ID " + PREFIX_ID + "Student_ID..."
            + "\nExample: "
            + COMMAND_WORD + "1 " + PREFIX_ID + "3,5,7";

    public static final String MESSAGE_INVALID_STUDENT = "One of the"
            + " Students provided is not inside of the training specified!";
    public static final String MESSAGE_DELETE_STUDENT_SUCCESS = "Deleted Student: %1$s";
    public static final String MESSAGE_STUDENT_DOES_NOT_EXIST = "One of the Ids "
            + "specified do not correspond to an existing Student!";
    public static final String MESSAGE_NO_STUDENTS_SPECIFIED = "At least one student to be deleted must be specified.";
    public static final String MESSAGE_REPEATED_STUDENT = "One of the Ids is repeated!";

    private final Index index;
    private final List<String> studentsToDelete;

    /**
     * @param index of the training in the filtered training list to delete
     * @param studentsToDelete corresponding Id of Students to delete
     */
    public DeleteStudentFromTrainingCommand(Index index, String[] studentsToDelete) {
        requireNonNull(index);
        requireNonNull(studentsToDelete);

        this.index = index;
        this.studentsToDelete = Arrays.asList(studentsToDelete);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        LOGGER.info("=============================[ Executing DeleteStudentFromTrainingCommand ]========"
                + "===================");
        requireNonNull(model);

        // Checks if students to delete are repeated
        if (!hasUniqueStudentsToDelete(studentsToDelete)) {
            LOGGER.warning("Repeated Id input detected.");
            throw new CommandException(MESSAGE_REPEATED_STUDENT);
        }

        List<Training> trainingList = model.getFilteredTrainingList();

        // Checks if training index is valid
        if (index.getZeroBased() >= trainingList.size()) {
            LOGGER.warning("Training index is invalid.");
            throw new CommandException(Messages.MESSAGE_INVALID_TRAINING_DISPLAYED_INDEX);
        }

        Training trainingToEdit = trainingList.get(index.getZeroBased());
        Training editedTraining = trainingToEdit.cloneTraining();

        List<Student> targetStudentList = new ArrayList<>();
        List<Student> editedStudentList = new ArrayList<>();

        for (String str : studentsToDelete) {

            // Checks if Id value input is valid
            if (!Id.isValidId(str)) {
                LOGGER.warning("Student Id is invalid.");
                throw new CommandException(String
                        .format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteStudentFromTrainingCommand.MESSAGE_USAGE));
            }

            // Checks if student of the Id exists
            if (getStudentWithId(model, str).isEmpty()) {
                throw new CommandException(MESSAGE_STUDENT_DOES_NOT_EXIST);
            }

            Student studentToEdit = getStudentWithId(model, str).get();
            Student editedStudent = createEditedStudent(studentToEdit, editedTraining);

            if (!hasStudentInTraining(editedTraining, studentToEdit)) {
                throw new CommandException(MESSAGE_INVALID_STUDENT);
            }

            // Edits training and adds valid students to corresponding student lists
            editedTraining.removeStudent(editedStudent);
            editedStudent.removeAttendance(new Attendance(editedTraining.getDateTime()));
            targetStudentList.add(studentToEdit);
            editedStudentList.add(editedStudent);
        }

        // Updates the model's student list
        for (int i = 0; i < targetStudentList.size(); i++) {
            model.setStudentInUniqueStudentList(targetStudentList.get(i), editedStudentList.get(i));
        }

        // Updates the model's training list
        model.setTraining(trainingToEdit, editedTraining);

        model.updateFilteredStudentList(PREDICATE_SHOW_ALL_STUDENTS);
        String result = getStudentsMessage(editedStudentList);
        return new CommandResult(String.format(MESSAGE_DELETE_STUDENT_SUCCESS, result)
                + " from Training Session " + index.getOneBased());
    }

    /**
     * Checks that the Training Specified contains the Student to be removed.
     */
    public boolean hasStudentInTraining(Training trainingToCheck, Student check) {
        return trainingToCheck.getStudents().stream()
                .anyMatch(student -> student.getId().equals(check.getId()));
    }

    /**
     * Returns the student in the model with the specified unique Id. May not exist.
     */
    public Optional<Student> getStudentWithId(Model model, String id) {
        id = id.trim();
        String finalId = id;
        return model.getFilteredStudentList().stream()
                .filter(student -> student.getId().getValue().equals(finalId))
                .findFirst();
    }

    private static Student createEditedStudent(Student studentToEdit, Training editedTraining) {
        assert studentToEdit != null;

        Student newStudent = studentToEdit.cloneStudent();
        newStudent.removeAttendance(new Attendance(editedTraining.getDateTime()));
        return newStudent;
    }

    private String getStudentsMessage(List<Student> students) {
        return students.stream()
                .map(Student::getId)
                .map(Id::getValue)
                .reduce((id1, id2) -> id1 + " " + id2)
                .get();
    }

    /**
     * Checks if a given list of student Id strings contains repeating values.
     */
    public static boolean hasUniqueStudentsToDelete(List<String> studentsToDelete) {
        return new HashSet<>(studentsToDelete).size() == studentsToDelete.size();
    }

    /**
     * Returns the index of the Training.
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

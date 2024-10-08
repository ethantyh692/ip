package duker;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

public class Parser {

    /**
     * Constructs a new Parser instance.
     */
    public Parser() {
    }

    private Todo createTodoFromString(String description, boolean isDone, boolean isHighPriority) {
        Todo todo = new Todo(description);
        if (isDone) {
            todo.markAsDone();
        }
        if (isHighPriority) {
            todo.markAsHighPriority();
        }
        return todo;
    }

    private Deadline createDeadlineFromString(
            String description, String[] parts, boolean isDone, boolean isHighPriority) {
        String timeToConvert = isolateTimeToConvert(parts[4]);
        Deadline deadline = new Deadline(description, convertStringToDate(timeToConvert));
        if (isDone) {
            deadline.markAsDone();
        }
        if (isHighPriority) {
            deadline.markAsHighPriority();
        }
        return deadline;
    }

    private Event createEventFromString(
            String description, String[] parts, boolean isDone, boolean isHighPriority) {
        String timeToConvertFrom = isolateTimeToConvert(parts[4]);
        String timeToConvertTo = isolateTimeToConvert(parts[5]);
        Event event = new Event(description, convertStringToDate(timeToConvertFrom),
                convertStringToDate(timeToConvertTo));
        if (isDone) {
            event.markAsDone();
        }
        if (isHighPriority) {
            event.markAsHighPriority();
        }
        return event;
    }

    /**
     * Converts a string representation of a task to a Task object.
     *
     * @param line the string representation of a task, formatted as
     *             "priority | type | status | description | time"
     * @return the Task object corresponding to the string
     */
    public Task convertStringToTask(String line) {
        assert line != null : "Input line cannot be null";

        String[] parts = line.split(" \\| ");
        assert parts.length >= 4 : "Invalid task format";

        String priority = parts[0];
        String taskType = parts[1];
        boolean isDone = parts[2].equals("1");
        String description = parts[3];
        boolean isHighPriority = priority.equals("1");

        assert taskType.equals("T") || taskType.equals("D") || taskType.equals("E") : "Unknown task type";

        if (taskType.equals("T")) {
            return createTodoFromString(description, isDone, isHighPriority);
        } else if (taskType.equals("D")) {
            assert parts.length == 5 : "Invalid deadline format";
            return createDeadlineFromString(description, parts, isDone, isHighPriority);
        } else {
            assert parts.length == 6 : "Invalid event format";
            return createEventFromString(description, parts, isDone, isHighPriority);
        }
    }

    private String isolateTimeToConvert(String timeContainingT) {
        String[] stringWithRemovedT = timeContainingT.split("T", 2);
        String timeToConvert = stringWithRemovedT[0] + " " + stringWithRemovedT[1];
        return timeToConvert;
    }

    /**
     * Converts a string representation of a date and time to a LocalDateTime object.
     *
     * @param dateTimeString the string representation of the date and time,
     *                       formatted as "yyyy-MM-dd HH:mm"
     * @return the LocalDateTime object parsed from the string,
     * or null if the format is invalid
     */
    public LocalDateTime convertStringToDate(String dateTimeString) {
        assert dateTimeString != null : "Date-time string cannot be null";

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            return LocalDateTime.parse(dateTimeString, formatter);
        } catch (DateTimeParseException e) {
            throw e;
        }
    }

    private Todo parseTodoCommand(String command) {
        assert command.startsWith("todo") : "Command must start with 'todo'";

        String description = command.substring(4).trim(); // Extract description
        if (description.isEmpty()) {
            throw new IllegalArgumentException("Description for 'todo' cannot be empty.");
        }
        return new Todo(description);
    }

    private Deadline parseDeadlineCommand(String command) {
        assert command.startsWith("deadline") : "Command must start with 'deadline'";

        String[] parts = command.split("/by");
        if (parts.length != 2) {
            throw new IllegalArgumentException(
                    "Deadline command must contain '/by' followed by a date-time.");
        }

        String description = parts[0].substring(8).trim();
        if (description.isEmpty()) {
            throw new IllegalArgumentException("Description for 'deadline' cannot be empty.");
        }

        String dateTimeString = parts[1].trim();
        try {
            LocalDateTime by = convertStringToDate(dateTimeString);
            return new Deadline(description, by);
        } catch (DateTimeParseException e) {
            throw e;
        }
    }

    protected Event parseEventCommand(String command) {
        assert command.startsWith("event") : "Command must start with 'event'";

        String[] partsFrom = command.split("/from");
        if (partsFrom.length != 2) {
            throw new IllegalArgumentException(
                    "Event command must contain '/from' followed by a start date-time.");
        }

        String[] partsTo = partsFrom[1].split("/to");
        if (partsTo.length != 2) {
            throw new IllegalArgumentException(
                    "Event command must contain '/to' followed by an end date-time.");
        }

        String description = partsFrom[0].substring(5).trim();
        if (description.isEmpty()) {
            throw new IllegalArgumentException("Description for 'event' cannot be empty.");
        }

        String fromDateTimeString = partsTo[0].trim(); // Extract start date-time
        String toDateTimeString = partsTo[1].trim(); // Extract end date-time

        try {
            LocalDateTime from = convertStringToDate(fromDateTimeString);
            LocalDateTime to = convertStringToDate(toDateTimeString);
            return new Event(description, from, to);
        } catch (DateTimeParseException e) {
            throw e;
        }
    }

    private int parseIndexCommand(String[] getInstr, TaskList taskList) throws InvalidIndexException {
        assert getInstr != null : "Instruction cannot be null";
        assert taskList != null : "TaskList cannot be null";

        int index;
        if (getInstr.length <= 1) {
            throw new InvalidIndexException("Invalid index provided, please provide proper index.");
        } else {
            index = Integer.parseInt(getInstr[1]);
        }
        if (index - 1 < 0 || index - 1 >= taskList.getSize()) {
            throw new InvalidIndexException("Invalid index provided, please provide proper index.");
        }
        return index;
    }

    private void executeMark(String[] getInstr, TaskList taskList, Storage storage) {
        try {
            int index = parseIndexCommand(getInstr, taskList);
            taskList.mark(index, storage);
        } catch (InvalidIndexException e) {
            System.out.println(e.toString());
        }
    }

    private void executeUnmark(String[] getInstr, TaskList taskList, Storage storage) {
        try {
            int index = parseIndexCommand(getInstr, taskList);
            taskList.unmark(index, storage);
        } catch (InvalidIndexException e) {
            System.out.println(e.toString());
        }
    }

    private void executePrioritise(String[] getInstr, TaskList taskList, Storage storage) {
        try {
            int index = parseIndexCommand(getInstr, taskList);
            taskList.prioritise(index, storage);
        } catch (InvalidIndexException e) {
            System.out.println(e.toString());
        }
    }

    private void executeDeprioritise(String[] getInstr, TaskList taskList, Storage storage) {
        try {
            int index = parseIndexCommand(getInstr, taskList);
            taskList.deprioritise(index, storage);
        } catch (InvalidIndexException e) {
            System.out.println(e.toString());
        }
    }

    private void executeDelete(String[] getInstr, TaskList taskList, Storage storage) {
        try {
            int index = parseIndexCommand(getInstr, taskList);
            taskList.delete(index, storage);
        } catch (InvalidIndexException e) {
            System.out.println(e.toString());
        }
    }

    private void executeTodo(String command, TaskList taskList, Storage storage) {
        try {
            Task todo = parseTodoCommand(command);
            taskList.add(todo, storage);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void executeDeadline(String command, TaskList taskList, Storage storage) {
        try {
            Task deadline = parseDeadlineCommand(command);
            taskList.add(deadline, storage);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date-time format. " +
                    "Please provide the date-time in 'yyyy-MM-dd HH:mm' format. " +
                    "Time should be in 24 hours format.");
        }
    }

    private void executeEvent(String command, TaskList taskList, Storage storage) {
        try {
            Task event = parseEventCommand(command);
            taskList.add(event, storage);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date-time format. " +
                    "Please provide the date-time in 'yyyy-MM-dd HH:mm' format. " +
                    "Time should be in 24 hours format.");
        }
    }

    private void executeFind(String[] getInstr, TaskList taskList, Ui ui) {
        try {
            if (getInstr.length <= 1) {
                throw new DukerException("Please provide a keyword");
            }
            ArrayList<Task> tasksFound = taskList.findTasks(getInstr[1]);
            ui.printKeywordList(tasksFound);
        } catch (DukerException e) {
            System.out.println(e.getMessage());
        }
    }

    private void executeDefault() {
        try {
            throw new DukerException("OOPS!!! I'm sorry, but I don't know what that means :-(");
        } catch (DukerException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Parses a user command and executes the corresponding operation on the task list.
     *
     * @param command  the user command to parse
     * @param taskList the TaskList object on which to perform the operation
     * @param storage  the Storage object to handle file operations
     * @param ui       the Ui object to interact with the user
     */
    public void parseCommand(String command, TaskList taskList, Storage storage, Ui ui) {
        String[] getInstr = command.split(" ", 2);
        String instr = getInstr[0];
        switch (instr) {
        case "mark":
            executeMark(getInstr, taskList, storage);
            break;
        case "unmark":
            executeUnmark(getInstr, taskList, storage);
            break;
        case "prioritise":
            executePrioritise(getInstr, taskList, storage);
            break;
        case "deprioritise":
            executeDeprioritise(getInstr, taskList, storage);
            break;
        case "delete":
            executeDelete(getInstr, taskList, storage);
            break;
        case "list":
            ui.printList(taskList);
            break;
        case "bye":
            ui.bye();
            break;
        case "todo":
            executeTodo(command, taskList, storage);
            break;
        case "deadline":
            executeDeadline(command, taskList, storage);
            break;
        case "event":
            executeEvent(command, taskList, storage);
            break;
        case "find":
            executeFind(getInstr, taskList, ui);
            break;
        case "priority":
            ui.printPriorityList(taskList);
            break;
        default:
            executeDefault();
        }
    }
}

package duke;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * The Storage class is responsible for handling file operations such as
 * loading tasks from a file, writing tasks to a file, and modifying the contents
 * of the file as needed by the Duke application.
 */
public class Storage {

    private String dataDir;
    private String filePath;

    /**
     * Constructs a new Storage instance with the specified data directory
     * and file path.
     *
     * @param dataDir  the directory where the data file is stored
     * @param filePath the name of the data file
     */
    public Storage(String dataDir, String filePath) {
        this.dataDir = dataDir;
        this.filePath = filePath;
    }

    /**
     * Loads tasks from the file specified by file path into the given TaskList.
     * If the file or directory does not exist, they are created.
     *
     * @param taskList the TaskList to populate with tasks
     * @param parser   the Parser to use for converting strings to tasks
     */
    public void loadFile(TaskList taskList, Parser parser) {
        try {
            File fileDir = new File(this.dataDir);
            if (!fileDir.exists()) {
                boolean isDirCreated = fileDir.mkdir();
            }
            File file = new File(this.dataDir + this.filePath);
            if (!file.exists()) {
                boolean isFileCreated = file.createNewFile();
            }
            BufferedReader reader = new BufferedReader(new FileReader(this.dataDir + this.filePath));
            String line;
            while ((line = reader.readLine()) != null) {
                taskList.load(parser.convertStringToTask(line));
            }
            reader.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Appends a new task to the file.
     *
     * @param task the Task to write to the file
     */
    public void writeToFile(Task task) {
        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(this.dataDir + this.filePath, true))) {
            String taskString = convertTaskToString(task);
            writer.write(taskString);
            writer.newLine();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private String convertTaskToString(Task task) {
        if (task instanceof Todo) {
            return "T | " + (task.isDone() ? "1" : "0") + " | " + task.getDescription();
        } else if (task instanceof Deadline) {
            return "D | " + (task.isDone() ? "1" : "0") + " | " + task.getDescription() +
                    " | " + ((Deadline) task).getBy().toString();
        } else if (task instanceof Event) {
            return "E | " + (task.isDone() ? "1" : "0") + " | " + task.getDescription() +
                    " | " + ((Event) task).getStart().toString() +
                    " | " + ((Event) task).getEnd().toString();
        }
        return "";
    }

    /**
     * Replaces a specific line in the file with the updated task string.
     *
     * @param taskList the TaskList containing tasks
     * @param index    the index of the task to replace in the file
     */
    public void replaceLineInFile(TaskList taskList, int index) {
        File inputFile = new File(this.dataDir + this.filePath);
        File tempFile = new File(this.dataDir + "temp.txt");

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String currentLine;
            int currentIdx = 0;

            while ((currentLine = reader.readLine()) != null) {
                if (currentIdx != index) {
                    writer.write(currentLine);
                    writer.newLine();
                } else {
                    String replacedLine = convertTaskToString(taskList.getTask(index));
                    writer.write(replacedLine);
                    writer.newLine();
                }
                currentIdx++;
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        boolean isDeleted = inputFile.delete();
        boolean isRenamed = tempFile.renameTo(inputFile);
    }

    /**
     * Deletes a specific line from the file.
     *
     * @param index the index of the line to delete from the file
     */
    public void deleteLineFromFile(int index) {
        File inputFile = new File(this.dataDir + this.filePath);
        File tempFile = new File(this.dataDir + "temp.txt");

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String currentLine;
            int currentIdx = 0;

            while ((currentLine = reader.readLine()) != null) {
                if (currentIdx != index) {
                    writer.write(currentLine);
                    writer.newLine();
                }
                currentIdx++;
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        boolean isDeleted = inputFile.delete();
        boolean isRenamed = tempFile.renameTo(inputFile);
    }


}

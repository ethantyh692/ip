package duke;

import java.util.ArrayList;

/**
 * The TaskList class represents a list of tasks and provides methods
 * to manipulate the list, such as adding, deleting, marking, and unmarking tasks.
 * It interacts with the Storage class to perform file operations for persisting
 * changes to tasks.
 */
public class TaskList {

    private ArrayList<Task> toDoList;
    private int counter;

    /**
     * Constructs an empty TaskList.
     */
    public TaskList() {
        this.toDoList = new ArrayList<Task>();
        this.counter = 1;
    }

    /**
     * Marks a task as done at the specified index and updates the storage file.
     *
     * @param index   the 1-based index of the task to mark as done
     * @param storage the Storage object to handle file operations
     */
    public void mark(int index, Storage storage) {
        Task task = this.toDoList.get(index - 1);
        task.markAsDone();
        storage.replaceLineInFile(this,index - 1);
        System.out.println("Nice! I've marked this task as done:");
        System.out.println(task.toString());
    }

    /**
     * Unmarks a task as not done yet at the specified index and updates the storage file.
     *
     * @param index   the 1-based index of the task to unmark
     * @param storage the Storage object to handle file operations
     */
    public void unmark(int index, Storage storage) {
        Task task = toDoList.get(index - 1);
        task.unmarkAsUndone();
        storage.replaceLineInFile(this,index - 1);
        System.out.println("Ok! I've marked this task as not done yet:");
        System.out.println(task.toString());
    }

    /**
     * Deletes a task at the specified index from the task list and updates the storage file.
     *
     * @param index   the 1-based index of the task to delete
     * @param storage the Storage object to handle file operations
     */
    public void delete(int index, Storage storage) {
        Task task = toDoList.get(index - 1);
        toDoList.remove(index - 1);
        storage.deleteLineFromFile(index - 1);
        System.out.println("Noted. I've removed this task:");
        System.out.println(task.toString());
        this.counter -= 1;
        System.out.println("Now you have " + toDoList.size() + " tasks in the list.");
    }

    /**
     * Loads a task into the task list. Used when loading tasks from a storage file.
     *
     * @param task the Task to load into the list
     */
    public void load(Task task) {
        this.toDoList.add(task);
        this.counter += 1;
    }

    /**
     * Adds a new task to the task list and writes it to the storage file.
     *
     * @param task    the Task to add
     * @param storage the Storage object to handle file operations
     */
    public void add(Task task, Storage storage) {
        this.toDoList.add(task);
        storage.writeToFile(task);
        System.out.println("Got it. I've added this task: ");
        System.out.println(task.toString());
        System.out.println("Now you have " + counter + " tasks in the list.");
        this.counter += 1;
    }

    /**
     * Returns the size of the task list.
     *
     * @return the number of tasks in the list
     */
    public int getSize() {
        return toDoList.size();
    }

    /**
     * Returns the task at the specified index.
     *
     * @param index the index of the task to retrieve
     * @return the Task at the specified index
     */
    public Task getTask(int index) {
        return toDoList.get(index);
    }

    /**
     * Returns the entire task list.
     *
     * @return the ArrayList of tasks
     */
    public ArrayList<Task> getTaskList() {
        return this.toDoList;
    }

}

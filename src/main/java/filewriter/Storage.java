package filewriter;

import datetime.DateTime;
import exception.DukeException;
import task.*;

import java.io.*;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Class used to store and read from text file.
 */
public class Storage {
    String filePath;
    FileWriter fileWriter;
    ArrayList<Task> taskList;
    boolean willAppend = true;

    /**
     * Constructor of Storage object.
     * @param filePath filepath to text file to be read from and edited.
     * @throws DukeException When CreateWriter throws DukeException.
     */
    public Storage(String filePath) throws DukeException {
        this.filePath = filePath;
    }

    public void write(TaskList schedule) throws DukeException{
        try {
            File text = new File(filePath);
            text.getParentFile().mkdirs();
            fileWriter = new FileWriter(text, false);
            fileWriter.write(schedule.toText());
            fileWriter.close();
        } catch (IOException e) {
            throw new DukeException("Error creating fileWriter");
        }
    }


    /**
     * Creates FileWriter member fw.
     * @throws DukeException when invalid filepath.
     */
    /*private void createWriter() throws DukeException {
        try {
            File file = new File(filePath);
            file.createNewFile();
            fw = new FileWriter(file, willAppend);
        } catch (IOException e) {
            throw new DukeException("invalid filepath");
        }
    }*/

    /**
     * Wrapper to close FileWriter fw.
     * Used in Execute method of ExitCommand object.
     * @throws DukeException when invalid filepath.
     */
    /*public void closeWriter() throws DukeException {
        try {
            fw.close();
        } catch (IOException e) {
            throw new DukeException("invalid filepath");
        }
    }*/

    /**
     * Adds Task to TaskList data stored in txt file.
     * Called during execute method of addCommand object.
     * @param textToAdd (Command Object).to String();
     * @throws DukeException thrown when IOException is caught.
     */
    /*public void writeToFile(String textToAdd) throws DukeException {
        try {
            checkAppend(true);
            fw.write(textToAdd + "\n");
        } catch (IOException e) {
            throw new DukeException("failed to write to file");
        }
    }*/

    /**
     * Rewrites data in txt file with editions made.
     * Called when Execute command of DeleteCommand or EditCommand object modifies TaskList.
     * @param schedule Modified contents of TaskList.
     * @throws DukeException thrown when IOException is caught.
     */
    /*public void editFile(TaskList schedule) throws DukeException {
        try {
            checkAppend(false);
            for (Task task: schedule.getList()) {
                fw.write(task.toString() + "\n");
            }
        } catch (IOException e) {
            throw new DukeException("failed to write to file");
        }
    }*/

    /**
     * toggles willAppend boolean, which determines if FileWriter fw will append to txt file or overwrite it.
     * @param toAppend new boolean value of toAppend.
     * @throws DukeException thrown when createWriter throws DukeException.
     */
    /*private void checkAppend(boolean toAppend) throws DukeException {
        closeWriter();
        willAppend = toAppend;
        createWriter();
    }*/

    /**
     * Method to interpret each line in txt file and add corresponding Task to TaskList.
     * @param line every line in the txt file represents a task.
     * @return Task object with type, task name, date and timing as described in String.
     * @throws DukeException thrown when Task is not stored in the correct format in txt file.
     */
    private Task read(String line) throws DukeException {
        Task output;
        switch (line.charAt(line.indexOf("[") + 1)) {
        case 'T':
            output = new Todo(line.substring(7, line.length()));
            updateStatus(output, line);
            return output;
        case 'D':
            try {
                int timeDivider = line.indexOf("(by:");
                int recDivider = line.indexOf(" every: ");
                String input = line.substring(7, timeDivider);
                boolean isRecurring = (recDivider != -1);
                if (isRecurring){
                    input += "/by " + DateTime.readDeadLine(line.substring(timeDivider + 5, recDivider - 1)).toString();

                } else {
                    input += "/by "
                            + DateTime.readDeadLine(line.substring(timeDivider + 5, line.length() - 1)).toString();
                }
                output = new Deadline(input);
                updateStatus(output, line);
                getRecurrance(recDivider, line, output);
                return output;
            } catch (Exception e) {
                throw new DukeException("Deadline task not stored properly.");
            }
        case 'E':
            try {
                int timeDivider = line.indexOf("(at:");
                int recDivider = line.indexOf(" every: ");
                String input = line.substring(7, timeDivider);
                boolean isRecurring = (recDivider != -1);
                if (isRecurring){
                    input += "/at "
                            + DateTime.readEventTime(line.substring(timeDivider + 5, recDivider - 1)).toString();
                } else {
                    input += "/at "
                            + DateTime.readEventTime(line.substring(timeDivider + 5, line.length() - 1)).toString();
                }
                output = new Event(input);
                updateStatus(output, line);
                getRecurrance(recDivider, line, output);
                return output;
            } catch (Exception e) {
                throw new DukeException("Event task not stored properly.");
            }
        default:
            throw new DukeException("Invalid entry in txt file: "+ line);
        }
    }

    private void updateStatus(Task task, String line){
        if (line.substring(4,5).equals("+")){
            task.markAsDone();
        }
    }


    private void getRecurrance(int recDivider, String line, Task task) throws DukeException{
        if (recDivider != -1){
            if (task instanceof Todo){
                throw new DukeException("Txt error: Can only set Event or Deadline as a recurrence.");
            }
            String[] details = line.substring(recDivider + 8, line.length() - 4).split(" ");
            ((Recurrence) task).setAsRecurring(details[1], Integer.parseInt(details[0]));
        }
    }

    /**
     * Reads txt file and updates TaskList accordingly.
     * @return object with updated ArrayList taskList used in construction of TaskList object.
     * @throws DukeException when read throws DukeException.
     */
    public Storage load() throws DukeException {
        try {
            File text = new File(filePath);
            text.createNewFile();
            text.getParentFile().mkdirs();
            BufferedReader reader = new BufferedReader(new FileReader(text));
            taskList = new ArrayList<>();
            Stream<String> stream
                    = reader.lines();
            taskList = new ArrayList(stream.filter(line -> !line.equals(""))
                    .map(line -> {
                        try {
                            return read(line);
                        } catch (DukeException e) {
                            throw new RuntimeException();
                        }
                    }).collect(Collectors.toList()));
            return this;
        } catch (FileNotFoundException e) {
            throw new DukeException("File not found!");
        } catch (IOException e) {
            return null;
        } catch (Exception e) {
            throw new DukeException("Unforeseen load errors: " + e.getMessage());
        }
    }

    /**
     * Called during construction method of TaskList.
     * @return ArrayList of Task
     */
    public ArrayList<Task> getSchedule() {
        return taskList;
    }
}

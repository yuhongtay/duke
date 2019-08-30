import java.util.ArrayList;


public class TaskList {
    String filePath = "data/duke.txt";
    ArrayList <Task> schedule = new ArrayList<> ();
    Storage storage;
    public int task_Num;
    boolean isFirst ;

    public TaskList(Storage storage){
        try {
            this.storage = storage;
            this.schedule = storage.getSchedule();
            task_Num = schedule.size();
            if (task_Num == 0){
                isFirst = true;
            } else {
                isFirst = false;
            }
            System.out.println(task_Num);
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public TaskList(){
        try {
            storage = new Storage(filePath);
            this.schedule = schedule;
            task_Num = schedule.size();
            if (task_Num == 0){
                isFirst = true;
            } else {
                isFirst = false;
            }
            System.out.println(task_Num);
        } catch (DukeException e){
            System.out.println(e.getMessage());
        }
    }

    public Task getTask(int index){

        return schedule.get(index);
    }

    public void complete(int index)
            throws NullPointerException, IndexOutOfBoundsException, NumberFormatException, DukeException{
        schedule.get(index).markAsDone();
        storage.editFile(schedule.toString());
    }

    public void addTask(Task task) throws DukeException{
        schedule.add(task);
        String input = "";
        if (isFirst){
            isFirst = !isFirst;
        } else {
            storage.writeToFile(System.lineSeparator());
        }
        storage.writeToFile(task.toString());
        task_Num++;
    }

    public Task remove(int index)
            throws NullPointerException, IndexOutOfBoundsException, NumberFormatException, DukeException{
        Task removed_Task = schedule.get(index);
        schedule.remove(index);
        storage.editFile(schedule.toString());
        task_Num --;
        return removed_Task;
    }

    public void stop() throws DukeException{
        storage.closeWriter();
    }

    /*public void add(String task){
        String[] word_Arr = task.split(" ", 2);
        try {
            Commands command = Commands.getByName(word_Arr[0]);
            switch (command) {
                case LIST:
                    System.out.println(this);
                    break;
                case DONE:
                    try {
                        try {
                            if (word_Arr.length < 2)
                                throw new DukeException((new Border()) + "\n     ☹ OOPS!!! Which task did you complete again?\n" + (new Border()));
                            schedule.get(Integer.parseInt(word_Arr[1]) - 1).markAsDone();
                            FileWriterClass.writeToFile(filePath, schedule.toString());
                            System.out.println(new Border());
                            System.out.println("     Nice! I've marked this task as done:");
                            System.out.println("       " + schedule.get(Integer.parseInt(word_Arr[1]) - 1).toString());
                            System.out.println(new Border());
                        } catch (NullPointerException | IndexOutOfBoundsException e) {
                            throw new DukeException((new Border()) + "\n     ☹ OOPS!!! Index out of bounds.\n" + (new Border()));
                        } catch (NumberFormatException e) {
                            throw new DukeException((new Border()) + "\n     ☹ OOPS!!! Please enter a single integer index of task to delete.\n" + (new Border()));
                        }
                    } catch (DukeException e) {
                        System.out.println(e.getMessage());
                    } finally {
                        break;
                    }
                case DELETE:
                    try {
                        try {
                            if (word_Arr.length < 2) {
                                throw new DukeException((new Border()) + "\n     ☹ OOPS!!! What do you want to delete again?\n" + (new Border()));
                            }
                            Task removed_Task = schedule.get(Integer.parseInt(word_Arr[1]) - 1);
                            schedule.remove(removed_Task);
                            FileWriterClass.writeToFile(filePath, schedule.toString());
                            task_Num --;
                            System.out.println(new Border());
                            System.out.println("     Noted. I've removed this task:");
                            System.out.println("       " + removed_Task.toString());
                            System.out.println("      Now you have " + task_Num + " tasks in the list.");
                            System.out.println(new Border());
                        } catch (NullPointerException | IndexOutOfBoundsException e) {
                            throw new DukeException((new Border()) + "\n     ☹ OOPS!!! Index out of bounds.\n" + (new Border()));
                        } catch (NumberFormatException e) {
                            throw new DukeException((new Border()) +
                                    "\n     ☹ OOPS!!! Please enter a single integer for index of task to delete.\n"
                                    + (new Border()));
                        }
                    } catch (DukeException e) {
                        System.out.println(e.getMessage());
                    } finally {
                        break;
                    }
                default:
                    try {
                        Task new_task = track(word_Arr);
                        schedule.add(new_task);
                        task_Num++;
                        System.out.println(new Border());
                        System.out.println("     Got it. I've added this task:");
                        System.out.println("       " + new_task.toString());
                        System.out.println("     Now you have " + task_Num + " tasks in the list.");
                        System.out.println(new Border() + "\n");
                        break;
                    } catch (DukeException e){
                        System.out.println(e.getMessage());
                    }
                }
            } catch (DukeException e) {
            System.out.println(e.getMessage());
        }
    }

    private Task track(String[] word_Arr) throws DukeException{
        try {
            if (word_Arr[0].equals("todo")) {
                check_Decription(word_Arr, "todo");
                return new Todo(word_Arr[1]);
            } else if (word_Arr[0].equals("deadline")) {
                check_Decription(word_Arr, "deadline");
                return new Deadline(word_Arr[1]);
            } else {
                check_Decription(word_Arr, "event");
                return new Event(word_Arr[1]);
            }
        } catch(DukeException e){
            throw e;
        }
    }

    private void check_Decription(String[] word_Arr, String task_Type) throws DukeException{
        if (word_Arr.length < 2) {
            throw new DukeException((new Border()) + "\n     ☹ OOPS!!! The description of a " + task_Type + " cannot be empty.\n" + (new Border()));
        }
    }*/


    public String toString(){
        String output = (new Border()) + "\n" + "     Here are the tasks in your list: \n";
        for (int index = 0; index < task_Num; index ++){
            Task task = schedule.get(index);
            output += ("     " + (index + 1) + "." + task.toString() + "\n");
        }
        return output + (new Border()) + "\n";
    }
}
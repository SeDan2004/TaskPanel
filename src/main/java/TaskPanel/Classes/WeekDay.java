package TaskPanel.Classes;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

public class WeekDay {
	private int weekDayInd;
	private String name;
	
	public List<Task> taskList;
	
	public WeekDay() {}
	
	public WeekDay(int weekDayInd, String name, List<Task> weekDayTasks) {
		this.weekDayInd = weekDayInd;
		this.name = name;
		this.taskList = weekDayTasks;
	}
	
	public int getWeekDayInd() {
		return weekDayInd;
	}
	
	public String getName() {
		return name;
	}
	
	public List<Task> getTaskPair(int pageNum, int contentCount) {
		List<Task> taskPair = new ArrayList<>();
		
		int lastIndex = pageNum * contentCount;
		int firstIndex = lastIndex - contentCount;
		
		if (lastIndex > taskList.size()) {
			taskPair = taskList.subList(firstIndex, taskList.size());
		} else {
			taskPair = taskList.subList(firstIndex, lastIndex);
		}
		
		return taskPair;
	}
	
	public String getNextPageFirstTask(int nextPageNum, int taskCount) throws Exception {
		if (nextPageNum != -1) {
		    int lastIndex = nextPageNum * taskCount;
		    int firstIndex = lastIndex - taskCount;
		
		    Task task = taskList.get(firstIndex);
		    String[] taskInf = {task.getId() + "", task.getDescription()};
		    
		    JSONArray json = new JSONArray(taskInf);
		    
		    return json.toString();
		} else {
			return "";
		}
	}
}
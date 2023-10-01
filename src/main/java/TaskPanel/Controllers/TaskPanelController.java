package TaskPanel.Controllers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import TaskPanel.Classes.Task;
import TaskPanel.Classes.WeekDay;
import TaskPanel.DAO.TaskDAO;

@Controller
@RequestMapping(value = "/", produces = "text/plain;charset=UTF-8")
public class TaskPanelController {
	
	@Autowired
	private TaskDAO taskDao;
	
	private List<WeekDay> weekDays;
	
	@GetMapping()
	public String index(Model model) {
		try {
			weekDays = taskDao.weekDaysInit();
			
			model.addAttribute("WeekDays", weekDays);
			model.addAttribute("CurrentDay", new Date().getDay());
			
			return "index";
		} catch (Exception ex) {
		    model.addAttribute("error", ex.getMessage());
		    return "error";
		}
	}
	
	@GetMapping("/showTasks{id}/{page}/{size}")
	public String show(@PathVariable("id") int id,
			           @PathVariable("page") int page,
			           @PathVariable("size") int size,
			           Model model) {
		WeekDay currentWeekDay = getCurrentWeekDay(id);
		
		model.addAttribute("WeekDay", currentWeekDay);
		return "show";
	}
	
	@PostMapping("add")
	public @ResponseBody String addTask(@RequestParam("description") String description,
			                            @RequestParam("weekDayInd") int weekDayInd) {
		
		try {
			WeekDay currentWeekDay = getCurrentWeekDay(weekDayInd);
			Date currentDate = new Date();
			String formatDateTxt = new SimpleDateFormat("yyyy-MM-dd").format(currentDate);
			
			currentDate.setHours(0);
			currentDate.setMinutes(0);
			currentDate.setSeconds(0);
			
			int id = taskDao.addTask(description, weekDayInd, formatDateTxt);
			
			currentWeekDay.taskList.add(0, new Task(id, description, weekDayInd, currentDate));
			
			return id + "";
		} catch (Exception ex) {
			return ex.getMessage();
		}
	}
	
	@PostMapping("delete")
	public @ResponseBody String deleteTask(@RequestParam("idTask") int idTask,
			                               @RequestParam("weekDayInd") int weekDayInd,
			                               @RequestParam("nextPageNum") int nextPageNum,
			                               @RequestParam("contentCount") int contentCount) {
		try {
		    WeekDay currentWeekDay = getCurrentWeekDay(weekDayInd);
		    String nextPageFirstTask = currentWeekDay.getNextPageFirstTask(nextPageNum, contentCount);
		    
		    taskDao.deleteTask(idTask);
		    Task currentTask = currentWeekDay.taskList.stream()
		    		                                  .filter(task -> task.getId() == idTask)
		    		                                  .findFirst()
		    		                                  .get();
		    
		    int currentTaskIndex = currentWeekDay.taskList.indexOf(currentTask);
		    currentWeekDay.taskList.remove(currentTaskIndex);
		    
		    return nextPageFirstTask;
		} catch(Exception ex) {
			return ex.getMessage();
		}
	}
	
	@PostMapping("update")
	public @ResponseBody void updateTask(@RequestParam("idTask") int idTask,
			               @RequestParam("description") String description) {
		try {
			taskDao.updateTask(idTask, description);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	@PostMapping("/get_count_tasks")
	public @ResponseBody String getCountTasks(@RequestParam("weekDayInd") int weekDayInd) {
		WeekDay currentWeekDay = getCurrentWeekDay(weekDayInd);
		return currentWeekDay.taskList.size() + "";
	}
	
	public WeekDay getCurrentWeekDay(int index) {
	    return weekDays.stream()
                       .filter(weekDay -> weekDay.getWeekDayInd() == index)
                       .findFirst()
                       .get();
	}
}
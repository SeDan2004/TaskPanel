package TaskPanel.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import TaskPanel.Classes.Database;
import TaskPanel.Classes.Task;
import TaskPanel.Classes.WeekDay;

@Component
public class TaskDAO {
	private Database db;
	
	@Autowired
	TaskDAO(Database db) {
		this.db = db;
	}
	
	public List<WeekDay> weekDaysInit() throws Exception {
		String[] weekDayNames = {
	        "Понедельник", "Вторник", "Среда",
			"Четверг", "Пятница", "Суббота",
			"Воскресенье"
		};
		
		List<WeekDay> weekDays = new ArrayList<>();
		
		for (int i = 1; i <= weekDayNames.length; i++) {
			int weekDayInd;
			
	        WeekDay weekDay;
			String currentWeekDayName;
			List<Task> weekDayTasks;
						
			if (i == weekDayNames.length) {
				currentWeekDayName = weekDayNames[6];
				weekDayInd = 0;
			} else {
				currentWeekDayName = weekDayNames[i - 1];
				weekDayInd = i;
			}
			
			weekDayTasks = getCurrentWeekDayTasks(weekDayInd);			
			weekDay = new WeekDay(weekDayInd, currentWeekDayName, weekDayTasks);
			    
			weekDays.add(weekDay);
		}
		    
        return weekDays;
	}
	
	private List<Task> getCurrentWeekDayTasks(int weekDayInd) throws Exception {
	    List<Task> tasks = new ArrayList<>();
			
		String sql = "SELECT id, description, howAddedTask FROM tasks WHERE weekDayInd = ? " +
		             "ORDER BY id DESC";
		
		Connection conn = Database.getConnection();
		PreparedStatement statement;
		ResultSet resSet;
			
		statement = conn.prepareStatement(sql);
		statement.setInt(1, weekDayInd);
			
		resSet = statement.executeQuery();
			
		while (resSet.next()) {
			Task task;
				
			int id = resSet.getInt("id");
			String description = resSet.getString("description");
			Date howAddedTask = resSet.getDate("howAddedTask");
				
			task = new Task(id, description, weekDayInd, howAddedTask);
			tasks.add(task);
		}
			
		return tasks;
	}
	
	public int addTask(String description, int weekDayInd, String formatDateTxt) throws Exception {
		String sql = "INSERT INTO tasks (description, weekDayInd, howAddedTask) " +
		             "VALUES (?, ?, ?)";
		
		Connection conn = Database.getConnection();
		PreparedStatement statement;
		ResultSet resSet;
		
		statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		
		statement.setString(1, description);
		statement.setInt(2, weekDayInd);
		statement.setString(3, formatDateTxt);
		
		statement.executeUpdate();
		
	    resSet = statement.getGeneratedKeys();
	    resSet.next();
		
	    return (int)resSet.getLong(1);
	}
	
	public void deleteTask(int idTask) throws Exception {
		String sql = "DELETE FROM tasks WHERE id = ?";
		
		Connection conn = Database.getConnection();
		PreparedStatement statement = conn.prepareStatement(sql);
		
		statement.setInt(1, idTask);
		statement.execute();
	}
	
	public void updateTask(int idTask, String description) throws Exception {
		String sql = "UPDATE tasks SET description = ? WHERE id = ?";
		
		Connection conn = Database.getConnection();
		PreparedStatement statement = conn.prepareStatement(sql);
		ResultSet resSet;
		
		statement.setString(1, description);
		statement.setInt(2, idTask);
		
		statement.execute();
	}
}
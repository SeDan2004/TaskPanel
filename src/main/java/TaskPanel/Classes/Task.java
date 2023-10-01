package TaskPanel.Classes;

import java.util.Date;

public class Task {
	private int id;
	private String description;
	private int weekDayInd;
	private Date howAddedTask;
	
	public Task(int id, String description, int weekDayInd, Date howAddedTask) {
		this.id = id;
		this.description = description;
		this.weekDayInd = weekDayInd;
		this.howAddedTask = howAddedTask;
	}
	
	public int getId() {
		return id;
	}
	
	public String getDescription() {
		return description;
	}
	
	public int getHowAddedTask() {
		return weekDayInd;
	}
	
	public Date getDate() {
		return howAddedTask;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
}
package TaskPanel.Classes;
import java.sql.Connection;
import java.sql.DriverManager;

import org.springframework.stereotype.Component;

@Component
public class Database {
	private static final String URL;
	private static final String LOGIN;
	private static final String PASS;
	
	static {
		URL = "jdbc:mysql://localhost:3306/task_panel";
		LOGIN = "root";
		PASS = "root";
	}
	
	public static Connection getConnection() throws Exception {
		Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
	    return DriverManager.getConnection(URL, LOGIN, PASS);
	}
}
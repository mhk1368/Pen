package net.kabulsoft.pen.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JOptionPane;

import net.kabulsoft.pen.util.PenDiags;

public class DataBase {
	static final String url = "jdbc:mysql://127.0.0.1/pen?useUnicode=true&characterEncoding=UTF-8&characterSetResults=UTF-8";
	protected static Connection connection = null;
	protected Statement statement = null;
	protected ResultSet results = null;
	public static final int LIMIT = 10;
	protected static int PERIOD = 1;
	public static Date PSDATE;

	public DataBase() 
	{
		try {
			if(connection == null){
				Class.forName("com.mysql.jdbc.Driver");
				connection = DriverManager.getConnection(url, "root", "");
			}
			statement = connection.createStatement();
		} catch (SQLException s) {
			PenDiags.showWarn("قادر به اتصال پایگاه داده نیست!");
		} catch (ClassNotFoundException e) {
		}
	}
	
	public static boolean setupLink()
	{
		if(connection == null){
			try {
				Class.forName("com.mysql.jdbc.Driver");
				connection = DriverManager.getConnection(url, "root", "");
				Statement query = connection.createStatement();
				String q = "SELECT period_id, period_name, period_start_date FROM financial_period WHERE period_active = 1 LIMIT 1";
				ResultSet res = query.executeQuery(q);
				if(res.next()){
					PERIOD = res.getInt("period_id");
					String dateStr = res.getString("period_start_date");
					DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
					PSDATE = df.parse(dateStr);
				}
			} 
			catch (SQLException e) {
				JOptionPane.showMessageDialog(null, e.getMessage());
				return false;
			} 
			catch (ParseException e) {} 
			catch (ClassNotFoundException e) {
			}
		}
		return true;
	}
	
	public boolean findPerson(String table, String col, String id)
	{
		String q = "SELECT %s FROM %s WHERE %s = %s";
		try{
			results = statement.executeQuery(String.format(q, col, table, col, id));
			if(results.next()){
				return true;
			}
		}
		catch(SQLException e){}
		return false;
	}

	public void closeLink() {
		try {
			if (results != null){
				results.close();
			}
			connection.close();
			statement.close();
		} catch (Exception e) {
		}
	}
	
	public Connection getConnection(){
		return connection;
	}
}
